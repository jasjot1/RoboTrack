package com.mycompany.a4;

import com.codename1.ui.Command;
import com.codename1.ui.events.ActionEvent;

public class GameModeCommands extends Command{
	private Game game; //Store instance of game
	private GameWorld gw;
	
	public GameModeCommands(String command, Game game, GameWorld gw) {
		super(command); //Call parent constructor to set command
		this.game = game;
		this.gw = gw;
	}
	
	public void actionPerformed(ActionEvent ev) {
		switch(getCommandName()) {
		case "Pause": game.changePlayPause(); break;
		case "Play": game.changePlayPause(); break;
		case "Position": gw.setPositionPressed(true);
		}
	}

}

