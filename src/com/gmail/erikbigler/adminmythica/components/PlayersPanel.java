package com.gmail.erikbigler.adminmythica.components;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.gmail.erikbigler.adminmythica.AdminMythica;
import com.gmail.erikbigler.adminmythica.threads.PlayerInfoUpdater;
import com.gmail.erikbigler.adminmythica.tools.Utils;

@SuppressWarnings("serial")
public class PlayersPanel extends JPanel {

	public JList<String> playerList;
	DefaultListModel<String> playerListModel;

	JTextPane playerInfo;
	JScrollPane listScroller;
	JScrollPane infoScroller;

	JFrame parent;
	JPanel left;
	JPanel right;
	JEditorPane rightScroll;

	JLabel selectName;
	boolean notSelected = true;

	JPanel status;
	public JLabel currentStatus;

	JPanel health;
	public JButton editHealth;
	public JLabel currentHealth;

	JPanel hunger;
	public JButton editHunger;
	public JLabel currentHunger;

	JPanel level;
	public JButton editLevel;
	public JLabel currentLevel;

	JPanel balance;
	public JButton editBal;
	public JLabel currentBal;

	JPanel location;
	public JLabel currentLoc;

	JPanel tools;
	public JButton heal, teleport, pm, mute, kick, store;
	public JButton ban;

	JButton refreshData;
	JPanel skin;
	public JLabel picLabel;


	ImageIcon icon;

	public PlayersPanel(JFrame parent){

		ActionListener lforButton = new ListenForButton();

		//Player List Panel
		this.parent = parent;
		icon = AdminMythica.wrench;
		left = new JPanel();
		playerListModel = new DefaultListModel<String>();
		playerList = new JList<String>(playerListModel);
		playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listScroller = new JScrollPane(playerList,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		playerList.setVisibleRowCount(17);
		playerList.setFixedCellHeight(23);
		playerList.setFixedCellWidth(150);
		ListSelectionListener lForSelection = new ListenForSelection();
		playerList.addListSelectionListener(lForSelection);
		left.add(listScroller);

		//Player Info Panel


		right = new JPanel((LayoutManager) new FlowLayout(FlowLayout.LEFT));
		right.setPreferredSize(new Dimension(610, 440));

		status = new JPanel();
		status.setBorder(BorderFactory.createTitledBorder("Status"));
		status.setPreferredSize(new Dimension(290, 60));
		status.setBackground(parent.getBackground());
		status.setVisible(false);
		currentStatus = new JLabel();
		currentStatus.setFont(this.getFont().deriveFont(20.0f));
		status.add(currentStatus);

		health = new JPanel();
		health.setBorder(BorderFactory.createTitledBorder("Health"));
		health.setPreferredSize(new Dimension(290, 60));
		health.setBackground(parent.getBackground());
		health.setVisible(false);

		currentHealth = new JLabel();
		currentHealth.setFont(currentHealth.getFont().deriveFont(20.0f));
		health.add(currentHealth);
		editHealth = new JButton(icon);
		editHealth.addActionListener(lforButton);
		health.add(editHealth);

		hunger = new JPanel();
		hunger.setBorder(BorderFactory.createTitledBorder("Hunger"));
		hunger.setPreferredSize(new Dimension(290, 60));
		hunger.setBackground(parent.getBackground());
		hunger.setVisible(false);

		currentHunger = new JLabel();
		currentHunger.setFont(currentHunger.getFont().deriveFont(20.0f));
		hunger.add(currentHunger);
		editHunger = new JButton(icon);
		editHunger.addActionListener(lforButton);
		hunger.add(editHunger);

		level = new JPanel();
		level.setBorder(BorderFactory.createTitledBorder("XP level"));
		level.setPreferredSize(new Dimension(290, 60));
		level.setBackground(parent.getBackground());
		level.setVisible(false);

		currentLevel = new JLabel();
		currentLevel.setFont(currentLevel.getFont().deriveFont(20.0f));
		level.add(currentLevel);
		editLevel = new JButton(icon);
		editLevel.addActionListener(lforButton);
		level.add(editLevel);

		balance = new JPanel();
		balance.setBorder(BorderFactory.createTitledBorder("Balance"));
		balance.setPreferredSize(new Dimension(290, 60));
		balance.setBackground(parent.getBackground());
		balance.setVisible(false);

		currentBal = new JLabel();
		currentBal.setFont(currentBal.getFont().deriveFont(20.0f));
		balance.add(currentBal);
		editBal = new JButton(icon);
		editBal.addActionListener(lforButton);
		editBal.setVisible(false);
		balance.add(editBal);

		location = new JPanel();
		location.setBorder(BorderFactory.createTitledBorder("Location"));
		location.setPreferredSize(new Dimension(290, 60));
		location.setBackground(parent.getBackground());
		location.setVisible(false);

		currentLoc = new JLabel();
		currentLoc.setFont(currentLoc.getFont().deriveFont(15.0f));
		location.add(currentLoc);

		tools = new JPanel();
		tools.setBorder(BorderFactory.createTitledBorder("Tools"));
		tools.setPreferredSize(new Dimension(580, 60));
		tools.setBackground(parent.getBackground());
		tools.setVisible(false);
		refreshData = new JButton("Refresh");
		refreshData.addActionListener(lforButton);
		teleport = new JButton("TP");
		teleport.addActionListener(lforButton);
		pm = new JButton("PM");
		pm.addActionListener(lforButton);
		mute = new JButton("Mute");
		mute.addActionListener(lforButton);
		kick = new JButton("Kick");
		kick.addActionListener(lforButton);
		ban = new JButton("Ban");
		ban.addActionListener(lforButton);
		store = new JButton("Store");
		store.addActionListener(lforButton);

		tools.add(refreshData);
		tools.add(pm);
		tools.add(teleport);
		tools.add(mute);
		tools.add(kick);
		//tools.add(ban);
		tools.add(store);

		picLabel = new JLabel();

		picLabel.setPreferredSize(new Dimension(110, 110));

		skin = new JPanel();
		skin.setPreferredSize(new Dimension(580, 110));
		skin.setBackground(parent.getBackground());
		skin.setVisible(false);
		skin.add(picLabel);


		rightScroll = new JEditorPane();
		rightScroll.setPreferredSize(new Dimension(610, 440));

		selectName = new JLabel("               Please select or start typing a player's name.");
		selectName.setFont(this.getFont().deriveFont(18.0f));
		selectName.setPreferredSize(new Dimension(600, 365));
		right.add(selectName);
		right.add(status);
		right.add(health);
		right.add(hunger);
		right.add(level);
		right.add(balance);
		right.add(location);
		right.add(tools);
		right.add(skin);

		this.add(left);
		this.add(right);

		playerList.addMouseListener( new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				if ( SwingUtilities.isRightMouseButton(e) )
				{
					try
					{
						Robot robot = new java.awt.Robot();
						robot.mousePress(InputEvent.BUTTON1_MASK);
						robot.mouseRelease(InputEvent.BUTTON1_MASK);
						System.out.println("robot clicked");
					}
					catch (AWTException ae) { System.out.println(ae); }
				}
			}

			public void mouseReleased(MouseEvent e)
			{
			}
		});

