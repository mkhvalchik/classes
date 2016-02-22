package Wingman;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import common_classes.Bullet;

/**
*
* @author Marie
*/
public class EnemyAirplane extends Airplane {
	/**
	 * type of the enemy
	 */
	int type;
	
	/**
	 * bullet image for faster loading
	 */
	Image bullet_img;
	
	/**
	 * time when this plane last fired
	 */
	int last_fire_frame;

	public EnemyAirplane(Graphics2D g2, ArrayList<Image> imgs, int x, int y, int speed,
			int direction, int screenSizeX, int screenSizeY, int health, ArrayList<Bullet> bullet_list, int type) {
        super(g2, imgs, x, y, speed, direction, screenSizeX, screenSizeY, health, bullet_list);
        this.type = type;
        try {
        	bullet_img = ImageIO.read(new File("Resources/enemybullet2.png"));
        }
        catch (Exception e) {
            System.out.print("No resources are found");
        }
        last_fire_frame = -1;
    }
	
	/**
	 * Fires a new bullet depending on the type of the plane.
	 */
	public void fire() {
		try {
			if (y < 0) {
				return;
			}
			boolean go_up = false;
			if (Math.sin(direction * Math.PI / 180) > 0) {
				go_up = true;
			}
			int bullet_y_shift;
			if (go_up) {
				bullet_y_shift = -(bullet_img.getHeight(null) + 5);
			} else {
				bullet_y_shift = (sizeY + 5);
			}
			ArrayList<Image> imgs = new ArrayList<Image>();
			imgs.add(bullet_img);
			if (type == 0 || type == 1) {
				bullet_list.add(new Bullet(g2, imgs, x, y + bullet_y_shift, 5, direction,
						10, screenSizeX, screenSizeY));
			}
			if (type == 2) {
				bullet_list.add(new Bullet(g2, imgs, x, y + bullet_y_shift, 5, direction - 45,
						10, screenSizeX, screenSizeY));
			}
			if (type == 3) {
				bullet_list.add(new Bullet(g2, imgs, x, y + bullet_y_shift, 5, direction + 45,
						10, screenSizeX, screenSizeY));
				bullet_list.add(new Bullet(g2, imgs, x, y + bullet_y_shift, 5, direction - 45,
						10, screenSizeX, screenSizeY));
			}
		} 
		catch (Exception e) {
            System.out.print("No resources are found");
        }
	}
	
	/**
	 * Checks if this plane should fire a bullet and also if we can delete the object.
	 */
	@Override
	public void update() {
		super.update();
		if (((last_fire_frame < 0) || (frame - last_fire_frame >= 100)) && Math.random() * 200 <= 1) {
			last_fire_frame = frame;
			fire();
		}
		if (((y > screenSizeX) || (y < 0)) && frame > 1000) {
			is_deleted = true;
		}
	}
}
