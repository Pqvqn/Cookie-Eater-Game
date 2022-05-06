package sprites;

import java.util.*;
import java.awt.*;
import java.awt.image.*;

import ce3.*;

public class SpriteCombo extends SpriteImage {

	private ArrayList<Sprite> parts;
	boolean rendered = false;
	int[] resol;
	
	public SpriteCombo(Board frame, ArrayList<Sprite> subsprites, int xres, int yres){
		super(frame);
		parts = subsprites;
		resol = new int[] {xres, yres};
	}
	
	public void setParts(ArrayList<Sprite> p) {parts = p;}
	public ArrayList<Sprite> getParts() {return parts;}
	
	//create single image by painting subelements, alpha determines if transparency is allowed
	public void render(boolean alpha) {
		setImg(new BufferedImage(resol[0],resol[1],(alpha)?BufferedImage.TYPE_INT_ARGB:BufferedImage.TYPE_INT_RGB));
		Graphics g = img.getGraphics();
		for(int i=0; i<parts.size(); i++) {
			addSprite(g, parts.get(i), false);
		}
		rendered = true;
	}
	
	public boolean rendered() {return rendered;}
	
	public void addSprite(Sprite s) {
		addSprite(img==null?null:img.getGraphics(), s, true);
	}
	
	public void addSprite(Sprite s, boolean addPart) {
		addSprite(img==null?null:img.getGraphics(), s, addPart);
	}
	
	public void addSprite(Graphics g, Sprite s, boolean addPart) {
		if(g!=null)s.paint(g,-x,-y);
		if(addPart)parts.add(s);
	}
	
	public void removeSprite(Sprite s) {
		parts.remove(s);
	}
	
	public void deletePortion(int x, int y, int w, int h) {
		int transparent = new Color(0, 0, 0, 0).getRGB();
		for(int ix=x; ix<x+w; ix++) {
			for(int iy=y; iy<y+h; iy++) {
				((BufferedImage) img).setRGB(ix, iy, transparent);
			}
		}
	}
	
	public void paint(Graphics g, int xo, int yo) {
		super.paint(g,xo,yo);
	}
	
}
