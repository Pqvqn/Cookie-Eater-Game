package levels;

import java.util.*;

import ce3.*;
import cookies.*;
import entities.*;
import items.*;
import mechanisms.*;

public abstract class Store extends Level{

	protected ArrayList<String> catalogue;
	protected ArrayList<Double> prices;
	protected double defaultItemCost;
	protected double shieldCost;
	protected double installCost;
	protected int shieldNum;
	protected int[][][] vendorSpaces; //spaces for vendor and their items for sale (first coordinate pair for vendor)
	protected int[][] passerbySpaces; //spaces for passerbys
	protected int[][] mechanicSpaces; //spaces for mechanic and stat change cookies
	
	public Store(Game frame, Board gameboard) {
		super(frame,gameboard);
		catalogue = new ArrayList<String>();
		prices = new ArrayList<Double>();
		startx = board.X_RESOL/2;
		starty = board.Y_RESOL/2;
		minDecay = Integer.MAX_VALUE;
		maxDecay = Integer.MAX_VALUE;
		shieldNum = 6;
	}
	
	public boolean haltEnabled() {return true;}
	public boolean specialsEnabled() {return false;}
	public boolean installPickups() {return true;}
	public boolean takeDamage() {return false;}
	
	//places purchasable cookies on the board
	public void placeCookies(){
		
		//all moved to npcs
		
		/*//shields
		int[][] shieldSpawns = {{board.X_RESOL/2-200,150} , {board.X_RESOL/2+200,150} , {board.X_RESOL/2,150} ,
				{board.X_RESOL/2-200,250} , {board.X_RESOL/2+200,250} , {board.X_RESOL/2,250}};
		for(int i=0; i<shieldNum && i<shieldSpawns.length; i++) {
			board.cookies.add(new CookieShield(board, shieldSpawns[i][0], shieldSpawns[i][1], shieldCost));
		}*/
	
		/*//stats
		board.cookies.add(new CookieStat(board,board.X_RESOL-390,315));
		board.cookies.add(new CookieStat(board,board.X_RESOL-390,board.Y_RESOL-315));
		board.cookies.add(new CookieStat(board,board.X_RESOL-185,board.Y_RESOL/2));*/
		
		/*//items
		int i = (int)(Math.random()*catalogue.size());
		placeItem(390,315,catalogue.get(i),prices.get(i));
		i = (int)(Math.random()*catalogue.size());
		placeItem(390,board.Y_RESOL-315,catalogue.get(i),prices.get(i));
		i = (int)(Math.random()*catalogue.size());
		placeItem(185,board.Y_RESOL/2,catalogue.get(i),prices.get(i));*/
		
		/*//pickups
		int[][] pickupSpawns = {{board.X_RESOL/2-200,board.Y_RESOL-150} , {board.X_RESOL/2+200,board.Y_RESOL-150} , {board.X_RESOL/2,board.Y_RESOL-150} ,
				{board.X_RESOL/2-200,board.Y_RESOL-250} , {board.X_RESOL/2+200,board.Y_RESOL-250} , {board.X_RESOL/2,board.Y_RESOL-250}};
		for(int j=0; !board.player().getPickups().isEmpty() && j<pickupSpawns.length; j++) {
			CookieItem c = board.player().getPickups().remove(0);
			c.setPos(pickupSpawns[j][0],pickupSpawns[j][1]);
			c.setPrice(installCost);
			board.cookies.add(c);
		}*/
		
		
		board.player().setScoreToWin(2);
	}
	
	
	public void build() {
		board.walls.add(new Wall(game,board,0,0,board.X_RESOL,BORDER_THICKNESS)); //add border walls
		board.walls.add(new Wall(game,board,0,0,BORDER_THICKNESS,board.Y_RESOL));
		board.walls.add(new Wall(game,board,0,board.Y_RESOL-BORDER_THICKNESS,board.X_RESOL,BORDER_THICKNESS));
		board.walls.add(new Wall(game,board,board.X_RESOL-BORDER_THICKNESS,0,BORDER_THICKNESS,board.Y_RESOL));
		
		board.walls.add(new Wall(game,board,0,0,625,150));
		board.walls.add(new Wall(game,board,0,board.Y_RESOL-150,625,150));
		board.walls.add(new Wall(game,board,board.X_RESOL-625,0,625,150));
		board.walls.add(new Wall(game,board,board.X_RESOL-625,board.Y_RESOL-150,625,150));
		
		board.walls.add(new Wall(game,board,625-75,0,75,350));
		board.walls.add(new Wall(game,board,625-75,board.Y_RESOL-350,75,350));
		board.walls.add(new Wall(game,board,board.X_RESOL-625,0,75,350));
		board.walls.add(new Wall(game,board,board.X_RESOL-625,board.Y_RESOL-350,75,350));
		
		board.walls.add(new Wall(game,board,0,0,230,250));
		board.walls.add(new Wall(game,board,board.X_RESOL-230,0,230,250));
		board.walls.add(new Wall(game,board,0,board.Y_RESOL-250,230,250));
		board.walls.add(new Wall(game,board,board.X_RESOL-230,board.Y_RESOL-250,230,250));
		
		board.walls.add(new Wall(game,board,0,0,130,350));
		board.walls.add(new Wall(game,board,board.X_RESOL-130,0,130,350));
		board.walls.add(new Wall(game,board,0,board.Y_RESOL-350,130,350));
		board.walls.add(new Wall(game,board,board.X_RESOL-130,board.Y_RESOL-350,130,350));
		
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
	
	public double getShieldCost() {return shieldCost;}
	
	
	//puts cookie item on board
	protected void placeItem(int x, int y, String i, double p) {
		Item b = generateItem(game,i);
		board.cookies.add(new CookieItem(game, board, x, y, b, p));
	}
	protected void configureCatalogue(double def, ArrayList<String> I,ArrayList<Double> P) {	
		addToCatalogue(I,"Boost",P,def*1);
		//addToCatalogue(I,"Bounce",P,def*.9,C,new Color(0,150,200));
		addToCatalogue(I,"Circle",P,def*1);
		addToCatalogue(I,"Chain",P,def*1.3);
		addToCatalogue(I,"Field",P,def*1.1);
		addToCatalogue(I,"Hold",P,def*1.1);
		addToCatalogue(I,"Recycle",P,def*1.4);
		addToCatalogue(I,"Shield",P,def*1.3);
		addToCatalogue(I,"Slowmo",P,def*1.4);
		addToCatalogue(I,"Ghost",P,def*1.4);
		addToCatalogue(I,"Return",P,def*1.1);
		addToCatalogue(I,"Teleport",P,def*1.2);
		//addToCatalogue(I,"Jab",P,def*1.1);
		addToCatalogue(I,"Repeat",P,def*1.2);
		//addToCatalogue(I,"Projectile",P,def*1.3);
		addToCatalogue(I,"Rebound",P,def*1.2);
		addToCatalogue(I,"Clone",P,def*1.3);
		addToCatalogue(I,"Ricochet",P,def*1.1);
		//addToCatalogue(I,"Slash",P,def*1.1);
		//addToCatalogue(I,"Wall",P,def*1.2);
		addToCatalogue(I,"Shrink",P,def*1.1);
		//addToCatalogue(I,"Hook",P,def*1.3);
		addToCatalogue(I,"Autopilot",P,def*1.3);
		addToCatalogue(I,"Flow",P,def*1.3);
		addToCatalogue(I,"Recharge",P,def*1.4);
		addToCatalogue(I,"Melee",P,def*1.1);
		addToCatalogue(I,"Projectile",P,def*1.1);
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
