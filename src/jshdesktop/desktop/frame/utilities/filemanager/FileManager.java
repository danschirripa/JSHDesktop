package jshdesktop.desktop.frame.utilities.filemanager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import jshdesktop.Clipboard;
import jshdesktop.desktop.frame.BasicFrame;
import jshdesktop.desktop.frame.utilities.ImageViewer;
import jshdesktop.desktop.frame.utilities.TextEditor;
import terra.shell.command.Command;
import terra.shell.launch.Launch;
import terra.shell.logging.LogManager;
import terra.shell.logging.Logger;

public class FileManager extends BasicFrame {
	private static final long serialVersionUID = 6084398124279326622L;
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
		private static final long serialVersionUID = -6547450279218432009L;

		public ChooserPopupMenu() {
			// Need basic file actions, open, new, delete, info, open with
			JMenuItem open = new JMenuItem("Open");
			JMenu openWith = new JMenu("Open With");

			JMenuItem openWithCmd = new JMenuItem("Command");
			JMenuItem openWithTextEditor = new JMenuItem("Text Edit");
			JMenuItem openWithImageView = new JMenuItem("Image Viewer");

			JMenuItem newFile = new JMenuItem("New");
			JMenuItem delete = new JMenuItem("Delete");
			JMenuItem info = new JMenuItem("Info");
			JMenuItem copy = new JMenuItem("Copy");
			JMenuItem cut = new JMenuItem("Cut");
			JMenuItem paste = new JMenuItem("Paste");

			open.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String fileName = mainChooser.getSelectedFile().getName();
					String fileExtension = fileName.substring(fileName.indexOf("."));
					Command c = ExtensionRegistry.getExtensionExecutor(fileExtension);
					if (c == null) {
						String cmd = JOptionPane.showInternalInputDialog(mainChooser,
								"No Command registered for this extension, please enter a command:", "Command Input",
								JOptionPane.INFORMATION_MESSAGE);
						c = Launch.getCmds().get(cmd);
						if (c == null) {
							JOptionPane.showInternalMessageDialog(mainChooser, "Command not found!", "Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						int alwaysUse = JOptionPane.showInternalConfirmDialog(mainChooser,
								"Always use " + c.getName() + " for " + fileExtension + " files?", "Always Use?",
								JOptionPane.YES_NO_OPTION);
						if (alwaysUse == JOptionPane.YES_OPTION) {
							boolean didRegister = ExtensionRegistry.registerExtension(fileExtension, c);
							if (!didRegister) {
								int replace = JOptionPane.showInternalConfirmDialog(mainChooser,
										"Extension already has a handler registered, replace?", "Replace?",
										JOptionPane.YES_NO_OPTION);
								if (replace == JOptionPane.YES_OPTION) {
									ExtensionRegistry.unRegisterExtension(fileExtension);
									ExtensionRegistry.registerExtension(fileExtension, c);
								}
							}
						}
					}
					c.addArgs(new String[] { mainChooser.getSelectedFile().getAbsolutePath() }, null);
					c.run();
				}

			});

			openWithCmd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String fileExtension = mainChooser.getSelectedFile().getName()
							.substring(mainChooser.getSelectedFile().getName().indexOf("."));
					Command c;
					String cmd = JOptionPane.showInternalInputDialog(mainChooser,
							"No Command registered for this extension, please enter a command:", "Command Input",
							JOptionPane.INFORMATION_MESSAGE);
					c = Launch.getCmds().get(cmd);
					if (c == null) {
						JOptionPane.showInternalMessageDialog(mainChooser, "Command not found!", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					int alwaysUse = JOptionPane.showInternalConfirmDialog(mainChooser,
							"Always use " + c.getName() + " for " + fileExtension + " files?", "Always Use?",
							JOptionPane.YES_NO_OPTION);
					if (alwaysUse == JOptionPane.YES_OPTION) {
						boolean didRegister = ExtensionRegistry.registerExtension(fileExtension, c);
						if (!didRegister) {
							int replace = JOptionPane.showInternalConfirmDialog(mainChooser,
									"Extension already has a handler registered, replace?", "Replace?",
									JOptionPane.YES_NO_OPTION);
							if (replace == JOptionPane.YES_OPTION) {
								ExtensionRegistry.unRegisterExtension(fileExtension);
								ExtensionRegistry.registerExtension(fileExtension, c);
							}
						}

					}
					c.addArgs(new String[] { mainChooser.getSelectedFile().getAbsolutePath() }, null);
					c.run();
				}
			});

			openWithTextEditor.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new TextEditor().open(selectedFile);
				}
			});

			openWithImageView.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						new ImageViewer(selectedFile);
					} catch (Exception e1) {
						JOptionPane.showInternalMessageDialog(mainChooser, "Failed to open with Image Viewer", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			});

			newFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String fileName = JOptionPane.showInternalInputDialog(mainChooser, "FileName:");
					File newFile = new File(mainChooser.getCurrentDirectory(), fileName);
					if (newFile.exists()) {
						JOptionPane.showInternalMessageDialog(mainChooser, "File already exists", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					try {
						newFile.createNewFile();
						mainChooser.rescanCurrentDirectory();
						mainChooser.setSelectedFile(newFile);
					} catch (Exception e1) {
						JOptionPane.showInternalMessageDialog(mainChooser, e1.getMessage(), e1.getMessage(),
								JOptionPane.ERROR_MESSAGE);
					}
				}
			});

			delete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					File file = mainChooser.getSelectedFile();
					int isSure = JOptionPane.showInternalConfirmDialog(mainChooser, "Delete " + file.getName() + "?",
							"Delete?", JOptionPane.OK_CANCEL_OPTION);
					if (isSure == JOptionPane.YES_OPTION) {
						boolean didDelete = file.delete();
						mainChooser.rescanCurrentDirectory();
						if (!didDelete)
							JOptionPane.showInternalMessageDialog(mainChooser, "Unable to delete file", "Error",
									JOptionPane.ERROR_MESSAGE);
					}
				}
			});

			info.addActionListener(new ActionListener() {

				private File infoFile;

				public void actionPerformed(ActionEvent e) {
					infoFile = mainChooser.getSelectedFile();
					new InfoPane();
				}

				final class InfoPane extends BasicFrame {

					@Override
					public void create() {
						JPanel infoPanel = new JPanel();
						infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
						File file = infoFile;
						String fileName = file.getName();
						String fileExtension = fileName.substring(fileName.indexOf("."));
						String defaultExtensionHandler = ExtensionRegistry.getExtensionExecutor(fileExtension)
								.getName();
						SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss:SSaa L/dd/y");
						format.setTimeZone(TimeZone.getTimeZone("EST"));
						String modificationTime = format.format(new Date(selectedFile.lastModified()));

						JLabel nameLabel = new JLabel(fileName);
						JLabel extensionLabel = new JLabel(fileExtension);
						JLabel defaultHandlerLabel = new JLabel(defaultExtensionHandler);
						JLabel modifiedTimeLabel = new JLabel(modificationTime);

						infoPanel.add(nameLabel);
						infoPanel.add(extensionLabel);
						infoPanel.add(defaultHandlerLabel);
						infoPanel.add(modifiedTimeLabel);

						add(infoPanel);
						setSize(300, 300);
						finish();
					}

				}
			});

			copy.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Clipboard.setClipboard(mainChooser.getSelectedFile());
				}
			});

			cut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

				}
			});

			paste.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						File file = (File) Clipboard.getClipboard();
						String name = file.getName();
						File dir;
						if (mainChooser.getSelectedFile().isDirectory()) {
							dir = mainChooser.getSelectedFile();
						} else
							dir = mainChooser.getCurrentDirectory();
					} catch (Exception e1) {

					}
				}
			});

			openWith.add(openWithCmd);
			openWith.add(openWithTextEditor);
			openWith.add(openWithImageView);

			add(open);
			add(openWith);
			add(newFile);
			add(delete);
			add(info);
		}
	}

	private class FileChooserActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
				String fileName = mainChooser.getSelectedFile().getName();
				String fileExtension = fileName.substring(fileName.indexOf("."));
				Command c = ExtensionRegistry.getExtensionExecutor(fileExtension);
				if (c == null) {
					String cmd = JOptionPane.showInternalInputDialog(mainChooser,
							"No Command registered for this extension, please enter a command:", "Command Input",
							JOptionPane.INFORMATION_MESSAGE);
					c = Launch.getCmds().get(cmd);
					if (c == null) {
						JOptionPane.showInternalMessageDialog(mainChooser, "Command not found!", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					int alwaysUse = JOptionPane.showInternalConfirmDialog(mainChooser,
							"Always use " + c.getName() + " for " + fileExtension + " files?", "Always Use?",
							JOptionPane.YES_NO_OPTION);
					if (alwaysUse == JOptionPane.YES_OPTION) {
						boolean didRegister = ExtensionRegistry.registerExtension(fileExtension, c);
						if (!didRegister) {
							int replace = JOptionPane.showInternalConfirmDialog(mainChooser,
									"Extension already has a handler registered, replace?", "Replace?",
									JOptionPane.YES_NO_OPTION);
							if (replace == JOptionPane.YES_OPTION) {
								ExtensionRegistry.unRegisterExtension(fileExtension);
								ExtensionRegistry.registerExtension(fileExtension, c);
							}
						}
					}
				}
				c.addArgs(new String[] { mainChooser.getSelectedFile().getAbsolutePath() }, null);
				c.run();
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
