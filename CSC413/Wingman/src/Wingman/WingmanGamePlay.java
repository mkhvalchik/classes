package Wingman;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
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
public class WingmanGamePlay extends GamePlay {
	/**
	 * array of enemy images for enemy type 1
	 */
	ArrayList<Image> enemy1_imgs;
	/**
	 * array of enemy images for enemy type 2
	 */
	ArrayList<Image> enemy2_imgs;
	/**
	 * array of enemy images for enemy type 3
	 */
	ArrayList<Image> enemy3_imgs;
	/**
	 * array of enemy images for enemy type 4
	 */
	ArrayList<Image> enemy4_imgs;
	/**
	 * array of enemy images for player
	 */
	ArrayList<Image> player_imgs;
	/**
	 * array of enemy images for enemy explosion
	 */
	ArrayList<Image> explosion_enemy_imgs;
	/**
	 * array of enemy images for player explosion
	 */
	ArrayList<Image> explosion_player_imgs;
	/**
	 * array of enemy images for islands
	 */
	ArrayList<Image> island_imgs;
	/**
	 * array of bullets
	 */
	ArrayList<Bullet> bullet_list;
	/**
	 * array of  enemies
	 */
	ArrayList<EnemyAirplane> enemy_planes;
	/**
	 * array of player planes
	 */
	ArrayList<PlayerAirplane> player_planes;
	
	/**
	 * frame number
	 */
	int frame;
	/**
	 * number of players
	 */
	int num_players;
	/**
	 * total number of scores
	 */
	int scores;

	public WingmanGamePlay(ImageObserver obs, KeyControl key, Graphics2D g2,
			int sizeX, int sizeY, int num_players, int player1_health_capacity, int player2_health_capacity,
			int player1_lives, int player2_lives) {
		super(obs, key, g2, sizeX, sizeY, new WingmanGameInterface(g2, null, 0, 0, sizeX, sizeY, num_players, player1_health_capacity, 
        		player2_health_capacity, player1_lives, player2_lives),
        		"Resources/background.wav");
		scores = 0;
		this.num_players = num_players;
	}
	
	/**
	 * Performs the initialization - reads all images and creates background along with player planes.
	 */
	public void start() {
		try {
			objects.add(new Background(g2, ImageIO.read(new File("Resources/water.png")), 0, 0, 1, sizeX, sizeY));
			enemy1_imgs = new ArrayList<Image>();
			enemy2_imgs = new ArrayList<Image>();
			enemy3_imgs = new ArrayList<Image>();
			enemy4_imgs = new ArrayList<Image>();
			player_imgs = new ArrayList<Image>();
			island_imgs = new ArrayList<Image>();
			bullet_list = new ArrayList<Bullet>();
			explosion_enemy_imgs = new ArrayList<Image>();
			explosion_player_imgs = new ArrayList<Image>();
			enemy_planes = new ArrayList<EnemyAirplane>();
			player_planes = new ArrayList<PlayerAirplane>();
			for (int i = 1; i <= 3; i++) {
				enemy1_imgs.add(ImageIO.read(new File(String.format("Resources/enemy1_%d.png", i))));
				enemy2_imgs.add(ImageIO.read(new File(String.format("Resources/enemy2_%d.png", i))));
				enemy3_imgs.add(ImageIO.read(new File(String.format("Resources/enemy3_%d.png", i))));
				enemy4_imgs.add(ImageIO.read(new File(String.format("Resources/enemy4_%d.png", i))));	
				player_imgs.add(ImageIO.read(new File(String.format("Resources/myplane_%d.png", i))));
				island_imgs.add(ImageIO.read(new File(String.format("Resources/island%d.png", i))));
			}
			for (int i = 1; i <= 6; i++) {
				explosion_enemy_imgs.add(ImageIO.read(new File(String.format("Resources/explosion1_%d.png", i))));
			}
			for (int i = 1; i <= 7; i++) {
				explosion_player_imgs.add(ImageIO.read(new File(String.format("Resources/explosion2_%d.png", i))));
			}
			PlayerAirplane p = new PlayerAirplane(g2, player_imgs, 100, 300,
					 90, sizeX, sizeY, game_interface.player1_health_capacity, bullet_list,
					 game_interface.player1_lives, 1);
			key.ge.addObserver(p);
			objects.add(p);
			player_planes.add(p);
			if (num_players == 2) {
				p = new PlayerAirplane(g2, player_imgs, 200, 300, 90, sizeX, sizeY, 
						game_interface.player2_health_capacity, bullet_list, game_interface.player2_lives, 2);
				key.ge.addObserver(p);
				objects.add(p);
				player_planes.add(p);
			}
		}
		catch (Exception e) {
            System.out.print("No resources are found");
        }
	}
	
