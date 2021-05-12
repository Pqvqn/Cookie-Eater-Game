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
			for(int c=0; c<palettes[0].length; c++) {
				palettes[r][c] = new Color(i.getRGB(r,c));
			}
		}
	}
	
	//converts image between color palettes
	public BufferedImage convertPalette(BufferedImage img, int oldIndex, int newIndex) {
		for(int x=0; x<img.getWidth(null); x++) {
			for(int y=0; y<img.getHeight(null); y++) {
				for(int p=0; p<palettes[oldIndex].length; p++) {
					int c = img.getRGB(x,y);
					if(palettes[oldIndex][p].getRGB() == c) {
						img.setRGB(x,y,palettes[newIndex][0].getRGB());
					}
				}
			}
		}
		return img;
	}
	
	public void paint(Graphics g) {
		
	}
}
