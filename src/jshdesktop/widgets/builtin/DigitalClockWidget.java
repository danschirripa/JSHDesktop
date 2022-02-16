package jshdesktop.widgets.builtin;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JMenuItem;

import jshdesktop.DesktopManager;
import jshdesktop.widgets.WidgetContextMenu;
import jshdesktop.widgets.WidgetFrame;
import terra.shell.launch.Launch;

public class DigitalClockWidget extends WidgetFrame {
	private static Font font;
	private Timer t;
	private DigitalClockWidget dcw;

	@Override
	public void create() {
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT,
					new File(Launch.getConfD().getParentFile(), "/modules/jshdesktop/assets/RondalSemibold-vmZ5Z.otf"));
			this.font = font.deriveFont(Font.BOLD, 50);
			this.font = this.font.deriveFont(Font.BOLD, AffineTransform.getScaleInstance(1, 1.5));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		setSize(215, 80);
		setLocation(DesktopManager.getScreenWidth() - 220, DesktopManager.getScreenHeight() - 100);
		setMovable(true);
		finish();

		this.dcw = this;
		t = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				dcw.repaint();
			}
		};
		t.scheduleAtFixedRate(task, 1000, 1000);
		new ContextMenu(this);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.black);
		g.setFont(font);
		g.drawString(LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ssa")), 0, 75);
	}

	private class ContextMenu extends WidgetContextMenu {

		public ContextMenu(WidgetFrame wf) {
			super(wf);
		}

		@Override
		protected void createMenu() {
			JMenuItem close = new JMenuItem("Close");
			JMenuItem showSettings = new JMenuItem("Settings");

			close.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					t.cancel();
					dcw.dispose();
				}
			});

			showSettings.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

				}
			});

			add(close);
			//add(showSettings);
		}

	}

}
