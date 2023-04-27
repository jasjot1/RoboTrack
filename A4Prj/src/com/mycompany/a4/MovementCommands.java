package com.mycompany.a4;

import com.codename1.ui.Command;
import com.codename1.ui.events.ActionEvent;

public class MovementCommands extends Command{

	private GameWorld gw; //Instance of GameWorld
	
	public MovementCommands(String command, GameWorld gw) {
		super(command); //Call parent constructor to set command
		this.gw  = gw;
	}
	
	public void actionPerformed(ActionEvent ev) {
		switch(getCommandName()) {
		
		case "Accelerate": gw.accelerate(); break;
		
		case "Left": gw.steerLeft(); break;
		
		case "Right": gw.steerRight(); break;
		
		case "Brake": gw.brake(); break;
		
		case "Change Strategies": gw.changeStrategies(); break;
		
		//case "Clock Tick": gw.clockTick(); break;
		
		}
	}

}
