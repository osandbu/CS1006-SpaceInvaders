package alien;

import java.awt.Color;
import java.awt.Font;

/**
 * Class used to store constants used in other classes.
 * 
 * @author Ole
 */
public class Constants {
	// Name of the game, as written on the title at the top of the program.
	public static final String GAME_NAME = "Rage Invaders";
	// Name of the game, as written on the welcome screen.
	public static final String GAME_NAME_UPPERCASE = GAME_NAME.toUpperCase();

	// Constants for movement
	public static final int RIGHT = 1;
	public static final int LEFT = -1;
	public static final int UP = -1;
	public static final int DOWN = 1;

	// Size of panel in which game is displayed
	public static final int PANEL_WIDTH = 500;
	public static final int PANEL_HEIGHT = 400;

	/**
	 * Number of columns of aliens
	 */
	public static final int ALIEN_COLS = 7;
	/**
	 * Number of rows of aliens
	 */
	public static final int ALIEN_ROWS = 4;

	// Scoring
	public static final int ALIEN_POINTS = 10;
	public static final int SPECIAL_ALIEN_POINT_MULTIPLIER = 10;

	// Initial number of lives.
	public static final int DEFAULT_LIVES = 5;

	// Y coordinate of the top of the barricades
	public static final int BARRICADE_AREA_Y = 310;
	public static final int BARRICADE_COUNT = 4;
	public static final int BARRICADE_PART_MAX_HEALTH = 4;

	// Fonts
	public static final Font NORMAL_FONT = new Font("Dialog", Font.PLAIN, 12);
	public static final Font BIG_FONT = NORMAL_FONT.deriveFont(40F);

	// Colors
	// Slightly see through background
	public static final Color PAUSE_BACKGROUND_COLOR = new Color(0, 0, 0, 200);
	public static final Color SPECIAL_ALIEN_COLOR = Color.PINK;
	public static final Color ALIEN_COLOR = Color.GREEN;
	public static final Color ALIEN_MISSILE_COLOR = Color.YELLOW;
	public static final Color BARRICADE_COLOR = Color.BLUE;
	public static final Color PLAYER_COLOR = Color.RED;
	public static final Color PLAYER_MISSILE_COLOR = Color.CYAN;

	// Text color for drawing on the panel
	public static final Color TEXT_COLOR = Color.YELLOW;

}
