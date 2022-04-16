package ui;

import java.util.*;

import ce3.*;
import cookies.*;
import entities.*;
import items.*;
import mechanisms.*;
import menus.*;
import menus.MenuButton.*;

public class UISettings extends UIElement{
	
	private boolean visible;
	//menu selector button
	private MenuButton sel;
	private SubmenuHandler menuHandler;
	private Eater player;
	private ArrayList<MenuButton> updateList; //buttons which need to have state updated depending on which player hit settings
	public Map<String,Object> setOptions; //options that have been set by the player and must be set
	public PointClicker pc;
	
	public UISettings(Game frame, int x, int y) {
		super(frame,x,y);
		setOptions = new HashMap<String,Object>();
	}
	
	public SaveData getSaveData() {
		SaveData data = new SaveData();
		Iterator<String> it = setOptions.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			data.addData(key,setOptions.get(key),0);
		}
		return data;
	}
	
	public void makeButtons(SaveData sd) {
		if(menuHandler!=null)menuHandler.delete();
		menuHandler = new SubmenuHandler("MAIN");
		updateList = new ArrayList<MenuButton>();
		OnClick oc;
		Board board = game.board;
		
		//toggles which menu is selected
		sel = new MenuButton(game, this, null, new String[] {"MAIN","DEBUG","TEST","BUILD"}, false, 1300,700,400,200);
		oc = () -> {
			//select menu from list based on button state
			menuHandler.displayMenu(sel.getState());
		};
		sel.setClick(oc);
		menuHandler.addButton("MAIN",sel);
		menuHandler.addButton("DEBUG",sel);
		menuHandler.addButton("TEST",sel);
		menuHandler.addButton("BUILD",sel);
		
		//selects which player the menu corresponds to
		String[] opts = new String[board.players.size()];
		for(int i=0; i<board.players.size(); i++) {
			opts[i] = "P"+i;
		}
		MenuButton psel = new MenuButton(game, this, null, opts, false, 10,10,100,100);
		oc = () -> {
			Eater p = board.players.get(psel.currentState());
			this.show(false);
			this.show(true, p);
		};
		psel.setClick(oc);
		menuHandler.addButton("MAIN",psel);
		
		//volume control
		MenuButton vol = new MenuButton(game, this, null, new String[] {"mutevol.png", "highvol.png", "midvol.png", "lowvol.png"}, true, 600,500,400,200);
		oc = () -> {
			//select volume from list based on button state
			int[] vols = {0,10,20};
			int a = vol.currentState()-1;
			if(a>-1) {
				game.audio.setMute(false);
				game.audio.setVolumeReduction(vols[a]);
			}else {
				game.audio.setMute(true);
			}
			setOptions.put("Volume",vol.getState());
		};
		setOptions.put("Volume",vol.getState());
		vol.setClick(oc);
		if(sd!=null)vol.clickTo(sd.getString("Volume",0));
		menuHandler.addButton("MAIN",vol);
		
		//set controls
		int[] keyBinds = {Controls.UPKEY,Controls.DOWNKEY,Controls.LEFTKEY,Controls.RIGHTKEY,Controls.SPECIALKEY,Controls.PAUSEKEY};
		String[] keyNames = {"up","down","left","right","special","esc"};
		int[][] keyPos = {{300,400},{300,520},{180,520},{420,520},{180,400},{260,280}};
		//int playerid = (player==null)?0:player.getID();
		for(int i=0; i<keyBinds.length; i++) {
			MenuButton keyset = new MenuButton(game, this, null, 
					new String[] {java.awt.event.KeyEvent.getKeyText(getSelectedPlayer().controls.getKeyBind(keyBinds[i]))}, false, keyPos[i][0],keyPos[i][1],100,100);
			final String keyname = keyNames[i];
			final int keybind = keyBinds[i];
			oc = () -> {
				//ask board to await key press to reassign
				Eater p = this.getSelectedPlayer();
				keyset.setCurrStateValue(keyname+" =");
				p.controls.awaitKeyBind(keyset,keybind);
			};
			keyset.setClick(oc);
			menuHandler.addButton("MAIN",keyset);
			updateList.add(keyset);
		}
		
		//gives the player a shield
		MenuButton givsh = new MenuButton(game, this, null, new String[] {"give 1 shield"}, false, 120,475,200,100);
		oc = () -> {
			getSelectedPlayer().addShields(1);
		};
		givsh.setClick(oc);
		menuHandler.addButton("DEBUG",givsh);
		//gives the player 100 shields
		MenuButton givmsh = new MenuButton(game, this, null, new String[] {"give 100 shields"}, false, 120,625,200,100);
		oc = () -> {
			getSelectedPlayer().addShields(100);
		};
		givmsh.setClick(oc);
		menuHandler.addButton("DEBUG",givmsh);
		
		//kills player to return to first floor
		MenuButton reset = new MenuButton(game, this, null, new String[] {"end run"}, false, 120,325,200,100);
		oc = () -> {
			board.killPlayers();
		};
		reset.setClick(oc);
		menuHandler.addButton("DEBUG",reset);
		
		//shows title screen and saves board
		MenuButton title = new MenuButton(game, this, null, new String[] {"title screen"}, false, 120,25,200,100);
		oc = () -> {
			game.board = null;
			this.show(false);
			game.ui_tis.show();
		};
		title.setClick(oc);
		menuHandler.addButton("MAIN",title);
		
		//moves to next floor
		MenuButton advance = new MenuButton(game, this, null, new String[] {"advance floor"}, false, 120,175,200,100);
		oc = () -> {
			if(!board.inConvo()) {
				Passage exit = board.currLevel.firstExit();
				exit.trigger(getSelectedPlayer());
			}
		};
		advance.setClick(oc);
		menuHandler.addButton("DEBUG",advance);
		
		//gives player 10 cookies
		MenuButton givco = new MenuButton(game, this, null, new String[] {"give 10 cookies"}, false, 120,775,200,100);
		oc = () -> {
			getSelectedPlayer().pay(10);
		};
		givco.setClick(oc);
		menuHandler.addButton("DEBUG",givco);
		
		//removes all cookies from the board
		MenuButton clrco = new MenuButton(game, this, null, new String[] {"clear cookies"}, false, 120,925,200,50);
		oc = () -> {
			board.currLevel.chunker.kill();
		};
		clrco.setClick(oc);
		menuHandler.addButton("DEBUG",clrco);

		//gives player item in name
		String[] powerups = {"Boost","Circle","Chain","Field","Hold","Recycle","Shield","Slowmo","Ghost",
				"Return","Teleport","Repeat","Rebound","Clone","Ricochet","Shrink","Autopilot","Flow","Recharge",
				"Melee","Projectile"}; //list of all items to make buttons for
		for(int i=0; i<powerups.length; i++) {
			String pw = powerups[i];
			int rows = 6, xs=400, ys=100, gap=50, wid=200, hei=100; //values for placement of buttons
			
			MenuButton givit = new MenuButton(game, this, null, new String[] {"give "+pw}, false, xs+(i/rows*(wid+gap)),(ys+((hei+gap)*(i%rows))),wid,hei);
			oc = () -> {
				getSelectedPlayer().addItem(getSelectedPlayer().getCurrentSpecial(),
						new CookieItem(game,board,board.currLevel,0,0,Item.generateItem(game,board,pw),0.0));
			};
			givit.setClick(oc);
			menuHandler.addButton("DEBUG",givit);
		}
		
		//adds to player stats
		for(int t=0; t<3; t++) {
			for(int d=-1; d<=1; d+=2) {
				int xs=1500, ys=100, gap=50, wid=200, hei=100; //values for placement of buttons
				MenuButton givst = new MenuButton(game, this, null, new String[] {"give "+t+":"+d}, false, (int)(xs+(d/2.0)*(gap+wid)), ys+t*(gap+hei),wid,hei);
				int t2 = t, d2 = d;
				oc = () -> {
					getSelectedPlayer().hitCookie(new CookieStat(game,board,board.currLevel,0,0,t2+1,d2,1));
				};
				givst.setClick(oc);
				menuHandler.addButton("DEBUG",givst);
			}
		}
		
		//enables point clicking tool
		MenuButton pointc = new MenuButton(game, this, null, new String[] {"Activate PointClicker","Deactivate PointClicker"}, false, 120,80,200,100);
		oc = () -> {
			if(pointc.currentState()-1 == 0) {
				if(pc==null) {
					pc = new PointClicker(game);
				}
				pc.activate(true);
			}else {
				pc.activate(false);
			}
		};
		pointc.setClick(oc);
		menuHandler.addButton("BUILD",pointc);
		
		//places a wall on the board
		MenuButton pwall = new MenuButton(game, this, null, new String[] {"place wall"}, false, 520,775,200,100);
		oc = () -> {
			Wall wnw = new Wall(game,board,board.currLevel,500,500,50,50);
			board.currLevel.walls.add(wnw);
			board.currLevel.mechanisms.add(wnw);
			game.draw.updateBG();
		};
		pwall.setClick(oc);
		menuHandler.addButton("BUILD",pwall);

		
		//shows fps
		MenuButton fps = new MenuButton(game, this, null, new String[] {"show fps","hide fps"}, false, 1420,555,100,100);
		oc = () -> {
			if(fps.currentState()==1 && !game.draw.getUIList().contains(game.ui_fps)) {
				game.draw.addUI((game.ui_fps));
			}else if(fps.currentState()==0 && game.draw.getUIList().contains(game.ui_fps)) {
				game.draw.removeUI((game.ui_fps));
			}
			setOptions.put("FPS",fps.getState());
		};
		setOptions.put("FPS",fps.getState());
		fps.setClick(oc);
		if(sd!=null)fps.clickTo(sd.getString("FPS",0));
		menuHandler.addButton("MAIN",fps);
		
		//toggles intensive graphics
		MenuButton graphic = new MenuButton(game, this, null, new String[] {"lower graphics","default graphics"}, false, 420,775,200,100);
		oc = () -> {
			game.draw.setGraphicsLevel(graphic.currentState()==0);
			setOptions.put("Graphics",graphic.getState());
		};
		setOptions.put("Graphics",graphic.getState());
		graphic.setClick(oc);
		if(sd!=null)graphic.clickTo(sd.getString("Graphics",0));
		menuHandler.addButton("MAIN",graphic);
		
	}
	//update controls for selected player
	public void updateButtons() {
		int[] keyBinds = {Controls.UPKEY,Controls.DOWNKEY,Controls.LEFTKEY,Controls.RIGHTKEY,Controls.SPECIALKEY,Controls.PAUSEKEY};
		for(int i=0; i<updateList.size(); i++) {
			updateList.get(i).setCurrStateValue(java.awt.event.KeyEvent.getKeyText(getSelectedPlayer().controls.getKeyBind(keyBinds[i])));
		}
	}
	public Eater getSelectedPlayer() {return (player==null)?game.board.players.get(0):player;}
	//show from perspective of a specific player
	public void show(boolean s, Eater p) {
		if(player==null || player.equals(p)) { //don't let players override other player's settings menu
			player = p;
			updateButtons();
			show(s);
		}
	}
	
	public void show(boolean s) {
		menuHandler.showFull(s);
		sel.resetState();
		if(visible!=s) {
			if(s) {
				if(!game.draw.getUIList().contains(this))game.draw.addUI(this);
			}else {
				if(game.draw.getUIList().contains(this))game.draw.removeUI(this);
				player = null;
			}
		}
		visible = s;
	}
	
	public boolean isVisible() {
		return visible;
	}

}
