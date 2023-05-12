package com.mycompany.a4;

import com.codename1.charts.models.Point;
import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;

import java.util.Random;

public class ShockWave extends Moveable {
    private Point[] controlPoints;
    private int maxLifetime;
    private int lifetime;

    public ShockWave(int size, int color, float x, float y, int worldWidth, int worldHeight) {
        super(size, color, x, y);

        Random random = new Random();
        controlPoints = new Point[4];
        for (int i = 0; i < 4; i++) {
            float controlX = x + ((i % 2 == 0) ? -size : size) * 2;
            float controlY = y + ((i < 2) ? -size : size) * 2;
            controlPoints[i] = new Point(controlX, controlY);
        }

        setHeading(random.nextInt(360));
        setSpeed(100); 

        maxLifetime = 8 * (worldWidth / getSpeed());
        lifetime = 0;
    }
    
    private void drawBezierCurve(Point[] controlPoints, Graphics g, Point pCmpRelPrnt) {
        if (straightEnough(controlPoints)) {
            g.drawLine((int) (pCmpRelPrnt.getX() + controlPoints[0].getX()), (int) (pCmpRelPrnt.getY() + controlPoints[0].getY()), (int) (pCmpRelPrnt.getX() + controlPoints[3].getX()), (int) (pCmpRelPrnt.getY() + controlPoints[3].getY()));
        } else {
            Point[] leftSubVector = new Point[4];
            Point[] rightSubVector = new Point[4];
            subdivideCurve(controlPoints, leftSubVector, rightSubVector);
            drawBezierCurve(leftSubVector, g, pCmpRelPrnt);
            drawBezierCurve(rightSubVector, g, pCmpRelPrnt);
        }
    }

    private void subdivideCurve(Point[] q, Point[] r, Point[] s) {
        r[0] = q[0];
        r[1] = addPoints(q[0], q[1]);
        r[1] = mulPoint(r[1], 0.5f);
        r[2] = addPoints(mulPoint(r[1], 0.5f), mulPoint(addPoints(q[1], q[2]), 0.25f));
        s[3] = q[3];
        s[2] = addPoints(q[2], q[3]);
        s[2] = mulPoint(s[2], 0.5f);
        s[1] = addPoints(mulPoint(addPoints(q[1], q[2]), 0.25f), mulPoint(s[2], 0.5f));
        r[3] = addPoints(r[2], s[1]);
        r[3] = mulPoint(r[3], 0.5f);
        s[0] = r[3];
    }

    private boolean straightEnough(Point[] controlPoints) {
        double d1 = distancePoints(controlPoints[0], controlPoints[1]) + distancePoints(controlPoints[1], controlPoints[2]) + distancePoints(controlPoints[2], controlPoints[3]);
        double d2 = distancePoints(controlPoints[0], controlPoints[3]);
        double epsilon = 0.001;

        return Math.abs(d1 - d2) < epsilon;
    }
    
    @Override
    public void draw(Graphics g, Point pCmpRelPrnt, Point pCmpRelScrn) {
    	g.setColor(getColor());
    	
    	
		Transform gXform = Transform.makeIdentity();
	    g.getTransform(gXform);
		Transform original = gXform.copy();
		
		gXform.translate(pCmpRelScrn.getX(),pCmpRelScrn.getY());
		gXform.translate(getMyTranslation().getTranslateX(), getMyTranslation().getTranslateY());
		gXform.concatenate(getMyRotation());
		gXform.scale(getMyScale().getScaleX(), getMyScale().getScaleY());
		gXform.translate(-pCmpRelScrn.getX(),-pCmpRelScrn.getY());
		g.setTransform(gXform);
		
    	drawBezierCurve(controlPoints, g, pCmpRelPrnt);
    	
    	g.setTransform(original); //Restore saved graphics transform
    }
    
    public boolean reachedMaxLifetime() {
    	if (lifetime >= maxLifetime) {
    		return true;
    	}
    	else 
    		return false;
    }
    
    private static Point addPoints(Point p1, Point p2) {
        float newX = p1.getX() + p2.getX();
        float newY = p1.getY() + p2.getY();
        return new Point(newX, newY);
    }

    private static Point mulPoint(Point p, float scalar) {
        float newX = p.getX() * scalar;
        float newY = p.getY() * scalar;
        return new Point(newX, newY);
    }

    private static double distancePoints(Point p1, Point p2) {
        float deltaX = p2.getX() - p1.getX();
        float deltaY = p2.getY() - p1.getY();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    
    @Override
	public void move(int timerRate, int mapWidth, int mapHeight) {
    	
		double deltaX, deltaY; //Stores calculated values of x/y

		//Calculate deltaX/Y with angle converted to radians
		deltaX = (Math.cos(Math.toRadians(90 - getHeading())) * getSpeed() * timerRate) / 1000.0;
	    deltaY = (Math.sin(Math.toRadians(90 - getHeading())) * getSpeed() * timerRate) / 1000.0;

		
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
		
		deltaX = newX - this.getX();
		deltaY = newY - this.getY();

		this.translate((float)deltaX, (float)deltaY);

		for (int i = 0; i < controlPoints.length; i++) {
		    float controlX = (float) (controlPoints[i].getX() + deltaX);
		    float controlY = (float) (controlPoints[i].getY() + deltaY);
		    controlPoints[i] = new Point(controlX, controlY);
		}
		
		lifetime++;

	}

    @Override
    public boolean collidesWith(GameObject otherObject) {
        return false;
    }

    @Override
    public void handleCollision(GameObject otherObject, GameWorld gw) {
    	
    }
}
