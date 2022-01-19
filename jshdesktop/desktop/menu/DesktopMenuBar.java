package jshdesktop.desktop.menu;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import jshdesktop.DesktopManager;
import jshdesktop.module;
import jshdesktop.desktop.DecoratedDesktopPane;
import jshdesktop.desktop.frame.utilities.SettingsPanel;
import jshdesktop.desktop.frame.utilities.TextEditor;
import jshdesktop.desktop.frame.utilities.VirtualConsole;
import jshdesktop.desktop.frame.utilities.filemanager.FileManager;
import jshdesktop.widgets.builtin.AnalogClockWidget;
import jshdesktop.widgets.builtin.CommandWidget;
import jshdesktop.widgets.builtin.DigitalClockWidget;
import terra.shell.command.Command;
import terra.shell.config.Configuration;
import terra.shell.launch.Launch;
import terra.shell.modules.ModuleEvent;
import terra.shell.utils.JProcess;

public class DesktopMenuBar extends JMenuBar {
	private final JMenu menu;
	private Configuration conf;
	private DecoratedDesktopPane p;

	public DesktopMenuBar(DecoratedDesktopPane p) {
		super();
		this.p = p;
		conf = Launch.getConfig("JSHDesktop/menuBar.conf");
		if (conf == null) {
			File confFile = new File(Launch.getConfD(), "/JSHDesktop/menuBar.conf");
			if (!confFile.exists()) {
				try {
					confFile.getParentFile().mkdirs();
					confFile.createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			conf = new Configuration(confFile);
			conf.setValue("enableCommandWidgets", "true");
			conf.setValue("commandWidgetConfDir", confFile.getParentFile().getAbsolutePath() + "/widgets");
			conf.setValue("actionsMenuIcon", "default");
		}

		ImageIcon icon = null;
		String iconPath = (String) conf.getValue("actionsMenuIcon");
		if (iconPath.equals("default")) {
			iconPath = Launch.getConfD().getParent() + "/modules/jshdesktop/assets/icons/actions_icon.png";
		}
		File iconFile = new File(iconPath);
		if (iconFile.exists()) {
			try {
				icon = new ImageIcon();
				Image iconImage = ImageIO.read(new File(iconPath));
				icon.setImage(iconImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (icon == null)
			this.menu = new JMenu("Actions");
		else {
			this.menu = new JMenu();
			this.menu.setIcon(icon);
		}

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

		JMenuItem launchFileBrowser = new JMenuItem("Files");
		launchFileBrowser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new FileManager();
			}
		});

		menu.add(endSession);
		menu.add(launchFileBrowser);
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

		if (((String) conf.getValue("enableCommandWidgets")).equals("true")) {
			loadMenuWidgets();
		}
	}

	private void loadMenuWidgets() {
		File widgetConfDir = new File((String) conf.getValue("commandWidgetConfDir"));
		if (!widgetConfDir.exists()) {
			widgetConfDir.mkdirs();
		}
		File[] widgetConfigs = widgetConfDir.listFiles();
		for (File confFile : widgetConfigs) {
			try {
				Configuration conf = new Configuration(confFile);
				String command = (String) conf.getValue("widgetCommand");
				String iconPath = (String) conf.getValue("iconPath");

				Command c = Launch.getCmds().get(command);
				if (c == null)
					throw new NullPointerException("Command not found " + command);

				if (iconPath.startsWith("internal;")) {
					iconPath = iconPath.replaceAll("internal;",
							Launch.getConfD().getParent() + "/modules/jshdesktop/assets");
				}

				File iconFile = new File(iconPath);
				Image icon = ImageIO.read(iconFile);

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new MenuWidget(c, icon, conf);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	public void setCursor(Cursor c) {
		super.setCursor(c);
		menu.setCursor(c);
		for (Component comp : menu.getMenuComponents())
			comp.setCursor(c);
		;
	}

	protected static int numWidgets = -1;
	protected static int numColumns = 20;

	private class MenuWidget extends CommandWidget {
		private Configuration conf;

		public MenuWidget(JProcess command, Image icon, Configuration conf) {
			super(command, icon);
			this.conf = conf;
		}

		@Override
		public void create() {
			numWidgets++;
			while (module.getDesktopFrame() == null)
				try {
					Thread.sleep(50);
				} catch (Exception e) {
					e.printStackTrace();
				}
			int numRows = (50 * numWidgets) + 25;
			if (numRows > DesktopManager.getScreenWidth()) {
				numColumns = numColumns + 80;
				numWidgets = 0;
				numRows = (50 * numWidgets) + 10;
			}
			setLocation(numRows, numColumns);
			finish();
		}

	}
}
