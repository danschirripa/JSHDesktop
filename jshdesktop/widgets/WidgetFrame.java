package jshdesktop.widgets;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.plaf.InternalFrameUI;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import jshdesktop.module;
import jshdesktop.desktop.frame.BasicFrame;

public abstract class WidgetFrame extends BasicFrame {
	private WidgetFrame widgetFrame;

	public WidgetFrame() {
		super();
		widgetFrame = this;
		((BasicInternalFrameUI) this.getUI()).setNorthPane(null);
		this.setOpaque(false);
		this.setBorder(null);
		this.getContentPane().setBackground(new java.awt.Color(0, 0, 0, 0));
	}

	@Override
	public void setUI(InternalFrameUI ui) {
		super.setUI(ui);
		BasicInternalFrameUI bUi = (BasicInternalFrameUI) ui;
		if (bUi != null)
			bUi.setNorthPane(null);
	}

	@Override
	public void finish() {
		setVisible(true);
		module.getDesktopFrame().add(this);
	}

	private MouseMotionListener mml;

	public void setMovable(boolean movable) {

		if (movable) {
			mml = new MouseMotionListener() {

				@Override
				public void mouseDragged(MouseEvent e) {
					widgetFrame.setLocation(e.getXOnScreen() - (widgetFrame.getWidth() / 2),
							e.getYOnScreen() - (widgetFrame.getHeight() / 2));
				}

				@Override
				public void mouseMoved(MouseEvent e) {

				}

			};
			this.addMouseMotionListener(mml);
			return;
		}
		widgetFrame.removeMouseMotionListener(mml);
		mml = null;
	}

}
