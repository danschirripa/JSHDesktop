package jshdesktop.desktop.frame.utilities;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import jshdesktop.desktop.frame.BasicFrame;

public class Updater extends BasicFrame {

	@Override
	public void create() {
		JPanel updatePanel = new JPanel();
		JButton checkForUpdates = new JButton("Check for Update");
		JLabel currentVersion = new JLabel(
				"Current Version: " + terra.shell.utils.system.updater.Updater.localVersion());

		updatePanel.add(currentVersion, BorderLayout.NORTH);
		updatePanel.add(checkForUpdates, BorderLayout.CENTER);

		checkForUpdates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (terra.shell.utils.system.updater.Updater.silentCheckForUpdates()) {
						setVisible(false);
						remove(updatePanel);

						setSize(300, 150);
						JPanel shouldUpdatePanel = new JPanel();
						shouldUpdatePanel.setLayout(new BoxLayout(shouldUpdatePanel, BoxLayout.Y_AXIS));
						JLabel updateLabel = new JLabel("Update found!");
						JLabel remoteVersion = new JLabel(
								"Remote version: " + terra.shell.utils.system.updater.Updater.remoteVersion());
						JLabel promptLabel = new JLabel("Update?");

						JPanel yesCancelPanel = new JPanel();
						JButton yesButton = new JButton("Update");
						JButton noButton = new JButton("Cancel");

						yesButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								try {
									terra.shell.utils.system.updater.Updater.downloadUpdate();
									JOptionPane.showInternalMessageDialog(shouldUpdatePanel,
											"Update downloaded, restart to complete update!", "Updater",
											JOptionPane.INFORMATION_MESSAGE);
								} catch (Exception e1) {
									JOptionPane.showInternalMessageDialog(shouldUpdatePanel, e1.getMessage(),
											"Updater: Error", JOptionPane.ERROR_MESSAGE);
								}
							}
						});

						noButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								setVisible(false);
								dispose();
							}
						});

						yesCancelPanel.add(yesButton, BorderLayout.WEST);
						yesCancelPanel.add(noButton, BorderLayout.EAST);

						shouldUpdatePanel.add(updateLabel);
						shouldUpdatePanel.add(currentVersion);
						shouldUpdatePanel.add(remoteVersion);
						shouldUpdatePanel.add(promptLabel);
						shouldUpdatePanel.add(yesCancelPanel);

						add(shouldUpdatePanel);
						revalidate();
						setVisible(true);
					}
				} catch (Exception e1) {
					JOptionPane.showInternalMessageDialog(null, e1.getMessage());
				}
			}
		});

		add(updatePanel);
		setTitle("Updater");
		setSize(200, 100);
		finish();
	}

}
