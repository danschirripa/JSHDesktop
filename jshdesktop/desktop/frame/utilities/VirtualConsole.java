package jshdesktop.desktop.frame.utilities;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultCaret;

import jshdesktop.desktop.frame.BasicFrame;
import terra.shell.command.Command;
import terra.shell.command.Terminal;
import terra.shell.launch.Launch;
import terra.shell.logging.LogManager;
import terra.shell.logging.Logger;

public class VirtualConsole extends BasicFrame {
	private Hashtable<String, Command> cmds;
	private static JTextArea outPut;
	private static Terminal t;
	private static EmulatedOutputStream eOut;
	private static Logger log = LogManager.getLogger("VirtualConsole");

	public VirtualConsole() {
		setTitle("vterm");
		cmds = Launch.getCmds();
		Thread th = new Thread(new Runnable() {
			public void run() {
				t = new Terminal();
				eOut = new EmulatedOutputStream();
				t.setGOutputStream(eOut);
				t.setOutputStream(eOut);
			}
		});
		Thread outMon = new Thread(new Runnable() {
			public void run() {
				while (outPut == null)
					;
				log.debug("Started monitoring");
				BlockingInputStream bin = new BlockingInputStream(eOut);
				int i;
				try {
					while ((i = bin.read()) != -2) {
						char c = (char) i;
						outPut.append(c + "");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		th.start();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		outMon.start();
	}

	@Override
	public void create() {
		JPanel mainPanel = new JPanel();
		JPanel inputPanel = new JPanel();
		outPut = new JTextArea();
		outPut.setColumns(40);
		outPut.setRows(20);
		outPut.setEditable(false);

		DefaultCaret caret = (DefaultCaret) outPut.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		outPut.setCaret(caret);

		JTextField inPut = new JTextField();
		inPut.setColumns(20);
		JButton send = new JButton(">");

		inputPanel.add(inPut, BorderLayout.CENTER);
		inputPanel.add(send, BorderLayout.EAST);

		JScrollPane outPutScroller = new JScrollPane(outPut);
		outPutScroller.setPreferredSize(new Dimension(450, 400));

		mainPanel.add(outPutScroller, BorderLayout.CENTER);
		mainPanel.add(inputPanel, BorderLayout.SOUTH);
		ActionListener sendActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String commandFull = inPut.getText();
				String[] commandPart = commandFull.split(" ");
				String cmd = commandPart[0];
				commandPart = Arrays.copyOfRange(commandPart, 1, commandPart.length);
				inPut.setText("");
				t.runCmd(cmd, commandPart);
			}
		};

		send.addActionListener(sendActionListener);

		registerKeyboardAction(sendActionListener, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Component c = e.getComponent();
				int w = c.getSize().width - 50;
				int h = c.getSize().height - 100;
				outPutScroller.setPreferredSize(new Dimension(w, h));
			}
		});

		add(mainPanel);
		setSize(500, 500);
		finish();
	}

	private class EmulatedOutputStream extends OutputStream {
		private LinkedList<Integer> bytes = new LinkedList<Integer>();

		@Override
		public void write(int b) throws IOException {
			bytes.add(b);
		}

		public int read() throws InterruptedException {
			while (bytes.size() == 0)
				Thread.sleep(100);
			int out = bytes.pop();
			return out;
		}

	}

	private class EmulatedInputStream extends InputStream {
		private LinkedList<Integer> bytes = new LinkedList<Integer>();

		public void writeTo(byte b) {
			bytes.add((int) b);
		}

		public void writeTo(byte[] b) {
			for (byte by : b)
				bytes.add((int) by);
		}

		@Override
		public int read() throws IOException {
			while (bytes.size() == 0)
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			int out = bytes.pop();
			return out;
		}

	}

	private class BlockingInputStream extends InputStream {
		private EmulatedOutputStream out;

		public BlockingInputStream(EmulatedOutputStream out) {
			this.out = out;
		}

		@Override
		public int read() throws IOException {
			try {
				int outI = out.read();
				return outI;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return -2;
		}

	}

}
