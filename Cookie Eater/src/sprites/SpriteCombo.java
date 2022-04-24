package sprites;

import java.util.*;
import java.awt.*;
import java.awt.image.*;

import ce3.*;

public class SpriteCombo extends Sprite {

	private BufferedImage img;
	private ArrayList<Sprite> parts;
	boolean rendered = false;
	
	public SpriteCombo(Board frame, ArrayList<Sprite> subsprites){
		super(frame);
		parts = subsprites;
	}
	
	public void setParts(ArrayList<Sprite> p) {parts = p;}
	
	//create single image by painting subelements, alpha determines if transparency is allowed
	public void render(boolean alpha) {
		img = new BufferedImage(Board.FRAME_X_RESOL,Board.FRAME_Y_RESOL,(alpha)?BufferedImage.TYPE_INT_ARGB:BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		for(int i=0; i<parts.size(); i++) {
			addSprite(g, parts.get(i),false);
		}
		rendered = true;
	}
	
	public boolean rendered() {return rendered;}
	
	public void addSprite(Sprite s) {
		addSprite(img.getGraphics(), s, true);
	}
	
	public void addSprite(Graphics g, Sprite s, boolean addPart) {
		s.paint(g);
		if(addPart)parts.add(s);
	}
	
	public void removeSprite(Sprite s) {
		parts.remove(s);
	}
	
	public void deletePortion(int x, int y, int w, int h) {
		int transparent = new Color(0, 0, 0, 0).getRGB();
		for(int ix=x; ix<x+w; ix++) {
			for(int iy=y; iy<y+h; iy++) {
				img.setRGB(ix, iy, transparent);
			}
		}
	}
	
	public void paint(Graphics g) {
		g.drawImage(img,0,0,null);
	}
	
}
