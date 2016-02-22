package Tanks;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import common_classes.Background;
import common_classes.Bullet;
import common_classes.Explosion;
import common_classes.GameInterface;
import common_classes.GameObject;
import common_classes.GamePlay;
import common_classes.KeyControl;

/**
*
* @author Marie
*/
public class TankGamePlay extends GamePlay {
	/**
	 * frame number
	 */
	int frame;
	
	/**
	 * Array of tanks in the game
	 */
	ArrayList<Tank> tanks;
	
	/**
	 * Array of blocks in the game
	 */
	ArrayList<Block> blocks;
	
	/**
	 * Array of bullets in the game for player 1 and 2
	 */
	ArrayList<Bullet> bullet_list1;
	ArrayList<Bullet> bullet_list2;
	
	/**
	 * Array of bonuses;
	 */
	ArrayList<Bonus> bonuses_list;
	
	/**
	 * Start Locations for player 1 and 2
	 */
	int tank1_start_x = 80;
	int tank2_start_x = 1170;
	int tank1_start_y = 450;
	int tank2_start_y = 450;
	
	/**
	 * Health capacities of player 1 and 2.
	 */
	int player1_health_capacity;
	int player2_health_capacity;
	
	/**
	 * Scores for player 1 and 2.
	 */
	int scores1 = 0;
	int scores2 = 0;
	
	public TankGamePlay(ImageObserver obs, KeyControl key, Graphics2D g2,
			int sizeX, int sizeY, int player1_health_capacity, int player2_health_capacity) {
		super(obs, key, g2, sizeX, sizeY, new TankGameInterface(g2, null, 0, 0, sizeX, sizeY, player1_health_capacity, 
        		player2_health_capacity, cropImage("Resources_tank/Weapon_strip3.png")),
        		"Resources_tank/Music.mid");
		this.player1_health_capacity = player1_health_capacity;
		this.player2_health_capacity = player2_health_capacity;
		try {
			objects.add(new Background(g2, ImageIO.read(new File("Resources_tank/Background.png")), 0, 0, 0, sizeX, sizeY));
			tanks = new ArrayList<Tank>();
			blocks = new ArrayList<Block>();
			bullet_list1 = new ArrayList<Bullet>();
			bullet_list2 = new ArrayList<Bullet>();
			Tank t = new Tank(g2, cropImage("Resources_tank/Tank_red_basic_strip60.png"), tank1_start_x, tank1_start_y,
					0, 0, sizeX, sizeY, player1_health_capacity, bullet_list1, 1);
			objects.add(t);
			tanks.add(t);
			key.ge.addObserver(t);
			t = new Tank(g2, cropImage("Resources_tank/Tank_blue_basic_strip60.png"), tank2_start_x, tank2_start_y,
					0, 180, sizeX, sizeY, player2_health_capacity, bullet_list2, 2);
			objects.add(t);
			tanks.add(t);
			key.ge.addObserver(t);
			
			bonuses_list = new ArrayList<Bonus>();
			
			setupNewLevel();
		}
		catch (Exception e) {
            System.out.print("No resources are found");
        }
	}
	
	/**
	 * Sets up new level (places blocks)
	 */
	void setupNewLevel() {
		try {
			Image block_image = ImageIO.read(new File("Resources_tank/Wall1.png"));
			Block b;
			for (int i = 0; i < sizeX / block_image.getWidth(null); i++) {
				b = new Block(g2, block_image, i * block_image.getWidth(null), 0, sizeX, sizeY, 100, false);
				objects.add(b);
				blocks.add(b);
				b = new Block(g2, block_image, i * block_image.getWidth(null), sizeY - block_image.getHeight(null), sizeX,
						sizeY, 100, false);
				objects.add(b);
				blocks.add(b);
			}
			for (int i = 1; i < sizeY / block_image.getHeight(null) - 1; i++) {
				b = new Block(g2, block_image, 0, block_image.getHeight(null) * i, sizeX, sizeY, 100, false);
				objects.add(b);
				blocks.add(b);
				b = new Block(g2, block_image, sizeX - block_image.getWidth(null), block_image.getHeight(null) * i, sizeX,
						sizeY, 100, false);
				objects.add(b);
				blocks.add(b);
			}
			block_image = ImageIO.read(new File("Resources_tank/Wall2.png"));
			for (int i = 0; i < sizeX / block_image.getWidth(null) / 2; i++) {
				if ((i >= (sizeX / block_image.getHeight(null) / 2 - 1) / 2 - 3) && 
						(i <= (sizeX / block_image.getHeight(null) / 2 - 1) / 2 + 3)) {
						continue;
					}
				b = new Block(g2, block_image, i * block_image.getWidth(null) + sizeX / 4, sizeY / 4, sizeX, sizeY, 100, true);
				objects.add(b);
				blocks.add(b);
				b = new Block(g2, block_image, i * block_image.getWidth(null) + sizeX / 4, 3 * sizeY / 4 - block_image.getHeight(null), sizeX,
						sizeY, 100, true);
				objects.add(b);
				blocks.add(b);
			}
			for (int i = 1; i < sizeY / block_image.getHeight(null) / 2 - 1; i++) {
				if ((i >= (sizeY / block_image.getHeight(null) / 2 - 1) / 2 - 3) && 
					(i <= (sizeY / block_image.getHeight(null) / 2 - 1) / 2 + 3)) {
					continue;
				}
				b = new Block(g2, block_image, sizeX / 4, block_image.getHeight(null) * i + sizeY / 4, sizeX, sizeY, 100, true);
				objects.add(b);
				blocks.add(b);
				b = new Block(g2, block_image, 3 * sizeX / 4 - block_image.getWidth(null), block_image.getHeight(null) * i  + sizeY / 4, sizeX,
						sizeY, 100, true);
				objects.add(b);
				blocks.add(b);
			}
			for (int i = sizeY / block_image.getHeight(null) / 2 - 3;
					i <= sizeY / block_image.getHeight(null) / 2 + 1; i++) {
				b = new Block(g2, block_image, sizeX / 8, block_image.getHeight(null) * i, sizeX, sizeY, 100, true);
				objects.add(b);
				blocks.add(b);
				b = new Block(g2, block_image, 7 * sizeX / 8 - block_image.getWidth(null), block_image.getHeight(null) * i, sizeX,
						sizeY, 100, true);
				objects.add(b);
				blocks.add(b);
				b = new Block(g2, block_image, sizeX / 2 - block_image.getWidth(null), block_image.getHeight(null) * i, sizeX,
						sizeY, 100, true);
				objects.add(b);
				blocks.add(b);
			}
		}
		catch (Exception e) {
            System.out.print("No resources are found");
        }
	}
	
