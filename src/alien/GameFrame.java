package alien;

// BlankFrame.java
// Includes a BlankPanel, which is the game's drawing surface, and
//  two textfields for showing the average FPS/UPS.
// Pausing/Resuming/Quiting are controlled via the frame's window
//  listener methods.

// Based on code by Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

/**
 * Class used to create the frame in which the game is run. Based of the given
 * BlankFrame.java
 * 
 * @author Ole
 */
public final class GameFrame extends JFrame implements WindowListener {
	// unused
	private static final long serialVersionUID = 1L;

	private static int DEFAULT_FPS = 60;

	private GamePanel thePanel; // game drawing surface
	private JTextField fpsField; // displays frames per second
	private JTextField upsField; // displays updates per second
	private JTextField levelField;
	private JTextField scoreField;
	private JTextField livesField;

	// two decimal places.
	private DecimalFormat twoDP = new DecimalFormat("0.##");

	private JTextField alienField;

	/*
	 * ======================================================================
	 * Constructor
	 * ======================================================================
	 */
	public GameFrame(long period) {
		super(Constants.GAME_NAME);
		makeGUI(period);

		addWindowListener(this);
		pack();
		setResizable(false);
		setVisible(true);
	} // end of BlankFrame constructor

	/*
	 * ======================================================================
	 * makeGUI Adds an instance of BlankPanel, and two text fields (for UPS,
	 * FPS) to the content pane.
	 * ======================================================================
	 */
	private void makeGUI(long period) {
		Container c = getContentPane();

		JPanel ctrls = new JPanel(); // a row of textfields
		ctrls.setLayout(new BoxLayout(ctrls, BoxLayout.X_AXIS));

		fpsField = new JTextField("FPS: 0");
		fpsField.setEditable(false);
		ctrls.add(fpsField);

		upsField = new JTextField("UPS: 0 secs");
		upsField.setEditable(false);
		ctrls.add(upsField);

		levelField = new JTextField("Level: 1");
		levelField.setEditable(false);
		ctrls.add(levelField);

		scoreField = new JTextField("Score: 0");
		scoreField.setEditable(false);
		ctrls.add(scoreField);

		livesField = new JTextField("Lives: " + Constants.DEFAULT_LIVES);
		livesField.setEditable(false);
		ctrls.add(livesField);

		alienField = new JTextField("Aliens: " + Constants.ALIEN_COLS
				* Constants.ALIEN_ROWS);
		alienField.setEditable(false);
		ctrls.add(alienField);

		c.add(ctrls, "South");

		thePanel = new GamePanel(this, period);
		c.add(thePanel, "Center");
	} // end of makeGUI()

	/*
	 * ======================================================================
	 * setFPS Called from storeStats() in thePanel
	 * ======================================================================
	 */
	public void setFPS(double fps) {
		fpsField.setText("FPS: " + twoDP.format(fps));
	}

	/*
	 * ======================================================================
	 * setUPS Called from storeStats() in thePanel
	 * ======================================================================
	 */
	public void setUPS(double ups) {
		upsField.setText("UPS: " + twoDP.format(ups));
	}

	public void setLevel(int level) {
		levelField.setText("Level: " + level);
	}

	public void setScore(int score) {
		scoreField.setText("Score: " + score);
	}

	public void setLives(int lives) {
		livesField.setText("Lives: " + lives);
	}

	public void setAlienCount(int alienCount) {
		alienField.setText("Aliens: " + alienCount);
	}

	// ----------------- window listener methods ----------------------------
	public void windowActivated(WindowEvent e) {
		/*
		 * Do nothing. Unlike BlankFrame.java, which resumes the game.
		 */
	}

	public void windowDeactivated(WindowEvent e) {
		thePanel.pauseGame();
	}

	public void windowDeiconified(WindowEvent e) {
		thePanel.resumeGame();
	}

	public void windowIconified(WindowEvent e) {
		thePanel.pauseGame();
	}

	public void windowClosing(WindowEvent e) {
		thePanel.stopGame();
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	/*
	 * ======================================================================
	 * main method Takes in a requested FPS, calculates the update period in ns,
	 * then creates a new BlankFrame instance
	 * ======================================================================
	 */
	public static void main(String args[]) {
		int fps = DEFAULT_FPS;
		if (args.length != 0)
			fps = Integer.parseInt(args[0]);
		new GameFrame(1000000000 / fps);
	} // end of main method
} // end of BlankFrame class

