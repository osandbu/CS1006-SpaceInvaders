package alien.entity;

import java.awt.Graphics;

/**
 * The Entity is used as the superclass of all the visible elements on the
 * screen. Extends Rectangle in order to provide basic collision detection.
 * 
 * @author Ole
 */
public abstract class Entity extends Rectangle {

	/**
	 * Create a new Entity with the given x, and y coordinate, width and height.
	 * The x and y coordinate is used for drawing purposes, while the width and
	 * height are used for collision detection.
	 * 
	 * @param x
	 *            The x-coordinate of the left-hand-side.
	 * @param y
	 *            The y-coordinate of the top of the entity.
	 * @param width
	 *            The width of the entity.
	 * @param height
	 *            The height of the entity.
	 */
	public Entity(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	/**
	 * Check if this entity has collided with another entity.
	 * 
	 * @param other
	 *            The entity with which this entity is checked to have collided
	 *            with.
	 * @return true if this entity has collided with the other entity, false
	 *         otherwise.
	 */
	public boolean collidedWith(Entity other) {
		return intersects(other);
	}

	/**
	 * Draw this entity on a given graphical context.
	 * 
	 * @param g
	 *            The graphics context to which this entity is to be drawn.
	 */
	public abstract void draw(Graphics g);
}
