package common_classes;

import java.awt.Graphics2D;
import java.awt.Image;

/**
*
* @author Marie
*/
public class GameInterface extends GameObject {
	/**
	 * Number of players
	 */
	protected int num_players;
	
	/**
	 * max number of health for player1.
	 */
	public int player1_health_capacity;
	/**
	 * max number of health for player2.
	 */
	public int player2_health_capacity;
	/**
	 * current number of health for player1.
	 */
	protected int player1_health_remainder;
	/**
	 * current number of health for player2.
	 */
	protected int player2_health_remainder;
	/**
	 * current number of lives for player1.
	 */
	public int player1_lives;
	/**
	 * current number of health for player2.
	 */
	public int player2_lives;
	
	protected GameInterface(Graphics2D g2, Image img, int x, int y, int screenSizeX, int screenSizeY, int num_players,
			int player1_health_capacity, int player2_health_capacity, int player1_lives, int player2_lives) {
        super(g2, img, x, y, screenSizeX, screenSizeY);
        this.num_players = num_players;
        this.player1_health_capacity = player1_health_capacity;
        this.player2_health_capacity = player2_health_capacity;
        this.player1_health_remainder = player1_health_capacity;
        this.player2_health_remainder = player2_health_capacity;
        this.player1_lives = player1_lives;
        this.player2_lives = player2_lives;
    }
	
	/**
	 * Updates all class parameters with new values.
     *
	 * @param player1_health_remainder - new value for corresponding class member
	 * @param player2_health_remainder - new value for corresponding class member
	 * @param player1_lives - new value for corresponding class member
	 * @param player2_lives - new value for corresponding class member
	 */
	public void update(int player1_health_remainder, int player2_health_remainder, int player1_lives,
			int player2_lives) {
		this.player1_health_remainder = player1_health_remainder;
        this.player2_health_remainder = player2_health_remainder;
        this.player1_lives = player1_lives;
        this.player2_lives = player2_lives;
	}
	
	public void update() {
	}
}
