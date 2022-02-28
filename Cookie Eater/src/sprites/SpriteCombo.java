package sprites;

import java.util.*;
import java.awt.*;
import java.awt.image.*;

import ce3.*;

public class SpriteCombo extends Sprite {

	private Image img;
	private ArrayList<Sprite> parts;
	
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
			parts.get(i).paint(g);
		}
	}
	
	public void paint(Graphics g) {
		g.drawImage(img,0,0,null);
	}
	
}
