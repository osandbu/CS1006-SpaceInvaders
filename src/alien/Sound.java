package alien;

import sun.audio.*; //import the sun.audio package
import java.io.*;

/**
 * Class used to play sounds.
 * 
 * @author Ole
 */
public class Sound {

	// Constants used to play sounds.
	public static final int LASER = 0;
	public static final int KILL = 1;
	public static final int UFOLOW = 2;
	public static final int MOVE1 = 3;
	public static final int MOVE2 = 4;
	public static final int MOVE3 = 5;
	public static final int MOVE4 = 6;
	public static final int BOOM = 7;

	private static final String[] FILE_NAMES = { "laser.au", "kill.au",
			"ufolow.au", "move1.au", "move2.au", "move3.au", "move4.au",
			"boom.au" };
	private static final String SOUND_DIR = "sounds/";
	private static byte[][] audioBytes = new byte[FILE_NAMES.length][];
	
	private static boolean playSounds = true;

	// load all the sounds for static usage
	static {
		for (int i = 0; i < FILE_NAMES.length; i++) {
			loadSound(i);
		}
	}

	/**
	 * Play the sound of the given index.
	 * 
	 * @param i
	 *            The index of the sound to be played.
	 */
	public static void play(int i) {
		if(!playSounds)
			return;
		// create an InputStream from which the sound can be read.
		InputStream byteStream = new ByteArrayInputStream(audioBytes[i]);
		AudioStream audioStream;
		try {
			// create an AudioStream from which the sound can be played
			audioStream = new AudioStream(byteStream);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		AudioPlayer.player.start(audioStream);
	}

	/**
	 * Load a sound of a given index. Stores it in an array of bytes.
	 * 
	 * @param i
	 *            The index of the sound to be loaded.
	 */
	private static void loadSound(int i) {
		if(!playSounds)
			return;
		String filename = SOUND_DIR + FILE_NAMES[i];
		File file = new File(filename);
		try {
			FileInputStream fileInput = new FileInputStream(filename);
			int fileLength = (int) file.length();
			audioBytes[i] = new byte[fileLength];
			fileInput.read(audioBytes[i]);
			fileInput.close();
		} catch (IOException e) {
			playSounds = false;
			return;
		}
	}
}
