package levels;

import java.util.*;

import ce3.*;
import cookies.*;
import entities.*;
import items.*;
import mechanisms.*;

public abstract class Store extends Level{

	protected int[][][] vendorSpaces; //spaces for vendor and their items for sale (first coordinate pair for vendor)
	protected int[][] passerbySpaces; //spaces for passerbys
	protected int[][] mechanicSpaces; //spaces for mechanic and stat change cookies
	
	public Store(Game frame, Board gameboard, String id) {
		super(frame,gameboard,id);
		minDecay = Integer.MAX_VALUE;
		maxDecay = Integer.MAX_VALUE;
	}
	public Store(Game frame, Board gameboard, ArrayList<Level> prev, ArrayList<Level> next, SaveData sd) {
		super(frame, gameboard, prev, next, sd);
		ArrayList<Object> vendor_space_data = sd.getData("vendorspaces");
		if(vendor_space_data!=null) {
			int vn = sd.getInteger("vendorspacenum",0);
			int[][][] vs = new int[vn][vendor_space_data.size()/vn/2][2];
			int count = 0;
			for(int i=0; i<vs.length; i++) {
				for(int j=0; j<vs[i].length; j++) {
					for(int h=0; h<vs[i][j].length; h++) {
						vs[i][j][h] = Integer.parseInt(vendor_space_data.get(count).toString());
						count++;
					}
				}
			}
			vendorSpaces = vs;
		}
		ArrayList<Object> passerby_space_data = sd.getData("passerbyspaces");
		if(passerby_space_data!=null) {
			int[][] ps = new int[passerby_space_data.size()/2][2];
			int count = 0;
			for(int i=0; i<ps.length; i++) {
				for(int j=0; j<ps[i].length; j++) {
					ps[i][j] = Integer.parseInt(passerby_space_data.get(count).toString());
					count++;
				}
			}
			passerbySpaces = ps;
		}
		ArrayList<Object> mechanic_space_data = sd.getData("mechanicspaces");
		if(mechanic_space_data!=null) {
			int[][] ms = new int[mechanic_space_data.size()/2][2];
			int count = 0;
			for(int i=0; i<ms.length; i++) {
				for(int j=0; j<ms[i].length; j++) {
					ms[i][j] = Integer.parseInt(mechanic_space_data.get(count).toString());
					count++;
				}
			}
			mechanicSpaces = ms;
		}
	}

	public SaveData getSaveData() {
		SaveData data = super.getSaveData();
		int ci = 0;
		if(vendorSpaces!=null) {
			data.addData("vendorspacenum",vendorSpaces.length);
			for(int i=0; i<vendorSpaces.length; i++) {
				for(int j=0; j<vendorSpaces[i].length; j++) {
					for(int h=0; h<vendorSpaces[i][j].length; h++) {
						data.addData("vendorspaces",vendorSpaces[i][j][h],ci++);
					}
				}
			}
		}
		ci=0;
		if(passerbySpaces!=null) {
			for(int i=0; i<passerbySpaces.length; i++) {
				for(int j=0; j<passerbySpaces[i].length; j++) {
					data.addData("passerbyspaces",passerbySpaces[i][j],ci++);
				}
			}
		}
		ci=0;
		if(mechanicSpaces!=null) {
			for(int i=0; i<mechanicSpaces.length; i++) {
				for(int j=0; j<mechanicSpaces[i].length; j++) {
					data.addData("mechanicspaces",mechanicSpaces[i][j],ci++);
				}
			}
		}

		return data;
	}
	public boolean haltEnabled() {return true;}
	public boolean specialsEnabled() {return false;}
	public boolean installPickups() {return true;}
	public boolean takeDamage() {return false;}
	
