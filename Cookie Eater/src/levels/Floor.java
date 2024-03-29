package levels;

import java.util.*;

import ce3.*;
import mechanisms.*;

public class Floor {
	
	public static final String startCode = "beg";
	public static final String blankCode = "no";
	protected Game game;
	protected Board board;
	protected Level[][] roomGrid; //grid of room locations
	//protected ArrayList<Floor> prevs;
	//protected ArrayList<Floor> nexts;
	protected ArrayList<Store> entrances;
	protected ArrayList<Store> exits;
	protected ArrayList<Level> ends; //terminal rooms
	protected Store store;
	protected String id;
	protected Layout layout; //stores data to make this type of Floor

	public Floor(Game frame, Board gameboard, Layout layouttemplate, String floorid) {
		game = frame;
		board = gameboard;
		id = floorid;
		layout = layouttemplate;
		//prevs = new ArrayList<Floor>();
		//nexts = new ArrayList<Floor>();
		entrances = new ArrayList<Store>();
		exits = new ArrayList<Store>();
		ends = new ArrayList<Level>();
		roomGrid = new Level[layout.rows][layout.cols];
		Room storeRoom = board.rooms.get(layout.store);
		store = new Store(game, board, storeRoom, layout.presentThemes, id);
	}
	
	public Floor(Game frame, Board gameboard, SaveData sd) {
		game = frame;
		board = gameboard;
		id = sd.getString("id",0);
		layout = board.layouts.get(sd.getString("layoutid",0));
		entrances = new ArrayList<Store>();
		exits = new ArrayList<Store>();
		ends = new ArrayList<Level>();
		roomGrid = new Level[layout.rows][layout.cols];
		Room storeRoom = board.rooms.get(sd.getString("store",0));
		store = new Store(game, board, storeRoom, layout.presentThemes, id);
		
		ArrayList<Level> loadedLevels = new ArrayList<Level>();
		Iterator<Store> it = board.stores.values().iterator();
		while(it.hasNext())loadedLevels.add(it.next());
		ArrayList<SaveData> roomData = sd.getSaveDataList("rooms");
		for(int i=0; i<roomData.size(); i++) {
			SaveData rd = roomData.get(i);
			Level room = new Level(game, board, this, rd.getSaveDataList("data").get(0));
			loadedLevels.add(room);
			roomGrid[rd.getInteger("index",0)][rd.getInteger("index",1)] = room;
		}
		
		ArrayList<SaveData> passData = sd.getSaveDataList("passages");
		for(int i=0; i<passData.size(); i++) {
			SaveData pd = passData.get(i);
			Passage pass = new Passage(game, board, loadedLevels, pd);
			if(pass.getEntrance()!=null)pass.getEntrance().addPassage(pass);
			if(pass.getExit()!=null)pass.getExit().addPassage(pass);
		}
	}
	
	public SaveData getSaveData() {
		SaveData data = new SaveData();
		data.addData("id",id);
		ArrayList<SaveData> roomData = new ArrayList<SaveData>();
		ArrayList<Passage> passages = new ArrayList<Passage>();
		for(int i=0; i<roomGrid.length; i++) {
			for(int j=0; j<roomGrid[i].length; j++) {
				if(roomGrid[i][j] != null) {
					SaveData room = new SaveData();
					room.addData("data",roomGrid[i][j].getSaveData());
					room.addData("index",i,0);
					room.addData("index",j,1);
					roomData.add(room);
					ArrayList<Passage> ps = roomGrid[i][j].getPassages();
					for(int k=0; k<ps.size(); k++) {
						if(!passages.contains(ps.get(k))) {
							passages.add(ps.get(k));
						}
					}
				}
			}
		}
		for(int i=0; i<passages.size(); i++)data.addData("passages",passages.get(i).getSaveData(),i);
		for(int i=0; i<roomData.size(); i++)data.addData("rooms",roomData.get(i),i);
		data.addData("layoutid",layout.nameSub);
		return data;
	}
	
