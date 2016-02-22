package common_classes;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;


/**
*
* @author Marie
*/
public class Background extends MovableObject {	
	public Background(Graphics2D g2, Image img, int x, int y, int speed, int screenSizeX, int screenSizeY) {
        super(g2, img, x, y, speed, 270, screenSizeX, screenSizeY);
    }
	
	
	/**
	 * Draws a background object.
	 *
	 * Calculates the number of tiles required to fill the background and draw it
	 *
	 * @param obs - ImageObserver required to draw the tiles
	 */
	@Override
	public void draw(ImageObserver obs) {
		int TileWidth = img.getWidth(null);
        int TileHeight = img.getHeight(null);

        int NumberX = (int) (screenSizeX / TileWidth);
        int NumberY = (int) (screenSizeY / TileHeight);

        for (int i = -1; i <= NumberY; i++) {
            for (int j = 0; j <= NumberX; j++) {
                g2.drawImage(img, j * TileWidth, 
                        i * TileHeight + (y % TileHeight), TileWidth, 
                        TileHeight, obs);
            }
        }
    }
}
