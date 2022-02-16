package jshdesktop.widgets.builtin;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JMenuItem;

import jshdesktop.widgets.WidgetContextMenu;
import jshdesktop.widgets.WidgetFrame;
import terra.shell.launch.Launch;

public class AnalogClockWidget extends WidgetFrame {
	private BufferedImage clockFace;
	private int deltaX, deltaY;
	private AnalogClockWidget cw;
	private Timer t;

	@Override
	public void create() {
		try {
			clockFace = ImageIO
					.read(new File(Launch.getConfD().getParent(), "modules/jshdesktop/assets/widgets/clock_face.png"));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		setSize(100, 100);
		deltaX = (3600 / 100);
		deltaY = (3600 / 100);
		setMovable(true);
		finish();

		cw = this;
		t = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				cw.repaint();
			}
		};
		t.scheduleAtFixedRate(task, 1000, 1000);
		new ContextMenu(this);
	}

	private int centerPointX;
	private int centerPointY;
	private int clockRadius;
	private LocalDateTime time;

	@Override
	public void paintComponent(Graphics g) {
		centerPointX = this.getWidth() / 2;
		centerPointY = this.getHeight() / 2;
		clockRadius = centerPointX;
		time = LocalDateTime.now();

		g.drawImage(clockFace, 0, 0, getWidth(), getHeight(), null);
		g.setColor(Color.GRAY);
		drawHourHand(g);
		drawMinuteHand(g);
		g.setColor(Color.RED);
		drawSecondHand(g);
	}

	private void drawMinuteHand(Graphics g) {
		int minute = time.getMinute();
		int mLength = (int) (clockRadius * 0.8);
		int xMinute = (int) (centerPointX + mLength * Math.sin(minute * (2 * Math.PI / 60)));
		int yMinute = (int) (centerPointY - mLength * Math.cos(minute * (2 * Math.PI / 60)));
		g.drawLine(centerPointX, centerPointY, xMinute, yMinute);
	}

	private void drawHourHand(Graphics g) {
		int hour = time.getHour();
		int minute = time.getMinute();
		int hLength = (int) (clockRadius * 0.5);
		int xHour = (int) (centerPointX + hLength * Math.sin((hour % 12 + minute / 60.0) * (2 * Math.PI / 12)));
		int yHour = (int) (centerPointY - hLength * Math.cos((hour % 12 + minute / 60.0) * (2 * Math.PI / 12)));
		g.drawLine(centerPointX, centerPointY, xHour, yHour);
	}

	private void drawSecondHand(Graphics g) {
		int second = time.getSecond();
		int sLength = (int) (clockRadius * 0.8);
		int xSecond = (int) (centerPointX + sLength * Math.sin(time.getSecond() * (2 * Math.PI / 60)));
		int ySecond = (int) (centerPointY - sLength * Math.cos(second * (2 * Math.PI / 60)));
		g.drawLine(centerPointX, centerPointY, xSecond, ySecond);
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
					cw.dispose();
				}
			});

			showSettings.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

				}
			});

			add(close);
			// add(showSettings);
		}

	}

}
