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
	
	public void render() {
		img = new BufferedImage(board.x_resol,board.y_resol,BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		for(int i=0; i<parts.size(); i++) {
			parts.get(i).paint(g);
		}
	}
	
	public void paint(Graphics g) {
		g.drawImage(img,0,0,null);
	}
	
}
