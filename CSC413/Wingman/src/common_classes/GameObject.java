package common_classes;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.Observable;
import java.util.Observer;

/**
*
* @author Marie
*/
public abstract class GameObject implements Observer {
	/**
	 * image to draw.
	 */
	Image img;
	
	/**
	 * x coordinate of left upper corner of the image
	 */
	public int x;
	
	/**
	 * y coordinate of left upper corner of the image
	 */
	public int y;
	
	/**
	 * size x of the image
	 */
	public int sizeX;
	
	/**
	 * size y of the image
	 */
	public int sizeY;
	
	/**
	 * graphics object
	 */
	protected Graphics2D g2;
	
	/**
	 * x size of the screen
	 */
	protected int screenSizeX;
	
	/**
	 * y size of the screen
	 */
	protected int screenSizeY;
	
	/**
	 * if object can be garbage collected
	 */
	public boolean is_deleted;
	
	protected GameObject(Graphics2D g2, Image img, int x, int y, int screenSizeX, int screenSizeY) {
        this.img = img;
        this.x = x;
        this.y = y;
        this.g2 = g2;
        if (img != null) {
	        sizeX = img.getWidth(null);
	        sizeY = img.getHeight(null);
        }
        this.screenSizeX = screenSizeX;
        this.screenSizeY = screenSizeY;
        this.is_deleted = false;
    }
	
	/**
	 * Draws an object.
	 *
	 * @param obs - ImageObserver required to draw the object
	 */
	public void draw(ImageObserver obs) {
       g2.drawImage(img, x, y, obs);
    }
	
	public abstract void update();
	
	public void update(Observable obj, Object arg) {
	}
}
