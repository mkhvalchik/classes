package common_classes;

import java.util.HashMap;
import java.util.ArrayList;
import java.awt.event.*;

/**
*
* @author Marie
*/
public class KeyControl extends KeyAdapter {
	/**
	 * hashmap of pressed keys
	 */
	HashMap<Integer, Boolean> hm = new HashMap<Integer, Boolean>();
	
	/**
	 * point to GameEvents object, to pass the events to ovservers
	 */
 	public GameEvents ge;
 	KeyEvent e;
 
 	public KeyControl(GameEvents ge) {
 		this.ge = ge;
 	}
 	
 	/**
	 * Called when key is released, in this case it is deleted from hashmap
	 *
	 * @param e - Keyevent happened
	 */
 	public void keyReleased(KeyEvent e) {
 		hm.remove(e.getKeyCode());
 	}
 	
 	/**
	 * Called when key is pressed, in this case it is added to hashmap
	 *
	 * @param e - Keyevent happened
	 */
    public void keyPressed(KeyEvent e) {
    	hm.put(e.getKeyCode(), true);
    	this.e = e;
    }
    
    /**
	 * When called, creates the events for all keys on the hashmap
	 *
	 */
    public void handlePressedKeys() {
    	ArrayList<Integer> to_remove = new ArrayList<Integer>();
    	for (Integer key : hm.keySet()) {
    		e.setKeyCode(key);
    		ge.setValue(e);
    		if((e.getKeyChar() == ' ' && e.getKeyCode() == 32) || e.getKeyCode() == KeyEvent.VK_CONTROL ||
    				e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DELETE) {
    			to_remove.add(e.getKeyCode());
    		}
    	}
    	for (int i = 0; i < to_remove.size(); i++) {
    		hm.remove(to_remove.get(i));
    	}
    }
}
