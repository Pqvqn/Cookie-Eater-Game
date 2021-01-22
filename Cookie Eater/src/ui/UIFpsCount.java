package ui;

import java.awt.*;
import java.text.*;

import ce3.*;

public class UIFpsCount extends UIElement{

	private UIText text;
	
	public UIFpsCount(Game frame, int x, int y, Color c) {
		super(frame,x,y);
		text = new UIText(board,x,y,"",c,new Font(Font.MONOSPACED,Font.PLAIN,15));
		parts.add(text);
	}
	public double update(long last, long curr) {
		DecimalFormat rounder = new DecimalFormat("#.#");
		double fps = Double.valueOf(rounder.format((1000.0/(curr-last))));
		text.setText(""+fps);
		return fps;
	}
}
