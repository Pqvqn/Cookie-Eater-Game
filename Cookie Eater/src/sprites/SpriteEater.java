package sprites;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ce3.*;

public class SpriteEater extends Sprite{

	private Eater user;
	private Image base;
	private Image face;
	private Color coloration;
	private double facex,facey;
	private double scale;
	private final int NORM=0, EAT=1, HIT=3, WIN=4, DIE=5, SPECIAL=6; //MUNCH = 2
	private File[] expressions = {new File("Cookie Eater/src/resources/eaterFace.png"),
			new File("Cookie Eater/src/resources/eaterFaceEat.png"),
			new File("Cookie Eater/src/resources/eaterFaceMonch.png"),
			new File("Cookie Eater/src/resources/eaterFaceOuch.png"),
			new File("Cookie Eater/src/resources/eaterFaceWin.png"),
			new File("Cookie Eater/src/resources/eaterFaceDie.png"),
			new File("Cookie Eater/src/resources/eaterFaceSpecial.png")};
	private int expression;
	
	public SpriteEater(Board frame, Eater e) throws IOException {
		super(frame);
		user = e;
		base = ImageIO.read(new File("Cookie Eater/src/resources/eaterBase.png"));
		expression = NORM;
		face = ImageIO.read(expressions[expression]);
		imgs.add(base);
		imgs.add(face);
	}
	public void setColor(Color c) {coloration = c;}
	public void prePaint() throws IOException {
		
		//scale = board.currFloor.getScale();
		scale = (double)user.getRadius()/Eater.DEFAULT_RADIUS;
		x = (int)(.5+user.getX());
		y = (int)(.5+user.getY());
		
		facex = x; 
		facey = y;
		switch(user.getDir()) {
		case Eater.UP:
			facey-=user.getRadius()/2;
			break;
		case Eater.DOWN:
			facey+=user.getRadius()/2;
			break;
		case Eater.RIGHT:
			facex+=user.getRadius()/2;
			break;
		case Eater.LEFT:
			facex-=user.getRadius()/2;
			break;
		case Eater.NONE:
			break;
		}
			
		
		expression = NORM;
		
		if(user.getNearCookie())
			expression = EAT;
		if(user.getShielded())
			expression = HIT;
		
		switch(user.getState()) {
		case Eater.WIN:
			expression = WIN;
			break;
		case Eater.DEAD:
			expression = DIE;
			break;
		case Eater.SPECIAL:
			expression = SPECIAL;
			break;
		}
		
		
		
		face = ImageIO.read(expressions[expression]);
		
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
		g.drawImage(base,(int)(.5+x-(base.getWidth(null)/10*scale)), (int)(.5+y-(base.getHeight(null)/10*scale)), (int)(2*(.5+base.getWidth(null)/10*scale)), (int)(2*(.5+base.getHeight(null)/10*scale)), null);
		
		
		g.drawImage(face,(int)(.5+facex-(face.getWidth(null)/10*scale)), (int)(.5+facey-(face.getHeight(null)/10*scale)), (int)(2*(.5+face.getWidth(null)/10*scale)), (int)(2*(.5+face.getHeight(null)/10*scale)), null);
	}
}
