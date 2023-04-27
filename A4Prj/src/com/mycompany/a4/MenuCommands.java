package com.mycompany.a4;

import com.codename1.ui.CheckBox;
import com.codename1.ui.Command;
import com.codename1.ui.Dialog;
import com.codename1.ui.events.ActionEvent;

public class MenuCommands extends Command{
	private GameWorld gw; //Store instance of game world
	
	public MenuCommands(String command, GameWorld gw) {
		super(command); //Call parent constructor to set command
		this.gw  = gw;
	}
	
	public void actionPerformed(ActionEvent ev) {
		switch(getCommandName()) {
		case "About":	//About command
			String text = "Jasjot Singh \nCSC 133 Section 5";
			Dialog.show("About", text, "OK", null);
			break;
		
		case "Exit":	//Exit command 
			Boolean exitD = Dialog.show("Confirm exit?", "Are you sure you want to exit?", "OK", "Cancel");
			if (exitD) //Checks if OK or cancel
				gw.exit(); 
			break;
		
		case "Help": 	//Help command
			String helpText = "A: Accelerate \n"
					+ "B: Brake \n"
					+ "L: Left Turn \n"
					+ "R: Right Turn \n"
					+ "E: Collide with Energy Station \n"
					+ "G: Collide with Drone \n"
					+ "T: Tick";

			Dialog.show("Help", helpText, "OK", null);
			break;
		
		case "Sound":	//Sound CheckBox command
	        CheckBox checkbar = (CheckBox) ev.getComponent();
	        if (checkbar.isSelected()) { //If selected turn on sound
	        	gw.setSound(true);
	        }
	        else {
	        	gw.setSound(false);
	        }
		    break;
		}
	}
}
