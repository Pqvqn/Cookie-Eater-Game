package levels;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import ce3.*;
import mechanisms.*;

public abstract class Floor {
	
	protected Game game;
	protected Board board;
	protected Level[][] roomGrid; //grid of room locations
	//protected ArrayList<Floor> prevs;
	//protected ArrayList<Floor> nexts;
	protected ArrayList<Store> entrances;
	protected ArrayList<Store> exits;
	protected HashMap<Class,Integer> roomWeights;
	protected int numRooms;
	protected String id;

	public Floor(Game frame, Board gameboard, int wid, int hei, String floorid) {
		game = frame;
		board = gameboard;
		id = floorid;
		//prevs = new ArrayList<Floor>();
		//nexts = new ArrayList<Floor>();
		entrances = new ArrayList<Store>();
		exits = new ArrayList<Store>();
		roomGrid = new Level[wid][hei];
	}
	
	//creates and connects levels in paths
	public void generateFloor() {
		//int entrances = prevs.size();
		//int exits = nexts.size();
		int[] currCoord = {0,0};
		String id = "";
		for(int i=0; i<Math.min(numRooms,roomGrid.length*roomGrid[0].length); i++)id+="0";
		//unpack weights
		ArrayList<Class> levels = new ArrayList<Class>();
		ArrayList<Integer> counts = new ArrayList<Integer>();
		Iterator<Class> it = roomWeights.keySet().iterator();
		int sum = 0;
		while(it.hasNext()) {
			Class<Level> classlvl = it.next();
			levels.add(classlvl);
			sum += roomWeights.get(classlvl);
			counts.add(sum);
		}
		for(int i=Math.min(numRooms,roomGrid.length*roomGrid[0].length)-1; i>=0; i--) {
			int chosen = (int)(Math.random()*sum);
			int find = 0;
			for(find = 0; find<counts.size() && counts.get(find)<chosen; find++);
			Class<Level> chosenlvl = levels.get(find);
			Level addition = null;
			try {
				addition = (Level)(chosenlvl.getDeclaredConstructor(Game.class, Board.class, Floor.class, String.class).newInstance(game, board, this, id.substring(0,i+1)));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(addition!=null) {
				
				int chosenDir = moveDirection(currCoord);
				int[] change = passageVector(chosenDir);
				
				roomGrid[currCoord[0]][currCoord[1]] = addition;
				ArrayList<Level> nexture = new ArrayList<Level>();
				ArrayList<Integer> dirture = new ArrayList<Integer>();
				if(chosenDir>=0) {
					nexture.add(roomGrid[currCoord[0]+change[0]][currCoord[1]+change[1]]);
					dirture.add(chosenDir);
				}
				addition.setNextLevels(nexture,dirture);
				currCoord[0] += change[0];
				currCoord[1] += change[1];
			}
		}
		if(!exits.isEmpty()) {
			ArrayList<Level> nexture = new ArrayList<Level>();
			ArrayList<Integer> dirture = new ArrayList<Integer>();
			nexture.add(exits.get(0));
			dirture.add(Passage.FLOOR);
			roomGrid[currCoord[0]][currCoord[1]].setNextLevels(nexture,dirture);
		}
		
	}
	
	//choose direction to move into
	private int moveDirection(int[] coords) {
		//create list of all dirs to move into and remove unavailable ones
		ArrayList<Integer> dirs = new ArrayList<Integer>();
		for(int d=-1; d<4; d++)dirs.add(d);
		if(coords[0]==0)dirs.remove(dirs.indexOf(Passage.LEFT));
		if(coords[1]==0)dirs.remove(dirs.indexOf(Passage.TOP));
		if(coords[0]==roomGrid[0].length-1)dirs.remove(dirs.indexOf(Passage.RIGHT));
		if(coords[1]==roomGrid.length-1)dirs.remove(dirs.indexOf(Passage.BOTTOM));
		for(int d=dirs.size()-1; d>=0; d--) {
			int[] change = passageVector(dirs.get(d));
			if(roomGrid[coords[0]+change[0]][coords[1]+change[1]]!=null) {
				dirs.remove(d);
			}
		}
		//select direction randomly
		if(dirs.isEmpty()) {
			return -1;
		}else {
			return dirs.get((int)(Math.random()*dirs.size()));
		}
	}
	
	
	//return Store to use before this Floor
	public Store generateStore() {
		return new Store1(game, board, id);
	}
	
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
	public void buildExit(Store s) {
		exits.add(s);
		int dir = Passage.BOTTOM;
		Passage p = new Passage(game,board,roomGrid[0][roomGrid.length-1],s,Passage.FLOOR,0,50);
		roomGrid[0][roomGrid.length-1].passageways.add(p);
		s.addEntrance(p);
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
}
