package jshdesktop.commands;

import java.util.ArrayList;

import jshdesktop.desktop.frame.utilities.Updater;
import terra.shell.command.Command;
import terra.shell.utils.perms.Permissions;

public class LaunchUpdaterCommand extends Command {

	@Override
	public String getName() {
		return "update";
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return "1.0";
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
		new Updater();
		return true;
	}

}
