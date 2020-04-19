package levels;

import java.util.*;
import java.awt.*;

import ce3.*;
import cookies.*;

public class Store4 extends Floor4{
	
	private ArrayList<String> catalogue;
	private ArrayList<Double> prices;
	private ArrayList<Color> colors;
	private double default_price;
	
	public Store4(Board frame) {
		super(frame);
		startx = board.X_RESOL/2;
		starty = board.Y_RESOL/2;
		minDecay = Integer.MAX_VALUE;
		maxDecay = Integer.MAX_VALUE;
		shieldCost = 75;
		default_price = 90;
		catalogue = new ArrayList<String>();
		prices = new ArrayList<Double>();
		colors = new ArrayList<Color>();
		configureCatalogue(default_price,catalogue,prices,colors);
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
		board.cookies.add(new CookieStat(board,board.X_RESOL-390,315));
		board.cookies.add(new CookieStat(board,board.X_RESOL-390,board.Y_RESOL-315));
		board.cookies.add(new CookieStat(board,board.X_RESOL-185,board.Y_RESOL/2));
		
		//items
		int i = (int)(Math.random()*catalogue.size());
		placeItem(390,315,catalogue.get(i),prices.get(i),colors.get(i));
		i = (int)(Math.random()*catalogue.size());
		placeItem(390,board.Y_RESOL-315,catalogue.get(i),prices.get(i),colors.get(i));
		i = (int)(Math.random()*catalogue.size());
		placeItem(185,board.Y_RESOL/2,catalogue.get(i),prices.get(i),colors.get(i));
		
		board.player.setScoreToWin(2);
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
