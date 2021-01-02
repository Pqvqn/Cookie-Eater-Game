package ui;

import java.awt.*;

import ce3.*;
import menus.*;

public class UIButton extends UIElement{

	private UIText text;
	private UIRectangle backing;
	private boolean highlighted;
	
	public UIButton(Board frame, MenuButton b) {
		super(frame,(int)b.bounds().getX(),(int)b.bounds().getY());
		Rectangle rect = b.bounds();
		parts.add(text = new UIText(board,xPos+5,yPos+30,b.text(),new Color(255,255,255,150),new Font("Arial",Font.BOLD,30))); //text
		parts.add(backing = new UIRectangle(board,(int)rect.getX(),(int)rect.getY(),(int)rect.getWidth(),(int)rect.getHeight(),new Color(0,0,0,50),true));
		highlighted = false;
	}
	
	public void highlight(boolean h) {
		highlighted = h;
		backing.setColor((highlighted)?new Color(255,255,255,50):new Color(0,0,0,50));
	}
	
	
}
