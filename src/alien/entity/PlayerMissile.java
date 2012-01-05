package alien.entity;

import java.awt.Graphics;

import alien.Constants;

/**
 * Missile the player fires at the aliens
 * 
 * @see Player
 * @author Ole & Peter
 */
public class PlayerMissile extends Missile {
	/**
	 * Creates a missile at the correct coordinate. Dependant on where the
	 * player is.
	 */
	public PlayerMissile(int x, int y) {
		super(x, y);
	}

	@Override
	public void move() {
		moveUp();
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Constants.PLAYER_MISSILE_COLOR);
		super.draw(g);
	}
}
