package com.tarena.fly;

import java.util.Random;

public class Satellite extends FlyingObject implements Enemy {
	private int speed;
	
	public Satellite() {
		this.image = ShootGame.airplane;
		width = 50;
		height = 40;
		y = -height;
		Random rand = new Random();
		x = rand.nextInt(ShootGame.WIDTH - width);
		speed = calculateSpeed();
	}
	
	private int calculateSpeed() {
		int baseSpeed = ShootGame.getEnemyBaseSpeed() + 1;
		Random rand = new Random();
		return baseSpeed + rand.nextInt(2);
	}
	
	@Override
	public int getScore() {
		return 15;
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
