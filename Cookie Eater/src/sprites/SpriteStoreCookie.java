package sprites;

import java.awt.*;
import java.io.*;

import javax.imageio.*;

import ce3.*;
import cookies.*;

public class SpriteStoreCookie extends Sprite{

	private Cookie user;
	private Image img;
	private double scale;
	private File file;
	
	public SpriteStoreCookie(Board frame, Cookie c, String fileName) throws IOException {
		super(frame);
		user = c;
		file = new File("Cookie Eater/src/resources/cookies/"+fileName+".png");
		img = ImageIO.read(file);
		imgs.add(img);
	}
	
	public void paint(Graphics g){
	
		scale = board.currLevel.getScale();
		x = user.getX();
		y = user.getY();
		g.drawImage(img,(int)(.5+x-(img.getWidth(null)/10*scale)), (int)(.5+y-(img.getHeight(null)/10*scale)), (int)(2*(.5+img.getWidth(null)/10*scale)), (int)(2*(.5+img.getHeight(null)/10*scale)), null);

	}
}
