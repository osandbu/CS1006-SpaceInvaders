package alien.entity;

import java.awt.Graphics;

import alien.Constants;

/**
 * AlienMissiles are fired by the Aliens and move downwards. AlienMissiles
 * degrade barricades and kill players if hit.
 * 
 * @see Alien
 * @author Ole
 */
public class AlienMissile extends Missile {

	/**
	 * Create an AlienMissile at the given point (x,y).
	 * 
	 * @param x
	 *            An integer.
	 * @param y
	 *            An integer.
	 */
	public AlienMissile(int x, int y) {
		super(x, y);
	}

	@Override
	public void move() {
		moveDown();
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Constants.ALIEN_MISSILE_COLOR);
		super.draw(g);
	}

}
