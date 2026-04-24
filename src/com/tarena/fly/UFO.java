package com.tarena.fly;

import java.util.Random;

public class UFO extends FlyingObject implements Enemy {
	private int xSpeed = 1;
	private int ySpeed;
	
	public UFO() {
		this.image = ShootGame.bee;
		width = 55;
		height = 35;
		y = -height;
		Random rand = new Random();
		x = rand.nextInt(ShootGame.WIDTH - width);
		ySpeed = calculateSpeed();
	}
	
	private int calculateSpeed() {
		int baseSpeed = ShootGame.getEnemyBaseSpeed();
		Random rand = new Random();
		return baseSpeed + rand.nextInt(2);
	}
	
	@Override
	public int getScore() {
		return 12;
	}

	@Override
	public boolean outOfBounds() {
		return y > ShootGame.HEIGHT;
	}

	@Override
	public void step() {
		x += xSpeed;
		y += ySpeed;
		if (x > ShootGame.WIDTH - width) {
			xSpeed = -1;
		}
		if (x < 0) {
			xSpeed = 1;
		}
	}
}
