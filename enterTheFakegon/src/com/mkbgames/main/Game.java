package com.mkbgames.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import com.mkbgames.entities.BulletShoot;
import com.mkbgames.entities.Enemy;
import com.mkbgames.entities.Entity;
import com.mkbgames.entities.Player;
import com.mkbgames.graficos.Spritesheet;
import com.mkbgames.graficos.UI;
import com.mkbgames.world.World;

public class Game extends Canvas implements Runnable, KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Como o game roda em Looping utilize o debug mode para testar alterações de
	// maneira rápida no jogo
	// PS: Lembre de dar um Control + S para salvar as alterações para ocorrer a
	// mudança

	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	public static final int WIDTH = 240;
	public static final int HEIGHT = 160;
	public static final int SCALE = 4;

	private int CUR_LEVEL = 1, MAX_LEVEL = 3;
	private BufferedImage image;

	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<BulletShoot> bullets;
	public static Spritesheet spritesheet;

	public static World world;
	public static Player player;
	public static Random random;
	public UI ui;
	public Menu menu;
	//public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("pixelfont.ttf");
	//public Font newFont;
	
	
	public static String gameState = "MENU";
	private boolean showMessageGameOver = true;
	private int frameGameOver = 0;
	private boolean restartGame = false;
	public boolean saveGame = false;

	public Game() {
		Sound.musicBackground.loop();
		random = new Random();
		addKeyListener(this);
		this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		initFrame();
		// Inicializando objetos
		ui = new UI();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		menu = new Menu();
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<BulletShoot>();
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0, 0, 16, 16, spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		world = new World("/level1.png");
		
		//try {
		//	newFont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(16f);
		//} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
	}

	// Inicia a Janela
	public void initFrame() {
		frame = new JFrame("Enter the Fakegon");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}

	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}

	public void tick() {
		if (gameState == "NORMAL") {
			if (this.saveGame) {
				this.saveGame = false;
				String[] opt1 = { "level", "vida" };
				int[] opt2 = { this.CUR_LEVEL, (int) player.life };
				Menu.saveGame(opt1, opt2, 10);
				System.out.println("Jogo salvo");
			}
			this.restartGame = false;
			for (int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.tick();
			}

			for (int i = 0; i < bullets.size(); i++) {
				bullets.get(i).tick();
			}

			if (enemies.size() == 0) {
				CUR_LEVEL++;
				if (CUR_LEVEL > MAX_LEVEL) {
					CUR_LEVEL = 1;
				}
				String newWorld = "level" + CUR_LEVEL + ".png";
				World.restartGame(newWorld);
			}
		} else if (gameState == "GAME_OVER") {
			this.frameGameOver++;
			if (this.frameGameOver == 30) {
				this.frameGameOver = 0;
				if (this.showMessageGameOver) {
					this.showMessageGameOver = false;
				} else {
					this.showMessageGameOver = true;
				}
			}
			if (restartGame) {
				this.restartGame = false;
				this.gameState = "NORMAL";
				CUR_LEVEL = 1;
				String newWorld = "level" + CUR_LEVEL + ".png";
				World.restartGame(newWorld);
			}
		} else if (gameState == "MENU") {
			menu.tick();
		}
	}

	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, WIDTH, HEIGHT);

		/* Renderização do Jogo */
		// Graphics2D g2 = (Graphics2D) g;
		world.render(g);
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).render(g);
		}
		ui.render(g);
		/***/
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		if (gameState == "GAME_OVER") {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0, 0, 0, 100));
			g2.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
			g.setFont(new Font("arial", Font.BOLD, 40));
			g.setColor(Color.WHITE);
			g.drawString("Game Over!", (WIDTH * SCALE) / 2 - 120, (HEIGHT * SCALE) / 2 - 10);
			if (showMessageGameOver) {
				g.drawString(">> Pressione Enter para Reiniciar << ", (WIDTH * SCALE) / 2 - 350,
						(HEIGHT * SCALE) / 2 + 50);
			}
		} else if (gameState == "MENU") {
			menu.render(g);
		}
		bs.show();

	}

	public void run() {
		// Define o tempo em nanossegundos da última atualização
		long lastTime = System.nanoTime();
		// Define a quantidade de atualizações por segundo que o jogo deve ter
		double amountOfTicks = 60.0;
		// Define o intervalo de tempo em nanossegundos entre cada atualização
		double ns = 1000000000 / amountOfTicks;
		// Define a quantidade de tempo que passou sem atualizar
		double delta = 0;
		// Define o número de quadros renderizados desde a última atualização de FPS
		int frames = 0;
		// Define o momento atual em milissegundos
		double timer = System.currentTimeMillis();
		requestFocus();

		// Loop principal do jogo
		while (isRunning) {
			// Define o tempo atual em nanossegundos
			long now = System.nanoTime();
			// Calcula a quantidade de tempo que passou desde a última atualização
			delta += (now - lastTime) / ns;
			// Atualiza o tempo da última atualização para o momento atual
			lastTime = now;

			// Verifica se é hora de atualizar e renderizar o jogo
			if (delta >= 1) {
				// Atualiza o jogo
				tick();
				// Renderiza o jogo
				render();
				// Incrementa o contador de quadros
				frames++;
				// Reduz a quantidade de tempo que passou sem atualizar
				delta--;
			}

			// Verifica se já passou um segundo desde a última atualização de FPS
			if (System.currentTimeMillis() - timer >= 1000) {
				// Exibe a quantidade de quadros renderizados nos últimos segundos
				System.out.println("FPS: " + frames);
				// Reinicia o contador de quadros
				frames = 0;
				// Atualiza o momento da última atualização de FPS
				timer += 1000;
			}
		}
		stop();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;

		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}

		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			player.up = true;
			if (gameState == "MENU") {
				menu.up = true;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;
			if (gameState == "MENU") {
				menu.down = true;
			}
		}

		if (e.getKeyCode() == KeyEvent.VK_Z) {
			player.shoot = true;
		}

		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.restartGame = true;
			if (gameState == "MENU") {
				menu.enter = true;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			gameState = "MENU";
			menu.pause = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			if (gameState == "NORMAL") {
				this.saveGame = true;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;

		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
		}

		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;
		}

	}

}
