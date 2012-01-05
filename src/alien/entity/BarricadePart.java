package alien.entity;

import java.awt.Graphics;

import alien.Constants;

/**
 * A BarricadePart is a part of a barricade. They degrade partially each time
 * they are hit by missiles, and on the forth hit, degrade completely.
 * 
 * @see Barricade
 * @author Ole
 */
public class BarricadePart extends Entity {
	// The height of a BarricadePart
	public static final int WIDTH = 12;
	// The width of a BarricadePart
	public static final int HEIGHT = 12;

	/*
	 * The health of this barricade part. When it hits zero, the barricade part
	 * has degraded completely.
	 */
	private int health = Constants.BARRICADE_PART_MAX_HEALTH;

	/**
	 * Create a barricade part with the given coordinates as it's
	 * upper-left-hand corner.
	 * 
	 * @param x
	 *            The x-coordinate.
	 * @param y
	 *            The y-coordinate.
	 */
	public BarricadePart(int x, int y) {
		super(x, y, WIDTH, HEIGHT);
	}

	public void draw(Graphics g) {
		g.fillRect(getX(), getY(), getWidth(), getHeight());
	}

	@Override
	public boolean collidedWith(Entity e) {
		return health > 0 && super.collidedWith(e);
	}

	/**
	 * Degrade from above.
	 */
	public void degradeAbove() {
		setY(getY() + HEIGHT / Constants.BARRICADE_PART_MAX_HEALTH);
		degrade();
	}

	/**
	 * Degrade from below.
	 */
	public void degradeBelow() {
		degrade();
	}

	/**
	 * Degrade this barricade part.
	 */
	private void degrade() {
		health--;
		setHeight(getHeight() - HEIGHT / Constants.BARRICADE_PART_MAX_HEALTH);
	}
}
