package ui;

import java.awt.*;
import java.text.*;

import ce3.*;

public class UIFpsCount extends UIElement{

	private UIText text;
	//averaging fps among last frames
	private int submitted;
	private int sumframes;
	private int waitfor;
	
	public UIFpsCount(Game frame, int x, int y, Color c) {
		super(frame,x,y);
		text = new UIText(game,x,y,"",c,new Font(Font.MONOSPACED,Font.PLAIN,15));
		parts.add(text);
		sumframes = 0;
		submitted = 0;
		waitfor = 50;
	}
	public void update(long last, long curr) {
		sumframes += curr-last;
		submitted++;
		if(submitted >= waitfor) {
			DecimalFormat rounder = new DecimalFormat("#.#");
			double fps = Double.valueOf(rounder.format((1000.0/(sumframes/waitfor))));
			text.setText(""+fps);
			submitted = 0;
			sumframes = 0;
		}
	}
}
