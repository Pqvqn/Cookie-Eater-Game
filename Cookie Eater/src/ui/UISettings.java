package ui;

import java.util.*;

import ce3.*;
import menus.*;
import menus.MenuButton.*;

public class UISettings extends UIElement{
	
	private boolean visible;
	
	//ids for menus of buttons in settings menu
	enum SubMenu {
		MAIN,
		DEBUG
	}
	private ArrayList<MenuButton> mainButtons;
	private ArrayList<MenuButton> debugButtons;
	
	public UISettings(Board frame, int x, int y) {
		super(frame,x,y);
		makeButtons();
		
	}
	
	public void makeButtons() {
		OnClick oc;
		mainButtons = new ArrayList<MenuButton>();
		debugButtons = new ArrayList<MenuButton>();
		
		
		//toggles which menu is selected
		MenuButton sel = new MenuButton(board, null, new String[] {"MAIN","DEBUG"}, false, 1000,500,400,200);
		oc = () -> {
			//select menu from list based on button state
			SubMenu[] sels = {SubMenu.MAIN,SubMenu.DEBUG};
			int a = sel.currentState();
			displayMenu(sels[a]);
		};
		sel.setClick(oc);
		mainButtons.add(sel);
		debugButtons.add(sel);
				
		//volume control
		MenuButton vol = new MenuButton(board, null, new String[] {"mutevol.png", "highvol.png", "midvol.png", "lowvol.png"}, true, 500,500,400,200);
		oc = () -> {
			//select volume from list based on button state
			int[] vols = {0,10,20};
			int a = vol.currentState()-1;
			if(a>-1) {
				board.audio.setMute(false);
				board.audio.setVolumeReduction(vols[a]);
			}else {
				board.audio.setMute(true);
			}
		};
		vol.setClick(oc);
		mainButtons.add(vol);
		
		//gives the player a shield
		MenuButton givsh = new MenuButton(board, null, new String[] {"give Shield"}, false, 200,500,400,200);
		oc = () -> {
			board.player.addShields(1);
		};
		givsh.setClick(oc);
		debugButtons.add(givsh);
		
		
	}
	
	//sets visibility of a menu
	public void showMenu(SubMenu m, boolean s) {
		ArrayList<MenuButton> menu = null;
		switch(m) {
		case MAIN:
			menu = mainButtons;
			break;
		case DEBUG:
			menu = debugButtons;
			break;
		}
		if(menu==null)return;
		for(int i=0; i<menu.size(); i++) {
			menu.get(i).show(s);
		}
	}
	
	//show the chosen menu and hide all others
	public void displayMenu(SubMenu m) {//
		for(SubMenu om : SubMenu.values()) {
			if(om!=m) {
				showMenu(om,false);
			}
		}
		showMenu(m,true);
	}
	
	public void show(boolean s) {
		displayMenu(SubMenu.MAIN);
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
