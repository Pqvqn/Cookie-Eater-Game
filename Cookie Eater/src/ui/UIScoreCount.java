package ui;

import java.awt.*;

import ce3.*;

public class UIScoreCount extends UIElement{

	String content;
	UIText cash, cooks;
	
	public UIScoreCount(Board frame, int x, int y) {
		super(frame);
		parts.add(cash = new UIText(board,x+20,y+40,content,Color.GRAY.brighter()));
		parts.add(cooks = new UIText(board,x,y,content,Color.WHITE));
	}
	public void update(int ch, int ck, int nd) {
		cash.setText("$"+ch);
		cooks.setText(ck+"/"+nd);
	}
	public void paint(Graphics g) {
		g.setFont(new Font("Arial",Font.BOLD,30));
		cash.paint(g);
		g.setFont(new Font("Arial",Font.BOLD,40));
		cooks.paint(g);
	}
}
