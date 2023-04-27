package com.mycompany.a4;

public interface ICollider {
	boolean collidesWith(GameObject otherObject);
	void handleCollision(GameObject otherObject, GameWorld gw);

}
