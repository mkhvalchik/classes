package Tanks;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.ArrayList;
import java.util.Observable;

import javax.imageio.ImageIO;

import common_classes.Bullet;
import common_classes.GameEvents;
import common_classes.MovableObject;

/**
*
* @author Marie
*/
public class Tank extends MovableObject{
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
	
	/**
	 * a list of bullet frames
	 */
	ArrayList<Image> bullet_imgs;
	
	/**
	 * a list of missle frames.
	 */
	ArrayList<Image> missle_imgs;
	
	/**
	 * image of the shield.
	 */
	Image shield_img;
	
	/**
	 * id of a player (1 or 2)
	 */
	int player_id;
	
	/**
	 * shows the direction of last move - either -1 (back) or 1 (forward)
	 */
	int last_move_vector = 0;
	
	/**
	 * shows the direction of last move - either -1 or 1
	 */
	int last_collision_frame = -1;
	
	/**
	 * type of active bonus
	 */
	int bonus_active = -1;
	
	/**
	 * Bonus count.
	 */
	int bonus_count = 0;
	
	/**
	 * x coordinate of another tank.
	 */
	int another_tank_x = 0;
	
	/**
	 * y coordinate of another tank.
	 */
	int another_tank_y = 0;
	
	/**
	 * tells whether the shield on tank is active.
	 */
	boolean shield_active = false;
	
	/**
	 * when shield was activated.
	 */
	int shield_actived_frame = 0;
	
	Tank(Graphics2D g2, ArrayList<Image> imgs, int x, int y, int speed,
			int direction, int screenSizeX, int screenSizeY, int health, ArrayList<Bullet> bullet_list, int player_id) {
        super(g2, imgs.get(0), x, y, speed, direction, screenSizeX, screenSizeY);
        this.health = health;
        this.bullet_list = bullet_list;
        this.imgs = imgs;
        this.initial_health = health;
        this.player_id = player_id;
        bullet_imgs = TankGamePlay.cropImage("Resources_tank/Shell_strip60.png");
        missle_imgs = TankGamePlay.cropImage("Resources_tank/Rocket_strip60.png");
        try {
        	shield_img = ImageIO.read(new File("Resources_tank/Shield1.png"));
        }
        catch (Exception e) {
            System.out.print("No resources are found");
        }      
    }
	
	/**
	 * fires normal weapon.
	 */
	public void fire() {
		try {
			int center_x = x + sizeX / 2 - 12;
			int center_y = y + sizeY / 2 - 12;
			int x_bullet = (int) (center_x + Math.round(Math.cos(direction * Math.PI / 180) * sizeX / 2));
			int y_bullet = (int) (center_y - Math.round(Math.sin(direction * Math.PI / 180) * sizeX / 2));
			bullet_list.add(new Bullet(g2, bullet_imgs, x_bullet, y_bullet, 6,
					Math.round(direction * (imgs.size() - 1) / 360) * 360 / imgs.size(),
					25, screenSizeX, screenSizeY));
		} 
		catch (Exception e) {
            System.out.print("No resources are found");
        }
	}
	
	/**
	 * fires secondary weapon.
	 */
	public void firebonus() {
		if (bonus_active == -1) {
			return;
		}
		try {
			bonus_count--;
			if (bonus_active == 2) {
				shield_active = true;
				shield_actived_frame = frame;
			}
			if (bonus_active == 0) {
				int center_x = x + sizeX / 2 - 12;
				int center_y = y + sizeY / 2 - 12;
				int dx = another_tank_x - center_x, dy = -another_tank_y + center_y;
				double angle = Math.acos(dx / Math.sqrt(dx * dx + dy * dy));
				if (dy < 0) {
					angle = 2 * Math.PI - angle;
				}
				int x_bullet = (int) (center_x + Math.round(Math.cos(angle) * sizeX / 2));
				int y_bullet = (int) (center_y - Math.round(Math.sin(angle) * sizeX / 2));
				bullet_list.add(new Bullet(g2, missle_imgs, x_bullet, y_bullet, 6,
						(int)Math.toDegrees(angle),
						25, screenSizeX, screenSizeY));
			}
			if (bonus_count <= 0) {
				bonus_active = -1;
			}
		} 
		catch (Exception e) {
            System.out.print("No resources are found");
        }
	}
	
	/**
	 * Reduces the health of the plane
	 * 
	 * @param num - the health will be reduced by this value
	 */
	public void reduceHealth(int num) {
		if (!shield_active) {
			health -= num;
			if (health <= 0) {
				is_deleted = true;
			}
		}
	}
	
	/**
	 * Draws the tank object depending on the angle
	 */
	@Override
	public void draw(ImageObserver obs) {
		if (shield_active) {
			g2.drawImage(shield_img, x-10, y-10, obs);
		}
		g2.drawImage(imgs.get(Math.round(direction * (imgs.size() - 1) / 360)), x, y, obs);
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
                    direction = (direction + 2) % 360;
                    break; 
                case KeyEvent.VK_RIGHT:
                	direction = direction - 2;
                	if (direction < 0) {
                		direction = 360 - direction;
                	}
                    break;
                case KeyEvent.VK_UP:
                    last_move_vector = +1;
                	speed += 2;
                    break; 
                case KeyEvent.VK_DOWN:
                	last_move_vector = -1;
                    speed -= 2;
                    break;
                case KeyEvent.VK_ENTER:
	            	fire();
	            	break;
                case KeyEvent.VK_DELETE:
	            	firebonus();
	            	break;
              }
        }
        if((ge.type == 1) && (player_id == 2)) {
        	KeyEvent e = (KeyEvent) ge.event;
        	switch (e.getKeyCode()) {    
            case KeyEvent.VK_A:
            	direction = (direction + 2) % 360;
                break; 
	            case KeyEvent.VK_D:
	            	direction = direction - 2;
                	if (direction < 0) {
                		direction = 360 - direction;
                	}
                    break;
	            case KeyEvent.VK_W:
	            	last_move_vector = +1;
	            	speed += 2;
	    	break; 
	            case KeyEvent.VK_S:
	            	last_move_vector = -1;
	            	speed -= 2;
	        break; 
	            case KeyEvent.VK_CONTROL:
	            	firebonus();
	            	break;
	        default:
                if(e.getKeyChar() == ' ' && e.getKeyCode() == 32) {
                  fire();  
                }
            }
        }
    }
	
	/**
	 * Rolls back the tank in case of collision
	 */
	public void rollback() {
		speed = -last_move_vector * 25;
		if (speed != 0) {
			last_collision_frame = frame;
		}
		super.move();
		speed = 0;
	}
	
	/**
	 * Updates the object - calls super class method and reverts speed back to 0.
	 */
	@Override
	public void update() {
		if (last_collision_frame != -1 && frame - last_collision_frame < 50) {
			speed = last_move_vector;
		}
		super.move();
		speed = 0;
		frame++;
		if (frame - shield_actived_frame >= 400) {
			shield_active = false;
		}
	}
	
	/**
	 * Sets the coordinates pointing to another tank. It is necessary to fire missles.
	 * 
	 * @param x - x center coordinate of another tank
	 * @param y - y center coordinate of another tank
	 */
	public void setAnotherTankCoordinates(int x, int y) {
		another_tank_x = x;
		another_tank_y = y;
	}
	
	/**
	 * Handles bonus consumption by the tank.
	 * 
	 * @param type  - type of bonus consumed.
	 */
	public void consumeBonus(int type) {
		// health
		if (type == 3) {
			health = initial_health;
		}
		// missle
		if (type == 0 || type == 2) {
			bonus_active = type;
			bonus_count = 10;
		}
	}
}
