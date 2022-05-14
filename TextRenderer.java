import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class TextRenderer {
	/**
	 * This class handles all the rendering of messages onto the screen.
	 */
	private List<Text> texts = new ArrayList<>();
	private BufferedImage[] letters;
	private BufferedImage[] numbers;
	
	/**
	 * Initializer of TextRenderer
	 * @param letters			BufferedImages for each letter
	 */
	public TextRenderer() {
		letters = Main.loadSpriteSheet(7, 26, "assets/letters.png"); //Loads sprite sheet of letters into image array
		numbers = Main.loadSpriteSheet(7, 10, "assets/numbers.png"); //Loads sprite sheet of letters into image array
	}
	
	/**
	 * Adds message onto the screen
	 * @param text			Text to add
	 * @param x				X position
	 * @param y				Y position
	 * @param xSize			message letter width
	 * @param ySize			message letter height
	 * @param spacing		message letter spacing
	 * @return				Text object added
	 */
	public Text addMessage(String text, int x, int y, int xSize, int ySize, int spacing) {
		Text t = new Text(text, x, y, xSize, ySize, spacing);
		boolean found = false;
		for(Text t1 : texts) {
			found = found || t1.equals(t);
		}
		if(!found) {
			texts.add(t);
		}
		return t;
	}
	
	/**
	 * Adds a text onto the screen
	 * @param text			Text object
	 * @return				Text object added
	 */
	public Text addMessage(Text text) {
		texts.add(text);
		return text;
	}
	
	/**
	 * Removes message from screen
	 * @param text			Text to remove
	 * @param x				X position of text
	 * @param y				Y position of text
	 * @param xSize			Text letter width
	 * @param ySize			Text letter height
	 * @param spacing		Text letter spacing
	 */
	public void removeMessage(String text, int x, int y, int xSize, int ySize, int spacing) {
		Text t1 = new Text(text, x, y, xSize, ySize, spacing);
		for(Text t : texts) {
			if(t.equals(t1)) {
				texts.remove(t);
				return;
			}
		}
	}
	
	/**
	 * Removes message from screen
	 * @param text			Message to remove
	 */
	public void removeMessage(Text text) {
		texts.remove(text);
	}
	
	/**
	 * Renders all the text
	 * @param g				Graphics2D context
	 */
	public void render(Graphics2D g) {
		for(Text t : texts) {
			for(int i = 0; i < t.text.length(); i++) {
				char c = t.text.charAt(i);
				BufferedImage sprite;
				if(c - 'A' >= 0) {
					sprite = letters[c - 'A'];
				}else {
					sprite = numbers[c - '0'];
				}
				g.drawImage(
					sprite,
					i * (t.xSize + t.spacing) + t.x,
					t.y,
					t.xSize,
					t.ySize,
					null
				);
			}
		}
	}
}