	//creates and connects levels in paths
	public void generateFloor() {
		ThemeSet[][] themeWeights = generateTheme();
		//int entrances = prevs.size();
		//int exits = nexts.size();
		int[] currCoord = {(int)(Math.random()*roomGrid.length),(int)(Math.random()*roomGrid[0].length)};

		
		HashMap<Room,Integer> roomWeights = new HashMap<Room,Integer>();
		for(int i=0; i<layout.roomGen.size(); i++) {
			SaveData sd = layout.roomGen.get(i);
			Room r = board.rooms.get(sd.getString("room",0));
			int weight = sd.getInteger("weight",0);
			roomWeights.put(r,weight);
		}
		
		roomGrid[currCoord[0]][currCoord[1]] = generateRoom(roomWeights,themeWeights[currCoord[0]][currCoord[1]],startCode);
		for(int i=Math.min(layout.numRooms,roomGrid.length*roomGrid[0].length)-1; i>=0; i--) {
			//test directions to move in
			int[] moveData = moveDirection(currCoord);
			int chosenDir = moveData[0];
			int[] change = {moveData[1], moveData[2]};
			int count = moveData[3];
			
			
			Level current = roomGrid[currCoord[0]][currCoord[1]];
			if(ends.contains(current) && !current.getPassages().isEmpty())ends.remove(current);
			//test if there are available directions
			if(count>0) { //move in chosen direction
				//generate a new room
				Level addition = generateRoom(roomWeights,themeWeights[currCoord[0]+change[0]][currCoord[1]+change[1]],currCoord[0]+"u"+currCoord[1]);
				
				//add room connection
				roomGrid[currCoord[0]+change[0]][currCoord[1]+change[1]] = addition;
				ArrayList<Level> nexture = new ArrayList<Level>();
				ArrayList<Integer> dirture = new ArrayList<Integer>();
				nexture.add(addition);
				dirture.add(chosenDir);
				current.setNextLevels(nexture,dirture);
				currCoord[0] += change[0];
				currCoord[1] += change[1];
			}else { //find cell to backtrack to
				ends.add(current);
				ArrayList<Integer[]> open = findOpenSpots();
				if(!open.isEmpty()) {
					int j = (int)(Math.random()*open.size());
					currCoord[0] = open.get(j)[0];
					currCoord[1] = open.get(j)[1];
					i++;
				}
			}
		}
		if(!ends.contains(roomGrid[currCoord[0]][currCoord[1]]))ends.add(roomGrid[currCoord[0]][currCoord[1]]);
		if(!exits.isEmpty()) {
			ArrayList<Level> nexture = new ArrayList<Level>();
			ArrayList<Integer> dirture = new ArrayList<Integer>();
			nexture.add(exits.get(0));
			dirture.add(Passage.FLOOR);
			roomGrid[currCoord[0]][currCoord[1]].setNextLevels(nexture,dirture);
		}
	}
	
	//choose direction to move into
	private int[] moveDirection(int[] coords) {
		//create list of all dirs to move into and remove unavailable ones
		ArrayList<Integer> dirs = new ArrayList<Integer>();
		for(int d=0; d<4; d++)dirs.add(d);
		if(coords[0]==0)dirs.remove(Integer.valueOf(Passage.LEFT));
		if(coords[1]==0)dirs.remove(Integer.valueOf(Passage.TOP));
		if(coords[0]==roomGrid.length-1)dirs.remove(Integer.valueOf(Passage.RIGHT));
		if(coords[1]==roomGrid[0].length-1)dirs.remove(Integer.valueOf(Passage.BOTTOM));

		for(int d=dirs.size()-1; d>=0; d--) {
			int[] change = passageVector(dirs.get(d));
			if(roomGrid[coords[0]+change[0]][coords[1]+change[1]]!=null) {
				dirs.remove(d);
			}
		}

		// {direction, x change, y change, num options}
		int[] result = new int[4];
		if(!dirs.isEmpty()) {
			//select direction randomly
			result[0]=dirs.get((int)(Math.random()*dirs.size()));
			int[] vec = passageVector(result[0]);
			result[1] = vec[0];
			result[2] = vec[1];
			result[3] = dirs.size();
		}
		return result;
	}
	
	//return a list of all cells that have open cells next to them
	public ArrayList<Integer[]> findOpenSpots(){
		ArrayList<Integer[]> open = new ArrayList<Integer[]>();
		for(int i=0; i<roomGrid.length; i++) {
			for(int j=0; j<roomGrid[i].length; j++) {
				int count = moveDirection(new int[] {i, j})[3];
				if(roomGrid[i][j]!=null && count>0) {
					open.add(new Integer[] {i, j});
				}
			}
		}
		return open;
	}
	
	
	//calculate weights of each theme at every cell
	public ThemeSet[][] generateTheme(){
		ThemeSet[][] field = new ThemeSet[layout.rows][layout.cols];
		Iterator<String> it = layout.presentThemes.themeIterator();
		while(it.hasNext()) {
			String theme = it.next();
			if(layout.presentThemes.weigh(theme) > 0) {
				int sr = (int)(Math.random()*layout.rows);
				int sc = (int)(Math.random()*layout.cols);
				for(int r=0; r<layout.rows; r++) {
					for(int c=0; c<layout.cols; c++) {
						if(field[r][c] == null) {
							field[r][c] = new ThemeSet();
						}
						field[r][c].addTheme(theme,Math.min(2,1/(Math.sqrt(Math.pow(sr-r,2)+Math.pow(sc-c,2)))));
					}
				}
			}
		}
		/*if(layout.nameSub.equals("Layout1")){
			for(int r=0; r<layout.rows; r++) {
				for(int c=0; c<layout.cols; c++) {
					for(int i=0; i<layout.themes.length; i++) {
						System.out.print(((int)(field[r][c][i]*100))/100.0+",");
					}
					System.out.print("   ");
				}
				System.out.println();
			}
		}*/
		return field;
	}
	
