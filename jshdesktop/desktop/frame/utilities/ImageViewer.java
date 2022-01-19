package jshdesktop.desktop.frame.utilities;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import jshdesktop.module;
import jshdesktop.desktop.frame.BasicFrame;

public class ImageViewer extends BasicFrame {
	private File imageFile;
	private JPanel imagePanel;
	private Image image;
	private ImageViewer me;

	public ImageViewer() {
		me = this;
	}

	public ImageViewer(Image i) {
		this.image = i;
		me = this;
	}

	public ImageViewer(File f) throws IOException {
		this.imageFile = f;
		image = ImageIO.read(f);
		me = this;
	}

	@Override
	public void create() {
		imagePanel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (image != null) {
					float scaleFactor = 1;
					if (me.getWidth() > me.getHeight()) {
						scaleFactor = ((float) me.getHeight()) / ((float) image.getHeight(null));
					}
					if (me.getHeight() > me.getWidth()) {
						scaleFactor = ((float) me.getWidth()) / ((float) image.getWidth(null));
					}
					g.drawImage(image, 0, 0, (int) (image.getWidth(null) * scaleFactor),
							(int) (image.getHeight(null) * scaleFactor), null);

				}
			}
		};
		imagePanel.setSize(getSize());
		add(imagePanel);
		setSize(500, 600);
		setJMenuBar(createMenuBar());
		finish();
	}

	private JMenuBar createMenuBar() {
		JMenuBar menu = new JMenuBar();
		JMenu fileMenu = new JMenu("File");

		JMenuItem open = new JMenuItem("Open");
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JInternalFrame parent = (JInternalFrame) SwingUtilities.getAncestorOfClass(JInternalFrame.class,
								fileChooser);
						if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
							parent.dispose();
							return;
						} else if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
							try {
								imageFile = fileChooser.getSelectedFile();
								image = ImageIO.read(fileChooser.getSelectedFile());
							} catch (Exception e1) {
								e1.printStackTrace();
								JOptionPane.showInternalMessageDialog(parent, e1.getMessage(), "Error",
										JOptionPane.ERROR_MESSAGE);
							}
						}
						parent.dispose();
					}
				});
				JInternalFrame intFileChooser = new JInternalFrame();
				intFileChooser.setContentPane(fileChooser);
				intFileChooser.setSize(200, 300);
				intFileChooser.setVisible(true);
				intFileChooser.setResizable(true);
				intFileChooser.toFront();
				module.getDesktopFrame().add(intFileChooser);
			}
		});

		fileMenu.add(open);
		menu.add(fileMenu);
		return menu;
	}

}
