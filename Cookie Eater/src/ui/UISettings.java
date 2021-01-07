package ui;

import ce3.*;
import menus.*;
import menus.MenuButton.*;

public class UISettings extends UIElement{
	
	private MenuButton vol;
	private boolean visible;
	
	public UISettings(Board frame, int x, int y) {
		super(frame,x,y);
		vol = new MenuButton(board, null, new String[] {"highvol.png", "midvol.png", "lowvol.png", "mutevol.png"}, true, 500,500,400,200);
		OnClick oc = () -> {
			//select volume from list based on button state
			int[] vols = {0,10,20};
			int a = vol.currentState();
			if(a<vols.length) {
				board.audio.setMute(false);
				board.audio.setVolumeReduction(vols[a]);
			}else {
				board.audio.setMute(true);
			}
		};
		vol.setClick(oc);
	}
	
	public void show(boolean s) {
		vol.show(s);
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
