package jshdesktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GraphicsEnvironment;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;

import jshdesktop.desktop.DecoratedDesktopPane;
import terra.shell.logging.LogManager;
import terra.shell.logging.Logger;

public class JDesktopFrame extends JFrame {
	private Logger log = LogManager.getLogger("DesktopFrame");
	private DecoratedDesktopPane desktopPane;

	public JDesktopFrame() {
		log.debug("Creating JDesktopFrame top level...");
		desktopPane = new DecoratedDesktopPane();
		this.setUndecorated(true);
		super.add(desktopPane);
		setSize(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width,
				GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height);
		super.add(desktopPane.getMenuBar(), BorderLayout.SOUTH);
		setVisible(true);
	}

	@Override
	public Component add(Component c) {
		return desktopPane.add(c);
	}

	public JDesktopPane getDesktopPane() {
		return desktopPane;
	}
}
