package com.gmail.erikbigler.adminmythica.threads;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.gmail.erikbigler.adminmythica.components.PlayersPanel;
import com.gmail.erikbigler.adminmythica.tools.URLEncoder;
import com.gmail.erikbigler.adminmythica.tools.Utils;


@SuppressWarnings("rawtypes")
public class PlayerInfoUpdater extends Thread {

	private String player;
	private PlayersPanel parent;
	private JLabel healthLabel;
	private JLabel statusLabel;
	private JLabel hungerLabel;
	private JLabel levelLabel;
	private JLabel balanceLabel;
	private JLabel locLabel;
	//private ImageComponent skinPreview;
	private JLabel skinLabel;
	private HashMap playerJSON;
	private JButton ban;

	public PlayerInfoUpdater(String player, PlayersPanel parent) {
		this.player = player;
		this.parent = parent;
	}

	public void run() {
		this.statusLabel = parent.currentStatus;
		this.healthLabel = parent.currentHealth;
		this.hungerLabel = parent.currentHunger;
		this.levelLabel = parent.currentLevel;
		this.balanceLabel = parent.currentBal;
		this.locLabel = parent.currentLoc;
		//this.skinPreview = parent.skinPreview;
		this.skinLabel = parent.picLabel;
		this.ban = parent.ban;
		playerJSON = getJSONHash(player);
		statusLabel.setText(getPlayerStatus());
		healthLabel.setText(getPlayerHealth() + "/20");
		hungerLabel.setText(getPlayerHunger() + "/20");
		levelLabel.setText(getPlayerLevel());
		balanceLabel.setText("$"+getPlayerBalance());
		locLabel.setText(getPlayerLocation());
		bannedButton();
		if(statusLabel.getText().equals("Offline")) {
			parent.setButtonsVisibility(false);
		} else {
			parent.setButtonsVisibility(true);
		}
		getPlayerSkin();
	}

	String getPlayerHealth() {
		String health = "";
		Double playerHealth = (Double) playerJSON.get("health");
		health = Integer.toString(playerHealth.intValue());
		return health;
	}

	void bannedButton() {
		boolean banned = (boolean) playerJSON.get("banned");
		if(banned) {
			ban.setText("Unban");
		} else {
			ban.setText("Ban");
		}
	}

	void getPlayerSkin() {
		BufferedImage fullSkin = null;
		try {
			fullSkin = ImageIO.read(new URL("http://s3.amazonaws.com/MinecraftSkins/" + parent.playerList.getSelectedValue() + ".png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		BufferedImage face = enlarge(fullSkin.getSubimage(8, 8, 8, 8), 13);
		skinLabel.setIcon(new ImageIcon(face));
	}
	public static BufferedImage enlarge(BufferedImage image, int n) {

		int w = n * image.getWidth();
		int h = n * image.getHeight();

		BufferedImage enlargedImage =
				new BufferedImage(w, h, image.getType());

		for (int y=0; y < h; ++y)
			for (int x=0; x < w; ++x)
				enlargedImage.setRGB(x, y, image.getRGB(x/n, y/n));

		return enlargedImage;
	}

	String getPlayerStatus() {
		String status = (String) playerJSON.get("ip");
		if(!status.equals("offline")) {
			status = status.replace("/", "");
			if(status.contains(":")) {
				int i = status.indexOf(":");
				status = status.substring(0, i);
			}
			status = "Online(" + status + ")";
		} else {
			status = "Offline";
		}
		return status;
	}

	String getPlayerHunger() {
		String hunger = "";
		Long playerHunger = (Long) playerJSON.get("foodLevel");
		hunger = Long.toString(playerHunger);
		return hunger;
	}

	String getPlayerLevel() {
		String level = "";
		long playerHunger = (long) playerJSON.get("level");
		level = Long.toString(playerHunger);
		return level;
	}

	String getPlayerLocation() {
		String location = "";
		HashMap locJSON = (HashMap) playerJSON.get("location");
		HashMap worldJSON = (HashMap) playerJSON.get("worldInfo");
		Double x = (Double) locJSON.get("x");
		Double y = (Double) locJSON.get("y");
		Double z = (Double) locJSON.get("z");
		String world = (String) worldJSON.get("name");
		switch(world) {
		case "The_Realm":
			world = "The Realm";
			break;
		case "The_Realm_nether":
			world = "Main Nether";
			break;
		case "The_Realm_the_end":
			world = "End";
			break;
		case "The_Temp":
			world = "Temp World";
			break;
		case "Temp_Nether":
			world = "Temp Nether";
			break;
		}
		location = Integer.toString(x.intValue()) + ", " + Integer.toString(y.intValue()) + ", " + Integer.toString(z.intValue()) + ", " + world;
		return location;
	}

	String getPlayerBalance() {
		String balance = requestDataFromServer("econ.getBalance", "\"" + player + "\"");
		System.out.println(balance);
		JSONParser parser = new JSONParser();
		JSONObject playerObj = null;
		try {
			playerObj = (JSONObject) parser.parse(balance);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Double playerHealth = (Double) playerObj.get("success");
		balance = Double.toString(playerHealth);
		return balance;
	}

	HashMap getJSONHash(String playername) {
		String playerJSON = requestDataFromServer("getPlayer", "\"" + playername + "\"");
		JSONParser parser = new JSONParser();
		JSONObject playerObj = null;
		try {
			playerObj = (JSONObject) parser.parse(playerJSON);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (HashMap) playerObj.get("success");
	}

	public static String requestDataFromServer(String methodName, String args) {
		String url = "http://mc.mythicacraft.com:20059/api/call?method=" + methodName;
		if(args != null) {
			args = "[" + args + "]";
			url += "&args=" + URLEncoder.encodeUTF8(args);
		} else {
			url += "&args=%5B%5D";
		}
		url += "&key=" + Utils.getKey(methodName);
		String jsonReply = "";
		try {
			URL u = new URL(url);
			URLConnection c = u.openConnection();
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							c.getInputStream()));
			jsonReply = in.readLine();
			in.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return jsonReply;
	}
}
