package sprites;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;

import ce3.*;

public class SpriteLevel extends Sprite{

	private ArrayList<Wall> wallList;
	private Image wall;
	private Image floor;
	private int wid,hei;
	private String lvl;
	
	public SpriteLevel(Board frame, ArrayList<Wall> w) throws IOException {
		super(frame);
		wallList = w;
		//wall = ImageIO.read(new File("Cookie Eater/src/resources/level/grad23.png"));
		//floor = ImageIO.read(new File("Cookie Eater/src/resources/level/grad23.png"));
		imgs.add(wall);
		lvl = "";
	}
	public void updateWalls(ArrayList<Wall> w) {
		wallList = w;
		
	}
	public String removeSpace(String s) {
		String ret = "";
		for(int i=0; i<s.length(); i++) {
			if(!s.substring(i,i+1).equals(" "))
				ret+=s.substring(i,i+1);
		}
		return ret;
	}
	public void prePaint() throws IOException {
		if(!lvl.equals(removeSpace(board.currFloor.getName()))){
			lvl = removeSpace(board.currFloor.getName());
			BufferedImage newWall = new BufferedImage(board.X_RESOL,board.Y_RESOL,BufferedImage.TYPE_INT_ARGB);
			Graphics2D newg = newWall.createGraphics();
			Image tile = ImageIO.read(new File("Cookie Eater/src/resources/level/walltile.png"));
			int wid = (int)(.5+tile.getWidth(null)*board.currFloor.getScale()),hei = (int)(.5+tile.getHeight(null)*board.currFloor.getScale());
			int xOffset = (int)(.5+Math.random()*wid),yOffset = (int)(.5+Math.random()*hei);
			for(int xl=0;xl<(int)(2+board.X_RESOL/wid);xl++) {
				for(int yl=0;yl<(int)(2+board.Y_RESOL/hei);yl++) {
					newg.drawImage(tile, xl*wid-xOffset, yl*hei-yOffset, wid, hei, null);
				}
			}
			newg.dispose();
			wall = newWall;
			floor = ImageIO.read(new File("Cookie Eater/src/resources/level/"+lvl+"FLOOR.png"));
		}
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
		//floor
		g.drawImage(floor,0,0,board.X_RESOL,board.Y_RESOL,null);
		//walls
		Area wallSpace = new Area();
		if(wallList==null)return;
		for(Wall w : wallList) {
			x=w.getX();
			y=w.getY();
			wid=w.getW();
			hei=w.getH();
			wallSpace.add(new Area(new Rectangle(x,y,wid,hei)));
		}
		g.setClip(wallSpace);
		g.drawImage(wall,0,0,board.X_RESOL,board.Y_RESOL,null);
		g.setClip(null);
	}
}
