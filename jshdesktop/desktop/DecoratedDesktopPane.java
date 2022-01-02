package jshdesktop.desktop;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JDesktopPane;
import javax.swing.JMenu;

import jshdesktop.desktop.menu.DesktopMenuBar;

public class DecoratedDesktopPane extends JDesktopPane {
	private Image background;
	private DesktopMenuBar bar;

	public DecoratedDesktopPane() {
		super();
		bar = new DesktopMenuBar(this);
	}

	public void setBackground(Image i) {
		super.setBackground(java.awt.Color.WHITE);
		this.background = i;
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(background, 0, 0, WIDTH, HEIGHT, this);
		super.paint(g);
	}

	public DesktopMenuBar getMenuBar() {
		return bar;
	}

}
