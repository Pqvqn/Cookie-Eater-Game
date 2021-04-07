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
	public static final int DEF_Y_RESOL = 1020, DEF_X_RESOL = 1920; //default board dimensions
	public int y_resol = 1020, x_resol = 1920; //board dimensions
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

	public Level[][] floorSequence; //order of floors for each dungeon 
	public LinkedList<Level> floors;
	public int currDungeon;
	public Level currFloor;
	public int playerCount;
	public boolean awaiting_start; //whether the game is paused awaiting a player input to begin
	public int cycletime;
	public HashMap<String,ArrayList<Entity>> connections; //list of entities to be connected, key is tag of entity to be given connections
	
	public UILevelInfo ui_lvl;
	public UIDialogue ui_dia;
	public UIConfirmation ui_cnf;
	
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
		
		cookies = new ArrayList<Cookie>();
		walls = new ArrayList<Wall>();
		mechanisms = new ArrayList<Mechanism>();
		wallSpace = new Area();
		enemies = new ArrayList<Enemy>();
		npcs = new ArrayList<Explorer>();
		present_npcs = new ArrayList<Explorer>();
		effects = new ArrayList<Effect>();
		menus = new ArrayList<Menu>();
		
		game.draw.setBoard(this);
		
		game.draw.addUI(ui_lvl = new UILevelInfo(game,x_resol/2,30));
		if(mode == LEVELS) {
			//create all of this game's npcs
			createNpcs(cycletime);
		}
		
		Level[][] floseq = {
				//vaults
				{new Store1(game,this),new Floor1(game,this),
				new Store2(game,this),new Floor2(game,this),new Floor2(game,this),
				new Store3(game,this),new Floor3(game,this),new Floor3(game,this),new Floor3(game,this), 
				new Store4(game,this),new Floor4(game,this),new Floor4(game,this),new Floor4(game,this),new Floor4(game,this),new Floor5(game,this)},
				//inners
				{new Store2(game,this),new FloorRound(game,this),
				new Store3(game,this),new Floor4(game,this),new FloorRound(game,this),
				new Store4(game,this),new FloorRound(game,this),new FloorRound(game,this),new FloorRound(game,this), 
				new Store1(game,this),new FloorRound(game,this),new FloorRound(game,this),new FloorRound(game,this),new FloorRound(game,this),new FloorRound(game,this)},
				//training
				{new Training1(game,this)}
		};
		floorSequence = floseq;
		
		loadDungeon(dungeon);
	}
	public Board(Game g, SaveData data, int cycle) {
		game = g;
		cycletime = cycle;		
		
		savename = data.getString("savename",0);
		mode = data.getInteger("mode",0);
		x_resol = data.getInteger("resolution",0);
		y_resol = data.getInteger("resolution",1);
		currDungeon = data.getInteger("currentdungeon",0);
		playerCount = data.getInteger("playercount",0);
		awaiting_start = data.getBoolean("awaiting",0);
		
		ArrayList<SaveData> flrData = data.getSaveDataList("floors");
		floorSequence = new Level[flrData.size()][1];
		for(int i=0; i<flrData.size(); i++) {
			ArrayList<SaveData> lvlData = flrData.get(i).getSaveDataList("levels");
			Level[] flr = new Level[lvlData.size()];
			for(int j=0; j<lvlData.size(); j++) {
				flr[j] = Level.loadFromData(game, this, lvlData.get(j));
			}
			floorSequence[i] = flr;
		}
		currFloor = floorSequence[currDungeon][data.getInteger("currentfloor",0)];
		floors = new LinkedList<Level>();
		if(mode==LEVELS) {
			for(int i=floorSequence[currDungeon].length-1; i>=0; i--) {
				floors.add(floorSequence[currDungeon][i]);
				if(i<floorSequence[currDungeon].length-1) 
					floorSequence[currDungeon][i].setNext(floorSequence[currDungeon][i+1]);
			}
		}else if(mode==PVP) {
			if(currDungeon==0) {
				floors.add(new Arena2(game,this));
			}else if(currDungeon==1) {
				floors.add(new ArenaRound(game,this));
			}else {
				floors.add(new Arena1(game,this));
			}

		}
		
		ArrayList<SaveData> cookieData = data.getSaveDataList("cookies");
		if(cookieData!=null) {
			cookies = new ArrayList<Cookie>();
			for(int i=0; i<cookieData.size(); i++) {
				Cookie loaded = Cookie.loadFromData(game, this, cookieData.get(i));
				if(!(loaded instanceof CookieStore) || ((CookieStore)loaded).getVendor()==null) {
					cookies.add(loaded);
				}
			}
		}
		ArrayList<SaveData> playerData = data.getSaveDataList("players");
		if(playerData!=null) {
			players = new ArrayList<Eater>();
			for(int i=0; i<playerCount; i++) {
				players.add(new Eater(game,this,playerData.get(i),cycletime));
			}
		}
		ArrayList<SaveData> npcData = data.getSaveDataList("explorers");
		if(npcData!=null) {
			npcs = new ArrayList<Explorer>();
			for(int i=0; i<npcData.size(); i++) {
				npcs.add(Explorer.loadFromData(game,this,npcData.get(i),cycletime));
			}
		}
		ArrayList<SaveData> presnpcData = data.getSaveDataList("presentexplorers");
		if(presnpcData!=null) {
			present_npcs = new ArrayList<Explorer>();
			for(int i=0; i<presnpcData.size(); i++) {
				Explorer ex = Explorer.loadFromData(game,this,npcData.get(i),cycletime);
				npcs.add(ex);
				present_npcs.add(ex);
			}
		}
		ArrayList<SaveData> enemyData = data.getSaveDataList("enemies");
		if(enemyData!=null) {
			enemies = new ArrayList<Enemy>();
			for(int i=0; i<enemyData.size(); i++) {
				enemies.add(Enemy.loadFromData(game,this,enemyData.get(i),cycletime));
			}
		}
		ArrayList<SaveData> effectData = data.getSaveDataList("effects");
		if(effectData!=null) {
			effects = new ArrayList<Effect>();
			for(int i=0; i<effectData.size(); i++) {
				effects.add(Effect.loadFromData(game,this,effectData.get(i),cycletime));
			}
		}
		ArrayList<SaveData> wallData = data.getSaveDataList("walls");
		if(wallData!=null) {
			walls = new ArrayList<Wall>();
			for(int i=0; i<wallData.size(); i++) {
				walls.add(new Wall(game, this, wallData.get(i)));
			}
		}
		wallSpace = new Area();
		for(Wall w : walls) {
			wallSpace.add(w.getArea());
		}
		ArrayList<SaveData> mechData = data.getSaveDataList("mechanisms");
		if(mechData!=null) {
			mechanisms = new ArrayList<Mechanism>();
			for(int i=0; i<mechData.size(); i++) {
				mechanisms.add(new Mechanism(game, this, mechData.get(i)));
			}
		}
		
		
		game.draw.addUI(ui_lvl = new UILevelInfo(game,x_resol/2,30));
		game.draw.setBoard(this);
	}
	//write data tp 
	public void createSave() {
		SaveData data = new SaveData();
		data.addData("savename",savename);
		data.addData("mode",mode);
		data.addData("resolution",x_resol,0);
		data.addData("resolution",y_resol,1);
		data.addData("currentdungeon",currDungeon);
		data.addData("playercount",playerCount);
		data.addData("awaiting",awaiting_start);
		
		for(int i=0; i<floorSequence.length; i++) {
			SaveData dungeonData = new SaveData();
			for(int j=0; j<floorSequence[i].length; j++) {
				dungeonData.addData("levels",floorSequence[i][j].getSaveData(),j);
			}
			data.addData("floors",dungeonData,i);
		}
		for(int i=0; i<floorSequence[currDungeon].length; i++) {
			if(floorSequence[currDungeon][i] == currFloor) {
				data.addData("currentfloor",i);
			}
		}
		
		for(int i=0; i<cookies.size(); i++) {
			Cookie toSave = cookies.get(i);
			if(!(toSave instanceof CookieStore) || ((CookieStore)toSave).getVendor()==null) {
				data.addData("cookies",cookies.get(i).getSaveData());
			}
		}
		
		for(int i=0; i<playerCount; i++) {
			data.addData("players",players.get(i).getSaveData());
		}
		for(int i=0; i<present_npcs.size(); i++) {
			data.addData("presentexplorers",present_npcs.get(i).getSaveData());
		}
		for(int i=0; i<npcs.size(); i++) {
			if(!present_npcs.contains(npcs.get(i)))
					data.addData("explorers",npcs.get(i).getSaveData());
		}
		for(int i=0; i<enemies.size(); i++) {
			data.addData("enemies",enemies.get(i).getSaveData());
		}
		for(int i=0; i<effects.size(); i++) {
			data.addData("effects",effects.get(i).getSaveData());
		}
		
		for(int i=0; i<walls.size(); i++) {
			data.addData("walls",walls.get(i).getSaveData());
		}
		for(int i=0; i<mechanisms.size(); i++) {
			data.addData("mechanisms",mechanisms.get(i).getSaveData());
		}
		
		
		
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
	
	public void updateUI() {
		//level display
		ui_lvl.update(currFloor.getName());
		
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
		if((game.ui_set!=null && game.ui_set.isVisible()) || (game.ui_tis!=null && game.ui_tis.isVisible()))return true;
		return false;
	}
	//tests if waiting for players should be allowed to input to start match
	public boolean awaitingStart() {
		return awaiting_start && !game.ui_set.isVisible();
	}
	
	
	//go back to first level
	public void resetGame() {
		currFloor = floors.getLast();
		currFloor.removeNpcs();
		for(int i=0; i<cookies.size(); i++) {
			cookies.get(i).kill(null);
			i--;
		}
		for(int i=0; i<mechanisms.size(); i++) {
			mechanisms.get(i).remove();
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
		for(int i=0; i<mechanisms.size(); i++) {
			mechanisms.get(i).remove();
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
			for(int i=floorSequence[num].length-1; i>=0; i--) {
				floors.add(floorSequence[num][i]);
				if(i<floorSequence[num].length-1) 
					floorSequence[num][i].setNext(floorSequence[num][i+1]);
			}
		}else if(mode==PVP) {
			if(num==0) {
				floors.add(new Arena2(game,this));
			}else if(num==1) {
				floors.add(new ArenaRound(game,this));
			}else {
				floors.add(new Arena1(game,this));
			}

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
		npcs.add(new ExplorerMechanic(game,this,cycletime));
		npcs.add(new ExplorerShopkeep(game,this,cycletime));
		npcs.add(new ExplorerVendor(game,this,cycletime));
		npcs.add(new ExplorerSidekick(game,this,cycletime));
		npcs.add(new ExplorerMystery(game,this,cycletime));
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
	//return a list of all entities that are connected to the entity with the given code
	public ArrayList<Entity> findEntities(String code){
		ArrayList<Entity> results = new ArrayList<Entity>();
		ArrayList<Entity> entities = new ArrayList<Entity>();
		for(Entity e : players)entities.add(e);
		for(Entity e : enemies)entities.add(e);
		for(Entity e : present_npcs)entities.add(e);
		for(Entity e : effects)entities.add(e);
		for(int i=0; i<entities.size(); i++) {
			Entity e = entities.get(i);
			if(e.connectionCode().equals(code)) {
				results.add(e);
			}
		}
		return results;
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
	public void requestConfirmation(Selection s, int x, int y, String text) {
		if(ui_cnf!=null)return;
		s.reopen();
		ui_cnf = new UIConfirmation(game,x,y,text,s);
		game.draw.addUI(ui_cnf);
	}
	
	public void endConfirmation(Selection s) {
		if(ui_cnf==null)return;
		if(!ui_cnf.sameSelection(s))return; //if the intended selection is not the currently displayed one
		s.close();
		game.draw.removeUI(ui_cnf);
		ui_cnf = null;
	}

}
