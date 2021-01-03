package ui;

import ce3.*;
import menus.*;
import menus.MenuButton.*;

public class UISettings extends UIElement{
	
	private MenuButton b;
	private boolean visible;
	
	public UISettings(Board frame, int x, int y) {
		super(frame,x,y);
		OnClick oc = () -> {
			System.out.println("AAA");
		};
		b = new MenuButton(board, oc, "ahh", 500,500,200,200);
	}
	
	public void show(boolean s) {
		b.show(s);
		if(visible!=s) {
			if(s) {
				if(!board.draw.getUIList().contains(this))board.draw.addUI(this);
			}else {
				if(board.draw.getUIList().contains(this))board.draw.removeUI(this);
			}
		}
		visible = s;
	}
	
	public boolean isVisible() {
		return visible;
	}

}
