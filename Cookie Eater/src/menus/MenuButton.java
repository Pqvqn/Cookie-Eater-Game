package menus;

import java.awt.*;
import java.awt.event.*;

import ce3.*;
import ui.*;

public class MenuButton implements MouseListener, MouseMotionListener{

	private Board board;
	private OnClick onClick;
	private Rectangle bounding;
	private UIButton ui;
	private String text;
	private boolean visible;
	
	public MenuButton(Board frame, OnClick oc, String t, int x, int y, int w, int h) {
		board = frame;
		onClick = oc;
		bounding = new Rectangle(x,y,w,h);
		text = t;
		ui = new UIButton(board,this);
		visible = false;
	}
	
	public void show(boolean s) {
		if(s!=visible) {
			if(s) {
				board.draw.addUI(ui);
				board.addMouseListener(this);
				board.addMouseMotionListener(this);
			}else {
				board.draw.removeUI(ui);
				board.removeMouseListener(this);
				board.removeMouseMotionListener(this);
			}
		}
		visible = s;
	}
	
	public void show() {
		show(true);
	}
	
	public void hide() {
		show(false);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
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

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(bounding.contains(new Point(e.getX(),e.getY()))) {
			ui.highlight(true);
		}else {
			if(ui.isHighlighted()) {
				ui.highlight(false);
			}
		}

	}
	
	
	public Rectangle bounds() {return bounding;}
	public String text() {return text;}
	
	public interface OnClick{
		public void click();
	}
}
