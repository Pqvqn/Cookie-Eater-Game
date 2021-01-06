package ui;

import ce3.*;
import menus.*;
import menus.MenuButton.*;

public class UISettings extends UIElement{
	
	private MenuButton mute;
	private boolean visible;
	
	public UISettings(Board frame, int x, int y) {
		super(frame,x,y);
		OnClick oc = () -> {
			board.audio.toggleMute();
		};
		mute = new MenuButton(board, oc, new String[] {"mute.png", "unmute.png"}, true, 500,500,400,200);
	}
	
	public void show(boolean s) {
		mute.show(s);
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
