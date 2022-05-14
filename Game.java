import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class Game extends JPanel implements Runnable, KeyListener {
	/**
	 * This is the main game method, it handles all the game logic as well as any rendering used in the game.
	 */
	private static final long serialVersionUID = 1L;
	
	private final int BLOCK_WIDTH = 100;
	private final int BLOCK_HEIGHT = 100;
	private final int X_OFFSET = 130;
	private final int Y_OFFSET = 90;
	private final int X_SPACE = 10;
	private final int Y_SPACE = 10;
	private final int X_SHRINK = 20;
	private final int Y_SHRINK = 20;
	
	int currentRow = 0;
	int[][] grid = new int[6][5];
	char[][] letters = new char[6][5];
	
	BufferedImage[] tiles;
	BufferedImage[] letterSprites;
	
	Text[] oldGuesses = new Text[6];

	String[] wordBank = {
		"Farid", "Early"	
	};
		
	Text points;
	Text guess;
	Text timer;

	String currentGuess = "";
	String word;
	boolean found = false;
	boolean spacePressed = false;
	boolean gameOver = false;
	boolean running = true;
	int score = 0;
	
	double lastTime = System.currentTimeMillis();
	double currentTime = System.currentTimeMillis();
	double timeEllapsed = 1000;
	int timeRemaining = 100;
	
	TextRenderer text;
	
	/**
	 * Initializer for the game
	 */
	public Game() {
		tiles = Main.loadSpriteSheet(9, 4, "assets/tiles.png"); //Loads sprite sheet of tiles into image array
		word = wordBank[(int)(Math.random() * wordBank.length)].toUpperCase(); //Loads a random word from the word bank
		text = new TextRenderer(); //Initialises a new TextRenderer
				
		text.addMessage("GARBLE", 220, 30, 50, 50, 10); //Add a word to be displayed onto the window
		timer = text.addMessage(Integer.toString(timeRemaining), 5, 5, 25, 25, 0);
		points = text.addMessage(Integer.toString(score), 700, 5, 25, 25, 0);
		guess = text.addMessage(
					"", 
					X_OFFSET + X_SHRINK, 
					currentRow * (BLOCK_HEIGHT + Y_SPACE) + Y_OFFSET + Y_SHRINK, 
					BLOCK_WIDTH - X_SHRINK * 2, 
					BLOCK_HEIGHT - Y_SHRINK * 2, 
					X_SPACE + X_SHRINK * 2
				);
		
		Thread thread = new Thread(this); //Create a new thread
		thread.start(); //Start the thread
	}
	
	/**
	 * Resets the game
	 */
	public void reset() {
		/*
		 * Add a pause before reseting 
		 */
		try {
			Thread.sleep(750);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * Resets all variables to default
		 */
		letters = new char[6][5];
		found = false;
		currentGuess = "";
		currentRow = 0;
		word = wordBank[(int)(Math.random() * wordBank.length)].toUpperCase();
		guess.y = currentRow * (BLOCK_HEIGHT + Y_SPACE) + Y_OFFSET + Y_SHRINK;
		guess.text = "";
		grid = new int[6][5];
		for(Text t : oldGuesses) {
			text.removeMessage(t);
		}
		repaint();
	}
	
	/**
	 * Restart screen
	 */
	public void restart() {
		/*
		 * Create the ending message:
		 * 
		 * 			SPACE
		 * 			 TO
		 * 		   CONTINUE
		 */
		gameOver = true;
		Text t1 = text.addMessage("SPACE", 180, 200, 75, 75, 10);
		Text t2 = text.addMessage("TO", 300, 300, 75, 75, 10);
		Text t3 = text.addMessage("CONTINUE", 60, 400, 75, 75, 10);
	
		repaint(); //Repaint the screen to show message
		
		/*
		 * Wait until space is pressed then continue
		 */
		while(!spacePressed) {
			System.out.println(); //Scuffed code, too lazy to look for proper implementation. If there is no code here or just continue it won't work. Don't know why.
		}
		
		gameOver = false;		
		timeRemaining = 101;
		timer.setText(timeRemaining);
		score = 0;
	    points.setText(score);
		/*
		 * Removes all message
		 */
		text.removeMessage(t1);
		text.removeMessage(t2);
		text.removeMessage(t3);
	}
	
	/**
	 * Main game loop
	 */
	@Override
	public void run() {
		while(running) {
			guess.text = currentGuess;
			repaint(); //Updates the panel
			
			/*
			 * Timer
			 */
			currentTime = System.currentTimeMillis();
			timeEllapsed -= currentTime - lastTime;
			lastTime = currentTime;
			if(timeEllapsed <= 0) {
				timeEllapsed = 1000;
				timeRemaining--;
				timer.setText(timeRemaining);
			}
			
			/*
			 * Logic check if word is found or if player loses
			 */
			if(found) {
				score++;
				points.setText(score);
				reset();
			}else if(currentRow >= 6 || timeRemaining <= 0) {
				reset();
				restart();
			}
		}
	}
	
	/**
	 * Main paint method
	 * 
	 * @param g2		Graphics object
	 */
	public void paintComponent(Graphics g2){
		/*
		 * Initialise graphics2D
		 */
		super.paintComponent(g2);
		Graphics2D g = (Graphics2D)g2;
		
		/*
		 * Draws all the tiles as well as previous word attempts
		 */
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 6; j++) {
				//Draw the tile
				g.drawImage(
						tiles[grid[j][i]], 
						i * (BLOCK_WIDTH + X_SPACE) + X_OFFSET, 
						j * (BLOCK_HEIGHT + Y_SPACE) + Y_OFFSET, 
						BLOCK_WIDTH, 
						BLOCK_HEIGHT, 
						null
				);
			}
		}
		/**
		 * Draw the message background if the game is over
		 */
		if(gameOver) {
			g.drawImage(
					tiles[1], 
					40,
					150,
					700,
					400,
					null
			);
		}
		/*
		 * Render all the texts
		 */
		text.render(g);
	}
	
	/**
	 * Main game logic, compares the current guess with the word. Returns a string which includes data such as:
	 * 		'+', letter is in the word, and in the right place
	 * 		'|', letter is in the word, and not in the right place
	 * 		'*', letter is not in the word
	 *
	 * @param guess			Current guess
	 * @return				Formatted answer string
	 */
	public String checkWord(String guess) {
		String currentGuess = ""; //Initialize output string
		int[] letterCount = countLetters(word); //Counts number of each letter in the word
		guess = guess.toUpperCase(); //Formats the guess to uppercase
		if(guess.length() != 5) { //Checks if the guess is 5 letters long
			return null;
		}
		/*
		 * First checking if there there are any letters in the right spot 
		 */
		for(int i = 0; i < guess.length(); i++) {
			char c = guess.charAt(i);
			if(c == word.charAt(i)) {
				currentGuess += '+'; //If in the right place put a + in the output string
				letterCount[c - 'A']--; //Reduce number letters remaining in the word
			}else {
				currentGuess += '*'; //Assumes that all letters that are not in the right place does not exist in the word
			}
		}
		/*
		 * Checks if any letters are in the word but in the wrong place
		 */
		for(int i = 0; i < guess.length(); i++) {
			char c = guess.charAt(i);
			if(letterCount[c - 'A'] > 0) {
				currentGuess = currentGuess.substring(0, i) + '|' + currentGuess.substring(i + 1); //If letter is in the wrong place add | to the output string
				letterCount[c - 'A']--;
			}
		}
		
		return currentGuess;
	}
	
	/**
	 * Updates the background tiles to correlate with the current guess:
	 * 		Green, 	letter is in the word, and in the right place
	 * 		Yellow, letter is in the word, and not in the right place
	 * 		Gray, 	letter is not in the word
	 * @param guess			Formatted guess string
	 */
	public void updateGrid(String word) {
		if(word == null) {
			return;
		}
		boolean f = true; //Checks if all letters are correct
		for(int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			letters[currentRow][i] = currentGuess.charAt(i);
			if(c == '+') { //If the formatted string has a + it means that the letter is in the right spot
				grid[currentRow][i] = 3;
			}else if(c == '|') { //If the formatted string has a | it means that the letter is in the wrong spot
				grid[currentRow][i] = 2;
				f = false;
			}else if(c == '*') { //If the formatted string has a * it means that the letter is not in the word
				grid[currentRow][i] = 1;
				f = false;
			}
		}
		oldGuesses[currentRow] = text.addMessage(guess.copy());
		found = f;
		currentGuess = ""; //Resets the guess
		currentRow++; //Shifts the current try down, aka shifting the current guess row down one
		guess.text = currentGuess;
		guess.y = currentRow * (BLOCK_HEIGHT + Y_SPACE) + Y_OFFSET + Y_SHRINK;
	}
	
	/**
	 * Counts the number of each letter in a word
	 * @param word			Word to search
	 * @return				Integer array correlating with number of each letter [A-Z]
	 */
	public int[] countLetters(String word) {
		int[] a = new int[26];
		for(int i = 0; i < word.length(); i++) {
			a[word.charAt(i) - 'A']++;
		}
		return a;
	}

	@Override
	public void keyTyped(KeyEvent e) {}
	
	/**
	 * Checks if a key is pressed
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		/*
		 * Get key board letter inputs and print letter onto screen 
		 */
		if(key - 'A' >= 0 && key - 'A' <= 26 && currentGuess.length() < 5) {
			currentGuess += (char) key;
		}
		/*
		 *	Delete last letter from current guess; Press BACKSPACE
		 */
		if(key == KeyEvent.VK_BACK_SPACE) {
			if(currentGuess.length() > 0) {
				currentGuess = currentGuess.substring(0, currentGuess.length() - 1);
			}
		}
		/*
		 * Check the current guess; Press ENTER
		 */
		if(key == KeyEvent.VK_ENTER) {
			updateGrid(checkWord(currentGuess));
		}
		/*
		 * Continue a new round; Press SPACE
		 */
		if(key == KeyEvent.VK_SPACE) {
			spacePressed = true;
		}
		/*
		 * Quit the game; Press ESC
		 */
		if(key == KeyEvent.VK_ESCAPE) {
			running = false;
			Main.close();
		}
	}

	/**
	 * Checks if a key is released
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_SPACE) {
			spacePressed = false;
		}
	}
}
