package Wingman;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

import common_classes.Bullet;
import common_classes.MovableObject;

/**
*
* @author Marie
*/
abstract public class Airplane extends MovableObject{
	/**
	 * current amount of health
	 */
	int health;
	
	/**
	 * max amount of health
	 */
	int initial_health;
	
	/**
	 * a list of bullets - we need to maintain it as a reference to add newly created bullets
	 */
	ArrayList<Bullet> bullet_list;
	
	/**
	 * a list of image frames
	 */
	ArrayList<Image> imgs;
	
	Airplane(Graphics2D g2, ArrayList<Image> imgs, int x, int y, int speed,
			int direction, int screenSizeX, int screenSizeY, int health, ArrayList<Bullet> bullet_list) {
        super(g2, imgs.get(0), x, y, speed, direction, screenSizeX, screenSizeY);
        this.health = health;
        this.bullet_list = bullet_list;
        this.imgs = imgs;
        this.initial_health = health;
    }
	
	public abstract void fire();
	
	/**
	 * Reduces the health of the plane
	 * 
	 * @param num - the health will be reduced by this value
	 */
	public void reduceHealth(int num) {
		health -= num;
		if (health <= 0) {
			is_deleted = true;
		}
	}
	
	/**
	 * Draws the object depending on the frame number - to imitate non-static sprite.
	 */
	@Override
	public void draw(ImageObserver obs) {
		g2.drawImage(imgs.get(frame  % imgs.size()), x, y, obs);
    }
}
