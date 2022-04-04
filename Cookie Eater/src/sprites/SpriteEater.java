package sprites;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;

import ce3.*;
import cookies.*;
import entities.*;

public class SpriteEater extends Sprite{

	private Eater user;
	
	private Color coloration;
	private double scale;
	private Image base, face, helmet;
	private BufferedImage fin;
	private int fullw, fullh; //pixel size of image
	private double facex,facey; //relative position of face on image

	private File[] faces = {new File("Cookie Eater/src/resources/explorers/eaterFace.png"),
			new File("Cookie Eater/src/resources/explorers/eaterFaceEat.png"),
			new File("Cookie Eater/src/resources/explorers/eaterFaceMonch.png"),
			new File("Cookie Eater/src/resources/explorers/eaterFaceOuch.png"),
			new File("Cookie Eater/src/resources/explorers/eaterFaceWin.png"),
			new File("Cookie Eater/src/resources/explorers/eaterFaceDie.png"),
			new File("Cookie Eater/src/resources/explorers/eaterFaceSpecial.png")};
	private File[] helmets = {new File("Cookie Eater/src/resources/explorers/eaterHelmNeutral.png"),
			new File("Cookie Eater/src/resources/explorers/eaterHelmUp.png"),
			new File("Cookie Eater/src/resources/explorers/eaterHelmRight.png"),
			new File("Cookie Eater/src/resources/explorers/eaterHelmDown.png"),
			new File("Cookie Eater/src/resources/explorers/eaterHelmLeft.png")};

	private File bases[] = {new File("Cookie Eater/src/resources/explorers/eaterBase.png")};
	private int expression;
	private final int NORM=0, EAT=1, HIT=3, WIN=4, DIE=5, SPECIAL=6; //MUNCH = 2
	private final int NEUTRAL=0, UP=1, RIGHT=2, DOWN=3, LEFT=4;
	private BufferedImage[][] imgset;
	
