package com.mycompany.a4;

import com.codename1.util.MathUtil;

public class MoveToBaseStrategy implements IStrategy{
	private NonPlayerRobot npr;	//Non Player Robot that the strategy is being applied to
	private Base base;			//Base instance used for calculations
	
	//Constructor takes in NPR and GameWorld
	public MoveToBaseStrategy(NonPlayerRobot nonPlayer, GameWorld gw) {
		npr = nonPlayer;
		base = gw.getBase(npr.getLastBaseReached()+1);	//Set the base to next sequence number of base
	}
	
	public void apply() {
		//Get x and y of NPR
	    float nprX = npr.getX();
	    float nprY = npr.getY();
	    
	    //Get x and y of base
	    float baseX = base.getX();
	    float baseY = base.getY();

	    //Set new heading based on formula for calculating new heading values for the NPR
	    //90 - ArcTan(Difference Y / Difference X)
	    npr.setHeading((int)(90 - Math.toDegrees(MathUtil.atan2(baseY - nprY, baseX - nprX))));
	}
}
