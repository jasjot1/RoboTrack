package com.mycompany.a4;

import com.codename1.util.MathUtil;

public class AttackPlayerStrategy implements IStrategy{
	private NonPlayerRobot npr;		//Non player robot that the strategy is being applied to
	private PlayerRobot playerRobot; //Player robot that is used to calculate new headings
	
	//Takes in NonPlayerRobot and GameWorld
	public AttackPlayerStrategy(NonPlayerRobot npr, GameWorld gw) {
		this.npr = npr;
		playerRobot = gw.getRobot();	//Use getRobot function from GameWorld to access player robot
	}
	
	public void apply() {
		//Get x and y of NPR
	    float nprX = npr.getX();
	    float nprY = npr.getY();
	    
	    //Get x and y of player robot
	    float playerX = playerRobot.getX();
	    float playerY = playerRobot.getY();

	    //Set new heading based on the formula for calculating new heading values for the NPR
	    //90 - ArcTan(Difference Y / Difference X)
	    npr.setHeading((int)(90 - Math.toDegrees(MathUtil.atan2(playerY - nprY, playerX - nprX))));
	}
}
