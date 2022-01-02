package jshdesktop.desktop.frame.utilities;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import jshdesktop.module;
import jshdesktop.desktop.frame.BasicFrame;

public class TextEditor extends BasicFrame {
	private static MenuListener listener;
	private static String[] fontList = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	private static java.awt.Font font;
	private static Integer[] fontSizes = { 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 20, 22, 24, 26, 28, 30,
			34, 38, 42, 46, 50, 56, 64, 72 };
	private static float fontSize = 12;
	private static JTextArea area;
	private static File curFile;
	private static JComboBox<String> fonts;
	private static JComboBox<Integer> fontSizeBox;

	@Override
	public void create() {
		fontList = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

		listener = new MenuListener();
		JPanel editor = new JPanel();
		area = new JTextArea();
		area.setColumns(50);
		area.setRows(70);
		area.setEditable(true);
		JScrollPane editorScroller = new JScrollPane(area);
		editor.add(editorScroller);
		setJMenuBar(createMenuBar());

		JPanel editorOptionPanel = new JPanel();
		JPanel fontPanel = createFontPanel();
		editorOptionPanel.add(fontPanel, BorderLayout.EAST);

		area.setFont(font);

		add(editor, BorderLayout.CENTER);
		add(editorOptionPanel, BorderLayout.NORTH);
		setSize(600, 750);
		finish();
	}

	private JPanel createFontPanel() {
		JPanel fontPanel = new JPanel();

		font = Font.decode(fontList[0]).deriveFont(12l);

		fonts = new JComboBox<String>(fontList);
		fontSizeBox = new JComboBox<Integer>(fontSizes);
		fontSizeBox.setSelectedIndex(8);

		fonts.setActionCommand("font");
		fontSizeBox.setActionCommand("fontSize");

		fonts.addActionListener(listener);
		fontSizeBox.addActionListener(listener);

		fontPanel.add(fonts, BorderLayout.EAST);
		fontPanel.add(fontSizeBox, BorderLayout.CENTER);

		JPanel fontOptions = new JPanel();
		JButton optionBold = new JButton("B");
		optionBold.setFont(font.deriveFont(Font.BOLD));

		JButton optionItalic = new JButton("I");
		optionItalic.setFont(font.deriveFont(Font.ITALIC));

		optionBold.setActionCommand("fontBold");
		optionItalic.setActionCommand("fontItalic");

		optionBold.addActionListener(listener);
		optionItalic.addActionListener(listener);

		fontOptions.add(optionBold, BorderLayout.EAST);
		fontOptions.add(optionItalic, BorderLayout.WEST);

		fontPanel.add(fontOptions, BorderLayout.WEST);

		return fontPanel;
	}

	private JMenuBar createMenuBar() {
		JMenuBar menu = new JMenuBar();

		JMenu file = new JMenu("File");
		JMenuItem save = new JMenuItem("Save");
		JMenuItem saveAs = new JMenuItem("Save As");
		JMenuItem load = new JMenuItem("Load");

		save.setActionCommand("save");
		saveAs.setActionCommand("saveAs");
		load.setActionCommand("load");

		save.addActionListener(listener);
		saveAs.addActionListener(listener);
		load.addActionListener(listener);

		file.add(save);
		file.add(saveAs);
		file.add(load);

		JMenu prop = new JMenu("Properties");
		JMenuItem preferences = new JMenuItem("Preferences");

		preferences.setActionCommand("pref");

		preferences.addActionListener(listener);

		prop.add(preferences);

		menu.add(file);
		menu.add(prop);
		return menu;
	}

