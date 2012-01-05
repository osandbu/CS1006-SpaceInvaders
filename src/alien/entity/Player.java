package alien.entity;

import java.awt.Graphics;

/**
 * The Player class is used to represent the control and representation of the
 * player's cannon that appears on the screen. The player moves left to right 
 * using the arrow keys and fires using the spacebar. However this class does 
 * not define the player controls.
 * 
 * @see PlayerMissile
 * @author Ole
 */

import alien.Constants;
import alien.Sound;

/**
 * The Player class is used to represent the behaviour and representation of the
 * player appearing on the screen. The player moves horizontally. This class
 * does not specify what makes the player moves.
 * 
 * @author os75
 */
public class Player extends Entity {
	public static final int WIDTH = 29;
	public static final int HEIGHT = 25;
	private static final int MOVE_DISTANCE = 2;
	private static final int[] POLYGON_X = { 11, 17, 17, 28, 28, 0, 0, 11 };
	private static final int[] POLYGON_Y = { 0, 0, 5, 5, 24, 24, 5, 5 };

	private int[] xPoints;
	private int[] yPoints;
	private int lives = Constants.DEFAULT_LIVES;

	/**
	 * Create a new player at the given coordinates.
	 * 
	 * @param x
	 *            A x-coordinate.
	 * @param y
	 *            A y-coordinate.
	 */
	public Player(int x, int y) {
		super(x, y, WIDTH, HEIGHT);
		xPoints = new int[POLYGON_X.length];
		yPoints = new int[POLYGON_Y.length];
		updatePolygon();
	}

	/**
	 * Update the polygon representing the player.
	 */
	private void updatePolygon() {
		for (int i = 0; i < xPoints.length; i++) {
			xPoints[i] = POLYGON_X[i] + getX();
			yPoints[i] = POLYGON_Y[i] + getY();
		}
	}

	/**
	 * Determines if the player is alive or not.
	 * 
	 * @return true if the player is alive, false otherwise.
	 */
	public boolean isAlive() {
		return lives > 0;
	}

	public int getLives() {
		return lives;
	}

	/**
	 * Make the player die.
	 */
	public void die() {
		lives--;
	}

	/**
	 * Make the player gain a life.
	 */
	public void gainLife() {
		lives++;
	}

	/**
	 * Reset the players lives to the default number.
	 */
	public void reset() {
		lives = Constants.DEFAULT_LIVES;
	}

	/**
	 * Move a set distance to the left.
	 */
	public void moveLeft() {
		int x = getX();
		if (x >= 0) {
			x -= MOVE_DISTANCE;
			if (x < 0)
				x = 0;
			setX(x);
		}
		updatePolygon();
	}

	/**
	 * Move a set distance to the right.
	 */
	public void moveRight() {
		int x = getX();
		if (x + WIDTH <= Constants.PANEL_WIDTH) {
			x += MOVE_DISTANCE;
			if (x + WIDTH > Constants.PANEL_WIDTH)
				x = Constants.PANEL_WIDTH - WIDTH;
			setX(x);
		}
		updatePolygon();
	}

	/**
	 * Fire a missile.
	 * 
	 * @return The fired missile.
	 */
	public Missile fire() {
		// play a laser sound.
		Sound.play(Sound.LASER);
		int x = getX() + getWidth() / 2 - Missile.WIDTH / 2;
		int y = getY() - 1;
		return new PlayerMissile(x, y);
	}

	@Override
	public void draw(Graphics g) {
		if (!isAlive())
			return;
		g.setColor(Constants.PLAYER_COLOR);
		g.fillPolygon(xPoints, yPoints, xPoints.length);
	}
}
