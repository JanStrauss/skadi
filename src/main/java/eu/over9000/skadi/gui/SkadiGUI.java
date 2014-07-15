package eu.over9000.skadi.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import eu.over9000.skadi.channel.Channel;
import eu.over9000.skadi.channel.ChannelEventListener;
import eu.over9000.skadi.channel.ChannelManager;
import eu.over9000.skadi.logging.SkadiLogging;
import eu.over9000.skadi.stream.StreamRetriever;
import eu.over9000.skadi.util.comperator.BooleanComperator;
import eu.over9000.skadi.util.comperator.LongComperator;

public class SkadiGUI extends JFrame implements ChannelEventListener {

	private static final long serialVersionUID = 2045150091920320920L;

	private static SkadiGUI instance;

	private JPanel pnTop;
	private JTextField textNewChannel;
	private JButton btnAddChannel;
	private JScrollPane spChannels;
	private JLabel labelAddChannel;

	private final ChannelDataTableModel tableModel;
	private JPanel pnButtons;
	private JButton btnOpenBoth;
	private JButton btnStream;
	private JButton btnChat;
	private JButton btnDelete;
	private JTable tableChannels;
	private JButton btnImportFollowing;
	private JPanel pnBottom;
	private JPanel pnLog;
	private JTextArea taLog;
	private JScrollPane spLog;
	private JSplitPane splitPane;
	private JComboBox<String> cbQuality;
	private JLabel lbUpdateIndicator;

	private ImageIcon updateIcon;
	private JPanel pnTopChannel;
	private JPanel pnSettingsBtn;
	private JButton btnSettings;

	private SkadiGUI() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		tableModel = new ChannelDataTableModel();
		setMinimumSize(new Dimension(640, 480));
		initialize();

		ChannelManager.getInstance().addListener(this);

