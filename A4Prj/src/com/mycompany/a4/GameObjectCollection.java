package com.mycompany.a4;

import java.util.ArrayList;

public class GameObjectCollection implements ICollection{
	private ArrayList<GameObject> collection; //Collection of GameObjects stored in array list
	
	//Constructor for GameObjectCollection
	public GameObjectCollection() {
		//Create new array list
		collection = new ArrayList<GameObject>();
	}
	
	//Add method to add to collection
	public void add(GameObject newObject) {
		collection.add(newObject);
	}
	
	//Getter for objects
	public ArrayList<GameObject> getObjects() {
		return collection;
	}
	
	//Gets the iterator for objects
	public IIterator getIterator() {
		return new GameObjectIterator();
	}
	
	//Private class for GameObjectIterator
	private class GameObjectIterator implements IIterator{
		private int currElementIndex; //Stores current index for elements
		
		public GameObjectIterator() {
			currElementIndex = -1; //Initialize to -1
		}
		
		//Checking whether there are more elements to be processed in the collection
		public boolean hasNext() {
			if (collection.size() <= 0) return false;
			
			if (currElementIndex == collection.size() - 1) return false;
			
			return true;
		}
		
		//Returning the next element to be processed from the collection
		public GameObject getNext ( ) {
			currElementIndex ++ ; //Increment index
			return(collection.get(currElementIndex));
		}
		
	}
}

