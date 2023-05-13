package com.mycompany.a4;

import java.util.Random;

import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;
import com.codename1.ui.Transform.NotInvertibleException;
import com.codename1.charts.models.Point;
import com.codename1.charts.util.ColorUtil;


public class EnergyStation extends Fixed{
	private static Random rand = new Random();
	Point lowerLeftInLocalSpace;
	
	private int capacity;	//Stores the capacity of energy station
	
	//EnergyStation constructor takes in only the size
	public EnergyStation(int color, int mapWidth, int mapHeight) {
		//Parent constructor updated with random size, random location, and color chosen.
		super(40 + rand.nextInt(60),color, (rand.nextInt(mapWidth)), (rand.nextInt(mapHeight)));
		capacity = getSize();	//Capacity is the same as the random size
		
		lowerLeftInLocalSpace = new Point(getX() - getSize() / 2, getY() - getSize() / 2);
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
	    
	    if (isSelected()) {
	    	g.drawArc(x, y, getSize(), getSize(), 0, 360);
	    }
	    else {
	    	g.fillArc(x, y, getSize(), getSize(), 0, 360);
	    }
	    
	    //Text shows up wrong way, use this to fix it
	    gXform = original.copy();
	    gXform.translate(pCmpRelScrn.getX(), pCmpRelScrn.getY());
	    gXform.translate(getMyTranslation().getTranslateX(), getMyTranslation().getTranslateY());
	    gXform.scale(1, -1); // Reflect the text vertically
	    gXform.translate(-pCmpRelScrn.getX(), -pCmpRelScrn.getY());
	    g.setTransform(gXform);
	    
	    g.setColor(ColorUtil.BLACK); //Black text
	    g.drawString(String.valueOf(getCapacity()), x + getSize() / 4, y + getSize() / 4); //Display capacity of energy station

	    g.setTransform(original);
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

	public boolean contains(float x, float y, int absX, int absY) {
		boolean hit = false;
		
	    Transform localXform = Transform.makeIdentity();
	    localXform.translate(absX, absY);
	    localXform.translate(getMyTranslation().getTranslateX(), getMyTranslation().getTranslateY());
	    localXform.concatenate(getMyRotation());
	    localXform.scale(getMyScale().getScaleX(), getMyScale().getScaleY());
	    localXform.translate(-absX, -absY);

		try {
			
			float[] pts = { x, y };
			Transform inverseConcatLTs = Transform.makeIdentity();
			Transform xForm = Transform.makeTranslation(absX, absY);
			xForm.concatenate(localXform);
			xForm.translate(-absX, -absY);
			xForm.getInverse(inverseConcatLTs);
			inverseConcatLTs.transformPoint(pts, pts);
			
			int px = (int) pts[0]; 
			int py = (int) pts[1]; 
		    
			if ( (px >= -getSize()/2) && (px <= getSize()/2) && (py >= -getSize()/2) && (py <= getSize()/2))
				hit = true;
			
		} catch (NotInvertibleException e) {
			System.out.println("Non invertible xform!");
		}
		return hit;
	}

}
