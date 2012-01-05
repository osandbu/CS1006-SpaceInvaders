package alien.hiscore;


public class Score {
	private String playerName;
	private int score;

	/**
	 * Create new Score object with given playerName and score.
	 * 
	 * @param playerName
	 *            The player's name.
	 * @param score
	 *            The score.
	 */
	public Score(String playerName, int score) {
		this.playerName = playerName;
		this.score = score;
	}

	public String getName() {
		return playerName;
	}

	public int getScore() {
		return score;
	}

	/**
	 * Read a score entry from a line of a file.
	 * 
	 * @param line
	 *            The line.
	 * @return The score.
	 * @throws ScoreFormatException
	 *             If the line is formatted incorrectly.
	 */
	public static Score fromLine(String line) throws ScoreFormatException {
		String[] split = line.split("\t");
		if (split.length != 2)
			throw new ScoreFormatException(
					"Incorrectly formatted information (expecting a string, a tab and an integer).\nIn line:\n"
							+ line);
		try {
			String name = split[0];
			int score = Integer.parseInt(split[1]);
			return new Score(name, score);
		} catch (NumberFormatException e) {
			throw new ScoreFormatException("Error in line:\n" + line);
		}
	}

	/**
	 * toString method used when writing score to file.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(playerName);
		sb.append('\t');
		sb.append(score);
		return sb.toString();
	}
}
