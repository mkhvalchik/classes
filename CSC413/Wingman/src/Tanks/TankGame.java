/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tanks;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import common_classes.GameEvents;
import common_classes.KeyControl;

/**
*
* @author Marie
*/
public class TankGame extends JApplet implements Runnable {
	
	/**
	 * thread which runs the applet
	 */
    private Thread thread;
    
    /**
	 * image to draw on Jpanel
	 */
    private BufferedImage bimg;
    
    /**
	 * graphics object
	 */
    Graphics2D g2;

    /**
	 * width and height of the screen
	 */
    int w = 640, h = 480; // fixed size window game 
    
    /**
	 * width and height of the 'canvas'
	 */
    int canvas_w  = w * 2, canvas_h = h * 2;
    
    /**
	 * gameplay object
	 */
    TankGamePlay gp;
    
    /**
	 * gameevnets object
	 */
    GameEvents ge;
    
    /**
	 * key handling object
	 */
    KeyControl key;
    
    /**
	 * Does all the initialization: creates core objects, asks for number of players.
	 */
    public void init() {
    	this.setFocusable(true);
        setBackground(Color.white);
        ge = new GameEvents();
        key = new KeyControl(ge);
        addKeyListener(key);
    }

    /**
	 * This function is called during repaint - it draws the bufferedimage.
	 * 
	 * It also creates the gameplay object.
	 */
    public void paint(Graphics g) {
        if(bimg == null) {
        	setSize(w, h);
        	Dimension windowSize = getSize();
            bimg = (BufferedImage) createImage(canvas_w, 
            		canvas_h);
            g2 = bimg.createGraphics();
            gp = new TankGamePlay(this, key, g2, canvas_w, canvas_h, 200, 200);
            gp.start();
        }
        gp.tick();
        
        int center0_x = gp.tanks.get(0).x + gp.tanks.get(0).sizeX / 2;
        int center1_x = gp.tanks.get(1).x + gp.tanks.get(1).sizeX / 2;
        int center0_y = gp.tanks.get(0).y + gp.tanks.get(0).sizeY / 2;
        int center1_y = gp.tanks.get(1).y + gp.tanks.get(1).sizeY / 2;
        
        if (center0_x - w / 4 < 0) {
        	center0_x = w / 4;
        }
        if (center0_x + w / 4 >= canvas_w) {
        	center0_x = canvas_w - w / 4 - 1;
        }
        if (center1_x - w / 4 < 0) {
        	center1_x = w / 4;
        }
        if (center1_x + w / 4 >= canvas_w) {
        	center1_x = canvas_w - w / 4 - 1;
        }
        if (center0_y - h / 2 < 0) {
        	center0_y = h / 2;
        }
        if (center0_y + h / 2 >= canvas_h) {
        	center0_y = canvas_h - h / 2 - 1;
        }
        if (center1_y - h / 2 < 0) {
        	center1_y = h / 2;
        }
        if (center1_y + h / 2 >= canvas_h) {
        	center1_y = canvas_h - h / 2 - 1;
        }
        
        int i = bimg.getHeight();
        int j = bimg.getWidth();
        BufferedImage left_upper_half = bimg.getSubimage(center0_x - w / 4 + 2, center0_y - h / 2, w / 2 - 2, h - h / 4);
        BufferedImage left_bottom_half = bimg.getSubimage(center0_x - w / 4 + 2,
        		center0_y - h / 2 + h - h / 4, w / 2 - 2 - w / 8 + 2, h / 4);
        BufferedImage right_upper_half = bimg.getSubimage(center1_x - w / 4 + 2, center1_y - h / 2, w / 2 - 2, h - h /4 );
        int a = center1_x - w / 4 + 2 + w / 8;
        int b = center1_y - h / 2 + h - h / 4;
        int c = a + w / 2 - 2 - w / 8 + 2;
        int d = b + h / 4;
        BufferedImage right_bottom_half = bimg.getSubimage(center1_x - w / 4 + 2 + w / 8,
        		center1_y - h / 2 + h - h / 4, w / 2 - 2 - w / 8 + 1, h / 4);
        
        Image mini_map = bimg.getScaledInstance(w / 4, -1, java.awt.Image.SCALE_FAST);
        //mini_map
        g.drawImage(left_upper_half, 0, 0, this);
        g.drawImage(left_bottom_half, 0, 3 * h / 4, this);
        g.drawImage(right_upper_half, w / 2 + 1, 0, this);
        g.drawImage(right_bottom_half, w / 2 + w / 8, 3 * h / 4, this);
        g.drawImage(mini_map, 3 * w / 8, 3 * h / 4, this);
    }

    /**
	 * Starts the main thread.
	 * 
	 */
    public void start() {
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }
    
    /**
	 * Periodically is awakened. When waked up, this function calls repaint.
	 * 
	 * If game is finished, it displays the top scores. 
	 * 
	 */
    public void run() {
    	
        Thread me = Thread.currentThread();
        while (thread == me) {
        	if (gp != null && gp.game_done) {
        		
        	}
          repaint();  
          try {
                thread.sleep(25);
            } catch (InterruptedException e) {
                break;
            }
            
        }
    }

}
