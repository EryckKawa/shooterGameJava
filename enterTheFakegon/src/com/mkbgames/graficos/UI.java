package com.mkbgames.graficos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.mkbgames.entities.Player;
import com.mkbgames.main.Game;

public class UI {

	public void render(Graphics g) {
		g.setColor(Color.RED);
		g.fillRect(8, 4, 60, 8);
		g.setColor(Color.GREEN);
		g.fillRect(8, 4, (int)((Game.player.life / Game.player.maxlife) * 60), 8);
		g.setColor(Color.WHITE);
		g.setFont(new Font("arial", Font.BOLD, 10));
		g.drawString((int) Game.player.life+"/"+(int)Game.player.maxlife, 9, 12);
		g.drawString("Munição: " + Player.ammo, 7, 20);
	}
}
