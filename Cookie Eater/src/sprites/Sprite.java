package sprites;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import ce3.*;

public abstract class Sprite {
	
	protected Board board;
	protected int x, y;
	protected ArrayList<Image> imgs; //images to reference
	protected int frameCount;
	protected Color[][] palettes; //list of color palettes (others swap out first row)
	
	public Sprite(Board frame) {
		board = frame;
		imgs = new ArrayList<Image>();
		frameCount=0;
	}
	
	//read in color palette from an image file
	public void readColors(BufferedImage i) {
		palettes = new Color[i.getHeight(null)][i.getWidth(null)];
		for(int r=0; r<palettes.length; r++) {
			for(int c=0; c<palettes[r].length; c++) {
				palettes[r][c] = new Color(i.getRGB(c,r),true);
			}
		}
	}
	
	//converts image between color palettes
	public BufferedImage convertPalette(BufferedImage img, int oldIndex, int newIndex) {
		for(int x=0; x<img.getWidth(null); x++) {
			for(int y=0; y<img.getHeight(null); y++) {
				int c = img.getRGB(x,y);
				for(int p=0; p<palettes[oldIndex].length; p++) {
					if(palettes[oldIndex][p].getRGB() == c) {
						img.setRGB(x,y,palettes[newIndex][p].getRGB());
					}
				}
			}
		}
		return img;
	}
	
	//graphics level of draw
	protected boolean graphicsLevel() {return board.game.draw.getGraphicsLevel();}
	
	public void paint(Graphics g) {
		
	}
}
