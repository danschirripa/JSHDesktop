package jshdesktop.commands;

import java.util.ArrayList;

import jshdesktop.desktop.frame.utilities.TextEditor;
import terra.shell.command.Command;
import terra.shell.utils.perms.Permissions;

public class LaunchTextEditorCommand extends Command {

	@Override
	public String getName() {
		return "textEdit";
	}

	@Override
	public String getVersion() {
		return "0.1";
	}

	@Override
	public String getAuthor() {
		return "D.S";
	}

	@Override
	public String getOrg() {
		return "java-shell";
	}

	@Override
	public boolean isBlocking() {
		return false;
	}

	@Override
	public ArrayList<String> getAliases() {
		return null;
	}

	@Override
	public ArrayList<Permissions> getPerms() {
		return null;
	}

	@Override
	public boolean start() {
		TextEditor te = new TextEditor();
		if (args != null && args.length != 0) {
			String filePath = args[0];
			return te.open(filePath);
		}
		return true;
	}

}