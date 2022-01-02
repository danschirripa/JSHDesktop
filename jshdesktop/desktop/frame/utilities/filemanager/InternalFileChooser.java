package jshdesktop.desktop.frame.utilities.filemanager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

import jshdesktop.desktop.frame.BasicFrame;

public class InternalFileChooser extends BasicFrame {
	private File f;

	@Override
	public void create() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JInternalFrame parent = (JInternalFrame) SwingUtilities.getAncestorOfClass(JInternalFrame.class,
						fileChooser);
				if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
					parent.dispose();
					return;
				} else if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
					f = fileChooser.getSelectedFile();
					selectedFileEvent();
				}
				parent.dispose();
			}
		});
		setContentPane(fileChooser);
		setSize(400, 500);
		setVisible(true);
		setResizable(true);
		finish();
	}

	private void selectedFileEvent() {
		ActionEvent fileEvent = new ActionEvent(this, 0, JFileChooser.APPROVE_SELECTION);
		this.processEvent(fileEvent);
	}

	public File getSelectedFile() {
		return f;
	}

}
