package com.mycompany.a4;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Button;
import com.codename1.ui.CheckBox;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.util.UITimer;

import java.lang.String;

//Displays the information about the state of the game
public class Game extends Form implements Runnable{
	private GameWorld gw;
	private MapView mapView;
	private ScoreView scoreView;
	
	private final int timerRate = 20; //Store the UITimer rate in MS
	
	private Button pausePlayButton;		//This button changes game modes, used in changePausePlayMode
	
	//All commands/buttons that can be disabled/enabled based on game mode defined here
	private MovementCommands accelerate;
	private Button accelerateButton;
	private MovementCommands left;
	private Button leftButton;
	private MovementCommands strat;
	private Button strategyButton;
	private MovementCommands brake;
	private Button brakeButton;
	private MovementCommands right;
	private Button rightButton;
	private GameModeCommands positionCommand;
	private Button positionButton;
	
	//Constructor instantiates a GameWorld
	public Game() {
		gw = new GameWorld();	//Create “Observable” GameWorld
		mapView = new MapView(gw);		//Create an "Observer" for the map
		scoreView = new ScoreView();	//Create an "Observer" for the game and player state data
		UITimer timer = new UITimer(this);	//Create new UI timer
		
		setLayout(new BorderLayout());		//Set border layout for game
		add(BorderLayout.CENTER,mapView);		//Add map view to center
		add(BorderLayout.NORTH,scoreView);			//Add score view to top
		
		//Creating tool bar
		Toolbar bar = new Toolbar();
		setToolbar(bar);
		bar.setTitle("Robo-Track Game");	//Set title of bar
		
		//Accelerate command button
		accelerate = new MovementCommands("Accelerate", gw);
		bar.addCommandToSideMenu(accelerate);
		
		//Sound check box
		CheckBox soundCheck = new CheckBox("Side Menu Item Check");
		MenuCommands soundCmd = new MenuCommands("Sound", gw);
		soundCheck.getAllStyles().setBgTransparency(255);
		soundCheck.setCommand(soundCmd);
		bar.addComponentToSideMenu(soundCheck);
		
		//About command
		MenuCommands about = new MenuCommands("About", gw);
		bar.addCommandToSideMenu(about);
		
		//Exit command
		MenuCommands exit = new MenuCommands("Exit", gw);
		bar.addCommandToSideMenu(exit);
		
		//Help command
		MenuCommands help = new MenuCommands("Help", gw);
		bar.addCommandToRightBar(help);
		
		//Creating separate containers
		Container westContainer = new Container();
		Container eastContainer = new Container();
		Container southContainer = new Container();
		
		westContainer.setLayout(new BoxLayout(BoxLayout.Y_AXIS)); //Using box layout for container with vertical (y) components
		eastContainer.setLayout(new BoxLayout(BoxLayout.Y_AXIS)); 
		southContainer.setLayout(new FlowLayout(Component.CENTER));
		
		//Add border to container
		westContainer.getAllStyles().setBorder(Border.createLineBorder(1, ColorUtil.BLACK));
		eastContainer.getAllStyles().setBorder(Border.createLineBorder(1, ColorUtil.BLACK));
		
		//Add empty Label to move buttons down
		westContainer.add(new Label(" "));
		westContainer.add(new Label(" "));
		
		//Accelerate command button
		accelerateButton = new Button(accelerate);
		accelerateButton.getAllStyles().setPadding(TOP, 6);
		accelerateButton.getAllStyles().setPadding(BOTTOM, 6);
		accelerateButton.getAllStyles().setPadding(LEFT, 2);
		accelerateButton.getAllStyles().setPadding(RIGHT, 2);
		accelerateButton.getAllStyles().setFgColor(ColorUtil.WHITE); 	//White text
		accelerateButton.getAllStyles().setBgColor(ColorUtil.BLUE);	//Blue background
		accelerateButton.getAllStyles().setBgTransparency(255);		//Make background fully visible
		accelerateButton.getAllStyles().setBorder(Border.createLineBorder(5,ColorUtil.BLACK)); //Create black border around button
		accelerateButton.getDisabledStyle().setBgColor(ColorUtil.BLACK); //Set background to black to show disabled
		westContainer.add(accelerateButton);
		addKeyListener('a', accelerate);	//Key listener for accelerate command 
		
		//Left command button
		left = new MovementCommands("Left",gw);
		leftButton = new Button(left);
		leftButton.getAllStyles().setPadding(TOP, 6);
		leftButton.getAllStyles().setPadding(BOTTOM, 6);
		leftButton.getAllStyles().setPadding(LEFT, 2);
		leftButton.getAllStyles().setPadding(RIGHT, 2);
		leftButton.getAllStyles().setFgColor(ColorUtil.WHITE); 	//White text
		leftButton.getAllStyles().setBgColor(ColorUtil.BLUE);	//Blue background
		leftButton.getAllStyles().setBgTransparency(255);		//Make background fully visible
		leftButton.getAllStyles().setBorder(Border.createLineBorder(5,ColorUtil.BLACK)); //Create black border around button
		leftButton.getDisabledStyle().setBgColor(ColorUtil.BLACK); //Set background to black to show disabled
		westContainer.add(leftButton);
		addKeyListener('l', left);	//Key listener for left command 
		
		//Change strategies command button
		strat = new MovementCommands("Change Strategies",gw);
		strategyButton = new Button(strat);
		strategyButton.getAllStyles().setPadding(TOP, 6);
		strategyButton.getAllStyles().setPadding(BOTTOM, 6);
		strategyButton.getAllStyles().setPadding(LEFT, 2);
		strategyButton.getAllStyles().setPadding(RIGHT, 2);
		strategyButton.getAllStyles().setFgColor(ColorUtil.WHITE); 	//White text
		strategyButton.getAllStyles().setBgColor(ColorUtil.BLUE);	//Blue background
		strategyButton.getAllStyles().setBgTransparency(255);		//Make background fully visible
		strategyButton.getAllStyles().setBorder(Border.createLineBorder(5,ColorUtil.BLACK)); //Create black border around button
		strategyButton.getDisabledStyle().setBgColor(ColorUtil.BLACK); //Set background to black to show disabled
		westContainer.add(strategyButton);
		
		//Add empty Label to move buttons down
		eastContainer.add(new Label(" "));
		eastContainer.add(new Label(" "));
		
		//Brake command button
		brake = new MovementCommands("Brake",gw);	
		brakeButton = new Button(brake);
		brakeButton.getAllStyles().setPadding(TOP, 6);
		brakeButton.getAllStyles().setPadding(BOTTOM, 6);
		brakeButton.getAllStyles().setPadding(LEFT, 2);
		brakeButton.getAllStyles().setPadding(RIGHT, 2);
		brakeButton.getAllStyles().setFgColor(ColorUtil.WHITE); 	//White text
		brakeButton.getAllStyles().setBgColor(ColorUtil.BLUE);	//Blue background
		brakeButton.getAllStyles().setBgTransparency(255);		//Make background fully visible
		brakeButton.getAllStyles().setBorder(Border.createLineBorder(5,ColorUtil.BLACK)); //Create black border around button
		brakeButton.getDisabledStyle().setBgColor(ColorUtil.BLACK); //Set background to black to show disabled
		eastContainer.add(brakeButton);
		addKeyListener('b', brake);	//Key listener for brake command 
		
		//Right command button
		right = new MovementCommands("Right",gw);	
		rightButton = new Button(right);
		rightButton.getAllStyles().setPadding(TOP, 6);
		rightButton.getAllStyles().setPadding(BOTTOM, 6);
		rightButton.getAllStyles().setPadding(LEFT, 2);
		rightButton.getAllStyles().setPadding(RIGHT, 2);
		rightButton.getAllStyles().setFgColor(ColorUtil.WHITE); 	//White text
		rightButton.getAllStyles().setBgColor(ColorUtil.BLUE);	//Blue background
		rightButton.getAllStyles().setBgTransparency(255);		//Make background fully visible
		rightButton.getAllStyles().setBorder(Border.createLineBorder(5,ColorUtil.BLACK)); //Create black border around button
		rightButton.getDisabledStyle().setBgColor(ColorUtil.BLACK); //Set background to black to show disabled
		eastContainer.add(rightButton);
		addKeyListener('r', right);	//Key listener for right command 
		
		//Position command button
		positionCommand = new GameModeCommands("Position", this, gw);	
		positionButton = new Button(positionCommand);
		positionButton.getAllStyles().setPadding(TOP, 6);
		positionButton.getAllStyles().setPadding(BOTTOM, 6);
		positionButton.getAllStyles().setPadding(LEFT, 2);
		positionButton.getAllStyles().setPadding(RIGHT, 2);
		positionButton.getAllStyles().setFgColor(ColorUtil.WHITE); 	//White text
		positionButton.getAllStyles().setBgColor(ColorUtil.BLUE);	//Blue background
		positionButton.getAllStyles().setBgTransparency(255);		//Make background fully visible
		positionButton.getAllStyles().setBorder(Border.createLineBorder(5,ColorUtil.BLACK)); //Create black border around button
	    positionButton.getDisabledStyle().setBgColor(ColorUtil.BLACK); //Set background to black to show disabled
		southContainer.add(positionButton);
	    positionCommand.setEnabled(false);	//Position button is disabled in default play mode
	    positionButton.setEnabled(false);

		
		//Pause or play button
		pausePlayButton = new Button(new GameModeCommands("Pause", this, gw)); //Default to play mode, so pause is shown at first
		pausePlayButton.getAllStyles().setPadding(TOP, 6);
		pausePlayButton.getAllStyles().setPadding(BOTTOM, 6);
		pausePlayButton.getAllStyles().setPadding(LEFT, 2);
		pausePlayButton.getAllStyles().setPadding(RIGHT, 2);
		pausePlayButton.getAllStyles().setFgColor(ColorUtil.WHITE); 	//White text
		pausePlayButton.getAllStyles().setBgColor(ColorUtil.BLUE);	//Blue background
		pausePlayButton.getAllStyles().setBgTransparency(255);		//Make background fully visible
		pausePlayButton.getAllStyles().setBorder(Border.createLineBorder(5,ColorUtil.BLACK)); //Create black border around button
		pausePlayButton.getDisabledStyle().setBgColor(ColorUtil.BLACK); //Set background to black to show disabled
		southContainer.add(pausePlayButton);
		
		add(BorderLayout.WEST, westContainer);
		add(BorderLayout.EAST, eastContainer);
		add(BorderLayout.SOUTH, southContainer);
		gw.addObserver(mapView);	//Add the map observer to GameWorld
		gw.addObserver(scoreView);	//Add the score observer GameWorld
		
		
		this.show();
		
		//Set height and width in game world based on map
		gw.setHeight(mapView.getHeight());	
		gw.setWidth(mapView.getWidth());
		
		gw.init(); //Sets initial state of game
		gw.createSounds(); 
		timer.schedule(timerRate, true, this); //Schedule timer with rate
	}

