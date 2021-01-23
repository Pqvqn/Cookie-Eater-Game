package ui;

import java.awt.*;
import java.util.*;

import ce3.*;
import menus.*;
import menus.MenuButton.*;

public class UITitleScreen extends UIElement{

	private UIRectangle backing;
	private UIText title;
	private boolean visible;
	private SubmenuHandler menuHandler;
	
	public UITitleScreen(Game frame, int x, int y) {
		super(frame, x, y);
		parts.add(backing = new UIRectangle(game,0,0,game.board.X_RESOL,game.board.Y_RESOL,Color.GRAY,true));
		parts.add(title = new UIText(game,200,350,"Cookie Eater 3",Color.WHITE,new Font("Arial",Font.BOLD,160)));
		makeButtons();
	}
	
	public void makeButtons() {
		Board board = game.board;
		OnClick oc;
		menuHandler = new SubmenuHandler("MAIN");
		
		MenuButton start = new MenuButton(game, this, null, new String[] {"START"}, false, 1300,700,400,200);
		oc = () -> {
			//start game
			this.hide();
		};
		start.setClick(oc);
		menuHandler.addButton("MAIN",start);

		MenuButton dungeon = new MenuButton(game, this, null, new String[] {"Dungeon: Vaults","Dungeon: Inners"}, false, 800,700,400,200);
		oc = () -> {
			//start game
			board.loadDungeon(dungeon.currentState());
		};
		dungeon.setClick(oc);
		menuHandler.addButton("MAIN",dungeon);
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
