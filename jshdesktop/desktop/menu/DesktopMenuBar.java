package jshdesktop.desktop.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import jshdesktop.desktop.DecoratedDesktopPane;
import jshdesktop.desktop.frame.utilities.TextEditor;
import jshdesktop.desktop.frame.utilities.VirtualConsole;
import terra.shell.modules.ModuleEvent;

public class DesktopMenuBar extends JMenuBar {
	private final JMenu menu;

	public DesktopMenuBar(DecoratedDesktopPane p) {
		super();
		this.menu = new JMenu("Actions");

		JMenuItem endSession = new JMenuItem();
		endSession.setText("End Session");
		endSession.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ModuleEvent("jshdesktop", "END_SESSION");
			}
		});

		JMenuItem launchTestFrame = new JMenuItem("TextEditor");
		launchTestFrame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new TextEditor();
			}
		});

		JMenuItem launchVirtualConsole = new JMenuItem("Virtual Console");
		launchVirtualConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VirtualConsole();
			}
		});

		menu.add(endSession);
		menu.add(launchTestFrame);
		menu.add(launchVirtualConsole);
		this.add(menu);
	}
}
