package sprites;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

import ce3.*;
import cookies.Cookie;
import entities.*;
public class SpriteExplorer extends Sprite{

	private Explorer user;
	private String name;
	private Image base, face, helmet;
	private Color coloration;
	private double facex,facey; //relative position of face on image
	private double scale;
	private BufferedImage fin;
	private int fullw, fullh; //pixel size of image
	public static final int NORM=0, EAT=1, HIT=3, WIN=4, DIE=5, SPECIAL=6; //MUNCH = 2
	public static final int NEUTRAL=0, UP=1, RIGHT=2, DOWN=3, LEFT=4;
	private File[] faces;
	private File[] helmets;
	private File[] bases;
	private int expression;
	private BufferedImage[][] imgset;
	
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
		File[] bas = {new File("Cookie Eater/src/resources/explorers/"+name+"Base.png")};
		
		name = user.getName().toLowerCase();
		File[] helm = {new File("Cookie Eater/src/resources/explorers/"+name+"HelmNeutral.png"),
				new File("Cookie Eater/src/resources/explorers/"+name+"HelmUp.png"),
				new File("Cookie Eater/src/resources/explorers/"+name+"HelmRight.png"),
				new File("Cookie Eater/src/resources/explorers/"+name+"HelmDown.png"),
				new File("Cookie Eater/src/resources/explorers/"+name+"HelmLeft.png")};

		faces = exp;
		helmets = helm;
		bases = bas;
		expression = NORM;
		fullw = 550;
		fullh = 550;
		//imgs.add(base);
		//imgs.add(face);
		//imgs.add(helmet);
		
		BufferedImage[] r_faces = new BufferedImage[faces.length];
		for(int i=0; i<faces.length; i++)r_faces[i] = ImageIO.read(faces[i]);
		BufferedImage[] r_bases = new BufferedImage[bases.length];
		for(int i=0; i<bases.length; i++)r_bases[i] = ImageIO.read(bases[i]);
		BufferedImage[] r_helmets = new BufferedImage[helmets.length];
		for(int i=0; i<helmets.length; i++)r_helmets[i] = ImageIO.read(helmets[i]);
		
		
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
		
		/*switch(user.getState()) {
		case Eater.WIN:
			expression = WIN;
			break;
		case Eater.DEAD:
			expression = DIE;
			break;
		}*/
		fin = imgset[expression][(user.getDir()<-1)?0:user.getDir()+1]; //choose correct pre-rendered image
		
	}
	public Image getCompiled(int exp, int dir) {
		return imgset[exp][dir];
	}
	public void paint(Graphics g) {
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
		g.drawImage(fin,(int)(.5+x-(fin.getWidth(null)/10*scale)), (int)(.5+y-(fin.getHeight(null)/10*scale)),(int)(.5+(fin.getWidth(null)/5*scale)),(int)(.5+(fin.getHeight(null)/5*scale)),null);
		/*g.drawImage(base,(int)(.5+x-(base.getWidth(null)/10*scale)), (int)(.5+y-(base.getHeight(null)/10*scale)), (int)(2*(.5+base.getWidth(null)/10*scale)), (int)(2*(.5+base.getHeight(null)/10*scale)), null);
		
		
		g.drawImage(face,(int)(.5+facex-(face.getWidth(null)/10*scale)), (int)(.5+facey-(face.getHeight(null)/10*scale)), (int)(2*(.5+face.getWidth(null)/10*scale)), (int)(2*(.5+face.getHeight(null)/10*scale)), null);
		g.drawImage(helmet,(int)(.5+x-(helmet.getWidth(null)/10*scale)), (int)(.5+y-(helmet.getHeight(null)/10*scale)), (int)(2*(.5+helmet.getWidth(null)/10*scale)), (int)(2*(.5+helmet.getHeight(null)/10*scale)), null);
		*/
	}
}
