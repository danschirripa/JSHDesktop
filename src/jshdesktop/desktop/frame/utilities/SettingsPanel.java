package jshdesktop.desktop.frame.utilities;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import jshdesktop.DesktopManager;
import jshdesktop.desktop.frame.BasicFrame;
import jshdesktop.desktop.frame.utilities.filemanager.ThumbnailFileView;
import terra.shell.emulation.concurrency.math.cluster.ConnectionManager;
import terra.shell.emulation.concurrency.math.cluster.ConnectionManager.NodeInfo;
import terra.shell.launch.Launch;

public class SettingsPanel extends BasicFrame {
	private int preferredWidth = 700, preferredHeight = 500;

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

	private JPanel nodeInfoPanel;

	private JPanel createClusterManagementSettingsTab() {
		JPanel clusterMan = new JPanel();
		// Cluster Management features:
		// Node count, CPU count
		// Live Local Process count
		// Overall usage report
		// - Overall CPU
		// - Overall Mem
		ConnectionManager conMan = Launch.getConnectionMan();
		int numNodes = conMan.numberOfNodes();
		NodeInfo[] nodes = conMan.nodes();

		if (nodes.length > 0)
			nodeInfoPanel = createNodeInfoPanel(nodes[0]);
		else {
			nodeInfoPanel = new JPanel();
			nodeInfoPanel.setPreferredSize(new Dimension(550, 450));
			nodeInfoPanel.add(new JLabel("There currently no associated Nodes"), BorderLayout.CENTER);
		}

		JPanel nodelistPanel = new JPanel();
		nodelistPanel.setPreferredSize(new Dimension(100, 450));

		JList<NodeInfo> nodeList = new JList<NodeInfo>(nodes);
		nodeList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				nodeInfoPanel = createNodeInfoPanel(nodes[nodeList.getSelectedIndex()]);
				nodeInfoPanel.revalidate();
				clusterMan.revalidate();
			}

		});

		JScrollPane nodeListScroller = new JScrollPane(nodeList);
		nodeListScroller.setPreferredSize(new Dimension(100, 400));

		nodelistPanel.add(nodeListScroller, BorderLayout.CENTER);
		JLabel nodeCount = new JLabel(numNodes + " nodes");
		nodelistPanel.add(nodeCount, BorderLayout.NORTH);

		clusterMan.add(nodelistPanel, BorderLayout.WEST);
		clusterMan.add(new JSeparator(JSeparator.VERTICAL), BorderLayout.CENTER);
		clusterMan.add(nodeInfoPanel, BorderLayout.EAST);
		return clusterMan;
	}

	private JPanel createNodeInfoPanel(NodeInfo ni) {
		JPanel nodeInfoPanel = new JPanel();
		nodeInfoPanel.setPreferredSize(new Dimension(550, 450));
		nodeInfoPanel.setName(ni.getIp());

		JPanel generalInfoPanel = new JPanel();
		generalInfoPanel.setLayout(new BoxLayout(generalInfoPanel, BoxLayout.Y_AXIS));

		JLabel nodeIp = new JLabel(ni.getIp());

		Date date = new Date(ni.lastPinged());
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SS aa");
		String lastPing = format.format(date);

		JLabel lastPinged = new JLabel(lastPing);
		JLabel curPing = new JLabel(ni.ping() + " ms");

		JButton pingButton = new JButton("Ping");
		pingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					curPing.setText(Launch.getConnectionMan().ping(ni.getIp()) + " ms");
					lastPinged.setText(format.format(LocalDateTime.now()));
					generalInfoPanel.revalidate();
				} catch (Exception e1) {
					JOptionPane.showInternalMessageDialog(nodeInfoPanel, "Ping failed: " + e1.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		generalInfoPanel.add(nodeIp);
		generalInfoPanel.add(new JSeparator());
		generalInfoPanel.add(lastPinged);
		generalInfoPanel.add(curPing);
		generalInfoPanel.add(pingButton);

		return nodeInfoPanel;
	}

}