	/**
	 * Gets list of images out of image strip.
	 * 
	 * @param source - path to the image strip
	 * @return list of stripped images.
	 */
	public static ArrayList<Image> cropImage(String source) {
		ArrayList<Image> a = new ArrayList<Image>();
		try {
			BufferedImage img = ImageIO.read(new File(source));
		    for (int i = 0; i < img.getWidth(null) / img.getHeight(null); i++) {
		    	a.add(img.getSubimage(i * img.getHeight(null), 0, img.getHeight(null), img.getHeight(null)));
		    }
		}
		catch (Exception e) {
            System.out.print("No resources are found");
        }
		return a;
	}
	
	/**
	 * Performs the initialization - reads all images and creates background along with player planes.
	 */
	public void start() {
		
	}
	
	/**
	 * Checks the collisions between enemies and players, planes and bullets.
	 * 
	 * If enemy or player gets destroyed, a new explosion is created and object get deleted.
	 */
	void handleCollisions() {
		tanks.get(0).update();
		tanks.get(1).update();
		for (int i = 0; i < tanks.size(); i++) {
			for (int j = 0; j < blocks.size(); j++) {
				if (blocks.get(j).is_visible && collision(tanks.get(i), blocks.get(j))) {
					tanks.get(i).rollback();
				}
			}
		}
		if (collision(tanks.get(0), tanks.get(1))) {
			tanks.get(0).rollback();
			tanks.get(1).rollback();
		}
		for (int j = 0; j < blocks.size(); j++) {
			for (int i = 0; i < bullet_list1.size(); i++) {
				if (blocks.get(j).is_visible && collision(bullet_list1.get(i), blocks.get(j))) {
					blocks.get(j).reduceHealth(bullet_list1.get(i).damage);
					objects.add(new Explosion(g2, cropImage("Resources_tank/Explosion_small_strip6.png"),
							bullet_list1.get(i).x, bullet_list1.get(i).y, sizeX, sizeY,
							"Resources_tank/Explosion_small.wav"));
					bullet_list1.remove(i);
					i--;
				}
			}
		}
		for (int j = 0; j < blocks.size(); j++) {
			for (int i = 0; i < bullet_list2.size(); i++) {
				if (blocks.get(j).is_visible && collision(bullet_list2.get(i), blocks.get(j))) {
					blocks.get(j).reduceHealth(bullet_list2.get(i).damage);
					objects.add(new Explosion(g2, cropImage("Resources_tank/Explosion_small_strip6.png"),
							bullet_list2.get(i).x, bullet_list2.get(i).y, sizeX, sizeY,
							"Resources_tank/Explosion_small.wav"));
					bullet_list2.remove(i);
					i--;
				}
			}
		}
		for (int i = 0; i < bullet_list1.size(); i++) {
			if (collision(bullet_list1.get(i), tanks.get(1))) {
				tanks.get(1).reduceHealth(bullet_list1.get(i).damage);
				objects.add(new Explosion(g2, cropImage("Resources_tank/Explosion_small_strip6.png"),
						bullet_list1.get(i).x, bullet_list1.get(i).y, sizeX, sizeY,
						"Resources_tank/Explosion_small.wav"));
				bullet_list1.remove(i);
				i--;
				if (tanks.get(1).is_deleted) {
					scores1++;
					objects.add(new Explosion(g2, cropImage("Resources_tank/Explosion_large_strip7.png"),
							tanks.get(1).x, tanks.get(1).y, sizeX, sizeY,
							"Resources_tank/Explosion_large.wav"));
					tanks.remove(1);
					Tank t = new Tank(g2, cropImage("Resources_tank/Tank_blue_basic_strip60.png"), tank2_start_x, tank2_start_y,
							0, 180, sizeX, sizeY, player2_health_capacity, bullet_list2, 2);
					tanks.add(1, t);
					objects.add(t);
					key.ge.addObserver(t);
					break;
				}
			}
		}
		for (int i = 0; i < bullet_list2.size(); i++) {
			if (collision(bullet_list2.get(i), tanks.get(0))) {
				tanks.get(0).reduceHealth(bullet_list2.get(i).damage);
				objects.add(new Explosion(g2, cropImage("Resources_tank/Explosion_small_strip6.png"),
						bullet_list2.get(i).x, bullet_list2.get(i).y, sizeX, sizeY,
						"Resources_tank/Explosion_small.wav"));
				bullet_list2.remove(i);
				i--;
				if (tanks.get(0).is_deleted) {
					scores2++;
					objects.add(new Explosion(g2, cropImage("Resources_tank/Explosion_large_strip7.png"),
							tanks.get(0).x, tanks.get(0).y, sizeX, sizeY,
							"Resources_tank/Explosion_large.wav"));
					tanks.remove(0);
					Tank t = new Tank(g2, cropImage("Resources_tank/Tank_red_basic_strip60.png"), tank1_start_x, tank1_start_y,
							0, 0, sizeX, sizeY, player1_health_capacity, bullet_list1, 1);
					tanks.add(0, t);
					objects.add(t);
					key.ge.addObserver(t);
					break;
				}
			}
		}
		for (int i = 0; i < bonuses_list.size(); i++) {
			if (collision(tanks.get(0), bonuses_list.get(i))) {
				tanks.get(0).consumeBonus(bonuses_list.get(i).type);
				bonuses_list.get(i).is_deleted = true;
				bonuses_list.remove(i);
				i--;
			} else if (collision(tanks.get(1), bonuses_list.get(i))) {
				tanks.get(1).consumeBonus(bonuses_list.get(i).type);
				bonuses_list.get(i).is_deleted = true;
				bonuses_list.remove(i);
				i--;
			}
		}
		tanks.get(0).setAnotherTankCoordinates(tanks.get(1).x + tanks.get(1).sizeX / 2,
				tanks.get(1).y + tanks.get(1).sizeY / 2);
		tanks.get(1).setAnotherTankCoordinates(tanks.get(0).x + tanks.get(0).sizeX / 2,
				tanks.get(0).y + tanks.get(0).sizeY / 2);
		tanks.get(0).last_move_vector = 0;
		tanks.get(1).last_move_vector = 0;
	}
	
