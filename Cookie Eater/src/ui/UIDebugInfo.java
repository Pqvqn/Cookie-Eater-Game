package ui;

import java.awt.*;
import java.util.*;

import ce3.*;

public class UIDebugInfo extends UIElement{

	private UIParagraph name;
	
	public UIDebugInfo(Game frame, int x, int y) {
		super(frame,x,y);
		parts.add(name = new UIParagraph(game,x,y,new ArrayList<String>(),new Color(255,255,255,200),new Font("Arial",Font.BOLD,30),30,30));
	}
	public void update(String[] themes, double[] weights) {
		String s = "";
		for(int i=0; i<themes.length; i++) {
			s += themes[i] + ": " + ((int)(weights[i]*100))/100.0 + "`";
		}
		name.setTextLines(s);
	}
}
