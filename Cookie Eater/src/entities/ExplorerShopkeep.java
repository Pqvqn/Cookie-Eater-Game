package entities;

import java.awt.Graphics;
//import java.util.*;

import ce3.*;
import cookies.*;
import levels.*;

public class ExplorerShopkeep extends Explorer{
	
	public ExplorerShopkeep(Board frame) {
		super(frame);
		name = "Unknown";
		chooseResidence();
		setRadius(40);
		min_cat = 3;
		max_cat = 8;
	}

	public void runEnds() {
		for(int i=0; i<Math.random()*4-1; i++) {
			removeRandomly();
		}
		for(int i=0; i<Math.random()*4-1 || to_sell.size()<min_cat; i++) {
			double choose = Math.random()*5;
			if(choose<=2) {
				addRandomly(new CookieShield(board,0,0,15));
			}else if(choose<=3) {
				addRandomly(new CookieItem(board,0,0,Level.generateItem(board,"Circle"),30));
			}else if(choose<=3.5) {
				addRandomly(new CookieItem(board,0,0,Level.generateItem(board,"Shield"),30));
			}else {
				addRandomly(new CookieItem(board,0,0,Level.generateItem(board,"Field"),30));
			}

		}
		while(to_sell.size()>max_cat)removeRandomly();
	}
	public void runUpdate() {
		super.runUpdate();
	}

	public void chooseResidence() {
		residence = findFloor("Dungeon Foyer",true,0,2);
	}

	public void createStash() {
		super.createStash();
		for(int i=0; i<4; i++) {
			double choose = Math.random()*5;
			if(choose<=2) {
				addRandomly(new CookieShield(board,0,0,15));
			}else if(choose<=3) {
				addRandomly(new CookieItem(board,0,0,Level.generateItem(board,"Circle"),30));
			}else if(choose<=3.5) {
				addRandomly(new CookieItem(board,0,0,Level.generateItem(board,"Shield"),30));
			}else {
				addRandomly(new CookieItem(board,0,0,Level.generateItem(board,"Field"),30));
			}

		}
	}
	
	public void paint(Graphics g) {
		g.fillOval((int)(.5+x-getRadius()), (int)(.5+y-getRadius()), (int)(.5+getRadius()*2), (int)(.5+getRadius()*2));
	}
}
