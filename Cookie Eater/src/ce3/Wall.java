package ce3;


public class Wall {
	
	//private Board board;
	int x,y;
	int w,h;
	
	public Wall(Board frame, int xPos, int yPos, int width, int height) {
		//board = frame;
		x = xPos;
		y = yPos; 
		w = width;
		h = height;
	}
	
	public int getX() {return x;}
	public int getY() {return y;}
	public int getW() {return w;}
	public int getH() {return h;}

}
