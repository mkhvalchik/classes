package common_classes;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

/**
*
* @author Marie
*/
public class Bullet extends MovableObject {
	/**
	 * damage made by this bullet
	 */
	public int damage;
	
	/**
	 * List of images for each angle of direction.
	 */
	ArrayList<Image> imgs;
	
	public Bullet(Graphics2D g2, ArrayList<Image> imgs, int x, int y, int speed,
		   int direction, int damage, int screenSizeX, int screenSizeY) {
		super(g2, imgs.get(0), x, y, speed, direction, screenSizeX, screenSizeY);
        this.damage = damage;
        this.imgs = imgs;
    }
	
	/**
	 * If bullet goes out of the screen borders, we can delete it.
	 */
	@Override
	public void update() {
		super.update();
		if (((y > screenSizeX) || (y < 0)) && frame > 1000) {
			is_deleted = true;
		}
	}
	
	/**
	 * Draws the bullet depending on the angle.
	 */
	@Override
	public void draw(ImageObserver obs) {
		g2.drawImage(imgs.get(Math.round(direction * (imgs.size() - 1) / 360)), x, y, obs);
    }
}
