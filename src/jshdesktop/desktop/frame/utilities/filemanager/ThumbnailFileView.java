package jshdesktop.desktop.frame.utilities.filemanager;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.WeakHashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileView;

import jshdesktop.iconography.SizedImageIcon;

public class ThumbnailFileView extends FileView {
	private WeakHashMap<String, Icon> loadedIcons = new WeakHashMap<String, Icon>();
	private Icon fileIcon = UIManager.getIcon("FileView.fileIcon");
	private Icon dirIcon = UIManager.getIcon("FileView.directoryIcon");

	public Icon getIcon(File f, int w, int h, boolean synchronously) {
		if (f.isDirectory())
			return dirIcon;
		if (loadedIcons.containsKey(f.getAbsolutePath())) {
			Icon i = loadedIcons.get(f.getAbsolutePath());
			if (i.getIconHeight() == h && i.getIconWidth() == w)
				return loadedIcons.get(f.getAbsolutePath());
		}
		String fileName = f.getName();
		if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")
				|| fileName.endsWith(".gif")) {
			ImageIcon i;
			if (!synchronously) {
				i = new ImageIcon();
				Image img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				fileIcon.paintIcon(null, img.getGraphics(), 0, 0);
				i.setImage(img);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						i.setImage(new SizedImageIcon(f.getAbsolutePath(), w, h).getImage());
					}
				});
			} else {
				i = new SizedImageIcon(f.getAbsolutePath(), w, h);
			}
			loadedIcons.put(f.getAbsolutePath(), i);
			return i;
		} else {
			return UIManager.getIcon("FileView.fileIcon");
		}
	}

	@Override
	public Icon getIcon(File f) {
		return getIcon(f, 20, 20, false);
	}
}