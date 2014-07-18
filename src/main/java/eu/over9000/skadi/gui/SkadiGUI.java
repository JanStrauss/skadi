/*******************************************************************************
 * Copyright (c) 2014 Jan Strauß
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
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

/**
 * The main window of Skadi.
 * 
 * @author Jan Strauß
 * 
 */
public final class SkadiGUI extends JFrame implements ChannelEventListener {
	
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
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.tableModel = new ChannelDataTableModel();
		this.setMinimumSize(new Dimension(640, 480));
		this.initialize();
		
		ChannelManager.getInstance().addListener(this);
		
		this.pack();
		this.setVisible(true);
		
	}
	
	private void initialize() {
		this.setTitle("Skadi");
		this.setIconImage(new ImageIcon(this.getClass().getResource("/icon.png")).getImage());
		this.updateIcon = new ImageIcon(this.getClass().getResource("/update_icon.gif"));
		this.setLocationRelativeTo(null);
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		this.getContentPane().add(this.getPnTop(), BorderLayout.NORTH);
		this.getContentPane().add(this.getSplitPane(), BorderLayout.CENTER);
		this.getRootPane().setDefaultButton(this.getBtnAddChannel());
	}
	
	private JPanel getPnTop() {
		if (this.pnTop == null) {
			this.pnTop = new JPanel();
			this.pnTop.setLayout(new BorderLayout(0, 0));
			this.pnTop.add(this.getPnTopChannel(), BorderLayout.WEST);
			this.pnTop.add(this.getPnSettingsBtn(), BorderLayout.EAST);
		}
		return this.pnTop;
	}
	
	private JTextField getTextNewChannel() {
		if (this.textNewChannel == null) {
			this.textNewChannel = new JTextField();
			this.textNewChannel.setColumns(20);
		}
		return this.textNewChannel;
	}
	
	private JButton getBtnAddChannel() {
		if (this.btnAddChannel == null) {
			this.btnAddChannel = new JButton("Add channel to list");
			this.btnAddChannel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent arg0) {
					final boolean result = ChannelManager.getInstance().addChannel(
					        SkadiGUI.this.getTextNewChannel().getText(), true);
					if (result) {
						SkadiGUI.this.getTextNewChannel().setText("");
					}
				}
			});
		}
		return this.btnAddChannel;
	}
	
	private JScrollPane getSpChannels() {
		if (this.spChannels == null) {
			this.spChannels = new JScrollPane();
			this.spChannels.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			this.spChannels.setViewportView(this.getTableChannels());
		}
		return this.spChannels;
	}
	
	private JLabel getLabelAddChannel() {
		if (this.labelAddChannel == null) {
			this.labelAddChannel = new JLabel("Add channel:");
		}
		return this.labelAddChannel;
	}
	
	public static void createInstance() {
		if (SkadiGUI.instance == null) {
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					try {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					        | UnsupportedLookAndFeelException e) {
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
		if (this.pnButtons == null) {
			this.pnButtons = new JPanel();
			this.pnButtons.add(this.getLbUpdateIndicator());
			this.pnButtons.add(this.getCbQuality());
			this.pnButtons.add(this.getBtnOpenBoth());
			this.pnButtons.add(this.getBtnStream());
			this.pnButtons.add(this.getBtnChat());
			this.pnButtons.add(this.getBtnDelete());
		}
		return this.pnButtons;
	}
	
	private JButton getBtnOpenBoth() {
		if (this.btnOpenBoth == null) {
			this.btnOpenBoth = new JButton("Open stream & chat");
			this.btnOpenBoth.setEnabled(false);
			this.btnOpenBoth.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(final ActionEvent e) {
					
					final Channel channel = SkadiGUI.this.getSelectedChannel();
					if (channel != null) {
						channel.openStreamAndChat((String) SkadiGUI.this.getCbQuality().getSelectedItem());
					}
				}
			});
		}
		return this.btnOpenBoth;
	}
	
	private JButton getBtnStream() {
		if (this.btnStream == null) {
			this.btnStream = new JButton("Open stream");
			this.btnStream.setEnabled(false);
			this.btnStream.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(final ActionEvent e) {
					final Channel channel = SkadiGUI.this.getSelectedChannel();
					if (channel != null) {
						channel.openStream((String) SkadiGUI.this.getCbQuality().getSelectedItem());
					}
				}
			});
		}
		return this.btnStream;
	}
	
	private JButton getBtnChat() {
		if (this.btnChat == null) {
			this.btnChat = new JButton("Open chat");
			this.btnChat.setEnabled(false);
			this.btnChat.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(final ActionEvent e) {
					final Channel channel = SkadiGUI.this.getSelectedChannel();
					if (channel != null) {
						channel.openChat();
					}
				}
			});
		}
		return this.btnChat;
	}
	
	private JButton getBtnDelete() {
		if (this.btnDelete == null) {
			this.btnDelete = new JButton("Delete");
			this.btnDelete.setEnabled(false);
			this.btnDelete.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(final ActionEvent e) {
					
					final Channel channel = SkadiGUI.this.getSelectedChannel();
					ChannelManager.getInstance().removeChannel(channel);
					SkadiGUI.this.getTableChannels().clearSelection();
				}
			});
		}
		return this.btnDelete;
	}
	
	private JTable getTableChannels() {
		if (this.tableChannels == null) {
			this.tableChannels = new JTable();
			this.tableChannels.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			this.tableChannels.setModel(this.tableModel);
			this.tableChannels.setDefaultRenderer(Object.class, new ChannelDataCellRenderer());
			this.tableChannels.setRowHeight(30);
			this.tableChannels.getTableHeader().setReorderingAllowed(false);
			// this.tableChannels.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			this.tableChannels.setAutoCreateRowSorter(true);
			
			final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(this.tableChannels.getModel());
			sorter.setSortsOnUpdates(true);
			sorter.setComparator(0, new BooleanComperator());
			sorter.setComparator(4, new LongComperator());
			sorter.setComparator(5, new LongComperator());
			this.tableChannels.setRowSorter(sorter);
			
			this.tableChannels.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				
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
						final int row = SkadiGUI.this.tableChannels.convertRowIndexToModel(SkadiGUI.this.tableChannels
						        .getSelectedRow());
						final Channel channel = ChannelManager.getInstance().getChannels().get(row);
						
						SkadiGUI.this.setQualities(channel.getQualityArray());
						
						if (channel.isLive() && !channel.isStreamdataRetrieved()) {
							StreamRetriever.updateStreamdataDelayed(channel);
							SkadiGUI.this.getLbUpdateIndicator().setIcon(SkadiGUI.this.updateIcon);
						} else {
							SkadiGUI.this.getLbUpdateIndicator().setIcon(null);
						}
					}
					
				}
			});
			
			this.tableChannels.getRowSorter().toggleSortOrder(0);
			this.tableChannels.getRowSorter().toggleSortOrder(0);
		}
		return this.tableChannels;
	}
	
	protected void setQualities(final String[] qualities) {
		this.getCbQuality().setModel(new DefaultComboBoxModel<String>(qualities));
	}
	
	private JButton getBtnImportFollowing() {
		if (this.btnImportFollowing == null) {
			this.btnImportFollowing = new JButton("Import followed channels");
			this.btnImportFollowing.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					new ImportDialog(SkadiGUI.this);
				}
			});
		}
		return this.btnImportFollowing;
	}
	
	private JPanel getPnBottom() {
		if (this.pnBottom == null) {
			this.pnBottom = new JPanel();
			this.pnBottom.setLayout(new BorderLayout(0, 0));
			this.pnBottom.add(this.getPnButtons(), BorderLayout.NORTH);
			this.pnBottom.add(this.getPnLog(), BorderLayout.CENTER);
		}
		return this.pnBottom;
	}
	
	private JPanel getPnLog() {
		if (this.pnLog == null) {
			this.pnLog = new JPanel();
			this.pnLog.setLayout(new BorderLayout(0, 0));
			this.pnLog.add(this.getSpLog(), BorderLayout.CENTER);
		}
		return this.pnLog;
	}
	
	private JTextArea getTaLog() {
		if (this.taLog == null) {
			this.taLog = new JTextArea();
			this.taLog.setFont(new Font("Arial", Font.PLAIN, 11));
			this.taLog.setRows(8);
			this.taLog.setEditable(false);
		}
		return this.taLog;
	}
	
	private JScrollPane getSpLog() {
		if (this.spLog == null) {
			this.spLog = new JScrollPane();
			this.spLog.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			this.spLog.setViewportView(this.getTaLog());
		}
		return this.spLog;
	}
	
	private JSplitPane getSplitPane() {
		if (this.splitPane == null) {
			this.splitPane = new JSplitPane();
			this.splitPane.setContinuousLayout(true);
			this.splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			this.splitPane.setLeftComponent(this.getSpChannels());
			this.splitPane.setRightComponent(this.getPnBottom());
		}
		return this.splitPane;
	}
	
	public static Component getInstance() {
		return SkadiGUI.instance;
	}
	
	private JComboBox<String> getCbQuality() {
		if (this.cbQuality == null) {
			this.cbQuality = new JComboBox<String>();
			this.cbQuality.setEnabled(false);
			
			final Dimension dimension = new Dimension(80, this.cbQuality.getPreferredSize().height);
			
			this.cbQuality.setSize(dimension);
			this.cbQuality.setPreferredSize(dimension);
		}
		return this.cbQuality;
	}
	
	private JLabel getLbUpdateIndicator() {
		if (this.lbUpdateIndicator == null) {
			this.lbUpdateIndicator = new JLabel();
			
			final Dimension dimension = new Dimension(16, 16);
			
			this.lbUpdateIndicator.setSize(dimension);
			this.lbUpdateIndicator.setPreferredSize(dimension);
		}
		return this.lbUpdateIndicator;
	}
	
	@Override
	public void added(final Channel channel) {
		final int size = ChannelManager.getInstance().getChannels().size();
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				
				SkadiGUI.this.tableModel.handleAdd(channel, size);
				
			}
		});
		
	}
	
	@Override
	public void removed(final Channel channel) {
		final int size = ChannelManager.getInstance().getChannels().size();
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				
				SkadiGUI.this.tableModel.handleDelete(channel, size);
				
			}
		});
	}
	
	@Override
	public void updatedMetadata(final Channel channel) {
		final int size = ChannelManager.getInstance().getChannels().size();
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				
				SkadiGUI.this.tableModel.handleUpdate(channel, size);
				
			}
		});
	}
	
	@Override
	public void updatedStreamdata(final Channel channel) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				if (channel.equals(SkadiGUI.this.getSelectedChannel())) {
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
		if (this.getTableChannels().getSelectionModel().isSelectionEmpty()) {
			return null;
		}
		
		final int row = SkadiGUI.this.getTableChannels().convertRowIndexToModel(
		        SkadiGUI.this.tableChannels.getSelectedRow());
		return ChannelManager.getInstance().getChannels().get(row);
	}
	
	private JPanel getPnTopChannel() {
		if (this.pnTopChannel == null) {
			this.pnTopChannel = new JPanel();
			this.pnTopChannel.add(this.getLabelAddChannel());
			this.pnTopChannel.add(this.getTextNewChannel());
			this.pnTopChannel.add(this.getBtnAddChannel());
			this.pnTopChannel.add(this.getBtnImportFollowing());
		}
		return this.pnTopChannel;
	}
	
	private JPanel getPnSettingsBtn() {
		if (this.pnSettingsBtn == null) {
			this.pnSettingsBtn = new JPanel();
			this.pnSettingsBtn.add(this.getBtnSettings());
		}
		return this.pnSettingsBtn;
	}
	
	private JButton getBtnSettings() {
		if (this.btnSettings == null) {
			this.btnSettings = new JButton("Settings");
			this.btnSettings.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					new SettingsDialog(SkadiGUI.this);
				}
			});
		}
		return this.btnSettings;
	}
}
