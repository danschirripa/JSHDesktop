package jshdesktop.widgets.builtin;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import jshdesktop.widgets.WidgetFrame;
import terra.shell.utils.JProcess;

public class CommandWidget extends WidgetFrame {
	private JProcess command;
	private Image icon;
	private boolean pressed, entered;

	public CommandWidget(JProcess command, Image icon) {
		this.icon = icon;
		this.command = command;
		setSize(50, 60);
		setMovable(true);
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				command.run();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				pressed = true;
				repaint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				pressed = false;
				repaint();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				entered = true;
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				entered = false;
				repaint();
			}

		});
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(icon, 0, 0, getWidth(), getHeight() - 10, null);
		if (pressed) {
			g.setColor(new Color(148, 148, 148));
			g.fillOval(getWidth() / 2, (getHeight() - 10) / 2, getWidth() / 3, (getHeight() - 10) / 3);
			return;
		}
		if (entered) {
			g.setColor(new Color(215, 215, 215, 130));
			g.fillOval(getWidth() / 2, (getHeight() - 10) / 2, getWidth() / 3, (getHeight() - 10) / 3);
			return;
		}
		g.setFont(new JLabel().getFont().deriveFont(Font.BOLD, 12));
		g.drawString(command.getName(), 0, 60);
	}

	@Override
	public void create() {
	}

}
