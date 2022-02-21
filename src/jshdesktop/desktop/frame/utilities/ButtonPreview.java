package jshdesktop.desktop.frame.utilities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.UIManager;

import jshdesktop.com.pump.plaf.PulsingCirclesThrobberUI;
import jshdesktop.com.pump.plaf.button.BevelButtonUI;
import jshdesktop.com.pump.swing.JThrobber;
import jshdesktop.desktop.frame.BasicFrame;

public class ButtonPreview extends BasicFrame {

	@Override
	public void create() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());

		JButton beveledButton = new JButton();
		beveledButton.setUI(new BevelButtonUI());

		JButton roundRectButton = new JButton();
		roundRectButton.setUI(new jshdesktop.com.pump.plaf.button.RoundRectButtonUI());
		UIManager.getDefaults().putIfAbsent("ThrobberUI", new PulsingCirclesThrobberUI());

		JThrobber throbber = new JThrobber();
		throbber.setForeground(Color.white);
		throbber.setSize(new Dimension(200, 200));
		throbber.setActive(true);
		throbber.setVisible(true);

		buttonPanel.add(beveledButton);
		buttonPanel.add(roundRectButton);
		buttonPanel.add(throbber);

		add(buttonPanel);
		setSize(500, 500);
		finish();
	}

}
