package ui;

import java.awt.*;

import ce3.*;
import menus.*;
import menus.MenuButton.*;

public class UITitleScreen extends UIElement{

	private UIRectangle backing;
	private MenuButton start;
	private boolean shown;
	
	public UITitleScreen(Board frame, int x, int y) {
		super(frame, x, y);
		parts.add(backing = new UIRectangle(board,0,0,board.X_RESOL,board.Y_RESOL,Color.GRAY,true));
		
		start = new MenuButton(board, this, null, new String[] {"START"}, false, 1300,700,400,200);
		OnClick oc = () -> {
			//start game
			this.hide();
		};
		start.setClick(oc);
	}
	
	//display title screen and lock board
	public void show() {
		if(shown)return;
		shown = true;
		board.draw.addUI(this);
		board.show_title = true;
		start.show();
	}
	//remove title screen and free board
	public void hide() {
		if(!shown)return;
		shown = false;
		board.draw.removeUI(this);
		board.show_title = false;
		start.hide();
	}

}
