package com.tarena.fly;

import java.util.Random;

public class Meteor extends FlyingObject implements Enemy {
	private int speed;
	
	public Meteor() {
		this.image = ShootGame.airplane;
		width = 45;
		height = 45;
		y = -height;
		Random rand = new Random();
		x = rand.nextInt(ShootGame.WIDTH - width);
		speed = calculateSpeed();
	}
	
	private int calculateSpeed() {
		int baseSpeed = ShootGame.getEnemyBaseSpeed();
		Random rand = new Random();
		return baseSpeed + rand.nextInt(2);
	}
	
	@Override
	public int getScore() {
		return 8;
	}

	@Override
	public boolean outOfBounds() {
		return y > ShootGame.HEIGHT;
	}

	@Override
	public void step() {
		y += speed;
	}
}
