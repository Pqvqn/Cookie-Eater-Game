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
	private int state;
	private final int REG=0, SPOILED=-1, ITEM=1;
	private int baseNum, chipNum;
	private boolean graphicsLevel;
	private int palette;
	private static final File defBases[] = {new File("Cookie Eater/src/resources/cookies/cookieBN1.png"),
			new File("Cookie Eater/src/resources/cookies/cookieBN2.png"),
			new File("Cookie Eater/src/resources/cookies/cookieBN3.png"),
			new File("Cookie Eater/src/resources/cookies/cookieBN4.png")};
	private static final File defChips[] = {new File("Cookie Eater/src/resources/cookies/cookieCN1.png"),
			new File("Cookie Eater/src/resources/cookies/cookieCN2.png"),
			new File("Cookie Eater/src/resources/cookies/cookieCN3.png"),
			new File("Cookie Eater/src/resources/cookies/cookieCN4.png")};
	private static final File paletteFile = new File("Cookie Eater/src/resources/cookies/itempalettes.png");
	
	public SpriteCookie(Board frame, Cookie c) throws IOException {
		this(frame,c,-1);
	}
	
	public SpriteCookie(Board frame, Cookie c, int cpalette) throws IOException {
		super(frame);
		user = c;
		graphicsLevel = true;
		baseNum = (int)((Math.random()*defBases.length)+1);
		chipNum = (int)((Math.random()*defChips.length)+1);
		base = ImageIO.read(defBases[baseNum-1]);
		chip = ImageIO.read(defChips[chipNum-1]);
		state = REG;

		palette = cpalette;
		if(palette>=0) {
			readColors(ImageIO.read(paletteFile));
			base = convertPalette((BufferedImage)base,0,palette);
			chip = convertPalette((BufferedImage)chip,0,palette);
			state = ITEM;
		}
		//imgs.add(base);
		//imgs.add(chip);
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
					compiled.drawImage(base,0,0,null);
					compiled.drawImage(chip,0,0,null);
					Image omg = GrayFilter.createDisabledImage(finimg);
					compiled.drawImage(omg,0,0,null);
					compiled.setComposite(AlphaComposite.SrcOver.derive(0.5f));
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
		//if(Math.sqrt(Math.pow(user.getX()-board.player().getX(),2)+Math.pow(user.getY()-board.player().getY(),2)) > board.renderDist)
		//	return;
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
