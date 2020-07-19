package ui;

import java.awt.*;
import java.text.DecimalFormat;

import ce3.*;
import cookies.*;

public class UIPurchaseInfo extends UIElement{

	private UIText price, item;
	private boolean visible;
	private UIPurchaseDesc extended;
	private CookieStore user;
	private UIRectangle backing;
	
	public UIPurchaseInfo(Board frame, CookieStore c) {
		super(frame,0,0);
		user = c;
		xPos=user.getX();
		yPos=user.getY()-50;
		parts.add(backing = new UIRectangle(board,xPos-100,yPos-100,200,100,new Color(0,0,0,100),true));
		parts.add(price = new UIText(board,xPos-80,yPos-20,"",new Color(120,120,120,150),new Font("Arial",Font.BOLD,25)));
		parts.add(item = new UIText(board,xPos-80,yPos-50,"",new Color(255,255,255,150),new Font("Arial",Font.BOLD,30)));
		parts.add(extended = new UIPurchaseDesc(board, c));
		visible = true;
	}
	public void update(boolean show1, boolean show2, double cost, boolean afford, String name, String desc) {
		visible = show1;
		xPos=user.getX();
		yPos=user.getY()-50;
		backing.setxPos(xPos-100);backing.setyPos(yPos-100);
		price.setxPos(xPos-80);price.setyPos(yPos-20);
		item.setxPos(xPos-80);item.setyPos(yPos-50);
		if(afford) {
			price.setColor(Color.gray);
		}else {
			price.setColor(Color.red);
		}
		DecimalFormat rounder = new DecimalFormat("#.#");
		price.setText("$"+rounder.format(cost+.04));
		item.setText(name);
		extended.update(show2,desc);
	}
	public void setVisible(boolean v) {visible = v;}
	public void paint(Graphics g) {
		if(visible)super.paint(g);
	}
}
