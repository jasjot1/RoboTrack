package com.mycompany.a4;

import com.codename1.ui.Command;
import com.codename1.ui.Dialog;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionEvent;

public class CollisionCommands extends Command{
	private GameWorld gw;	//Store instance of game world
	
	public CollisionCommands(String command, GameWorld gw) {
		super(command);
		this.gw  = gw;
	}
	
	public void actionPerformed(ActionEvent ev) {
		switch(getCommandName()) {
		
		case "Collide with Base":	//Base collision 
			Command commandConfirm = new Command("Confirm");			//Confirm collision
			TextField inputBase = new TextField();
			Command c = Dialog.show("Enter Base Number:", inputBase, commandConfirm);
			
			if (c == commandConfirm) { //Check if confirmed
				try {
				    int intValue = Integer.parseInt(inputBase.getText()); //Parse string input to integer
				    gw.collideBase(intValue);
				} catch (Exception e) {	//Invoked if input is not valid
				    System.out.println("Please enter an integer for input.");
				}
			}
			break;
		
		case "Collide drone": gw.collideDrone(); break; //Drone collision
		
		//case "Collide Energy Station": gw.collideEnergyStation(); break; //Energy station collision
		
		case "Collide NPR": gw.collideRobot(); break;	//Non player robot collision
		
			
		}
	}
}
