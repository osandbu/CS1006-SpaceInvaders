package alien.entity;

import java.awt.Graphics;


/**
 * A missile is a weapon which is fired towards enemies (i.e. by aliens towards
 * the player and the player towards the aliens). They also make barricades
 * degrade if hit. The Missile class is the superclass for all types of
 * Missiles.
 * 
 * @author Ole
 */
public abstract class Missile extends Entity implements Movable {
	public static final int MOVE_DISTANCE = 5;
	public static final int WIDTH = 2;
	public static final int HEIGHT = 4;

	/**
	 * Create a missile at a given point.
	 * 
	 * @param x
	 *            An x-coordinate.
	 * @param y
	 *            A y-coordinate.
	 */
	public Missile(int x, int y) {
		super(x, y, WIDTH, HEIGHT);
	}

	/**
	 * Specifies the way in which a missile moves, i.e. up or down, or something
	 * else.
	 */
	public abstract void move();

	/**
	 * Move missile up. Protected as it should only be called from subclasses.
	 */
	protected void moveUp() {
		setY(getY() - MOVE_DISTANCE);
	}

	/**
	 * Move missile down. Protected as it should only be called from subclasses.
	 */
	protected void moveDown() {
		setY(getY() + MOVE_DISTANCE);
	}

	@Override
	public void draw(Graphics g) {
		g.fillRect(getX(), getY(), getWidth(), getHeight());
	}

}
