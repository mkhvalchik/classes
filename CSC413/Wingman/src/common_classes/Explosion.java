package common_classes;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

/**
*
* @author Marie
*/
public class Explosion extends GameObject {
	protected int frame;
	
	/**
	 * This is number of "real-time" frames which each explosion frame will be on the screen.
	 */
	int delay;
	
	/**
	 * List of images with each explosion frame.
	 */
	ArrayList<Image> imgs;
	
	/**
	 * Sound object to play during the explosion.
	 */
	SoundObject snd;
	
	public Explosion(Graphics2D g2, ArrayList<Image> imgs, int x, int y, int screenSizeX, int screenSizeY,
			String sound_path) {
        super(g2, imgs.get(0), x, y, screenSizeX, screenSizeY);
        this.imgs = imgs;
        delay = 5;
        snd = new SoundObject(sound_path, false);
    }
	
	/**
	 * Updates the frame number.
	 *
	 */
	@Override
	public void update() {
		frame++;
	}
	
	/**
	 * Draws an object.
	 *
	 * Each explosion consists of the sequence of frames and this function draws each frame depending
	 * on the timeline.
	 *
	 * @param obs - ImageObserver required to draw the tiles
	 */
	@Override
	public void draw(ImageObserver obs) {
		g2.drawImage(imgs.get((frame / delay)  % imgs.size()), x, y, obs);
		if (frame > delay && (frame / delay)  % imgs.size() == 0) {
			is_deleted = true;
		}
    }
}
