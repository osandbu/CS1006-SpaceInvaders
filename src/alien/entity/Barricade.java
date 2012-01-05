package alien.entity;

import java.awt.Graphics;

import alien.Constants;

/**
 * A barricade is used to protect the player against alien/enemy missiles. It
 * consists of barricade parts, which partially degrade when shot.
 * 
 * @author Ole
 */
public class Barricade extends Entity {
	// The BarricadeParts this Barricade consists of.
	private BarricadePart[] parts;

	/**
	 * Creates a Barricade consisting of 10 BarricadeParts, in a pattern like
	 * this: 
	 * XXXX 
	 * XXXX 
	 * X  X
	 * 
	 * @param x
	 *            The x-coordinate of the left-hand side.
	 * @param y
	 *            The y-coordinate of the upper side.
	 */
	public Barricade(int x, int y) {
		// Neither the width nor the height is used.
		super(x, y, 0, 0);
		parts = new BarricadePart[10];
		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < 4; col++) {
				parts[row * 4 + col] = new BarricadePart(x, y);
				x += BarricadePart.WIDTH;
			}
			x = getX();
			y += BarricadePart.HEIGHT;
		}
		parts[8] = new BarricadePart(x, y);
		parts[9] = new BarricadePart(x + 3 * BarricadePart.WIDTH, y);
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Constants.BARRICADE_COLOR);
		for (BarricadePart part : parts) {
			if (part != null)
				part.draw(g);
		}
	}

	@Override
	// checks if the entity has collided with any of the barricade's parts
	public boolean collidedWith(Entity entity) {
		for (BarricadePart part : parts) {
			if (part != null && part.collidedWith(entity)) {
				return true;
			}
		}
		return false;
	}

	public BarricadePart getPart(int index) {
		return parts[index];
	}

	public int size() {
		return parts.length;
	}
}
