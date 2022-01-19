package jshdesktop.desktop.frame.utilities;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import jshdesktop.DesktopManager;
import jshdesktop.desktop.frame.BasicFrame;
import jshdesktop.desktop.frame.utilities.filemanager.FileManager;
import jshdesktop.desktop.frame.utilities.filemanager.ThumbnailFileView;
import terra.shell.launch.Launch;

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

		tabs.add(mouseSettings, "Mouse");
		tabs.add(lookAndFeelSettings, "Look and Feel");
		tabs.add(clusterManagementSettings, "Cluster Management");

		add(tabs);
		setSize(700, 500);
		finish();
	}

	private JPanel createMouseSettingsTab() {
		JPanel mouseSettings = new JPanel();

		return mouseSettings;
	}

	private JPanel createLookAndFeelSettingsTab() {
		JPanel lookFeelSettingsTab = new JPanel(new BorderLayout());

		JPanel lookFeel = new JPanel(new BorderLayout());

		JList<String> themeList = new JList<String>();
		DefaultListModel<String> themeListModel = new DefaultListModel<String>();
		themeListModel.add(0, "Darcula");
		themeListModel.add(1, "Dark");
		themeListModel.add(2, "Light");
		themeListModel.add(3, "Java");
		themeList.setModel(themeListModel);

		themeList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				PopupProgressBar pop = new PopupProgressBar();
				pop.setString("Loading theme...");
				DesktopManager.setTheme(themeList.getSelectedValue().toLowerCase());
				pop.dispose();
			}

		});
		JLabel themeListLabel = new JLabel("Theme List");
		lookFeel.add(themeListLabel, BorderLayout.NORTH);
		lookFeel.add(themeList, BorderLayout.EAST);

		JPanel backgroundSettings = new JPanel(new BorderLayout());
		JLabel chooseBackgroundLabel = new JLabel("Choose Background:");

		JFileChooser chooser = new JFileChooser(Launch.getConfD().getParent() + "/modules/jshdesktop/assets");
		chooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				String name = f.getName();
				if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif")
						|| f.isDirectory())
					return true;
				return false;
			}

			@Override
			public String getDescription() {
				return "Supported Images (png, jpg, gif)";
			}

		});

		chooser.setFileView(new ThumbnailFileView());

		chooser.setControlButtonsAreShown(false);

		backgroundSettings.add(chooseBackgroundLabel, BorderLayout.NORTH);
		backgroundSettings.add(chooser, BorderLayout.CENTER);

		JButton setBackground = new JButton("Set Background");
		setBackground.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File newBackground = chooser.getSelectedFile();
				try {
					DesktopManager.setBackgroundImage(newBackground.getAbsolutePath());
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(chooser, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		backgroundSettings.add(setBackground, BorderLayout.SOUTH);

		JPanel westPanel = new JPanel(new BorderLayout());

		JPanel cursorIconSetting = new JPanel(new BorderLayout());

		westPanel.add(lookFeel, BorderLayout.NORTH);
		westPanel.add(cursorIconSetting, BorderLayout.SOUTH);

		lookFeelSettingsTab.add(westPanel, BorderLayout.WEST);
		lookFeelSettingsTab.add(backgroundSettings, BorderLayout.EAST);

		return lookFeelSettingsTab;
	}

	private JPanel createClusterManagementSettingsTab() {
		JPanel clusterMan = new JPanel();
		return clusterMan;
	}

}
