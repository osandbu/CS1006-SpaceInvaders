package alien.entity;

import java.awt.Graphics;

import alien.Constants;

/**
 * The SpecialAlien class defines the behaviour of the special alien. It
 * randomly appears in the upper left or right corner and moves to the other
 * side of the screen.
 * 
 * @author os75
 */
public class SpecialAlien extends Entity implements Movable {
	private static final int MOVE_DISTANCE = 1;
	private static final int Y = 2;
	private static final int WIDTH = 63;
	private static final int HEIGTH = 25;
	private int direction;

	private static final int[] POLYGON_X = { 26, 46, 46, 53, 53, 57, 57, 60,
			60, 63, 63, 57, 57, 53, 53, 50, 50, 46, 46, 39, 39, 33, 33, 26, 26,
			22, 22, 19, 19, 15, 15, 9, 9, 12, 12, 15, 15, 19, 19, 26 };
	private static final int[] POLYGON_Y = { 4, 4, 8, 8, 11, 11, 14, 14, 18,
			18, 21, 21, 24, 24, 28, 28, 24, 24, 21, 21, 24, 24, 21, 21, 24, 24,
			28, 28, 24, 24, 21, 21, 18, 18, 14, 14, 11, 11, 8, 8 };

	private int[] xPoints;
	private int[] yPoints;

	/**
	 * Create a special alien which randomly appears in the left or right corner
	 * of the screen.
	 */
	public SpecialAlien() {
		super(1 - WIDTH, Y, WIDTH, HEIGTH);
		direction = randomDirection();
		if (direction == Constants.LEFT) {
			setX(Constants.PANEL_WIDTH - 1);
		}
		xPoints = new int[POLYGON_X.length];
		yPoints = new int[POLYGON_Y.length];
		updatePolygon();
	}

	private int randomDirection() {
		if (Math.random() < 0.5)
			return Constants.RIGHT;
		else
			return Constants.LEFT;
	}

	public void updatePolygon() {
		for (int i = 0; i < xPoints.length; i++) {
			xPoints[i] = POLYGON_X[i] + getX();
			yPoints[i] = POLYGON_Y[i] + getY();
		}
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Constants.SPECIAL_ALIEN_COLOR);
		g.fillPolygon(xPoints, yPoints, xPoints.length);
	}

	public void move() {
		setX(getX() + MOVE_DISTANCE * direction);
		updatePolygon();
	}

}
