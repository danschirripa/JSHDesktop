package jshdesktop.commands;

import java.io.File;
import java.util.ArrayList;

import jshdesktop.desktop.frame.utilities.ImageViewer;
import terra.shell.command.Command;
import terra.shell.utils.perms.Permissions;

public class LaunchImageViewerCommand extends Command {

	@Override
	public String getName() {
		return "imageViewer";
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return "0.1";
	}

	@Override
	public String getAuthor() {
		// TODO Auto-generated method stub
		return "D.S";
	}

	@Override
	public String getOrg() {
		// TODO Auto-generated method stub
		return "java-shell";
	}

	@Override
	public boolean isBlocking() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ArrayList<String> getAliases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Permissions> getPerms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean start() {
		if (args != null && args.length > 0) {
			String filePath = args[0];
			try {
				new ImageViewer(new File(filePath));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		new ImageViewer();
		return true;
	}

}
