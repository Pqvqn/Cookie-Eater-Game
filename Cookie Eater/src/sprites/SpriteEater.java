package sprites;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.io.IOException;

import javax.imageio.ImageIO;

import ce3.*;
import entities.*;

public class SpriteEater extends Sprite{

	private Eater user;
	private Image base;
	private Image face;
	private Image helmet;
	private Color coloration;
	private double facex,facey; //relative position of face on image
	private double scale;
	private BufferedImage fin;
	private int fullw, fullh; //pixel size of image
	private final int NORM=0, EAT=1, HIT=3, WIN=4, DIE=5, SPECIAL=6; //MUNCH = 2
	private File[] expressions = {new File("Cookie Eater/src/resources/explorers/eaterFace.png"),
			new File("Cookie Eater/src/resources/explorers/eaterFaceEat.png"),
			new File("Cookie Eater/src/resources/explorers/eaterFaceMonch.png"),
			new File("Cookie Eater/src/resources/explorers/eaterFaceOuch.png"),
			new File("Cookie Eater/src/resources/explorers/eaterFaceWin.png"),
			new File("Cookie Eater/src/resources/explorers/eaterFaceDie.png"),
			new File("Cookie Eater/src/resources/explorers/eaterFaceSpecial.png")};
	private File[] helmets = {new File("Cookie Eater/src/resources/explorers/eaterHelmNeutral.png"),
			new File("Cookie Eater/src/resources/explorers/eaterHelmUp.png"),
			new File("Cookie Eater/src/resources/explorers/eaterHelmDown.png"),
			new File("Cookie Eater/src/resources/explorers/eaterHelmLeft.png"),
			new File("Cookie Eater/src/resources/explorers/eaterHelmRight.png")};
	private File bases;
	private int expression;
	private final int NEUTRAL=0, UP=1, DOWN=2, LEFT=3, RIGHT=4;
	
	public SpriteEater(Board frame, Eater e) throws IOException {
		super(frame);
		user = e;
		bases = new File("Cookie Eater/src/resources/explorers/eaterBase.png");
		base = ImageIO.read(bases);
		expression = NORM;
		face = ImageIO.read(expressions[expression]);
		helmet = ImageIO.read(helmets[NEUTRAL]);
		fullw = 550;
		fullh = 550;
		imgs.add(base);
		imgs.add(face);
		imgs.add(helmet);
	}
	public void setColor(Color c) {coloration = c;}
	public void prePaint() throws IOException {
		fin = new BufferedImage(fullw,fullh,BufferedImage.TYPE_INT_ARGB); //the image composited onto
		//scale = board.currFloor.getScale();
		scale = (double)user.getRadius()/Eater.DEFAULT_RADIUS;
		x = (int)(.5+user.getX());
		y = (int)(.5+user.getY());
		
		facex = fullw/2; 
		facey = fullh/2;
		switch(user.getDir()) {
		case Eater.UP:
			//facey-=user.getRadius()/2;
			facey /= 2;
			helmet = ImageIO.read(helmets[UP]);
			break;
		case Eater.DOWN:
			//facey+=user.getRadius()/2;
			facey *= 1.5;
			helmet = ImageIO.read(helmets[DOWN]);
			break;
		case Eater.RIGHT:
			//facex+=user.getRadius()/2;
			facex *= 1.5;
			helmet = ImageIO.read(helmets[RIGHT]);
			break;
		case Eater.LEFT:
			//facex-=user.getRadius()/2;
			facex /= 2;
			helmet = ImageIO.read(helmets[LEFT]);
			break;
		case Eater.NONE:
			helmet = ImageIO.read(helmets[NEUTRAL]);
			break;
		}
			
		
		expression = NORM;
		
		if(user.getNearCookie())
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
		
		
		base = ImageIO.read(bases);
		face = ImageIO.read(expressions[expression]);
		Graphics compiled =  fin.getGraphics();
		compiled.drawImage(face,(int)(.5+facex-(face.getWidth(null)/2)), (int)(.5+facey-(face.getHeight(null)/2)), null);
		compiled.drawImage(base,(fullw-base.getWidth(null))/2,(fullh-base.getHeight(null))/2,null);
		compiled.drawImage(helmet,(fullw-helmet.getWidth(null))/2,(fullh-helmet.getHeight(null))/2,null);
	}
	public void paint(Graphics g) {
		super.paint(g);
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