	public SpriteEater(Board frame, Eater e) throws IOException {
		super(frame);
		user = e;
		expression = NORM;
		fullw = 550;
		fullh = 550;
		//imgs.add(base);
		//imgs.add(face);
		//imgs.add(helmet);
		
		//load images and set their palettes
		readColors(ImageIO.read(new File("Cookie Eater/src/resources/explorers/eaterpalette.png")));
		int pid = user.getID();
		
		BufferedImage[] r_faces = new BufferedImage[faces.length];
		for(int i=0; i<faces.length; i++)r_faces[i] = convertPalette(ImageIO.read(faces[i]),0,pid);
		BufferedImage[] r_bases = new BufferedImage[bases.length];
		for(int i=0; i<bases.length; i++)r_bases[i] = convertPalette(ImageIO.read(bases[i]),0,pid);
		BufferedImage[] r_helmets = new BufferedImage[helmets.length];
		for(int i=0; i<helmets.length; i++)r_helmets[i] = convertPalette(ImageIO.read(helmets[i]),0,pid);
		
		imgset = new BufferedImage[faces.length][5];
		
		//render all possible images and store them
		for(int ie=0; ie<faces.length; ie++) {
			for(int id=-1; id<4; id++) {
				BufferedImage imgcombo = new BufferedImage(fullw,fullh,BufferedImage.TYPE_INT_ARGB); //the image composited onto
				facex = fullw/2; 
				facey = fullh/2;
				switch(id) {
				case Eater.UP:
					facey /= 2;
					helmet = r_helmets[UP];
					break;
				case Eater.DOWN:
					facey *= 1.5;
					helmet = r_helmets[DOWN];
					break;
				case Eater.RIGHT:
					facex *= 1.5;
					helmet = r_helmets[RIGHT];
					break;
				case Eater.LEFT:
					facex /= 2;
					helmet = r_helmets[LEFT];
					break;
				case Eater.NONE:
					helmet = r_helmets[NEUTRAL];
					break;
				case Eater.CORPSE:
					helmet = r_helmets[NEUTRAL];
					break;
				}
				base = r_bases[0];
				face = r_faces[ie];
				
				Graphics compiled = imgcombo.getGraphics();
				compiled.drawImage(face,(int)(.5+facex-(face.getWidth(null)/2)), (int)(.5+facey-(face.getHeight(null)/2)), null);
				compiled.drawImage(base,(fullw-base.getWidth(null))/2,(fullh-base.getHeight(null))/2,null);
				compiled.drawImage(helmet,(fullw-helmet.getWidth(null))/2,(fullh-helmet.getHeight(null))/2,null);
				imgset[ie][id+1] = imgcombo;
				
			}
		}
	}
	public void setColor(Color c) {coloration = c;}
	public void prePaint() throws IOException {
		//scale = board.currFloor.getScale();
		scale = (double)user.getRadius()/Eater.DEFAULT_RADIUS;
		x = (int)(.5+user.getX());
		y = (int)(.5+user.getY());
		
		expression = NORM;
		
		ArrayList<Cookie> nearcs = user.nearCookies();
		if(nearcs!=null && !nearcs.isEmpty())
			expression = EAT;
		if(user.getShielded())
			expression = HIT;
		if(user.getSpecialActivated()) {
			expression = SPECIAL;
		}
		
		switch(user.getState()) {
		case Eater.WIN:
			expression = WIN;
			break;
		case Eater.DEAD:
			expression = DIE;
			break;
		}
		fin = imgset[expression][(user.getDir()<-1)?0:user.getDir()+1]; //choose correct pre-rendered image
	}
	public void paint(Graphics g) {
		try {
			prePaint();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int state = user.getState();
		double radius = user.getRadius();
		if(state==Eater.DEAD) {
			g.setColor(new Color(0,0,0,100));
			g.fillOval((int)(.5+x-2*radius), (int)(.5+y-2*radius), (int)(.5+4*radius), (int)(.5+4*radius));
		}else if(state==Eater.WIN) {
			g.setColor(new Color(255,255,255,100));
			g.fillOval((int)(.5+x-2*radius), (int)(.5+y-2*radius), (int)(.5+4*radius), (int)(.5+4*radius));
		}else if(user.getCurrentSpecial()!=-1) {
			Color meh = user.getSpecialColors().get(user.getCurrentSpecial());
			g.setColor(new Color(meh.getRed(),meh.getGreen(),meh.getBlue(),100));
			g.fillOval((int)(.5+x-1.5*radius), (int)(.5+y-1.5*radius), (int)(.5+3*radius), (int)(.5+3*radius));
		}
		if(user.getShielded()) { //invert color if shielded
			g.setColor(new Color(255-coloration.getRed(),255-coloration.getGreen(),255-coloration.getBlue()));
		}else {
			g.setColor(coloration);
		}
		if(user.getGhosted()) {
			g.setColor(new Color(g.getColor().getRed(),g.getColor().getGreen(),g.getColor().getBlue(),100));
		}
		g.fillOval((int)(.5+x-radius), (int)(.5+y-radius), (int)(.5+2*radius), (int)(.5+2*radius));
		//images
		g.drawImage(fin,(int)(.5+x-(fin.getWidth(null)/10*scale)), (int)(.5+y-(fin.getHeight(null)/10*scale)),(int)(.5+(fin.getWidth(null)/5*scale)),(int)(.5+(fin.getHeight(null)/5*scale)),null);
		
		/*
		g.drawImage(face,(int)(.5+facex-(face.getWidth(null)/10*scale)), (int)(.5+facey-(face.getHeight(null)/10*scale)), (int)(2*(.5+face.getWidth(null)/10*scale)), (int)(2*(.5+face.getHeight(null)/10*scale)), null);
		g.drawImage(helmet,(int)(.5+x-(helmet.getWidth(null)/10*scale)), (int)(.5+y-(helmet.getHeight(null)/10*scale)), (int)(2*(.5+helmet.getWidth(null)/10*scale)), (int)(2*(.5+helmet.getHeight(null)/10*scale)), null);
		*/
	}
}
