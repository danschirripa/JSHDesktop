package jshdesktop.desktop;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.BevelBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import jshdesktop.DesktopManager;
import jshdesktop.desktop.menu.DesktopMenuBar;
import jshdesktop.widgets.WidgetFrame;

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

	@Override
	public Component add(Component c) {
		super.add(c);
		if (c instanceof JInternalFrame && !(c instanceof WidgetFrame)) {
			JInternalFrame tmp = (JInternalFrame) c;
			JMenuItem menuItem = new JMenuItem(tmp.getTitle());
			menuItem.setIcon(tmp.getFrameIcon());
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (tmp.isIcon()) {
						try {
							tmp.setIcon(false);
							tmp.toFront();
						} catch (Exception e1) {
						}
					} else {
						try {
							tmp.setIcon(true);
						} catch (Exception e1) {
						}
					}
				}
			});
			JPopupMenu menuItemPopup = new JPopupMenu();
			JMenuItem closeFrame = new JMenuItem("Close");
			JMenuItem minimizeFrame = new JMenuItem("Minimize");
			JMenuItem bringToFront = new JMenuItem("Focus");
			JMenuItem maximizeFrame = new JMenuItem("Maximize");

			menuItemPopup.add(closeFrame);
			menuItemPopup.add(minimizeFrame);
			menuItemPopup.add(bringToFront);
			menuItemPopup.add(maximizeFrame);

			menuItem.setComponentPopupMenu(menuItemPopup);
			menuItem.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
			menuItem.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
				}

				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseEntered(MouseEvent e) {
					menuItem.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					menuItem.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
				}

			});
			tmp.addInternalFrameListener(new InternalFrameListener() {

				@Override
				public void internalFrameOpened(InternalFrameEvent e) {
				}

				@Override
				public void internalFrameClosing(InternalFrameEvent e) {
				}

				@Override
				public void internalFrameClosed(InternalFrameEvent e) {
					bar.remove(menuItem);
				}

				@Override
				public void internalFrameIconified(InternalFrameEvent e) {
				}

				@Override
				public void internalFrameDeiconified(InternalFrameEvent e) {
				}

				@Override
				public void internalFrameActivated(InternalFrameEvent e) {
				}

				@Override
				public void internalFrameDeactivated(InternalFrameEvent e) {
				}

			});
			bar.add(menuItem, 1);
		}
		return c;
	}

	public enum BackgroundRatio {
		KEEP_ASPECT_RATIO, STRETCH_TO_FIT
	}

}
