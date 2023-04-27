package com.mycompany.a4;

import java.io.InputStream;

import com.codename1.media.Media;
import com.codename1.media.MediaManager;
import com.codename1.ui.Display;

public class BGSound implements Runnable{
	private Media m;
	
	public BGSound(String fileName){
	try {
		InputStream is = Display.getInstance().getResourceAsStream(getClass(), "/" + fileName);
		m = MediaManager.createMedia( is , "audio/mp3", this);
		} catch (Exception err) { err.printStackTrace(); }
	}
	
	//Play the sound
	public void play() {
		m.play();
	}
	
	//Pause the sound
	public void pause() {
		m.pause();
	}
	
	public void run() {
		m.setTime(0);
		play();
	}
}