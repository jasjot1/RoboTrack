package com.mycompany.a4;

import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;
import com.codename1.charts.models.Point;
import com.codename1.charts.util.ColorUtil;

public class PlayerRobot extends Robot{

	private static PlayerRobot robo; //Stores the unique instance of Robot
	private int armAngle;
	
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

	    int x = (int) (pCmpRelPrnt.getX() - getSize() / 2);
	    int y = (int) (pCmpRelPrnt.getY() - getSize() / 2);

	    g.fillRect(x, y, getSize(), getSize());
	    
	    drawArm(g, x, y);
	    drawTire(g, x, y);

	    g.setTransform(original); // Restore saved graphics transform
	}

	

	private void drawArm(Graphics g, int x, int y) {
	    int armWidth = getSize() / 2;
	    int armHeight = getSize() / 2;

	    // Calculate the center points for the arms
	    int centerXLeft = x;
	    int centerYLeft = y + getSize() / 2;
	    int centerXRight = x + getSize();
	    int centerYRight = y + getSize() / 2;

	    // Save the current transform
	    Transform originalTransform = Transform.makeIdentity();
	    g.getTransform(originalTransform);

	    // Apply a rotation transform to the left arm
	    Transform leftArmRotation = Transform.makeIdentity();
	    leftArmRotation.translate(centerXLeft, centerYLeft);
	    leftArmRotation.rotate((float) Math.toRadians(armAngle), 0, 0);
	    leftArmRotation.translate(-centerXLeft, -centerYLeft);
	    g.transform(leftArmRotation);

	    // Draw the left arm
	    int[] xPointsLeft = {centerXLeft, centerXLeft - armWidth, centerXLeft};
	    int[] yPointsLeft = {y, y + armHeight, y + getSize()};
	    g.fillPolygon(xPointsLeft, yPointsLeft, 3);

	    // Restore the original transform
	    g.setTransform(originalTransform);

	    // Apply a rotation transform to the right arm
	    Transform rightArmRotation = Transform.makeIdentity();
	    rightArmRotation.translate(centerXRight, centerYRight);
	    rightArmRotation.rotate((float) Math.toRadians(-armAngle), 0, 0);
	    rightArmRotation.translate(-centerXRight, -centerYRight);
	    g.transform(rightArmRotation);

	    // Draw the right arm
	    int[] xPointsRight = {centerXRight, centerXRight + armWidth, centerXRight};
	    int[] yPointsRight = {y, y + armHeight, y + getSize()};
	    g.fillPolygon(xPointsRight, yPointsRight, 3);

	    // Restore the original transform
	    g.setTransform(originalTransform);
	}

    
    private void drawTire(Graphics g, int x, int y) {
        int radius = getSize() / 4; // Calculate radius for the tire (small black circles)
        int offsetX = getSize() / 4; // Offset for tire position along the x-axis
        int offsetY = -radius; // Offset for tire position along the y-axis

        g.setColor(ColorUtil.BLACK); // Set the color to black for the tires

        // Draw the left tire
        g.fillArc(x + offsetX - radius, y + offsetY - radius, 2 * radius, 2 * radius, 0, 360);

        // Draw the right tire
        g.fillArc(x + getSize() - offsetX - radius, y + offsetY - radius, 2 * radius, 2 * radius, 0, 360);
    }

    
    //Override move method from in order to apply dynamic transformation  
    @Override
	public void move(int timerRate, int mapWidth, int mapHeight) {
		double deltaX, deltaY; //Stores calculated values of x/y

		//Calculate deltaX/Y with angle converted to radians
		deltaX = (Math.cos(Math.toRadians(90 - getHeading())) * getSpeed() * timerRate) / 1000.0;
	    deltaY = (Math.sin(Math.toRadians(90 - getHeading())) * getSpeed() * timerRate) / 1000.0;

		
		
		//Update location by adding deltaX/deltaY to old X/Y
		float newX = (float) (Math.round((this.getX() + deltaX) * 10.0) / 10.0);
		float newY = (float) (Math.round((this.getY() + deltaY) * 10.0) / 10.0);
		
		//Boundary checking if object moves out of the screen
		
		if (newX < 0) {
			newX = 0;
		}
		if (newY < 0) {
			newY = 0;
		}
		if (newX > mapWidth) {
			newX = mapWidth;
		}
		if (newY > mapHeight) {
			newY = mapHeight;
		}
		
		this.translate(newX - this.getX(), newY-this.getY());
		armAngle = (armAngle + 5) % 360;
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
