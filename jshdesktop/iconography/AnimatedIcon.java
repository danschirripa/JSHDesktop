package jshdesktop.iconography;

import java.awt.Image;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;

public class AnimatedIcon extends ImageIcon {
	private Image[] images;
	private int index = 0;
	private AnimatedIcon me;
	private Timer t;
	private long refreshRate;

	public AnimatedIcon(AnimatedIcon icon) {
		this(icon.getRefreshRate(), icon.getFrames());
	}

	public AnimatedIcon(long refreshRate, Image... images) {
		me = this;
		this.refreshRate = refreshRate;
		this.images = images;
		t = new Timer();
	}

	public void start() {
		t.cancel();
		t = new Timer();
		TimerTask updateImage = new TimerTask() {
			public void run() {
				index++;
				if (index >= images.length)
					index = 0;
				me.setImage(images[index]);
			}
		};
	}

	public void stop() {
		t.cancel();
	}

	public Image[] getFrames() {
		return images;
	}

	public long getRefreshRate() {
		return refreshRate;
	}
}
