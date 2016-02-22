package common_classes;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

import common_classes.SoundObject;

/**
*
* @author Marie
*/
abstract public class GamePlay {
	/**
	 * array of all game objects
	 */
	protected ArrayList<GameObject> objects;
	
	/**
	 * elements of GUI
	 */
	protected GameInterface game_interface;
	
	/**
	 * key control handling
	 */
	protected KeyControl key;
	
	/**
	 * observer to draw the images
	 */
	protected ImageObserver obs;
	
	/**
	 * size x of the screen
	 */
	protected int sizeX;
	
	/**
	 * size y of the screen
	 */
	protected int sizeY;
	
	/**
	 * graphics object
	 */
	protected Graphics2D g2;
	
	/**
	 * background music
	 */
	SoundObject background;
	
	/**
	 * if game is finished, this is set to true
	 */
	public boolean game_done;
	
	public GamePlay(ImageObserver obs, KeyControl key, Graphics2D g2, int sizeX, int sizeY, GameInterface gi,
			String background_file_path) {
		this.key = key;
		this.obs = obs;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.g2 = g2;
		objects = new ArrayList<GameObject>();
		game_interface = gi;
		background = new SoundObject(background_file_path, true);
		game_done = false;
	}
	
	abstract public void start();
	
	/**
	 * Handles timeline - updates and draws all game objects
	 * 
	 */
	public void tick() {
		for (int i = 0; i < objects.size(); i++) {
			if (objects.get(i).is_deleted) {
				objects.remove(i);
				i--;
			} else {
				objects.get(i).update();
				objects.get(i).draw(obs);
			}
		}
		game_interface.update();
		game_interface.draw(obs);
	}
	
	/**
	 * Checks if collision happened
	 *
	 * @param o1 - the first object to check
	 * @param o2 - the second object to check
	 * @return - true if object collide with each other
	 */
	protected boolean collision(GameObject o1, GameObject o2) {
		Rectangle bbox = new Rectangle(o1.x, o1.y, o1.sizeX, o1.sizeY);
        Rectangle otherBBox = new Rectangle (o2.x, o2.y, o2.sizeX, o2.sizeY);
        if(bbox.intersects(otherBBox)) { 
            return true;
        }
        return false;
	}
}
