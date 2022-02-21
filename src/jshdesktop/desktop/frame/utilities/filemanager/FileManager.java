package jshdesktop.desktop.frame.utilities.filemanager;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
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
import javax.swing.UIManager;

import jshdesktop.Clipboard;
import jshdesktop.com.pump.plaf.BreadCrumbUI;
import jshdesktop.com.pump.swing.JBreadCrumb;
import jshdesktop.com.pump.swing.JBreadCrumb.BreadCrumbFormatter;
import jshdesktop.desktop.frame.BasicFrame;
import jshdesktop.desktop.frame.utilities.ImageViewer;
import jshdesktop.desktop.frame.utilities.TextEditor;
import terra.shell.command.Command;
import terra.shell.launch.Launch;
import terra.shell.logging.LogManager;
import terra.shell.logging.Logger;
import terra.shell.utils.system.GeneralVariable;
import terra.shell.utils.system.Variables;

public class FileManager extends BasicFrame {
	private static final long serialVersionUID = 6084398124279326622L;
	private JFileChooser mainChooser;
	private File selectedFile;
	private JPanel infoPane;
	private JBreadCrumb<String> breadCrumb;
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

		breadCrumb = new JBreadCrumb<String>() {
			private static final long serialVersionUID = -432694358798718146L;

			public void paintComponent(Graphics g) {
				g.setColor(getBackground());
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		Color highLightColor = Color.blue.darker();
		GeneralVariable defColorVar = (GeneralVariable) Variables.getVar("defaultHighlightColor");
		if (defColorVar != null) {
			int r, g, b, a;
			String[] rgba = defColorVar.getVarValue().split(",");
			r = Integer.parseInt(rgba[0]);
			g = Integer.parseInt(rgba[1]);
			b = Integer.parseInt(rgba[2]);
			a = Integer.parseInt(rgba[3]);
			highLightColor = new Color(r, g, b);
		}
		breadCrumb.setBackground(highLightColor);
		breadCrumb.setFormatter(new BreadCrumbFormatter<String>() {

			@Override
			public void format(JBreadCrumb<String> container, JLabel label, String pathNode, int index) {

				label.setIcon(UIManager.getIcon("FileView.directoryIcon"));
				label.setForeground(Color.DARK_GRAY);
				label.setText(pathNode);
				container.addMouseListener(new MouseListener() {

					@Override
					public void mouseClicked(MouseEvent e) {
						String[] paths = container.getPath();
						String path = "";
						for (int i = 0; i < paths.length; i++) {
							path += "/" + paths[i];
						}
						mainChooser.setCurrentDirectory(new File(path));
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

		});
		breadCrumb.putClientProperty(BreadCrumbUI.PROPERTY_SEPARATOR_ICON, new RoundedShadowIcon());
		breadCrumb.setPath(mainChooser.getCurrentDirectory().getPath().split("/"));
		mainPanel.add(breadCrumb, BorderLayout.NORTH);
		mainPanel.add(mainChooser, BorderLayout.CENTER);
		mainPanel.add(infoPane, BorderLayout.EAST);

		add(mainPanel);
		setFrameIcon(UIManager.getIcon("FileView.directoryIcon"));
		setTitle("File Browser");
		setSize(700, 500);
		finish();
	}

	private class RoundedShadowIcon implements Icon {

		BufferedImage scratch;

		public RoundedShadowIcon() {
			int separatorWidth = 5;
			int leftPadding = 6;
			int rightPadding = 10;

			scratch = new BufferedImage(separatorWidth + leftPadding + rightPadding, 26, BufferedImage.TYPE_INT_ARGB);
			Color darkShadow = new Color(0, 0, 0, 50);
			Color lightShadow = new Color(0, 0, 0, 0);
			Graphics2D g2 = scratch.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setComposite(AlphaComposite.Clear);
			g2.fillRect(0, 0, scratch.getWidth(), scratch.getHeight());
			g2.setComposite(AlphaComposite.SrcOver);
			g2.setPaint(new GradientPaint(0, 0, darkShadow, scratch.getWidth() - 3, 0, lightShadow));
			g2.fillRect(0, 0, scratch.getWidth(), scratch.getHeight());

			g2.setComposite(AlphaComposite.Clear);
			GeneralPath chunk = new GeneralPath();
			chunk.moveTo(leftPadding, 0);
			int k = 5;
			chunk.curveTo(leftPadding + k, k, leftPadding + k, getIconHeight() - k, leftPadding, getIconHeight());
			chunk.lineTo(0, getIconHeight());
			chunk.lineTo(0, 0);
			g2.fill(chunk);
			g2.dispose();
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.drawImage(scratch, x, y, null);
		}

		@Override
		public int getIconWidth() {
			return scratch.getWidth();
		}

		@Override
		public int getIconHeight() {
			return scratch.getHeight();
		}

	};

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
					Clipboard.setClip(mainChooser.getSelectedFile(), "COPY");
				}
			});

			cut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						Clipboard.setClip(mainChooser.getSelectedFile(), "CUT");
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});

			paste.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						File file = (File) Clipboard.getClip();
						String name = file.getName();
						File dir;
						if ((mainChooser.getSelectedFile() != null) && mainChooser.getSelectedFile().isDirectory()) {
							dir = mainChooser.getSelectedFile();
						} else
							dir = mainChooser.getCurrentDirectory();

						FileInputStream fin = new FileInputStream(file);
						FileChannel fInChannel = fin.getChannel();
						FileOutputStream fout = new FileOutputStream(new File(dir, name));
						fout.getChannel().transferFrom(fInChannel, 0, fInChannel.size());
						fout.flush();
						fout.close();
						fInChannel.close();
						fin.close();
						if (Clipboard.getClipType().equals("CUT")) {
							file.delete();
						}
						mainChooser.rescanCurrentDirectory();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});

			openWith.add(openWithCmd);
			openWith.add(openWithTextEditor);
			openWith.add(openWithImageView);

			add(open);
			add(openWith);
			add(cut);
			add(copy);
			add(paste);
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
			if (breadCrumb != null)
				breadCrumb.setPath(mainChooser.getCurrentDirectory().getPath().split("/"));
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
