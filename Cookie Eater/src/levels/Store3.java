package levels;

import java.util.*;
import java.awt.*;

import ce3.*;
import cookies.*;

public class Store3 extends Floor3{
	
	private ArrayList<String> catalogue;
	private ArrayList<Double> prices;
	private ArrayList<Color> colors;
	
	public Store3(Board frame) {
		super(frame);
		startx = board.X_RESOL/2;
		starty = board.Y_RESOL/2;
		minDecay = Integer.MAX_VALUE;
		maxDecay = Integer.MAX_VALUE;
		catalogue = new ArrayList<String>();
		prices = new ArrayList<Double>();
		colors = new ArrayList<Color>();
		configureCatalogue();
	}
	
	
	public boolean haltEnabled() {return true;}
	public boolean specialsEnabled() {return false;}
	
	
	public void placeCookies(){
		
		//shields
		board.cookies.add(new CookieShield(board,board.X_RESOL/2,150));
		board.cookies.add(new CookieShield(board,board.X_RESOL/2-200,150));
		board.cookies.add(new CookieShield(board,board.X_RESOL/2+200,150));
		board.cookies.add(new CookieShield(board,board.X_RESOL/2,board.Y_RESOL-150));
		board.cookies.add(new CookieShield(board,board.X_RESOL/2-200,board.Y_RESOL-150));
		board.cookies.add(new CookieShield(board,board.X_RESOL/2+200,board.Y_RESOL-150));
	
		//stats
		board.cookies.add(new CookieStat(board,board.X_RESOL-390,250));
		board.cookies.add(new CookieStat(board,board.X_RESOL-390,board.Y_RESOL-250));
		board.cookies.add(new CookieStat(board,board.X_RESOL-120,board.Y_RESOL/2));
		
		//items
		int i = (int)(Math.random()*catalogue.size());
		placeItem(390,250,catalogue.get(i),prices.get(i),colors.get(i));
		i = (int)(Math.random()*catalogue.size());
		placeItem(390,board.Y_RESOL-250,catalogue.get(i),prices.get(i),colors.get(i));
		i = (int)(Math.random()*catalogue.size());
		placeItem(120,board.Y_RESOL/2,catalogue.get(i),prices.get(i),colors.get(i));
		
		board.scoreToWin = 2;
	}
	private void configureCatalogue() {	
		addToCatalogue("Boost", 55, new Color(200,200,30));
		addToCatalogue("Bounce", 50, new Color(0,150,200));
		addToCatalogue("Circle", 60, new Color(220, 170, 70));
		addToCatalogue("Chain", 60, new Color(150,80,20));
		addToCatalogue("Field", 55, new Color(90,30,170));
		addToCatalogue("Hold", 55, new Color(200,50,50));
		addToCatalogue("Recycle", 70, new Color(30,30,230));
		addToCatalogue("Shield", 65, new Color(20,170,180));
		addToCatalogue("Slowmo", 70, new Color(100,120,100));
		addToCatalogue("Ghost", 70, new Color(30,200,150));
	}
	protected void addToCatalogue(String i, double p, Color c) {
		catalogue.add(i);
		prices.add(p);
		colors.add(c);
	}
	
	public void build() {
		board.walls.add(new Wall(board,0,0,board.X_RESOL,board.BORDER_THICKNESS)); //add border walls
		board.walls.add(new Wall(board,0,0,board.BORDER_THICKNESS,board.Y_RESOL));
		board.walls.add(new Wall(board,0,board.Y_RESOL-board.BORDER_THICKNESS,board.X_RESOL,board.BORDER_THICKNESS));
		board.walls.add(new Wall(board,board.X_RESOL-board.BORDER_THICKNESS,0,board.BORDER_THICKNESS,board.Y_RESOL));
		
		board.walls.add(new Wall(board,0,0,625,150));
		board.walls.add(new Wall(board,0,board.Y_RESOL-150,625,150));
		board.walls.add(new Wall(board,board.X_RESOL-625,0,625,150));
		board.walls.add(new Wall(board,board.X_RESOL-625,board.Y_RESOL-150,625,150));
		
		board.walls.add(new Wall(board,625-75,0,75,350));
		board.walls.add(new Wall(board,625-75,board.Y_RESOL-350,75,350));
		board.walls.add(new Wall(board,board.X_RESOL-625,0,75,350));
		board.walls.add(new Wall(board,board.X_RESOL-625,board.Y_RESOL-350,75,350));
		
		board.walls.add(new Wall(board,0,0,230,250));
		board.walls.add(new Wall(board,board.X_RESOL-230,0,230,250));
		board.walls.add(new Wall(board,0,board.Y_RESOL-250,230,250));
		board.walls.add(new Wall(board,board.X_RESOL-230,board.Y_RESOL-250,230,250));
		
		board.walls.add(new Wall(board,0,0,130,350));
		board.walls.add(new Wall(board,board.X_RESOL-130,0,130,350));
		board.walls.add(new Wall(board,0,board.Y_RESOL-350,130,350));
		board.walls.add(new Wall(board,board.X_RESOL-130,board.Y_RESOL-350,130,350));
	}
	
}
