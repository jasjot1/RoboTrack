package com.mycompany.a4;

import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;
import com.codename1.charts.models.Point;
import com.codename1.charts.util.ColorUtil;

public class NonPlayerRobot extends Robot{

	private IStrategy curStrategy;
	
	public NonPlayerRobot(int size, int color, float x, float y) {
		super(size, color, x, y);
		
		setSteeringDirection(0);
		setMaximumSpeed(25);
		setEnergyLevel(1000);
		setEnergyConsumptionRate(1);
		setDamageLevel(0);
		setLastBaseReached(1);
		setSpeed(25);
		setMaxDamageLevel(70);  

	}
	
	public void setStrategy(IStrategy s) {
		curStrategy = s;
	}
	
	public void invokeStrategy() {
		curStrategy.apply();
	}
	
	//Returns string of what the current strategy is
	public String printStrategy() {
		if (curStrategy instanceof MoveToBaseStrategy)
			return "Base Strategy";
		
		if (curStrategy instanceof AttackPlayerStrategy)
			return "Attack Strategy";
		
		else //Default
			return "Strategy not set";
	}
	
	//toString for NPR
	public String toString() {
		String parentDesc = super.toString();	//Get parent description
		
		String newDesc = " heading=" + getHeading() + " speed=" + getSpeed() + 
				" maxSpeed=" + getMaximumSpeed() + " steeringDirection=" + getSteeringDirection()
				+ " energyLevel=" + getEnergyLevel() + " damageLevel=" + getDamageLevel() 
				+ " Current Strategy: " + printStrategy();
		
		return "NonPlayerRobot:" + parentDesc + newDesc;
	}
	
	public void draw(Graphics g, Point pCmpRelPrnt, Point pCmpRelScrn) {
	    g.setColor(getColor());

		Transform gXform = Transform.makeIdentity();
	    g.getTransform(gXform);
		Transform original = gXform.copy();
	    gXform.translate(pCmpRelScrn.getX(), pCmpRelScrn.getY());
	    gXform.translate(getMyTranslation().getTranslateX(), getMyTranslation().getTranslateY());
	    gXform.concatenate(getMyRotation());
	    gXform.scale(getMyScale().getScaleX(), getMyScale().getScaleY());
	    gXform.translate(-pCmpRelScrn.getX(), -pCmpRelScrn.getY());
	    g.setTransform(gXform);

	    int x = (int) (pCmpRelPrnt.getX() + getX() - getSize() / 2);
	    int y = (int) (pCmpRelPrnt.getY() + getY() - getSize() / 2);

	    g.drawRect(x, y, getSize(), getSize());

	    g.setTransform(original); // Restore saved graphics transform
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
				
				increaseDamageLevel(5);	//Increase damage of current npr
				
				//Fade color of robot
				setColor(ColorUtil.red(getColor()), ColorUtil.green(getColor())+40, ColorUtil.blue(getColor())+40);
				
				System.out.println("NPR collides with another NPR");
				
				gw.robotCollideSoundPlay();
			}
		}
		
		//NPR collides with energy station
		if (otherObject instanceof EnergyStation) {
			EnergyStation station = (EnergyStation) otherObject;
			
			//Check if capacity of current energy station isn't 0
			if (station.getCapacity() != 0) {
				//Increase robots energy by the capacity of energy station
				increaseEnergyLevel(station.getCapacity());
				
				//Reduce capacity to 0
				station.setCapacity(0);
				
				//Fade color of energy station
				station.setColor(ColorUtil.red(station.getColor())+150, ColorUtil.green(station.getColor()), ColorUtil.blue(station.getColor())+150);
				
				//Add new energy station (will be random location and size)
				gw.getCollection().add(new EnergyStation(ColorUtil.rgb(0, 180, 0), gw.getWidth(), gw.getHeight()));
				gw.energySoundPlay();
			}
		}
		
		//NPR collides with Drone
		if (otherObject instanceof Drone) {
			increaseDamageLevel(5);	//Increase damage of current npr
			
			//Fade color of robot
			setColor(ColorUtil.red(getColor()), ColorUtil.green(getColor())+40, ColorUtil.blue(getColor())+40);
			gw.droneCollideSoundPlay();
			
		}
		
		//NPR collides with base
		if (otherObject instanceof Base) {
			int lastBase = getLastBaseReached();
			Base currentBase = (Base)otherObject;
			
			//New base must be 1 larger than last base to set it
			if ((currentBase.getSequenceNumber() - lastBase) == 1) {
				setLastBaseReached(currentBase.getSequenceNumber());
				System.out.println("NPR Reached base " + currentBase.getSequenceNumber());
			}
			
			else {
				System.out.println("Based already reached or order not correct.");
			}
			
			//Set the strategy to make NPR go to next base
			if (curStrategy instanceof MoveToBaseStrategy) {
				setStrategy(new MoveToBaseStrategy(this, gw));
			}
		}
		
		gw.checkWin();
	}

}