	@Override
	public void run() {
		if (gw.isPlayMode()) { //If play mode is on, then tick the clock 
			gw.clockTick(timerRate);
		}

	}
	
	//Changes game modes (pause mode to play, or play to pause)
	public void changePlayPause() {
	    gw.changeGameMode();

	    if (gw.isPlayMode()) { //Changing to play mode
	        pausePlayButton.setCommand(new GameModeCommands("Pause", this, gw));

	        //Add back key listeners
	        addKeyListener('a', accelerate);	//Key listener for accelerate command
	        addKeyListener('l', left);	//Key listener for left command
	        addKeyListener('r', right);	//Key listener for right command
	        addKeyListener('b', brake);	//Key listener for brake command
	        
	        if(gw.getSound()) {
	        	gw.backgroundSoundPlay();
	        }
	    } 
	    
	    else { //Changing to pause mode
	        pausePlayButton.setCommand(new GameModeCommands("Play", this, gw));

	        //Remove key listeners
	        this.removeKeyListener('a', accelerate);
	        this.removeKeyListener('l', left);
	        this.removeKeyListener('r', right);
	        this.removeKeyListener('b', brake);
	        
	        if(gw.getSound()) {
	        	gw.backgroundSoundPause();
	        }
	    }

	    //Position button is disabled during play, enabled in pause
	    positionCommand.setEnabled(!gw.isPlayMode());
	    positionButton.setEnabled(!gw.isPlayMode());

	    //Rest of the commands/buttons go based on if the game is in play mode
	    accelerate.setEnabled(gw.isPlayMode());
	    accelerateButton.setEnabled(gw.isPlayMode());
	    
	    left.setEnabled(gw.isPlayMode());
	    leftButton.setEnabled(gw.isPlayMode());
	    
	    right.setEnabled(gw.isPlayMode());
	    rightButton.setEnabled(gw.isPlayMode());
	    
	    brake.setEnabled(gw.isPlayMode());
	    brakeButton.setEnabled(gw.isPlayMode());
	    
	    strat.setEnabled(gw.isPlayMode());
	    strategyButton.setEnabled(gw.isPlayMode());
	}

}
;