	//places not-npc owned purchasable cookies on the board
	public void placeCookies(){
		board.player().setScoreToWin(2);
	}
	
	
	public void build() {
		Wall rig,lef;
		board.walls.add(new Wall(game,board,0,0,board.x_resol,BORDER_THICKNESS)); //add border walls
		board.walls.add(lef = new Wall(game,board,0,0,BORDER_THICKNESS,board.y_resol));
		board.walls.add(new Wall(game,board,0,board.y_resol-BORDER_THICKNESS,board.x_resol,BORDER_THICKNESS));
		board.walls.add(rig = new Wall(game,board,board.x_resol-BORDER_THICKNESS,0,BORDER_THICKNESS,board.y_resol));
		
		board.walls.add(new Wall(game,board,0,0,625,150));
		board.walls.add(new Wall(game,board,0,board.y_resol-150,625,150));
		board.walls.add(new Wall(game,board,board.x_resol-625,0,625,150));
		board.walls.add(new Wall(game,board,board.x_resol-625,board.y_resol-150,625,150));
		
		board.walls.add(new Wall(game,board,625-75,0,75,350));
		board.walls.add(new Wall(game,board,625-75,board.y_resol-350,75,350));
		board.walls.add(new Wall(game,board,board.x_resol-625,0,75,350));
		board.walls.add(new Wall(game,board,board.x_resol-625,board.y_resol-350,75,350));
		
		board.walls.add(new Wall(game,board,0,0,230,250));
		board.walls.add(new Wall(game,board,board.x_resol-230,0,230,250));
		board.walls.add(new Wall(game,board,0,board.y_resol-250,230,250));
		board.walls.add(new Wall(game,board,board.x_resol-230,board.y_resol-250,230,250));
		
		board.walls.add(new Wall(game,board,0,0,130,350));
		board.walls.add(new Wall(game,board,board.x_resol-130,0,130,350));
		board.walls.add(new Wall(game,board,0,board.y_resol-350,130,350));
		board.walls.add(new Wall(game,board,board.x_resol-130,board.y_resol-350,130,350));
		
		//add cases to every item
		int caseWidth = 80;
		for(int i=0; i<vendorSpaces.length; i++) {
			for(int j=1; j<vendorSpaces[i].length; j++) {
				//board.walls.add(new WallCase(game,board,vendorSpaces[i][j][0]-caseWidth/2,vendorSpaces[i][j][1]-caseWidth/2,caseWidth,caseWidth));
				board.mechanisms.add(new WallCase(game,board,vendorSpaces[i][j][0],vendorSpaces[i][j][1],caseWidth/2));
			}
		}
		
		board.walls.add(breakWall(rig,false,board.y_resol/2-100,board.y_resol/2+100));
		board.walls.add(breakWall(lef,false,board.y_resol/2-100,board.y_resol/2+100));

		for(int i=0; i<passageways.size(); i++) {
			addMechanism(passageways.get(i));
		}
		board.mechanisms.add(new WallDoor(game,board,board.x_resol-BORDER_THICKNESS,board.y_resol/2-100,BORDER_THICKNESS/2,200,2));
		
	}
	
	//creates passages to next levels
	public void buildPassages(ArrayList<Level> nextLevels, int size){
		Passage p = new Passage(game,board,this,nextLevels.get(0),Passage.RIGHT,board.y_resol/2,size);
		passageways.add(p);
		nextLevels.get(0).addEntrance(p);
	}
	
	public void spawnEnemies() {}
	
	//puts cookie item on board
	protected void placeItem(int x, int y, String i, double p) {
		Item b = Item.generateItem(game,board,i);
		board.cookies.add(new CookieItem(game, board, x, y, b, p));
	}
	protected void addToCatalogue(ArrayList<String> I, String i, ArrayList<Double> P, double p) {
		I.add(i);
		P.add(p);
	}
	public void spawnNpcs() {
		//retrieve the mechanic
		Explorer mechanic = null;
		for(int i=0; i<board.npcs.size() && mechanic==null; i++) {
			Explorer testnpc = board.npcs.get(i);
			if(testnpc.getName().equals("Mechanic") && !board.present_npcs.contains(testnpc)) {
				mechanic = testnpc;
				board.present_npcs.add(mechanic);
				mechanic.spawn();
			}
		}
		
		int vendors=0,passerbys=0;
		for(int i=0; i<board.present_npcs.size(); i++) {
			Explorer e = board.present_npcs.get(i);
			if(e==null) {
				
			}else if(e instanceof ExplorerMechanic) { //put mechanic in right place
				e.sellWares(mechanicSpaces);
			}else if(e.getState()==Explorer.VENDOR) { //put vendors in shop locations
				if(vendors<vendorSpaces.length)e.sellWares(vendorSpaces[vendors++]);
			}else if(e.getState()==Explorer.VENTURE || e.getState()==Explorer.STOP || e.getState()==Explorer.STAND) { //put npcs that are passing through in standby areas
				if(passerbys<passerbySpaces.length)e.standBy(passerbySpaces[passerbys++]);
			}
		}
	}
	public void removeNpcs() {
		for(int i=0; i<board.present_npcs.size(); i++) {
			board.present_npcs.get(i).packUp(); //all npcs pick up cookies for sale
		}
	}
	
}
