package ce3;

public abstract class Level{
	
	private double scale;
	private Level next;
	private Board board;
	
	public Level(Board frame, Level nextFloor) {
		next = nextFloor;
		scale = 1;
		board = frame;
	}
	
	public void build() {
		
	}
	public void placeCookies() {
		
	}
	public Level getNext() {
		return next;
	}
	public void setNext(Level newNext) {
		next = newNext;
	}

}
