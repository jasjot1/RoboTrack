package com.mycompany.a4;

import java.util.Observable;
import java.util.Random;

import com.codename1.charts.util.ColorUtil;

//Holds collections of game objects and state variables
public class GameWorld extends Observable{
	private int lives;	//Stores the number of lives remaining for player robot
	private int time;	//Stores the time that has elapsed
	private Boolean isSound; //Stores whether sound is on or off
	private int height;	//Stores game height
	private int width;	//Stores game width
	
	//Collection of GameObjects stored in collection
	private GameObjectCollection gameCollection = new GameObjectCollection();
	
	private int timeMS = 0; //Accumulator for time in in MS

	private boolean isPlayMode = true;	//Stores where on play mode or not (Default to play mode - true)
	private boolean positionPressed = false; //Stores whether position button is pressed or not
	
	//Creating variables for each sound
	private BGSound backgroundSound;	//This stores the background sound
	private Sound robotCollideSound;
	private Sound energySound;
	private Sound lifeLostSound;
	private Sound droneCollideSound;
	
	//Initialize the game world with adding all of the objects and setting the number of lives
	public void init() {
		lives = 3;
		time = 0;
		isSound = false;
		addObjects();	//call addObjects to add desired objects to game
	}
	
	
	//Adds game objects to gameObjectList
	public void addObjects() {
		//4 bases with set sequence number, size, color, and locations added
		gameCollection.add(new Base(1, 50, ColorUtil.rgb(255, 0, 0), 50.0f, 50.0f));
		gameCollection.add(new Base(2, 50, ColorUtil.rgb(255, 0, 0), 250.0f, 600.0f));
		gameCollection.add(new Base(3, 50, ColorUtil.rgb(255, 0, 0), 650.0f, 650.0f));
		gameCollection.add(new Base(4, 50, ColorUtil.rgb(255, 0, 0), 600.0f, 100.0f));
		
		//Drones added with set color
		//Other properties of game object are set in constructor
		//Also takes in game world map height and width
		gameCollection.add(new Drone(ColorUtil.GRAY, width, height));
		gameCollection.add(new Drone(ColorUtil.GRAY, width, height));
		
		//Robot added with set size, color, and location with same location as base 1
		//Since robot is singleton we must use getRobot to add to collection
		PlayerRobot playerRobot = PlayerRobot.getRobot(60,ColorUtil.BLUE,50.0f,50.0f);
		gameCollection.add(playerRobot);
		
		//Adding the non player robots
		gameCollection.add(new NonPlayerRobot(50,ColorUtil.rgb(255, 0, 0), 100.0f,200.0f));
		gameCollection.add(new NonPlayerRobot(50,ColorUtil.rgb(255, 0, 0), 350.0f,100.0f));
		gameCollection.add(new NonPlayerRobot(50,ColorUtil.rgb(255, 0, 0), 1000.0f,1000.0f));
		gameCollection.add(new NonPlayerRobot(50,ColorUtil.rgb(255, 0, 0), 1200.0f,500.0f));
		
		//Set random strategy for every non player robot
		Random rand = new Random();
		IIterator elements = gameCollection.getIterator();
		while (elements.hasNext()) {
			GameObject obj = (GameObject) elements.getNext();
			if (obj instanceof NonPlayerRobot) {
				NonPlayerRobot npr = (NonPlayerRobot) obj;
				
				int randomNum = rand.nextInt(11); //0-10 random number
				if (randomNum %2 == 0) { //if even, then attack strategy 
					npr.setStrategy(new AttackPlayerStrategy(npr, this));
				}
				else {//If odd then base strategy
					npr.setStrategy(new MoveToBaseStrategy(npr, this));
				}
				
			}
		}

		//Energy stations added with set color
		//Other properties of game object are set in constructor
		gameCollection.add(new EnergyStation(ColorUtil.rgb(0, 180, 0), width, height));
		gameCollection.add(new EnergyStation(ColorUtil.rgb(0, 180, 0), width, height));
	}

	//m command
	//Prints properties of all of the game objects in world
	public void printMap() {
		IIterator elements = gameCollection.getIterator();
		while (elements.hasNext()) {
			GameObject obj = (GameObject) elements.getNext();
			System.out.println(obj);
		}
		System.out.println();
	}
	
	//d command
	//Prints the lives remaining, time, last base reached, energy level, and damage level
	public void printGameState() {
		System.out.println("Lives Remaining: " + lives);
		System.out.println("Elapsed Time: " + time);
		System.out.println("Base Reached: " + getRobot().getLastBaseReached());
		System.out.println("Energy Level: " + getRobot().getEnergyLevel());
		System.out.println("Damage Level: " + getRobot().getDamageLevel());
	}
	
