package com.gmail.erikbigler.adminmythica.threads;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.gmail.erikbigler.adminmythica.AdminMythica;
import com.gmail.erikbigler.adminmythica.tools.Utils;



public class ConnectionsStream extends Thread {

	private BufferedReader in;
	private PrintWriter out;
	private AdminMythica parent;
	private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + "[^ -~][^ -~]" + "[0-9A-FK-OR]");
	String previous = "";



	public ConnectionsStream(AdminMythica parent) {
		this.parent = parent;
	}

	@SuppressWarnings("resource")
	public void run() {
		try {
			String serverAddress = "mc.mythicacraft.com";
			Socket socket = new Socket(serverAddress, 20060);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (Exception e) {

		}
		out.println("/api/subscribe?source=connections&key=" + Utils.getKey("connections") + "&show_previous=false");
		out.println("/api/subscribe?source=console&key=" + Utils.getKey("console"));
		out.println("/api/subscribe?source=chat&key=" + Utils.getKey("chat"));
		out.println("/api/subscribe?source=herochat&key=" + Utils.getKey("herochat"));
		out.println("/api/subscribe?source=notifications&key=" + Utils.getKey("notifications") + "&show_previous=false");
		out.println("/api/subscribe?source=irc&key=" + Utils.getKey("irc"));

		while (true) {
			String line = null;
			Object obj = null;
			try {
				line = in.readLine();
				JSONParser parser = new JSONParser();
				obj = parser.parse(line);
			} catch (Exception e) {
			}
			JSONObject result = (JSONObject)obj;
			JSONObject data = (JSONObject) result.get("success");

			String source = (String) result.get("source");

			if(source.equals("console")) {
				String consoleLine = (String) data.get("line");
				consoleLine = consoleLine.replace("\n", "");
				if(!consoleLine.equals(previous)) {
					parent.consoleFeed.append("\n" + stripColor(consoleLine));
					parent.consoleFeed.setCaretPosition(parent.consoleFeed.getDocument().getLength());
					previous = consoleLine;
				}
			}
			//else if(source.equals("chat")) {
			//	String playerMessage = data.get("player") + ": " + data.get("message");
			//	if(playerMessage.startsWith(": ")) {
			//		playerMessage = playerMessage.replaceFirst(": ", "");
			//	}
			//	parent.chatFeed.append("\n" + stripColor(playerMessage));
			//	parent.chatFeed.setCaretPosition(parent.chatFeed.getDocument().getLength());
			//}
			else if(source.equals("connections")) {
				String action = (String) data.get("action");
				String player = (String) data.get("player");
				if(action.equals("disconnected")) {
					if(parent.defListModel.contains(player)) {
						parent.defListModel.removeElement(player);
					}

				} else {
					if(!parent.defListModel.contains(player)) {
						parent.defListModel.addElement(player);
					}
				}
				parent.chatFeed.append("\n[" + player + " " + action + "]");
				parent.chatFeed.setCaretPosition(parent.chatFeed.getDocument().getLength());
			}
			else if(source.equals("herochat")) {
				String channel = (String) data.get("channel");
				String player = (String) data.get("player");
				String message = (String) data.get("message");
				if(message != null) {
					if(parent.channelCheckBoxes.containsKey(channel)) {
						if(parent.channelCheckBoxes.get(channel).isSelected()) {
							parent.chatFeed.append("\n(" + channel + ") "+ stripColor(player) + ": " + stripColor(message));
							parent.chatFeed.setCaretPosition(parent.chatFeed.getDocument().getLength());
						}
					}
				}
			}
			else if(source.equals("irc")) {
				String channel = (String) data.get("channel");
				String player = (String) data.get("sender");
				String message = (String) data.get("message");
				if(message != null) {
					if(parent.channelCheckBoxes.containsKey(channel)) {
						if(parent.channelCheckBoxes.get(channel).isSelected()) {
							if(message.equals("connect") || message.equals("disconnect")) {
								message += "ed.";
								parent.chatFeed.append("\n(" + channel + ") (IRC) ["+ stripColor(player) + " " + stripColor(message) +"]");
								parent.chatFeed.setCaretPosition(parent.chatFeed.getDocument().getLength());
							} else {
								parent.chatFeed.append("\n(" + channel + ") (IRC) "+ stripColor(player) + ": " + stripColor(message));
								parent.chatFeed.setCaretPosition(parent.chatFeed.getDocument().getLength());
							}
						}
					}
				}
			}
			else if(source.equals("notifications")) {
				String type = (String) data.get("type");
				String player = (String) data.get("player");
				String message = (String) data.get("message");
				if(type.equals("helpme")) {
					JOptionPane test = new JOptionPane();
					test.setMessageType(JOptionPane.OK_OPTION);
					if(message.contains("Reason:")) {
						int i = message.indexOf("!");
						message = message.substring(0, i+1).trim() + "\n" + message.substring(i+1).trim();
					}
					test.setMessage(player + " " + message.trim());
					test.setVisible(true);
					JDialog dialog = test.createDialog("Notification");
					dialog.setLocationRelativeTo(null);
					dialog.setAlwaysOnTop(true);
					dialog.setVisible(true);
					parent.pp.playerList.setSelectedValue(player, true);
					parent.tabbedPane.setSelectedIndex(2);
					parent.pp.playerList.requestFocus();
				}
				if(type.equals("newplayer")) {
					JOptionPane newPlayer = new JOptionPane();
					newPlayer.setMessageType(JOptionPane.OK_OPTION);
					newPlayer.setMessage("New player join: \n" + player);
					newPlayer.setVisible(true);
					JDialog dialog = newPlayer.createDialog("Notification");
					dialog.setLocationRelativeTo(null);
					dialog.setAlwaysOnTop(true);
					dialog.setVisible(true);
					parent.pp.updatePlayerList();
					parent.pp.playerList.setSelectedValue(player, true);
					parent.tabbedPane.setSelectedIndex(2);
					parent.pp.playerList.requestFocus();
				}
			}
		}
	}

	public static String stripColor(final String input) {
		if (input == null) {
			return null;
		}
		return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
	}
	public static String getTime() {
		Calendar cal = Calendar.getInstance();
		Date dateO= cal.getTime();
		DateFormat df = new SimpleDateFormat("h:mm a");
		String date = df.format(dateO);
		return date;
	}
}
