package ui;

import java.awt.*;

import ce3.*;

public class UILevelInfo extends UIElement{

	private UIText name;
	
	public UILevelInfo(Game frame, int x, int y) {
		super(frame,x,y);
		parts.add(name = new UIText(board,x,y,"",new Color(255,255,255,200),new Font("Arial",Font.BOLD,30)));
	}
	public void update(String s) {
		name.setText(s);
		int newx = (int)(.5+xPos - (s.length()/2.0)*17);
		name.setxPos(newx);
	}
}
