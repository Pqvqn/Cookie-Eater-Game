package sprites;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.*;

import ce3.*;
import cookies.*;

public class SpriteCookie extends Sprite{

	private Cookie user;
	private Image base, chip;
	private BufferedImage finimg;
	private double scale;
	private File defBase, splBase, defChip, splChip;
	private int state;
	private final int REG=0, SPOILED=-1, ITEM=1;
	private int baseNum, chipNum;
	private final int TOTALIMGS = 4;
	
	public SpriteCookie(Board frame, Cookie c) throws IOException {
		this(frame,c,-1);
	}
	
	public SpriteCookie(Board frame, Cookie c, int palette) throws IOException {
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

		finimg = new BufferedImage(base.getWidth(null),base.getHeight(null),BufferedImage.TYPE_INT_ARGB);
		Graphics compiled = finimg.getGraphics();
		compiled.drawImage(base,0,0,null);
		compiled.drawImage(chip,0,0,null);
		
		if(palette>=0) {
			readColors(ImageIO.read(new File("Cookie Eater/src/resources/cookies/itempalettes.png")));
			//base = convertPalette((BufferedImage)base,0,palette);
			//chip = convertPalette((BufferedImage)chip,0,palette);
			finimg = convertPalette((BufferedImage)finimg,0,palette);
			state = ITEM;
		}
		
		imgs.add(base);
		imgs.add(chip);
	}
	public void prePaint() throws IOException {
		if(user.getDecayed()&&state==REG) {
			base = ImageIO.read(splBase);
			chip = ImageIO.read(splChip);
			finimg = new BufferedImage(base.getWidth(null),base.getHeight(null),BufferedImage.TYPE_INT_ARGB);
			Graphics compiled = finimg.getGraphics();
			compiled.drawImage(base,0,0,null);
			compiled.drawImage(chip,0,0,null);
			state = SPOILED;
		}else if(!user.getDecayed()&&state==SPOILED) {
			base = ImageIO.read(defBase);
			chip = ImageIO.read(defChip);
			finimg = new BufferedImage(base.getWidth(null),base.getHeight(null),BufferedImage.TYPE_INT_ARGB);
			Graphics compiled = finimg.getGraphics();
			compiled.drawImage(base,0,0,null);
			compiled.drawImage(chip,0,0,null);
			state = REG;
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
		//g.drawImage(base,(int)(.5+x-(base.getWidth(null)/10*scale)), (int)(.5+y-(base.getHeight(null)/10*scale)), (int)(2*(.5+base.getWidth(null)/10*scale)), (int)(2*(.5+base.getHeight(null)/10*scale)), null);
		//g.drawImage(chip,(int)(.5+x-(chip.getWidth(null)/10*scale)), (int)(.5+y-(chip.getHeight(null)/10*scale)), (int)(2*(.5+chip.getWidth(null)/10*scale)), (int)(2*(.5+chip.getHeight(null)/10*scale)), null);
		g.drawImage(finimg,(int)(.5+x-(finimg.getWidth(null)/10*scale)), (int)(.5+y-(finimg.getHeight(null)/10*scale)), (int)(2*(.5+finimg.getWidth(null)/10*scale)), (int)(2*(.5+finimg.getHeight(null)/10*scale)), null);

	}
}
