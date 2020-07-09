package sprites;

import java.awt.*;
import java.io.*;

import javax.imageio.ImageIO;

import ce3.*;
import entities.*;

public class SpriteExplorer extends Sprite{

	private Explorer user;
	private String name;
	private Image base;
	private Image face;
	private Image helmet;
	private Color coloration;
	private double facex,facey;
	private double scale;
	private final int NORM=0; //EAT=1, HIT=3, WIN=4, DIE=5, SPECIAL=6; //MUNCH = 2
	private final int NEUTRAL=0, UP=1, DOWN=2, LEFT=3, RIGHT=4;
	private File[] expressions;
	private File[] helmets;
	private int expression;
	
	public SpriteExplorer(Board frame, Explorer e) throws IOException {
		super(frame);
		user = e;
		name = "eater";
		File[] exp = {new File("Cookie Eater/src/resources/explorers/"+name+"Face.png"),
				new File("Cookie Eater/src/resources/explorers/"+name+"FaceEat.png"),
				new File("Cookie Eater/src/resources/explorers/"+name+"FaceMonch.png"),
				new File("Cookie Eater/src/resources/explorers/"+name+"FaceOuch.png"),
				new File("Cookie Eater/src/resources/explorers/"+name+"FaceWin.png"),
				new File("Cookie Eater/src/resources/explorers/"+name+"FaceDie.png"),
				new File("Cookie Eater/src/resources/explorers/"+name+"FaceSpecial.png")};
		base = ImageIO.read(new File("Cookie Eater/src/resources/explorers/"+name+"Base.png"));
		name = user.getName().toLowerCase();
		File[] helm = {new File("Cookie Eater/src/resources/explorers/"+name+"HelmNeutral.png"),
				new File("Cookie Eater/src/resources/explorers/"+name+"HelmUp.png"),
				new File("Cookie Eater/src/resources/explorers/"+name+"HelmDown.png"),
				new File("Cookie Eater/src/resources/explorers/"+name+"HelmLeft.png"),
				new File("Cookie Eater/src/resources/explorers/"+name+"HelmRight.png")};
		expressions = exp;
		helmets = helm;
		expression = NORM;
		face = ImageIO.read(expressions[expression]);
		helmet = ImageIO.read(helmets[NEUTRAL]);
		imgs.add(base);
		imgs.add(face);
		imgs.add(helmet);
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
		case Explorer.UP:
			facey-=user.getRadius()/2;
			helmet = ImageIO.read(helmets[UP]);
			break;
		case Explorer.DOWN:
			facey+=user.getRadius()/2;
			helmet = ImageIO.read(helmets[DOWN]);
			break;
		case Explorer.RIGHT:
			facex+=user.getRadius()/2;
			helmet = ImageIO.read(helmets[RIGHT]);
			break;
		case Explorer.LEFT:
			facex-=user.getRadius()/2;
			helmet = ImageIO.read(helmets[LEFT]);
			break;
		case Explorer.NONE:
			helmet = ImageIO.read(helmets[NEUTRAL]);
			break;
		}
			
		
		expression = NORM;
		
		/*if(user.getNearCookie())
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
		
		
		*/
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
		double radius = user.getRadius();
		if(user.getCurrentSpecial()!=-1) {
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
		g.drawImage(helmet,(int)(.5+x-(helmet.getWidth(null)/10*scale)), (int)(.5+y-(helmet.getHeight(null)/10*scale)), (int)(2*(.5+helmet.getWidth(null)/10*scale)), (int)(2*(.5+helmet.getHeight(null)/10*scale)), null);
	}
}
