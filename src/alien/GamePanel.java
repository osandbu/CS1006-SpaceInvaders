package alien;

// BlankPanel.java
// The game's drawing surface
// Based on code by Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

import alien.entity.Alien;
import alien.entity.AlienMissile;
import alien.entity.Barricade;
import alien.entity.BarricadePart;
import alien.entity.Entity;
import alien.entity.Missile;
import alien.entity.Player;
import alien.entity.PlayerMissile;
import alien.entity.Rectangle;
import alien.entity.SpecialAlien;
import alien.hiscore.Hiscore;
import alien.hiscore.Score;
import alien.hiscore.ScoreFormatException;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Game used to display and process game elements and user input. Based on the
 * given BlankPanel.java.
 * 
 * @author Ole
 */
public final class GamePanel extends JPanel implements Runnable {
	/**
	 * Unused.
	 */
	private static final long serialVersionUID = 1L;

	// parent frame
	private GameFrame theFrame;

	public static final Rectangle gameScreen = new Rectangle(0, 0,
			Constants.PANEL_WIDTH, Constants.PANEL_HEIGHT);

	// Thread control ==========================================================
	// the thread that performs the animation
	private Thread animator;
	// used to stop the animation thread
	private boolean running = false;

	// period between drawing in _nanosecs_
	private long period;

	// Number of frames with a delay of 0 ms before the animation thread yields
	// to other running threads.
	private static final int NO_DELAYS_PER_YIELD = 16;

	// no. of frames that can be skipped in any one animation loop
	// i.e the games state is updated but not rendered
	private static int MAX_FRAME_SKIPS = 5;

	// number of FPS values stored to get an average
	private static int NUM_FPS = 10;

	// off screen rendering
	private Graphics g;
	private Image dbImage = null;

	// Stats ==================================================================
	private static long MAX_STATS_INTERVAL = 1000000000L;

	// used for gathering statistics
	private long statsInterval = 0L; // in ns
	private long prevStatsTime;
	private long totalElapsedTime = 0L;
	private long gameStartTime;
	private int timeSpentInGame = 0; // in seconds

	// number of frames which have been drawn on the Panel
	private long frameCount = 0;
	private double fpsStore[];
	private long statsCount = 0;
	private double averageFPS = 0.0;

	// number of frames which have been skipped
	private long framesSkipped = 0L;
	private long totalFramesSkipped = 0L;
	private double upsStore[];
	private double averageUPS = 0.0;

	private static final DecimalFormat df = new DecimalFormat("0.##"); // 2 dp

	// controls how often the special sound starts playing
	private static final int SPECIAL_SOUND_MAX = 140;
	/*
	 * Controls how many milliseconds there should be between each time the
	 * aliens move at the beginning of the game.
	 */
	private static final int START_ALIEN_MOVEMENT_DELAY = 500;
	/*
	 * Controls how often the last alien in each column should fire (delay in
	 * ms).
	 */
	private static final int START_ALIEN_FIRE_DELAY_MIN = 200;
	private static final int START_ALIEN_FIRE_DELAY_MAX = 5000;

	// --- TIMER VARIABLES ---
	// 
	private double alienFireDelayMin;
	private double alienFireDelayMax;
	/*
	 * Stores the amount of time between the last fired missile and when the
	 * next one should be fired.
	 */
	private double[] alienFireRandom = new double[Constants.ALIEN_COLS];
	// stores the time at which each column of aliens last fired
	private long[] alienFireTimer = new long[Constants.ALIEN_COLS];
	// The delay between each horizontal alien movement.
	private double alienMovementDelay = START_ALIEN_MOVEMENT_DELAY;
	// The time at which the aliens last moved.
	private long alienMoveTimer;
	// Controls that the special alien sound is played again at the right time
	private int specialSoundCount;
	private double alienMoveDelayMultiplier;
	// --- SPECIAL ALIEN TIMERS ---
	/*
	 * The minimum amount of time between the appearance of a new special alien
	 * after the beginning of a level or one is killed or disappears.
	 */
	private static final int SPECIAL_DELAY_MIN = 10000;
	/*
	 * The maximum amount of time between the appearance of a new special alien
	 * after the beginning of a level or one is killed or disappears.
	 */
	private static final int SPECIAL_DELAY_MAX = 20000;
	// the time at which the special alien timer started
	private long specialTimer;
	/*
	 * The amount of time after the start of the special alien timer the special
	 * alien should appear.
	 */
	private int specialDelay;

