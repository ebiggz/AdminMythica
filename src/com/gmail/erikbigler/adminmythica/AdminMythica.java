package com.gmail.erikbigler.adminmythica;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.gmail.erikbigler.adminmythica.components.PlayerListPopup;
import com.gmail.erikbigler.adminmythica.components.PlayersPanel;
import com.gmail.erikbigler.adminmythica.threads.ConnectionsStream;
import com.gmail.erikbigler.adminmythica.threads.ServerConnector;
import com.gmail.erikbigler.adminmythica.tools.Utils;

@SuppressWarnings("serial")
public class AdminMythica extends JFrame {

	public static JFrame main;
	public JTabbedPane tabbedPane;

	public JTextField username;
	public JPasswordField password;
	public JButton loginButton;
	public JButton cancelButton;

	public JPanel consolePanel;
	public JPanel chatPanel;
	public PlayersPanel pp;

	public JTextArea consoleFeed;
	public JTextField commandCompose;
	public JScrollPane consoleScrollbar;

	public JTextArea chatFeed;
	public JTextField chatCompose;
	public JTextField chatName;
	public String chatNameText = "&cAdmin";
	public JScrollPane chatScrollbar;
	public JScrollPane pListScrollbar;
	public JSplitPane chatSplit;
	public JComboBox<String> chatChannels;
	public HashMap<String, JCheckBox> channelCheckBoxes;
	public JCheckBox global, local, modChat, spawnChat, rememberMe;
	public JDialog channelOptions;
	public static JDialog login;
	public JButton ok;
	public JButton chatSettings;
	public static ListenForAction lForAction;

	public JList<String> playerList;
	public DefaultListModel<String> defListModel = new DefaultListModel<String>();
	public JPopupMenu popup;
	public JMenuItem popupItem;
	public static ImageIcon wrench;
	public ImageIcon spinner;
	public JLabel spinLabel;
	public Preferences prefs;

	public static void main(String[] args){
		//attemptLAFChange();
		new AdminMythica();
	}

