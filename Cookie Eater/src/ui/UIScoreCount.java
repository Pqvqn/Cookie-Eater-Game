package ui;

import java.awt.*;

import ce3.*;

public class UIScoreCount extends UIElement{

	UIText cash, cooks;
	
	public UIScoreCount(Board frame, int x, int y) {
		super(frame);
		parts.add(cash = new UIText(board,x+20,y+40,"",Color.GRAY.brighter(),new Font("Arial",Font.BOLD,30)));
		parts.add(cooks = new UIText(board,x,y,"",Color.WHITE,new Font("Arial",Font.BOLD,40)));
	}
	public void update(int ch, int ck, int nd) {
		cash.setText("$"+ch);
		cooks.setText(ck+"/"+nd);
	}
}
