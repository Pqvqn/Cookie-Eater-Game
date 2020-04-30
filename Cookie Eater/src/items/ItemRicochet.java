package items;
import java.util.*;

import ce3.*;

public class ItemRicochet extends Item{
	
	private double maxRad;
	private ArrayList<SummonExplosion> booms;
	
	public ItemRicochet(Board frame) {
		super(frame);
		maxRad = 120;
		booms = new ArrayList<SummonExplosion>();
		name = "Ricochet";
		desc="Creates explosion on impact.`Amplify: Larger explosions";
	}
	public void prepare() {

	}
	public void initialize() {

	}
	public void execute() {
		if(checkCanceled())return;
		for(int i=0; i<booms.size(); i++) {
			booms.get(i).execute();
		}
		for(int i=0; i<player.getSummons().size(); i++) {
			Summon sm = player.getSummons().get(i);
			if(sm.hitWall() && sm.getClass()!=SummonExplosion.class) {
				booms.add(0,new SummonExplosion(board,board.player,maxRad,sm.getEdgeX(),sm.getEdgeY()));
				player.addSummon(booms.get(0));
				booms.get(0).prepare();
				booms.get(0).initialize();
			}
		}
	}
	public void end(boolean interrupted) {
		for(int i=0; i<booms.size(); i++) {
			booms.get(i).end(false);
			player.removeSummon(booms.get(i));
			booms.remove(i);
			i--;
		}
	}
	public void bounce(boolean x, boolean y) {
		booms.add(0,new SummonExplosion(board,board.player,maxRad,player.getX(),player.getY()));
		player.addSummon(booms.get(0));
		booms.get(0).prepare();
		booms.get(0).initialize();
	}

	public void amplify() {
		super.amplify();
		maxRad+=100;
		for(int i=0; i<booms.size(); i++) {
			booms.get(i).setMax(maxRad);
		}
	}
	public void deamplify() {
		super.deamplify();
		maxRad-=100;
		for(int i=0; i<booms.size(); i++) {
			booms.get(i).setMax(maxRad);
		}
	}
}
