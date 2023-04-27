package com.mycompany.a4;

abstract class Robot extends Moveable implements ISteerable{
	
	//Constructor for Robot
	public Robot(int size, int color, float x, float y) {
		super(size, color, x, y);
		
	}

	private int steeringDirection;	//Stores the steering direction 
    private int maximumSpeed;		//Stores the maximum speed 
    private int energyLevel;		//Stores the energy level
    private double energyConsumptionRate;	//How much energy robot spends per clock click
    private int damageLevel;		//Stores object damage level
    private int lastBaseReached;	//Stores what the last base reached was 
    private int maxDamageLevel;		//Stores damage level is set at 25
    private final int maxEnergyLevel = 10000;	//Maximum energy level is set at 1000

    
    //Checks whether current damage level is equal to (or greater than) the maximum allowed damage level
    public boolean isMaxDamage() {
    	return (damageLevel >= maxDamageLevel);
    }
    
    //Steers to the right by 5 degrees, at most up to 40
    public void turnRight() {
    	steeringDirection += 5; //Add 5 to direction
    	System.out.println("Turning right");
    	
    	//Max is 40 
    	if (steeringDirection > 40) {
    		steeringDirection = 40;
    		System.out.println("Cannot turn more right!");
    	}
    	
    }
    
  //Steers to the left by 5 degrees, at most up to -40
    public void turnLeft() {
    	steeringDirection -= 5;	
    	System.out.println("Turning left");
    	
    	//Max is negative 40 degrees
    	if(steeringDirection < -40) {
    		steeringDirection = -40;
    		System.out.println("Cannot turn more left!");
    	}
    	
    }
    
	//Increment or decrement heading by steering direction of robot
	public void incrementHeading() {
		setHeading(getHeading() + steeringDirection);
	}
    
	//Get steering direction
	public int getSteeringDirection() {
		return steeringDirection;
	}

	//Setter for steering direction
	public void setSteeringDirection(int steeringDirection) {
		this.steeringDirection = steeringDirection;
	}

	//Getter for maximum speed
	public int getMaximumSpeed() {
		return maximumSpeed;
	}

	//Setter for maximum speed
	public void setMaximumSpeed(int maximumSpeed) {
		this.maximumSpeed = maximumSpeed;
	}

	//Getter for energy level
	public int getEnergyLevel() {
		return energyLevel;
	}
	
	//Setter for energy level
	public void setEnergyLevel(int level) {
		energyLevel = level;
	}

	//Takes away energy level by the energyConsumptionRate of robot
	public void decreaseEnergyLevel() {
		if (energyLevel > 0)
			energyLevel = (int) (energyLevel - energyConsumptionRate);
		else
			System.out.println("No more energy level");
	}
	
	//Increases energy level by desired amount (size of energy station)
	public void increaseEnergyLevel(int amount) {
		//Add only when less than maxEnergyLevel
		if (energyLevel < maxEnergyLevel)
			energyLevel = energyLevel + amount;
		
		//If the amount added goes over maxEnergyLevel, set back to max
		if (energyLevel > maxEnergyLevel)
			energyLevel = maxEnergyLevel;
	}
	
	//Getter for energy consumption
	public double getEnergyConsumptionRate() {
		return energyConsumptionRate;
	}

	//Setter for energy consumption rate
	public void setEnergyConsumptionRate(double rate) {
		energyConsumptionRate = rate;
	}

	//Getter for damage level
	public int getDamageLevel() {
		return damageLevel;
	}
	
	//Setter for damage level
	public void setDamageLevel(int level) {
		damageLevel = level;
	}
	
	//Increase damageLevel and decrease maximum speed as damage level increases
	public void increaseDamageLevel(int damage) {
		if (damageLevel < maxDamageLevel) {
			this.damageLevel += damage;
			this.maximumSpeed -= damage;
		}
		else {
			System.out.println("Max Damage Level reached");
			setSpeed(0);
		}
	}

	//Getter for last base reached
	public int getLastBaseReached() {
		return lastBaseReached;
	}

	//Setter for last base reached
	public void setLastBaseReached(int lastBaseReached) {
		this.lastBaseReached = lastBaseReached;
	}
    
	//Setter for energy level
	public void setMaxDamageLevel(int maxDamage) {
		maxDamageLevel = maxDamage;
	}
	
}
