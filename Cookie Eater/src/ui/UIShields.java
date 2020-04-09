package ui;

import java.awt.*;
import java.util.*;

import ce3.*;

public class UIShields extends UIElement{

	//UIText shields;
	private ArrayList<UIOval> shields;
	private final int SEPARATION = 30;
	private final int SIZE = 50;
	
	public UIShields(Board frame, int x, int y) {
		super(frame,x,y);
		shields = new ArrayList<UIOval>();
		//parts.add(shields = new UIText(board,x,y,"",new Color(100,200,255),new Font("Arial",Font.ITALIC,30)));
	}
	public void update(int s) {
		//shields.setText(s+" shields");
		if(shields.size()<s) {
			UIOval newOne = new UIOval(board, xPos-shields.size()*(SIZE+SEPARATION)-SIZE/2, yPos, SIZE, SIZE, new Color(50,200,210,200));
			shields.add(newOne);
			parts.add(newOne);
		}else if(shields.size()>s) {
			System.out.println("rem");
			UIOval kill = shields.get(shields.size()-1);
			shields.remove(kill);
			parts.remove(kill);
		}
	}
}
