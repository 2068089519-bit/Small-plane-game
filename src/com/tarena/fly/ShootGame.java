package com.tarena.fly;

import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ShootGame extends JPanel {
	public static final int WIDTH = 600;
	public static final int HEIGHT = 800;
	
	private int state;
	private static final int START = 0;
	private static final int RUNNING = 1;
	private static final int PAUSE = 2;
	private static final int GAME_OVER = 3;
	private static final int DIFFICULTY_SELECT = 4;

	private int score = 0;
	private int missedCount = 0;
	private Timer timer;
	private int intervel = 1000 / 100;
	
	private int gameTime = 30;
	private int timeCounter = 0;
	private int elapsedSeconds = 0;
	
	public static final int DIFFICULTY_EASY = 1;
	public static final int DIFFICULTY_MEDIUM = 2;
	public static final int DIFFICULTY_HARD = 3;
	private static int currentDifficulty = DIFFICULTY_MEDIUM;
	
	private static int enemyBaseSpeed = 2;
	private static int spawnInterval = 50;
	
	public static int getCurrentDifficulty() {
		return currentDifficulty;
	}
	
	public static int getEnemyBaseSpeed() {
		return enemyBaseSpeed;
	}

	public static BufferedImage background;
	public static BufferedImage start;
	public static BufferedImage airplane;
	public static BufferedImage bee;
	public static BufferedImage bullet;
	public static BufferedImage hero0;
	public static BufferedImage hero1;
	public static BufferedImage pause;
	public static BufferedImage gameover;
	public static BufferedImage meteor;
	public static BufferedImage ufo;
	public static BufferedImage satellite;
	public static BufferedImage rocket;
	public static BufferedImage spaceBg;

	private FlyingObject[] flyings = {};
	private Bullet[] bullets = {};
	private Hero hero = new Hero();

	static {
		try {
			background = ImageIO.read(ShootGame.class
					.getResource("background.png"));
			start = ImageIO.read(ShootGame.class.getResource("start.png"));
			airplane = ImageIO
					.read(ShootGame.class.getResource("airplane.png"));
			bee = ImageIO.read(ShootGame.class.getResource("bee.png"));
			bullet = ImageIO.read(ShootGame.class.getResource("bullet.png"));
			hero0 = ImageIO.read(ShootGame.class.getResource("hero0.png"));
			hero1 = ImageIO.read(ShootGame.class.getResource("hero1.png"));
			pause = ImageIO.read(ShootGame.class.getResource("pause.png"));
			gameover = ImageIO
					.read(ShootGame.class.getResource("gameover.png"));
			meteor = airplane;
			ufo = bee;
			satellite = airplane;
			rocket = bullet;
			spaceBg = background;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void paint(Graphics g) {
		drawSpaceBackground(g);
		paintHero(g);
		paintBullets(g);
		paintFlyingObjects(g);
		paintScore(g);
		paintState(g);
	}
	
	private void drawSpaceBackground(Graphics g) {
		g.setColor(new Color(10, 10, 40));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		Random rand = new Random(12345);
		for (int i = 0; i < 200; i++) {
			int x = rand.nextInt(WIDTH);
			int y = rand.nextInt(HEIGHT);
			int size = rand.nextInt(3) + 1;
			float brightness = rand.nextFloat() * 0.8f + 0.2f;
			g.setColor(new Color(brightness, brightness, brightness));
			g.fillOval(x, y, size, size);
		}
		
		for (int i = 0; i < 30; i++) {
			int x = rand.nextInt(WIDTH);
			int y = rand.nextInt(HEIGHT);
			float hue = rand.nextFloat();
			g.setColor(Color.getHSBColor(hue, 0.3f, 0.8f));
			g.fillOval(x, y, 2, 2);
		}
	}

	public void paintHero(Graphics g) {
		g.drawImage(hero.getImage(), hero.getX(), hero.getY(), null);
	}

	public void paintBullets(Graphics g) {
		for (int i = 0; i < bullets.length; i++) {
			Bullet b = bullets[i];
			g.setColor(new Color(255, 150, 50));
			g.fillRect(b.getX() - 3, b.getY(), 6, 15);
			g.setColor(new Color(255, 100, 0));
			g.fillRect(b.getX() - 2, b.getY() + 15, 4, 8);
			g.setColor(new Color(255, 200, 0));
			g.fillRect(b.getX() - 1, b.getY() + 18, 2, 5);
			g.setColor(Color.WHITE);
			g.fillRect(b.getX() - 1, b.getY() + 2, 2, 5);
		}
	}

	public void paintFlyingObjects(Graphics g) {
		for (int i = 0; i < flyings.length; i++) {
			FlyingObject f = flyings[i];
			if (f instanceof Meteor) {
				drawMeteor(g, (Meteor) f);
			} else if (f instanceof UFO) {
				drawUFO(g, (UFO) f);
			} else if (f instanceof Satellite) {
				drawSatellite(g, (Satellite) f);
			} else if (f instanceof Airplane) {
				drawEnemyPlane(g, (Airplane) f);
			} else if (f instanceof Bee) {
				drawRewardBee(g, (Bee) f);
			} else {
				g.drawImage(f.getImage(), f.getX(), f.getY(), null);
			}
		}
	}
	
	private void drawEnemyPlane(Graphics g, Airplane plane) {
		int x = plane.getX();
		int y = plane.getY();
		int w = plane.getWidth();
		int h = plane.getHeight();
		
		g.setColor(new Color(180, 50, 50));
		g.fillRect(x + w/4, y, w/2, h * 3/4);
		
		g.setColor(new Color(150, 40, 40));
		g.fillRect(x, y + h/4, w, h/3);
		
		g.setColor(new Color(200, 70, 70));
		g.fillRect(x + w/3, y - h/4, w/3, h/4);
		
		g.setColor(new Color(255, 200, 0));
		g.fillOval(x + w/2 - 3, y + h/2 - 3, 6, 6);
	}
	
	private void drawRewardBee(Graphics g, Bee bee) {
		int x = bee.getX();
		int y = bee.getY();
		int w = bee.getWidth();
		int h = bee.getHeight();
		
		g.setColor(new Color(255, 215, 0));
		g.fillOval(x, y, w, h);
		
		g.setColor(Color.BLACK);
		g.fillRect(x, y + h/3, w, 4);
		g.fillRect(x, y + h*2/3, w, 4);
		
		g.setColor(new Color(200, 200, 255, 150));
		g.fillOval(x - 8, y + h/4, 12, h/2);
		g.fillOval(x + w - 4, y + h/4, 12, h/2);
		
		if (bee.getType() == Award.LIFE) {
			g.setColor(Color.RED);
			g.fillOval(x + w/2 - 5, y + h/2 - 3, 5, 5);
			g.fillOval(x + w/2, y + h/2 - 3, 5, 5);
			g.fillRect(x + w/2 - 4, y + h/2, 8, 6);
		} else {
			g.setColor(Color.ORANGE);
			g.fillRect(x + w/2 - 1, y + h/4, 2, h/2);
			g.fillRect(x + w/4, y + h/2 - 1, w/2, 2);
		}
	}
	
	private void drawMeteor(Graphics g, Meteor meteor) {
		int x = meteor.getX();
		int y = meteor.getY();
		int w = meteor.getWidth();
		int h = meteor.getHeight();
		
		g.setColor(new Color(139, 90, 43));
		g.fillOval(x, y, w, h);
		
		g.setColor(new Color(101, 67, 33));
		g.fillOval(x + w/4, y + h/4, w/3, h/3);
		g.fillOval(x + w/2, y + h/2, w/4, h/4);
		
		g.setColor(new Color(255, 100, 50, 100));
		for (int i = 0; i < 5; i++) {
			g.fillOval(x - i * 3, y + h/2, 8 - i, 8 - i);
		}
	}
	
	private void drawUFO(Graphics g, UFO ufo) {
		int x = ufo.getX();
		int y = ufo.getY();
		int w = ufo.getWidth();
		int h = ufo.getHeight();
		
		g.setColor(new Color(180, 180, 200));
		g.fillOval(x, y + h/3, w, h/3);
		
		g.setColor(new Color(100, 200, 255, 150));
		g.fillOval(x + w/4, y, w/2, h/2);
		
		g.setColor(new Color(255, 50, 50));
		for (int i = 0; i < 5; i++) {
			g.fillOval(x + w/8 + i * w/5, y + h/2, 4, 4);
		}
		
		g.setColor(new Color(50, 255, 100, 100));
		g.fillRect(x + w/2 - 2, y + h, 4, 15);
	}
	
	private void drawSatellite(Graphics g, Satellite sat) {
		int x = sat.getX();
		int y = sat.getY();
		int w = sat.getWidth();
		int h = sat.getHeight();
		
		g.setColor(new Color(220, 220, 220));
		g.fillRect(x + w/4, y + h/4, w/2, h/2);
		
		g.setColor(new Color(50, 100, 200));
		g.fillRect(x, y + h/3, w/4, h/3);
		g.fillRect(x + w*3/4, y + h/3, w/4, h/3);
		
		g.setColor(Color.BLACK);
		for (int i = 0; i < 3; i++) {
			g.fillRect(x + 2, y + h/3 + i * 5, w/4 - 4, 2);
			g.fillRect(x + w*3/4 + 2, y + h/3 + i * 5, w/4 - 4, 2);
		}
		
		g.setColor(new Color(0, 255, 0));
		g.fillOval(x + w/2 - 3, y + h/2 - 3, 6, 6);
	}

	public void paintScore(Graphics g) {
		int x = 10;
		int y = 30;
		Font font = new Font("微软雅黑", Font.BOLD, 20);
		
		g.setColor(new Color(255, 215, 0));
		g.setFont(font);
		g.drawString("得分: " + score, x, y);
		
		y += 28;
		g.setColor(new Color(255, 100, 100));
		g.drawString("未拦截: " + missedCount, x, y);
		
		y += 28;
		g.setColor(new Color(100, 255, 100));
		g.drawString("生命: " + hero.getLife(), x, y);
		
		y += 28;
		g.setColor(new Color(100, 200, 255));
		String diffStr = "难度: ";
		if (currentDifficulty == DIFFICULTY_EASY) diffStr += "初级";
		else if (currentDifficulty == DIFFICULTY_MEDIUM) diffStr += "中级";
		else diffStr += "高级";
		g.drawString(diffStr, x, y);
		
		y += 28;
		g.setColor(new Color(255, 255, 100));
		int remaining = gameTime - elapsedSeconds;
		if (remaining < 0) remaining = 0;
		g.drawString("剩余时间: " + remaining + "秒", x, y);
		
		if (state == START) {
			g.setColor(new Color(255, 255, 255, 200));
			Font bigFont = new Font("微软雅黑", Font.BOLD, 28);
			g.setFont(bigFont);
			g.drawString("太空战机", WIDTH/2 - 80, 150);
			
			Font smallFont = new Font("微软雅黑", Font.PLAIN, 18);
			g.setFont(smallFont);
			g.setColor(new Color(200, 200, 200));
			g.drawString("用鼠标控制飞机移动", WIDTH/2 - 90, 220);
			g.drawString("按空格键暂停游戏", WIDTH/2 - 85, 250);
			g.drawString("游戏时长30秒", WIDTH/2 - 65, 280);
			
			g.setColor(new Color(255, 215, 0));
			g.drawString("点击选择难度开始游戏", WIDTH/2 - 100, 340);
			
			drawDifficultyButtons(g);
		}
	}
	
	private void drawDifficultyButtons(Graphics g) {
		int btnY = 380;
		int btnHeight = 50;
		int spacing = 30;
		
		Font btnFont = new Font("微软雅黑", Font.BOLD, 20);
		g.setFont(btnFont);
		
		g.setColor(new Color(100, 200, 100));
		g.fillRect(WIDTH/2 - 100, btnY, 200, btnHeight);
		g.setColor(Color.WHITE);
		g.drawRect(WIDTH/2 - 100, btnY, 200, btnHeight);
		g.setColor(Color.BLACK);
		g.drawString("初级 - 简单模式", WIDTH/2 - 75, btnY + 33);
		
		btnY += btnHeight + spacing;
		g.setColor(new Color(255, 180, 50));
		g.fillRect(WIDTH/2 - 100, btnY, 200, btnHeight);
		g.setColor(Color.WHITE);
		g.drawRect(WIDTH/2 - 100, btnY, 200, btnHeight);
		g.setColor(Color.BLACK);
		g.drawString("中级 - 普通模式", WIDTH/2 - 75, btnY + 33);
		
		btnY += btnHeight + spacing;
		g.setColor(new Color(255, 80, 80));
		g.fillRect(WIDTH/2 - 100, btnY, 200, btnHeight);
		g.setColor(Color.WHITE);
		g.drawRect(WIDTH/2 - 100, btnY, 200, btnHeight);
		g.setColor(Color.BLACK);
		g.drawString("高级 - 困难模式", WIDTH/2 - 75, btnY + 33);
	}

	public void paintState(Graphics g) {
		switch (state) {
		case START:
			break;
		case PAUSE:
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(0, 0, WIDTH, HEIGHT);
			
			g.setColor(new Color(255, 215, 0));
			Font pauseFont = new Font("微软雅黑", Font.BOLD, 36);
			g.setFont(pauseFont);
			g.drawString("游戏暂停", WIDTH/2 - 90, HEIGHT/2 - 20);
			
			Font smallFont = new Font("微软雅黑", Font.PLAIN, 18);
			g.setFont(smallFont);
			g.setColor(Color.WHITE);
			g.drawString("按空格键继续游戏", WIDTH/2 - 90, HEIGHT/2 + 30);
			break;
		case GAME_OVER:
			g.setColor(new Color(0, 0, 0, 180));
			g.fillRect(0, 0, WIDTH, HEIGHT);
			
			Font gameOverFont = new Font("微软雅黑", Font.BOLD, 40);
			g.setFont(gameOverFont);
			g.setColor(new Color(255, 80, 80));
			g.drawString("游戏结束", WIDTH/2 - 100, HEIGHT/2 - 80);
			
			Font resultFont = new Font("微软雅黑", Font.BOLD, 24);
			g.setFont(resultFont);
			g.setColor(new Color(255, 215, 0));
			g.drawString("最终得分: " + score, WIDTH/2 - 80, HEIGHT/2 - 20);
			
			g.setColor(new Color(255, 100, 100));
			g.drawString("未拦截数: " + missedCount, WIDTH/2 - 80, HEIGHT/2 + 20);
			
			Font smallFont = new Font("微软雅黑", Font.PLAIN, 18);
			g.setFont(smallFont);
			g.setColor(Color.WHITE);
			g.drawString("点击任意位置返回主菜单", WIDTH/2 - 110, HEIGHT/2 + 80);
			break;
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("太空战机");
		ShootGame game = new ShootGame();
		frame.add(game);
		frame.setSize(WIDTH, HEIGHT);
		frame.setAlwaysOnTop(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		game.action();
	}

	public void action() {
		MouseAdapter l = new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (state == RUNNING) {
					int x = e.getX();
					int y = e.getY();
					hero.moveTo(x, y);
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				int mouseX = e.getX();
				int mouseY = e.getY();
				
				if (state == START) {
					int btnY = 380;
					int btnHeight = 50;
					int spacing = 30;
					
					if (mouseX >= WIDTH/2 - 100 && mouseX <= WIDTH/2 + 100) {
						if (mouseY >= btnY && mouseY <= btnY + btnHeight) {
							setDifficulty(DIFFICULTY_EASY);
							startGame();
						}
						btnY += btnHeight + spacing;
						if (mouseY >= btnY && mouseY <= btnY + btnHeight) {
							setDifficulty(DIFFICULTY_MEDIUM);
							startGame();
						}
						btnY += btnHeight + spacing;
						if (mouseY >= btnY && mouseY <= btnY + btnHeight) {
							setDifficulty(DIFFICULTY_HARD);
							startGame();
						}
					}
				} else if (state == GAME_OVER) {
					showGameOverDialog();
				}
			}
		};
		this.addMouseListener(l);
		this.addMouseMotionListener(l);
		
		this.setFocusable(true);
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					if (state == RUNNING) {
						state = PAUSE;
					} else if (state == PAUSE) {
						state = RUNNING;
					}
				}
			}
		});

		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (state == RUNNING) {
					timeCounter++;
					if (timeCounter % 100 == 0) {
						elapsedSeconds++;
						if (elapsedSeconds >= gameTime) {
							state = GAME_OVER;
							showGameOverDialog();
						}
					}
					
					enterAction();
					stepAction();
					shootAction();
					bangAction();
					outOfBoundsAction();
					checkGameOverAction();
				}
				repaint();
			}

		}, intervel, intervel);
	}
	
	private void startGame() {
		flyings = new FlyingObject[0];
		bullets = new Bullet[0];
		hero = new Hero();
		score = 0;
		missedCount = 0;
		elapsedSeconds = 0;
		timeCounter = 0;
		state = RUNNING;
	}
	
	private void setDifficulty(int diff) {
		currentDifficulty = diff;
		switch (diff) {
			case DIFFICULTY_EASY:
				enemyBaseSpeed = 1;
				spawnInterval = 70;
				break;
			case DIFFICULTY_MEDIUM:
				enemyBaseSpeed = 2;
				spawnInterval = 50;
				break;
			case DIFFICULTY_HARD:
				enemyBaseSpeed = 4;
				spawnInterval = 30;
				break;
		}
	}
	
	private void showGameOverDialog() {
		String message = "游戏结束！\n\n" +
		                 "最终得分: " + score + "\n" +
		                 "未拦截数: " + missedCount + "\n\n" +
		                 "是否再玩一次？";
		
		int option = JOptionPane.showConfirmDialog(this, message, "游戏结束", 
		                                           JOptionPane.YES_NO_OPTION,
		                                           JOptionPane.INFORMATION_MESSAGE);
		
		if (option == JOptionPane.YES_OPTION) {
			resetToStart();
		}
	}
	
	private void resetToStart() {
		flyings = new FlyingObject[0];
		bullets = new Bullet[0];
		hero = new Hero();
		score = 0;
		missedCount = 0;
		elapsedSeconds = 0;
		timeCounter = 0;
		state = START;
	}

	int flyEnteredIndex = 0;

	public void enterAction() {
		flyEnteredIndex++;
		if (flyEnteredIndex % spawnInterval == 0) {
			FlyingObject obj = nextOne();
			flyings = Arrays.copyOf(flyings, flyings.length + 1);
			flyings[flyings.length - 1] = obj;
		}
	}

	public void stepAction() {
		for (int i = 0; i < flyings.length; i++) {
			FlyingObject f = flyings[i];
			f.step();
		}

		for (int i = 0; i < bullets.length; i++) {
			Bullet b = bullets[i];
			b.step();
		}
		hero.step();
	}

	public void flyingStepAction() {
		for (int i = 0; i < flyings.length; i++) {
			FlyingObject f = flyings[i];
			f.step();
		}
	}

	int shootIndex = 0;

	public void shootAction() {
		shootIndex++;
		if (shootIndex % 30 == 0) {
			Bullet[] bs = hero.shoot();
			bullets = Arrays.copyOf(bullets, bullets.length + bs.length);
			System.arraycopy(bs, 0, bullets, bullets.length - bs.length,
					bs.length);
		}
	}

	public void bangAction() {
		for (int i = 0; i < bullets.length; i++) {
			Bullet b = bullets[i];
			bang(b);
		}
	}

	public void outOfBoundsAction() {
		int index = 0;
		FlyingObject[] flyingLives = new FlyingObject[flyings.length];
		for (int i = 0; i < flyings.length; i++) {
			FlyingObject f = flyings[i];
			if (!f.outOfBounds()) {
				flyingLives[index++] = f;
			} else {
				if (f instanceof Enemy) {
					missedCount++;
				}
			}
		}
		flyings = Arrays.copyOf(flyingLives, index);

		index = 0;
		Bullet[] bulletLives = new Bullet[bullets.length];
		for (int i = 0; i < bullets.length; i++) {
			Bullet b = bullets[i];
			if (!b.outOfBounds()) {
				bulletLives[index++] = b;
			}
		}
		bullets = Arrays.copyOf(bulletLives, index);
	}

	public void checkGameOverAction() {
		if (isGameOver() == true) {
			state = GAME_OVER;
			showGameOverDialog();
		}
	}

	public boolean isGameOver() {
		
		for (int i = 0; i < flyings.length; i++) {
			int index = -1;
			FlyingObject obj = flyings[i];
			if (hero.hit(obj)) {
				hero.subtractLife();
				hero.setDoubleFire(0);
				index = i;
			}
			if (index != -1) {
				FlyingObject t = flyings[index];
				flyings[index] = flyings[flyings.length - 1];
				flyings[flyings.length - 1] = t;

				flyings = Arrays.copyOf(flyings, flyings.length - 1);
			}
		}
		
		return hero.getLife() <= 0;
	}

	public void bang(Bullet bullet) {
		int index = -1;
		for (int i = 0; i < flyings.length; i++) {
			FlyingObject obj = flyings[i];
			if (obj.shootBy(bullet)) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			FlyingObject one = flyings[index];

			FlyingObject temp = flyings[index];
			flyings[index] = flyings[flyings.length - 1];
			flyings[flyings.length - 1] = temp;

			flyings = Arrays.copyOf(flyings, flyings.length - 1);

			if (one instanceof Enemy) {
				Enemy e = (Enemy) one;
				score += e.getScore();
			} else {
				Award a = (Award) one;
				int type = a.getType();
				switch (type) {
				case Award.DOUBLE_FIRE:
					hero.addDoubleFire();
					break;
				case Award.LIFE:
					hero.addLife();
					break;
				}
			}
		}
	}

	public static FlyingObject nextOne() {
		Random random = new Random();
		int type = random.nextInt(100);
		if (type < 10) {
			return new Bee();
		} else if (type < 40) {
			return new Airplane();
		} else if (type < 70) {
			return new Meteor();
		} else if (type < 85) {
			return new UFO();
		} else {
			return new Satellite();
		}
	}

}
