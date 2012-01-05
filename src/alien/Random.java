package alien;

/**
 * Class used to generate random numbers.
 * 
 * @author Ole
 * 
 */
public class Random {
	/**
	 * Generate a random number in the range 0 to max - 1.
	 * 
	 * @param max
	 *            The maximum number which can be returned, minus one.
	 * @return A random number in the range 0 to max - 1.
	 */
	public int generate(int max) {
		return generate(0, max);
	}

	/**
	 * Generate a random integer in the range min to max - 1.
	 * 
	 * @param min
	 *            A minimum number.
	 * @param max
	 *            A maximum number.
	 * @return A random integer in the range min to max - 1.
	 */
	public static int generate(int min, int max) {
		return (int) Math.random() * (max - min) + min;
	}

	/**
	 * Generate a random double value in the range min to max.
	 * 
	 * @param min
	 *            The minimum number that can be returned.
	 * @param max
	 *            The maximum number that can be returned.
	 * @return A randomly generated double value in the range min to max.
	 */
	public static double generate(double min, double max) {
		return Math.random() * (max - min) + min;
	}
}
