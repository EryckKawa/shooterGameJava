package com.mkbgames.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import com.mkbgames.entities.Entity;
import com.mkbgames.entities.Player;
import com.mkbgames.graficos.Spritesheet;
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
	private final int WIDTH = 240;
	private final int HEIGHT = 160;
	private final int SCALE = 4;

	private BufferedImage image;

	public List<Entity> entities;
	public static Spritesheet spritesheet;
	
	public static World world;
	
	private Player player;

	public Game() {
		addKeyListener(this);
		this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		initFrame();
		// Inicializando objetos
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		spritesheet = new Spritesheet("/spritesheet.png");
		world = new World("/map.png");
		player = new Player(0, 0, 16, 16, spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
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
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.tick();
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

		/***/
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
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
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;
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
