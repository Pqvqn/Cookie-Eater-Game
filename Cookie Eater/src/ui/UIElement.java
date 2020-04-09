package ui;

import java.awt.*;
import java.util.*;

import ce3.*;

public abstract class UIElement {
	
	protected ArrayList<UIElement> parts; //uielements within this one
	protected Board board;
	
	public UIElement(Board frame) {
		parts = new ArrayList<UIElement>();
		board = frame;
	}
	
	public void paint(Graphics g) {
		for(int i=0; i<parts.size(); i++) { //paint all uielements within
			parts.get(i).paint(g);
		}
	}
}
