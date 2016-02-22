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
public class Block extends GameObject {
	/**
	 * Health of the block.
	 */
	int health;
	
	/**
	 * If true, the block can be destroyed
	 */
	boolean destructable;
	
	/**
	 * If true, the block is visible
	 */
	boolean is_visible = true;
	
	/**
	 * Frame the block was destroyed at.
	 */
	int destroyed_frame = -1;
	
	/**
	 * Frame count from the object creation.
	 */
	int frame;
	
	public Block(Graphics2D g2, Image img, int x, int y, int screenSizeX, int screenSizeY, int health,
			boolean destructable) {
        super(g2, img, x, y, screenSizeX, screenSizeY);
        this.health = health;
        this.destructable = destructable;
    }
	
	/**
	 * Makes the destroyed block visible.
	 */
	@Override
	public void update() {
		if (frame - destroyed_frame > 500) {
			is_visible = true;
		}
		frame++;
	}
	
	/**
	 * Draws an object in case if it is visible.
	 *
	 * @param obs - ImageObserver required to draw the object
	 */
	@Override
	public void draw(ImageObserver obs) {
      if (is_visible) super.draw(obs);
	}
	
	/**
	 * Reduces the health of the block
	 * 
	 * @param damage - the health will be reduced by this value
	 */
	public void reduceHealth(int damage) {
		if (destructable) {
			health -= damage;
		}
		if (health <= 0) {
			is_visible = false;
			destroyed_frame = frame;
		}
	}
}
