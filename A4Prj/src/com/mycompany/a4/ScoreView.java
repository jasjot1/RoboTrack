package com.mycompany.a4;

import java.util.Observable;
import java.util.Observer;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.FlowLayout;

public class ScoreView extends Container implements Observer{
	
	//Labels for score view
	//label names ending in Value are updated in update()
	private Label timeLabel = new Label("Time:");
	private Label timeValue = new Label("0   ");
	
	private Label livesLabel = new Label("Lives Left:");
	private Label livesValue = new Label("3");
	
	private Label baseLabel = new Label("Player Last Base Reached:");
	private Label baseValue = new Label("1");
	
	private Label energyLabel = new Label("Player Energy Level:");
	private Label energyValue = new Label("5000");
	
	private Label damageLabel = new Label("Player Damage Level:");
	private Label damageValue = new Label("0    ");
	
	private Label soundLabel = new Label("Sound:");
	private Label soundValue = new Label("OFF");
	
	public ScoreView() {
		//Set flow layout to center score
		setLayout(new FlowLayout(Component.CENTER));
		
		//Make text of all labels black
		timeLabel.getAllStyles().setFgColor(ColorUtil.BLACK);
		timeValue.getAllStyles().setFgColor(ColorUtil.BLACK);
		livesLabel.getAllStyles().setFgColor(ColorUtil.BLACK);
		livesValue.getAllStyles().setFgColor(ColorUtil.BLACK);
		baseLabel.getAllStyles().setFgColor(ColorUtil.BLACK);
		baseValue.getAllStyles().setFgColor(ColorUtil.BLACK);
		energyLabel.getAllStyles().setFgColor(ColorUtil.BLACK);
		energyValue.getAllStyles().setFgColor(ColorUtil.BLACK);
		damageLabel.getAllStyles().setFgColor(ColorUtil.BLACK);
		damageValue.getAllStyles().setFgColor(ColorUtil.BLACK);
		soundLabel.getAllStyles().setFgColor(ColorUtil.BLACK);
		soundValue.getAllStyles().setFgColor(ColorUtil.BLACK);
		
		//Add all of the labels to the container
		add(timeLabel);
		add(timeValue);
		add(livesLabel);
		add(livesValue);
		add(baseLabel);
		add(baseValue);
		add(energyLabel);
		add(energyValue);
		add(damageLabel);
		add(damageValue);
		add(soundLabel);
		add(soundValue);
	}
	
	@Override
	public void update(Observable observable, Object data) {
		GameWorld gw = (GameWorld) data; //cast data to GameWorld
		
		//Setting the text of the labels based on game world data 
		timeValue.setText(String.valueOf(gw.getTime()));
		livesValue.setText(String.valueOf(gw.getLives()));
		baseValue.setText(String.valueOf(gw.getRobot().getLastBaseReached()));
		energyValue.setText(String.valueOf(gw.getRobot().getEnergyLevel()));
		damageValue.setText(String.valueOf(gw.getRobot().getDamageLevel()));
		
		//Check if sound is on/off
		if(gw.getSound())
			soundValue.setText("ON");
		else
			soundValue.setText("OFF");
		
		repaint();
	}
}
