package cookies;

import java.awt.*;

import items.Item;
import ce3.*;
//import levels.*;

public class CookieItem extends CookieStore{
	
	private Item myItem;
	private Color color;
	
	public CookieItem(Board frame, int startx, int starty, Item i, double p, Color c) {
		super(frame,startx,starty);
		price = board.currFloor.getShieldCost();
		myItem = i;
		price = p;
		name = myItem.getName();
		color = c;
	}
	public boolean purchase() {
		if(board.player.getCash()>=price) {
			
			player.addItem(player.getCurrentSpecial(), myItem);
			board.player.addCash(-price);
			return true;}
		return false;
	}
	public void paint(Graphics g) {
		g.setColor(color);
		g.fillOval((int)(.5+x-radius*board.currFloor.getScale()), (int)(.5+y-radius*board.currFloor.getScale()), (int)(.5+radius*board.currFloor.getScale()*2), (int)(.5+radius*board.currFloor.getScale()*2));
	}
}
