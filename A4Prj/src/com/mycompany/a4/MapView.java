package com.mycompany.a4;

import java.util.Observable;
import java.util.Observer;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Container;
import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;
import com.codename1.ui.Transform.NotInvertibleException;
import com.codename1.ui.geom.Dimension;
import com.codename1.charts.models.Point;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.plaf.Border;

public class MapView extends Container implements Observer{
	
	GameWorld gw; //Store GameWorld instance
	Transform worldToND, ndToDisplay, theVTM;
	private float winLeft, winBottom, winRight, winTop;
	private float winWidth,winHeight;
	
	public MapView(GameWorld gw) {
		this.gw = gw;	//Constructor takes in instance of game world and assigns it
		
		//Creating new border layout
		setLayout(new BorderLayout());
		
		//Red border
		getAllStyles().setBorder(Border.createLineBorder(3, ColorUtil.rgb(255, 0, 0)));
		
		//Lower left corner
		winLeft = 0;
		winBottom = 0;
		
		//Temp values for upper right corner that will be changed after calling show in Game
		winRight = 500;
		winTop = 500;
		
		
		theVTM = Transform.makeIdentity();
	}
	
	//Initialize upper right corner values only after calling show()
	public void updateBoundaries(int width, int height) {
		this.winTop = height/2; //add divide by 2
		this.winRight = width/2;
		
	    winWidth = winRight - winLeft;
	    winHeight = winTop - winBottom;
		
	    System.out.println(winWidth+", "+winHeight);
		repaint();
	}
	
	
	@Override
	public void update(Observable observable, Object data) {
		gw = (GameWorld) data;
	
		//gw.printMap(); //print map to console when anything updates
		repaint();
	}
	
	public void paint(Graphics g) {
	    super.paint(g);
	    GameObjectCollection collection = gw.getCollection(); // Get the game object collection

	    worldToND = buildWorldToNDXform(winWidth, winHeight, winLeft, winBottom);
	    ndToDisplay = buildNDToDisplayXform(getWidth(), getHeight());
	    theVTM = ndToDisplay.copy();
	    theVTM.concatenate(worldToND);

	    Transform gXform = Transform.makeIdentity();
	    g.getTransform(gXform);
	    gXform.translate(getAbsoluteX(), getAbsoluteY());
	    gXform.concatenate(theVTM);
	    gXform.translate(-getAbsoluteX(), -getAbsoluteY());
	    g.setTransform(gXform);

	    Point pCmpRelPrnt = new Point(getX(), getY());
	    Point pCmpRelScrn = new Point(getAbsoluteX(), getAbsoluteY());

	    IIterator elements = collection.getIterator();
	    while (elements.hasNext()) {
	        GameObject obj = (GameObject) elements.getNext();
	        obj.draw(g, pCmpRelPrnt, pCmpRelScrn);
	    }

	    g.resetAffine();
	}

	private Transform buildWorldToNDXform(float winWidth, float winHeight, float winLeft, float winBottom) {
	    Transform tmpXform = Transform.makeIdentity();
	    tmpXform.scale(1 / winWidth, 1 / winHeight);
	    tmpXform.translate(-winLeft, -winBottom);
	    return tmpXform;
	}

	private Transform buildNDToDisplayXform(float displayWidth, float displayHeight) {
	    Transform tmpXform = Transform.makeIdentity();
	    tmpXform.translate(0, displayHeight);
	    tmpXform.scale(displayWidth, -displayHeight);
	    return tmpXform;
	}
	
	public void pointerPressed(int x, int y) {
		//x = x - getParent().getAbsoluteX();
		//y = y - getParent().getAbsoluteY();
		//Point pPtrRelPrnt = new Point(x, y);
		//Point pCmpRelPrnt = new Point(getX(), getY());
		
	    float[] fPtr = new float[] {x - getAbsoluteX(), y - getAbsoluteY()};
	    Transform inverseVTM = Transform.makeIdentity();
	    try {
	        theVTM.getInverse(inverseVTM);
	    } catch (NotInvertibleException e) {
	        System.out.println("Non invertible xform!");
	    }
	    
	    inverseVTM.transformPoint(fPtr, fPtr);
		
		GameObjectCollection collection = gw.getCollection();	//Get the game object collection
		IIterator elements = collection.getIterator();
		
		if (!gw.isPlayMode()) { //if in pause mode
			while (elements.hasNext()) {
				GameObject obj = (GameObject) elements.getNext();
				if (obj instanceof Fixed) {
					Fixed fixedObj = (Fixed) obj;
					
					//If object inside pointer
					if (fixedObj.contains(fPtr)) {
						fixedObj.setSelected(true);
					}

					
					else {
						//If the position button is selected 
						if (gw.isPositionPressed()) {
							if (fixedObj.isSelected()) {
							fixedObj.setLocation(fPtr[0], fPtr[1]); //Set the location to where the pointer pressed
							
							repaint();
							
							gw.setPositionPressed(false); //No longer selected
							}
						}
						fixedObj.setSelected(false);
					}
				}
			}
		}
		repaint();
	}
	
	

	public void zoom(float factor) {
		float newWinLeft = winLeft * factor;
		float newWinRight = winRight * factor;
		float newWinTop = winTop * factor;
		float newWinBottom = winBottom * factor;
		float newWinHeight = newWinTop - newWinBottom;
		float newWinWidth = newWinRight - newWinLeft;
		
		if (newWinWidth >= 200 && newWinHeight >= 200 && newWinWidth <= 1614 && newWinHeight <= 1190) {
			winLeft = newWinLeft;
			winRight = newWinRight;
			winTop = newWinTop;
			winBottom = newWinBottom;

			winWidth = newWinWidth;
			winHeight= newWinHeight;
		}
		
		this.repaint();
	}

	public void panHorizontal(double delta) {
		winLeft += delta;
		winRight += delta;
		this.repaint();
	}

	public void panVertical(double delta) {
		winBottom += delta;
		winTop += delta;
		this.repaint();
	}
	
	@Override
	public boolean pinch(float scale) {
		System.out.println("pinch" + scale);
		if (scale < 1.0) {
			zoom(1.01f);
		} else if (scale > 1.0) {
			zoom(0.95f);
		}
		return true;
	}
	
	private Point pPrevDragLoc = new Point(-1, -1);
	@Override
	public void pointerDragged(int x, int y) {
		if (pPrevDragLoc.getX() != -1) {
			if (pPrevDragLoc.getX() < x)
				panHorizontal(10);
			else if (pPrevDragLoc.getX() > x)
				panHorizontal(-10);
			if (pPrevDragLoc.getY() < y)
				panVertical(-10);
			else if (pPrevDragLoc.getY() > y)
				panVertical(10);
		}
		pPrevDragLoc.setX(x);
		pPrevDragLoc.setY(y);
	}
}
