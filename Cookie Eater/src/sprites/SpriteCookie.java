package sprites;

import java.awt.*;
import java.io.*;

import javax.imageio.*;

import ce3.*;
import cookies.*;

public class SpriteCookie extends Sprite{

	private Cookie user;
	private Image base, chip;
	private double scale;
	private File defBase, splBase, defChip, splChip;
	private int state;
	private final int REG=0, SPOILED=-1;
	private int baseNum, chipNum;
	private final int TOTALIMGS = 3;
	
	public SpriteCookie(Board frame, Cookie c) throws IOException {
		super(frame);
		user = c;
		baseNum = (int)((Math.random()*TOTALIMGS)+1);
		chipNum = (int)((Math.random()*TOTALIMGS)+1);
		defBase = new File("Cookie Eater/src/resources/cookies/cookieBN"+baseNum+".png");
		splBase = new File("Cookie Eater/src/resources/cookies/cookieBS"+baseNum+".png");
		defChip = new File("Cookie Eater/src/resources/cookies/cookieCN"+chipNum+".png");
		splChip = new File("Cookie Eater/src/resources/cookies/cookieCS"+chipNum+".png");
		base = ImageIO.read(defBase);
		chip = ImageIO.read(defChip);
		state = REG;
		imgs.add(base);
	}
	public void prePaint() throws IOException {
		if(user.getDecayed()&&state==REG) {
			base = ImageIO.read(splBase);
			chip = ImageIO.read(splChip);
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
		g.drawImage(chip,(int)(.5+x-(chip.getWidth(null)/10*scale)), (int)(.5+y-(chip.getHeight(null)/10*scale)), (int)(2*(.5+chip.getWidth(null)/10*scale)), (int)(2*(.5+chip.getHeight(null)/10*scale)), null);

	}
}
