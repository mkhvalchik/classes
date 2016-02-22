package Wingman;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.File;

import javax.imageio.ImageIO;

import common_classes.GameInterface;

/**
*
* @author Marie
*/
public class WingmanGameInterface extends GameInterface {
	/**
	 * image of the live
	 */
	Image live_image;
	
	/**
	 * current score
	 */
	int score;
	
	WingmanGameInterface(Graphics2D g2, Image img, int x, int y, int screenSizeX, int screenSizeY, int num_players,
			int player1_health_capacity, int player2_health_capacity, int player1_lives, int player2_lives) {
        super(g2, img, x, y, screenSizeX, screenSizeY, num_players, player1_health_capacity, 
        		player2_health_capacity, player1_lives, player2_lives);
        
        try {
        	live_image = ImageIO.read(new File("Resources/life.png"));
        } catch (Exception e) {
            System.out.print("No resources are found");
        }
        score = 0;
    }
	
	/**
	 * Draws the lives, health bar and the total score.
	 */
	@Override
	public void draw(ImageObserver obs) {
		for (int i = 0; i < player1_lives; i++) {
			g2.drawImage(live_image, 20 + live_image.getWidth(null) * (i + 1),
					screenSizeY - live_image.getHeight(null) - 10, obs);
		}
		if (num_players == 2) {
			for (int i = 0; i < player2_lives; i++) {
				g2.drawImage(live_image, screenSizeX - 20 - live_image.getWidth(null) * (i + 1),
						screenSizeY - live_image.getHeight(null) - 10, obs);
			}
		}
		g2.setColor(Color.RED);
		Rectangle r = new Rectangle(20 + live_image.getWidth(null), screenSizeY - live_image.getHeight(null) - 50,
				live_image.getWidth(null) * 3 * player1_health_remainder / player1_health_capacity, 30);
		g2.drawRect((int) r.getX(), (int)r.getY(), live_image.getWidth(null) * 3, (int)r.getHeight());
		g2.fillRect((int) r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
		if (num_players == 2) {
			r = new Rectangle(screenSizeX - 20 - live_image.getWidth(null) * 3,
					screenSizeY - live_image.getHeight(null) - 50,
					live_image.getWidth(null) * 3 * player2_health_remainder / player2_health_capacity, 30);
			g2.drawRect((int) r.getX(), (int)r.getY(), live_image.getWidth(null) * 3, (int)r.getHeight());
			g2.fillRect((int) r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
		}
		g2.setFont(new Font("TimesRoman", Font.PLAIN, 40));
		g2.drawString(String.format("Score: %d", score), screenSizeX / 2 - 50, screenSizeY - live_image.getHeight(null));
	}
	
	/**
	 * Updates the class parameters.
	 * 
	 * @param player1_health_remainder - corresposnding parameter to update
	 * @param player2_health_remainder - corresposnding parameter to update
	 * @param player1_lives - corresposnding parameter to update
	 * @param player2_lives - corresposnding parameter to update
	 * @param score - corresponding parameter to update
	 */
	public void update(int player1_health_remainder, int player2_health_remainder, int player1_lives,
			int player2_lives, int score) {
		super.update(player1_health_remainder, player2_health_remainder, player1_lives,
				player2_lives);
		this.score = score;
	}
}
