package com.gmail.erikbigler.adminmythica.components;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;



@SuppressWarnings("serial")
public class PlayerListPopup extends JPopupMenu {
	public PlayerListPopup(JMenuItem playInfo){
		playInfo.setText("Show Player Info");
		add(playInfo);
	}
}