		left.setBorder(BorderFactory.createTitledBorder(null, "Players", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.getFont().deriveFont(22.0f)));
		right.setBorder(BorderFactory.createTitledBorder(null, "Player info", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.getFont().deriveFont(22.0f)));
		updatePlayerList();
	}

	@SuppressWarnings("unchecked")
	String[] getAllPlayers() {
		String[] players = null;
		String offlinePlayersData = Utils.requestDataFromServer("getOfflinePlayerNames", null);
		String onlinePlayersData = Utils.requestDataFromServer("getPlayerNames", null);
		JSONParser parser = new JSONParser();
		JSONObject offlinePlayersObj = null;
		JSONObject onlinePlayersObj = null;
		try {
			offlinePlayersObj = (JSONObject) parser.parse(offlinePlayersData);
			onlinePlayersObj = (JSONObject) parser.parse(onlinePlayersData);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		JSONArray offlinePlayers = (JSONArray) offlinePlayersObj.get("success");
		JSONArray onlinePlayers = (JSONArray) onlinePlayersObj.get("success");
		offlinePlayers.addAll(onlinePlayers);
		ArrayList<String> allPlayers = offlinePlayers;
		Collections.sort(allPlayers, new SortIgnoreCase());
		players = new String[allPlayers.size()];
		allPlayers.toArray(players);
		return players;
	}
	@SuppressWarnings("unchecked")
	ArrayList<String> getOnlinePlayers() {
		String onlinePlayersData = Utils.requestDataFromServer("getPlayerNames", null);
		JSONParser parser = new JSONParser();
		JSONObject onlinePlayersObj = null;
		try {
			onlinePlayersObj = (JSONObject) parser.parse(onlinePlayersData);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		JSONArray onlinePlayers = (JSONArray) onlinePlayersObj.get("success");
		ArrayList<String> playas = onlinePlayers;
		return playas;
	}

	public void setButtonsVisibility(boolean setting){
		editHealth.setVisible(setting);
		editHunger.setVisible(setting);
		editLevel.setVisible(setting);
		editBal.setVisible(setting);
		pm.setVisible(setting);
		kick.setVisible(setting);
		mute.setVisible(setting);
		teleport.setVisible(setting);
	}
	public void updatePlayerList() {
		String[] players = getAllPlayers();
		for(String player: players) {
			playerListModel.addElement(player);
		}
	}

	void updatePlayerData() {
		Thread updatePlayerData = new PlayerInfoUpdater(playerList.getSelectedValue(), this);
		updatePlayerData.start();
	}


	class ListenForSelection implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(e.getSource() == playerList && !e.getValueIsAdjusting()) {
				if(notSelected) {
					right.remove(selectName);
					notSelected = false;
					status.setVisible(true);
					health.setVisible(true);
					hunger.setVisible(true);
					level.setVisible(true);
					balance.setVisible(true);
					location.setVisible(true);
					tools.setVisible(true);
					skin.setVisible(true);
				}
				updatePlayerData();
				right.setBorder(BorderFactory.createTitledBorder(null, playerList.getSelectedValue() + " info", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, parent.getFont().deriveFont(22.0f)));
			}
		}
	}

	class ListenForButton implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == editHealth) {
				String input = Utils.openDialog(parent, "Edit " + playerList.getSelectedValue() + " Health", "Please enter a new health value (0-20):", "");
				if(input != null) {
					try {
						int health = Integer.parseInt(input);
						if(health > 20 || health < 0) {
							JOptionPane.showMessageDialog(parent, "That number is out of bounds! Enter a number between 0 and 20", "Invalid Entry", JOptionPane.ERROR_MESSAGE);
						} else {
							Utils.sendMethodToServer("setPlayerHealth", "\"" + playerList.getSelectedValue() + "\"," + input);
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(parent, "Please enter an integer!", "Invalid Entry", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					System.out.println("null!");
				}
			}

			else if(e.getSource() == editHunger) {
				String input = Utils.openDialog(parent, "Edit " + playerList.getSelectedValue() + " Hunger", "Please enter a new hunger value (0-20):", "");
				if(input != null) {
					try {
						int health = Integer.parseInt(input);
						if(health > 20 || health < 0) {
							JOptionPane.showMessageDialog(parent, "That number is out of bounds! Enter a number between 0 and 20", "Invalid Entry", JOptionPane.ERROR_MESSAGE);
						} else {
							Utils.sendMethodToServer("setPlayerFoodLevel", "\"" + playerList.getSelectedValue() + "\"," + input);
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(parent, "Please enter an integer!", "Invalid Entry", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					System.out.println("null!");
				}
			}
			else if(e.getSource() == editLevel) {
				String input = Utils.openDialog(parent, "Edit " + playerList.getSelectedValue() + " Level", "Please enter a new XP level:", "");
				if(input != null) {
					try {
						int xp = Integer.parseInt(input);
						if(xp < 0) {
							JOptionPane.showMessageDialog(parent, "That number is too low! Enter a number above 0", "Invalid Entry", JOptionPane.ERROR_MESSAGE);
						} else {
							Utils.sendMethodToServer("setPlayerLevel", "\"" + playerList.getSelectedValue() + "\"," + input);
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(parent, "Please enter an integer!", "Invalid Entry", JOptionPane.ERROR_MESSAGE);
					}
				} else {
				}
			} else if (e.getSource() == refreshData) {

			} else if (e.getSource() == teleport) {
				ArrayList<String> possibilities = getOnlinePlayers();
				possibilities.remove(playerList.getSelectedValue());
				possibilities.add("Spawn");
				possibilities.add("Custom Location");
				String[] possibilitiesArray = new String[possibilities.size()];
				possibilities.toArray(possibilitiesArray);
				String s = (String)JOptionPane.showInputDialog(
						parent,
						"Please choose a player or location as \n teleport destination:",
						"Telport",
						JOptionPane.PLAIN_MESSAGE,
						null,
						possibilitiesArray,
						null);
				if(s == null) return;
				if(s.equals("Custom Location")) {
					Object[] worlds = {"The Realm", "Main Nether", "End", "Temp World", "Temp Nether", "Creative"};
					String world = (String)JOptionPane.showInputDialog(
							parent,
							"Please choose the world for your custom location:",
							"Teleport",
							JOptionPane.PLAIN_MESSAGE,
							null,
							worlds,
							null);
					if(world == null) return;
					String coords = (String)JOptionPane.showInputDialog(
							parent,
							"Please specify the coords of your custom location\nseperated by comas: (I.E. 10,63,-543)",
							"Teleport",
							JOptionPane.PLAIN_MESSAGE,
							null,
							null,
							"x,y,z");
					if(coords == null) return;
					String[] splitCoords = coords.split(",");
					if(splitCoords.length != 3) {
						JOptionPane.showMessageDialog(parent, "Error getting custom location! Please try again.", "Invalid Entry", JOptionPane.ERROR_MESSAGE);
						return;
					}
					int x;
					int y;
					int z;
					try {
						x = Integer.parseInt(splitCoords[0].trim());
						y = Integer.parseInt(splitCoords[1].trim());
						z = Integer.parseInt(splitCoords[2].trim());
					} catch (Exception exc) {
						JOptionPane.showMessageDialog(parent, "Error getting custom location! Please try again.", "Invalid Entry", JOptionPane.ERROR_MESSAGE);
						return;
					}
					switch(world) {
					case "The Realm":
						world = "The_Realm";
						break;
					case "Main Nether":
						world = "The_Realm_nether";
						break;
					case "End":
						world = "The_Realm_the_end";
						break;
					case "Temp World":
						world = "The_Temp";
						break;
					case "Temp Nether":
						world = "Temp_Nether";
						break;
					}
					Utils.sendMethodToServer("teleportPlayerToWorldLocation", "\"" + playerList.getSelectedValue() + "\",\"" + world + "\"," + x + "," + y + "," + z);
				}
				if(s.equals("Spawn")) {
					Utils.sendMethodToServer("teleportPlayerToWorldLocation", "\"" + playerList.getSelectedValue() + "\",\"" + "The_Realm" + "\"," + -38 + "," + 133 + "," + 21);
					return;
				} else {
					Utils.sendMethodToServer("teleport", "\"" + playerList.getSelectedValue() + "\",\"" + s + "\"");
				}
			} else if(e.getSource() == store) {
				String itemName = (String)JOptionPane.showInputDialog(
						parent,
						"Type the name of the store item to give",
						"Store",
						JOptionPane.PLAIN_MESSAGE,
						null,
						null,
						"");
				if(itemName == null) return;
				Utils.sendMethodToServer("giveStoreItem", "\"" + itemName + "\",\"" + playerList.getSelectedValue() + "\"");
				System.out.println("Sent store item " + itemName + " to " + playerList.getSelectedValue());
			} else if(e.getSource() == pm) {
				String message = (String)JOptionPane.showInputDialog(
						parent,
						"Enter Private Message",
						"Private Message",
						JOptionPane.PLAIN_MESSAGE,
						null,
						null,
						"");
				if(message == null) return;
				message ="&cFrom admin: &7" + message;
				message = Utils.translateAlternateColorCodes('&', message);
				Utils.sendMethodToServer("sendMessage", "\"" + playerList.getSelectedValue() + "\",\"" + message + "\"");
			} else if(e.getSource() == mute) {
				Utils.sendMethodToServer("runConsoleCommand", "\"mute " + playerList.getSelectedValue()+ "\"");
			} else if(e.getSource() == kick) {
				Utils.sendMethodToServer("kickPlayer", "\"" + playerList.getSelectedValue() + "\"," + "\"You have been kicked by an admin!\"");
			} else if(e.getSource() == ban) {
				String text = ban.getText();
				if(text.equals("Ban")) {
					Utils.sendMethodToServer("ban", "\"" + playerList.getSelectedValue()+ "\"");
				} else {
					Utils.sendMethodToServer("unban", "\"" + playerList.getSelectedValue()+ "\"");
				}
			} else if(e.getSource() == editBal) {
				Object[] options = {"Cancel",
						"Remove",
				"Add"};
				int n = JOptionPane.showOptionDialog(parent,
						"How would you like to edit " + playerList.getSelectedValue() + "'s balance?",
						"Edit Balance",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE,
						null,
						options,
						options[0]);
				if(n == 2) {
					String input = Utils.openDialog(parent, "Edit Balance", "Please enter how much money to add:", "0.00");
					if(input != null) {
						try {
							double add = Double.parseDouble(input.replace("$", ""));
							if(add > 0) {
								Utils.sendMethodToServer("econ.depositPlayer", "\"" + playerList.getSelectedValue() + "\"," + input);
							}
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(parent, "Please enter an integer!", "Invalid Entry", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				if(n == 1) {
					String input = Utils.openDialog(parent, "Edit Balance", "Please enter how much money to remove:", "0.00");
					if(input != null) {
						try {
							double add = Double.parseDouble(input.replace("$", ""));
							if(add > 0) {
								Utils.sendMethodToServer("econ.withdrawPlayer", "\"" + playerList.getSelectedValue() + "\"," + input);
							}
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(parent, "Please enter an integer!", "Invalid Entry", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			} else {
				playerList.requestFocusInWindow();
				return;
			}
			updatePlayerData();
			playerList.requestFocusInWindow();
		}

	}

	public class SortIgnoreCase implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			String s1 = (String) o1;
			String s2 = (String) o2;
			return s1.toLowerCase().compareTo(s2.toLowerCase());
		}
	}
}

