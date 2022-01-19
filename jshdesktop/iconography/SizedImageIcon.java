package jshdesktop.iconography;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public class SizedImageIcon extends ImageIcon {
	private int width, height;

	public SizedImageIcon(String path) {
		super(path);
		Image i = getImage().getScaledInstance(20, 20, BufferedImage.SCALE_FAST);
		setImage(i);
	}

	public SizedImageIcon(String path, int width, int height) {
		super(path);
		this.width = width;
		this.height = height;
		Image i = getImage().getScaledInstance(width, height, BufferedImage.SCALE_FAST);
		setImage(i);
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public void setDimension(int width, int height) {
		this.width = width;
		this.height = height;
		Image i = getImage().getScaledInstance(width, height, BufferedImage.SCALE_FAST);
		setImage(i);
	}

}
