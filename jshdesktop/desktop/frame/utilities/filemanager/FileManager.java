package jshdesktop.desktop.frame.utilities.filemanager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import jshdesktop.desktop.frame.BasicFrame;
import terra.shell.command.Command;
import terra.shell.launch.Launch;
import terra.shell.logging.LogManager;
import terra.shell.logging.Logger;

public class FileManager extends BasicFrame {
	private JFileChooser mainChooser;
	private File selectedFile;
	private JPanel infoPane;
	private boolean showThumbs = false;
	private ThumbnailFileView fileView;
	private Logger log;

	@Override
	public void create() {
		log = LogManager.getLogger("File Manager");
		String homeDir = System.getProperty("user.home");

		mainChooser = new JFileChooser(homeDir);
		mainChooser.setControlButtonsAreShown(false);
		mainChooser.addPropertyChangeListener(new FileChooserPropertyListener());
		mainChooser.addActionListener(new FileChooserActionListener());

		fileView = new ThumbnailFileView();
		mainChooser.setFileView(fileView);
		mainChooser.setPreferredSize(new Dimension(500, 500));

		mainChooser.remove(mainChooser.getComponents()[mainChooser.getComponents().length - 1]);

		mainChooser.setComponentPopupMenu(new ChooserPopupMenu());

		JPanel mainPanel = new JPanel(new BorderLayout());

		infoPane = new JPanel();
		infoPane.setPreferredSize(new Dimension(200, 500));

		mainPanel.add(mainChooser, BorderLayout.CENTER);
		mainPanel.add(infoPane, BorderLayout.EAST);

		add(mainPanel);
		setTitle("File Browser");
		setSize(700, 500);
		finish();
	}

	private class ChooserPopupMenu extends JPopupMenu {
		public ChooserPopupMenu() {

		}
	}

	private class FileChooserActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
				String fileName = mainChooser.getSelectedFile().getName();
				String fileExtension = fileName.substring(fileName.lastIndexOf("."));
				Command exec = ExtensionRegistry.getExtensionExecutor(fileExtension);
				if (exec == null) {
					String cmd = JOptionPane.showInternalInputDialog(mainChooser,
							"No command registered for this extension, please enter a command:");
					exec = Launch.getCmds().get(cmd);
					if (exec == null) {
						JOptionPane.showInternalMessageDialog(mainChooser, "Command not found!");
						return;
					}
				}
				exec.addArgs(new String[] { mainChooser.getSelectedFile().getAbsolutePath() }, null);
				exec.run();
			}
		}

	}

	private class FileChooserPropertyListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (mainChooser.getSelectedFile() != null) {
				updateInfoPane();
				infoPane.revalidate();
				repaint();
			}
		}

	}

	private void updateInfoPane() {
		if (infoPane == null)
			infoPane = new JPanel();
		infoPane.removeAll();
		selectedFile = mainChooser.getSelectedFile();
		JLabel fileName = new JLabel("File Name: " + selectedFile.getName());
		JLabel fileSize = new JLabel("File Size: " + selectedFile.getTotalSpace() + " bytes");
		SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss:SSaa L/dd/y");
		format.setTimeZone(TimeZone.getTimeZone("EST"));
		JLabel modifiedDateTime = new JLabel("Last Modified: " + format.format(new Date(selectedFile.lastModified())));
		Icon fileIcon = fileView.getIcon(selectedFile, 100, 100, true);

		JPanel iconPane = new JPanel();
		JLabel iconLabel = new JLabel(fileIcon);
		iconLabel.setSize(100, 100);
		iconPane.add(iconLabel);

		JPanel generalInfoPane = new JPanel();
		generalInfoPane.setLayout(new BoxLayout(generalInfoPane, BoxLayout.Y_AXIS));
		generalInfoPane.add(fileName);
		generalInfoPane.add(fileSize);
		if (selectedFile.isDirectory()) {
			JLabel numFiles = new JLabel("Contains: " + selectedFile.listFiles().length + " files");
			generalInfoPane.add(numFiles);
		}
		generalInfoPane.add(modifiedDateTime);

		infoPane.setLayout(new BorderLayout());
		infoPane.add(iconPane, BorderLayout.NORTH);
		infoPane.add(generalInfoPane, BorderLayout.CENTER);
		repaint();
	}
}