	private void loadFile(File f) {
		try {
			PopupProgressBar prog = new PopupProgressBar();
			module.getDesktopFrame().add(prog);
			FileInputStream fin = new FileInputStream(f);
			Scanner sc = new Scanner(fin);
			while (sc.hasNextLine()) {
				area.append(sc.nextLine() + "\n");
			}
			sc.close();
			curFile = f;
			module.getDesktopFrame().remove(prog);
			prog.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean saveFile() {
		return saveFile(curFile);
	}

	private boolean saveFile(File f) {
		try {
			PopupProgressBar prog = new PopupProgressBar();
			module.getDesktopFrame().add(prog);
			FileOutputStream fOut = new FileOutputStream(f);
			PrintStream out = new PrintStream(fOut);
			String text = area.getText();
			out.print(text);
			out.flush();
			out.close();
			module.getDesktopFrame().remove(prog);
			prog.dispose();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private class MenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			String cmd = e.getActionCommand();
			switch (cmd) {
			case "save":
				if (curFile == null) {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					fileChooser.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							JInternalFrame parent = (JInternalFrame) SwingUtilities
									.getAncestorOfClass(JInternalFrame.class, fileChooser);
							if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
								parent.dispose();
								return;
							} else if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
								String fileName = JOptionPane.showInternalInputDialog(parent, "File Name");
								File file = new File(fileChooser.getSelectedFile(), fileName);
								if (file.exists()) {
									int resp = JOptionPane.showInternalConfirmDialog(parent, "File Exists, Overwrite?");
									if (resp != JOptionPane.YES_OPTION)
										return;
								}
								saveFile(file);
								curFile = file;
							}
							parent.dispose();
						}
					});
					JInternalFrame intFileChooser = new JInternalFrame();
					intFileChooser.setContentPane(fileChooser);
					intFileChooser.setSize(200, 300);
					intFileChooser.setVisible(true);
					intFileChooser.setResizable(true);
					module.getDesktopFrame().add(intFileChooser);
					return;
				}
				if (!saveFile()) {
					JOptionPane.showInternalMessageDialog(area, "Failed to save file");
				}
				break;
			case "saveAs":
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JInternalFrame parent = (JInternalFrame) SwingUtilities.getAncestorOfClass(JInternalFrame.class,
								fileChooser);
						if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
							parent.dispose();
							return;
						} else if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
							String fileName = JOptionPane.showInternalInputDialog(parent, "File Name");
							File file = new File(fileChooser.getSelectedFile(), fileName);
							if (file.exists()) {
								int resp = JOptionPane.showInternalConfirmDialog(parent, "File Exists, Overwrite?");
								if (resp != JOptionPane.YES_OPTION)
									return;
							}
							saveFile(new File(fileChooser.getSelectedFile(), fileName));
						}
						parent.dispose();
					}
				});
				JInternalFrame intFileChooser1 = new JInternalFrame();
				intFileChooser1.setContentPane(fileChooser);
				intFileChooser1.setSize(200, 300);
				intFileChooser1.setVisible(true);
				intFileChooser1.setResizable(true);
				module.getDesktopFrame().add(intFileChooser1);
				break;
			case "load":
				fileChooser = new JFileChooser();
				fileChooser.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JInternalFrame parent = (JInternalFrame) SwingUtilities.getAncestorOfClass(JInternalFrame.class,
								fileChooser);
						if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
							parent.dispose();
							return;
						} else if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
							loadFile(fileChooser.getSelectedFile());
						}
						parent.dispose();
					}
				});
				JInternalFrame intFileChooser = new JInternalFrame();
				intFileChooser.setContentPane(fileChooser);
				intFileChooser.setSize(200, 300);
				intFileChooser.setVisible(true);
				intFileChooser.setResizable(true);
				module.getDesktopFrame().add(intFileChooser);
				break;
			case "pref":
				break;
			case "font":
				font = Font.decode(fontList[fonts.getSelectedIndex()]).deriveFont(fontSize);
				area.setFont(font);
				break;
			case "fontSize":
				fontSize = Integer.parseInt(fontSizeBox.getSelectedItem().toString());
				font = font.deriveFont(fontSize);
				area.setFont(font);
				break;
			case "fontBold":
				if (!font.isBold())
					font = font.deriveFont(Font.BOLD);
				else
					font = font.deriveFont(Font.PLAIN);
				area.setFont(font);
				break;
			case "fontItalic":
				if (!font.isItalic())
					font = font.deriveFont(Font.ITALIC);
				else
					font = font.deriveFont(Font.PLAIN);
				area.setFont(font);
				break;
			}
		}

	}

}
