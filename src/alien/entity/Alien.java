package alien.entity;

import java.awt.Graphics;

import alien.Constants;

/**
 * The Alien class is used to represent the behaviour and representation of the
 * aliens appearing on the screen. Aliens move from left to right, then move
 * down and turn around before hitting the wall. This class does not specify how
 * often aliens move or fire.
 * 
 * @see AlienMissile
 * @author Ole
 */
public class Alien extends Entity implements Movable {
	// the distance an alien moves each time the move method is called
	private static final int MOVE_DISTANCE = 10;

	// The width of an alien. Used for collision detection.
	private static final int WIDTH = 34;
	// The height of an alien. Used for collision detection.
	private static final int HEIGHT = 24;

	/*
	 * Arrays used to specify the points of the polygons of the aliens (first
	 * frame).
	 */
	private static final int[] ANIM1_X = { 0, 3, 3, 11, 11, 22, 22, 31, 31, 34,
			34, 25, 25, 28, 28, 34, 34, 23, 23, 14, 14, 20, 20, 11, 11, 5, 5,
			0, 0, 6, 6, 8, 8, 0 };
	private static final int[] ANIM1_Y = { 6, 6, 3, 3, 0, 0, 3, 3, 6, 6, 14,
			14, 17, 17, 23, 23, 20, 20, 17, 17, 20, 20, 17, 17, 20, 20, 23, 23,
			20, 20, 17, 17, 14, 14 };

	/*
	 * Arrays used to specify the points of the polygons of the aliens (second
	 * frame).
	 */
	private static final int[] ANIM2_X = { 0, 3, 3, 11, 11, 22, 22, 30, 30, 33,
			33, 28, 28, 30, 30, 28, 28, 22, 22, 25, 25, 19, 19, 14, 14, 19, 19,
			8, 8, 11, 11, 5, 5, 3, 3, 5, 5, 0 };
	private static final int[] ANIM2_Y = { 6, 6, 3, 3, 0, 0, 3, 3, 6, 6, 14,
			14, 17, 17, 20, 20, 23, 23, 20, 20, 17, 17, 14, 14, 20, 20, 17, 17,
			20, 20, 23, 23, 20, 20, 17, 17, 14, 14 };

	// the direction in which the aliens are moving
	private static int direction = Constants.RIGHT;
	// which frame of the animation is to be displayed
	private static boolean anim1 = true;

	// x-coordinates of the polygon representing this alien
	private int[] xPoints;
	// y-coordinates of the polygon representing this alien
	private int[] yPoints;

	/**
	 * Creates a new Alien whose upper-left-hand-corner is at the point (x,y).
	 * 
	 * @param x
	 *            An integer x-coordinate.
	 * @param y
	 *            An integer y-coordinate.
	 */
	public Alien(int x, int y) {
		super(x, y, WIDTH, HEIGHT);
		updatePolygon();
	}

	/**
	 * Get the direction in which all the aliens are moving.
	 * 
	 * @return The direction in which the aliens are moving. Either 1 for right,
	 *         or -1 for left.
	 */
	public static int getDirection() {
		return direction;
	}

	/**
	 * Set the direction in which all the aliens should move.
	 * 
	 * @param newDirection
	 *            A direction represented as an integer. 1 for right, -1 for
	 *            left.
	 */
	public static void setDirection(int newDirection) {
		direction = newDirection;
	}

	/**
	 * Switch the direction in which all the aliens are moving.
	 */
	public static void changeDirection() {
		setDirection(getDirection() * -1);
	}

	/**
	 * Switch to the other animation frame.
	 */
	public static void changeAnimation() {
		anim1 = !anim1;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Constants.ALIEN_COLOR);
		g.fillPolygon(xPoints, yPoints, xPoints.length);
	}

	public void move() {
		setX(getX() + getDirection() * MOVE_DISTANCE);
		// update the polygons position
		updatePolygon();
	}

	public void moveDown() {
		setY(getY() + MOVE_DISTANCE);
		updatePolygon();
	}

	/**
	 * Determines whether an alien is facing the wrong direction, i.e. if it has
	 * gone too far to one side and it is time for it to turn around and moving
	 * in the other direction.
	 * 
	 * @return true if the alien is facing the wrong direction, false otherwise.
	 */
	public boolean facingWrongDirection() {
		if (getX() < MOVE_DISTANCE && getDirection() == Constants.LEFT) {
			return true;
		} else if (getX() > Constants.PANEL_WIDTH - WIDTH - MOVE_DISTANCE
				&& getDirection() == Constants.RIGHT) {
			return true;
		}
		return false;
	}

	/**
	 * Determines if the alien has entered the barricade area (causing the game
	 * to be over).
	 * 
	 * @return true if the alien is in the barricade area, false otherwise.
	 */
	public boolean inBarricadeArea() {
		return Constants.BARRICADE_AREA_Y <= getY() + getHeight();
	}

	/**
	 * Update the polygon representing alien.
	 */
	public void updatePolygon() {
		int[] animX;
		int[] animY;
		if (anim1) {
			animX = ANIM1_X;
			animY = ANIM1_Y;
		} else {
			animX = ANIM2_X;
			animY = ANIM2_Y;
		}
		xPoints = new int[animX.length];
		yPoints = new int[animY.length];
		for (int i = 0; i < xPoints.length; i++) {
			xPoints[i] = getX() + animX[i];
			yPoints[i] = getY() + animY[i];
		}
	}

	/**
	 * Fire a missile towards the player.
	 * 
	 * @return The fired missile.
	 */
	public AlienMissile fire() {
		int x = getX() + getWidth() / 2;
		int y = getY() + getHeight();
		return new AlienMissile(x, y);
	}
}
