package jshdesktop.desktop.frame;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import jshdesktop.module;

public class DialogFrame {

	private static HashSet<String> finishedCommands = new HashSet<String>();
	private static DialogListener listener = new DialogListener();

	public static enum DialogType {
		OK, OK_CANCEL, YES_NO
	};

	public static enum Response {
		OK, CANCEL, YES, NO
	}

	public static Response createDialog(DialogType type, String prompt) {
		if (type == DialogType.OK) {
			return createOkDialog(prompt);
		}
		if (type == DialogType.OK_CANCEL) {
			return createOkCancelDialog(prompt);
		}
		if (type == DialogType.YES_NO) {
			return createYesNoDialog(prompt);
		}
		return Response.CANCEL;
	}

	public static <T> void createInputDialog(T type) {

	}

	private static Response createOkDialog(String prompt) {
		String actionCommand = Math.random() + "";

		JInternalFrame okDia = new JInternalFrame();
		okDia.setSize(300, 200);
		JButton okButton = new JButton("OK");
		JLabel textPrompt = new JLabel(prompt);
		okDia.add(textPrompt, BorderLayout.CENTER);
		okDia.add(okButton, BorderLayout.SOUTH);
		okButton.setActionCommand(actionCommand);
		okButton.addActionListener(listener);
		okDia.setVisible(true);
		module.getDesktopFrame().add(okDia);
		
		
		
		return Response.OK;
	}

	private static Response createOkCancelDialog(String prompt) {
		return null;
	}

	private static Response createYesNoDialog(String prompt) {
		return null;
	}

	private static class DialogListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			finishedCommands.add(e.getActionCommand());
		}

	}

}
