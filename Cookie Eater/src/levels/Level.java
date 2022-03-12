package levels;


import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import ce3.*;
import cookies.*;
import entities.*;
import mechanisms.*;

public class Level{
	
	public final int BORDER_THICKNESS = 40;
	protected Game game;
	protected Board board;
	protected Floor floor;
	protected Room room; //stores data used to make this type of level
	protected ArrayList<Passage> passageways; //entrances and exits
	protected String lvlid; //id code for this level's path
	protected boolean loaded; //whether this level has been entered before
	//protected String[] themes;
	//protected double[] themeWeights;
	protected ThemeSet weightThemes;
	
	//public ArrayList<Cookie> cookies;
	public ArrayList<Wall> walls;
	public ArrayList<Mechanism> mechanisms; //moving or functional parts of level
	public ArrayList<Effect> effects;
	public ArrayList<Enemy> enemies;
	public ArrayList<Explorer> presentnpcs; //npcs that exist on current level
	
	protected ArrayList<int[]> nodes;
	protected ArrayList<int[]> bodes;
	protected ArrayList<int[]> lines;
	
	public Image bgImg;
	public Image wallImg;
	
	public int score, maxScore; //number of cookies picked up by player and number of cookies available in room
	
	public ChunkManager chunker; //manages chunks for reducing tests
	public int x_resol, y_resol; //x and y ranges for in-bounds rectangle
	
	
	public Level(Game frame, Board gameboard, Floor floorlevel, Room roomtemplate, ThemeSet themeWeights, String id) {
		game = frame;
		board = gameboard;
		floor = floorlevel;
		room = roomtemplate;
		lvlid = id;
		loaded = false;
		
		weightThemes = themeWeights;
		
		enemies = new ArrayList<Enemy>();
		effects = new ArrayList<Effect>();
		cookies = new ArrayList<Cookie>();
		walls = new ArrayList<Wall>();
		mechanisms = new ArrayList<Mechanism>();
		presentnpcs = new ArrayList<Explorer>();
		
		passageways = new ArrayList<Passage>();
		nodes = new ArrayList<int[]>();
		lines = new ArrayList<int[]>();
		
		score=0;
		maxScore=0;
		
		x_resol = room.x_resol;
		y_resol = room.y_resol;
		chunker = new ChunkManager(this,room.chunkerDims);
	}
	public Level(Game frame, Board gameboard, Floor floorlevel, SaveData sd) {
		game = frame;
		board = gameboard;
		floor = floorlevel;
		passageways = new ArrayList<Passage>();
		lvlid = sd.getString("id",0);
		room = board.rooms.get(sd.getString("roomid",0));
		loaded = sd.getBoolean("loaded",0);
		
		weightThemes = new ThemeSet(sd.getSaveDataList("themes").get(0));
		
		if(loaded) {
			ArrayList<SaveData> cookieData = sd.getSaveDataList("cookies");
			cookies = new ArrayList<Cookie>();
			if(cookieData!=null) {
				for(int i=0; i<cookieData.size(); i++) {
					Cookie loaded = Cookie.loadFromData(game, board, this, cookieData.get(i));
					if(!(loaded instanceof CookieStore) || ((CookieStore)loaded).getVendor()==null) {
						addCookie(loaded);
					}
				}
			}
			ArrayList<SaveData> enemyData = sd.getSaveDataList("enemies");
			enemies = new ArrayList<Enemy>();
			if(enemyData!=null) {
				for(int i=0; i<enemyData.size(); i++) {
					enemies.add(Enemy.loadFromData(game,board,enemyData.get(i),board.cycletime));
				}
			}
			ArrayList<SaveData> effectData = sd.getSaveDataList("effects");
			effects = new ArrayList<Effect>();
			if(effectData!=null) {
				for(int i=0; i<effectData.size(); i++) {
					effects.add(Effect.loadFromData(game,board,effectData.get(i),board.cycletime));
				}
			}
			ArrayList<SaveData> wallData = sd.getSaveDataList("walls");
			walls = new ArrayList<Wall>();
			if(wallData!=null) {
				for(int i=0; i<wallData.size(); i++) {
					walls.add(new Wall(game, board, this, wallData.get(i)));
				}
			}
			
			ArrayList<SaveData> mechData = sd.getSaveDataList("mechanisms");
			mechanisms = new ArrayList<Mechanism>();
			if(mechData!=null) {
				for(int i=0; i<mechData.size(); i++) {
					Mechanism mech = Mechanism.loadFromData(game, board, this, mechData.get(i));
					if(mech!=null)mechanisms.add(mech);
				}
			}
			for(int i=0; i<passageways.size(); i++) {
				mechanisms.add(passageways.get(i));
			}
			
			presentnpcs = new ArrayList<Explorer>();
			for(int i=0; sd.getData("presentnpcs")!=null && i<sd.getData("presentnpcs").size(); i++) {
				Explorer ex = board.getNPC(sd.getString("presentnpcs",i));
				presentnpcs.add(ex);
			}
			
			score = sd.getInteger("score",0);
			maxScore = sd.getInteger("score",1);
		}else {
			enemies = new ArrayList<Enemy>();
			effects = new ArrayList<Effect>();
			cookies = new ArrayList<Cookie>();
			walls = new ArrayList<Wall>();
			mechanisms = new ArrayList<Mechanism>();			
			score=0;
			maxScore=0;
		}
		
		
		nodes = new ArrayList<int[]>();
		lines = new ArrayList<int[]>();
		
		x_resol = room.x_resol;
		y_resol = room.y_resol;
		chunker = new ChunkManager(this,room.chunkerDims);
	}

	
	public SaveData getSaveData() {
		SaveData data = new SaveData();
		data.addData("id",lvlid);
		data.addData("roomid",room.code);
		data.addData("loaded",loaded);
		
		data.addData("themes",weightThemes.getSaveData());
		
		if(loaded) {
			int ci = 0;
			for(int i=0; i<cookies.size(); i++) {
				Cookie toSave = cookies.get(i);
				if(!(toSave instanceof CookieStore) || ((CookieStore)toSave).getVendor()==null) {
					data.addData("cookies",cookies.get(i).getSaveData(),ci++);
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
			for(int i=0; i<presentnpcs.size(); i++) {
				data.addData("presentnpcs",presentnpcs.get(i).getName(),i);
			}
			
			data.addData("score",score,0);
			data.addData("score",maxScore,1);
		}
		
		return data;
	}
	
	//update all moving parts in the level for this cycle
	public void runUpdate() {
		for(int i=0; i<mechanisms.size(); i++) {
			mechanisms.get(i).runUpdate();
		}
		for(int i=0; i<effects.size(); i++) {
			effects.get(i).runUpdate();
		}
		ArrayList<Cookie> chcooks = cookies();
		for(int i=0; i<chcooks.size(); i++) {
			if(i<chcooks.size()) {
				Cookie curr = chcooks.get(i);
				if(curr!=null)
				curr.runUpdate();
				if(i<chcooks.size()&&chcooks.get(i)!=null&&curr!=null&&!chcooks.get(i).equals(curr))
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
		for(int i=0; i<enemies.size(); i++) {
			if(i<enemies.size()) {
				Enemy curr = enemies.get(i);
				curr.endCycle();
			}
		}
	}
	
	public String getID() {return lvlid;}
	public void setID(String id) {lvlid = id;}
	public void addScore(int n) {score += n;}
	public int getScore() {return score;}
	public void setNextLevels(ArrayList<Level> next, ArrayList<Integer> directions) {
		buildPassages(next, directions, 150);
	}
	public void addPassage(Passage p) {
		passageways.add(p);
		addMechanism(p);
	}
	public void addCookie(Cookie c) {
		cookies.add(c);
		c.setLevel(this);
		chunker.addCookie(c);
	}
	public ArrayList<Cookie> cookies(){
		return chunker.cookies;
	}
	
	public ArrayList<Passage> getPassages(){
		return passageways;
	}
	
	//put walls in floor
	public void build() {
		Wall top,bot,lef,rig;
		walls.add(top = new Wall(game,board,this,0,-BORDER_THICKNESS/2,x_resol,BORDER_THICKNESS)); //add border walls
		walls.add(lef = new Wall(game,board,this,-BORDER_THICKNESS/2,0,BORDER_THICKNESS,y_resol));
		walls.add(bot = new Wall(game,board,this,0,y_resol-BORDER_THICKNESS/2,x_resol,BORDER_THICKNESS));
		walls.add(rig = new Wall(game,board,this,x_resol-BORDER_THICKNESS/2,0,BORDER_THICKNESS,y_resol));
		//put breaks in walls for passages (works for 1 passage per wall)
		for(Passage p : passageways) {
			p.setMode(this);
			boolean enter = p.isEntrance();
			switch(p.getDirection()) {
			case Passage.TOP:
				walls.add(breakWall(top,true,p.getLeft(enter),p.getRight(enter)));
				break;
			case Passage.BOTTOM:
				walls.add(breakWall(bot,true,p.getLeft(enter),p.getRight(enter)));
				break;
			case Passage.LEFT:
				walls.add(breakWall(lef,false,p.getUp(enter),p.getDown(enter)));
				break;
			case Passage.RIGHT:
				walls.add(breakWall(rig,false,p.getUp(enter),p.getDown(enter)));
				break;
			}
			addMechanism(p);
			if(enter) { //place door
				mechanisms.add(new WallDoor(game,board,this,(int)(p.getX()+.5),(int)(p.getY()+.5),p.getWidth()/2,p.cookieProportion(),true));
			}

		}
		genPaths(room.pathGen[0], room.pathGen[1], room.pathGen[2], room.pathGen[3], room.pathGen[4], room.regions);
		if(room.roundWalls) {
			genRoundWalls(room.wallGen[0], room.wallGen[1], room.wallGen[2]);
		}else {
			genWalls(room.wallGen[0], room.wallGen[1], room.wallGen[2], room.angledWalls);
		}
		
		if(room.mechanismGen != null) {
			for(int i=0; i<room.mechanismGen.size(); i++) {
				SaveData mechData = room.mechanismGen.get(i);
				int min = mechData.getInteger("quantity",0);
				int max = mechData.getInteger("quantity",1);
				int buildCount = (int)(Math.random()*(max-min)) + min;
				if(mechData.getData("bonusthemes")!=null){
					ThemeSet extras = new ThemeSet(mechData.getSaveDataList("preferredthemes").get(0));
					double bonus = extras.affinityWith(weightThemes);
					buildCount += bonus;
				}
				for(int j=0; j<buildCount; j++) {
					Mechanism m = Mechanism.loadFromData(game,board,this,mechData.getSaveDataList("mechanism").get(0));
					addMechanism(m);
				}
			}
		}
		
		loaded = true;
	}
	//puts a gap in a wall and returns a second wall for the other side
	//only functions correctly for rectangular, un-angled walls
	public Wall breakWall(Wall w, boolean horiz, int start, int end) {
		Wall w2;
		if(horiz) {
			int wid = (int)w.getW();
			int xp = (int)w.getX();
			w.setW(start-xp);
			w2 = new Wall(game,board,this,xp+end,(int)w.getY(),xp+wid-end,(int)w.getH());
		}else {
			int hei = (int)w.getH();
			int yp = (int)w.getY();
			w.setH(start-yp);
			w2 = new Wall(game,board,this,(int)w.getX(),yp+end,(int)w.getW(),yp+hei-end);
		}
		w.resetOrigin();
		return w2;
	}
	
	//creates passages to next levels
	public void buildPassages(ArrayList<Level> nextLevels, ArrayList<Integer> directions, int size){
		int[] dirs = {Passage.RIGHT,Passage.TOP};
		int[] poss = new int[6];
		poss[Passage.RIGHT]=y_resol/2;poss[Passage.LEFT]=y_resol/2;poss[Passage.TOP]=x_resol/2;poss[Passage.BOTTOM]=x_resol/2;poss[Passage.FLOOR]=0;poss[Passage.CEILING]=0;
		
		for(int i=0; i<nextLevels.size() && i<directions.size(); i++) {
			int dir = (directions==null)?dirs[i]:directions.get(i);
			Passage p = new Passage(game,board,this,nextLevels.get(i),dir,poss[dir],size);
			passageways.add(p);
			nextLevels.get(i).addEntrance(p);
		}
	}
	//add an entrance into this level
	public void addEntrance(Passage p) {
		passageways.add(p);
	}
	
	//adds a level mechanism to the board
	public void addMechanism(Mechanism m) {
		mechanisms.add(m);
	}
	public void placeCookies() {
		placeCookies(room.cookieGen[0], room.cookieGen[1]);
	}

	
	//put cookies in floor
	public void placeCookies(int clear, int separate) { //clearance between cookies and walls, separation between cookies
		if(room.cookieGen==null)return;
		double clearance = clear * room.scale;
		double separation = separate * room.scale;
		//place cookies so that none touch walls
		int cooks = 0; //count of cookies placed
		//vars for first/last cookie in line
		int xOrig = (int)(BORDER_THICKNESS+clearance+(int)(Cookie.DEFAULT_RADIUS*room.scale)+1);
		int yOrig = (int)(BORDER_THICKNESS+clearance+(int)(Cookie.DEFAULT_RADIUS*room.scale)+1);
		int tY = 0, tX = 0;
		//adjust cookie grid to be centered
		for(tY = yOrig; tY<y_resol-BORDER_THICKNESS-clearance; tY+=separation);
		for(tX = xOrig; tX<x_resol-BORDER_THICKNESS-clearance; tX+=separation);
		xOrig+=(x_resol-tX-xOrig)/2;
		yOrig+=(y_resol-tY-yOrig)/2;
		for(int pY = yOrig; pY<y_resol-BORDER_THICKNESS-clearance; pY+=separation) { //make grid of cookies
			for(int pX = xOrig; pX<x_resol-BORDER_THICKNESS-clearance; pX+=separation) {
				boolean place = true;
				if(!areaToPlace(pX,pY,(int)(Cookie.DEFAULT_RADIUS*room.scale+clearance+.5),board.wallSpace)) {
					place = false;
				}
				if(Math.sqrt(Math.pow(Math.abs(pX - board.player().getX()), 2) + Math.pow(Math.abs(pY - board.player().getY()), 2)) < board.player().getRadius() + Cookie.DEFAULT_RADIUS*getScale()){
					place = false;
				}
				if(place) { //place cookies, increment count
					addCookie(new Cookie(game,board,this,pX,pY,true));
					cooks++;
				}
			}
		}
		//remove cookies that player can't access
		for(int i=0; i<cookies.size(); i++) {
			Cookie currCookie = cookies.get(i);
			if(lineOfSight(currCookie.getX(),currCookie.getY(),(int)(.5+board.player().getX()),(int)(.5+board.player().getY()),(int)(board.player().getRadius()*room.scale*1.5),board.wallSpace)) {
				currCookie.setAccess(true);
				
			}
			
		}
		boolean did = false;
		do{
			did = false;
			for(int i=0; i<cookies.size(); i++) {
				Cookie currCookie = cookies.get(i);
				if(!currCookie.getAccess()) {
					for(int j=0; j<cookies.size(); j++) {
						Cookie testCookie = cookies.get(j);
						if(testCookie.getAccess() && lineOfSight(currCookie.getX(),currCookie.getY(),testCookie.getX(),testCookie.getY(),(int)(board.player().getRadius()*room.scale*1.5),board.wallSpace)) {
							currCookie.setAccess(true);
							did=true;
							j=cookies.size();
						}	
					}
				}
			}
		}while(did);
		for(int i=0; i<cookies.size(); i++) {
			Cookie currCookie = cookies.get(i);
			if(!currCookie.getAccess()) {
				currCookie.kill(null);
				cooks--;
				i--;
			}
		}
		
		maxScore = cooks;
		//update number of cookies that mechanisms use
		for(int i=0; i<mechanisms.size(); i++) {
			mechanisms.get(i).updateCookieTotal(cooks);
		}
	}
	//put enemies on floor
	public void spawnEnemies() {
		if(room.enemyGen==null)return;
		int cycle = game.getCycle();
		for(int i=0; i<room.enemyGen.size(); i++) {
			SaveData genData = room.enemyGen.get(i);
			int min = genData.getInteger("chance",0);
			int max = genData.getInteger("chance",1);
			String type = genData.getString("type",0);
			int spawnCount = (int)(Math.random()*(max-min)) + min;
			for(int j=0; j<spawnCount; j++) {
				Enemy e = Enemy.loadNewInstance(game, board, cycle, 0, 0, type);
				spawnAtRandom(e);
				//e.giveCookie(new CookieItem(board,0,0,Level.generateItem(board,possible.get((int)(Math.random()*possible.size()))),0));
			}
		}
	}
	
	//put all Npcs meant to be on this floor in their place
	public void checkNPCs(ArrayList<Explorer> npcs) {
		for(int i=0; i<npcs.size(); i++) {
			if(npcs.get(i).getResidence().equals(this)) {
				presentnpcs.add(npcs.get(i));
				npcs.get(i).spawn();
			}else if(presentnpcs.contains(npcs.get(i))) {
				presentnpcs.remove(npcs.get(i));
			}
		}
		for(int i=0; i<presentnpcs.size(); i++) {
			spawnAtRandom(presentnpcs.get(i)); //put on random cookie
		}
	}
	
	//reloads stored level data
	public void reload() {
		for(Passage p : passageways) {
			p.setMode(this);
		}
	}
	
	//remove all npcs from the board
	public void removeNPCs() {
		presentnpcs = new ArrayList<Explorer>();
	}
	
	//remove traces from board to switch to other level
	public void clean() {
		for(int i=0; i<mechanisms.size(); i++) {
			mechanisms.get(i).clean();
		}
	}
	
	//delete all state data
	public void remove() {
		removeNPCs();
		for(int i=0; i<cookies.size(); i++) {
			cookies.get(i).kill(null);
		}
		for(int i=0; i<mechanisms.size(); i++) {
			mechanisms.get(i).remove();
		}
		enemies = new ArrayList<Enemy>();
		walls = new ArrayList<Wall>();
		mechanisms = new ArrayList<Mechanism>();
		effects = new ArrayList<Effect>();
		score = 0;
		maxScore = 0;
		loaded = false;
	}
	
	//spawns chosen enemy at random cookie
	public void spawnAtRandom(Entity e) {
		Cookie c = cookies.remove((int)(Math.random()*cookies.size()));
		if(e instanceof Explorer)maxScore-=1;
		e.setX(c.getX());
		e.setY(c.getY());
		e.orientParts();
		if(e instanceof Enemy)enemies.add((Enemy)e);
		e.giveCookie(c);
	}
	
	public String getTitle() {return room.title;}
	public String getKeyTheme() {return room.keyTheme;}
	public ThemeSet getThemeWeights() {return weightThemes;}
	public double getScale() {return room.scale;}
	public int getMinDecay() {return room.minDecay;}
	public int getMaxDecay() {return room.maxDecay;}
	//public Color getBGColor() {return room.bgColor;}
	//public Color getWallColor() {return room.wallColor;}
	public boolean haltEnabled() {return room.haltEnabled;} //if the player can press button to stop movement
	public boolean specialsEnabled() {return room.specialsEnabled;} //if specials are allowed
	public boolean installPickups() {return room.installPickups;} //if picked up items are automatically installed
	public boolean takeDamage() {return room.takeDamage;} //if shields are used/player is killed when hits wall
	public boolean saveGame() {return room.saveGame;} //if game should be saved on entrance
	public double getExitProportion() {return room.exitProportion;}
	public Floor getFloor() {return floor;}
	public boolean loaded() {return loaded;}
	
	public double[][] getStarts(){return room.startposs;}
	//returns the first found exit passageway
	public Passage firstExit() {
		for(int i=0; i<passageways.size(); i++) {
			if(passageways.get(i).entranceAt(this)) {
				return passageways.get(i);
			}
		}
		return null;
	}
	
	//creates nodes and connections
	public void genPaths(int num, int nradminimum, int nradmaximum, int lradius, int ldiv, int[][] areas) {
		int nradmin = (int)(nradminimum * room.scale),nradmax = (int)(nradmaximum* room.scale),lrad= (int)(lradius * room.scale);
		if(areas == null) { //default areas to full screen
			int[][] nareas = {{0,x_resol,0,y_resol}};
			areas = nareas;
		}
		ArrayList<int[]> ranges = new ArrayList<int[]>(); //put ranges into list
		for(int i=0; i<areas.length; i++) {
			ranges.add(areas[i]);
		}
		
		for(int i=0; i<=num; i++) { //make num of extra nodes
			if(i<areas.length) { //if some region empty
				int[] ra = ranges.remove((int)(Math.random()*ranges.size())); //choose region
				nodes.add(new int[] {(int)(Math.random()*(ra[1]-ra[0])+ra[0]),(int)(Math.random()*(ra[3]-ra[2])+ra[2]),(int)(Math.random()*(nradmax-nradmin)+nradmin)}); //add randomly in region
			}else{
				nodes.add(new int[] {(int)(Math.random()*x_resol),(int)(Math.random()*y_resol),(int)(Math.random()*(nradmax-nradmin)+nradmin)}); //add random node
			}
			if(nodes.size()>1) {
				int c = (int)(Math.random()*(nodes.size()-2)); //choose random existing node but not last node
				lines.add(new int[] {nodes.get(i)[0],nodes.get(i)[1],nodes.get(c)[0],nodes.get(c)[1]});
			}
			//splitLine(lineRad,nodes.get(c)[0],nodes.get(c)[1],nodes.get(i)[0],nodes.get(i)[1]);//make two lines for path
		}
		
		
		for(int i=0; i<passageways.size(); i++) { //add nodes for every entrance and exit
			Passage p = passageways.get(i);
			int[] na = {(int)p.getX(),(int)p.getY(),(int)(p.getWidth()+Math.random()*(nradmax-nradmin))};
			nodes.add(na);
			
			int c = (int)(Math.random()*(nodes.size()-1)); //choose random existing node
			lines.add(new int[] {na[0],na[1],nodes.get(c)[0],nodes.get(c)[1]});
		}

		
		
		for(int[] b : lines) { //add lineDiv number of nodes along lines
			double diffX = b[0]-b[2], diffY = b[1]-b[3];
			double currX = b[2], currY=b[3];
			for(int i=0; i<ldiv; i++) {
				currX+=diffX/ldiv;
				currY+=diffY/ldiv;
				nodes.add(new int[] {(int)(currX+.5),(int)(currY+.5),lrad});
			}
		}
		bodes = nodes;
	}
	public void paint(Graphics g) {
		if(bodes==null)return;
		for(int[] n : bodes) {
			g.setColor(Color.RED);
			g.drawOval(n[0]-n[2],n[1]-n[2],n[2]*2,n[2]*2);
		}
	}
		
	//places walls that don't touch paths or nodes
	public void genWalls(int separation, int minimum, int maximum, boolean angled) {
		//for(int i=0; i<num; i++) { //make num of walls
		if(separation==0)return;
		int sep = (int)(separation * room.scale),min = (int)(minimum * room.scale),max= (int)(maximum * room.scale);
		for(int i=BORDER_THICKNESS; i<y_resol; i+=sep) {
			for(int j=BORDER_THICKNESS; j<x_resol; j+=sep) {
				//int cX = (int)(Math.random()*board.x_resol), cY = (int)(Math.random()*board.y_resol); //choose wall center
				int x=j,y=i,w=1,h=1;
				double a = (angled)?Math.random()*Math.PI*2:0;
				if(rectOK(x,y,w,h,a,i,j,max)) { //if center is valid
					while(rectOK(x,y,w,h,a,i,j,max)) {
						x-=10;w+=10;
					}
					x+=10;w-=10;
					while(rectOK(x,y,w,h,a,i,j,max)) {
						x--;w++;
					}
					x++;w--;
					while(rectOK(x,y,w,h,a,i,j,max)) {
						y-=10;h+=10;
					}
					y+=10;h-=10;
					while(rectOK(x,y,w,h,a,i,j,max)) {
						y--;h++;
					}
					y++;h--;
					while(rectOK(x,y,w,h,a,i,j,max)) {
						w+=10;
					}
					w-=10;
					while(rectOK(x,y,w,h,a,i,j,max)) {
						w++;
					}
					w--;
					while(rectOK(x,y,w,h,a,i,j,max)) {
						h+=10;
					}
					h-=10;
					while(rectOK(x,y,w,h,a,i,j,max)) {
						h++;
					}
					h--;
					if(h>=min && w>=min) //remove small walls
						walls.add(new Wall(game,board,this,x,y,w,h,a,i,j));
				}
			}
		}
	}  
	
	//places walls that don't touch paths or nodes
	public void genRoundWalls(int sep, int min, int max) {
		if(sep==0)return;
		for(int i=0; i<y_resol; i+=sep) {
			for(int j=0; j<x_resol; j+=sep) {
				int r=1;
				if(circOK(j,i,r,max)) { //if center is valid
					while(circOK(j,i,r,max)) {
						r+=10;
					}
					r-=10;
					while(circOK(j,i,r,max)) {
						r++;
					}
					r--;
					if(r>=min) //remove small walls
						walls.add(new Wall(game,board,this,j,i,r));
				}
			}
		}
	} 
	
	//gives length of line rom start/end points
	public static double lineLength(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
	}
	
	//tests if a circle and a rectangle overlap
	public static boolean collidesCircleAndRect(double cX, double cY, double cR, double rX, double rY, double rW, double rH) {
		return (Math.abs(cX - rX) <= cR && cY>=rY && cY<=rY+rH) ||
				(Math.abs(cX - (rX+rW)) <= cR && cY>=rY && cY<=rY+rH)||
				(Math.abs(cY - rY) <= cR && cX>=rX && cX<=rX+rW) ||
				(Math.abs(cY - (rY+rH)) <= cR && cX>=rX && cX<=rX+rW) ||
				(Math.sqrt((cX-rX)*(cX-rX) + (cY-rY)*(cY-rY))<=cR) ||
				(Math.sqrt((cX-(rX+rW))*(cX-(rX+rW)) + (cY-rY)*(cY-rY))<=cR) ||
				(Math.sqrt((cX-rX)*(cX-rX) + (cY-(rY+rH))*(cY-(rY+rH)))<=cR) ||
				(Math.sqrt((cX-(rX+rW))*(cX-(rX+rW)) + (cY-(rY+rH))*(cY-(rY+rH)))<=cR) ||
				(cX >= rX && cX <= rX+rW && cY >= rY && cY <= rY+rH);
	}
	public static
	boolean collidesCircleAndRect(double cX, double cY, double cR, double rX, double rY, double rW, double rH, double rA) { //supposed to be static
		
		double wX = rW * Math.cos(rA);
		double wY = rW * Math.sin(rA);
		double hX = rH * Math.cos(rA+Math.PI/2);
		double hY = rH * Math.sin(rA+Math.PI/2);
		if(Math.abs(hX)<.0000001)hX=0;
		
		return Level.collidesLineAndCircle(rX, rY, rX+wX, rY+wY, cX, cY, cR) || 
				Level.collidesLineAndCircle(rX, rY, rX+hX, rY+hY, cX, cY, cR) || 
				Level.collidesLineAndCircle(rX+wX, rY+wY, rX+wX+hX, rY+wY+hY, cX, cY, cR) || 
				Level.collidesLineAndCircle(rX+hX, rY+hY, rX+wX+hX, rY+wY+hY, cX, cY, cR);
	}
	public static boolean collidesCircleAndRect(double cX, double cY, double cR, double rX, double rY, double rW, double rH, double rA, double rOX, double rOY) { //supposed to be static
		
		double[][] corners = corners(rX,rY,rW,rH,rOX,rOY,rA);
		double[] last = corners[3];
		for(int i=0; i<4; i++) {
			double[] curr = corners[i];
			if(Level.collidesLineAndCircle(last[0], last[1], curr[0], curr[1], cX, cY, cR))
				return true;
			last = curr;
		}
		return false;
	}
	
	//return corner coordinates of rotated rectangle
	public static double[][] corners(double x, double y, double w, double h, double rx, double ry, double a){
		double[][] ret = new double[4][2];
		//top-left
		ret[0][0] = rx - Level.lineLength(x,y,rx,ry) * Math.cos(a+Math.atan2(y-ry,x-rx));
		ret[0][1] = ry - Level.lineLength(x,y,rx,ry) * Math.sin(a+Math.atan2(y-ry,x-rx));
		//top-right
		ret[1][0] = rx - Level.lineLength(x+w,y,rx,ry) * Math.cos(a+Math.atan2(y-ry,x+w-rx));
		ret[1][1] = ry - Level.lineLength(x+w,y,rx,ry) * Math.sin(a+Math.atan2(y-ry,x+w-rx));
		//bottom-right
		ret[2][0] = rx - Level.lineLength(x+w,y+h,rx,ry) * Math.cos(a+Math.atan2(y+h-ry,x+w-rx));
		ret[2][1] = ry - Level.lineLength(x+w,y+h,rx,ry) * Math.sin(a+Math.atan2(y+h-ry,x+w-rx));
		//bottom-left
		ret[3][0] = rx - Level.lineLength(x,y+h,rx,ry) * Math.cos(a+Math.atan2(y+h-ry,x-rx));
		ret[3][1] = ry - Level.lineLength(x,y+h,rx,ry) * Math.sin(a+Math.atan2(y+h-ry,x-rx));
		return ret;
	}
	
	//if two rectangles collide (doesn't count one fully engulfing the other)
	public static boolean collidesRectAndRect(double x1, double y1, double w1, double h1, double a1, double x2, double y2, double w2, double h2, double a2) {
		double wx1 = w1 * Math.cos(a1);
		double wy1 = w1 * Math.sin(a1);
		double hx1 = h1 * Math.cos(a1+Math.PI/2);
		double hy1 = h1 * Math.sin(a1+Math.PI/2);
		if(Math.abs(hx1)<.0000001)hx1=0;
		
		double wx2 = w2 * Math.cos(a2);
		double wy2 = w2 * Math.sin(a2);
		double hx2 = h2 * Math.cos(a2+Math.PI/2);
		double hy2 = h2 * Math.sin(a2+Math.PI/2);
		if(Math.abs(hx2)<.0000001)hx2=0;
		
		double[][] corners1 = {{x1+hx1,y1+hy1},{x1,y1},{x1+wx1,y1+wy1},{x1+wx1+hx1,y1+wy1+hy1},{x1+hx1,y1+hy1}};
		double[][] corners2 = {{x2+hx2,y2+hy2},{x2,y2},{x2+wx2,y2+wy2},{x2+wx2+hx2,y2+wy2+hy2},{x2+hx2,y2+hy2}};
		
		
		boolean collision = false;
		for(int i=1; i<=4; i++) { //all 1st rect sides
			for(int j=1; j<=4; j++) { //all 2nd rect sides
				if(collidesLineAndLine(corners1[i][0],corners1[i][1],corners1[i-1][0],corners1[i-1][1],corners2[j][0],corners2[j][1],corners2[j-1][0],corners2[j-1][1]))
					collision = true;
			}
		}
		return collision;
	}
	
	//collision point of two rectangles
	public static double[] rectAndRectHitPoint(double x1, double y1, double w1, double h1, double a1, double x2, double y2, double w2, double h2, double a2) {
		Rectangle2D.Double c = new Rectangle2D.Double(x1,y1,w1,h1);
		AffineTransform at = AffineTransform.getRotateInstance(a1,x1,y1);
		Shape cc = at.createTransformedShape(c);
		Rectangle2D.Double b = new Rectangle2D.Double(x2,y2,w2,h2);
		AffineTransform at2 = AffineTransform.getRotateInstance(a2,x2,y2);
		Shape bb = at2.createTransformedShape(b);
		double[] aa = areasHitPoint(new Area(bb),new Area(cc));
		return aa;
	}
	
	public static boolean lineOfSight(int x1, int y1, int x2, int y2, int wid, Area walls) {
		double xm = (x1+x2)/2,ym = (y1+y2)/2;
		double am = Math.atan2(y2-y1,x2-x1);
		double lm = lineLength(x1,y1,x2,y2);
		double rx = xm-Math.cos(am)*(lm/2)-Math.cos(am+Math.PI/2)*(wid/2), ry = ym-Math.sin(am)*(lm/2)-Math.sin(am+Math.PI/2)*(wid/2);
		Rectangle2D.Double sight = new Rectangle2D.Double(rx,ry,lm,wid);
		AffineTransform at = AffineTransform.getRotateInstance(am,rx,ry);
		Shape cc = at.createTransformedShape(sight);
		Area a = new Area(cc);
		a.intersect(walls);
		return a.isEmpty();
	}
	public static boolean areaToPlace(int x1, int y1, int rad, Area walls) {
		Ellipse2D.Double field = new Ellipse2D.Double(x1-rad,y1-rad,rad*2,rad*2);
		Area a = new Area(field);
		a.intersect(walls);
		return a.isEmpty();
	}
	
	//tests if a line and a rectangle overlap
	public static boolean collidesLineAndRect(double x1, double y1, double x2, double y2, double rX, double rY, double rW, double rH) {
		return collidesLineAndLine(x1,y1,x2,y2,rX,rY,rX+rW,rY) || //top
				collidesLineAndLine(x1,y1,x2,y2,rX,rY,rX,rY+rH) || //left
				collidesLineAndLine(x1,y1,x2,y2,rX,rY+rH,rX+rW,rY+rH) || //bottom
				collidesLineAndLine(x1,y1,x2,y2,rX+rW,rY,rX+rW,rY+rH) || //right
				(x1>=rX && x1<=rX+rW && y1>=rY && y1<=rY+rH); //both points inside rectangle
	}
	//tests if a line and a rectangle overlap
	public static boolean collidesLineAndRect(double x1, double y1, double x2, double y2, double rX, double rY, double rW, double rH, double rA) {
		double wX = rW * Math.cos(rA);
		double wY = rW * Math.sin(rA);
		double hX = rH * Math.cos(rA+Math.PI/2);
		double hY = rH * Math.sin(rA+Math.PI/2);
		if(Math.abs(hX)<.0000001)hX=0;
		
		return collidesLineAndLine(x1,y1,x2,y2,rX, rY, rX+wX, rY+wY) || //top
				collidesLineAndLine(x1,y1,x2,y2,rX, rY, rX+hX, rY+hY) || //left
				collidesLineAndLine(x1,y1,x2,y2,rX+wX, rY+wY, rX+wX+hX, rY+wY+hY) || //right
				collidesLineAndLine(x1,y1,x2,y2,rX+hX, rY+hY, rX+wX+hX, rY+wY+hY) || //bottom
				//test if each of the line points are inside the rectangle
				(collidesLineAndCircle(rX, rY, rX+wX, rY+wY, x1, y1, lineLength(rX, rY, rX+hX, rY+hY)) &&
				collidesLineAndCircle(rX+hX, rY+hY, rX+wX+hX, rY+wY+hY, x1, y1, lineLength(rX, rY, rX+hX, rY+hY)) &&
				collidesLineAndCircle(rX, rY, rX+hX, rY+hY, x1, y1, lineLength(rX, rY, rX+wX, rY+wY)) &&
				collidesLineAndCircle(rX+wX, rY+wY, rX+wX+hX, rY+wY+hY, x1, y1, lineLength(rX, rY, rX+wX, rY+wY)))
				||
				(collidesLineAndCircle(rX, rY, rX+wX, rY+wY, x2, y2, lineLength(rX, rY, rX+hX, rY+hY)) &&
				collidesLineAndCircle(rX+hX, rY+hY, rX+wX+hX, rY+wY+hY, x2, y2, lineLength(rX, rY, rX+hX, rY+hY)) &&
				collidesLineAndCircle(rX, rY, rX+hX, rY+hY, x2, y2, lineLength(rX, rY, rX+wX, rY+wY)) &&
				collidesLineAndCircle(rX+wX, rY+wY, rX+wX+hX, rY+wY+hY, x2, y2, lineLength(rX, rY, rX+wX, rY+wY)));
	}
	public static boolean collidesLineAndRect(double x1, double y1, double x2, double y2, double rX, double rY, double rW, double rH, double rA, double rOX, double rOY) {
		double[][] corners = corners(rX,rY,rW,rH,rOX,rOY,rA);
		double[] last = corners[3];
		for(int i=0; i<4; i++) {
			double[] curr = corners[i];
			if(Level.collidesLineAndLine(last[0], last[1], curr[0], curr[1], x1, y1, x2, y2))
				return true;
			last = curr;
		}
		
		//test if each of the line points are inside the rectangle
		return (collidesLineAndCircle(corners[0][0], corners[0][1], corners[1][0], corners[1][1], x1, y1, lineLength(corners[0][0],corners[0][1],corners[3][0],corners[3][1])) &&
					collidesLineAndCircle(corners[3][0], corners[3][1], corners[2][0], corners[2][1], x1, y1, lineLength(corners[0][0],corners[0][1],corners[3][0],corners[3][1])) &&
					collidesLineAndCircle(corners[0][0], corners[0][1], corners[3][0], corners[3][1], x1, y1, lineLength(corners[0][0],corners[0][1],corners[1][0],corners[1][1])) &&
					collidesLineAndCircle(corners[1][0], corners[1][1], corners[2][0], corners[2][1], x1, y1, lineLength(corners[0][0],corners[0][1],corners[1][0],corners[1][1])))
				||
				(collidesLineAndCircle(corners[0][0], corners[0][1], corners[1][0], corners[1][1], x2, y2, lineLength(corners[0][0],corners[0][1],corners[3][0],corners[3][1])) &&
						collidesLineAndCircle(corners[3][0], corners[3][1], corners[2][0], corners[2][1], x2, y2, lineLength(corners[0][0],corners[0][1],corners[3][0],corners[3][1])) &&
						collidesLineAndCircle(corners[0][0], corners[0][1], corners[3][0], corners[3][1], x2, y2, lineLength(corners[0][0],corners[0][1],corners[1][0],corners[1][1])) &&
						collidesLineAndCircle(corners[1][0], corners[1][1], corners[2][0], corners[2][1], x2, y2, lineLength(corners[0][0],corners[0][1],corners[1][0],corners[1][1])));
	}
	//tests if a line and a circle overlap
	public static boolean collidesLineAndCircle(double x1, double y1, double x2, double y2, double cX, double cY, double cR) {
		double lM = (y2-y1)/(x2-x1);
		double dM = -1/lM;
		double dL = Math.sqrt(1+dM*dM);
		double ratio = cR/dL;
		double fX = ratio;
		double fY = ratio*dM;
		if(x2-x1==0) { //vertical line (avoid NaN)
			return collidesLineAndLine(x1,y1,x2,y2,cX+cR,cY,cX-cR,cY) || lineLength(x1,y1,cX,cY)<=cR || lineLength(x2,y2,cX,cY)<=cR;
		}
		if(lM==0) { //horizontal line (avoid NaN)
			return collidesLineAndLine(x1,y1,x2,y2,cX,cY+cR,cX,cY-cR) || lineLength(x1,y1,cX,cY)<=cR || lineLength(x2,y2,cX,cY)<=cR;
		}else {
			return collidesLineAndLine(x1,y1,x2,y2,cX+fX,cY+fY,cX-fX,cY-fY) || lineLength(x1,y1,cX,cY)<=cR || lineLength(x2,y2,cX,cY)<=cR;
		}
	}
	//tests if two lines intersect
	public static boolean collidesLineAndLine(double x1a, double y1a, double x2a, double y2a, double x1b, double y1b, double x2b, double y2b) {
		if(x1a==x2a) { //vertical line
			if(x1a>=Math.min(x1b, x2b) && x1a<=Math.max(x1b, x2b)) { //if this line is in middle of other
				if(x1a==x1b && x1a==x2b) { //if both are vertical
					return (y1a>=Math.min(y1b, y2b) && y1a<=Math.max(y1b, y2b)) || //return if at least one point is in other line
							(y2a>=Math.min(y1b, y2b) && y2a<=Math.max(y1b, y2b)) ||
							(y1b>=Math.min(y1a, y2a) && y1b<=Math.max(y1a, y2a)) ||
							(y2b>=Math.min(y1a, y2a) && y2b<=Math.max(y1a, y2a));
				}else { //one vertical, one not
					double mb = (y1b-y2b)/(x1b-x2b); //slope
					double bb = y1b-mb*x1b; //yint
					double y = mb * x1a + bb; //intersection
					return (y>=Math.min(y1a, y2a) && y<=Math.max(y1a, y2a)); //if intersection is within line ends
				}
			}else {
				return false; //line outside of range
			}
		}
		if(x1b==x2b){
			if(x1b>=Math.min(x1a, x2a) && x1b<=Math.max(x1a, x2a)) {
				double ma = (y1a-y2a)/(x1a-x2a);
				double ba = y1a-ma*x1a;
				double y = ma * x1b + ba;
				return (y>=Math.min(y1b, y2b) && y<=Math.max(y1b, y2b));
			}else {
				return false;
			}
		}
		if(y1a==y2a){
			if(y1a>=Math.min(y1b, y2b) && y1a<=Math.max(y1b, y2b)) {
				if(y1a==y1b && y1a==y2b) {
					return (x1a>=Math.min(x1b, x2b) && x1a<=Math.max(x1b, x2b)) ||
							(x2a>=Math.min(x1b, x2b) && x2a<=Math.max(x1b, x2b)) ||
							(x1b>=Math.min(x1a, x2a) && x1b<=Math.max(x1a, x2a)) ||
							(x2b>=Math.min(x1a, x2a) && x2b<=Math.max(x1a, x2a));
				}else {
					double nb = (x1b-x2b)/(y1b-y2b);
					double db = x1b-nb*y1b;
					double x = nb * y1a + db;
					return (x>=Math.min(x1a, x2a) && x<=Math.max(x1a, x2a));
				}
			}else {
				return false;
			}
		}
		if(y1b==y2b){
			if(y1b>=Math.min(y1a, y2a) && y1b<=Math.max(y1a, y2a)) {
				double na = (x1a-x2a)/(y1a-y2a);
				double da = x1a-na*y1a;
				double x = na * y1b + da;
				return (x>=Math.min(x1b, x2b) && x<=Math.max(x1b, x2b));
			}else {
				return false;
			}
		}
		
		double ma = (y1a-y2a)/(x1a-x2a); //slope
		double na = (x1a-x2a)/(y1a-y2a); //inv slope
		double mb = (y1b-y2b)/(x1b-x2b);
		double nb = (x1b-x2b)/(y1b-y2b);
		double ba = y1a-ma*x1a; //yint
		double da = x1a-na*y1a; //xint
		double bb = y1b-mb*x1b;
		double db = x1b-nb*y1b;
		
		double x = (ba-bb)/(mb-ma); //intersection coords
		double y = (da-db)/(nb-na);
		
		return (x>=Math.min(x1a, x2a) && x<=Math.max(x1a, x2a)) &&
				(x>=Math.min(x1b, x2b) && x<=Math.max(x1b, x2b)) &&
				(y>=Math.min(y1a, y2a) && y<=Math.max(y1a, y2a)) &&
				(y>=Math.min(y1b, y2b) && y<=Math.max(y1b, y2b)); //intersections within bounds
		
	}
	//returns [x,y] array of coordinates for the point where two lines intersect
	public static double[] lineIntersection(double x1a, double y1a, double x2a, double y2a, double x1b, double y1b, double x2b, double y2b) {
		if(collidesLineAndLine(x1a,y1a,x2a,y2a,x1b,y1b,x2b,y2b)) {
			double[] ret = new double[2];
			if(x1a==x2a) {
				if(x1b==x2b) {
					ret[0] = x1a;
					ret[1] = (y1a+y2a+y1b+y2b)/4;
				}else {
					double mb = (y2b-y1b)/(x2b-x1b);
					double bb = y1b - mb * x1b;
					ret[0] = x1a;
					ret[1] = x1a*mb+bb;
				}
			}else {
				if(x1b==x2b) {
					double ma = (y2a-y1a)/(x2a-x1a);
					double ba = y1a - ma * x1a;
					ret[0] = x1b;
					ret[1] = x1b*ma+ba;
				}else {
					double ma = (y2a-y1a)/(x2a-x1a);
					double mb = (y2b-y1b)/(x2b-x1b);
					double ba = y1a - ma * x1a;
					double bb = y1b - mb * x1b;
					ret[0] = (bb-ba)/(ma-mb);
					ret[1] = ma * ret[0] + ba;
				}
			}
			return ret;
		}else {
			return null;
		}
	}
	
		
	//are any rectangle corners colliding with nodes
	public boolean rectOK(int x, int y, int w, int h, double a, int ox, int oy, int max) {
		if(w>max || h>max) {
			return false; //false if wall too big
		}
		double[][] corners = corners(x,y,w,h,ox,oy,a);
		for(int[] node : nodes) {
			if(collidesCircleAndRect((int)(node[0]+.5),(int)(node[1]+.5),node[2],x,y,w,h,a,ox,oy)) {
				return false; //false if wall hits node
			}
			if((lineLength(node[0],node[1],corners[0][0],corners[0][1])<node[2] || lineLength(node[0],node[1],corners[1][0],corners[1][1])<node[2]) ||
			lineLength(node[0],node[1],corners[2][0],corners[2][1])<node[2] || lineLength(node[0],node[1],corners[3][0],corners[3][1])<node[2]) {
				return false; //false if any edge is in a node radius
			}
		}
		for(Wall wl : walls) {
			int num = 0;
			for(int i=0; i<4; i++) {
				if(collidesLineAndRect(corners[i][0],corners[i][1],corners[i][0],corners[i][1],wl.getX(),wl.getY(),wl.getW(),wl.getH(),wl.getA(),wl.getOX(),wl.getOY()))num++;
			}
			if(num>=3) 
				return false; //false if at least 3 corners are within another wall
		}
		int num = 0;
		for(int i=0; i<4; i++) {
			if(corners[i][0]<=0 || corners[i][0]>=x_resol || corners[i][1]<=0 || corners[i][1]>=y_resol)num++;
		}
		if(num>=2) 
			return false; //false if at least 2 corners are outside
		return true;
	}

	//is the circle wall ok to generate
	public boolean circOK(int x, int y, int r, int max) {
		if(r>max) {
			return false; //false if wall too big
		}
		for(int[] node : nodes) {
			if(Level.lineLength(node[0],node[1],x,y)<=r+node[2]) {
				return false; //false if wall hits node
			}
		}
		return true;
	}
	//point where circle and rectangle collide
	public //static 
	double[] circAndRectHitPoint(double cx, double cy, double cr, double rx, double ry, double rw, double rh) {
		double[] ret = {cx,cy};
		boolean xB=false,yB=false;
		if(cy>ry+rh) {
			ret[1] = cy-cr;
			yB=true;
		}if(cy<ry) {
			ret[1] = cy+cr;
			yB=true;
		}if(cx>rx+rw) {
			ret[0] = cx-cr;
			xB=true;
		}if(cx<rx) {
			ret[0] = cx+cr;
			xB=true;
		}
		if(xB&&yB) {
			ret[1]=(cy>ry+rh)?ry+rh:ry;
			ret[0]=(cx>rx+rw)?rx+rw:rx;
		}
		return ret;
	}
	//point where circle and rotated rectangle collide
	public static double[] circAndRectHitPoint(double cx, double cy, double cr, double rx, double ry, double rw, double rh, double ra) {
		Rectangle2D.Double c = new Rectangle2D.Double(rx,ry,rw,rh);
		AffineTransform at = AffineTransform.getRotateInstance(ra,rx,ry);
		Shape cc = at.createTransformedShape(c);
		return areasHitPoint(new Area(new Ellipse2D.Double(cx-cr,cy-cr,cr*2,cr*2)),new Area(cc));
	}
	//point where two circles collide
	public static double[] circAndCircHitPoint(double c1x, double c1y, double c1r, double c2x, double c2y, double c2r) {
		return areasHitPoint(new Area(new Ellipse2D.Double(c1x-c1r,c1y-c1r,c1r*2,c1r*2)),new Area(new Ellipse2D.Double(c2x-c2r,c2y-c2r,c2r*2,c2r*2)));
	}
	
	//returns collision point of two areas, determined by center point of bounding rectangle of intersection
	public static
	double[] areasHitPoint(Area a1, Area a2) {
		a2.intersect(a1);
		Rectangle r = a2.getBounds();
		return new double[] {r.x+r.width/2,r.y+r.height/2};
	}
}
