package com.mycompany.a4;

import java.util.Observable;
import java.util.Observer;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Container;
import com.codename1.ui.Graphics;
import com.codename1.ui.geom.Dimension;
import com.codename1.charts.models.Point;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.plaf.Border;

public class MapView extends Container implements Observer{
	
	GameWorld gw; //Store GameWorld instance
	
	public MapView(GameWorld gw) {
		this.gw = gw;	//Constructor takes in instance of game world and assigns it
		
		//Creating new border layout
		setLayout(new BorderLayout());
		
		//Red border
		getAllStyles().setBorder(Border.createLineBorder(3, ColorUtil.rgb(255, 0, 0)));
		
	}
	
	
	@Override
	public void update(Observable observable, Object data) {
		gw = (GameWorld) data;
	
		//gw.printMap(); //print map to console when anything updates
		repaint();
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		GameObjectCollection collection = gw.getCollection();	//Get the game object collection
		
		//origin location of the component (CustomContainer) relative to its parent container origin
		Point pCmpRelPrnt = new Point(getX(),getY());
		//origin location of the component (CustomContainer) relative to the screen origin
		Point pCmpRelScreen = new Point(getAbsoluteX(),getAbsoluteY());
		
		//Iterate through all of the GameObjects to call draw
		IIterator elements = collection.getIterator();
		while (elements.hasNext()) {
			GameObject obj = (GameObject) elements.getNext();
			obj.draw(g, pCmpRelPrnt, pCmpRelScreen);
		}
	}
	
	//Make fixed objects selectable while in pause mode
	public void pointerPressed(int x, int y) {
		x = x - getParent().getAbsoluteX();
		y = y - getParent().getAbsoluteY();
		Point pPtrRelPrnt = new Point(x, y);
		Point pCmpRelPrnt = new Point(getX(), getY());
		
		GameObjectCollection collection = gw.getCollection();	//Get the game object collection
		IIterator elements = collection.getIterator();
		
		if (!gw.isPlayMode()) { //if in play mode
			while (elements.hasNext()) {
				GameObject obj = (GameObject) elements.getNext();
				if (obj instanceof Fixed) {
					Fixed fixedObj = (Fixed) obj;
					
					//If object inside pointer
					if (fixedObj.contains(pPtrRelPrnt, pCmpRelPrnt)) {
						fixedObj.setSelected(true);
	
					}
					
					else {
						//If the position button is selected 
						if (gw.isPositionPressed()) {
							if (fixedObj.isSelected()) {
							fixedObj.setLocation(x-getX(), y-getY()); //Set the location to where the pointer pressed
							
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
	
	
	

}
