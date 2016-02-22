package common_classes;

import java.io.*;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.*;

/**
*
* @author Marie
*/
public class SoundObject {
	SoundObject(String path, boolean loop) {
		try {
			if (path.contains("mid")) {
				Sequencer sequencer = MidiSystem.getSequencer();
				sequencer.open();
				InputStream is = new BufferedInputStream(new FileInputStream(new File(path)));
				sequencer.setSequence(is);
				sequencer.start();
			}
			Clip clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(path));
			clip.open(inputStream);
			if (loop) {
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			} else {
				clip.start();
			}
		}
		catch (Exception e) {

		}
	}
}
