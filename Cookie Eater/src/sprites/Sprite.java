package sprites;

import java.awt.*;
import java.util.*;

import ce3.*;

public abstract class Sprite {
	
	protected Board board;
	protected int x, y;
	protected ArrayList<Image> imgs;
	protected int frameCount;
	
	public Sprite(Board frame) {
		board = frame;
		imgs = new ArrayList<Image>();
		frameCount=0;
	}
	public void paint(Graphics g) {
		
	}
}
