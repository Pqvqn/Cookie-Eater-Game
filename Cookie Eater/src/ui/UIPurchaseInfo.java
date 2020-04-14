package ui;

import java.awt.*;

import ce3.*;

public class UIPurchaseInfo extends UIElement{

	private UIText price, item;
	boolean visible;
	//private UIRectangle backing;
	
	public UIPurchaseInfo(Board frame, int x, int y) {
		super(frame,x,y);
		parts.add(price = new UIText(board,xPos-80,yPos-20,"",new Color(120,120,120,200),new Font("Arial",Font.BOLD,25)));
		parts.add(item = new UIText(board,xPos-80,yPos-50,"",new Color(255,255,255,200),new Font("Arial",Font.BOLD,30)));
		parts.add(new UIRectangle(board,xPos-100,yPos-100,200,100,new Color(0,0,0,100))); //backing
		visible = true;
	}
	public void update(boolean show, double cost, boolean afford, String name) {
		visible = show;
		if(afford) {
			price.setColor(Color.gray);
		}else {
			price.setColor(Color.red);
		}
		price.setText("$"+cost);
		item.setText(name);
	}
	public void paint(Graphics g) {
		if(visible)super.paint(g);
	}
}
