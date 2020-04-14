package cookies;

import java.awt.*;

import items.Item;
import ce3.*;
//import levels.*;

public class CookieItem extends CookieStore{
	
	private Item myItem;
	private Color color;
	private int special;
	
	public CookieItem(Board frame, int startx, int starty, int s, Item i, double p, Color c) {
		super(frame,startx,starty);
		price = board.currFloor.getShieldCost();
		myItem = i;
		price = p;
		name = myItem.getName();
		color = c;
		special = s;
	}
	public boolean purchase() {
		if(board.cash>=price) {
			player.addItem(special, myItem);
			board.cash-=price;
			return true;}
		return false;
	}
	public void paint(Graphics g) {
		g.setColor(color);
		g.fillOval((int)(.5+x-radius*board.currFloor.getScale()), (int)(.5+y-radius*board.currFloor.getScale()), (int)(.5+radius*board.currFloor.getScale()*2), (int)(.5+radius*board.currFloor.getScale()*2));
	}
}
