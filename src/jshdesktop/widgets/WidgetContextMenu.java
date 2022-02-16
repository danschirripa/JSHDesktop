package jshdesktop.widgets;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;

public abstract class WidgetContextMenu extends JPopupMenu {
	private WidgetFrame wf;
	private WidgetContextMenu wcm;

	public WidgetContextMenu(WidgetFrame wf) {
		createMenu();
		this.wf = wf;
		this.wcm = this;
		wf.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					wcm.show(wf, e.getX(), e.getY());
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

		});
	}

	protected abstract void createMenu();

	protected void recreateMenu() {
		removeAll();
		createMenu();
	}

}
