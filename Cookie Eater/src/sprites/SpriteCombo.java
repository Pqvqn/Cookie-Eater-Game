package sprites;

import java.util.*;
import java.awt.*;

import ce3.*;

public class SpriteCombo extends Sprite {

	private Image img;
	private ArrayList<Sprite> parts;
	
	public SpriteCombo(Board frame, ArrayList<Sprite> subsprites){
		super(frame);
		parts = subsprites;
	}
	
	public void render() {
		Graphics g = img.getGraphics();
		for(int i=0; i<parts.size(); i++) {
			parts.get(i).paint(g);
		}
	}
	
	public void paint(Graphics g) {
		g.drawImage(img,0,0,null);
	}
	
}
