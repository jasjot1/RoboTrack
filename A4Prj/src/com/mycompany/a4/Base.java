package com.mycompany.a4;

import com.codename1.ui.Graphics;
import com.codename1.charts.models.Point;
import com.codename1.charts.util.ColorUtil;

public class Base extends Fixed{
	private final int sequenceNumber;	//Each base is numbered marker 
	
	public Base(int sequenceNumber, int size, int color, float x, float y) {
		super(size, color, x, y);
		this.sequenceNumber = sequenceNumber;
		
	}
	
	public int getSequenceNumber() {
		return sequenceNumber;
	}
	
	//Color of base not allowed to be changed
	public void setColor(int color) {}
	public void setColor(int red, int green, int blue) {}
	
	//toString method for Base
	public String toString() {
		String parentDesc = super.toString();	//Get parent description
		String desc = " seqNum=" + sequenceNumber;	//Base only adds sequence number to parent
		
		return "Base: " + parentDesc + desc;
	}
	
	//
	public void draw(Graphics g, Point pCmpRelPrnt) {
		g.setColor(getColor());
		
		int x = (int) (getX() + pCmpRelPrnt.getX());
		int y = (int) (getY() + pCmpRelPrnt.getY());
		
		int xPoints[] = {x-(getSize()/2), x, x + (getSize()/2)};
		int yPoints[] = {y + (getSize()/2), y+getSize(), y + (getSize()/2)};
		
		
		if(isSelected()) {
			g.drawPolygon(xPoints, yPoints, 3);
		}
		else	//Unfilled triangle if selected
			g.fillPolygon(xPoints, yPoints, 3);
		
		g.setColor(ColorUtil.BLACK); //Black text
		g.drawString(String.valueOf(sequenceNumber), (int)(getX()+ pCmpRelPrnt.getX()), (int)(getY()+ pCmpRelPrnt.getY())); //Display sequence number
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
		if (otherObject instanceof Robot) {
			Robot robo = (Robot)otherObject;
			int lastBase = robo.getLastBaseReached();
			
			//New base must be 1 larger than last base to set it
			if ((getSequenceNumber() - lastBase) == 1) {
				robo.setLastBaseReached(getSequenceNumber());
				System.out.println("Reached base " + getSequenceNumber());
			}
			
			else {
				System.out.println("Based already reached or order not correct.");
			}
			
			if (otherObject instanceof NonPlayerRobot) {
				NonPlayerRobot npr = (NonPlayerRobot) otherObject;
				//Set the strategy to make NPR go to next base
				if (npr.printStrategy() == "Base Strategy") {
					npr.setStrategy(new MoveToBaseStrategy(npr, gw));
				}
			}
			
		}
		gw.checkWin();
	}

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