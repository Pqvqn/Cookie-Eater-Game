package sprites;

import java.util.*;
import java.awt.*;
import java.io.*;

import javax.imageio.ImageIO;

import ce3.*;
import enemies.*;

public class SpriteEnemy extends Sprite{

	private Segment user;
	private Image base;
	private ArrayList<File> states;
	private int state;
	private double scale;
	
	public SpriteEnemy(Board frame, Segment e, ArrayList<String> imgstates) throws IOException {
		super(frame);
		user = e;
		state = 0;
		states=new ArrayList<File>();
		for(String s : imgstates)states.add(new File("Cookie Eater/src/resources/enemies/"+s+".png"));
		base = ImageIO.read(states.get(state));
		imgs.add(base);
	}
	public void prePaint() throws IOException {
		scale = board.currFloor.getScale();
		base = ImageIO.read(states.get(state));
		x = (int)(.5+user.getCenterX());
		y = (int)(.5+user.getCenterY());		
	}
	public void setImage(int i) {
		state = i;
	}
	public void paint(Graphics g) {
		super.paint(g);
		try {
			prePaint();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//images
		g.drawImage(base,(int)(.5+x-(base.getWidth(null)/10*scale)), (int)(.5+y-(base.getHeight(null)/10*scale)), (int)(2*(.5+base.getWidth(null)/10*scale)), (int)(2*(.5+base.getHeight(null)/10*scale)), null);
	}
}
