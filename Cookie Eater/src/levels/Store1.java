package levels;

import ce3.*;
import cookies.*;

public class Store1 extends FloorEntrance{

	public Store1(Board frame) {
		super(frame);
		minDecay = 60;
		maxDecay = 1800;
	}
	public void placeCookies(){
		board.cookies.add(new Cookie(board,(int)(.5+startx),(int)(.5+starty-200)));
		
		board.cookies.add(new ShieldCookie(board,(int)(.5+startx-100),(int)(.5+starty-200)));
		board.cookies.add(new ShieldCookie(board,(int)(.5+startx+100),(int)(.5+starty-200)));
		
		board.scoreToWin = 1;
	}
	public void build() {
		super.build();
	}
	
}
