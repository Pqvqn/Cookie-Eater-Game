package ui;

import java.awt.*;
import java.text.DecimalFormat;

import ce3.*;

public class UIPurchaseInfo extends UIElement{

	private UIText price, item;
	private boolean visible;
	private UIPurchaseDesc extended;
	//private UIRectangle backing;
	
	public UIPurchaseInfo(Board frame, int x, int y) {
		super(frame,x,y);
		parts.add(new UIRectangle(board,xPos-100,yPos-100,200,100,new Color(0,0,0,100))); //backing
		parts.add(price = new UIText(board,xPos-80,yPos-20,"",new Color(120,120,120,150),new Font("Arial",Font.BOLD,25)));
		parts.add(item = new UIText(board,xPos-80,yPos-50,"",new Color(255,255,255,150),new Font("Arial",Font.BOLD,30)));
		parts.add(extended = new UIPurchaseDesc(board, xPos, yPos+110));
		visible = true;
	}
	public void update(boolean show1, boolean show2, double cost, boolean afford, String name, String desc) {
		visible = show1;
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
	public void paint(Graphics g) {
		if(visible)super.paint(g);
	}
}
