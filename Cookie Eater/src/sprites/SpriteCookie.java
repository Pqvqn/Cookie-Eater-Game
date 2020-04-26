package sprites;

import java.awt.*;
import java.io.*;

import javax.imageio.*;

import ce3.*;
import cookies.*;

public class SpriteCookie extends Sprite{

	private Cookie user;
	private Image base;
	private double scale;
	private final File def = new File("Cookie Eater/src/resources/cookie.png");
	private final File spoiled = new File("Cookie Eater/src/resources/cookieSpoiled.png");
	private int state;
	private final int REG=0, SPOILED=-1;
	
	public SpriteCookie(Board frame, Cookie c) throws IOException {
		super(frame);
		user = c;
		base = ImageIO.read(def);
		state = REG;
		imgs.add(base);
	}
	public void prePaint() throws IOException {
		if(user.getDecayed()&&state==REG) {
			base = ImageIO.read(spoiled);
			state = SPOILED;
		}
	}
	public void paint(Graphics g){
		super.paint(g);
		try {
			prePaint();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		scale = board.currFloor.getScale();
		x = user.getX();
		y = user.getY();
		g.drawImage(base,(int)(.5+x-(base.getWidth(null)/10*scale)), (int)(.5+y-(base.getHeight(null)/10*scale)), (int)(2*(.5+base.getWidth(null)/10*scale)), (int)(2*(.5+base.getHeight(null)/10*scale)), null);


	}
}
