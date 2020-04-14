package levels;

import java.util.*;
import java.awt.*;

import ce3.*;
import cookies.*;
import items.*;

public class Store1 extends FloorEntrance{
	
	private ArrayList<String> catalogue;
	private ArrayList<Double> prices;
	private ArrayList<Color> colors;
	private int slot;
	
	public Store1(Board frame) {
		super(frame);
		minDecay = Integer.MAX_VALUE;
		maxDecay = Integer.MAX_VALUE;
		catalogue = new ArrayList<String>();
		prices = new ArrayList<Double>();
		colors = new ArrayList<Color>();
		configureCatalogue();
		slot = 0;
	}
	
	
	public boolean specialsEnabled() {return false;}
	public void selectSlot(int s) {slot = s;}
	
	
	public void placeCookies(){
		
		board.cookies.add(new CookieShield(board,(int)(.5+startx-150),(int)(.5+starty-200)));
		board.cookies.add(new CookieShield(board,(int)(.5+startx+150),(int)(.5+starty-200)));
		
		board.cookies.add(new CookieStat(board,(int)(.5+startx-300),(int)(.5+starty+200)));
		board.cookies.add(new CookieStat(board,(int)(.5+startx+300),(int)(.5+starty+200)));
		board.cookies.add(new CookieStat(board,(int)(.5+startx),(int)(.5+starty+200)));
		
		int i = (int)(Math.random()*catalogue.size());
		placeItem((int)(.5+startx-500), (int)(starty), slot, catalogue.get(i), prices.get(i), colors.get(i));
		
		
		
		board.scoreToWin = 2;
	}
	private void configureCatalogue() {	
		addToCatalogue("Boost", 15, new Color(200,200,30));
		addToCatalogue("Bounce", 10, new Color(0,150,200));
		addToCatalogue("Circle", 20, new Color(220, 170, 70));
		addToCatalogue("Chain", 20, new Color(150,80,20));
		addToCatalogue("Field", 15, new Color(30,200,150));
		addToCatalogue("Hold", 15, new Color(200,50,50));
		addToCatalogue("Recycle", 30, new Color(30,30,230));
		addToCatalogue("Shield", 25, new Color(20,170,180));
		addToCatalogue("Slowmo", 30, new Color(100,120,100));
	}
	private void addToCatalogue(String i, double p, Color c) {
		catalogue.add(i);
		prices.add(p);
		colors.add(c);
	}
	private void placeItem(int x, int y, int s, String i, double p, Color c) {
		Item b;
		switch(i) {
		case "Boost":
			b = new ItemBoost(board);
			break;
		case "Bounce":
			b = new ItemBounce(board);
			break;
		case "Circle":
			b = new ItemCircle(board);
			break;
		case "Chain":
			b = new ItemCookieChain(board);
			break;
		case "Field":
			b = new ItemField(board);
			break;
		case "Hold":
			b = new ItemHold(board);
			break;
		case "Recycle":
			b = new ItemRecycle(board);
			break;
		case "Shield":
			b = new ItemShield(board);
			break;
		case "Slowmo":
			b = new ItemSlowmo(board);
			break;
		default:
			b = null;
		}
		board.cookies.add(new CookieItem(board, x, y, s, b, p, c));
	}
	
	public void build() {
		super.build();
	}
	
}
