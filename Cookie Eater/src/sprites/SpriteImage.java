package sprites;

import java.awt.*;

import ce3.*;

public class SpriteImage extends Sprite{
	
	Image img;
	
	public SpriteImage(Board frame) {
		super(frame);
	}
	
	public void setImg(Image newimg) {
		img = newimg;
	}
	
	public void paint(Graphics g) {
		if(img!=null)g.drawImage(img,x,y,null);
	}
}
