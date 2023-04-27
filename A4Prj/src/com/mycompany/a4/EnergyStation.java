package com.mycompany.a4;

import java.util.Random;

import com.codename1.ui.Graphics;
import com.codename1.charts.models.Point;
import com.codename1.charts.util.ColorUtil;


public class EnergyStation extends Fixed{
	private static Random rand = new Random();
	
	private int capacity;	//Stores the capacity of energy station
	
	//EnergyStation constructor takes in only the size
	public EnergyStation(int color, int mapWidth, int mapHeight) {
		//Parent constructor updated with random size, random location, and color chosen.
		super(40 + rand.nextInt(60),color, (rand.nextInt(mapWidth)), (rand.nextInt(mapHeight)));
		capacity = getSize();	//Capacity is the same as the random size
	}

	//Getter for capacity
	public int getCapacity() {
		return capacity;
	}

	//Setter for capacity
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	//toString for EnergyStation
	public String toString() {
		String parentDesc = super.toString();  //Get parent description
		
		//Adding EnergyStation capacity to parent description
		String desc = " capacity=" + capacity;
		
		return "EnergyStation: " + parentDesc + desc;
	}
	
	public void draw(Graphics g, Point pCmpRelPrnt) {
		g.setColor(getColor());	//Set the color using what was created in Game world
		
		if(isSelected()) {
			g.drawArc((int)(getX()+ pCmpRelPrnt.getX()), (int)(getY()+ pCmpRelPrnt.getY()), getSize(), getSize(), 0, 360); //Drawing a filled circle
		}
		else {
			g.fillArc((int)(getX()+ pCmpRelPrnt.getX()), (int)(getY()+ pCmpRelPrnt.getY()), getSize(), getSize(), 0, 360); //Drawing a filled circle
		}
		
		g.setColor(ColorUtil.BLACK); //Black text
		g.drawString(String.valueOf(getCapacity()), (int)(getX()+ pCmpRelPrnt.getX()), (int)(getY()+ pCmpRelPrnt.getY())); //Display capacity of energy station
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
		if (otherObject instanceof Robot) { //Both NPR and player robot can collides with energy station
			//Check if capacity of current energy station isn't 0
			if (getCapacity() != 0) {
				
				//Increase robots energy by the capacity of energy station
				((Robot)otherObject).increaseEnergyLevel(10*getCapacity());
				
				//Reduce capacity to 0
				setCapacity(0);
				
				//Fade color of energy station
				setColor(ColorUtil.red(getColor())+150, ColorUtil.green(getColor()), ColorUtil.blue(getColor())+150);
				
				//Add new energy station (will be random location and size)
				gw.getCollection().add(new EnergyStation(ColorUtil.rgb(0, 180, 0), gw.getWidth(), gw.getHeight()));
				
				gw.energySoundPlay();
			}
		}
	}

	@Override
	public boolean contains(Point pPtrRelPrnt, Point pCmpRelPrnt) {
		int px = (int) pPtrRelPrnt.getX(); // pointer location relative to
		int py = (int) pPtrRelPrnt.getY(); // parent’s origin
		int xLoc = (int) (pCmpRelPrnt.getX()+ getX());// shape location relative
		int yLoc = (int) (pCmpRelPrnt.getY()+ getY());// to parent’s origin
		
		if ( (px >= xLoc) && (px <= xLoc+getSize()) && (py >= yLoc) && (py <= yLoc+getSize()))
			return true; 
		else 
			return false;
	}

}
