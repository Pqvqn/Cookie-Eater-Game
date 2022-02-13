package menus;

import java.awt.event.*;

import ce3.Game;

public class PointClicker implements MouseListener, MouseMotionListener{

	Game game;
	int[] lastpoint;
	
	public PointClicker(Game frame) {
		game = frame;
	}
	
	public void activate(boolean yes) {
		if(yes) {
			game.addMouseListener(this);
			game.addMouseMotionListener(this);
		}else {
			game.removeMouseListener(this);
			game.removeMouseMotionListener(this);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(lastpoint==null || lastpoint[0]!=e.getX() || lastpoint[1]!=e.getY()){
			System.out.print(e.getX() + ";"+ e.getY()+";");
			if(lastpoint == null)lastpoint = new int[2];
			lastpoint[0] = e.getX();
			lastpoint[1] = e.getY();
		}else {
			System.out.println();
			activate(false);
		}

		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
