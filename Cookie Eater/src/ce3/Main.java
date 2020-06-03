package ce3;


public class Main {
	
	
	public static final int LEVELS = 0, PVP = 1;
	private static int mode;
	
	public static void main(String[] args) {
		mode = LEVELS;
		new Board(mode);
	}
}
