package com.mycompany.a4;

import com.codename1.ui.Graphics;
import com.codename1.charts.models.Point;
import com.codename1.charts.util.ColorUtil;

public class PlayerRobot extends Robot{

	private static PlayerRobot robo; //Stores the unique instance of Robot
	
	//Singleton pattern for playerRobot (private constructor)
	private PlayerRobot(int size, int color, float x, float y) {
		super(size, color, x, y);
		
		//Initial values for robot
		setSteeringDirection(0);
		setMaximumSpeed(50);
		setEnergyLevel(4000);
		setEnergyConsumptionRate(1);
		setDamageLevel(0);
		setLastBaseReached(1);
		setSpeed(35);
		setMaxDamageLevel(25);
	}
	
    
    //This method is used to access and store Player Robot instance in GameWorld
    public static PlayerRobot getRobot(int size, int color, float x, float y) {
        if (robo == null) {	//If PlayerRobot hasn't been created then create
            robo = new PlayerRobot(size, color, x, y);
        }
        return robo;			//Else return robot instance
    }
    
	//Resets robot after new life
	public void newLife(float x, float y) {
		//Default values for robot
		setSteeringDirection(0);
		setMaximumSpeed(50);
		setEnergyLevel(4000);
		setEnergyConsumptionRate(1);
		setDamageLevel(0);
		setLastBaseReached(getLastBaseReached());
		setHeading(0);
		setSpeed(35);
		setLocation(x,y);
		
		//Set color back to red
		setColor(0, 0, 255);
	}
	
    //toString method for PlayerRobot shows heading, speed, max speed, steering direction, energy level, and damage level
	public String toString() {
		String parentDesc = super.toString();	//Get parent description
		
		//Adding new robot stuff
		String desc = " heading=" + getHeading() + " speed=" + getSpeed() + 
				" maxSpeed=" + getMaximumSpeed() + " steeringDirection=" + getSteeringDirection()
				+ " energyLevel=" + getEnergyLevel() + " damageLevel=" + getDamageLevel();
		
		
		return "PlayerRobot: " + parentDesc + desc;
	}

	public void draw(Graphics g, Point pCmpRelPrnt) {
		g.setColor(getColor());
		g.fillRect((int)(getX() + pCmpRelPrnt.getX()) - getSize()/2, (int)(getY() + pCmpRelPrnt.getY()) - getSize()/2, getSize(), getSize());
	}


	@Override
	public boolean collidesWith(GameObject otherObject) {
		
		//Bounding boxes for current object and other object
		int l1 = (int) (getX() - (getSize()/2)); //Left x for Player Robot
		int r1 = (int) (getX() + (getSize()/2)); //Right x for player
		int t1 = (int) (getY() - (getSize()/2)); //Top y for player
		int b1 = (int) (getY() + (getSize()/2)); //Bottom y for player
		
		int l2 = (int) (otherObject.getX() - (otherObject.getSize()/2)); //Left x for other
		int r2 = (int) (otherObject.getX() + (otherObject.getSize()/2)); //Right x for other
		int t2 = (int) (otherObject.getY() - (otherObject.getSize()/2)); //Top y for other
		int b2 = (int) (otherObject.getY() + (otherObject.getSize()/2)); //Bottom y for other

		if ( (r1 < l2) || (r2 < l1) || (t2 > b1) || (t1 > b2))
			return false;
		else
			return true;
	}


	@Override
	public void handleCollision(GameObject otherObject, GameWorld gw) {
		//Player collides with non player robot
		if (otherObject instanceof NonPlayerRobot) {
			if(!isMaxDamage()) {
				((NonPlayerRobot)otherObject).increaseDamageLevel(5); //Increase damage of npr
				
				increaseDamageLevel(5);	//Increase damage of player
				
				System.out.println("Player collides with npr");
				
				//Fade color of robot
				setColor(ColorUtil.red(getColor())+40, ColorUtil.green(getColor())+40, ColorUtil.blue(getColor()));
				
				gw.robotCollideSoundPlay();
			}
		}
		
		//Player collides with energy station
		if (otherObject instanceof EnergyStation) {
			//Call collide energy station method created in game world
			gw.collideEnergyStation((EnergyStation) otherObject);
			gw.energySoundPlay();
		}
		
		//Player collides with Drone
		if (otherObject instanceof Drone) {
			gw.collideDrone();
			gw.droneCollideSoundPlay();
		}
		
		//Player collides with base
		if (otherObject instanceof Base) {
			int lastBase = getLastBaseReached();
			Base currentBase = (Base)otherObject;
			
			//New base must be 1 larger than last base to set it
			if ((currentBase.getSequenceNumber() - lastBase) == 1) {
				setLastBaseReached(currentBase.getSequenceNumber());
				System.out.println("Reached base " + currentBase.getSequenceNumber());
			}
			
			else {
				System.out.println("Based already reached or order not correct.");
			}
		}
		
		gw.checkGameOver(this);
		gw.checkWin();
	}
}
