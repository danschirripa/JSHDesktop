package jshdesktop.desktop.frame.utilities;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import jshdesktop.desktop.frame.BasicFrame;

public class SettingsPanel extends BasicFrame {

	@Override
	public void create() {
		JTabbedPane tabs = new JTabbedPane();
		JPanel mouseSettings = new JPanel();
		JPanel lookAndFeelSettings = new JPanel();
		JPanel clusterManagementSettings = new JPanel();

		mouseSettings = createMouseSettingsTab();
		lookAndFeelSettings = createLookAndFeelSettingsTab();
		clusterManagementSettings = createClusterManagementSettingsTab();

		tabs.add(mouseSettings);
		tabs.add(lookAndFeelSettings);
		tabs.add(clusterManagementSettings);

		setSize(700, 500);
		finish();
	}

	private JPanel createMouseSettingsTab() {
		JPanel mouseSettings = new JPanel();

		return mouseSettings;
	}

	private JPanel createLookAndFeelSettingsTab() {
		JPanel lookFeel = new JPanel();
		JPanel backgroundImageSelection = new JPanel();

		return lookFeel;
	}

	private JPanel createClusterManagementSettingsTab() {
		JPanel clusterMan = new JPanel();
		return clusterMan;
	}

}
