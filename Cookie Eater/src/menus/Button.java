package menus;

import java.awt.*;
import java.awt.event.*;

import ce3.*;
import ui.*;

public class Button implements MouseListener{

	private Board board;
	private OnClick onClick;
	private Rectangle bounding;
	private UIButton ui;
	
	public Button(Board frame, OnClick oc, int x, int y, int w, int h) {
		board = frame;
		onClick = oc;
		bounding = new Rectangle(x,y,w,h);
		ui = new UIButton();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(bounding.contains(new Point(e.getX(),e.getY()))) {
			onClick.click();
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
	public interface OnClick{
		public void click();
	}
}
