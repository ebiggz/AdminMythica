package com.gmail.erikbigler.adminmythica.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ServerConnector extends Thread {

	BufferedReader in;
	PrintWriter out;

	public void run() {

		// Make connection and initialize streams
		String serverAddress = "mc.mythicacraft.com";
		Socket socket = null;
		try {
			socket = new Socket(serverAddress, 9001);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		while (true) {
			String line = null;
			try {
				line = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(line == null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		}
	}
}
