package ui;

import java.util.*;

import ce3.*;
import items.*;
import levels.*;
import menus.*;
import menus.MenuButton.*;

public class UISettings extends UIElement{
	
	private boolean visible;
	
	//ids for menus of buttons in settings menu
	enum SubMenu {
		MAIN,
		DEBUG
	}
	//lists of buttons in each menu
	private ArrayList<MenuButton> mainButtons;
	private ArrayList<MenuButton> debugButtons;
	//menu selector button
	private MenuButton sel;
	
	public UISettings(Board frame, int x, int y) {
		super(frame,x,y);
		makeButtons();
		
	}
	
	public void makeButtons() {
		OnClick oc;
		mainButtons = new ArrayList<MenuButton>();
		debugButtons = new ArrayList<MenuButton>();
		
		
		//toggles which menu is selected
		sel = new MenuButton(board, this, null, new String[] {"MAIN","DEBUG"}, false, 1300,700,400,200);
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
		MenuButton vol = new MenuButton(board, this, null, new String[] {"mutevol.png", "highvol.png", "midvol.png", "lowvol.png"}, true, 500,500,400,200);
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
		MenuButton givsh = new MenuButton(board, this, null, new String[] {"give 1 shield"}, false, 120,475,200,100);
		oc = () -> {
			board.player.addShields(1);
		};
		givsh.setClick(oc);
		debugButtons.add(givsh);
		
		//kills player to return to first floor
		MenuButton reset = new MenuButton(board, this, null, new String[] {"end run"}, false, 120,325,200,100);
		oc = () -> {
			board.player.kill();
		};
		reset.setClick(oc);
		debugButtons.add(reset);
		
		//kills player to return to first floor
		MenuButton title = new MenuButton(board, this, null, new String[] {"title screen"}, false, 120,25,200,100);
		oc = () -> {
			board.player.kill();
			board.ui_tis.show();
		};
		title.setClick(oc);
		debugButtons.add(title);
		
		//moves to next floor
		MenuButton advance = new MenuButton(board, this, null, new String[] {"advance floor"}, false, 120,175,200,100);
		oc = () -> {
			if(!board.inConvo())board.player.win();
		};
		advance.setClick(oc);
		debugButtons.add(advance);
		
		//gives player 10 cookies
		MenuButton givco = new MenuButton(board, this, null, new String[] {"give 10 cookies"}, false, 120,625,200,100);
		oc = () -> {
			board.player.pay(10);
		};
		givco.setClick(oc);
		debugButtons.add(givco);

		//gives player item in name
		String[] powerups = {"Boost","Circle","Chain","Field","Hold","Recycle","Shield","Slowmo","Ghost",
				"Return","Teleport","Repeat","Rebound","Clone","Ricochet","Shrink","Autopilot","Flow","Recharge",
				"Melee","Projectile"}; //list of all items to make buttons for
		for(int i=0; i<powerups.length; i++) {
			String pw = powerups[i];
			int rows = 6, xs=400, ys=100, gap=50, wid=200, hei=100; //values for placement of buttons
			
			MenuButton givit = new MenuButton(board, this, null, new String[] {"give "+pw}, false, xs+(i/rows*(wid+gap)),(ys+((hei+gap)*(i%rows))),wid,hei);
			oc = () -> {
				board.player.addItem(0,Level.generateItem(board,pw));
			};
			givit.setClick(oc);
			debugButtons.add(givit);
		}
		
		
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
		showMenu(SubMenu.MAIN,s);
		sel.resetState();
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
