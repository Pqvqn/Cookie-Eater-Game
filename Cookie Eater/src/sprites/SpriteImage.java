package sprites;

import java.awt.Graphics;
import java.awt.image.*;

import ce3.*;

public class SpriteImage extends Sprite{
	
	BufferedImage img;
	
	public SpriteImage(Board frame) {
		super(frame);
	}
	
	public void setImg(BufferedImage newimg) {
		img = newimg;
	}
	
	public void paint(Graphics g) {
		if(img!=null)g.drawImage(img,x,y,null);
	}
}
