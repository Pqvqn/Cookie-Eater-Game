package sprites;

import java.awt.*;
import java.awt.geom.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;

import ce3.*;

public class SpriteLevel extends Sprite{

	private ArrayList<Wall> walls;
	private Image base;
	private int wid,hei;
	
	public SpriteLevel(Board frame, ArrayList<Wall> w) throws IOException {
		super(frame);
		walls = w;
		base = ImageIO.read(new File("Cookie Eater/src/resources/level/grad23.png"));
		imgs.add(base);
	}
	public void updateWalls(ArrayList<Wall> w) {
		walls = w;
		
	}
	public void prePaint() throws IOException {
		
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
		Area wallSpace = new Area();
		if(walls==null)return;
		for(Wall w : walls) {
			x=w.getX();
			y=w.getY();
			wid=w.getW();
			hei=w.getH();
			wallSpace.add(new Area(new Rectangle(x,y,wid,hei)));
		}
		g.setClip(wallSpace);
		g.drawImage(base,0,0,board.X_RESOL,board.Y_RESOL,null);
		g.setClip(null);
	}
}
