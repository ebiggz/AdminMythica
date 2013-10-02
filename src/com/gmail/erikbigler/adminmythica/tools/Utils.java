package com.gmail.erikbigler.adminmythica.tools;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class Utils {

	public static final char COLOR_CHAR = '\u00A7';
	private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-OR]");

	public static String getKey(String methodName) {
		String key = sha256("mythicacraft" + methodName + "ballsack69");
		return key;
	}


	public static String sha256(String base) {
		String key = "";
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(base.getBytes());

			byte byteData[] = md.digest();

			//convert the byte to hex format method 1
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			key = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return key;
	}

	public static String urlEncoder(String string) {
		URI uri = null;
		try {
			uri = new URI(
					null,
					string,
					null);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return uri.toASCIIString();
	}

	public static void sendMethodToServer(String methodName, String args) {
		String url = "http://mc.mythicacraft.com:20059/api/call?method=" + methodName;
		if(args != null) {
			args = "[" + args + "]";
			url += "&args=" + URLEncoder.encodeUTF8(args);
		} else {
			url += "&args=%5B%5D";
		}
		url += "&key=" + Utils.getKey(methodName);
		try {
			URL u = new URL(url);
			URLConnection c = u.openConnection();
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							c.getInputStream()));
			in.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
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

	public static String openDialog(JFrame frame, String dialogTitle, String prompt, String fieldText) {
		String input = (String)JOptionPane.showInputDialog(
				frame,
				prompt,
				dialogTitle,
				JOptionPane.PLAIN_MESSAGE,
				null,
				null,
				fieldText);

		//If a string was returned, say so.
		if ((input != null) && (input.length() > 0)) {
			return input;
		}
		return null;
	}
	public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
		char[] b = textToTranslate.toCharArray();
		for (int i = 0; i < b.length - 1; i++) {
			if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i+1]) > -1) {
				b[i] = COLOR_CHAR;
				b[i+1] = Character.toLowerCase(b[i+1]);
			}
		}
		return new String(b);
	}
	public static String stripColor(final String input) {
		if (input == null) {
			return null;
		}
		return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<String> getOnlinePlayers() {
		String onlinePlayersData = requestDataFromServer("getPlayerNames", null);
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
}


