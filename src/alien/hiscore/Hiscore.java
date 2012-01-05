package alien.hiscore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

public class Hiscore {
	private static final int LENGTH = 10;
	
	/**
	 * @clientCardinality 1
	 * @supplierCardinality 0..10
	 */
	
	private Score[] scores = new Score[LENGTH];
	private String filename;

	/**
	 * Initialise and load the hiscores file in the default location.
	 * 
	 * @throws IOException
	 *             If a reading error is encountered while loading the hiscores.
	 * @throws ScoreFormatException
	 *             If the hiscore file is formatted incorrectly.
	 */
	public Hiscore() throws IOException, ScoreFormatException {
		this("hiscores.txt");
	}

	/**
	 * Initialise and load the hiscores file in a given location.
	 * 
	 * @param filename
	 *            The filename of the hiscores file.
	 * @throws IOException
	 *             If a reading error is encountered while loading the hiscores.
	 * @throws ScoreFormatException
	 *             If the hiscore file is formatted incorrectly.
	 */
	public Hiscore(String filename) throws IOException, ScoreFormatException {
		this.filename = filename;
		load();
	}

	/**
	 * Load the hiscores from file specified in constructor.
	 * 
	 * @throws IOException
	 *             If a reading error is encountered while loading the hiscores.
	 * @throws ScoreFormatException
	 *             If the hiscore file is formatted incorrectly.
	 */
	public void load() throws IOException, ScoreFormatException {
		File file = new File(filename);
		if (!file.exists()) {
			file.createNewFile();
			return;
		}
		BufferedReader in = new BufferedReader(new FileReader(file));
		int i = 0;
		while (in.ready() && i < scores.length) {
			scores[i] = Score.fromLine(in.readLine());
			i++;
		}
		if (in.ready()) {
			throw new ScoreFormatException(
					"Length of hiscore file is too long.");
		}
		in.close();
	}

	/**
	 * Save hiscores to file specified in constructor.
	 * 
	 * @throws IOException
	 */
	public void save() throws IOException {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(filename)));
			for (int i = 0; i < scores.length && scores[i] != null; i++) {
				out.println(scores[i]);
			}
			out.close();
		} catch (FileNotFoundException e) {
		}
	}

	/**
	 * Returns true if the score is eligible to be added to the hiscore table.
	 * 
	 * @param score
	 *            A score
	 * @return True if the hiscore list is not full or the score passed in is
	 *         higher than any one entry
	 */
	public boolean eligible(int score) {
		if (score <= 0)
			return false;
		if (scores[scores.length - 1] == null)
			return true;
		if (score > scores[scores.length - 1].getScore())
			return true;
		else
			return false;
	}

	/**
	 * Add Score with given player name and points.
	 * 
	 * @param playerName
	 *            A player's name.
	 * @param points
	 *            A score.
	 */
	public void add(String playerName, int points) {
		Score newScore = new Score(playerName, points);
		int index = -1;
		for (int i = 0; i < scores.length; i++) {
			if (scores[i] == null || points > scores[i].getScore()) {
				index = i;
				break;
			}
		}
		if (index == -1)
			return;
		for (int i = scores.length - 1; i > index; i--) {
			scores[i] = scores[i - 1];
		}
		scores[index] = newScore;
	}

	/**
	 * Read name of player from a popup window, and add score to hiscores.
	 * 
	 * @param owner
	 *            The owner component.
	 * @param points
	 */
	public void add(JComponent owner, int points) {
		String name = JOptionPane.showInputDialog(owner,
				"Please enter a nickname (up to 7 characters).", "Hiscore!",
				JOptionPane.QUESTION_MESSAGE);
		if (name == null)
			return;
		if (name.length() > 7) {
			JOptionPane.showMessageDialog(owner,
					"The name entered is too long, please try again.",
					"Name too long", JOptionPane.INFORMATION_MESSAGE);
			add(owner, points);
		} else {
			add(name, points);
		}
	}

	public Score getScore(int index) {
		return scores[index];
	}

	public int length() {
		return scores.length;
	}
}
