package com.mkbgames.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.mkbgames.main.Game;
import com.mkbgames.main.Sound;
import com.mkbgames.world.Camera;
import com.mkbgames.world.World;

public class Enemy extends Entity {

	private int frames = 0, maxFrames = 10, index = 0, maxIndex = 1;
	private double speed = 0.7;

	private int maskx = 1, masky = 1, maskw = 16, maskh = 16;

	private BufferedImage[] sprites;

	private int life = 5;

	private boolean isDamaged = false;
	private int damageFrames = 10, damageCurrent = 0;

	public Enemy(int x, int y, int width, int height, BufferedImage[] sprite) {
		super(x, y, width, height, null);
		sprites = new BufferedImage[2];
		sprites[0] = Game.spritesheet.getSprite(112, 16, 16, 16);
		sprites[1] = Game.spritesheet.getSprite(112 + 16, 16, 16, 16);
	}

	public void tick() {
		if (this.isColiddingWithPlayer() == false) {
			if ((int) x < Game.player.getX() && World.isFree((int) (x + speed), this.getY())
					&& !isColidding((int) (x + speed), this.getY())) {
				x += speed;
			} else if ((int) x > Game.player.getX() && World.isFree((int) (x - speed), this.getY())
					&& !isColidding((int) (x - speed), this.getY())) {
				x -= speed;
			}

			if ((int) y < Game.player.getY() && World.isFree(this.getX(), (int) (y + speed))
					&& !isColidding(this.getX(), (int) (y + speed))) {
				y += speed;
			} else if ((int) y > Game.player.getY() && World.isFree(this.getX(), (int) (y - speed))
					&& !isColidding(this.getX(), (int) (y - speed))) {
				y -= speed;
			}
		} else {
			if (Game.random.nextInt(100) < 10) {
				Sound.hitHurt.play();
				Game.player.life -= Game.random.nextInt(3);
				Game.player.isDamaged = true;
			}
		}
		frames++;
		if (frames == maxFrames) {
			frames = 0;
			index++;
			if (index > maxIndex) {
				index = 0;
			}
		}

		coliddingBullet();
		if (life <= 0) {
			destroySelf();
		}
		if(isDamaged) {
			this.damageCurrent++;
			if(this.damageCurrent == this.damageFrames) {
				this.damageCurrent = 5 ;
				this.isDamaged = false;
			}
		}

	}

	public void destroySelf() {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}

	public void coliddingBullet() {
		for (int i = 0; i < Game.bullets.size(); i++) {
			Entity e = Game.bullets.get(i);
			if (e instanceof BulletShoot) {
				if (Entity.isColidding(this, e)) {
					isDamaged = true;
					life--;
					Game.bullets.remove(i);
					return;
				}
			}
		}
	}

	public boolean isColiddingWithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskx, this.getY() + masky, mwidth, mheight);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16, 16);

		return enemyCurrent.intersects(player);
	}

	public boolean isColidding(int xnext, int ynext) {
		Rectangle enemyCurrent = new Rectangle(xnext + maskx, ynext + masky, maskw, maskh);
		for (int i = 0; i < Game.enemies.size(); i++) {
			Enemy e = Game.enemies.get(i);
			if (e == this) {
				continue;
			}
			Rectangle targetEnemy = new Rectangle(e.getX() + maskx, e.getY() + masky, maskw, maskh);
			if (enemyCurrent.intersects(targetEnemy)) {
				return true;
			}
		}
		return false;
	}

	public void render(Graphics g) {
		if (!isDamaged) {
			g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		} else {
			g.drawImage(Entity.ENEMY_FEEDBACK, this.getX() - Camera.x, this.getY() - Camera.y, null);
		}
	}

}
