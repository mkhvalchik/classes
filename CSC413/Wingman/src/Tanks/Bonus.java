package Tanks;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

import common_classes.GameObject;

/**
*
* @author Marie
*/
public class Bonus extends GameObject {
	/**
	 * Type of the bonus. 0 - missle, 2 - shield, 3 - health
	 */
	int type;
	
	/**
	 * Frame count from the object creation.
	 */
	int frame;
	
	public Bonus(Graphics2D g2, Image img, int x, int y, int screenSizeX, int screenSizeY, int type) {
        super(g2, img, x, y, screenSizeX, screenSizeY);
        this.type = type;
    }
	
	/**
	 * Deletes the bonus if it exists for too long.
	 */
	@Override
	public void update() {
		if (frame > 1500) {
			is_deleted = true;
		}
		frame++;
	}
}
