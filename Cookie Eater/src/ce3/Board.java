package ce3;

//import java.awt.*;
//import java.awt.event.*;

import javax.swing.*;

import cookies.*;
import entities.*;
import levels.*;
import mechanisms.*;
import ui.*;
import menus.*;
import menus.Menu;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Board{

	public Game game;
	public static final int LEVELS = 0, PVP = 1;
	public int mode;
	public final int Y_RESOL = 1020, X_RESOL = 1920; //board dimensions
	public final int BORDER_THICKNESS = 20;
	private Eater player;
	public ArrayList<Cookie> cookies;
	public ArrayList<Wall> walls;
	public ArrayList<Mechanism> mechanisms; //moving or functional parts of level
	public Area wallSpace;
	public ArrayList<Effect> effects;
	public ArrayList<Enemy> enemies;
	public ArrayList<Eater> players;
	public ArrayList<Explorer> npcs;
	public ArrayList<Explorer> present_npcs; //npcs that exist on current level
	public ArrayList<Menu> menus;

	public final Level[][] FLOOR_SEQUENCE = {
			{new Store1(game),new Floor1(game),
			new Store2(game),new Floor2(game),new Floor2(game),
			new Store3(game),new Floor3(game),new Floor3(game),new Floor3(game), 
			new Store4(game),new Floor4(game),new Floor4(game),new Floor4(game),new Floor4(game),new Floor5(game)},
			
			{new Store2(game),new FloorRound(game),
			new Store3(game),new Floor4(game),new FloorRound(game),
			new Store4(game),new FloorRound(game),new FloorRound(game),new FloorRound(game), 
			new Store1(game),new FloorRound(game),new FloorRound(game),new FloorRound(game),new FloorRound(game),new FloorRound(game)}
	}; //order of floors for each dungeon 
	public LinkedList<Level> floors;
	public int currDungeon;
	public Level currFloor;
	public int playerCount;
	public boolean awaiting_start;
	public int cycletime;
	
	public UILevelInfo ui_lvl;
	public UIDialogue ui_dia;
	
	public Board(Game g, int gamemode, int dungeon, int cycle) {
		game = g;
		mode = gamemode;
		cycletime = cycle;
		//initializing classes
		players = new ArrayList<Eater>();
		playerCount = 4;
		if(mode==LEVELS) {
			players.add(player = new Eater(game,0,cycletime));
		}else if(mode==PVP) { //add number of players
			for(int i=0; i<playerCount; i++)
				players.add(new Eater(game,i,cycletime));
		}
		
		
		cookies = new ArrayList<Cookie>();
		walls = new ArrayList<Wall>();
		mechanisms = new ArrayList<Mechanism>();
		wallSpace = new Area();
		enemies = new ArrayList<Enemy>();
		npcs = new ArrayList<Explorer>();
		present_npcs = new ArrayList<Explorer>();
		effects = new ArrayList<Effect>();
		menus = new ArrayList<Menu>();
		
		game.draw.addUI(ui_lvl = new UILevelInfo(game,X_RESOL/2,30));
		if(mode == LEVELS) {
			//create all of this game's npcs
			createNpcs(cycletime);
		}
		
		loadDungeon(dungeon);
		
	}
	
	
	public void updateUI() {
		//level display
		ui_lvl.update(currFloor.getName());
		
		//dialogue
		if(ui_dia!=null)ui_dia.update();
		
		//player's ui
		for(int i=0; i<players.size(); i++) {
			players.get(i).updateUI();
		}

	}
	//update all objects
	public void runUpdate() {
		updateUI();
		if(isPaused())//if board is paused, do not update
			return;
		for(int i=0; i<mechanisms.size(); i++) {
			mechanisms.get(i).runUpdate();
		}
		for(int i=0; i<effects.size(); i++) {
			effects.get(i).runUpdate();
		}
		for(int i=0; i<players.size(); i++) {
			players.get(i).runUpdate();
		}
		for(int i=0; i<present_npcs.size(); i++) {
			present_npcs.get(i).runUpdate();
		}
		for(int i=0; i<cookies.size(); i++) {
			if(i<cookies.size()) {
				Cookie curr = cookies.get(i);
				if(curr!=null)
				curr.runUpdate();
				if(i<cookies.size()&&cookies.get(i)!=null&&curr!=null&&!cookies.get(i).equals(curr))
					i--;
			}
		}
		for(int i=0; i<enemies.size(); i++) {
			if(i<enemies.size()) {
				Enemy curr = enemies.get(i);
				curr.runUpdate();
				if(i<enemies.size()&&!enemies.get(i).equals(curr))
					i--;
			}
		}
		for(int i=0; i<effects.size(); i++) {
			effects.get(i).endCycle();
		}
		for(int i=0; i<players.size(); i++) {
			players.get(i).endCycle();
		}
		for(int i=0; i<present_npcs.size(); i++) {
			present_npcs.get(i).endCycle();
		}
		for(int i=0; i<enemies.size(); i++) {
			if(i<enemies.size()) {
				Enemy curr = enemies.get(i);
				curr.endCycle();
			}
		}
		game.draw.runUpdate();
	}
	//returns eater to be acted on by other classes
	public Eater player() {
		if(mode == LEVELS) {
			return player;
		}else if(mode == PVP) {
			return players.get(0);
		}else {
			return null;
		}
	}
	//test if board should be paused
	public boolean isPaused() {
		//tests if players are waiting to change direction
		if(awaiting_start) {
			//test if players have moved directions; begin if so
			boolean ready = true;
			for(int i=0; i<players.size(); i++) {
				if(players.get(i).getDir() == Eater.NONE && !currFloor.haltEnabled()) {
					ready = false;
				}
			}
			//unpause if players are aimed
			if(ready) {
				awaiting_start = false;
			}else {
				return true;
			}
		}
		//tests if settings window is up
		if((game.ui_set!=null && game.ui_set.isVisible()) || (game.ui_set!=null && game.ui_tis.isVisible()))return true;
		return false;
	}
	
	
	
	//go back to first level
	public void resetGame() {
		currFloor = floors.getLast();
		currFloor.removeNpcs();
		for(int i=0; i<cookies.size(); i++) {
			cookies.get(i).kill(null);
			i--;
		}
		enemies = new ArrayList<Enemy>();
		walls = new ArrayList<Wall>();
		mechanisms = new ArrayList<Mechanism>();
		effects = new ArrayList<Effect>();

		buildBoard();
		
		for(int i=0; i<players.size(); i++)
			players.get(i).reset();
		
		for(int i=0; i<npcs.size(); i++)
			npcs.get(i).runEnds();
		
		cookies = new ArrayList<Cookie>();
		if(mode==LEVELS) {
		makeCookies();}
		spawnEnemies();
		for(int i=0; i<npcs.size(); i++) {
			if(npcs.get(i).getResidence().equals(currFloor)) {
				present_npcs.add(npcs.get(i));
				npcs.get(i).spawn();
			}else if(present_npcs.contains(npcs.get(i))) {
				present_npcs.remove(npcs.get(i));
			}
		}
		setDialogue(null,null);
		spawnNpcs();
		awaiting_start = true;
	}
			
	//advances level
	public void nextLevel() {
		for(int i=0; i<present_npcs.size(); i++) {
			present_npcs.get(i).levelComplete();
		}
		currFloor.removeNpcs();
		for(int i=0; i<cookies.size(); i++) {
			cookies.get(i).kill(null);
			i--;
		}
		enemies = new ArrayList<Enemy>();
		walls = new ArrayList<Wall>();
		mechanisms = new ArrayList<Mechanism>();
		effects = new ArrayList<Effect>();
		//shields+=cash/currFloor.getShieldCost();
		//cash=cash%currFloor.getShieldCost();
		currFloor=currFloor.getNext();
		buildBoard();
		cookies = new ArrayList<Cookie>();
		makeCookies();
		spawnEnemies();
		present_npcs = new ArrayList<Explorer>();
		for(int i=0; i<npcs.size(); i++) {
			if(npcs.get(i).getResidence().equals(currFloor)) {
				present_npcs.add(npcs.get(i));
				npcs.get(i).spawn();
			}
		}
		setDialogue(null,null);
		spawnNpcs();
		awaiting_start = true;
	}
	
	public void loadDungeon(int num) {
		currDungeon = num;
		//converting list of floors to linked list
		floors = new LinkedList<Level>();
		if(mode==LEVELS) {
			for(int i=FLOOR_SEQUENCE[num].length-1; i>=0; i--) {
				floors.add(FLOOR_SEQUENCE[num][i]);
				if(i<FLOOR_SEQUENCE[num].length-1) 
					FLOOR_SEQUENCE[num][i].setNext(FLOOR_SEQUENCE[num][i+1]);
			}
		}else if(mode==PVP) {
			floors.add(new Floor1(game));
		}
		resetNpcs();
		//create floor 1
		resetGame();

				
	}	
	
	public void setCalibrations(double cycle) {
		for(int i=0; i<players.size(); i++) {
			players.get(i).setCalibration(cycle); //give player more accurate cycle time
		}
		for(int i=0; i<enemies.size(); i++) {
			enemies.get(i).setCalibration(cycle); //give enemies more accurate cycle time
		}
		for(int i=0; i<effects.size(); i++) {
			effects.get(i).setCalibration(cycle); //give enemies more accurate cycle time
		}
		for(int i=0; i<present_npcs.size(); i++) {
			present_npcs.get(i).setCalibration(cycle); //give npcs more accurate cycle time
		}
		for(int i=0; i<cookies.size(); i++) {
			cookies.get(i).setCalibration(cycle); //give cookies more accurate cycle time
		}
	}
	
	//create walls
	public void buildBoard() {
		currFloor.build();
		game.draw.updateBG();
		wallSpace = new Area();
		for(Wall w : walls) {
			wallSpace.add(w.getArea());
		}
	}
	
	//add cookies to board
	public void makeCookies() {
		currFloor.placeCookies();
	}
	
	//add enemies to board
	public void spawnEnemies() {
		currFloor.spawnEnemies();
	}
	
	//add enemies to board
	public void spawnNpcs() {
		currFloor.spawnNpcs();
	}
	
	//creates all the non-player characters and puts them in their starting levels
	public void createNpcs(int cycle) {
		npcs.add(new ExplorerMechanic(game,cycletime));
		npcs.add(new ExplorerShopkeep(game,cycletime));
		npcs.add(new ExplorerVendor(game,cycletime));
		npcs.add(new ExplorerSidekick(game,cycletime));
		npcs.add(new ExplorerMystery(game,cycletime));
	}
	//resets npcs for new dungeon
	public void resetNpcs() {
		for(int i=0; i<npcs.size(); i++) {
			npcs.get(i).chooseResidence();
			npcs.get(i).createStash();
		}
	}
	
	//returns nearest cookie to a given point on the board
	public Cookie nearestCookie(double x, double y) {
		double bestDist = Integer.MAX_VALUE;
		Cookie save = null;
		for(int i=0; i<cookies.size(); i++) {
			if(cookies.get(i)!=null){
				double thisDist = Level.lineLength(cookies.get(i).getX(),cookies.get(i).getY(),x,y);
				if(thisDist<bestDist&&thisDist!=0) {
					save = cookies.get(i);
					bestDist = thisDist;
				}
			}
		}
		return save;
	}
	
	public void setDialogue(Entity speaker, Conversation convo) { //sets the current dialogue
		game.draw.removeUI(ui_dia);
		if(convo==null) {
			ui_dia = null;
		}else {
			ui_dia = new UIDialogue(game,convo.currentLine(),convo.getOptions(),convo.getExpression());
			game.draw.addUI(ui_dia);
		}
	}
	public boolean inConvo() { //if player is in dialogue
		return ui_dia != null;
	}

}
