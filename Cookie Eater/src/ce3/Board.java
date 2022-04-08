package ce3;

import cookies.*;
import entities.*;
import levels.*;
import mechanisms.*;
import ui.*;
import menus.*;
import menus.Menu;

import java.awt.geom.*;
import java.io.*;
import java.util.*;

public class Board{

	public Game game;
	public String savename;
	public static final int LEVELS = 0, PVP = 1;
	public int mode;
	public static final int FRAME_X_RESOL = 1920, FRAME_Y_RESOL = 1020; //dimensions of board as shown on screen
	//public int y_resol = 1020, x_resol = 1920; //board dimensions
	private Eater player;
	public Area wallSpace;
	public ArrayList<Eater> players;
	public ArrayList<Explorer> npcs;
	//public ArrayList<Explorer> presentnpcs; //npcs that exist on current level
	public ArrayList<Menu> menus;

	public static final String[] themes = {"forest","dungeon","cave","ice","harsh","void"}; //art and mechanic themes for across floors
	public HashMap<String,Store> stores; //stores (stay the same between resets)
	public HashMap<String,Room> rooms; //room templates that may be placed within floors
	public HashMap<String,Layout> layouts; //layout templates that may be used to create floors
	public ArrayList<Floor> floors; //list of floors, unordered
	public String[][][] floorSequence = { //stores floor tiers 
			//vaults
			{{"Layout1"},{"Layout2"},{"LayoutRound","Layout3"},{"Layout4"}},
			//inners
			{{}},
			//training
			{{"LayoutTrain"}},
	};
	public Level nextLevel;
	public Level currLevel;
	public int currDungeon;
	public int playerCount;
	public boolean awaiting_start; //whether the game is paused awaiting a player input to begin
	public int cycletime;
	public HashMap<String,ArrayList<Entity>> connections; //list of entities to be connected, key is tag of entity to be given connections
	
	public SaveData data; //saved data for savefiles
	
	public UILevelInfo ui_lvl;
	public UIDialogue ui_dia;
	public UIConfirmation ui_cnf;
	public UIDebugInfo ui_dbg;
	
	public int renderDist = 1000;
	
