package jshdesktop;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import jshdesktop.desktop.DecoratedDesktopPane;
import jshdesktop.desktop.frame.utilities.filemanager.ExtensionRegistry;
import jshdesktop.iconography.IconManager;
import terra.shell.config.Configuration;
import terra.shell.launch.Launch;
import terra.shell.logging.LogManager;
import terra.shell.logging.Logger;
import terra.shell.utils.system.GeneralVariable;
import terra.shell.utils.system.Variables;

public class DesktopManager {
	private static boolean init = false;
	private static Configuration conf;
	protected static DecoratedDesktopPane desktop;
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
			conf.setValue("theme", "darcula");
			conf.setValue("cursor", "default");
			conf.setValue("highlightColor", "108,137,184,0");
		}
		try {
			IconManager.init();
			ExtensionRegistry.init();
		} catch (Exception e) {
			e.printStackTrace();
		}

		String theme = (String) conf.getValue("theme");
		try {
			FlatLaf laf = new FlatDarculaLaf();
			if (theme.equals("darcula"))
				laf = new FlatDarculaLaf();
			if (theme.equals("dark"))
				laf = new FlatDarkLaf();
			if (theme.equals("light"))
				laf = new FlatLightLaf();

			UIManager.setLookAndFeel(laf);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			String highlightColor = (String) conf.getValue("highlightColor");
			Variables.setVar(new GeneralVariable("defaultHighlightColor", highlightColor));
		} catch (Exception e) {
			if (!(e instanceof NullPointerException)) {
				e.printStackTrace();
			}
		}

		String cursorImgPath = (String) conf.getValue("cursor");
		if (cursorImgPath.equals("default"))
			cursorImgPath = Launch.getConfD().getParent() + "/modules/jshdesktop/assets/icons/default_cursor.png";
		setCursor(cursorImgPath, new Point(0, 0));

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
		desktop.repaint();
		return false;
	}

	public static boolean setTheme(String theme) {
		try {
			LookAndFeel laf = new FlatDarculaLaf();
			if (theme.equals("darcula"))
				laf = new FlatDarculaLaf();
			if (theme.equals("dark"))
				laf = new FlatDarkLaf();
			if (theme.equals("light"))
				laf = new FlatLightLaf();
			if (theme.equals("java"))
				laf = new MetalLookAndFeel();

			UIManager.setLookAndFeel(laf);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean setCursor(String cursorPath, Point hotspot) {
		File cursorFile = new File(cursorPath);
		if (!cursorFile.exists())
			return false;
		try {
			Image cursorImage = ImageIO.read(cursorFile).getScaledInstance(20, 20, BufferedImage.SCALE_SMOOTH);
			Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, hotspot, cursorFile.getName());
			desktop.setCursor(cursor);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static int getScreenWidth() {
		return desktop.getWidth();
	}

	public static int getScreenHeight() {
		return desktop.getHeight();
	}

}
