package jshdesktop.desktop.frame;

import javax.swing.JInternalFrame;

import jshdesktop.module;
import terra.shell.utils.system.JSHProcesses;

public abstract class BasicFrame extends JInternalFrame implements JProcessComponent {
	private JGuiProcess local;

	public BasicFrame() {
		setName("BasicFrame");
		this.local = new JGuiProcess(this);
		local.run();
		JSHProcesses.addProcess(local);
	}

	public String getName() {
		return "BasicFrame";
	}

	public void finish() {
		setVisible(true);
		setResizable(true);
		setClosable(true);
		module.getDesktopFrame().add(this);
		toFront();
	}

	public abstract void create();

}
