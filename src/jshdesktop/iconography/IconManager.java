package jshdesktop.iconography;

import java.awt.Image;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import terra.shell.config.Configuration;
import terra.shell.launch.Launch;
import terra.shell.logging.LogManager;
import terra.shell.logging.Logger;

public final class IconManager {
	private static boolean init = false;
	private static Hashtable<String, Icon> icons;
	private static Configuration conf;
	private static Logger log;

	public static void init() throws FileNotFoundException {
		if (init)
			return;
		log = LogManager.getLogger("IconManager");
		icons = new Hashtable<String, Icon>();
		init = true;
		conf = Launch.getConfig("JSHDesktop/iconMan.conf");
		if (conf == null) {
			try {
				File confFile = new File(Launch.getConfD(), "JSHDesktop/iconMan.conf");
				if (!confFile.exists()) {
					confFile.getParentFile().mkdirs();
					confFile.createNewFile();
				}
				conf = new Configuration(confFile);
				conf.setValue("iconDir", "default");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String iconDir = (String) conf.getValue("iconDir");
		if (iconDir.equals("default"))
			iconDir = Launch.getConfD().getParent() + "/modules/jshdesktop/assets/icons";

		File iconDirFile = new File(iconDir);
		if (!iconDirFile.exists() || !iconDirFile.isDirectory())
			throw new FileNotFoundException(iconDirFile.getAbsolutePath());

		File[] iconListings = iconDirFile.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				String name = pathname.getName();
				if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".gif") || name.endsWith("jpeg"))
					return true;
				if (pathname.isDirectory() && !pathname.getName().equals("animated"))
					return true;
				return false;
			}

		});
		loadIconsRecursively(iconListings);

		File animatedIconDirFile = new File(iconDirFile, "animated");
		File[] animatedIcons = animatedIconDirFile.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory() || pathname.getName().endsWith(".gif"))
					return true;
				return false;
			}

		});
		loadAnimatedIcons(animatedIcons);
	}

	public static Icon getIcon(String icon) {
		return icons.get(icon);
	}

	public static void registerIcon(String iconName, Icon icon) {
		icons.put(iconName, icon);
		log.debug("Registered icon " + iconName);
	}

	private static void loadAnimatedIcons(File[] iconListings) {
		for (File iconListing : iconListings) {
			if (iconListing.isDirectory()) {
				LinkedList<Image> iconImages = new LinkedList<Image>();
				int index = 0;
				while (true) {
					try {
						index ++;
						File iconImageFile = new File(iconListing, index + ".png");
						if (!iconImageFile.exists())
							break;
						Image iconImage = ImageIO.read(iconImageFile);
						iconImages.add(iconImage);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				Image[] iconImagesArray = new Image[iconImages.size()];
				iconImagesArray = iconImages.toArray(iconImagesArray);
				AnimatedIcon icon = new AnimatedIcon(500, iconImagesArray);
				String iconName = iconListing.getAbsolutePath()
						.replaceFirst(Launch.getConfD().getParent() + "/modules/jshdesktop/assets/icons", "");
				icons.put(iconName, icon);
				log.debug("Loaded animated icon " + iconName + " with " + iconImagesArray.length + " frames");
				continue;
			}

		}
	}

	private static void loadIconsRecursively(File[] iconListings) {
		for (File iconListing : iconListings) {
			if (iconListing.isDirectory()) {
				loadIconsRecursively(iconListing.listFiles());
				continue;
			}
			try {
				Image iconImage = ImageIO.read(iconListing);
				ImageIcon icon = new ImageIcon(iconImage);
				String iconName = iconListing.getAbsolutePath()
						.replaceFirst(Launch.getConfD().getParent() + "/modules/jshdesktop/assets/icons", "");
				iconName = iconName.substring(0, iconName.lastIndexOf("."));

				icons.put(iconName, icon);
				log.debug("Loaded icon " + iconName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
