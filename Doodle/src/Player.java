public class Player {
	String username;
	int score;
	boolean isDrawing;
	boolean guessed;

	/**
	 * default constructor
	 */
	public Player() {
		isDrawing = false;
	}

	/**
	 * Makes a new Player given just a username
	 * 
	 * @param username
	 */
	public Player(String username) {
		this.username = username;
		score = 0;
		isDrawing = false;
		guessed = false;
	}

	/**
	 * Initializes a new Player given username, score, status of drawing, and if
	 * player has guessed. Used for updating player values in server userList.
	 * 
	 * @param username1
	 * @param score1
	 * @param isDrawing1
	 * @param guessed1
	 */
	public Player(String username1, int score1, boolean isDrawing1,
			boolean guessed1) {
		username = username1;
		score = score1;
		isDrawing = isDrawing1;
		guessed = guessed1;
	}

	public boolean isDrawing() {
		return isDrawing;
	}

	public void setDrawing(boolean isDrawing) {
		this.isDrawing = isDrawing;
	}

	public String toString() {
		return username;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public boolean isGuessed() {
		return guessed;
	}

	public void setGuessed(boolean guessed) {
		this.guessed = guessed;
	}

}
