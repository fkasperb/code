import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Main extends JFrame{
	/**
	 * This class handles the creation of the window.
	 */
	private static final long serialVersionUID = 1L;
	private static Game main;
	public static Main window;
	public static final int WIDTH = 800, HEIGHT = 800;
	
	/**
	 * Initializer for the main class
	 */
	public Main() {
		main = new Game();
		addKeyListener(main);
	}
	
	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * Create JFrame (window) and link it to a JPanel (Game)
		 */
		window = new Main();
		Container ctn = window.getContentPane();
		ctn.setLayout(new BorderLayout());
		ctn.add(Main.main,BorderLayout.CENTER);
		
		/*
		 * Sets the dimensions and attributes of the window
		 */
		window.pack();
		window.setSize(WIDTH,HEIGHT);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		window.setLocation(dim.width/2-window.getSize().width/2, dim.height/2-window.getSize().height/2);
				
		window.setTitle("GARBLE");
		window.setDefaultCloseOperation(EXIT_ON_CLOSE);
		window.setResizable(true);
		window.setVisible(true);
		window.setAlwaysOnTop(true);
		window.setFocusable(true);
	}
	
	/**
	 * Loads sprite sheet into an array of BufferedImages
	 * @param resolution		Resolution of the sprite sheet
	 * @param size				Number of sprites in the sprite sheet
	 * @param path				File directory of the sprite sheet
	 * @return					Array of BufferedImages
	 */
	public static BufferedImage[] loadSpriteSheet(int resolution, int size, String path) {
		BufferedImage[] spriteSheet = new BufferedImage[size]; //Creates blank array of images
		try {
			BufferedImage image = ImageIO.read(new File(path)); //Loads the sprite sheet
			int index = 0;
			/*
			 * Seperate each indivdual sprite from sprite sheet
			 */
			for(int i = 0; i < image.getHeight() / resolution; i++) {
				for(int j = 0; j < image.getWidth() / resolution; j++) {
					if(index >= size) { //If looked through all sprites in sprite sheet, exit
						return spriteSheet;
					}
					spriteSheet[index] = image.getSubimage(j * resolution, i * resolution, resolution, resolution); //Fetches a sprite from the sprite sheet
					index++; //Increase number of sprites added
				}
			}
			return spriteSheet;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Close the window
	 */
	public static void close() {
		System.out.println("CLOSING WINDOW...");
		window.setVisible(false);
		window.dispose();
	}
}