	/**
	 * Generates new bonuses on the field.
	 */
	void maybeAddBonus() {
		if (frame % 700 == 0) {
			while (true) {
				int type = (int) Math.round(Math.random() * 3);
				if (type == 1) continue;
				Bonus b = new Bonus(g2, cropImage("Resources_tank/Pickup_strip4.png").get(type),
						(int) (Math.random() * sizeX), (int) (Math.random() * sizeY), sizeX, sizeY, type); 
				boolean collision = false;
				for (int j = 0; j < blocks.size(); j++) {
					if (collision(b, blocks.get(j))) {
						collision = true;
						break;
					}
				}
				if (collision)
					break;
				bonuses_list.add(b);
				objects.add(b);
				break;
			}
		}
	}
	
	/**
	 * Handles timeline - checks for collisions, generates new bonuses.
	 * 
	 * Finishes the game if necessary.
	 */
	@Override
	public void tick() {
		maybeAddBonus();
		key.handlePressedKeys();
		handleCollisions();
		
		super.tick();
		for (int i = 0; i < bullet_list1.size(); i++) {
			if (bullet_list1.get(i).is_deleted) {
				bullet_list1.remove(i);
				i--;
			} else {
				bullet_list1.get(i).update();
				bullet_list1.get(i).draw(obs);
			}
		}
		for (int i = 0; i < bullet_list2.size(); i++) {
			if (bullet_list2.get(i).is_deleted) {
				bullet_list2.remove(i);
				i--;
			} else {
				bullet_list2.get(i).update();
				bullet_list2.get(i).draw(obs);
			}
		}
		
		((TankGameInterface)game_interface).update(tanks.get(0).health, tanks.get(1).health, tanks.get(0).x + tanks.get(0).sizeX / 2,
				tanks.get(0).y + tanks.get(0).sizeY, tanks.get(1).x + tanks.get(1).sizeX / 2,
				tanks.get(1).y + tanks.get(1).sizeY, scores1, scores2, tanks.get(0).bonus_active,
				tanks.get(1).bonus_active, tanks.get(0).bonus_count, tanks.get(1).bonus_count);
		frame++;
	}
}
