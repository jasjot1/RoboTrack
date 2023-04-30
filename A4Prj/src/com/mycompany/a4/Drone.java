package com.mycompany.a4;
import java.util.Random;

import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;
import com.codename1.charts.models.Point;
import com.codename1.charts.util.ColorUtil;

public class Drone extends Moveable{
	
	private static Random rand = new Random();
	private int mapWidth;
	private int mapHeight;
	
	private Point top, bottomLeft, bottomRight;
	
	//Takes in color of drone, along with map dimensions to randomly place drone somewhere in the map
	public Drone(int color, int mapWidth, int mapHeight) {
		//Set drone to random location, random size, and color that is chosen. 
		super(35 + rand.nextInt(60),color, (rand.nextInt(mapWidth)), (rand.nextInt(mapHeight)));
		
		setHeading(rand.nextInt(360));	//Drone has random heading between 0 and 359
		
		setSpeed(30 + rand.nextInt(46));	//Speed is random from 
		
		this.mapHeight = mapHeight;
		this.mapWidth = mapWidth;
		
	}

	//Color of drone not allowed to be changed
	//These methods are overwritten (can't do anything)
	public void setColor(int color) {}
	public void setColor(int red, int green, int blue) {}
	
	//toString for Drone
	public String toString() {
		String parentDesc = super.toString();	//Get parent description
		
		//Adding new Drone information (heading and speed) to parent
		String desc = " heading=" + getHeading() + " speed=" + getSpeed();
		
		return "Drone: " + parentDesc + desc;
	}
	
	//Update the drone's heading randomly
	public void changeHeading() {
		Random rand = new Random();
		
		//Add small random values to heading so robot isn't in straight line 
		setHeading(getHeading() + (rand.nextInt(21) - 10));
		
		//If drone goes out of bounds
		if ((getX() <= 0 || getX() >= mapWidth) || (getY() <= 0 || getY() >= mapHeight)) {
			//setHeading(rand.nextInt(360));	//Random heading
			setHeading(getHeading() + 180);		//Turn robot in opposite direction
		}
		
	}
	
	//Draw drone
	public void draw(Graphics g, Point pCmpRelPrnt, Point pCmpRelScrn) {
		g.setColor(getColor());
		
		Transform gXform = Transform.makeIdentity();
	    g.getTransform(gXform);
		Transform original = gXform.copy();
		
		gXform.translate(pCmpRelScrn.getX(),pCmpRelScrn.getY());
		gXform.translate(getMyTranslation().getTranslateX(), getMyTranslation().getTranslateY());
		gXform.concatenate(getMyRotation());
		gXform.scale(getMyScale().getScaleX(), getMyScale().getScaleY());
		gXform.translate(-pCmpRelScrn.getX(),-pCmpRelScrn.getY());
		g.setTransform(gXform);
		
		top = new Point(getX(), getY() + getSize()/2);
		bottomLeft = new Point(getX()-getSize()/2, getY()-getSize()/2);
		bottomRight = new Point(getX()+getSize()/2, getY()-getSize()/2);
		
	    g.drawLine((int) (pCmpRelPrnt.getX() + top.getX()), (int) (pCmpRelPrnt.getY() + top.getY()),
	    		(int) (pCmpRelPrnt.getX() + bottomLeft.getX()), 
	    		(int) (pCmpRelPrnt.getY() + bottomLeft.getY()));
	    
	    g.drawLine((int) (pCmpRelPrnt.getX() + bottomLeft.getX()), 
	    		(int) (pCmpRelPrnt.getY() + bottomLeft.getY()),
	               (int) (pCmpRelPrnt.getX() + bottomRight.getX()), 
	               (int) (pCmpRelPrnt.getY() + bottomRight.getY()));
	    
	    g.drawLine((int) (pCmpRelPrnt.getX() + bottomRight.getX()), 
	    		(int) (pCmpRelPrnt.getY() + bottomRight.getY()),
	               (int) (pCmpRelPrnt.getX() + top.getX()), 
	               (int) (pCmpRelPrnt.getY() + top.getY()));
	    
	    g.setTransform(original); //Restore saved graphics transform
	}

	@Override
	public boolean collidesWith(GameObject otherObject) {
		//Bounding boxes for current object and other object
		int l1 = (int) (getX() - (getSize()/2)); //Left x for current
		int r1 = (int) (getX() + (getSize()/2)); //Right x for current
		int t1 = (int) (getY() - (getSize()/2)); //Top y for current
		int b1 = (int) (getY() + (getSize()/2)); //Bottom y for current
		
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
		if (otherObject instanceof NonPlayerRobot) { //Collides with Non player robot
			System.out.println("NPR Collison with drone occured!");
			NonPlayerRobot npr = (NonPlayerRobot)otherObject;
			npr.increaseDamageLevel(5);	//Increase damage of npr
			
			//Fade color of robot
			npr.setColor(ColorUtil.red(getColor()), ColorUtil.green(getColor())+40, ColorUtil.blue(getColor())+40);
		}
		
		if (otherObject instanceof PlayerRobot) { //Drone collides with player robot
			System.out.println("Player Collison with drone occured!");
			PlayerRobot robo = (PlayerRobot)otherObject;
			
			if(!robo.isMaxDamage()) {
				robo.increaseDamageLevel(5);	//Increase damage of player robot
			}
			
			//Fade color of robot
			setColor(ColorUtil.red(getColor())+40, ColorUtil.green(getColor())+40, ColorUtil.blue(getColor()));

			gw.checkGameOver(robo);
		}
		
		//Play drone collision sound
		gw.droneCollideSoundPlay();

		
	}
}
