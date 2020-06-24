package entities;

import java.awt.Graphics;
//import java.util.*;

import ce3.*;
import cookies.*;

public class ExplorerShopkeep extends Explorer{
	
	public ExplorerShopkeep(Board frame) {
		super(frame);
		name = "Unknown";
		chooseResidence();
		setRadius(40);
	}

	public void runEnds() {
		
	}
	public void runUpdate() {
		super.runUpdate();
	}

	public void chooseResidence() {
		residence = findFloor("Dungeon Foyer",true,0,2);
	}

	public void createStash() {
		to_sell.add(new CookieShield(board,(int)(.5+x),(int)(.5+y),15));
	}
	
	public void paint(Graphics g) {
		g.fillOval((int)(.5+x-getRadius()), (int)(.5+y-getRadius()), (int)(.5+getRadius()*2), (int)(.5+getRadius()*2));
	}
}