	//removes data from all levels
	public void wipeFloor() {
		for(int i=0; i<roomGrid.length; i++) {
			for(int j=0; j<roomGrid.length; j++) {
				if(roomGrid[i][j]!=null) {
					roomGrid[i][j].remove();
				}
			}
		}
	}
	
	public Store getStore() {return store;}
	
	//finds the room belonging to the given code
	public Level findRoom(String code) {
		/*(Level curr = firstLevel;
		String cid = curr.getID();
		//crawl through rooms, looking for next one based on matching id
		for(int i=1; i<code.length(); i++) {
			for(int j=0; j<curr.getPassages().size(); j++) {
				if(curr.getPassages().get(j).entranceAt(curr)) {
					Level exit = curr.getPassages().get(j).getExit();
					String oid = exit==null?cid:exit.getID();
					if(oid.substring(0,i).equals(cid)) {
						curr = curr.getPassages().get(j).getExit();
						cid = oid;
					}
				}
			}
			if(cid.length() < i)return null;
		}
		return curr;
	}*/
		for(int i=0; i<roomGrid.length; i++) {
			for(int j=0; j<roomGrid[i].length; j++) {
				if(roomGrid[i][j]!=null && roomGrid[i][j].getID().equals(code)) {
					return roomGrid[i][j];
				}
			}
		}
		return null;
	}
	
	public void addEntrance(Store s) {
		entrances.add(s);
	}
	public void addExit(Store s) {
		exits.add(s);
	}
	//add an exit into a store to every dead end
	public void buildExits() {
		if(exits.isEmpty())return;
		for(int i=0; i<ends.size(); i++) {
			Store choice = exits.get((int)(Math.random()*exits.size()));
			Passage p = new Passage(game,board,ends.get(i),choice,Passage.FLOOR,0,50);
			ends.get(i).passageways.add(p);
			choice.addEntrance(p);
		}

	}
	public int numExits() {return exits.size();}
	public String getID() {return id;}
	/*public void addPrev(Floor f) {prevs.add(f);}
	public void addNext(Floor f) {nexts.add(f);}
	public ArrayList<Floor> getPrev() {return prevs;}
	public ArrayList<Floor> getNext() {return nexts;}*/
	
	//get the change in x and y indices for a given direction
	public int[] passageVector(int dir) {
		if(dir==Passage.TOP) {
			return new int[]{0,-1};
		}else if(dir==Passage.BOTTOM) {
			return new int[] {0,1};
		}else if(dir==Passage.RIGHT) {
			return new int[] {1,0};
		}else if(dir==Passage.LEFT) {
			return new int[] {-1,0};
		}else {
			return new int[] {0,0};
		}
		
	}
	
	public Level generateRoom(HashMap<Room,Integer> roomWeights, ThemeSet themeWeights, String id) {
		//unpack weights for valid rooms
		ArrayList<Room> levels = new ArrayList<Room>();
		ArrayList<Double> counts = new ArrayList<Double>();
		Iterator<Room> it = roomWeights.keySet().iterator();
		double sum = 0;
		while(it.hasNext()) {
			Room classlvl = it.next();
			double roomWeight = roomWeights.get(classlvl) * classlvl.neededThemes.affinityWith(themeWeights);
			
			//check that theme weights are met, only add rooms to options that meet themes
			if(classlvl.neededThemes.metBy(themeWeights)) {
				levels.add(classlvl);
				sum += roomWeight;
				counts.add(sum);
			}
		}
		Level addition;
		Room chosenlvl;
		//no valid rooms, use blank
		if(sum==0) {
			chosenlvl = board.rooms.get("RoomBlank");
		}else {
			//choose a level
			double chosen = (Math.random()*sum);
			int find = 0;
			for(find = 0; find<counts.size() && counts.get(find)<chosen; find++);
			chosenlvl = levels.get(find);
		}
		switch(chosenlvl.roomType) {
		case Room.STAGE:
			addition = new Level(game, board, this, chosenlvl, themeWeights, id);
			break;
		case Room.STORE:
			addition = new Store(game, board, this, chosenlvl, themeWeights, id);
			break;
		case Room.TRAIN:
			addition = new Training(game, board, this, chosenlvl, themeWeights, id);
			break;
		default:
			addition = new Level(game, board, this, chosenlvl, themeWeights, id);
		}

		return addition;
	}
	
	public String toString() {
		String ret = "";
		String[] dirs = {"^","v",">","<","x","."};
		for(int i=0; i<roomGrid.length; i++) {
			for(int j=0; j<roomGrid[i].length; j++) {
				Level room = roomGrid[j][i];
				if(room==null) {
					ret += "-";
				}else {
					//ret += room;
					String rethere = ret;
					ArrayList<Passage> passs = room.getPassages();
					for(int p=0; p<passs.size(); p++) {
						Passage pass = passs.get(p);
						if(pass.entranceAt(room))ret += dirs[pass.getEntranceDirection()];
					}
					if(rethere.equals(ret))ret += "O";
				}
				ret+="  ";
			}
			ret += "\n";
		}
		return ret;
	}
}
