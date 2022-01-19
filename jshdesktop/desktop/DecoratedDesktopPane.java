package jshdesktop.desktop;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import jshdesktop.DesktopManager;
import jshdesktop.desktop.menu.DesktopMenuBar;

public class DecoratedDesktopPane extends JDesktopPane {
	private Image background;
	private DesktopMenuBar bar;
	private BackgroundRatio bgRatio = BackgroundRatio.STRETCH_TO_FIT;

	public DecoratedDesktopPane() {
		super();
		DesktopManager.init(this);
		bar = new DesktopMenuBar(this);
		bar.setCursor(this.getCursor());
	}

	public void setBackground(Image i) {
		this.background = i;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (bgRatio == BackgroundRatio.STRETCH_TO_FIT)
			g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
		if (bgRatio == BackgroundRatio.KEEP_ASPECT_RATIO)
			g.drawImage(background, (getWidth() / 2) - (background.getWidth(null) / 2),
					(getHeight() / 2) - (background.getHeight(null) / 2), background.getWidth(null),
					background.getHeight(null), null);
	}

	public DesktopMenuBar getMenuBar() {
		return bar;
	}

	@Override
	public void setCursor(Cursor c) {
		super.setCursor(c);
		if (bar != null)
			bar.setCursor(c);
		for (JInternalFrame in : this.getAllFrames())
			in.setCursor(c);
	}

	public enum BackgroundRatio {
		KEEP_ASPECT_RATIO, STRETCH_TO_FIT
	}

}
