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
	
	public void setPos(int xp, int yp) {
		x=xp;
		y=yp;
	}
	
	public void paint(Graphics g, int xo, int yo) {
		if(img!=null)g.drawImage(img,x+xo,y+yo,null);
	}
}
