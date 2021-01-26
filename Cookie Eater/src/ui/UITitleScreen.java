package ui;

import java.awt.*;

import ce3.*;
import menus.*;
import menus.MenuButton.*;

public class UITitleScreen extends UIElement{

	//private UIRectangle backing;
	//private UIText title;
	private boolean visible;
	private SubmenuHandler menuHandler;
	
	public UITitleScreen(Game frame, int x, int y) {
		super(frame, x, y);
		parts.add(new UIRectangle(game,0,0,game.board.X_RESOL,game.board.Y_RESOL,Color.GRAY,true)); //backing
		parts.add(new UIText(game,200,350,"Cookie Eater 3",Color.WHITE,new Font("Arial",Font.BOLD,160))); //title
		makeButtons();
	}
	
	public void makeButtons() {
		//Board board = game.board;
		OnClick oc;
		menuHandler = new SubmenuHandler("MAIN");
		
		MenuButton mode = new MenuButton(game, this, null, new String[] {"Mode: Levels","Mode: PvP"}, false, 300,700,200,200);
		MenuButton dungeon = new MenuButton(game, this, null, new String[] {"Dungeon: Vaults","Dungeon: Inners"}, false, 800,700,200,200);
		MenuButton pcount = new MenuButton(game, this, null, new String[] {"1 P","2 P","3 P","4 P"}, false, 550,700,200,200);
		oc = () -> {
			//switch selected dungeon generation
		};
		dungeon.setClick(oc);
		menuHandler.addButton("MAIN",dungeon);
		oc = () -> {
			//switch selected game mode
		};
		mode.setClick(oc);
		menuHandler.addButton("MAIN",mode);
		oc = () -> {
			//switch number of players
		};
		pcount.setClick(oc);
		menuHandler.addButton("MAIN",pcount);
		
		
		MenuButton start = new MenuButton(game, this, null, new String[] {"START"}, false, 1300,700,400,200);
		oc = () -> {
			int[] modes = {Board.LEVELS,Board.PVP};
			game.ui_set.show(false);
			game.loadDungeon(modes[mode.currentState()],dungeon.currentState(),pcount.currentState()+1);
			//start game, load board from other buttons
			this.hide();
		};
		start.setClick(oc);
		menuHandler.addButton("MAIN",start);
	}
	
	//display title screen and lock board
	public void show() {
		if(visible)return;
		visible = true;
		game.draw.addUI(this);
		menuHandler.showFull(true);
	}
	//remove title screen and free board
	public void hide() {
		if(!visible)return;
		visible = false;
		game.draw.removeUI(this);
		menuHandler.showFull(false);
	}
	
	public boolean isVisible() {
		return visible;
	}

}
