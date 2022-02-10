package menus;

import java.awt.event.*;

import ce3.Game;

public class PointClicker implements MouseListener, MouseMotionListener{

	Game game;
	
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
		// TODO Auto-generated method stub
		System.out.println(e.getX() + ","+ e.getY());
		
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
