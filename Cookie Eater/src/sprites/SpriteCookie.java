package sprites;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;

import javax.imageio.*;
import javax.swing.*;

import ce3.*;
import cookies.*;

public class SpriteCookie extends Sprite{

	private Cookie user;
	//private Image base, chip;
	private Image finimg;
	private double scale;
	private int state;
	private static final int REG=0, SPOILED=1, ITEM=-1;
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
	private static Image[][][] sprites;
	
	public static void makeSprites() {
		sprites = new BufferedImage[3][defBases.length][defChips.length];
		// generate all possible sprites ahead of time
		for(int b=0; b<defBases.length; b++) {
			Image base = null;
			try {
				base = ImageIO.read(defBases[b]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int c=0; c<defChips.length; c++) {
				Image chip = null;
				try {
					chip = ImageIO.read(defChips[b]);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Image fNorm = new BufferedImage(base.getWidth(null),base.getHeight(null),BufferedImage.TYPE_INT_ARGB);
				Image fClear = new BufferedImage(base.getWidth(null),base.getHeight(null),BufferedImage.TYPE_INT_ARGB);
				Image fGray = new BufferedImage(base.getWidth(null),base.getHeight(null),BufferedImage.TYPE_INT_ARGB);
				Graphics2D cNorm = (Graphics2D)fNorm.getGraphics();
				Graphics2D cClear = (Graphics2D)fClear.getGraphics();
				Graphics2D cGray = (Graphics2D)fGray.getGraphics();
				cNorm.drawImage(base,0,0,null);
				cNorm.drawImage(chip,0,0,null);
				Image omg = GrayFilter.createDisabledImage(fGray);
				cGray.drawImage(omg,0,0,null);
				cClear.setComposite(AlphaComposite.SrcOver.derive(0.5f));
				cClear.setComposite(AlphaComposite.SrcOver.derive(0.5f));
				cClear.drawImage(base,0,0,null);
				cClear.drawImage(chip,0,0,null);
				
				sprites[REG][b][c] = fNorm;
				sprites[SPOILED][b][c] = fClear;
				sprites[SPOILED+1][b][c] = fGray;
			}
		}
	}
	   
	public SpriteCookie(Board frame, Cookie c) throws IOException {
		this(frame,c,-1);
	}
	
	public SpriteCookie(Board frame, Cookie c, int cpalette) throws IOException {
		super(frame);

		if(SpriteCookie.sprites==null)SpriteCookie.makeSprites();
		
		user = c;
		graphicsLevel = true;
		baseNum = (int)((Math.random()*defBases.length)+1);
		chipNum = (int)((Math.random()*defChips.length)+1);
		//base = ImageIO.read(defBases[baseNum-1]);
		//chip = ImageIO.read(defChips[chipNum-1]);
		finimg = SpriteCookie.sprites[REG][baseNum-1][chipNum-1];
		state = REG;

		palette = cpalette;
		if(palette>=0) {
			readColors(ImageIO.read(paletteFile));
			finimg = convertPalette((BufferedImage)finimg,0,palette);
			//base = convertPalette((BufferedImage)base,0,palette);
			//chip = convertPalette((BufferedImage)chip,0,palette);
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
			graphicsLevel = graphicsLevel();
			int gstate = (state==SPOILED && !graphicsLevel)?SPOILED+1:state;
			finimg = SpriteCookie.sprites[gstate][baseNum][chipNum];
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