	/**
	 * Generates new waves of enemies. Also generates new islands.
	 */
	public void maybeGenerateNewEnemyWave() {
		if (frame % 300 == 0) {
			Island i = new Island(g2, island_imgs.get((int) (Math.random() * 3)),
					(int) (Math.random() * sizeX), -100, 1, 270, sizeX, sizeY);
			objects.add(1, i);
		}
		if (frame == 0) {
			for (int i = 0; i < 10; i++) {
				EnemyAirplane e = new EnemyAirplane(g2, enemy1_imgs, (i + 1) * sizeX / 9, -15 * i, 2,
						270, sizeX, sizeY, 50, bullet_list, 0);
				objects.add(e);
				enemy_planes.add(e);
			}
		}
		if (frame == 500) {
			for (int i = 0; i < 7; i++) {
				EnemyAirplane e = new EnemyAirplane(g2, enemy2_imgs, (i + 1) * sizeX / 9, -15 * (7 - i), 1,
						270, sizeX, sizeY, 100, bullet_list, 1);
				objects.add(e);
				enemy_planes.add(e);
			}
		}
		if (frame == 1000) {
			for (int i = 0; i < 7; i++) {
				EnemyAirplane e = new EnemyAirplane(g2, enemy3_imgs, -40 * i, i * sizeY / 7, 1,
						0, sizeX, sizeY, 75, bullet_list, 2);
				objects.add(e);
				enemy_planes.add(e);
			}
		}
		if (frame == 1500) {
			for (int i = 0; i < 7; i++) {
				EnemyAirplane e = new EnemyAirplane(g2, enemy4_imgs, (i + 1) * sizeX / 9, sizeY + 15 * i, 1,
						90, sizeX, sizeY, 75, bullet_list, 3);
				objects.add(e);
				enemy_planes.add(e);
			}
		}
		if (frame == 2200) {
			game_done = true;
		}
	}
	
	/**
	 * Checks the collisions between enemies and players, planes and bullets.
	 * 
	 * If enemy or player gets destroyed, a new explosion is created and object get deleted.
	 */
	void handleCollisions() {
 		for (int i = 0; i < bullet_list.size(); i++) {
			for (int j = 0; j < player_planes.size(); j++) {
				if (collision(bullet_list.get(i), player_planes.get(j))) {
					int lives_before = player_planes.get(j).lives;
					player_planes.get(j).reduceHealth(bullet_list.get(i).damage);
					if (lives_before != player_planes.get(j).lives) {
						objects.add(new Explosion(g2, explosion_player_imgs, player_planes.get(j).x, player_planes.get(j).y,
								sizeX, sizeY, "Resources/snd_explosion1.wav"));
					}
					bullet_list.remove(i);
					i--;
					if (player_planes.get(j).is_deleted) {
						player_planes.remove(j);
						j--;
					}
					break;
				}
			}
		}
		for (int i = 0; i < bullet_list.size(); i++) {
			for (int j = 0; j < enemy_planes.size(); j++) {
				if (collision(bullet_list.get(i), enemy_planes.get(j))) {
					enemy_planes.get(j).reduceHealth(bullet_list.get(i).damage);
					bullet_list.remove(i);
					i--;
					if (enemy_planes.get(j).is_deleted) {
						scores += enemy_planes.get(j).initial_health;
						objects.add(new Explosion(g2, explosion_enemy_imgs, enemy_planes.get(j).x, enemy_planes.get(j).y,
								sizeX, sizeY, "Resources/snd_explosion1.wav"));
						enemy_planes.remove(j);
						j--;
					}
					break;
				}
			}
		}
		for (int i = 0; i < enemy_planes.size(); i++) {
			for (int j = 0; j < player_planes.size(); j++) {
				if (collision(enemy_planes.get(i), player_planes.get(j))) {
					objects.add(new Explosion(g2, explosion_enemy_imgs, enemy_planes.get(i).x, enemy_planes.get(i).y,
							sizeX, sizeY, "Resources/snd_explosion1.wav"));
					for (int k = 0; k < objects.size(); k++) {
						if (objects.get(k) == enemy_planes.get(i)){
							objects.remove(k);
							break;
						}
					}
					int lives_before = player_planes.get(j).lives;
					player_planes.get(j).reduceHealth(25);
					if (lives_before != player_planes.get(j).lives) {
						objects.add(new Explosion(g2, explosion_player_imgs, player_planes.get(j).x, player_planes.get(j).y,
								sizeX, sizeY, "Resources/snd_explosion1.wav"));
					}
					enemy_planes.remove(i);
					i--;
					if (player_planes.get(j).is_deleted) {
						player_planes.remove(j);
						j--;
					}
					break;
				}
			}
		}
	}
	
	/**
	 * Handles timeline - checks for collisions, generates new enemies.
	 * 
	 * Finishes the game if necessary.
	 */
	@Override
	public void tick() {
		maybeGenerateNewEnemyWave();
		handleCollisions();
		key.handlePressedKeys();
		super.tick();
		for (int i = 0; i < bullet_list.size(); i++) {
			if (bullet_list.get(i).is_deleted) {
				bullet_list.remove(i);
				i--;
			} else {
				bullet_list.get(i).update();
				bullet_list.get(i).draw(obs);
			}
		}
		if (player_planes.size() != num_players) {
			game_done = true;
			return;
		}
		int player1_health = 0, player1_lives = 0, player2_health = 0, player2_lives = 0;
		if (player_planes.size() >= 1) {
			player1_health = player_planes.get(0).health;
			player1_lives = player_planes.get(0).lives;
		}
		if (player_planes.size() >= 2) {
			player2_health = player_planes.get(1).health;
			player2_lives = player_planes.get(1).lives;
		}
		((WingmanGameInterface)game_interface).update(player1_health, player2_health, player1_lives, player2_lives, scores);
		frame++;
	}
}
