package Tanks;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import common_classes.GameInterface;

/**
*
* @author Marie
*/
public class TankGameInterface extends GameInterface {
	
	/**
	 * x coordinate of player1 bottom center
	 */
	int player1_center_bottom_x;
	
	/**
	 * y coordinate of player1 bottom center
	 */
	int player1_center_bottom_y;
	
	/**
	 * x coordinate of player1 bottom center
	 */
	int player2_center_bottom_x;
	
	/**
	 * y coordinate of player2 bottom center
	 */
	int player2_center_bottom_y;
	
	/**
	 * score of player1
	 */
	int score1;
	
	/**
	 * score of player2
	 */
	int score2;
	
	/**
	 * type of bonus active for tank1
	 */
	int tank1_bonus_active;
	
	/**
	 * type of bonus active for tank2
	 */
	int tank2_bonus_active;
	
	/**
	 * count of bonus remaining for tank1
	 */
	int tank1_bonus_count;
	
	/**
	 * count of bonus remaining for tank2
	 */
	int tank2_bonus_count;
	
	/**
	 * list of images for bonuses
	 */
	ArrayList<Image> bonus_list;
	
	TankGameInterface(Graphics2D g2, Image img, int x, int y, int screenSizeX, int screenSizeY, 
			int player1_health_capacity, int player2_health_capacity, ArrayList<Image>  bonus_list) {
        super(g2, img, x, y, screenSizeX, screenSizeY, 1, player1_health_capacity, 
        		player2_health_capacity, 0, 0);
        this.bonus_list = bonus_list;
    }
	
	/**
	 * Draws the bonuses, health bar and score for each tank.
	 */
	@Override
	public void draw(ImageObserver obs) {
		g2.setColor(Color.GREEN);
		Rectangle r = new Rectangle(player1_center_bottom_x - 30, player1_center_bottom_y + 5,
				60, 7);
		g2.drawRect((int) r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
		g2.fillRect((int) r.getX(), (int)r.getY(), (int)r.getWidth() * player1_health_remainder / player1_health_capacity,
				(int)r.getHeight());
		
		r = new Rectangle(player2_center_bottom_x - 30, player2_center_bottom_y + 5,
				60, 7);
		g2.drawRect((int) r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
		g2.fillRect((int) r.getX(), (int)r.getY(), (int)r.getWidth() * player2_health_remainder / player2_health_capacity,
				(int)r.getHeight());
		
		g2.setColor(Color.BLUE);
		g2.setFont(new Font("TimesRoman", Font.BOLD, 20));
		g2.drawString(String.format("%d", score1), player1_center_bottom_x - 40, player1_center_bottom_y);
		
		g2.setColor(Color.RED);
		g2.setFont(new Font("TimesRoman", Font.BOLD, 20));
		g2.drawString(String.format("%d", score2), player2_center_bottom_x - 40, player2_center_bottom_y);
	
		if (tank1_bonus_active != -1) {
			g2.drawImage(bonus_list.get(tank1_bonus_active),
					player1_center_bottom_x - 10, player1_center_bottom_y + 15, obs);
			g2.setColor(Color.WHITE);
			g2.setFont(new Font("TimesRoman", Font.BOLD, 10));
			g2.drawString(String.format("%d", tank1_bonus_count),
					player1_center_bottom_x + 10, player1_center_bottom_y + 25);
		}
		if (tank2_bonus_active != -1) {
			g2.drawImage(bonus_list.get(tank2_bonus_active),
					player2_center_bottom_x - 10, player2_center_bottom_y + 15, obs);
			g2.setColor(Color.WHITE);
			g2.setFont(new Font("TimesRoman", Font.BOLD, 10));
			g2.drawString(String.format("%d", tank2_bonus_count),
					player2_center_bottom_x + 10, player2_center_bottom_y + 25);
		}
	}
	
	/**
	 * Updates the class parameters.
	 * 
	 * @param player1_health_remainder - corresposnding parameter to update
	 * @param player2_health_remainder - corresposnding parameter to update
	 * @param player1_center_bottom_x - x coordinate of player1 bottom center
	 * @param player1_center_bottom_y - y coordinate of player2 bottom center
	 * @param player2_center_bottom_x - x coordinate of player2 bottom center
	 * @param player2_center_bottom_y - y coordinate of player2 bottom center
	 * @param score1 - score of player1
	 * @param score2 - score of player2
	 * @param tank1_bonus_active - what type of bonus is active on tank1
	 * @param tank2_bonus_active - what type of bonus is active on tank2
	 * @param tank1_bonus_count - how many bonuses left on tank1
	 * @param tank2_bonus_count - how many bonuses left on tank2
	 */
	public void update(int player1_health_remainder, int player2_health_remainder, int player1_center_bottom_x,
			int player1_center_bottom_y, int player2_center_bottom_x, int player2_center_bottom_y,
			int score1, int score2, int tank1_bonus_active, int tank2_bonus_active,
			int tank1_bonus_count, int tank2_bonus_count) {
		super.update(player1_health_remainder, player2_health_remainder, 0, 0);
		this.score1 = score1;
		this.score2 = score2;
		this.player1_center_bottom_x = player1_center_bottom_x;
		this.player1_center_bottom_y = player1_center_bottom_y;
		this.player2_center_bottom_x = player2_center_bottom_x;
		this.player2_center_bottom_y = player2_center_bottom_y;
		this.tank1_bonus_active = tank1_bonus_active;
		this.tank2_bonus_active = tank2_bonus_active;
		this.tank1_bonus_count = tank1_bonus_count;
		this.tank2_bonus_count = tank2_bonus_count;
	}
}
