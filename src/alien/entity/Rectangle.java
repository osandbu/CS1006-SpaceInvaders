package alien.entity;

/**
 * Wrapper used to wrap a java.awt.Rectangle. Provides basic getters and setters
 * and a method of collision detection.
 * 
 * @author Ole
 */
public class Rectangle {
	private java.awt.Rectangle value;

	/**
	 * Create a rectangle at a starting point (x, y) and with a given width and
	 * height.
	 * 
	 * @param x
	 *            The x-coordinate.
	 * @param y
	 *            The y-coordinate.
	 * @param width
	 *            The width.
	 * @param height
	 *            The height.
	 */
	public Rectangle(int x, int y, int width, int height) {
		value = new java.awt.Rectangle(x, y, width, height);
	}

	public int getX() {
		return value.x;
	}

	public void setX(int x) {
		value.x = x;
	}

	public int getY() {
		return value.y;
	}

	public void setY(int y) {
		value.y = y;
	}

	public int getWidth() {
		return value.width;
	}

	public void setWidth(int width) {
		value.width = width;
	}

	public int getHeight() {
		return value.height;
	}

	public void setHeight(int height) {
		value.height = height;
	}

	/**
	 * Determines if this rectangle intersects another rectangle.
	 * 
	 * @param other
	 *            Another rectangle.
	 * @return true if the rectangles intersect, false otherwise.
	 */
	public boolean intersects(Rectangle other) {
		return value.intersects(other.value);
	}
}
