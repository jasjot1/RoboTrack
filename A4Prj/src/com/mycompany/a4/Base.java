package com.mycompany.a4;

import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;
import com.codename1.ui.Transform.NotInvertibleException;
import com.codename1.charts.models.Point;
import com.codename1.charts.util.ColorUtil;

public class Base extends Fixed {
	private final int sequenceNumber; // Each base is numbered marker
	private Point top, bottomLeft, bottomRight;

	public Base(int sequenceNumber, int size, int color, float x, float y) {
		super(size, color, x, y);
		this.sequenceNumber = sequenceNumber;
		top = new Point(0, getSize() / 2);
		bottomLeft = new Point(-getSize() / 2, -getSize() / 2);
		bottomRight = new Point(getSize() / 2, -getSize() / 2);
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	// Color of base not allowed to be changed
	public void setColor(int color) {
	}

	public void setColor(int red, int green, int blue) {
	}

	// toString method for Base
	public String toString() {
		String parentDesc = super.toString(); // Get parent description
		String desc = " seqNum=" + sequenceNumber; // Base only adds sequence number to parent

		return "Base: " + parentDesc + desc;
	}

	// Draw base
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

		/*
		 * top = new Point(0, getSize()/2); bottomLeft = new Point(-getSize()/2,
		 * -getSize()/2); bottomRight = new Point(getSize()/2, -getSize()/2);
		 */

		int[] xPoints = { (int) (pCmpRelPrnt.getX() + top.getX()), (int) (pCmpRelPrnt.getX() + bottomLeft.getX()),
				(int) (pCmpRelPrnt.getX() + bottomRight.getX()) };
		int[] yPoints = { (int) (pCmpRelPrnt.getY() + top.getY()), (int) (pCmpRelPrnt.getY() + bottomLeft.getY()),
				(int) (pCmpRelPrnt.getY() + bottomRight.getY()) };

		if (isSelected()) {
			g.drawPolygon(xPoints, yPoints, 3);
		} else // Unfilled triangle if selected
			g.fillPolygon(xPoints, yPoints, 3);

		g.setColor(ColorUtil.BLACK); // Black text
		g.drawString(String.valueOf(sequenceNumber), (int) (pCmpRelPrnt.getX()), (int) (pCmpRelPrnt.getY())); // Display
																												// sequence
																												// number

		g.setTransform(original); // Restore saved graphics transform
	}

	@Override
	public boolean collidesWith(GameObject otherObject) {
		// Bounding boxes for current object and other object
		int l1 = (int) (getX() - (getSize() / 2)); // Left x for current
		int r1 = (int) (getX() + (getSize() / 2)); // Right x for current
		int t1 = (int) (getY() - (getSize() / 2)); // Top y for current
		int b1 = (int) (getY() + (getSize() / 2)); // Bottom y for current

		int l2 = (int) (otherObject.getX() - (otherObject.getSize() / 2)); // Left x for other
		int r2 = (int) (otherObject.getX() + (otherObject.getSize() / 2)); // Right x for other
		int t2 = (int) (otherObject.getY() - (otherObject.getSize() / 2)); // Top y for other
		int b2 = (int) (otherObject.getY() + (otherObject.getSize() / 2)); // Bottom y for other

		if ((r1 < l2) || (r2 < l1) || (t2 > b1) || (t1 > b2))
			return false;
		else
			return true;
	}

	@Override
	public void handleCollision(GameObject otherObject, GameWorld gw) {
		if (otherObject instanceof Robot) {
			Robot robo = (Robot) otherObject;
			int lastBase = robo.getLastBaseReached();

			// New base must be 1 larger than last base to set it
			if ((getSequenceNumber() - lastBase) == 1) {
				robo.setLastBaseReached(getSequenceNumber());
				System.out.println("Reached base " + getSequenceNumber());
			}

			else {
				System.out.println("Based already reached or order not correct.");
			}

			if (otherObject instanceof NonPlayerRobot) {
				NonPlayerRobot npr = (NonPlayerRobot) otherObject;
				// Set the strategy to make NPR go to next base
				if (npr.printStrategy() == "Base Strategy") {
					npr.setStrategy(new MoveToBaseStrategy(npr, gw));
				}
			}

		}
		gw.checkWin();
	}

	public boolean contains(float[] fPtr) {

		Point lowerLeftInLocalSpace = new Point(getX() - getSize() / 2, getY() - getSize() / 2); // corresponds to upper
																									// left corner on
																									// screen

		Transform concatLTs = Transform.makeIdentity();
		concatLTs.translate(getMyTranslation().getTranslateX(), getMyTranslation().getTranslateY());
		concatLTs.concatenate(getMyRotation());
		concatLTs.scale(getMyScale().getScaleX(), getMyScale().getScaleY());
		Transform inverseConcatLTs = Transform.makeIdentity();
		try {
			concatLTs.getInverse(inverseConcatLTs);
		} catch (NotInvertibleException e) {
			System.out.println("Non invertible xform!");
		}
		// fPtr is in the local space of HierObj, calculate the corresponding point in
		// the local space of Square
		inverseConcatLTs.transformPoint(fPtr, fPtr);

		int px = (int) fPtr[0]; // pointer location relative to
		int py = (int) fPtr[1]; // local origin
		int xLoc = (int) lowerLeftInLocalSpace.getX(); // square lower left corner
		int yLoc = (int) lowerLeftInLocalSpace.getY(); // location relative to local origin
		if ((px >= xLoc) && (px <= xLoc + getSize()) && (py >= yLoc) && (py <= yLoc + getSize()))
			return true;
		else
			return false;
	}
}
