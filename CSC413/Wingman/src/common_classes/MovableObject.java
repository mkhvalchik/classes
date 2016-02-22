package common_classes;

import java.awt.Graphics2D;
import java.awt.Image;

/**
*
* @author Marie
*/
public abstract class MovableObject extends GameObject {
	/**
	 * speed of the object
	 */
	public int speed;
	
	/**
	 * direction of the movement
	 */
	public int direction; // angle in degrees
	
	/**
	 * frame number
	 */
	protected int frame;
	
	protected MovableObject(Graphics2D g2, Image img, int x, int y, int speed,
			int direction, int screenSizeX, int screenSizeY) {
        super(g2, img, x, y, screenSizeX, screenSizeY);
        this.speed = speed;
        this.direction = direction;
    }
	
	/**
	 * Moves the object depending on the speed and direction.
	 */
	public void move() {
		x += Math.round(Math.cos(direction * Math.PI / 180) * speed);
		y -= Math.round(Math.sin(direction * Math.PI / 180) * speed);
	}
	
	/**
	 * Updates the object - calls move and increments frame number
	 */
	@Override
	public void update() {
		move();
		frame++;
	}
}
