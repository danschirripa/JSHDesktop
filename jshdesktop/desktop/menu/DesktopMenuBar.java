package jshdesktop.desktop.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import jshdesktop.desktop.DecoratedDesktopPane;
import jshdesktop.desktop.frame.utilities.SettingsPanel;
import jshdesktop.desktop.frame.utilities.TextEditor;
import jshdesktop.desktop.frame.utilities.VirtualConsole;
import jshdesktop.widgets.builtin.AnalogClockWidget;
import jshdesktop.widgets.builtin.DigitalClockWidget;
import terra.shell.modules.ModuleEvent;

public class DesktopMenuBar extends JMenuBar {
	private final JMenu menu;

	public DesktopMenuBar(DecoratedDesktopPane p) {
		super();
		ImageIcon icon = new ImageIcon();

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

		JMenuItem launchSettings = new JMenuItem("Settings");
		launchSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SettingsPanel();
			}
		});

		JMenuItem launchClockWidget = new JMenuItem("Analog Clock");
		launchClockWidget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AnalogClockWidget();
			}
		});

		JMenuItem launchDigitalClockWidget = new JMenuItem("Digital Clock");
		launchDigitalClockWidget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new DigitalClockWidget();
			}
		});

		menu.add(endSession);
		menu.add(launchTestFrame);
		menu.add(launchVirtualConsole);
		menu.add(launchSettings);
		menu.add(launchClockWidget);
		menu.add(launchDigitalClockWidget);

		JMenuItem clock = new JMenuItem();
		clock.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("E hh:mma")));

		Timer t = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				clock.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("E hh:mma")));
			}
		};
		t.scheduleAtFixedRate(task,
				Time.valueOf(LocalTime.of(LocalTime.now().getHour(), LocalTime.now().getMinute() + 1)), (60 * 1000));

		this.add(clock);
		this.add(menu);
	}
}
