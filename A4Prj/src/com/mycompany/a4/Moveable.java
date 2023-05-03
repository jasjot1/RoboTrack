package com.mycompany.a4;

abstract class Moveable extends GameObject{

	private int heading; //Stores the compass angle in degrees
	private int speed;	//Stores the speed of object
	
	//Constructor for objects that are moveable
	public Moveable(int size, int color, float x, float y) {
		super(size, color, x, y);
	}
	
	//Moving game object based on speed and heading angle
	public void move(int timerRate, int mapWidth, int mapHeight) {
		double deltaX, deltaY; //Stores calculated values of x/y

		//Calculate deltaX/Y with angle converted to radians
		deltaX = (Math.cos(Math.toRadians(90 - this.heading)) * this.speed * timerRate) / 1000.0;
	    deltaY = (Math.sin(Math.toRadians(90 - this.heading)) * this.speed * timerRate) / 1000.0;

		
		
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
		
		
		//Update current location
		//this.setLocation(newX, newY);
		this.translate(newX - this.getX(), newY-this.getY());
	}
	
	//Getter for speed
	public int getSpeed() {
		return speed;
	}
	
	//Getter for heading
	public int getHeading() {
		return heading;
	}
	
	//Change speed: increment or decrement current speed by desired amount
	public void changeSpeed(int speed) {
		this.speed += speed;
	}
	
	//Setter for speed
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	//setter for heading: checks if heading is between 0-359
	public void setHeading(int newHeading) {
		int oldHeading = heading;
		heading = newHeading;
		
		//Add or subtract 360 from heading whether heading goes outside 0-359
		if (heading > 359)
			setHeading(heading - 360);
		if (heading < 0)
			setHeading(360 + heading);
		
		//Fix this
		this.rotate(heading - oldHeading);
	}
	

}
