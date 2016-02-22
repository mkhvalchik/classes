package Wingman;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Observable;

import javax.imageio.ImageIO;

import common_classes.Bullet;
import common_classes.GameEvents;

/**
*
* @author Marie
*/
public class PlayerAirplane extends Airplane {
	/**
	 * number of lives for this plane
	 */
	int lives;
	
	/**
	 * list of bullet frames
	 */
	Image bullet_img;
	
	/**
	 * last time this plane fired (to prevent non-stoppable firing)
	 */
	int last_fire_frame;
	
	/**
	 * id of the player
	 */
	int player_id;

	PlayerAirplane(Graphics2D g2, ArrayList<Image> imgs, int x, int y,
			int direction, int screenSizeX, int screenSizeY, int health, ArrayList<Bullet> bullet_list, int lives,
			int player_id) {
        super(g2, imgs, x, y, 10, 90, screenSizeX, screenSizeY, health, bullet_list);
        this.lives = lives;
        this.player_id = player_id;
        try {
        	bullet_img = ImageIO.read(new File("Resources/bullet.png"));
        }
        catch (Exception e) {
            System.out.print("No resources are found");
        }
        initial_health = health;
    }
	
	/**
	 * Updates this object depending on the type of the event which came and player id of the plane
	 */
	public void update(Observable obj, Object arg) {
        GameEvents ge = (GameEvents) arg;
        if((ge.type == 1) && (player_id == 1)) {
            KeyEvent e = (KeyEvent) ge.event;
            switch (e.getKeyCode()) {    
                case KeyEvent.VK_LEFT:
                    x -= speed;
                    break; 
                case KeyEvent.VK_RIGHT:
                    x += speed;
                    break;
                case KeyEvent.VK_UP:
                    y -= speed;
                    break; 
                case KeyEvent.VK_DOWN:
                    y += speed;
                    break;
                default:
                	if(e.getKeyChar() == ' ') {
                		fire();  
              }
            }
        }
        if((ge.type == 1) && (player_id == 2)) {
        	KeyEvent e = (KeyEvent) ge.event;
        	switch (e.getKeyCode()) {    
            case KeyEvent.VK_A:
                x -= speed;
	    	break; 
	            case KeyEvent.VK_D:
	                x += speed;
	    	break;
	            case KeyEvent.VK_W:
	                y -= speed;
	    	break; 
	            case KeyEvent.VK_S:
	                y += speed;
	        break; 
	            case KeyEvent.VK_CONTROL:
	            	fire();
            }
        }
    }
	
	/**
	 * Fires a new bullet.
	 */
	public void fire() {
		ArrayList<Image> imgs = new ArrayList<Image>();
		imgs.add(bullet_img);
		try {
			bullet_list.add(new Bullet(g2, imgs, x + (sizeX - bullet_img.getWidth(null)) / 2, y - bullet_img.getHeight(null) - 5, 10, 90,
					25, screenSizeX, screenSizeY));
			last_fire_frame = frame;
		} 
		catch (Exception e) {
            System.out.print("No resources are found");
        }
	}
	
	@Override
	public void move() {
	}
	
	/**
	 * reduces health and lives if health is under 0, if lives is under 0, the plane can be deleted.
	 */
	@Override
	public void reduceHealth(int num) {
		super.reduceHealth(num);
		if (health <= 0) {
			lives--;
			if (lives >= 0) {
				health = initial_health;
			}
		}
		if (lives < 0) {
			is_deleted = true;
		} else {
			is_deleted = false;
		}
	}
}
