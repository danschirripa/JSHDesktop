package jshdesktop.desktop;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JDesktopPane;

import jshdesktop.DesktopManager;
import jshdesktop.desktop.menu.DesktopMenuBar;

public class DecoratedDesktopPane extends JDesktopPane {
	private Image background;
	private DesktopMenuBar bar;

	public DecoratedDesktopPane() {
		super();
		bar = new DesktopMenuBar(this);
		DesktopManager.init(this);
	}

	public void setBackground(Image i) {
		this.background = i;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
	}

	public DesktopMenuBar getMenuBar() {
		return bar;
	}

}
