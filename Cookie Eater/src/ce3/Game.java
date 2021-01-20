package ce3;

import java.awt.Color;
import java.awt.Toolkit;

import javax.swing.*;

import ui.*;

public class Game extends JFrame {

	private static final long serialVersionUID = 1L;
	
	//engine classes
	public Draw draw; //handles graphics
	public Audio audio; //handles sound effects
	public Music music; //handles music/background sound
	
	//fps/calibration
	private long lastFrame; //time of last frame
	private int cycletime;
	private int fpscheck;
	private int true_cycle;
	private int skipframes;
	public boolean check_calibration;
	
	//ui
	public UIFpsCount ui_fps;
	public UISettings ui_set;
	public UITitleScreen ui_tis;
	
	public Game() {
		super("Cookie Eater");
		cycletime=5;
		fpscheck=100;
		skipframes = 0;
		check_calibration = true;
		
		draw = new Draw(this);
		audio = new Audio(this);
        music = new Music(this);
        
        //window settings
  		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  		setUndecorated(false);
  		setExtendedState(JFrame.MAXIMIZED_BOTH);
  		setVisible(true);
  		setFocusable(true);
  		for(int i=0; i<controls.size(); i++) {
  			addKeyListener(controls.get(i));
  		}
  		requestFocus();
  		setSize(Toolkit.getDefaultToolkit().getScreenSize());
  		//setBackground(Color.GRAY);
  		//setForeground(Color.GRAY);
  		
  		add(draw);
  		pack();
  		
  		//ui
		draw.addUI(ui_fps = new UIFpsCount(this,10,10,Color.WHITE));	
		ui_set = new UISettings(this,0,0);
		ui_tis = new UITitleScreen(this,0,0);
		
		ui_tis.show();
		
		
		//run the game
		while(true)
			run(cycletime);
	}
	
	public int getCycle() {return cycletime;}
	public double getAdjustedCycle() {return true_cycle/100.0;}
	public void setCycle(int tim) {cycletime=tim;}
	
	public void run(int time) {
		if(skipframes>0) {
			skipframes--;
		}else {
			updateUI();
			draw.runUpdate(); //update all game objects
		}
		try {
			Thread.sleep(time); //time between updates
		}catch(InterruptedException e){};
	}
	public void freeze(int time) { //freeze-frame for length of time
		time/=getAdjustedCycle();
		skipframes+=time;
	}
	
	public void updateUI() {
		//fps counter
		if(fpscheck--<=0) {
			//fps.update(lastFrame,System.currentTimeMillis());
			true_cycle=(int)(System.currentTimeMillis()-lastFrame); 
			if(check_calibration){
				setCalibrations(getAdjustedCycle());
			}
			lastFrame = System.currentTimeMillis();
			fpscheck=100;
		}
		board.updateUI();

	}
}
