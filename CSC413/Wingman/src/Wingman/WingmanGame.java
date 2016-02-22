/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Wingman;

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
public class WingmanGame extends JApplet implements Runnable {
	
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
	 * gameplay object
	 */
    WingmanGamePlay gp;
    
    /**
	 * gameevnets object
	 */
    GameEvents ge;
    
    /**
	 * key handling object
	 */
    KeyControl key;
    
    /**
	 * number of players
	 */
    int num_players;
    
    /**
	 * Does all the initialization: creates core objects, asks for number of players.
	 */
    public void init() {
    	this.setFocusable(true);
        setBackground(Color.white);
        ge = new GameEvents();
        key = new KeyControl(ge);
        addKeyListener(key);
        Object[] options = {"1 player", "2 players"};
        int n = JOptionPane.showOptionDialog(this,
        		"How many players?",
        		"Player selection",
        		JOptionPane.YES_NO_OPTION,
        		JOptionPane.QUESTION_MESSAGE,
        		null,     //do not use a custom Icon
        		options,  //the titles of buttons
        		options[0]); //default button title
        num_players = n + 1;
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
            bimg = (BufferedImage) createImage(windowSize.width, 
                    windowSize.height);
            g2 = bimg.createGraphics();
            gp = new WingmanGamePlay(this, key, g2, w, h, num_players, 100, 100, 3, 3);
            gp.start();
        }
        gp.tick();
        g.drawImage(bimg, 0, 0, this);
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
	 * Reads the file with top scores and adds player to this file if eligible. Finishes the applet.
	 * 
	 */
    void handleTopScores() {
    	ArrayList<String> players = new ArrayList<String>();
		ArrayList<Integer> scores = new ArrayList<Integer>();
		try{
			String line;
			BufferedReader br = new BufferedReader(new FileReader("topscores.txt"));
			int i = 0;
			while ((line = br.readLine()) != null) {
     		    if (i % 2 == 0) {
     		    	players.add(line);
     		    } else {
     		    	scores.add(Integer.parseInt(line));
     		    }
				i++;
     		}
     		br.close();
		} catch (Exception e) {
			
		}
		String s = "";
		if (scores.size() < 2 || gp.scores > scores.get(scores.size() - 1)) {
			s = (String)JOptionPane.showInputDialog(
                    this,
                    "Enter your name",
                    "ScoreBoard",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "Player1");
		}
		int i;
		for (i = 0; i < scores.size(); i++) {
			if (gp.scores > scores.get(i))
				break;
		}
		scores.add(i, gp.scores);
		players.add(i, s);
		String total = "";
		for (i = 0; i < 10; i++) {
			if (i == players.size())
				break;
			total += players.get(i) + String.format(" %d\n", scores.get(i));
			
		}
		JOptionPane.showMessageDialog(null, total, "ScoreBoard", JOptionPane.INFORMATION_MESSAGE);
		try{
			String line;
			BufferedWriter bw = new BufferedWriter(new FileWriter("topscores.txt"));
			i = 0;
			for (i = 0; i < 10; i++) {
    			if (i == players.size())
    				break;
    			bw.write(players.get(i) + "\n");
    			bw.write(scores.get(i) + "\n");
    		}
     		bw.close();
		} catch (Exception e) {
			
		}
		System.exit(0);
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
        		handleTopScores();
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
