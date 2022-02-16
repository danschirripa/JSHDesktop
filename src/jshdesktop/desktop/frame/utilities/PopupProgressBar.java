package jshdesktop.desktop.frame.utilities;

import javax.swing.JInternalFrame;
import javax.swing.JProgressBar;

public class PopupProgressBar extends JInternalFrame {
	private JProgressBar barInternal;

	public PopupProgressBar() {
		barInternal = new JProgressBar();
		barInternal.setIndeterminate(true);
		add(barInternal);
		setSize(100, 50);
		setVisible(true);
	}

	public PopupProgressBar(int min, int max) {
		barInternal = new JProgressBar(min, max);
		barInternal.setIndeterminate(false);
		barInternal.setValue(min);
		add(barInternal);
		setSize(100, 50);
		setVisible(true);
	}

	public void setIndeterminate(boolean in) {
		barInternal.setIndeterminate(in);
	}

	public void setValue(int v) {
		barInternal.setValue(v);
	}

	public void setString(String s) {
		barInternal.setString(s);
	}

	public void setStringPainted(boolean paint) {
		barInternal.setStringPainted(paint);
	}
}
