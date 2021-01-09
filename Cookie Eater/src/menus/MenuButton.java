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
	private boolean visible;
	private boolean usesImage;
	private String[] states;
	private int currState;
	
	public MenuButton(Board frame, OnClick oc, String[] statelist, boolean img, int x, int y, int w, int h) {
		board = frame;
		onClick = oc;
		bounding = new Rectangle(x,y,w,h);
		states = statelist;
		currState = 0;
		visible = false;
		usesImage = img;
		ui = new UIButton(board,this);

	}
	
	public void setClick(OnClick oc) {onClick = oc;}
	
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
		if(bounding.contains(board.draw.convertPoint(e.getLocationOnScreen()))) {
			currState++;
			if(currState >= states.length)currState = 0;
			ui.trigger(currState);
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
		if(bounding.contains(board.draw.convertPoint(e.getLocationOnScreen()))) {
			ui.highlight(true);
		}else {
			if(ui.isHighlighted()) {
				ui.highlight(false);
			}
		}

	}
	
	
	public Rectangle bounds() {return bounding;}
	public int currentState() {return currState;}
	public String getState() {return states[currState];}
	public String[] stateList() {return states;}
	public boolean usesImage() {return usesImage;}
	public void resetState() {currState=0;ui.trigger(currState);}
	
	public interface OnClick{
		public void click();
	}
}