	// keyboard input variables
	private boolean leftPressed = false;
	private boolean rightPressed = false;
	private boolean spacePressed = false;

	// The state at which the game is in.
	private State state;
	// Stores the hiscores.
	private Hiscore scores;
	// The current score.
	private int score;
	// The current level.
	private int level;
	// the next alien move sound to be played.
	private int alienMoveSound = Sound.MOVE1;

	// visible entities
	private Player player;
	private Barricade[] barricades;
	private Alien[] aliens = new Alien[Constants.ALIEN_ROWS
			* Constants.ALIEN_COLS];
	private ArrayList<Missile> missiles = new ArrayList<Missile>();
	private SpecialAlien specialAlien;

	/*
	 * ======================================================================
	 * Constructor - Initialises the welcome screen.
	 * ======================================================================
	 */
	public GamePanel(GameFrame inFrame, long period) {
		theFrame = inFrame;
		this.period = period;

		setBackground(Color.black);
		setPreferredSize(new Dimension(Constants.PANEL_WIDTH,
				Constants.PANEL_HEIGHT));

		setFocusable(true);
		requestFocus(); // the JPanel now has focus, so receives key events
		setupControls();

		// initialise timing elements
		fpsStore = new double[NUM_FPS];
		upsStore = new double[NUM_FPS];
		for (int i = 0; i < NUM_FPS; i++) {
			fpsStore[i] = 0.0;
			upsStore[i] = 0.0;
		}

		try {
			scores = new Hiscore();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ScoreFormatException e) {
			e.printStackTrace();
		}

		player = new Player((Constants.PANEL_WIDTH - Player.WIDTH) / 2,
				Constants.PANEL_HEIGHT - Player.HEIGHT);
		barricades = new Barricade[Constants.BARRICADE_COUNT];
		setState(State.WELCOME_SCREEN);
	}

