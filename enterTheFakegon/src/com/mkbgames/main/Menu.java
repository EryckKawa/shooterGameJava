package com.mkbgames.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Scanner;

import com.mkbgames.world.World;

public class Menu {

	public String options[] = { "novo jogo", "carregar", "sair" };

	public int currentOption = 0;
	public int maxOption = options.length - 1;
	public boolean up, down, enter = false;
	public static boolean pause = false;
	public static boolean saveExists = false;
	public static boolean saveGame = false;

	public void tick() {
		File file = new File("save.txt");
		if (file.exists()) {
			saveExists = true;
		} else {
			saveExists = false;
		}

		if (up) {
			up = false;
			currentOption--;
			if (currentOption < 0) {
				currentOption = maxOption;
			}
		}
		if (down) {
			down = false;
			currentOption++;
			if (currentOption > maxOption) {
				currentOption = 0;
			}
		}
		if (enter) {
			enter = false;
			if (options[currentOption] == "novo jogo" || options[currentOption] == "continuar") {
				Game.gameState = "NORMAL";
				pause = false;
				file = new File("save.txt");
				file.delete();
			} else if (options[currentOption] == "carregar") {
				file = new File("save.txt");
				if (file.exists()) {
					String saver = loadGame(10);
					applySave(saver);
				}
			} else if (options[currentOption] == "sair") {
				System.exit(1);
			}

		}
	}

	public static void applySave(String str) {
		String[] spl = str.split("/");
		for (int i = 0; i < spl.length; i++) {
			String[] spl2 = spl[i].split(":");
			switch (spl2[0]) 
			{
				case "level":
					World.restartGame("level" + spl2[1] + ".png");
					Game.gameState = "NORMAL";
					pause = false;
					break;
				case "vida":
					Game.player.life = Integer.parseInt(spl2[1]);
					break;
			}
		}
	}

	public static String loadGame(int encode) {
		String line = "";
		File file = new File("save.txt");
		if (file.exists()) {
			try {
				String singleLine = null;
				BufferedReader reader = new BufferedReader(new FileReader("save.txt"));
				try {
					while ((singleLine = reader.readLine()) != null) {
						String[] trans = singleLine.split(":");
						char[] val = trans[1].toCharArray();
						trans[1] = "";
						for (int i = 0; i < val.length; i++) {
							val[i] -= encode;
							trans[1] += val[i];
						}
						line += trans[0];
						line += ":";
						line += trans[1];
						line += "/";
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		return line;
	}

	public static void saveGame(String[] val1, int[] val2, int encode) {
		BufferedWriter write = null;
		try {
			write = new BufferedWriter(new FileWriter("save.txt"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < val1.length; i++) {
			String current = val1[i];
			current += ":";
			char[] value = Integer.toString(val2[i]).toCharArray();
			for (int j = 0; j < value.length; j++) {
				value[j] += encode;
				current += value[j];
			}
			try {
				write.write(current);
				if (i < val1.length - 1) {
					write.newLine();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				write.flush();
				write.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public void render(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(0, 0, 0, 100));
		g2.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 40));
		g.drawString(">>Enter the Fakegon<<", (Game.WIDTH * Game.SCALE) / 2 - 220,
				(Game.HEIGHT * Game.SCALE) / 2 - 180);
		g.setFont(new Font("arial", Font.BOLD, 30));
		g.setColor(Color.WHITE);
		if (pause == false) {
			g.drawString("---> Novo Jogo", (Game.WIDTH * Game.SCALE) / 2 - 460, (Game.HEIGHT * Game.SCALE) / 2 - 30);
		} else {
			g.drawString("---> Continuar", (Game.WIDTH * Game.SCALE) / 2 - 460, (Game.HEIGHT * Game.SCALE) / 2 - 30);
		}
		g.drawString("---> Carregar", (Game.WIDTH * Game.SCALE) / 2 - 460, (Game.HEIGHT * Game.SCALE) / 2 + 40);
		g.drawString("---> Sair", (Game.WIDTH * Game.SCALE) / 2 - 460, (Game.HEIGHT * Game.SCALE) / 2 + 110);

		if (options[currentOption] == "novo jogo") {
			if (pause == false) {
				g.setColor(Color.red);
				g.drawString("---> Novo Jogo", (Game.WIDTH * Game.SCALE) / 2 - 460,
						(Game.HEIGHT * Game.SCALE) / 2 - 30);
			} else {
				g.setColor(Color.red);
				g.drawString("---> Continuar", (Game.WIDTH * Game.SCALE) / 2 - 460,
						(Game.HEIGHT * Game.SCALE) / 2 - 30);
			}
		} else if (options[currentOption] == "carregar") {
			g.setColor(Color.red);
			g.drawString("---> Carregar", (Game.WIDTH * Game.SCALE) / 2 - 460, (Game.HEIGHT * Game.SCALE) / 2 + 40);

		} else if (options[currentOption] == "sair") {
			g.setColor(Color.red);
			g.drawString("---> Sair", (Game.WIDTH * Game.SCALE) / 2 - 460, (Game.HEIGHT * Game.SCALE) / 2 + 110);

		}
	}
}