	//Loops through gameObjectList and return instance of player robot
	public PlayerRobot getRobot() {
		
		IIterator elements = gameCollection.getIterator();
		while (elements.hasNext()) {
			GameObject obj = (GameObject) elements.getNext();
			if (obj instanceof PlayerRobot) {
				return (PlayerRobot) obj;
			}
		}
		return null;
	}
	
	//Loops through gameObjectList and return instance of base with sequence number
	public Base getBase(int seqNum) {
		
		IIterator elements = gameCollection.getIterator();
		while (elements.hasNext()) {
			GameObject obj = (GameObject) elements.getNext();
			if (obj instanceof Base) {
				Base base = (Base) obj;
				//Check if base matches sequence number
				if((base.getSequenceNumber() == seqNum))
					return base;
			}
		}
		return null;
	}
	
	//a command: Increase speed of player robot by 1
	public void accelerate() {
		//Check if current speed is less than max speed and still has energy
		if (getRobot().getSpeed()< getRobot().getMaximumSpeed() && (getRobot().getEnergyLevel() > 0)) {
			getRobot().changeSpeed(5);
			System.out.println("Speed Increased");
		}
			
		else
			System.out.println("Max speed reached");

		setChanged();
		notifyObservers(this);
	}
	
	//b command: Decrease speed of player robot by 1
	public void brake() {
		if (getRobot().getSpeed() > 0) { //If speed is positive value
			getRobot().changeSpeed(-1);
			System.out.println("Speed Decreased");
		}
		
		//Zero speed, do nothing
		else
			System.out.println("Speed is zero.");
		
		setChanged();
		notifyObservers(this);
	}
	
	//t command
	//Ticks the game clock and moves the movable objects and updates their heading
	public void clockTick(int timerRate) {
		IIterator elements = gameCollection.getIterator();
		while (elements.hasNext()) {
			GameObject obj = (GameObject) elements.getNext();
	
			if (obj instanceof PlayerRobot) {
				PlayerRobot playerRobot = (PlayerRobot) obj;
				
				//Decrease life if energy level is 0 or max damage already reached
				checkGameOver(playerRobot);
				
				//Check if robot did not run out of energy, no max damage, speed not zero
				if(playerRobot.getEnergyLevel() != 0 && !playerRobot.isMaxDamage() 
						&& playerRobot.getSpeed() !=0) {
					playerRobot.move(timerRate, width, height);
					
					//robot's heading incremented/decrement by the steering direction
					playerRobot.incrementHeading();;
					
					//Reset steering direction
					playerRobot.setSteeringDirection(0);
					
					//Robots energy is reduced by energyConsumptionRate
					playerRobot.decreaseEnergyLevel();

				}
			}
			
			if(obj instanceof Drone) {
				Drone drone = (Drone) obj;
				//Drone updates heading and moves
				drone.changeHeading();
				drone.move(timerRate,width,height);
				
			}
			
			if(obj instanceof NonPlayerRobot) {
				NonPlayerRobot npr = (NonPlayerRobot) obj;
				
				npr.move(timerRate,width,height); //Move npr
				
				if (npr.getEnergyLevel() < 2) { //NPR has infinite energy
					npr.setEnergyLevel(100);
				}
				
				//Invoke strategy
				npr.invokeStrategy();
				
			}
			
			if(obj instanceof ShockWave) {
				ShockWave wave = (ShockWave) obj;
				wave.move(timerRate,width,height);
				
				
				if(wave.reachedMaxLifetime()) {
					gameCollection.remove(wave);
					break;
				}
			}
		}
		
		//collisionCheckCounter++; 
		
		//Check if counter for delay more than collision delay
		//if (collisionCheckCounter >= collisionCheckDelay) {
			IIterator iterator = gameCollection.getIterator();
			//Check collision
			while (iterator.hasNext()) {
				GameObject currentObject = (GameObject) iterator.getNext();
				//Check if currentObject collides with other object
				IIterator iterator2 = gameCollection.getIterator();
				while (iterator2.hasNext()) {
					GameObject otherObject = (GameObject) iterator2.getNext();
					
					//If objects aren't the same
					if(otherObject != currentObject) {
						if (currentObject.collidesWith(otherObject)) { //Check if current object collides with other
							
			                //See if the collision is not in the collision vector
			                if (!currentObject.getCollisionVector().contains(otherObject) && !otherObject.getCollisionVector().contains(currentObject)) {
			                    currentObject.handleCollision(otherObject, this);
			                    currentObject.getCollisionVector().add(otherObject);	//Add to both collision vectors
			                    otherObject.getCollisionVector().add(currentObject);
			                    //break;
			                }
			            } 
						else { //No longer colliding
			                currentObject.getCollisionVector().remove(otherObject); //Remove objects from eachother's collision vector
			                otherObject.getCollisionVector().remove(currentObject);
			            }
					}
				}

			}
			//collisionCheckCounter = 0;
		//}
		
		//
		timeMS += timerRate;
		if(timeMS >= 1000) {
			time += 1;
			timeMS = timeMS - 1000;
		}
		
		setChanged();
		notifyObservers(this);

	}
	
	
	//l command
	//Calls turn left function on robot to turn robot 5 degrees left
	public void steerLeft() {
		getRobot().turnLeft();
		
		setChanged();
		notifyObservers(this);
		
	}
	
