package jshdesktop.widgets.builtin;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import jshdesktop.widgets.WidgetFrame;
import terra.shell.utils.JProcess;

public class CommandWidget extends WidgetFrame {
	private JProcess command;
	private Image icon;
	private boolean pressed, entered;

	public CommandWidget(JProcess command, Image icon) {
		this.icon = icon;
		this.command = command;
		setSize(50, 50);
		setMovable(true);
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				command.run();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				pressed = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				pressed = false;
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				entered = true;
			}

			@Override
			public void mouseExited(MouseEvent e) {
				entered = false;
			}

		});
	}

	public void paintComponent(Graphics g) {
		g.drawImage(icon, 0, 0, getWidth(), getHeight(), null);
		if (pressed) {
			g.setColor(new Color(148, 148, 148));
			g.fillOval(getWidth() / 2, getHeight() / 2, getWidth() / 10, getHeight() / 10);
			return;
		}
		if (entered) {
			g.setColor(new Color(148, 148, 148, 100));
			g.fillOval(getWidth() / 2, getHeight() / 2, getWidth() / 10, getHeight() / 10);
			return;
		}
	}

	@Override
	public void create() {
	}

}
