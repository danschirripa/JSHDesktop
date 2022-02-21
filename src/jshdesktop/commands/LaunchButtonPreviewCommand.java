package jshdesktop.commands;

import java.util.ArrayList;

import jshdesktop.desktop.frame.utilities.ButtonPreview;
import terra.shell.command.BasicCommand;
import terra.shell.utils.perms.Permissions;

public class LaunchButtonPreviewCommand extends BasicCommand {

	@Override
	public String getName() {
		return "buttonPreview";
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
		new ButtonPreview();
		return false;
	}

}
