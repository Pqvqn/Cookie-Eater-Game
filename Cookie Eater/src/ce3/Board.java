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
import java.lang.reflect.InvocationTargetException;
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

	/*public Class[][] levelSequence = { //order of rooms for each dungeon 
			//vaults
			{Store1.class,Room1.class,
			Store2.class,Room2.class,Room2.class,
			Store3.class,Room3.class,Room3.class,Room3.class, 
			Store4.class,Room4.class,Room4.class,Room4.class,Room4.class,Room5.class},
			//inners
			{Store2.class,RoomRound.class,
			Store3.class,Room4.class,RoomRound.class,
			Store4.class,RoomRound.class,RoomRound.class,RoomRound.class, 
			Store1.class,RoomRound.class,RoomRound.class,RoomRound.class,RoomRound.class,RoomRound.class},
			//training
			{Training1.class}
	};*/
	//public LinkedList<Level> levels; //level progression
	public HashMap<String,Store> stores; //stores (stay the same between resets)
	public ArrayList<Floor> floors; //list of floors, unordered
	public Class[][][] floorSequence = { //stores floor tiers 
			//vaults
			{{Floor1.class},{Floor2.class},{Floor3.class}},
			//inners
			{{}},
			//training
			{{}},
	};
	public Level nextLevel;
	//public Level firstLevel;
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
		
		loadDungeon(dungeon);
	}
	public Board(Game g, SaveData data, int cycle) {
		this.data = data;
		game = g;
		cycletime = cycle;		
		menus = new ArrayList<Menu>();
		
		savename = data.getString("savename",0);
		mode = data.getInteger("mode",0);
		x_resol = data.getInteger("resolution",0);
		y_resol = data.getInteger("resolution",1);
		currDungeon = data.getInteger("currentdungeon",0);
		playerCount = data.getInteger("playercount",0);
		awaiting_start = data.getBoolean("awaiting",0);
		

		ArrayList<SaveData> levelsData = data.getSaveDataList("levels");
		ArrayList<Level> tempLevels = new ArrayList<Level>();
		for(int i=0; i<levelsData.size(); i++) {
			tempLevels.add(Level.loadFromData(game, this, null, null, levelsData.get(i)));
		}
		
		levels = new LinkedList<Level>();
		stores = new HashMap<String,Level>();
		for(int i=tempLevels.size()-1; i>=0; i--) {
			Level addlevel = tempLevels.get(i);
			levels.add(addlevel);
			String id = tempLevels.get(i).getID();
			ArrayList<Level> next = new ArrayList<Level>();
			ArrayList<Level> prev = new ArrayList<Level>();
			for(int j=0; j<tempLevels.size(); j++) {
				String id2 = tempLevels.get(j).getID();
				if(id2.length()==id.length()+1 && id2.substring(0,id.length()).equals(id)) {
					next.add(tempLevels.get(j));
				}
				if(id2.length()==id.length()-1 && id.substring(0,id2.length()).equals(id2)) {
					prev.add(tempLevels.get(j));
				}
			}
			addlevel.loadPassages(prev,next,levelsData.get(i));
			if(addlevel.getID().contentEquals(data.getString("firstlevel",0))) {
				firstLevel = addlevel;
			}
			if(addlevel instanceof Store)stores.put(addlevel.getName(),addlevel);
		}
		currLevel = findRoom(data.getString("currentlevel",0));

		
		
		ArrayList<SaveData> cookieData = data.getSaveDataList("cookies");
		cookies = new ArrayList<Cookie>();
		if(cookieData!=null) {
			for(int i=0; i<cookieData.size(); i++) {
				Cookie loaded = Cookie.loadFromData(game, this, cookieData.get(i));
				if(!(loaded instanceof CookieStore) || ((CookieStore)loaded).getVendor()==null) {
					cookies.add(loaded);
				}
			}
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
		ArrayList<SaveData> presnpcData = data.getSaveDataList("presentexplorers");
		present_npcs = new ArrayList<Explorer>();
		if(presnpcData!=null) {
			for(int i=0; i<presnpcData.size(); i++) {
				Explorer ex = Explorer.loadFromData(game,this,presnpcData.get(i),cycletime);
				ex.spawn();
				npcs.add(ex);
				present_npcs.add(ex);
			}
		}
		ArrayList<SaveData> enemyData = data.getSaveDataList("enemies");
		enemies = new ArrayList<Enemy>();
		if(enemyData!=null) {
			for(int i=0; i<enemyData.size(); i++) {
				enemies.add(Enemy.loadFromData(game,this,enemyData.get(i),cycletime));
			}
		}
		ArrayList<SaveData> effectData = data.getSaveDataList("effects");
		effects = new ArrayList<Effect>();
		if(effectData!=null) {
			for(int i=0; i<effectData.size(); i++) {
				effects.add(Effect.loadFromData(game,this,effectData.get(i),cycletime));
			}
		}
		ArrayList<SaveData> wallData = data.getSaveDataList("walls");
		walls = new ArrayList<Wall>();
		if(wallData!=null) {
			for(int i=0; i<wallData.size(); i++) {
				walls.add(new Wall(game, this, wallData.get(i)));
			}
		}
		wallSpace = new Area();
		for(Wall w : walls) {
			wallSpace.add(w.getArea());
		}
		ArrayList<SaveData> mechData = data.getSaveDataList("mechanisms");
		mechanisms = new ArrayList<Mechanism>();
		if(mechData!=null) {
			for(int i=0; i<mechData.size(); i++) {
				Mechanism mech = Mechanism.loadFromData(game, this, mechData.get(i));
				if(mech!=null)mechanisms.add(mech);
			}
		}
		for(int i=0; i<currLevel.getPassages().size(); i++) {
			mechanisms.add(currLevel.getPassages().get(i));
		}
		game.draw.addUI(ui_lvl = new UILevelInfo(game,x_resol/2,30));
		game.draw.setBoard(this);
	}
	//write data tp 
	public void createSave() {
		return;
		/*if(savename.isBlank())return;
		data = new SaveData();
		data.addData("savename",savename);
		data.addData("mode",mode);
		data.addData("resolution",x_resol,0);
		data.addData("resolution",y_resol,1);
		data.addData("currentdungeon",currDungeon);
		data.addData("playercount",playerCount);
		data.addData("awaiting",awaiting_start);
		
		
		Iterator<Level> it = levels.descendingIterator();
		while(it.hasNext()) {
			Level curr = it.next();
			data.addData("levels",curr.getSaveData(),0);
		}
		data.addData("currentlevel",currLevel.getID());
		data.addData("firstlevel",firstLevel.getID());
		
		int ci = 0;
		for(int i=0; i<cookies.size(); i++) {
			Cookie toSave = cookies.get(i);
			if(!(toSave instanceof CookieStore) || ((CookieStore)toSave).getVendor()==null) {
				data.addData("cookies",cookies.get(i).getSaveData(),ci++);
			}
		}
		
		for(int i=0; i<playerCount; i++) {
			data.addData("players",players.get(i).getSaveData(),i);
		}
		for(int i=0; i<present_npcs.size(); i++) {
			data.addData("presentexplorers",present_npcs.get(i).getSaveData(),i);
		}
		ci = 0;
		for(int i=0; i<npcs.size(); i++) {
			if(!present_npcs.contains(npcs.get(i))) {
				data.addData("explorers",npcs.get(i).getSaveData(),ci++);
			}
		}
		for(int i=0; i<enemies.size(); i++) {
			data.addData("enemies",enemies.get(i).getSaveData(),i);
		}
		for(int i=0; i<effects.size(); i++) {
			data.addData("effects",effects.get(i).getSaveData(),i);
		}
		
		for(int i=0; i<walls.size(); i++) {
			data.addData("walls",walls.get(i).getSaveData(),i);
		}
		for(int i=0; i<mechanisms.size(); i++) {
			data.addData("mechanisms",mechanisms.get(i).getSaveData(),i);
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
		}*/
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
		ui_lvl.update(currLevel.getName());
		
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
	//returns the total score on the board among all players
	public int totalScore() {
		int sum = 0;
		for(int i=0; i<players.size(); i++) {
			sum += players.get(i).getScore();
		}
		return sum;
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
		if(currLevel!=null)currLevel.removeNpcs();
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
			players.get(i).reset(null);
		
		for(int i=0; i<npcs.size(); i++)
			npcs.get(i).runEnds();
		
		cookies = new ArrayList<Cookie>();
		if(mode==LEVELS) {
		makeCookies();}
		spawnEnemies();
		for(int i=0; i<npcs.size(); i++) {
			if(npcs.get(i).getResidence().equals(currLevel)) {
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
		currLevel.removeNpcs();
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
		//shields+=cash/currLevel.getShieldCost();
		//cash=cash%currLevel.getShieldCost();
		//currLevel=currLevel.getNext();
		currLevel = nextLevel;
		buildBoard();
		cookies = new ArrayList<Cookie>();
		makeCookies();
		spawnEnemies();
		present_npcs = new ArrayList<Explorer>();
		for(int i=0; i<npcs.size(); i++) {
			if(npcs.get(i).getResidence().equals(currLevel)) {
				present_npcs.add(npcs.get(i));
				npcs.get(i).spawn();
			}
		}
		setDialogue(null,null);
		spawnNpcs();
		awaiting_start = true;
		createSave();
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
		int num = currDungeon;
		floors = new ArrayList<Floor>();
		stores = new HashMap<String,Store>();
		int searchidx = 0; //where in floors the last tier begins
		Class[][] dungeonSeq = floorSequence[num];
		for(int i=0; i<dungeonSeq.length; i++) { //create each floor
			int idx = floors.size();
			for(int j=0; j<dungeonSeq[i].length; j++) {
				Floor leadin=null;
				if(i>0) {//get previous floor to use as entrance
					leadin = floors.get((int)(Math.random() * (dungeonSeq[i-1].length) + searchidx));
				}
				String id = ((leadin==null)?"0":leadin.getID()+leadin.numExits());
				Floor currf = readFloor(dungeonSeq[i][j],7,7,id); //read floors
				floors.add(currf);
				Store nextStore = currf.generateStore();
				if(leadin!=null) {
					leadin.addExit(nextStore);
					leadin.buildExit(nextStore);
				}else {
					currLevel = nextStore;
				}
				stores.put(nextStore.getID(),nextStore);
				currf.addEntrance(nextStore);
				currf.generateFloor();
				ArrayList<Level> nexts = new ArrayList<Level>();
				ArrayList<Integer> dirs = new ArrayList<Integer>();
				nexts.add(currf.findRoom("0"));
				dirs.add(Passage.FLOOR);
				nextStore.setNextLevels(nexts,dirs);
			}
			searchidx = idx;
		}
		//converting list of levels to linked list
		/*levels = new LinkedList<Level>();
		if(mode==LEVELS) {
			Level last = null;
			Level curr = null;
			for(int i=0; i<levelSequence[num].length; i++) {
				Class<Level> lvlclass = levelSequence[num][i];
				String lvlid = (last==null)?"0":last.getID()+"0";
				//if store, keep old one
				if(Store.class.isAssignableFrom(lvlclass)) {
					Level newstore = readRoom(lvlclass,lvlid);
					Level store = stores.get(newstore.getName());
					if(store == null) {
						store = newstore;
					}
					levels.add(store);
					store.setID(lvlid);
					stores.put(store.getName(),store);
					curr = store;
				}else {
					levels.add(curr = readRoom(lvlclass,lvlid));
				}
				if(last!=null) {
					ArrayList<Level> l = new ArrayList<Level>();
					l.add(curr);
					if(lvlid.equals("000")) {
						Level branch;
						levels.add(branch = readRoom(lvlclass,"001"));
						l.add(branch);
					}
					last.setNextLevels(l);
				}
				last = curr;
				if(i==0)firstLevel = curr;
				//temporary test of branching levels

			}
		}else if(mode==PVP) {
			if(num==0) {
				levels.add(new Arena2(game,this,""));
			}else if(num==1) {
				levels.add(new ArenaRound(game,this,""));
			}else {
				levels.add(new Arena1(game,this,""));
			}
			firstLevel = levels.getFirst();

		}
		currLevel = firstLevel;*/
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
		currLevel.build();
		game.draw.updateBG();
		wallSpace = new Area();
		for(Wall w : walls) {
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
	public void spawnNpcs() {
		currLevel.spawnNpcs();
	}
	
	//creates all the non-player characters and puts them in their starting levels
	public void createNpcs(int cycle) {
		npcs.add(new ExplorerMechanic(game,this,cycletime));
		//npcs.add(new ExplorerShopkeep(game,this,cycletime));
		//npcs.add(new ExplorerVendor(game,this,cycletime));
		//npcs.add(new ExplorerSidekick(game,this,cycletime));
		//npcs.add(new ExplorerMystery(game,this,cycletime));
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
	
	//returns instance of given Level subclass
	public Level readRoom(Class<Level> s, String id) {
		try {
			return (s.getDeclaredConstructor(Game.class, Board.class, String.class).newInstance(game,this,id));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	//returns instance of given Level subclass
	public Floor readFloor(Class<Floor> s, int w, int h, String id) {
		try {
			return (s.getDeclaredConstructor(Game.class, Board.class, int.class, int.class, String.class)).newInstance(game,this,w,h,id);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	

}