	//r command
	//Calls turn right function on robot to turn robot 5 degrees right
	public void steerRight() {
		getRobot().turnRight();
		
		setChanged();
		notifyObservers(this);
	}
	
	//c command
	//Pretend player robot has collided with some other robot
	public void collideRobot() {
		System.out.println("Collison with non player robot occured!");
		
		//Store instance of playerRobot
		PlayerRobot playerRobot = getRobot();
		IIterator elements = gameCollection.getIterator();
		while (elements.hasNext()) {
			GameObject obj = (GameObject) elements.getNext();
	
			if (obj instanceof NonPlayerRobot) {
				NonPlayerRobot npr = (NonPlayerRobot) obj;
				
				//Not fully damaged yet
				if(!playerRobot.isMaxDamage()) {
					//Damage player robot
					playerRobot.increaseDamageLevel(10);
					
					//Damage non player robot
					npr.increaseDamageLevel(10);
					
					//Fade color of robot
					playerRobot.setColor(ColorUtil.red(playerRobot.getColor())+40, ColorUtil.green(playerRobot.getColor())+40, ColorUtil.blue(playerRobot.getColor()));
					break;
				}
				
				
				//Check if game is over or remove a life
				checkGameOver(playerRobot);
			}
			
		}
		
		setChanged();
		notifyObservers(this);
	}
	
	//1-9 commands: pretend collision with base 1-9
	public void collideBase(int baseNum) {
		int lastBase = getRobot().getLastBaseReached();
		
		//New base must be 1 larger than last base to set it
		if ((baseNum - lastBase) == 1) {
			getRobot().setLastBaseReached(baseNum);
			System.out.println("Reached base " + baseNum);
		}
		
		else {
			System.out.println("Error: Based already reached or order not correct.");
		}
		
		//Check if last base has reached
		checkWin();
		
		setChanged();
		notifyObservers(this);
		
	}
	
	//Checks if the last base (4) has been reached yet, if so the game will end and user won.
	public void checkWin() {
		//End game when desired base has reached
		if (getRobot().getLastBaseReached() >= 4) {
			System.out.println("Game over, you win!");
			System.out.println("Total time: " + time);	//Print time it took to win
			System.exit(0);
		}
		
		//Check if npr has reached last base
		IIterator elements = gameCollection.getIterator();
		while (elements.hasNext()) {
			GameObject obj = (GameObject) elements.getNext();
			if (obj instanceof NonPlayerRobot) {
				NonPlayerRobot npr = (NonPlayerRobot) obj;
				if (npr.getLastBaseReached() >= 4) {
					System.out.println("Game over, a non-player robot wins!");
					System.exit(0);
				}
			}
		}
		
	}
	
	//e command: Pretend there is a collision with energy station
	public void collideEnergyStation(EnergyStation station) {
		System.out.println("Collison with energy station occured!");
		
		Robot playerRobot = getRobot();
				
		//Check if capacity of current energy station isn't 0
		if (station.getCapacity() != 0) {
			//Increase robots energy by the capacity of energy station
			playerRobot.increaseEnergyLevel(10*station.getCapacity());
			
			//Reduce capacity to 0
			station.setCapacity(0);
			
			//Fade color of energy station
			station.setColor(ColorUtil.red(station.getColor())+150, ColorUtil.green(station.getColor()), ColorUtil.blue(station.getColor())+150);
			
			//Add new energy station (will be random location and size)
			gameCollection.add(new EnergyStation(ColorUtil.rgb(0, 180, 0), width, height));
		}

		setChanged();
		notifyObservers(this);
		
	}
	
	//g command: pretend that drone has flown over robot
	public void collideDrone() {
		System.out.println("Collison with drone occured!");
		PlayerRobot playerRobot = getRobot();
				
		//Not fully damaged yet
		if(!playerRobot.isMaxDamage()) {
			playerRobot.increaseDamageLevel(5);
			
			//Fade color of robot
			playerRobot.setColor(ColorUtil.red(playerRobot.getColor())+40, ColorUtil.green(playerRobot.getColor())+40, ColorUtil.blue(playerRobot.getColor()));
		}
		
		//Pass in robot player to check if game is over or loose a life
		checkGameOver(playerRobot);
		
		setChanged();
		notifyObservers(this);
	}
	
