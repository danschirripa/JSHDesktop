package jshdesktop.desktop.frame.utilities.filemanager;

import java.io.File;

import terra.shell.command.Command;
import terra.shell.config.Configuration;
import terra.shell.launch.Launch;

public class ExtensionRegistry {
	private static boolean init = false;
	private static Configuration conf;

	public static void init() {
		if (init)
			return;
		conf = Launch.getConfig("JSHDesktop/extensionRegistry");
		if (conf == null) {
			File confFile = new File(Launch.getConfD(), "JSHDesktop/extensionRegistry.conf");
			if (!confFile.exists()) {
				try {
					confFile.getParentFile().mkdirs();
					confFile.createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
			conf = new Configuration(confFile);
			conf.setValue(".conf", "textEdit");
			conf.setValue(".txt", "textEdit");
			conf.setValue(".log", "textEdit");
		}
	}

	public static boolean registerExtension(String extension, Command exec) {
		if (conf.getValue(extension) != null) {
			return false;
		}
		conf.setValue(extension, exec.getName());
		return true;
	}

	public static void unRegisterExtension(String extension) {
		conf.removeKey(extension);
	}

	public static Command getExtensionExecutor(String extension) {
		try {
			return Launch.getCmds().get(conf.getValue(extension));
		} catch (Exception e) {
			return null;
		}
	}

}
