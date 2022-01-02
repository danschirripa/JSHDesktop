package jshdesktop.desktop.frame.utilities.filemanager;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JList;
import javax.swing.JPanel;

import jshdesktop.desktop.frame.BasicFrame;

public class FileManager extends BasicFrame implements MouseListener{
	private File directory;
	private JList<File> dirList;

	@Override
	public void create() {
		String homeDir = System.getProperty("user.home");
		directory = new File(homeDir);

		File[] dirListing = directory.listFiles();

		dirList = new JList<File>(dirListing);
		getContentPane().add(dirList, BorderLayout.CENTER);

		JPanel sidePanel = new JPanel();
		getContentPane().add(sidePanel, BorderLayout.WEST);

		JPanel fileInfoPanel = new JPanel();
		getContentPane().add(fileInfoPanel, BorderLayout.EAST);

		JPanel topPanel = new JPanel();
		getContentPane().add(topPanel, BorderLayout.NORTH);
	}

	@Override
	public void mouseClicked(MouseEvent e) {		
	}

	@Override
	public void mousePressed(MouseEvent e) {		
	}

	@Override
	public void mouseReleased(MouseEvent e) {		
	}

	@Override
	public void mouseEntered(MouseEvent e) {		
	}

	@Override
	public void mouseExited(MouseEvent e) {		
	}

}
