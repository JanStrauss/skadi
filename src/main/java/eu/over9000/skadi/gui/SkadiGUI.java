package eu.over9000.skadi.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import eu.over9000.skadi.SkadiMain;
import eu.over9000.skadi.channel.ChannelInstance;

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
	private JList<ChannelInstance> listChannels;
	private JLabel labelAddChannel;
	
	private final ChannelDataListModel listModel;
	private JPanel panel;
	private JButton btnOpenBoth;
	private JButton btnStream;
	private JButton btnChat;
	private JButton btnDelete;
	
	private SkadiGUI() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.listModel = new ChannelDataListModel();
		
		this.initialize();
		this.pack();
		this.setMinimumSize(this.getSize());
		this.setVisible(true);
	}
	
	private void initialize() {
		this.setTitle("Skadi");
		this.setIconImage(new ImageIcon(this.getClass().getResource("/icon.png")).getImage());
		this.setLocationRelativeTo(null);
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		this.getContentPane().add(this.getPnNew(), BorderLayout.NORTH);
		this.getContentPane().add(this.getSpChannels(), BorderLayout.CENTER);
		this.getContentPane().add(this.getPanel(), BorderLayout.SOUTH);
	}
	
	private JPanel getPnNew() {
		if (this.pnNew == null) {
			this.pnNew = new JPanel();
			this.pnNew.add(this.getLabelAddChannel());
			this.pnNew.add(this.getTextNewChannel());
			this.pnNew.add(this.getBtnAddChannel());
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
			this.spChannels.setViewportView(this.getListChannels());
		}
		return this.spChannels;
	}
	
	private JList<ChannelInstance> getListChannels() {
		if (this.listChannels == null) {
			this.listChannels = new JList<>(this.listModel);
			this.listChannels.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			this.listChannels.setCellRenderer(new ChannelDataListRenderer());
		}
		return this.listChannels;
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
			e.printStackTrace();
		}
		
		SkadiGUI.instance = new SkadiGUI();
	}
	
	public static void handleChannelListUpdate() {
		SkadiGUI.instance.listModel.handleUpdate();
	}
	
	private JPanel getPanel() {
		if (this.panel == null) {
			this.panel = new JPanel();
			this.panel.add(this.getBtnOpenBoth());
			this.panel.add(this.getBtnStream());
			this.panel.add(this.getBtnChat());
			this.panel.add(this.getBtnDelete());
		}
		return this.panel;
	}
	
	private JButton getBtnOpenBoth() {
		if (this.btnOpenBoth == null) {
			this.btnOpenBoth = new JButton("Open Stream & Chat");
			this.btnOpenBoth.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(final ActionEvent e) {
					final ChannelInstance channel = SkadiGUI.this.listChannels.getSelectedValue();
					
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
			this.btnStream.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(final ActionEvent e) {
					final ChannelInstance channel = SkadiGUI.this.listChannels.getSelectedValue();
					
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
			this.btnChat.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(final ActionEvent e) {
					final ChannelInstance channel = SkadiGUI.this.listChannels.getSelectedValue();
					
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
			this.btnDelete.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(final ActionEvent e) {
					final ChannelInstance channel = SkadiGUI.this.listChannels.getSelectedValue();
					
					SkadiMain.getInstance().deleteChannel(channel);
				}
			});
		}
		return this.btnDelete;
	}
}