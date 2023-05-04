package com.mkbgames.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.mkbgames.graficos.Spritesheet;
import com.mkbgames.main.Game;
import com.mkbgames.main.Sound;
import com.mkbgames.world.Camera;
import com.mkbgames.world.World;

public class Player extends Entity {

	public boolean right, up, left, down;
	public int right_dir = 0, left_dir = 1, up_dir = 2;
	public int dir = right_dir;
	public double speed = 1.5;

	private int frames = 0, maxFrames = 10, index = 0, maxIndex = 3;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	private BufferedImage damagePlayer;

	private boolean hasGun = false;

	public static int ammo = 0;

	public boolean isDamaged = false;
	private int damageFrames = 0;

	public boolean shoot = false;

	public double life = 100, maxlife = 100;

	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);

		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		damagePlayer = Game.spritesheet.getSprite(0, 16, 16, 16);
		for (int i = 0; i < 4; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i * 16), 0, 16, 16);
		}
		for (int i = 0; i < 4; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i * 16), 16, 16, 16);
		}

	}

	public void tick() {
		moved = false;
		if (right && World.isFree((int) (x + speed), this.getY())) {
			moved = true;
			dir = right_dir;
			x += speed;
		} else if (left && World.isFree((int) (x - speed), this.getY())) {
			moved = true;
			dir = left_dir;
			x -= speed;
		}
		if (up && World.isFree(this.getX(), (int) (y - speed))) {
			moved = true;
			y -= speed;
		} else if (down && World.isFree(this.getX(), (int) (y + speed))) {
			moved = true;
			y += speed;
		}
		if (moved) {
			frames++;
			if (frames == maxFrames) {
				frames = 0;
				index++;
				if (index > maxIndex) {
					index = 0;
				}
			}
		}

		checkCollisionLifePack();
		checkCollisionBullet();
		checkCollisionGun();

		if (isDamaged) {
			this.damageFrames++;
			if (this.damageFrames == 5) {
				this.damageFrames = 0;
				isDamaged = false;
			}
		}
		if (shoot) {
			shoot = false;
			if (hasGun && ammo > 0) {
				Sound.gunShoot.play();
				ammo--;
				int dx = 0;
				int px = 0;
				int py = 7;
				if (dir == right_dir) {
					px = 12;
					dx = 1;
				} else {
					px = 0;
					dx = -1;
				}
				BulletShoot bullet = new BulletShoot(this.getX() + px, this.getY() + py, 3, 3, null, dx, 0);
				Game.bullets.add(bullet);
			}
		}
		if (life <= 0) {
			life = 0;
			Game.gameState = "GAME_OVER";
		}

		updateCamera();
	}

	public void updateCamera() {
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH / 2), 0, World.WIDTH * 16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT / 2), 0, World.HEIGHT * 16 - Game.HEIGHT);
	}
	
	
	public void checkCollisionGun() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if (atual instanceof Weapon) {
				if (Entity.isColidding(this, atual)) {
					hasGun = true;
					Game.entities.remove(i);
					return;
				}
			}
		}
	}

	public void checkCollisionBullet() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if (atual instanceof Bullet) {
				if (Entity.isColidding(this, atual)) {
					ammo += 50;
					Game.entities.remove(i);
					return;
				}
			}
		}
	}

	public void checkCollisionLifePack() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if (atual instanceof Lifepack) {
				if (Entity.isColidding(this, atual)) {
					life += 10;
					if (life >= 100)
						life = 100;
					Game.entities.remove(i);
					return;
				}
			}
		}
	}

	public void render(Graphics g) {
		if (!isDamaged) {
			if (dir == right_dir) {
				g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				if (hasGun) {
					g.drawImage(Entity.GUN_RIGHT, (this.getX() + 2) - Camera.x, (this.getY() + 1) - Camera.y, null);
				}
			} else if (dir == left_dir) {
				g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				if (hasGun) {
					g.drawImage(Entity.GUN_LEFT, (this.getX() - 2) - Camera.x, (this.getY() + 1) - Camera.y, null);
				}
			}
		} else {
			g.drawImage(damagePlayer, this.getX() - Camera.x, this.getY() - Camera.y, null);
			if(hasGun) {
				if(dir == left_dir) {
					g.drawImage(Entity.GUN_DAMEGE_LEFT, (this.getX() - 2) - Camera.x, (this.getY() + 1) - Camera.y, null);
				} else {
					g.drawImage(Entity.GUN_DAMAGE_RIGHT, (this.getX() + 2) - Camera.x, (this.getY() + 1) - Camera.y, null);
				}
			}
		}
	}
}
