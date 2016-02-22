package common_classes;

import java.awt.event.KeyEvent;
import java.util.Observable;

/**
*
* @author Marie
*/
public class GameEvents extends Observable {
	public int type;
    public Object event;
    
    /**
	 * Creates a new event for KeyEvent and notifies all observers about it
	 *
	 * @param e - generated KeyEvent
	 */
    public void setValue(KeyEvent e) {
    	type = 1; // let's assume this means key input. 
    	//Should use CONSTANT value for this when you program
    	event = e;
    	setChanged();
    	// trigger notification
        notifyObservers(this);  
   }

    /**
	 * Creates a new event for String and notifies all observers about it
	 *
	 * @param msg - String message
	 */
   public void setValue(String msg) {
	   type = 2; 
       event = msg;
       setChanged();
       // trigger notification
       notifyObservers(this);  
   }
}