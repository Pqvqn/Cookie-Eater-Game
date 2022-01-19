package sprites;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;
import javax.swing.*;

import ce3.*;
import cookies.*;

public class SpriteCookie extends Sprite{

	private Cookie user;
	private Image base, chip;
	private Image finimg;
	private double scale;
	private File defBase, defChip; //, splBase, splChip;
	private int state;
	private final int REG=0, SPOILED=-1, ITEM=1;
	private int baseNum, chipNum;
	private final int TOTALIMGS = 4;
	private boolean graphicsLevel;
	private int palette;
	
	public SpriteCookie(Board frame, Cookie c) throws IOException {
		this(frame,c,-1);
	}
	
	public SpriteCookie(Board frame, Cookie c, int cpalette) throws IOException {
		super(frame);
		user = c;
		graphicsLevel = true;
		baseNum = (int)((Math.random()*TOTALIMGS)+1);
		chipNum = (int)((Math.random()*TOTALIMGS)+1);
		defBase = new File("Cookie Eater/src/resources/cookies/cookieBN"+baseNum+".png");
		//splBase = new File("Cookie Eater/src/resources/cookies/cookieBS"+baseNum+".png");
		defChip = new File("Cookie Eater/src/resources/cookies/cookieCN"+chipNum+".png");
		//splChip = new File("Cookie Eater/src/resources/cookies/cookieCS"+chipNum+".png");
		base = ImageIO.read(defBase);
		chip = ImageIO.read(defChip);
		state = REG;

		/*finimg = new BufferedImage(base.getWidth(null),base.getHeight(null),BufferedImage.TYPE_INT_ARGB);
		Graphics compiled = finimg.getGraphics();
		compiled.drawImage(base,0,0,null);
		compiled.drawImage(chip,0,0,null);*/
		imgs.add(base);
		imgs.add(chip);
		palette = cpalette;
		if(palette>=0) {
			readColors(ImageIO.read(new File("Cookie Eater/src/resources/cookies/itempalettes.png")));
			base = convertPalette((BufferedImage)base,0,palette);
			chip = convertPalette((BufferedImage)chip,0,palette);
			state = ITEM;
		}
	}
	
	public void prePaint() throws IOException {
		int initstate = state;
		if(user.getDecayed()&&initstate==REG) {
			state = SPOILED;
		}else if(!user.getDecayed()&&initstate==SPOILED) {
			state = REG;
		}
		if(finimg==null || state!=initstate || graphicsLevel!=graphicsLevel()) {
			finimg = new BufferedImage(base.getWidth(null),base.getHeight(null),BufferedImage.TYPE_INT_ARGB);
			Graphics2D compiled = (Graphics2D)finimg.getGraphics();
			//make translucent for spoiled
			if(state==SPOILED) {
				//only do transparent if graphics high
				if(!graphicsLevel()) {
					finimg = GrayFilter.createDisabledImage(finimg);
				}else {
					compiled.setComposite(AlphaComposite.SrcOver.derive(0.5f));
				}
			}
			compiled.drawImage(base,0,0,null);
			compiled.drawImage(chip,0,0,null);
			graphicsLevel = graphicsLevel();
		}
	}
	public void paint(Graphics g){
		try {
			prePaint();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		scale = board.currLevel.getScale();
		x = user.getX();
		y = user.getY();
		//g.drawImage(base,(int)(.5+x-(base.getWidth(null)/10*scale)), (int)(.5+y-(base.getHeight(null)/10*scale)), (int)(2*(.5+base.getWidth(null)/10*scale)), (int)(2*(.5+base.getHeight(null)/10*scale)), null);
		//g.drawImage(chip,(int)(.5+x-(chip.getWidth(null)/10*scale)), (int)(.5+y-(chip.getHeight(null)/10*scale)), (int)(2*(.5+chip.getWidth(null)/10*scale)), (int)(2*(.5+chip.getHeight(null)/10*scale)), null);
		g.drawImage(finimg,(int)(.5+x-(finimg.getWidth(null)/10*scale)), (int)(.5+y-(finimg.getHeight(null)/10*scale)), (int)(2*(.5+finimg.getWidth(null)/10*scale)), (int)(2*(.5+finimg.getHeight(null)/10*scale)), null);

	}
}
