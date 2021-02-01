package menus;


import ce3.*;

public class Menu {

	protected Board board;
	
	public Menu(Board frame) {
		board = frame;
		board.menus.add(this);
	}
	
	public void keyPress(int key) {
		
	}
	public boolean inAction() {
		return board.menus.contains(this);
	}
	
	public void close() {
		board.menus.remove(this);
	}
	
	public void reopen() {
		board.menus.add(this);
	}
	
}