	@SuppressWarnings("unchecked")
	public AdminMythica(){
		new Utils();
		this.setTitle("Mythica Administration");
		this.setSize(900, 515);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//this.setResizable(false);

		InputStream imgStream = this.getClass().getClassLoader().getResourceAsStream("spire.png");
		Image spire = null;
		try {
			spire = ImageIO.read(imgStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setIconImage(spire);

		main = this;
		imgStream = this.getClass().getClassLoader().getResourceAsStream("wrench.png");
		Image wrench = null;
		try {
			wrench = ImageIO.read(imgStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		AdminMythica.wrench = new ImageIcon(wrench);

		lForAction = new ListenForAction();

		username = new JTextField(8);
		username.setBorder(BorderFactory.createTitledBorder("Username"));
		password = new JPasswordField(8);
		password.setBorder(BorderFactory.createTitledBorder("Password"));
		loginButton = new JButton("Login");
		cancelButton = new JButton("Cancel");
		password.addActionListener(lForAction);
		loginButton.addActionListener(lForAction);
		cancelButton.addActionListener(lForAction);
		rememberMe = new JCheckBox("Remember me", false);

		tabbedPane = new JTabbedPane();

		consolePanel = new JPanel();
		chatPanel = new JPanel();
		pp = (PlayersPanel) new PlayersPanel(this);

		tabbedPane.addTab("Console", consolePanel);
		tabbedPane.addTab("Chat", chatPanel);
		tabbedPane.addTab("Players", pp);

		int dividerLoc = 640;
		if(UIManager.getLookAndFeel().getName().equalsIgnoreCase("Metal")) {
			consoleFeed = new JTextArea(23, 76);
			consoleFeed.setFont(consoleFeed.getFont().deriveFont(13.0f));
			commandCompose = new JTextField("", 78);
			chatFeed = new JTextArea(19, 64);
			chatFeed.setFont(chatFeed.getFont().deriveFont(13.0f));
			chatCompose = new JTextField("", 64);
			dividerLoc = 700;
		} else {
			consoleFeed = new JTextArea(25, 71);
			commandCompose = new JTextField("", 71); //71
			chatFeed = new JTextArea(25, 60);
			chatCompose = new JTextField("", 55); //60
		}

		String ourNodeName = "/com/gmail/erikbigler/adminmythica";
		prefs = Preferences.userRoot().node(ourNodeName);
		String chatNameString = prefs.get("chatName", null);
		if(chatNameString == null) {
			prefs.put("chatName", "&cAdmin");
		}
		consoleFeed.setText("Streaming console...");
		consoleFeed.setLineWrap(true);
		consoleFeed.setWrapStyleWord(true);
		consoleFeed.setEditable(false);

		consoleScrollbar = new JScrollPane(consoleFeed, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		consolePanel.add(consoleScrollbar);

		commandCompose.addActionListener(lForAction);
		consolePanel.add(commandCompose);

		chatFeed.setText("Streaming chat...");
		chatFeed.setLineWrap(true);
		chatFeed.setWrapStyleWord(true);
		chatFeed.setEditable(false);

		chatScrollbar = new JScrollPane(chatFeed, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		playerList = new JList<String>(defListModel);
		playerList.setFont(playerList.getFont().deriveFont(14.0f));
		playerList.setVisibleRowCount(22);
		playerList.setFixedCellHeight(18);
		playerList.setFixedCellWidth(110);
		pListScrollbar = new JScrollPane(playerList,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		chatSplit= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatScrollbar, pListScrollbar);
		chatSplit.setOneTouchExpandable(true);
		chatSplit.setDividerLocation(dividerLoc);
		chatPanel.add(chatSplit);

		String jsonString = Utils.requestDataFromServer("getChatChannelNames", null);
		Object obj = null;
		try {
			JSONParser parser = new JSONParser();
			obj = parser.parse(jsonString);
		} catch (Exception e) {
		}
		JSONObject result = (JSONObject)obj;
		JSONArray jChannels = (JSONArray) result.get("success");
		jChannels.remove("Kids");
		jChannels.remove("Survival");
		jChannels.remove("PvP");
		jChannels.remove("Spawn");
		String[] channels = new String[jChannels.size()];
		jChannels.toArray(channels);

		channelOptions = new JDialog(this);
		channelOptions.setTitle("Chat Options");
		channelCheckBoxes = new HashMap<String, JCheckBox>();
		global = new JCheckBox("Global", true);
		local = new JCheckBox("Local", true);
		modChat = new JCheckBox("ModChat", true);
		spawnChat = new JCheckBox("SpawnChat", true);
		channelCheckBoxes.put("Global", global);
		channelCheckBoxes.put("Local", local);
		channelCheckBoxes.put("ModChat", modChat);
		channelCheckBoxes.put("SoawnChat", spawnChat);
		JPanel channelCBs = new JPanel();
		channelCBs.add(global);
		channelCBs.add(local);
		channelCBs.add(modChat);
		channelCBs.add(spawnChat);
		channelCBs.setBorder(BorderFactory.createTitledBorder("Feed Chat Channels"));
		channelOptions.add(channelCBs, "North");
		String chatNameStr = prefs.get("chatName", null);
		chatName = new JTextField(chatNameStr, 10);
		JPanel chatNameP = new JPanel();
		chatNameP.setBorder(BorderFactory.createTitledBorder("Chat Name"));
		chatNameP.add(chatName);
		channelOptions.add(chatNameP, "Center");
		ok = new JButton("     OK     ");
		ok.addActionListener(lForAction);
		JPanel buttonP = new JPanel();
		buttonP.add(ok);
		channelOptions.add(buttonP, "South");
		channelOptions.setLocationRelativeTo(null);
		channelOptions.pack();
		channelOptions.setModalityType(ModalityType.APPLICATION_MODAL);
		channelOptions.setResizable(false);

		chatChannels = new JComboBox<String>(channels);
		chatChannels.setSelectedItem("Global");
		chatChannels.setToolTipText("Channel to chat in");
		chatChannels.setPreferredSize(new Dimension(120, 22));
		popupItem = new JMenuItem();
		popupItem.addActionListener(lForAction);
		popup = new PlayerListPopup(popupItem);
		playerList.addMouseListener( new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					playerList.setSelectedIndex( playerList.locationToIndex(e.getPoint()) );
					popup.show(playerList, e.getX(), e.getY());
				}
			}
		});


		chatCompose.addActionListener(lForAction);
		chatSettings = new JButton();
		chatSettings.setIcon(AdminMythica.wrench);
		chatSettings.addActionListener(lForAction);
		chatPanel.add(chatSettings);
		chatPanel.add(chatChannels);
		chatPanel.add(chatCompose);

		this.add(tabbedPane);

		verifyIdentity(this);

		this.setVisible(true);

		Thread connections = new ConnectionsStream(this);
		Thread serverConnect = new ServerConnector();
		connections.start();
		serverConnect.start();

		ArrayList<String> onlineP = Utils.getOnlinePlayers();
		String[] players = new String[onlineP.size()];
		onlineP.toArray(players);
		defListModel.clear();
		for(String player: players){
			defListModel.addElement(player);
		}
	}


	//Implement listeners

	private class ListenForAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == commandCompose) {
				String text = commandCompose.getText();
				if(!text.isEmpty()) {
					Utils.sendMethodToServer("runConsoleCommand", "\"" + text + "\"");
					consoleFeed.setCaretPosition(consoleFeed.getDocument().getLength());
					commandCompose.setText("");
				}
			}
			if(e.getSource() == chatCompose) {
				String text = chatCompose.getText();
				if(!text.isEmpty()) {
					String channel = (String) chatChannels.getSelectedItem();
					String sender = Utils.translateAlternateColorCodes('&', chatName.getText()) + Utils.COLOR_CHAR+"r";
					String message = Utils.translateAlternateColorCodes('&', text);
					Utils.sendMethodToServer("sendChatChannelMessage", "\"" + channel + "\",\"" + sender + "\",\"" + message + "\"");
					chatFeed.setCaretPosition(chatFeed.getDocument().getLength());
					chatCompose.setText("");
					prefs.remove("chatName");
					prefs.put("chatName", chatName.getText());
				}
			}
			if(e.getSource() == popupItem) {
				String player = playerList.getSelectedValue();
				pp.playerList.setSelectedValue(player, true);
				tabbedPane.setSelectedIndex(2);
				pp.playerList.requestFocus();
			}
			if(e.getSource() == chatSettings) {
				channelOptions.setLocationRelativeTo(main);
				channelOptions.setModal(true);
				channelOptions.setModalityType(ModalityType.APPLICATION_MODAL);
				channelOptions.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				channelOptions.setVisible(true);
			}
			if(e.getSource() == ok) {
				channelOptions.setVisible(false);
				prefs.remove("chatName");
				prefs.put("chatName", chatName.getText());
			}
			if(e.getSource() == loginButton || e.getSource() == password) {
				attemptLogin();
			}
			if(e.getSource() == cancelButton) {
				System.exit(0);
			}
		}
	}
	static void attemptLAFChange() {
		//com.apple.laf.AquaLookAndFeel
		LookAndFeelInfo[] laf = UIManager.getInstalledLookAndFeels();
		for(int i = 0; i < laf.length; i++) {
			if(laf[i].getClassName().equals("javax.swing.plaf.metal.MetalLookAndFeel")) {
				try {
					UIManager.setLookAndFeel(laf[i].getClassName());
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	static void verifyIdentity(AdminMythica main) {
		InputStream imgStream = main.getClass().getClassLoader().getResourceAsStream("login_logo.png");
		Image logo = null;
		try {
			logo = ImageIO.read(imgStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		JLabel logoL = new JLabel(new ImageIcon(logo));
		login = new JDialog();
		login.setTitle("Login");
		login.getContentPane().setLayout(new BoxLayout(login.getContentPane(), BoxLayout.Y_AXIS));
		JPanel creds = new JPanel();
		JPanel buttons = new JPanel();
		JPanel loginOptions = new JPanel();
		String rememberMePref = main.prefs.get("rememberMe", "false");
		if(rememberMePref.equals("true")) {
			main.rememberMe.setSelected(true);
		}
		loginOptions.add(main.rememberMe);
		if(main.rememberMe.isSelected()) {
			main.username.setText(main.prefs.get("username", ""));
			main.password.setText(main.prefs.get("password", ""));
		}
		creds.add(main.username);
		creds.add(main.password);
		logoL.setAlignmentX(Component.CENTER_ALIGNMENT);
		login.getContentPane().add(logoL);
		login.getContentPane().add(creds);
		login.getContentPane().add(loginOptions);
		buttons.add(main.loginButton);
		buttons.add(main.cancelButton);
		login.getContentPane().add(buttons);
		login.setModal(true);
		login.setModalityType(ModalityType.APPLICATION_MODAL);
		login.pack();
		login.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		login.setLocationRelativeTo(null);
		login.setVisible(true);
	}
	public static String checkLogin(String username, String password) {
		String url = "http://www.mythicacraft.com/regcheck.php?username=" + username + "&password=" + password;
		String reply = "";
		try {
			URL u = new URL(url);
			URLConnection c = u.openConnection();
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							c.getInputStream()));
			reply = in.readLine();
			in.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return reply;
	}
	@SuppressWarnings("unchecked")
	void attemptLogin() {
		if(username.getText().isEmpty() || password.getPassword().length < 1) return;
		String pass = "";
		for(char c: password.getPassword()) {
			pass += c;
		}
		String reply = checkLogin(username.getText(), pass);

		if(reply.startsWith("1")) {
			String mcName = reply.substring(reply.indexOf(",")+1, reply.length()).trim();
			String result = Utils.requestDataFromServer("permissions.getGroups", "\""+mcName+"\"");
			JSONParser parser = new JSONParser();
			JSONObject data = null;
			try {
				data = (JSONObject) parser.parse(result);
			} catch (ParseException e2) {
				e2.printStackTrace();
			}
			JSONArray permGroups = (JSONArray) data.get("success");
			ArrayList<String> groups = permGroups;
			if(!(groups.contains("Owner") || groups.contains("Admin") || groups.contains("Moderator"))) {
				JOptionPane.showMessageDialog(main,
						"You arn't authorized to use this app!",
						"Not Authorized!",
						JOptionPane.ERROR_MESSAGE);
			} else {
				login.setVisible(false);
				if(groups.contains("Moderator")) {
					tabbedPane.setEnabledAt(0, false);
					tabbedPane.setSelectedIndex(1);
					pp.ban.setEnabled(false);
					pp.editBal.setEnabled(false);
					pp.editHealth.setEnabled(false);
					pp.editHunger.setEnabled(false);
					pp.editLevel.setEnabled(false);
				}
				if(groups.contains("Admin")) {
					commandCompose.setEnabled(false);
				}
				if(rememberMe.isSelected()) {
					prefs.put("username", username.getText());
					prefs.put("password", pass);
					prefs.put("rememberMe", "true");
				} else {
					prefs.put("rememberMe", "false");
					prefs.put("username", null);
					prefs.put("password", null);
				}
			}
		} else {
			JOptionPane.showMessageDialog(main,
					"Sorry, that username/password combo wasn't recognized.",
					"Not Recognized!",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
