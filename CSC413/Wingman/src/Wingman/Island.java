package Wingman;

import java.awt.Graphics2D;
import java.awt.Image;

import common_classes.MovableObject;

/**
*
* @author Marie
*/
public class Island extends MovableObject {
	Island(Graphics2D g2, Image img, int x, int y, int speed,
			int direction, int screenSizeX, int screenSizeY) {
        super(g2, img, x, y, speed, direction, screenSizeX, screenSizeY);
    }
	
	/**
	 * Checks if island can be deleted.
	 */
	@Override
	public void update() {
		super.update();
		if (((y > screenSizeX) || (y < 0)) && frame > 1000) {
			is_deleted = true;
		}
	}
}
