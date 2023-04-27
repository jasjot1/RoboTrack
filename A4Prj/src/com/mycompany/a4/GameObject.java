package com.mycompany.a4;

import java.util.ArrayList;

import com.codename1.charts.models.Point;
import com.codename1.charts.util.ColorUtil;

abstract class GameObject implements IDrawable, ICollider{
	private final int size;		//Size provides length of bounding square, cannot be changed
	private int color;			//Objects color stored as integer
	private Point location;		//Stores location as Point
	private ArrayList<GameObject> collisionVector = new ArrayList<>(); //Store collided objects in here
	
	//Constructor sets the size and color of object
	public GameObject(int size, int color, float x, float y) {
		this.size = size;
		this.color = color;
		
		//Set location based on xy coordinates
		this.location = new Point(x,y);
	}
	
	//Getter for size
	public int getSize() {
		return size;
	}
	
	//Getter for x value
	public float getX() {
		return location.getX();
	}
	
	//Getter for y value
	public float getY() {
		return location.getY();
	}
	
	//Setter for location
	public void setLocation(float x, float y) {
		location.setX(x);
		location.setY(y);
	}
	
	//Getter for integer value of color
	public int getColor() {
		return color;
	}
	
	//Setter for color using RGB
	public void setColor(int red, int green, int blue) {
		this.color = ColorUtil.rgb(red, green, blue);
	}
	
	//Setter for color using integer values
	public void setColor(int color) {
		this.color = color;
		
	}
	
	//toString for game object includes what every game object must have
	//This returns location, color, and size
	public String toString() {
		String desc = "\tLoc=" + getX() + ',' + getY() + " color=[" + 
				ColorUtil.red(color) + ',' + ColorUtil.green(color) + ',' +
				ColorUtil.blue(color) + "] size=" + size;
		
		return desc;
	}
	
	//Getter for collision vector
	public ArrayList<GameObject> getCollisionVector(){
		return collisionVector;
	}
	
	
}