	/**
	 * Determines what is to be done when keyboard input is received.
	 */
	private void setupControls() {
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				// If ESC, q, end or ctrl+c is pressed. quit game.
				if ((keyCode == KeyEvent.VK_ESCAPE)
						|| (keyCode == KeyEvent.VK_Q)
						|| (keyCode == KeyEvent.VK_END)
						|| ((keyCode == KeyEvent.VK_C) && e.isControlDown())) {
					// quit game
					running = false;
				} else if (keyCode == KeyEvent.VK_P
						|| keyCode == KeyEvent.VK_PAUSE) {
					// pause/resume game
					if (state == State.PAUSED)
						resumeGame();
					else if (state == State.PLAYING)
						pauseGame();
				} else if (keyCode == KeyEvent.VK_SPACE) {
					onSpacePress();
				} else if (keyCode == KeyEvent.VK_LEFT) {
					leftPressed = true;
				} else if (keyCode == KeyEvent.VK_RIGHT) {
					rightPressed = true;
				} else if (keyCode == KeyEvent.VK_N && e.isControlDown()) {
					// Ctrl+N starts a new game.
					newGame();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if (keyCode == KeyEvent.VK_SPACE) {
					spacePressed = false;
				} else if (keyCode == KeyEvent.VK_LEFT) {
					leftPressed = false;
				} else if (keyCode == KeyEvent.VK_RIGHT) {
					rightPressed = false;
				}
			}
		});
	}

	/**
	 * Determines what should happen when space is pressed in the different
	 * states.
	 */
	private void onSpacePress() {
		switch (state) {
		case PLAYING:
			spacePressed = true;
			break;
		case LEVEL_PAUSE:
			nextLevel();
			break;
		case NEW_GAME:
			setState(State.PLAYING);
			break;
		case WELCOME_SCREEN:
		case GAME_OVER:
			newGame();
		}
	}

	/**
	 * Start a new game.
	 */
	private void newGame() {
		player.reset();
		score = 0;
		level = 0;
		resetFireDelay();
		nextLevel();
		updateAlienCounter();
		if (state != State.PLAYING)
			setState(State.NEW_GAME);
	}

	/**
	 * Go to next level.
	 */
	private void nextLevel() {
		level++;
		updateLevel();
		updateScore();
		alienMovementDelay = START_ALIEN_MOVEMENT_DELAY;
		alienMoveDelayMultiplier = 1 - level / 400D;
		setupEntities();
		decreaseFireDelay();
		if (state == State.LEVEL_PAUSE) {
			player.gainLife();
			setState(State.PLAYING);
		}
		updateLives();
	}

	/**
	 * Decrease the maximum amount of time in which an alien waits before firing
	 * again.
	 */
	private void decreaseFireDelay() {
		alienFireDelayMax *= 0.95;
	}

	/**
	 * Reset the randomisation variables of the delay between each time each
	 * alien column fires.
	 */
	private void resetFireDelay() {
		// currently never changed, but may want to in the future.
		alienFireDelayMin = START_ALIEN_FIRE_DELAY_MIN;
		alienFireDelayMax = START_ALIEN_FIRE_DELAY_MAX;
	}

	/**
	 * Randomise the aliens' fire timer and and the amount of time until the
	 * next time they fire. The fire timer starts when an alien fires.
	 */
	private void randomizeFireDelay() {
		for (int i = 0; i < Constants.ALIEN_COLS; i++) {
			alienFireTimer[i] = System.currentTimeMillis();
			alienFireRandom[i] = Random.generate(alienFireDelayMin,
					alienFireDelayMax);
		}
	}

	/**
	 * Reset the special alien so that it appears in SPECIAL_DELAY_MIN to
	 * SPECIAL_DELAY_MAX milliseconds after the method call.
	 */
	private void resetSpecialAlien() {
		if (specialAlien != null)
			specialAlien = null;
		specialDelay = Random.generate(SPECIAL_DELAY_MIN, SPECIAL_DELAY_MAX);
		specialTimer = System.currentTimeMillis();
	}

	/**
	 * Update the level in the status bar.
	 */
	private void updateLevel() {
		theFrame.setLevel(level);
	}

	/**
	 * Update the score in the status bar.
	 */
	private void updateScore() {
		theFrame.setScore(score);
	}

	/**
	 * Update the lives of the player in the status bar.
	 */
	private void updateLives() {
		theFrame.setLives(player.getLives());
	}

	/**
	 * Set up the entities on the screen in preparation for a new level.
	 */
	private void setupEntities() {
		missiles.clear();
		addBarricades();
		addAliens();
	}

	/**
	 * Add new barricades to the game.
	 */
	private void addBarricades() {
		int x = 30;
		for (int i = 0; i < barricades.length; i++) {
			Barricade bar = new Barricade(x, Constants.BARRICADE_AREA_Y);
			barricades[i] = bar;
			x += 125;
		}
	}

	/**
	 * Add aliens to the screen.
	 */
	private void addAliens() {
		int x;
		int y = 30;
		for (int row = 0; row < Constants.ALIEN_ROWS; row++) {
			x = 30;
			for (int col = 0; col < Constants.ALIEN_COLS; col++) {
				aliens[row * Constants.ALIEN_COLS + col] = new Alien(x, y);
				x += 50;
			}
			y += 40;
		}
	}

	/*
	 * ======================================================================
	 * gameUpdate The objects in the game are each updated.
	 * ======================================================================
	 */
	private void gameUpdate() {
		if (state == State.PLAYING) {
			if (timeForAlienMovement()) {
				Alien.changeAnimation();
				if (wrongAlienDirection()) {
					moveAliensDown();
					if (alienInBarricadeArea()) {
						setState(State.GAME_OVER);
					} else {
						Alien.changeDirection();
					}
				} else {
					moveAliens();
				}
				if (getAlienCount() > 0) {
					Sound.play(alienMoveSound);
					nextAlienSound();
				}
				alienMoveTimer = System.currentTimeMillis();
			}
			if (timeForSpecialAlien()) {
				specialAlien = new SpecialAlien();
			}
			if (specialAlien != null) {
				specialAlien.move();
				if (specialSoundCount == 0)
					Sound.play(Sound.UFOLOW);
				specialSoundCount = ++specialSoundCount % SPECIAL_SOUND_MAX;
			}
			alienFire();
			moveMissiles();
			deleteOutOfScreenEntities();
			processCollisions();
			processPressedKeys();
			updateScore();

			updateAlienCounter();
		}
	} // end of gameUpdate()

	/**
	 * Get the alienCount and update the alien counter in the status bar
	 * accordingly. This counter includes the special alien.
	 */
	private void updateAlienCounter() {
		int alienCount = getAlienCount();
		if (specialAlien != null)
			alienCount++;
		else if (alienCount == 0) {
			setState(State.LEVEL_PAUSE);
		}
		theFrame.setAlienCount(alienCount);
	}

	/**
	 * Returns the number of aliens which are alive, not including the special
	 * alien.
	 * 
	 * @return The number of aliens alive.
	 */
	private int getAlienCount() {
		int alienCount = 0;
		for (Alien alien : aliens) {
			if (alien != null)
				alienCount++;
		}
		return alienCount;
	}

	/**
	 * Determines if it is time for the aliens to make another horizontal move.
	 * 
	 * @return true is it is time for the aliens to make a move, false
	 *         otherwise.
	 */
	private boolean timeForAlienMovement() {
		return System.currentTimeMillis() - alienMoveTimer > alienMovementDelay;
	}

	/**
	 * Determines if it is time for a new special alien to appear.
	 * 
	 * @return True if is it time for a special alien to appear, false
	 *         otherwise.
	 */
	private boolean timeForSpecialAlien() {
		return specialAlien == null
				&& System.currentTimeMillis() - specialTimer > specialDelay;
	}

	/**
	 * Determines if the aliens are facing in the wrong direction and should
	 * move in the opposite direction of what they are.
	 * 
	 * @return true if the first living alien in any column is facing the wrong
	 *         direction, false otherwise.
	 */
	private boolean wrongAlienDirection() {
		for (int col = 0; col < Constants.ALIEN_COLS; col++) {
			for (int row = 0; row < Constants.ALIEN_ROWS; row++) {
				Alien alien = aliens[row * Constants.ALIEN_COLS + col];
				if (alien != null) {
					if (alien.facingWrongDirection()) {
						return true;
					}
					break;
				}
			}
		}
		return false;
	}

	/**
	 * Determines if the lowest row of living aliens have entered the barricade
	 * area.
	 * 
	 * @return true if the lowest row of living aliens have entered barricade
	 *         area, false otherwise.
	 */
	private boolean alienInBarricadeArea() {
		for (int row = Constants.ALIEN_ROWS - 1; row >= 0; row--) {
			for (int col = 0; col < Constants.ALIEN_COLS; col++) {
				Alien alien = aliens[row * Constants.ALIEN_COLS + col];
				// alien is dead, continue searching row.
				if (alien == null)
					continue;
				if (alien.inBarricadeArea())
					return true;
				/*
				 * If there are living aliens in the lowest row which are not in
				 * the barricade area.
				 */
				return false;
			}
		}
		return false;
	}

	/**
	 * Make all the aliens move down.
	 */
	private void moveAliensDown() {
		for (Alien alien : aliens) {
			if (alien != null)
				alien.moveDown();
		}
	}

	/**
	 * Go through and check if it is time for the last alien in each of the
	 * columns to fire, and it is, make them.
	 */
	private void alienFire() {
		for (int col = 0; col < Constants.ALIEN_COLS; col++) {
			boolean timeToFire = timeToFire(col);
			if (!timeToFire)
				continue;
			for (int row = Constants.ALIEN_ROWS - 1; row >= 0; row--) {
				Alien alien = aliens[row * Constants.ALIEN_COLS + col];
				// If the alien is dead, continue to the alien above it
				if (alien != null) {
					missiles.add(alien.fire());
					break;
				}
			}
			resetAlienColumnFireTimer(col);
		}
	}

	/**
	 * Set the current time as the time at which the selected alien column fired
	 * and randomise the time until the next time it will fire.
	 * 
	 * @param col
	 *            The column of aliens selected.
	 */
	private void resetAlienColumnFireTimer(int col) {
		alienFireTimer[col] = System.currentTimeMillis();
		alienFireRandom[col] = Random.generate(alienFireDelayMin,
				alienFireDelayMax);
	}

	/**
	 * Determine if it is time for the last alien in a column to fire.
	 * 
	 * @param col
	 *            The column of aliens selected.
	 * @return true if it is time for the last alien in a column to fire.
	 */
	private boolean timeToFire(int col) {
		return System.currentTimeMillis() - alienFireTimer[col] > alienFireRandom[col];
	}

	/**
	 * Delete any out of screen entities, including missiles and special aliens.
	 */
	private void deleteOutOfScreenEntities() {
		for (int i = 0; i < missiles.size(); i++) {
			if (!onScreen(missiles.get(i))) {
				missiles.remove(i--);
			}
		}
		if (specialAlien != null && !onScreen(specialAlien)) {
			resetSpecialAlien();
		}
	}

	/**
	 * Determines whether or not an entity is on the screen.
	 * 
	 * @param entity
	 *            An entity.
	 * @return true if any part of the entity is on the screen, false otherwise.
	 */
	private boolean onScreen(Entity entity) {
		return entity != null && entity.intersects(gameScreen);
	}

	/**
	 * Process keys which are pressed.
	 */
	private void processPressedKeys() {
		if (state != State.PLAYING)
			return;
		if (rightPressed) {
			if (!leftPressed)
				player.moveRight();
		} else if (leftPressed) {
			player.moveLeft();
		}
		if (spacePressed && getPlayerMissileCount() == 0) {
			missiles.add(player.fire());
		}
	}

	/**
	 * Get number of player missiles on the screen.
	 * 
	 * @return true if a player missile is on the screen, false otherwise.
	 */
	private int getPlayerMissileCount() {
		int count = 0;
		for (Missile mis : missiles)
			if (mis instanceof PlayerMissile)
				count++;
		return count;
	}

	/**
	 * Process collisions.
	 */
	private void processCollisions() {
		// process alienMissile collisions
		for (int misCount = 0; misCount < missiles.size(); misCount++) {
			Missile mis = missiles.get(misCount);
			if (player.collidedWith(mis)) {
				missiles.remove(misCount--);
				Sound.play(Sound.BOOM);
				player.die();
				if (!player.isAlive()) {
					setState(State.GAME_OVER);
				}
				updateLives();
			} else if (barricadeCollisionWith(mis)) {
				missiles.remove(misCount--);
				break;
			} else if (mis instanceof PlayerMissile && alienCollisionWith(mis)) {
				alienMovementDelay *= alienMoveDelayMultiplier;
				score += Constants.ALIEN_POINTS;
				Sound.play(Sound.KILL);
				missiles.remove(misCount--);
			} else if (specialAlien != null && specialAlien.collidedWith(mis)) {
				score += getSpecialAlienPoints();
				Sound.play(Sound.KILL);
				missiles.remove(misCount--);
				resetSpecialAlien();
			}
		}
	}

	/**
	 * Returns the number of points rewarded for killing the special alien at
	 * this time. This number is ten times the number of aliens that have been
	 * killed.
	 * 
	 * @return 10 * aliens killed
	 */
	private int getSpecialAlienPoints() {
		return (Constants.ALIEN_ROWS * Constants.ALIEN_COLS - getAlienCount())
				* Constants.SPECIAL_ALIEN_POINT_MULTIPLIER;
	}

	/**
	 * Determines whether a missile has collided with any of the barricades and
	 * degrades any barricade-part accordingly.
	 * 
	 * @param mis
	 *            A missile.
	 * @return true is the missile has collided with a barricade, false
	 *         otherwise.
	 */
	private boolean barricadeCollisionWith(Missile mis) {
		for (Barricade barricade : barricades) {
			for (int i = 0; i < barricade.size(); i++) {
				BarricadePart part = barricade.getPart(i);
				if (part.collidedWith(mis)) {
					if (mis instanceof AlienMissile) {
						part.degradeAbove();
					} else if (mis instanceof PlayerMissile) {
						part.degradeBelow();
					}
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Determines whether a missile has collided with any of the aliens and an
	 * alien accordingly.
	 * 
	 * @param mis
	 *            A missile.
	 * @return true is the missile has collided with an alien, false otherwise.
	 */
	private boolean alienCollisionWith(Missile mis) {
		for (int i = 0; i < aliens.length; i++) {
			Alien alien = aliens[i];
			if (alien != null && alien.collidedWith(mis)) {
				aliens[i] = null;
				return true;
			}
		}
		return false;
	}

	/**
	 * Move all the missiles on the screen.
	 */
	private void moveMissiles() {
		for (Missile m : missiles) {
			m.move();
		}
	}

	/**
	 * Move all the aliens on the screen.
	 */
	private void moveAliens() {
		for (Alien alien : aliens) {
			if (alien != null) {
				alien.move();
			}
		}
	}

	/**
	 * Choose the next alien sound to be played.
	 */
	private void nextAlienSound() {
		alienMoveSound++;
		if (alienMoveSound > Sound.MOVE4) {
			alienMoveSound = Sound.MOVE1;
		}
	}

	/*
	 * ======================================================================
	 * gameRender: Render the game objects onto dbImage
	 * ======================================================================
	 */
	private void gameRender() {
		if (dbImage == null) {
			dbImage = createImage(Constants.PANEL_WIDTH, Constants.PANEL_HEIGHT);
			if (dbImage == null) {
				System.out.println("dbImage is null");
				return;
			} else
				g = dbImage.getGraphics();
		}

		// clear the background
		g.setColor(Color.black);
		g.fillRect(0, 0, Constants.PANEL_WIDTH, Constants.PANEL_HEIGHT);

		// if not on welcome screen, draw entities
		if (state != State.WELCOME_SCREEN)
			drawEntities();
		// if not playing, draw a see-through rectangle
		if (state != State.PLAYING) {
			// draw a
			g.setColor(Constants.PAUSE_BACKGROUND_COLOR);
			g.fillRect(0, 0, Constants.PANEL_WIDTH, Constants.PANEL_HEIGHT);
		}
		// set the text color
		g.setColor(Constants.TEXT_COLOR);
		switch (state) {
		case WELCOME_SCREEN:
			drawStartingScreen();
			break;
		case LEVEL_PAUSE:
			drawLevelPause();
			break;
		case PAUSED:
			drawPauseScreen();
			break;
		case GAME_OVER:
			drawGameOver();
			break;
		case NEW_GAME:
			drawNewGameScreen();
			break;
		}

	} // end of gameRender()

	/**
	 * Get the y-coordinate of the location in which a text must be drawn to be
	 * in the center of the screen.
	 * 
	 * @return The y-coordinate.
	 */
	private int getCenteredStringY() {
		FontMetrics met = g.getFontMetrics();
		return (Constants.PANEL_HEIGHT - met.getHeight()) / 2;
	}

	/**
	 * Draw the starting screen.
	 */
	private void drawStartingScreen() {
		int y = getCenteredStringY() - 10;
		g.setFont(Constants.BIG_FONT);
		drawCenteredString(Constants.GAME_NAME_UPPERCASE, y);
		y += 40;
		g.setFont(Constants.NORMAL_FONT);
		drawCenteredString("Press SPACE to start a new game", y);
	}

	/**
	 * Draw the text which should be displayed when a new game has been created,
	 * but not started.
	 */
	private void drawNewGameScreen() {
		int y = getCenteredStringY() - 10;
		drawCenteredString("Are you ready!?", y);
		y += 20;
		drawCenteredString("Press SPACE to start", y);
	}

	/**
	 * Draw a centred string.
	 * 
	 * @param str
	 *            A string.
	 * @param y
	 *            The y-coordinate from which the string is to be drawn.
	 */
	private void drawCenteredString(String str, int y) {
		FontMetrics met = g.getFontMetrics();
		int x = (Constants.PANEL_WIDTH - met.stringWidth(str)) / 2;
		g.drawString(str, x, y);
	}

	/**
	 * Draw text which is to be displayed at the end of a game, when the player
	 * has lost.
	 */
	private void drawGameOver() {
		drawHiscores();
		int y = getCenteredStringY() - 10;
		drawCenteredString("GAME OVER", y);
		y += 20;
		drawCenteredString("Press SPACE for new game", y);
	}

	/**
	 * Draw the text which is to be displayed when a level has been completed
	 * successfully.
	 */
	private void drawLevelPause() {
		int y = getCenteredStringY() - 10;
		drawCenteredString("Level Clear!", y);
		y += 20;
		drawCenteredString("Press SPACE to continue", y);
	}

	/**
	 * Draw the text which is to be displayed when the game is paused. This
	 * includes instructions on how to play.
	 */
	private void drawPauseScreen() {
		int y = getCenteredStringY() - 70;
		drawCenteredString("PAUSED", y);
		y += 30;
		drawCenteredString("Controls", y);
		y += 20;
		drawCenteredString("Move left: Left arrow", y);
		y += 20;
		drawCenteredString("Move right: Right arrow", y);
		y += 20;
		drawCenteredString("Space bar: Shoot", y);
		y += 20;
		drawCenteredString("Resume game: P", y);
	}

	/**
	 * Draw hiscores onto the game panel.
	 * 
	 */
	private void drawHiscores() {
		int x = 30;
		int y = 30;
		g.drawString("Hiscores", x, y);
		y += 30;
		for (int i = 0; i < scores.length(); i++) {
			Score score = scores.getScore(i);
			if (score == null) {
				int pos = i + 1;
				String posStr = Integer.toString(pos) + ": ";
				if (pos != 10)
					posStr = '0' + posStr;
				g.drawString(posStr, x, y);
			} else {
				g.drawString((i + 1) + ": " + score.getName(), x, y);
				String scoreStr = Integer.toString(score.getScore());
				g.drawString(scoreStr, x + 60, y);
			}
			y += 30;
		}
	}

	/**
	 * Draw entities onto the screen. These include aliens, missiles,
	 * barricades, the player and occasionally a special alien.
	 * 
	 */
	private void drawEntities() {
		for (Alien alien : aliens) {
			if (alien != null)
				alien.draw(g);
		}
		for (Missile m : missiles) {
			m.draw(g);
		}
		for (Barricade bar : barricades) {
			bar.draw(g);
		}
		player.draw(g);
		if (specialAlien != null)
			specialAlien.draw(g);
	}

	/*
	 * ======================================================================
	 * addNotify: Notifies this component that it now has a parent component
	 * wait for the JPanel to be added to the JFrame before starting
	 * ======================================================================
	 */
	public void addNotify() {
		super.addNotify(); // creates the peer
		startGame(); // start the thread
	}

	/*
	 * ======================================================================
	 * startGame: initialise and start the thread
	 * ======================================================================
	 */
	private void startGame() {
		if (animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	} // end of startGame()

	// ------------- game life cycle methods ---------------------------------
	// called by the JFrame's window listener methods

	// called when the JFrame is activated / deiconified
	public void resumeGame() {
		if (state == State.PAUSED || state == State.NEW_GAME) {
			setState(State.PLAYING);
		}
	}

	// called when the JFrame is deactivated / iconified
	public void pauseGame() {
		if (state == State.PLAYING) {
			setState(State.PAUSED);
			spacePressed = false;
			leftPressed = false;
			rightPressed = false;
		}
	}

	// called when the JFrame is closing
	public void stopGame() {
		running = false;
	}

	// ----------------------------------------------------------------------

	/*
	 * ======================================================================
	 * run: Required by Runnable interface. The frames of the animation are
	 * drawn inside the while loop.
	 * ======================================================================
	 */
	public void run() {
		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;

		gameStartTime = System.nanoTime();
		prevStatsTime = gameStartTime;
		beforeTime = gameStartTime;

		running = true;

		while (running) {
			/*
			 * if (isPaused) { try { afterTime = System.nanoTime(); timeDiff =
			 * afterTime - beforeTime; sleepTime = (period - timeDiff) -
			 * overSleepTime; Thread.sleep(sleepTime / 1000000L); } catch
			 * (InterruptedException e) { } beforeTime = System.nanoTime();
			 * frameCount++; continue; }
			 */
			gameUpdate();
			gameRender();
			paintScreen();

			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			sleepTime = (period - timeDiff) - overSleepTime;

			if (sleepTime > 0) { // some time left in this cycle
				try {
					Thread.sleep(sleepTime / 1000000L); // nano -> ms
				} catch (InterruptedException ex) {
				}
				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
			} else { // sleepTime <= 0; the frame took longer than the period
				excess -= sleepTime; // store excess time value
				overSleepTime = 0L;

				if (++noDelays >= NO_DELAYS_PER_YIELD) {
					Thread.yield(); // give another thread a chance to run
					noDelays = 0;
				}
			}

			beforeTime = System.nanoTime();

			/*
			 * If frame animation is taking too long, update the game state
			 * without rendering it, to get the updates/sec nearer to the
			 * required FPS.
			 */
			int skips = 0;
			while ((excess > period) && (skips < MAX_FRAME_SKIPS)) {
				excess -= period;
				gameUpdate(); // update state but don't render
				skips++;
			}
			framesSkipped += skips;

			storeStats();
		}

		printStats();
		System.exit(0); // so window disappears
	} // end of run()

	/*
	 * ======================================================================
	 * paintScreen: use active rendering to put the buffered image on-screen
	 * ======================================================================
	 */
	private void paintScreen() {
		Graphics g;
		try {
			g = this.getGraphics();
			if ((g != null) && (dbImage != null))
				g.drawImage(dbImage, 0, 0, null);
			g.dispose();
		} catch (Exception e) {
			System.out.println("Graphics context error: " + e);
		}
	} // end of paintScreen()

	/*
	 * ======================================================================
	 * storeStats: The statistics: - the summed periods for all the iterations
	 * in this interval (period is the amount of time a single frame iteration
	 * should take), the actual elapsed time in this interval, the error between
	 * these two numbers;
	 * 
	 * - the total frame count, which is the total number of calls to run();
	 * 
	 * - the frames skipped in this interval, the total number of frames
	 * skipped. A frame skip is a game update without a corresponding render;
	 * 
	 * - the FPS (frames/sec) and UPS (updates/sec) for this interval, the
	 * average FPS & UPS over the last NUM_FPSs intervals.
	 * 
	 * The data is collected every MAX_STATS_INTERVAL (1 sec).
	 * ======================================================================
	 */
	private void storeStats() {
		frameCount++;
		statsInterval += period;

		// record stats every MAX_STATS_INTERVAL
		if (statsInterval >= MAX_STATS_INTERVAL) {
			long timeNow = System.nanoTime();
			timeSpentInGame = (int) ((timeNow - gameStartTime) / 1000000000L); // ns
			// -->
			// secs

			long realElapsedTime = timeNow - prevStatsTime; // time since last
			// stats collection
			totalElapsedTime += realElapsedTime;

			totalFramesSkipped += framesSkipped;

			double actualFPS = 0; // calculate the latest FPS and UPS
			double actualUPS = 0;
			if (totalElapsedTime > 0) {
				actualFPS = (((double) frameCount / totalElapsedTime) * 1000000000L);
				actualUPS = (((double) (frameCount + totalFramesSkipped) / totalElapsedTime) * 1000000000L);
			}

			// store the latest FPS and UPS
			fpsStore[(int) statsCount % NUM_FPS] = actualFPS;
			upsStore[(int) statsCount % NUM_FPS] = actualUPS;
			statsCount = statsCount + 1;

			double totalFPS = 0.0; // total the stored FPSs and UPSs
			double totalUPS = 0.0;
			for (int i = 0; i < NUM_FPS; i++) {
				totalFPS += fpsStore[i];
				totalUPS += upsStore[i];
			}

			if (statsCount < NUM_FPS) { // obtain the average FPS and UPS
				averageFPS = totalFPS / statsCount;
				averageUPS = totalUPS / statsCount;
			} else {
				averageFPS = totalFPS / NUM_FPS;
				averageUPS = totalUPS / NUM_FPS;
			}
			theFrame.setFPS(averageFPS);
			theFrame.setUPS(averageUPS);

			framesSkipped = 0;
			prevStatsTime = timeNow;
			statsInterval = 0L; // reset
		}
	} // end of storeStats()

	/*
	 * ======================================================================
	 * printStats: displays a stat summary, called upon termination.
	 * ======================================================================
	 */
	private void printStats() {
		System.out.println("Frame Count/Loss: " + frameCount + " / "
				+ totalFramesSkipped);
		System.out.println("Average FPS: " + df.format(averageFPS));
		System.out.println("Average UPS: " + df.format(averageUPS));
		System.out.println("Time Spent: " + timeSpentInGame + " secs");
	} // end of printStats()

	/**
	 * Save the hiscores to the hiscore file.
	 */
	public void saveScores() {
		try {
			scores.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the current state of the game.
	 * 
	 * @return The current state of the game.
	 */
	public State getState() {
		return state;
	}

	/**
	 * Set the current state of the game.
	 * 
	 * @param state
	 *            A state.
	 */
	public void setState(State state) {
		switch (state) {
		case PLAYING:
			if (specialAlien == null)
				resetSpecialAlien();
			randomizeFireDelay();
			break;
		case GAME_OVER:
			if (scores.eligible(score)) {
				scores.add(this, score);
				try {
					scores.save();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this,
							"Could not save hiscore file", "Writing error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
			break;
		}
		this.state = state;
	}

	// Enumerated type which specifies the state of the game.
	enum State {
		WELCOME_SCREEN, NEW_GAME, GAME_OVER, LEVEL_PAUSE, PAUSED, PLAYING
	}
} // end of BlankPanel class