	public Board(Game g, String name, int gamemode, int dungeon, int playercount, int cycle) {
		game = g;
		savename = name;
		mode = gamemode;
		cycletime = cycle;
		//initializing classes
		players = new ArrayList<Eater>();
		playerCount = playercount;
		if(mode==LEVELS) {
			players.add(player = new Eater(game,this,0,cycletime));
		}else if(mode==PVP) { //add number of players
			for(int i=0; i<playerCount; i++)
				players.add(new Eater(game,this,i,cycletime));
		}
		
		wallSpace = new Area();
		npcs = new ArrayList<Explorer>();
		
		menus = new ArrayList<Menu>();
		
		try {
			readLayoutsAndRooms();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		game.draw.setBoard(this);
		
		game.draw.addUI(ui_lvl = new UILevelInfo(game,FRAME_X_RESOL/2,30));
		game.draw.addUI(ui_dbg = new UIDebugInfo(game,FRAME_X_RESOL-200,FRAME_Y_RESOL-200));
		if(mode == LEVELS) {
			//create all of this game's npcs
			createNpcs(cycletime);
		}
		
		loadDungeon(dungeon);
	}
	public Board(Game g, SaveData data, int cycle) {
		this.data = data;
		game = g;
		cycletime = cycle;		
		menus = new ArrayList<Menu>();
		
		savename = data.getString("savename",0);
		mode = data.getInteger("mode",0);
		currDungeon = data.getInteger("currentdungeon",0);
		playerCount = data.getInteger("playercount",0);
		awaiting_start = data.getBoolean("awaiting",0);
		
		try {
			readLayoutsAndRooms();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		stores = new HashMap<String,Store>();
		ArrayList<SaveData> storeData = data.getSaveDataList("stores");
		for(int i=0; i<storeData.size(); i++) {
			Store str = new Store(game, this, null, storeData.get(i));
			stores.put(str.getID(),str);
		}
		
		floors = new ArrayList<Floor>();
		ArrayList<SaveData> floorData = data.getSaveDataList("floors");
		for(int i=0; i<floorData.size(); i++) {
			Floor flr = new Floor(game, this, floorData.get(i));
			floors.add(flr);
		}
		
		if(data.getBoolean("currentlevel",0)) {
			currLevel = findFloor(data.getString("currentlevel",1)).findRoom(data.getString("currentlevel",2));
		}else {
			currLevel = stores.get(data.getString("currentlevel",2));
		}
		
		
		ArrayList<SaveData> playerData = data.getSaveDataList("players");
		players = new ArrayList<Eater>();
		if(playerData!=null) {
			for(int i=0; i<playerCount; i++) {
				players.add(new Eater(game,this,playerData.get(i),cycletime));
			}
		}
		if(mode==LEVELS)player=players.get(0);
		ArrayList<SaveData> npcData = data.getSaveDataList("explorers");
		npcs = new ArrayList<Explorer>();
		if(npcData!=null) {
			for(int i=0; i<npcData.size(); i++) {
				npcs.add(Explorer.loadFromData(game,this,npcData.get(i),cycletime));
			}
		}


		wallSpace = new Area();
		for(Wall w : currLevel.walls) {
			wallSpace.add(w.getArea());
		}
		game.draw.addUI(ui_lvl = new UILevelInfo(game,FRAME_X_RESOL/2,30));
		game.draw.addUI(ui_dbg = new UIDebugInfo(game,FRAME_X_RESOL-200,FRAME_Y_RESOL-200));
		game.draw.setBoard(this);
	}
	//write data tp 
	public void createSave() {
		if(savename.isBlank())return;
		data = new SaveData();
		data.addData("savename",savename);
		data.addData("mode",mode);
		data.addData("currentdungeon",currDungeon);
		data.addData("playercount",playerCount);
		data.addData("awaiting",awaiting_start);
		
		Iterator<String> it = stores.keySet().iterator();
		while(it.hasNext()) {
			Store curr = stores.get(it.next());
			data.addData("stores",curr.getSaveData(),0);
		}
		
		for(int i=0; i<floors.size(); i++) {
			data.addData("floors",floors.get(i).getSaveData(),i);
		}
		
		data.addData("currentlevel",currLevel.getFloor()!=null,0);
		data.addData("currentlevel",currLevel.getFloor()==null?Floor.blankCode:currLevel.getFloor().getID(),1);
		data.addData("currentlevel",currLevel.getID(),2);
		
		int ci = 0;
		for(int i=0; i<npcs.size(); i++) {
			data.addData("explorers",npcs.get(i).getSaveData(),ci++);
		}	

		for(int i=0; i<playerCount; i++) {
			data.addData("players",players.get(i).getSaveData(),i);
		}
		
		data.addData("settings",game.ui_set.getSaveData());
		
		
		File f = new File(System.getProperty("user.home")+"/Documents/CookieEater/"+savename+".txt");
		try {
			f.getParentFile().mkdirs();
			f.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			data.saveToFile(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//load board back up after closing out
	public void loadUp() {
		game.draw.setBoard(this); //update draw for new board
		game.draw.updateBG();
		for(int i=0; i<players.size(); i++) { //add controls to game
			game.addControls(players.get(i).controls);
		}
		if(ui_cnf!=null)endConfirmation(ui_cnf.getSelection()); //remove lingering ui
	}
	
	//load room templates from room file
	public void readLayoutsAndRooms() throws IOException {
		rooms = new HashMap<String,Room>();
		File f = new File("Cookie Eater/src/resources/level/rooms.txt");
		SaveData sd = new SaveData(f);
		Iterator<String> it = sd.dataMap().keySet().iterator();
		while(it.hasNext()) {
			String n = it.next();
			Room r = new Room(n,sd.getSaveDataList(n).get(0));
			rooms.put(n,r);
		}
		
		layouts = new HashMap<String,Layout>();
		File f2 = new File("Cookie Eater/src/resources/level/layouts.txt");
		SaveData sd2 = new SaveData(f2);
		Iterator<String> it2 = sd2.dataMap().keySet().iterator();
		while(it2.hasNext()) {
			String n = it2.next();
			Layout l = new Layout(n,sd2.getSaveDataList(n).get(0));
			layouts.put(n,l);
		}
	}
	
	
	public void updateUI() {
		//level display
		ui_lvl.update(currLevel.getTitle());
		
		//debug display
		ui_dbg.update(currLevel.getThemeWeights());
		
		//dialogue
		if(ui_dia!=null)ui_dia.update();
		
		//confirmation
		if(ui_cnf!=null)ui_cnf.update();
		
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
		
		for(int i=0; i<players.size(); i++) {
			players.get(i).runUpdate();
		}
		for(int i=0; i<currLevel.presentnpcs.size(); i++) {
			currLevel.presentnpcs.get(i).runUpdate();
		}
		currLevel.runUpdate();
		for(int i=0; i<players.size(); i++) {
			players.get(i).endCycle();
		}
		for(int i=0; i<currLevel.presentnpcs.size(); i++) {
			currLevel.presentnpcs.get(i).endCycle();
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
				if(players.get(i).getDir() == Eater.NONE && !currLevel.haltEnabled()) {
					ready = false;
				}
			}
			//unpause if players are aimed
			if(ready && game.fpscheck==0) {
				awaiting_start = false;
			}else {
				return true;
			}
		}
		//tests if settings window is up
		if((game.ui_set!=null && game.ui_set.isVisible()) || (game.ui_tis!=null && game.ui_tis.isVisible()))return true;
		return false;
	}
	//tests if waiting for players should be allowed to input to start match
	public boolean awaitingStart() {
		return awaiting_start && !game.ui_set.isVisible();
	}
	
	
	//go back to first level
	public void resetGame() {
		buildBoard();
		
		for(int i=0; i<players.size(); i++)
			players.get(i).reset(null,true);
		
		for(int i=0; i<npcs.size(); i++)
			npcs.get(i).runEnds();
		
		if(mode==LEVELS) {
		makeCookies();}
		spawnEnemies();
		
		setDialogue(null,null);
		checkNpcs();
		awaiting_start = true;
		createSave();
	}
			
	//advances level
	public void nextLevel() {
		currLevel.clean();
		for(int i=0; i<currLevel.presentnpcs.size(); i++) {
			currLevel.presentnpcs.get(i).levelComplete();
		}
		if(currLevel.getFloor() != nextLevel.getFloor() && currLevel.getFloor()!=null) {
			currLevel.getFloor().wipeFloor();
		}
		currLevel = nextLevel;
		buildBoard();
		makeCookies();
		spawnEnemies();
		setDialogue(null,null);
		checkNpcs();
		awaiting_start = true;
		if(currLevel.saveGame()) {
			createSave();
		}
	}
	
	//moves to previously loaded level
	public void backLevel() {
		currLevel.clean();
		currLevel = nextLevel;
		currLevel.reload();
		
		wallSpace = new Area();
		for(Wall w : currLevel.walls) {
			wallSpace.add(w.getArea());
		}
		game.draw.updateBG();

		setDialogue(null,null);
		checkNpcs();
	}
	
	public void setNext(Level l) {
		nextLevel = l;
	}
	
	public void loadDungeon(int num) {
		currDungeon = num;
		//stores = new HashMap<String,Level>();
		resetLevels();
		resetNpcs();
		//create floor 1
		resetGame();	
	}	

	//recreates level progression
	public void resetLevels() {
		if(currLevel!=null) {
			currLevel.clean();
			if(currLevel.getFloor()!=null)currLevel.getFloor().wipeFloor();
		}
		
		int num = currDungeon;
		floors = new ArrayList<Floor>();
		stores = new HashMap<String,Store>();
		int searchidx = 0; //where in floors the last tier begins
		String[][] dungeonSeq = floorSequence[num];
		for(int i=0; i<dungeonSeq.length; i++) { //create each floor
			int idx = floors.size();
			for(int j=0; j<dungeonSeq[i].length; j++) {
				Floor leadin=null;
				if(i>0) {//get previous floor to use as entrance
					leadin = floors.get((int)(Math.random() * (dungeonSeq[i-1].length) + searchidx));
				}
				String id = ((leadin==null)?"0":leadin.getID()+leadin.numExits());
				Floor currf = new Floor(game, this, layouts.get(dungeonSeq[i][j]), id);
				floors.add(currf);
				Store nextStore = currf.getStore();
				if(leadin!=null) {
					leadin.addExit(nextStore);
				}else {
					currLevel = nextStore;
				}
				stores.put(nextStore.getID(),nextStore);
				currf.addEntrance(nextStore);
				currf.generateFloor();
				ArrayList<Level> nexts = new ArrayList<Level>();
				ArrayList<Integer> dirs = new ArrayList<Integer>();
				nexts.add(currf.findRoom(Floor.startCode));
				dirs.add(Passage.FLOOR);
				nextStore.setNextLevels(nexts,dirs);
			}
			searchidx = idx;
		}
		for(int i=0; i<floors.size(); i++) {
			floors.get(i).buildExits();
		}
	}
	
	public void setCalibrations(double cycle) {
		for(int i=0; i<players.size(); i++) {
			players.get(i).setCalibration(cycle); //give player more accurate cycle time
		}
		for(int i=0; i<currLevel.enemies.size(); i++) {
			currLevel.enemies.get(i).setCalibration(cycle); //give enemies more accurate cycle time
		}
		for(int i=0; i<currLevel.effects.size(); i++) {
			currLevel.effects.get(i).setCalibration(cycle); //give enemies more accurate cycle time
		}
		for(int i=0; i<currLevel.presentnpcs.size(); i++) {
			currLevel.presentnpcs.get(i).setCalibration(cycle); //give npcs more accurate cycle time
		}
		for(int i=0; i<currLevel.cookies().size(); i++) {
			currLevel.cookies().get(i).setCalibration(cycle); //give cookies more accurate cycle time
		}
	}
	
	//create walls
	public void buildBoard() {
		currLevel.build();
		game.draw.updateBG();
		wallSpace = new Area();
		for(Wall w : currLevel.walls) {
			wallSpace.add(w.getArea());
		}
	}
	
	//add cookies to board
	public void makeCookies() {
		currLevel.placeCookies();
	}
	
	//add enemies to board
	public void spawnEnemies() {
		currLevel.spawnEnemies();
	}
	
	//add enemies to board
	public void checkNpcs() {
		currLevel.checkNPCs(npcs);
	}
	
	//creates all the non-player characters and puts them in their starting levels
	public void createNpcs(int cycle) {
		npcs.add(new ExplorerMechanic(game,this,cycletime));
		npcs.add(new ExplorerShopkeep(game,this,cycletime));
		npcs.add(new ExplorerVendor(game,this,cycletime));
		npcs.add(new ExplorerSidekick(game,this,cycletime));
		npcs.add(new ExplorerMystery(game,this,cycletime));
		npcs.add(new ExplorerTutor(game,this,cycletime));
	}
	//resets npcs for new dungeon
	public void resetNpcs() {
		for(int i=0; i<npcs.size(); i++) {
			npcs.get(i).chooseResidence();
			npcs.get(i).createStash();
			if(npcs.get(i).getResidence() == null) {
				npcs.remove(i);
				i--;
			}
		}
	}
	//kills all players
	public void killPlayers() {
		for(int i=0; i<players.size(); i++) {
			players.get(i).kill();
		}
	}
	//return a list of all entities of various kinds
	public ArrayList<Entity> allEntities(){
		ArrayList<Entity> entities = new ArrayList<Entity>();
		for(Entity e : players)entities.add(e);
		for(Entity e : currLevel.enemies)entities.add(e);
		for(Entity e : currLevel.presentnpcs)entities.add(e);
		for(Entity e : currLevel.effects)entities.add(e);
		return entities;
	}
	//return a list of all entities that are connected to the entity with the given code
	public ArrayList<Entity> findEntities(String code){
		ArrayList<Entity> results = new ArrayList<Entity>();
		ArrayList<Entity> entities = allEntities();
		for(int i=0; i<entities.size(); i++) {
			Entity e = entities.get(i);
			if(e.connectionCode().equals(code)) {
				results.add(e);
			}
		}
		return results;
	}
	//returns nearest cookie to a given point on the board
	/*public Cookie nearestCookie(double x, double y) {
		double bestDist = Integer.MAX_VALUE;
		Cookie save = null;
		for(int i=0; i<currLevel.cookies.size(); i++) {
			if(currLevel.cookies.get(i)!=null){
				double thisDist = Level.lineLength(currLevel.cookies.get(i).getX(),currLevel.cookies.get(i).getY(),x,y);
				if(thisDist<bestDist&&thisDist!=0) {
					save = currLevel.cookies.get(i);
					bestDist = thisDist;
				}
			}
		}
		return save;
	}*/
	
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
	public void requestConfirmation(Selection s, int x, int y, String text) {
		if(ui_cnf!=null)return;
		s.reopen();
		ui_cnf = new UIConfirmation(game,x,y,text,s,true);
		game.draw.addUI(ui_cnf);
	}
	
	public void endConfirmation(Selection s) {
		if(ui_cnf==null)return;
		if(!ui_cnf.sameSelection(s))return; //if the intended selection is not the currently displayed one
		s.close();
		game.draw.removeUI(ui_cnf);
		ui_cnf = null;
	}
	
	//returns a loaded floor of a given ID
	public Floor findFloor(String id){
		for(int i=0; i<floors.size(); i++) {
			if(floors.get(i).getID().equals(id)) {
				return floors.get(i);
			}
		}
		return null;
	}

	
	/*
	//public int score() {return currLevel.score;}
	public int maxScore() {return currLevel.maxScore;}
	public void addScore(int n) {currLevel.score+=n;}
	
	//returns level objects loaded for current level
	public ArrayList<Cookie> cookies(){return currLevel==null?null:currLevel.cookies;}
	public ArrayList<Wall> walls(){return currLevel==null?null:currLevel.walls;}
	public ArrayList<Mechanism> mechanisms(){return currLevel==null?null:currLevel.mechanisms;}
	public ArrayList<Decoration> decorations(){
		if(currLevel==null)return null;
		ArrayList<Decoration> rets = new ArrayList<Decoration>();
		for(Mechanism m : mechanisms())if(m instanceof Decoration)rets.add((Decoration)m);
		return rets;
	}
	public ArrayList<Effect> effects(){return currLevel==null?null:currLevel.effects;}
	public ArrayList<Enemy> enemies(){return currLevel==null?null:currLevel.enemies;}
	*/
	
	public Explorer getNPC(String name) {
		for(int i=0; i<npcs.size(); i++) {
			if(npcs.get(i).getName().equals(name))return npcs.get(i);
		}
		return null;
	}

}
