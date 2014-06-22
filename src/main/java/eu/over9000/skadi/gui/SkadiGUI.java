package eu.over9000.skadi.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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

import eu.over9000.skadi.SkadiMain;
import eu.over9000.skadi.channel.Channel;
import eu.over9000.skadi.logging.SkadiLogging;
import eu.over9000.skadi.util.comperator.BooleanComperator;
import eu.over9000.skadi.util.comperator.LongComperator;

public class SkadiGUI extends JFrame {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2045150091920320920L;
	
	private static SkadiGUI instance;
	
	private JPanel pnNew;
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
	
	private SkadiGUI() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.tableModel = new ChannelDataTableModel();
		this.setMinimumSize(new Dimension(640, 480));
		this.initialize();
		this.pack();
		this.setVisible(true);
	}
	
	private void initialize() {
		this.setTitle("Skadi");
		this.setIconImage(new ImageIcon(this.getClass().getResource("/icon.png")).getImage());
		this.setLocationRelativeTo(null);
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		this.getContentPane().add(this.getPnNew(), BorderLayout.NORTH);
		this.getContentPane().add(this.getSpChannels(), BorderLayout.CENTER);
		this.getContentPane().add(this.getPnButtons(), BorderLayout.SOUTH);
	}
	
	private JPanel getPnNew() {
		if (this.pnNew == null) {
			this.pnNew = new JPanel();
			this.pnNew.add(this.getLabelAddChannel());
			this.pnNew.add(this.getTextNewChannel());
			this.pnNew.add(this.getBtnAddChannel());
			this.pnNew.add(this.getBtnImportFollowing());
		}
		return this.pnNew;
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
					SkadiMain.getInstance().addNewChannel(SkadiGUI.this.getTextNewChannel().getText());
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
	
	public static void create() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
		        | UnsupportedLookAndFeelException e) {
			SkadiLogging.log(e);
		}
		
		SkadiGUI.instance = new SkadiGUI();
	}
	
	public static SkadiGUI getInstance() {
		return SkadiGUI.instance;
	}
	
	public static void handleChannelTableUpdate(final Channel channel) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				SkadiGUI.instance.tableModel.handleUpdate(channel);
				
			}
		});
		
	}
	
	public static void handleChannelTableDelete(final Channel channel) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				SkadiGUI.instance.tableModel.handleDelete(channel);
				
			}
		});
		
	}
	
	public static void handleChannelTableAdd(final Channel channel) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				SkadiGUI.instance.tableModel.handleAdd(channel);
				
			}
		});
		
	}
	
	private JPanel getPnButtons() {
		if (this.pnButtons == null) {
			this.pnButtons = new JPanel();
			this.pnButtons.add(this.getBtnOpenBoth());
			this.pnButtons.add(this.getBtnStream());
			this.pnButtons.add(this.getBtnChat());
			this.pnButtons.add(this.getBtnDelete());
		}
		return this.pnButtons;
	}
	
	private JButton getBtnOpenBoth() {
		if (this.btnOpenBoth == null) {
			this.btnOpenBoth = new JButton("Open Stream & Chat");
			this.btnOpenBoth.setEnabled(false);
			this.btnOpenBoth.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(final ActionEvent e) {
					final int row = SkadiGUI.this.tableChannels.convertRowIndexToModel(SkadiGUI.this.tableChannels
					        .getSelectedRow());
					SkadiMain.getInstance();
					final Channel channel = SkadiMain.getInstance().getChannels().get(row);
					if (channel != null) {
						channel.openStreamAndChat();
					}
				}
			});
		}
		return this.btnOpenBoth;
	}
	
	private JButton getBtnStream() {
		if (this.btnStream == null) {
			this.btnStream = new JButton("Open Stream");
			this.btnStream.setEnabled(false);
			this.btnStream.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(final ActionEvent e) {
					final int row = SkadiGUI.this.tableChannels.convertRowIndexToModel(SkadiGUI.this.tableChannels
					        .getSelectedRow());
					final Channel channel = SkadiMain.getInstance().getChannels().get(row);
					if (channel != null) {
						channel.openStream();
					}
				}
			});
		}
		return this.btnStream;
	}
	
	private JButton getBtnChat() {
		if (this.btnChat == null) {
			this.btnChat = new JButton("Open Chat");
			this.btnChat.setEnabled(false);
			this.btnChat.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(final ActionEvent e) {
					final int row = SkadiGUI.this.tableChannels.convertRowIndexToModel(SkadiGUI.this.tableChannels
					        .getSelectedRow());
					final Channel channel = SkadiMain.getInstance().getChannels().get(row);
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
					
					final int row = SkadiGUI.this.tableChannels.convertRowIndexToModel(SkadiGUI.this.tableChannels
					        .getSelectedRow());
					final Channel channel = SkadiMain.getInstance().getChannels().get(row);
					SkadiMain.getInstance().deleteChannel(channel);
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
			this.tableChannels.setDefaultRenderer(String.class, new ChannelDataCellRenderer());
			this.tableChannels.setRowHeight(30);
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
					
					final ListSelectionModel lsm = (ListSelectionModel) e.getSource();
					SkadiGUI.this.getBtnChat().setEnabled(!lsm.isSelectionEmpty());
					SkadiGUI.this.getBtnDelete().setEnabled(!lsm.isSelectionEmpty());
					SkadiGUI.this.getBtnOpenBoth().setEnabled(!lsm.isSelectionEmpty());
					SkadiGUI.this.getBtnStream().setEnabled(!lsm.isSelectionEmpty());
					
				}
			});
			this.applyPrefWidth();
			
		}
		return this.tableChannels;
	}
	
	public void applyPrefWidth() {
		if (this.tableChannels == null) {
			return;
		}
		
		this.tableChannels.getColumnModel().getColumn(0).setPreferredWidth(40);
		this.tableChannels.getColumnModel().getColumn(0).setMaxWidth(40);
		this.tableChannels.getColumnModel().getColumn(0).setMaxWidth(40);
		this.tableChannels.getColumnModel().getColumn(0).setWidth(40);
		
		this.tableChannels.getColumnModel().getColumn(1).setPreferredWidth(150);
		this.tableChannels.getColumnModel().getColumn(2).setPreferredWidth(200);
		
		this.tableChannels.getColumnModel().getColumn(4).setPreferredWidth(80);
		this.tableChannels.getColumnModel().getColumn(5).setPreferredWidth(90);
	}
	
	private JButton getBtnImportFollowing() {
		if (this.btnImportFollowing == null) {
			this.btnImportFollowing = new JButton("Import Followed Channels");
			this.btnImportFollowing.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					new ImportDialog();
				}
			});
		}
		return this.btnImportFollowing;
	}
}