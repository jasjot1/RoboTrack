package com.mycompany.a4;


abstract class Fixed extends GameObject implements ISelectable{
	private boolean isSelected; //Store whether object is selected or not
	
	//Constructor for Fixed game objects
	public Fixed(int size, int color, float x, float y) {
		super(size, color, x, y);
	}

	@Override
	//A3: Can change position of fixed based on position command
	//public void setLocation(float x, float y) {
		//Fixed game objects are not allowed to change location
	//}
	
	//Getter for isSelected
	public boolean isSelected() {
		return isSelected;
	}
	
	//Setter for isSelected
	public void setSelected(boolean b){
		isSelected = b; 
	}
	
	
}
