package jshdesktop;

import javax.swing.SwingUtilities;

import jshdesktop.commands.LaunchButtonPreviewCommand;
import jshdesktop.commands.LaunchImageViewerCommand;
import jshdesktop.commands.LaunchTextEditorCommand;
import jshdesktop.commands.LaunchUpdaterCommand;
import jshdesktop.commands.LaunchVirtualConsoleCommand;
import jshdesktop.desktop.frame.DialogFrame;
import jshdesktop.desktop.frame.DialogFrame.DialogType;
import terra.shell.launch.Launch;
import terra.shell.modules.ModuleEvent;
import terra.shell.modules.ModuleEvent.DummyEvent;
import terra.shell.utils.keys.Event;
import terra.shell.utils.system.GeneralVariable;
import terra.shell.utils.system.Variables;

public class module extends terra.shell.modules.Module {
	private static JDesktopFrame main;

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "jshdesktop";
	}

	@Override
	public void run() {
		log.debug("Run");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				main = new JDesktopFrame();
			}
		});
	}

	@Override
	public String getVersion() {
		return null;
	}

	@Override
	public String getAuthor() {
		return null;
	}

	@Override
	public String getOrg() {
		return null;
	}

	@Override
	public void onEnable() {
		Variables.setVar(new GeneralVariable("jshDesktop", "true"));
		log.log("Registering JSHDesktop commands...");
		LaunchTextEditorCommand textEdit = new LaunchTextEditorCommand();
		Launch.registerCommand(textEdit.getName(), textEdit);
		LaunchVirtualConsoleCommand vterm = new LaunchVirtualConsoleCommand();
		Launch.registerCommand(vterm.getName(), vterm);
		LaunchImageViewerCommand imageViewer = new LaunchImageViewerCommand();
		Launch.registerCommand(imageViewer.getName(), imageViewer);
		LaunchUpdaterCommand updater = new LaunchUpdaterCommand();
		Launch.registerCommand(updater.getName(), updater, null);
		LaunchButtonPreviewCommand buttonPreview = new LaunchButtonPreviewCommand();
		Launch.registerCommand("buttonPreview", buttonPreview);
	}

	@Override
	public void trigger(Event event) {
		log.debug("Got Event");
		if (event instanceof DummyEvent) {
			log.debug("Got ModuleEvent");
			ModuleEvent me = ((DummyEvent) event).getME();
			Object[] args = me.getArgs();
			if (args.length != 1 || !(args[0] instanceof String)) {
				log.err("Invalid arguments from ModuleEvent");
				return;
			}
			String arg = (String) args[0];
			if (arg.equals("END_SESSION")) {
				DialogFrame.createDialog(DialogType.OK, "Ending Session");
				main.setVisible(false);
				main = null;
			}
		}
	}

	public static JDesktopFrame getDesktopFrame() {
		return main;
	}

}
