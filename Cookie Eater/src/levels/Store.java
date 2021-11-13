package levels;

import java.util.*;

import ce3.*;
import cookies.*;
import entities.*;
import items.*;
import mechanisms.*;

public class Store extends Level{
	
	public Store(Game frame, Board gameboard, Room roomtemplate, String id) {
		super(frame,gameboard,null,roomtemplate,id);
	}
	public Store(Game frame, Board gameboard, Floor floor, SaveData sd) {
		super(frame, gameboard, floor, sd);
	}
	
	//places not-npc owned purchasable cookies on the board
	public void placeCookies(){
		maxScore = 0;
	}
	
	
	public void build() {
		//Wall rig,lef;
		walls.add(new Wall(game,board,0,0,board.x_resol,BORDER_THICKNESS)); //add border walls
		walls.add(new Wall(game,board,0,0,BORDER_THICKNESS,board.y_resol)); //lef
		walls.add(new Wall(game,board,0,board.y_resol-BORDER_THICKNESS,board.x_resol,BORDER_THICKNESS));
		walls.add(new Wall(game,board,board.x_resol-BORDER_THICKNESS,0,BORDER_THICKNESS,board.y_resol)); //rig
		
		walls.add(new Wall(game,board,0,0,625,150));
		walls.add(new Wall(game,board,0,board.y_resol-150,625,150));
		walls.add(new Wall(game,board,board.x_resol-625,0,625,150));
		walls.add(new Wall(game,board,board.x_resol-625,board.y_resol-150,625,150));
		
		walls.add(new Wall(game,board,625-75,0,75,350));
		walls.add(new Wall(game,board,625-75,board.y_resol-350,75,350));
		walls.add(new Wall(game,board,board.x_resol-625,0,75,350));
		walls.add(new Wall(game,board,board.x_resol-625,board.y_resol-350,75,350));
		
		walls.add(new Wall(game,board,0,0,230,250));
		walls.add(new Wall(game,board,board.x_resol-230,0,230,250));
		walls.add(new Wall(game,board,0,board.y_resol-250,230,250));
		walls.add(new Wall(game,board,board.x_resol-230,board.y_resol-250,230,250));
		
		walls.add(new Wall(game,board,0,0,130,350));
		walls.add(new Wall(game,board,board.x_resol-130,0,130,350));
		walls.add(new Wall(game,board,0,board.y_resol-350,130,350));
		walls.add(new Wall(game,board,board.x_resol-130,board.y_resol-350,130,350));
		
		//add cases to every item
		int caseWidth = 80;
		for(int i=0; i<room.vendorSpaces.length; i++) {
			for(int j=1; j<room.vendorSpaces[i].length; j++) {
				//walls.add(new WallCase(game,board,vendorSpaces[i][j][0]-caseWidth/2,vendorSpaces[i][j][1]-caseWidth/2,caseWidth,caseWidth));
				mechanisms.add(new WallCase(game,board,room.vendorSpaces[i][j][0],room.vendorSpaces[i][j][1],caseWidth/2));
			}
		}
		
		//walls.add(breakWall(rig,false,board.y_resol/2-100,board.y_resol/2+100));
		//walls.add(breakWall(lef,false,board.y_resol/2-100,board.y_resol/2+100));

		for(int i=0; i<passageways.size(); i++) {
			Passage p = passageways.get(i);
			p.setMode(this);
			addMechanism(p);
			if(p.isEntrance())mechanisms.add(new WallDoor(game,board,(int)(p.getX()+.5),(int)(p.getY()+.5),p.getWidth()/2,2,false));
		}
		
		//mechanisms.add(new WallDoor(game,board,board.x_resol-BORDER_THICKNESS,board.y_resol/2-100,BORDER_THICKNESS/2,200,2,false));
		loaded = true;
	}
	
	//creates passages to next levels
	public void buildPassages(ArrayList<Level> nextLevels, int size){
		Passage p = new Passage(game,board,this,nextLevels.get(0),Passage.RIGHT,board.y_resol/2,size);
		passageways.add(p);
		nextLevels.get(0).addEntrance(p);
	}
	
	//puts cookie item on board
	protected void placeItem(int x, int y, String i, double p) {
		Item b = Item.generateItem(game,board,i);
		cookies.add(new CookieItem(game, board, x, y, b, p));
	}
	protected void addToCatalogue(ArrayList<String> I, String i, ArrayList<Double> P, double p) {
		I.add(i);
		P.add(p);
	}
	public void spawnNPCs() {
		int passerbys=0,mechanics=0,vendors=0;
		//retrieve the mechanic
		Explorer mechanic = null;
		for(int i=0; i<board.npcs.size() && mechanic==null; i++) {
			Explorer testnpc = board.npcs.get(i);
			if(testnpc.getState() == Explorer.MECHANIC && !board.presentnpcs.contains(testnpc)) {
				mechanic = testnpc;
				board.presentnpcs.add(mechanic);
				mechanic.spawn();
			}
		}
		
		for(int i=0; i<board.presentnpcs.size(); i++) {
			Explorer e = board.presentnpcs.get(i);
			if(e==null) {
				
			}else if(e.getState()==Explorer.MECHANIC && mechanics<room.mechanicSpaces.length) { //put mechanic in right place
				e.sellWares(room.mechanicSpaces);
				mechanics++;
			}else if(e.getState()==Explorer.VENDOR && vendors<room.vendorSpaces.length) { //put vendors in shop locations
				e.sellWares(room.vendorSpaces[vendors++]);
			}else if(e.getState()==Explorer.VENTURE || e.getState()==Explorer.STOP || e.getState()==Explorer.STAND && passerbys<room.passerbySpaces.length) { //put npcs that are passing through in standby areas
				e.standBy(room.passerbySpaces[passerbys++]);
			}else {

			}
		}
	}
	//if all npc slots are taken
	public boolean isFull(Explorer place, boolean vendor, boolean passerby, boolean mechanic) {
		int count = 0;
		for(int i=0; i<board.npcs.size(); i++) {
			Explorer e = board.npcs.get(i);
			if(e!=place && e.getResidence()==this){
				if(mechanic) {
					if(e.getState()==Explorer.MECHANIC)count++;
				}else if(vendor) {
					if(e.getState()==Explorer.VENDOR)count++;
				}else if(passerby) {
					if(e.getState()==Explorer.VENTURE || e.getState()==Explorer.STOP || e.getState()==Explorer.STAND)count++;
				}
			}
		}
		int max = mechanic?1:(vendor?room.vendorSpaces.length:(passerby?room.passerbySpaces.length:0));
		return count >= max;
	}
	public void removeNPCs() {
		for(int i=0; i<board.presentnpcs.size(); i++) {
			board.presentnpcs.get(i).packUp(); //all npcs pick up cookies for sale
		}
	}
	
}
