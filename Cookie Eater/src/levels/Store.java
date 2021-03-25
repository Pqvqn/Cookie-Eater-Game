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
	
	public Store(Game frame, Board gameboard) {
		super(frame,gameboard);
		startx = board.x_resol/2;
		starty = board.y_resol/2;
		minDecay = Integer.MAX_VALUE;
		maxDecay = Integer.MAX_VALUE;
	}
	public Store(Game frame, Board gameboard, SaveData sd) {
		super(frame, gameboard, null, sd);
		
		
	}
	public SaveData getSaveData() {
		SaveData data = super.getSaveData();
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
		board.walls.add(new Wall(game,board,0,0,board.x_resol,BORDER_THICKNESS)); //add border walls
		board.walls.add(new Wall(game,board,0,0,BORDER_THICKNESS,board.y_resol));
		board.walls.add(new Wall(game,board,0,board.y_resol-BORDER_THICKNESS,board.x_resol,BORDER_THICKNESS));
		board.walls.add(new Wall(game,board,board.x_resol-BORDER_THICKNESS,0,BORDER_THICKNESS,board.y_resol));
		
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
		int caseWidth = 70;
		for(int i=0; i<vendorSpaces.length; i++) {
			for(int j=1; j<vendorSpaces[i].length; j++) {
				//board.walls.add(new WallCase(game,board,vendorSpaces[i][j][0]-caseWidth/2,vendorSpaces[i][j][1]-caseWidth/2,caseWidth,caseWidth));
				board.mechanisms.add(new WallCase(game,board,vendorSpaces[i][j][0],vendorSpaces[i][j][1],caseWidth/2));
			}
		}
	}
	
	public void spawnEnemies() {}
	
	//puts cookie item on board
	protected void placeItem(int x, int y, String i, double p) {
		Item b = Item.generateItem(game,i);
		board.cookies.add(new CookieItem(game, board, x, y, b, p));
	}
	protected void addToCatalogue(ArrayList<String> I, String i, ArrayList<Double> P, double p) {
		I.add(i);
		P.add(p);
	}
	public void spawnNpcs() {
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
