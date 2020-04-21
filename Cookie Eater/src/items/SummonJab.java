package items;

import java.awt.*;

import ce3.*;

public class SummonJab extends Summon{
	
	private int range;
	
	public SummonJab(Board frame, Eater summoner, int r) {
		super(frame, summoner);
		range = r;
	}
	
	public void prepare() {
		
	}
	public void initialize() {
		
	}
	public void execute() {
			
	}
	public void end(boolean interrupted) {
			
	}
	public void paint(Graphics2D g2) {
		range = 40;
		x=player.getX();
		y=player.getY();
		g2.setColor(Color.WHITE);
		g2.rotate(playerAngle(),x,y);
		g2.fillRect(x-50,y-5,100,10);
	}
}
