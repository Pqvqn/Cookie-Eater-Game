package levels;

import ce3.*;
import cookies.*;

public class Store1 extends FloorEntrance{

	public Store1(Board frame) {
		super(frame);
		minDecay = Integer.MAX_VALUE;
		maxDecay = Integer.MAX_VALUE;
	}
	public void placeCookies(){
		
		board.cookies.add(new ShieldCookie(board,(int)(.5+startx-150),(int)(.5+starty-200)));
		board.cookies.add(new ShieldCookie(board,(int)(.5+startx+150),(int)(.5+starty-200)));
		
		board.cookies.add(new StatCookie(board,(int)(.5+startx-300),(int)(.5+starty+200)));
		board.cookies.add(new StatCookie(board,(int)(.5+startx+300),(int)(.5+starty+200)));
		board.cookies.add(new StatCookie(board,(int)(.5+startx),(int)(.5+starty+200)));
		
		board.scoreToWin = 2;
	}
	public void build() {
		super.build();
	}
	
}