		pack();
		setVisible(true);

	}

	private void initialize() {
		setTitle("Skadi");
		setIconImage(new ImageIcon(this.getClass().getResource("/icon.png")).getImage());
		updateIcon = new ImageIcon(this.getClass().getResource("/update_icon.gif"));
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout(0, 0));
		getContentPane().add(getPnTop(), BorderLayout.NORTH);
		getContentPane().add(getSplitPane(), BorderLayout.CENTER);
		getRootPane().setDefaultButton(getBtnAddChannel());
	}

	private JPanel getPnTop() {
		if (pnTop == null) {
			pnTop = new JPanel();
			pnTop.setLayout(new BorderLayout(0, 0));
			pnTop.add(getPnTopChannel(), BorderLayout.WEST);
			pnTop.add(getPnSettingsBtn(), BorderLayout.EAST);
		}
		return pnTop;
	}

	private JTextField getTextNewChannel() {
		if (textNewChannel == null) {
			textNewChannel = new JTextField();
			textNewChannel.setColumns(20);
		}
		return textNewChannel;
	}

	private JButton getBtnAddChannel() {
		if (btnAddChannel == null) {
			btnAddChannel = new JButton("Add channel to list");
			btnAddChannel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent arg0) {
					final boolean result = ChannelManager.getInstance().addChannel(SkadiGUI.this.getTextNewChannel().getText(), true);
					if (result) {
						SkadiGUI.this.getTextNewChannel().setText("");
					}
				}
			});
		}
		return btnAddChannel;
	}

	private JScrollPane getSpChannels() {
		if (spChannels == null) {
			spChannels = new JScrollPane();
			spChannels.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			spChannels.setViewportView(getTableChannels());
		}
		return spChannels;
	}

	private JLabel getLabelAddChannel() {
		if (labelAddChannel == null) {
			labelAddChannel = new JLabel("Add channel:");
		}
		return labelAddChannel;
	}

	public static void createInstance() {
		if (SkadiGUI.instance == null) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					try {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
						SkadiLogging.log(e);
					}
					SkadiGUI.instance = new SkadiGUI();
					SkadiGUI.instance.applyPrefWidth();
				}
			});
		}
	}

	public static void appendLog(final String logEntry) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				if (SkadiGUI.instance != null) {
					SkadiGUI.instance.taLog.append(logEntry + System.lineSeparator());
					SkadiGUI.instance.taLog.setCaretPosition(SkadiGUI.instance.taLog.getDocument().getLength());
				}
			}
		});

	}

	public void applyPrefWidth() {
		if ((SkadiGUI.instance == null) || (SkadiGUI.instance.tableChannels == null)) {
			System.out.println("ret");
			System.out.println(SkadiGUI.instance);
			return;
		}

		SkadiGUI.instance.tableChannels.getColumnModel().getColumn(0).setPreferredWidth(40);
		SkadiGUI.instance.tableChannels.getColumnModel().getColumn(0).setMaxWidth(40);
		SkadiGUI.instance.tableChannels.getColumnModel().getColumn(0).setMaxWidth(40);
		SkadiGUI.instance.tableChannels.getColumnModel().getColumn(0).setWidth(40);

		SkadiGUI.instance.tableChannels.getColumnModel().getColumn(1).setPreferredWidth(150);
		SkadiGUI.instance.tableChannels.getColumnModel().getColumn(2).setPreferredWidth(200);

		SkadiGUI.instance.tableChannels.getColumnModel().getColumn(4).setPreferredWidth(80);
		SkadiGUI.instance.tableChannels.getColumnModel().getColumn(5).setPreferredWidth(90);
	}

	private JPanel getPnButtons() {
		if (pnButtons == null) {
			pnButtons = new JPanel();
			pnButtons.add(getLbUpdateIndicator());
			pnButtons.add(getCbQuality());
			pnButtons.add(getBtnOpenBoth());
			pnButtons.add(getBtnStream());
			pnButtons.add(getBtnChat());
			pnButtons.add(getBtnDelete());
		}
		return pnButtons;
	}

	private JButton getBtnOpenBoth() {
		if (btnOpenBoth == null) {
			btnOpenBoth = new JButton("Open stream & chat");
			btnOpenBoth.setEnabled(false);
			btnOpenBoth.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {

					final Channel channel = SkadiGUI.this.getSelectedChannel();
					if (channel != null) {
						channel.openStreamAndChat((String) SkadiGUI.this.getCbQuality().getSelectedItem());
					}
				}
			});
		}
		return btnOpenBoth;
	}

	private JButton getBtnStream() {
		if (btnStream == null) {
			btnStream = new JButton("Open stream");
			btnStream.setEnabled(false);
			btnStream.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final Channel channel = SkadiGUI.this.getSelectedChannel();
					if (channel != null) {
						channel.openStream((String) SkadiGUI.this.getCbQuality().getSelectedItem());
					}
				}
			});
		}
		return btnStream;
	}

	private JButton getBtnChat() {
		if (btnChat == null) {
			btnChat = new JButton("Open chat");
			btnChat.setEnabled(false);
			btnChat.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final Channel channel = SkadiGUI.this.getSelectedChannel();
					if (channel != null) {
						channel.openChat();
					}
				}
			});
		}
		return btnChat;
	}

	private JButton getBtnDelete() {
		if (btnDelete == null) {
			btnDelete = new JButton("Delete");
			btnDelete.setEnabled(false);
			btnDelete.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {

					final Channel channel = SkadiGUI.this.getSelectedChannel();
					ChannelManager.getInstance().removeChannel(channel);
					SkadiGUI.this.getTableChannels().clearSelection();
				}
			});
		}
		return btnDelete;
	}

	private JTable getTableChannels() {
		if (tableChannels == null) {
			tableChannels = new JTable();
			tableChannels.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tableChannels.setModel(tableModel);
			tableChannels.setDefaultRenderer(Object.class, new ChannelDataCellRenderer());
			tableChannels.setRowHeight(30);
			tableChannels.getTableHeader().setReorderingAllowed(false);
			// this.tableChannels.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			tableChannels.setAutoCreateRowSorter(true);

			final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableChannels.getModel());
			sorter.setSortsOnUpdates(true);
			sorter.setComparator(0, new BooleanComperator());
			sorter.setComparator(4, new LongComperator());
			sorter.setComparator(5, new LongComperator());
			tableChannels.setRowSorter(sorter);

			tableChannels.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(final ListSelectionEvent e) {
					if (e.getValueIsAdjusting()) {
						return;
					}

					final ListSelectionModel lsm = (ListSelectionModel) e.getSource();

					final boolean enabled = !lsm.isSelectionEmpty();

					SkadiGUI.this.getBtnChat().setEnabled(enabled);
					SkadiGUI.this.getBtnDelete().setEnabled(enabled);
					SkadiGUI.this.getBtnOpenBoth().setEnabled(enabled);
					SkadiGUI.this.getBtnStream().setEnabled(enabled);
					SkadiGUI.this.getCbQuality().setEnabled(enabled);

					if (enabled) {
						final int row = tableChannels.convertRowIndexToModel(tableChannels.getSelectedRow());
						final Channel channel = ChannelManager.getInstance().getChannels().get(row);

						SkadiGUI.this.setQualities(channel.getQualityArray());

						if (channel.isLive() && !channel.isStreamdataRetrieved()) {
							StreamRetriever.updateStreamdataDelayed(channel);
							SkadiGUI.this.getLbUpdateIndicator().setIcon(updateIcon);
						} else {
							SkadiGUI.this.getLbUpdateIndicator().setIcon(null);
						}
					}

				}
			});

			tableChannels.getRowSorter().toggleSortOrder(0);
			tableChannels.getRowSorter().toggleSortOrder(0);
		}
		return tableChannels;
	}

	protected void setQualities(final String[] qualities) {
		getCbQuality().setModel(new DefaultComboBoxModel<String>(qualities));
	}

	private JButton getBtnImportFollowing() {
		if (btnImportFollowing == null) {
			btnImportFollowing = new JButton("Import followed channels");
			btnImportFollowing.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					new ImportDialog(SkadiGUI.this);
				}
			});
		}
		return btnImportFollowing;
	}

	private JPanel getPnBottom() {
		if (pnBottom == null) {
			pnBottom = new JPanel();
			pnBottom.setLayout(new BorderLayout(0, 0));
			pnBottom.add(getPnButtons(), BorderLayout.NORTH);
			pnBottom.add(getPnLog(), BorderLayout.CENTER);
		}
		return pnBottom;
	}

	private JPanel getPnLog() {
		if (pnLog == null) {
			pnLog = new JPanel();
			pnLog.setLayout(new BorderLayout(0, 0));
			pnLog.add(getSpLog(), BorderLayout.CENTER);
		}
		return pnLog;
	}

	private JTextArea getTaLog() {
		if (taLog == null) {
			taLog = new JTextArea();
			taLog.setFont(new Font("Arial", Font.PLAIN, 11));
			taLog.setRows(8);
			taLog.setEditable(false);
		}
		return taLog;
	}

	private JScrollPane getSpLog() {
		if (spLog == null) {
			spLog = new JScrollPane();
			spLog.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			spLog.setViewportView(getTaLog());
		}
		return spLog;
	}

	private JSplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new JSplitPane();
			splitPane.setContinuousLayout(true);
			splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane.setLeftComponent(getSpChannels());
			splitPane.setRightComponent(getPnBottom());
		}
		return splitPane;
	}

	public static Component getInstance() {
		return SkadiGUI.instance;
	}

	private JComboBox<String> getCbQuality() {
		if (cbQuality == null) {
			cbQuality = new JComboBox<String>();
			cbQuality.setEnabled(false);

			final Dimension dimension = new Dimension(80, cbQuality.getPreferredSize().height);

			cbQuality.setSize(dimension);
			cbQuality.setPreferredSize(dimension);
		}
		return cbQuality;
	}

	private JLabel getLbUpdateIndicator() {
		if (lbUpdateIndicator == null) {
			lbUpdateIndicator = new JLabel();

			final Dimension dimension = new Dimension(16, 16);

			lbUpdateIndicator.setSize(dimension);
			lbUpdateIndicator.setPreferredSize(dimension);
		}
		return lbUpdateIndicator;
	}

	@Override
	public void added(final Channel channel) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				tableModel.handleAdd(channel);

			}
		});

	}

	@Override
	public void removed(final Channel channel) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				tableModel.handleDelete(channel);

			}
		});
	}

	@Override
	public void updatedMetadata(final Channel channel) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				tableModel.handleUpdate(channel);

			}
		});
	}

	@Override
	public void updatedStreamdata(final Channel channel) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				if (SkadiGUI.this.getSelectedChannel().equals(channel)) {
					SkadiGUI.this.setQualities(channel.getQualityArray());
					SkadiGUI.this.getLbUpdateIndicator().setIcon(null);
				}
			}
		});
	}

	@Override
	public String getListenerName() {
		return this.getClass().getName();
	}

	private Channel getSelectedChannel() {
		final int row = SkadiGUI.this.getTableChannels().convertRowIndexToModel(SkadiGUI.this.tableChannels.getSelectedRow());
		return ChannelManager.getInstance().getChannels().get(row);
	}

	private JPanel getPnTopChannel() {
		if (pnTopChannel == null) {
			pnTopChannel = new JPanel();
			pnTopChannel.add(getLabelAddChannel());
			pnTopChannel.add(getTextNewChannel());
			pnTopChannel.add(getBtnAddChannel());
			pnTopChannel.add(getBtnImportFollowing());
		}
		return pnTopChannel;
	}

	private JPanel getPnSettingsBtn() {
		if (pnSettingsBtn == null) {
			pnSettingsBtn = new JPanel();
			pnSettingsBtn.add(getBtnSettings());
		}
		return pnSettingsBtn;
	}

	private JButton getBtnSettings() {
		if (btnSettings == null) {
			btnSettings = new JButton("Settings");
			btnSettings.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					new SettingsDialog(SkadiGUI.this);
				}
			});
		}
		return btnSettings;
	}
}