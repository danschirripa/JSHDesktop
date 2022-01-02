package jshdesktop.desktop.frame;

import terra.shell.utils.JProcess;

public class JGuiProcess extends JProcess {
	private final JProcessComponent comp;

	public JGuiProcess(JProcessComponent comp) {
		this.comp = comp;
		getLogger().debug("Created JProcessComponent");
	}

	@Override
	public String getName() {
		return "JGuiProcess";
	}

	@Override
	public boolean start() {
		getLogger().debug("Running create()");
		comp.create();
		getLogger().debug("Done");
		return true;
	}

}