	//Takes in object Player robot and checks if the game should be over.
	public void checkGameOver(PlayerRobot playerRobot) {
		//When player has reached max damage or energy level 0
		if(playerRobot.isMaxDamage() || playerRobot.getEnergyLevel() == 0) {
			if (lives > 0) {	//If there are still lives remaining, remove 1 life
				lives--;
				System.out.println("Lost a life");
				
				if (getSound()) {
					lifeLostSound.play();
				}
				
				//Call getBase to find what robot's last base was 
				Base lastBase = getBase(playerRobot.getLastBaseReached());
				
				//Reset robot to new life with location at last base
				playerRobot.newLife(lastBase.getX(), lastBase.getY());
			}
			
			//When no lives are remaining (0), exit the game
			if (lives == 0) {
				System.out.println("Game over, you failed!");
				System.exit(0);
			}
			
		}
		
	}
	
	//Getter for lives remaining
	public int getLives() {
		return lives;
	}
	
	//Getter for game time
	public int getTime() {
		return time;
	}
	
	//x command: exit the game
	public void exit() {
		System.out.println("Exiting");
		System.exit(0);
	}
	
	//Getter for sound on or off
	public Boolean getSound() {
		return isSound;
	}
	
	//Setter for sound
	public void setSound(Boolean sound) {
		isSound = sound;
		
		
		if(sound && isPlayMode()) {
			backgroundSound.play();
		}
		else
			backgroundSound.pause();
		
		setChanged();
		notifyObservers(this);
	}
	
	//Getter for height
	public int getHeight() {
		return height;
	}
	
	//Getter for width
	public int getWidth() {
		return width;
	}
	
	//Set height
	public void setHeight(int height) {
		this.height=height;
	}
	
	//Set width
	public void setWidth(int width) {
		this.width = width;
	}
	
	//Changes the current strategy and increases the last base reached
	public void changeStrategies() {
		System.out.println("Switching Strategies");
		
		IIterator elements = gameCollection.getIterator();
		while (elements.hasNext()) {
			GameObject obj = (GameObject) elements.getNext();
			if (obj instanceof NonPlayerRobot) {
				NonPlayerRobot npr = (NonPlayerRobot) obj;
				
				//If base strategy change to attack
				if(npr.printStrategy() == "Base Strategy") {
					npr.setStrategy(new AttackPlayerStrategy(npr, this));
				}
				else { //If attack, then change to base
					npr.setStrategy(new MoveToBaseStrategy(npr, this));
				}
				
				//Increase last base reached
				npr.setLastBaseReached(npr.getLastBaseReached()+1);
				checkWin();
			}
			
		}
		setChanged();
		notifyObservers(this);
	}
	
	//Getter for the game collection
	public GameObjectCollection getCollection() {
		return gameCollection;
	}


	//Getter for play mode
	public boolean isPlayMode() {
		return isPlayMode;
	}

	
	//Changes to opposite mode
	public void changeGameMode() {
		isPlayMode = !isPlayMode; //Change game modes (do opposite)
	}

	//Getter for position pressed (t/f)
	public boolean isPositionPressed() {
		return positionPressed;
	}

	//Flag for setting if position button is pressed
	public void setPositionPressed(boolean positionPressed) {
		this.positionPressed = positionPressed;
		setChanged();
		notifyObservers(this);
	}
	
	//Creating all the sounds based on their file names
	public void createSounds() {
		backgroundSound = new BGSound("game-music.mp3");
		robotCollideSound = new Sound("crash.wav");
		energySound = new Sound("lightning.wav");
		lifeLostSound = new Sound("boom.wav");
		droneCollideSound = new Sound("drone.wav");
	}
	
	//Plays robot collide sound
	public void robotCollideSoundPlay() {
		if (getSound())
			robotCollideSound.play();
	}
	
	//Plays energy station collision sound
	public void energySoundPlay() {
		if (getSound())
			energySound.play();
	}
	
	//Plays drone collision sound
	public void droneCollideSoundPlay() {
		if (getSound())
			droneCollideSound.play();
	}
	
	//Plays background music
	public void backgroundSoundPlay() {
		if (getSound())
			backgroundSound.play();
	}
	
	//Pauses background music
	public void backgroundSoundPause() {
		if (getSound())
			backgroundSound.pause();
	}
	
	public void addGameObject(GameObject gameObject) {
	    gameCollection.add(gameObject);
	}
	
	public void addShockWave(Robot robo) {
		// Create a new ShockWave
		ShockWave shockWave = new ShockWave(50, ColorUtil.CYAN, robo.getX(), robo.getY(), width, height);

		Random rand = new Random();
		shockWave.setHeading(rand.nextInt(360));
		gameCollection.add(shockWave);
	}
	
	public void removeShockWave(ShockWave wave) {
		gameCollection.remove(wave);
	}
	
}
