package jshdesktop;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import jshdesktop.desktop.DecoratedDesktopPane;
import terra.shell.config.Configuration;
import terra.shell.launch.Launch;
import terra.shell.logging.LogManager;
import terra.shell.logging.Logger;

public class DesktopManager {
	private static boolean init = false;
	private static Configuration conf;
	private static DecoratedDesktopPane desktop;
	private static Logger log = LogManager.getLogger("DesktopManager");

	public static void init(DecoratedDesktopPane topLevel) {
		if (init)
			return;
		desktop = topLevel;
		init = true;
		conf = Launch.getConfig("JSHDesktop/dm.conf");
		if (conf == null) {
			File confFile = new File(Launch.getConfD(), "JSHDesktop/dm.conf");
			if (!confFile.exists()) {
				try {
					confFile.getParentFile().mkdirs();
					confFile.createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			conf = new Configuration(confFile);
			conf.setValue("backgroundImg", "default");
			conf.setValue("theme", "default");
		}

		String themePath = (String) conf.getValue("theme");
		if (themePath.equals("default"))
			themePath = (Launch.getConfD().getParent().toString() + "/modules/jshdesktop/assets/icons");

		String imgPath = (String) conf.getValue("backgroundImg");
		if (imgPath.equals("default")) {
			imgPath = Launch.getConfD().getParent().toString() + "/modules/jshdesktop/assets/WaterGap_Overlook.jpg";
		}
		try {
			setBackgroundImage(imgPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean setBackgroundImage(String imgPath) throws IOException {
		File f = new File(imgPath);
		if (!f.exists()) {
			log.err("File Not Found: " + imgPath);
			throw new FileNotFoundException();
		}
		BufferedImage i = ImageIO.read(f);
		conf.setValue("backgroundImg", imgPath);
		desktop.setBackground(i);
		return false;
	}

	public static int getScreenWidth() {
		return desktop.getWidth();
	}

	public static int getScreenHeight() {
		return desktop.getHeight();
	}

}
