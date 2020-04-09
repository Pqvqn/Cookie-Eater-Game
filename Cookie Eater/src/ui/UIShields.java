package ui;

import java.awt.*;

import ce3.*;

public class UIShields extends UIElement{

	UIText shields;
	
	public UIShields(Board frame, int x, int y) {
		super(frame);
		parts.add(shields = new UIText(board,x,y,"",new Color(100,200,255),new Font("Arial",Font.ITALIC,30)));
	}
	public void update(int s) {
		shields.setText(s+" shields");
	}
}
