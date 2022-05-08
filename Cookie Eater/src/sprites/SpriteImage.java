package sprites;

import java.awt.*;
import java.awt.geom.*;

import ce3.*;

public class SpriteImage extends Sprite{
	
	Image img;
	Area clip;
	
	public SpriteImage(Board frame) {
		super(frame);
	}
	
	public void setImg(Image newimg) {
		img = newimg;
	}
	
	public void setClip(Area c) {
		// set clip area to just area of chunk
		clip = new Area(c);
		Rectangle r = new Rectangle(x,y,img.getWidth(null),img.getHeight(null));
		clip.intersect(new Area(r));
		
		// move wall area to fit chunk
		AffineTransform t = new AffineTransform();
		t.setToTranslation(-x, -y);
		clip.transform(t);
	}
	
	public void setPos(int xp, int yp) {
		x=xp;
		y=yp;
	}
	
	public boolean hasImage() {return img!=null;}
	
	public void paint(Graphics g, int xo, int yo) {
		if(clip!=null)g.setClip(clip);
		if(img!=null)g.drawImage(img,x+xo,y+yo,null);
		if(clip!=null)g.setClip(null);
	}
}
