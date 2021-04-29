package ce3;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import ui.*;

public class Game extends JFrame {

	private static final long serialVersionUID = 1L;
	
	//engine classes
	public Draw draw; //handles graphics
	public Audio audio; //handles sound effects
	public Music music; //handles music/background sound
	public HashMap<String,Board> boards; //board save structure
	public Board board; //current board that is running
	public ArrayList<Controls> controls; //all active key listeners/controls
	
	//fps/calibration
	private long lastFrame; //time of last frame
	private int cycletime;
	public int fpscheck;
	private int true_cycle;
	private int skipframes;
	public boolean check_calibration;
	private boolean pause_calibration;
	
	//ui
	public UIFpsCount ui_fps;
	public UISettings ui_set;
	public UITitleScreen ui_tis;
	
	public String saveFilePath; //path of game save files
	
	public Game() {
		super("Cookie Eater");
		cycletime=5;
		fpscheck=100;
		skipframes = 0;
		check_calibration = true;
		pause_calibration = false;
		
		saveFilePath = System.getProperty("user.home")+"/Documents/CookieEater/";
		
		draw = new Draw(this);
		audio = new Audio(this);
        music = new Music(this);
        
        controls = new ArrayList<Controls>(); 
        
        boards = new HashMap<String,Board>();
        //load all save file names into saved game map
        File dir = new File(saveFilePath);
        File[] savefiles = dir.listFiles();
        if (savefiles != null) {
          for (int i=0; i<savefiles.length; i++) {
        	  String name = savefiles[i].getName();
        	  name = name.substring(0,name.indexOf("."));
        	  boards.put(name,null);
          }
        }
        
        //window settings
  		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  		setUndecorated(false);
  		setExtendedState(JFrame.MAXIMIZED_BOTH);
  		setVisible(true);
  		setFocusable(true);
  		requestFocus();
  		setSize(Toolkit.getDefaultToolkit().getScreenSize());
  		//setBackground(Color.GRAY);
  		//setForeground(Color.GRAY);
  		
  		add(draw);
  		pack();
  		
  		//ui
		ui_fps = new UIFpsCount(this,10,10,Color.WHITE);	
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
			if(board!=null)board.runUpdate();
		}
		try {
			Thread.sleep(time); //time between updates
		}catch(InterruptedException e){};
	}
	public void freeze(int time) { //freeze-frame for length of time
		time/=getAdjustedCycle();
		skipframes+=time;
	}
	
	//generate new Board based on settings
	public void createDungeon(String name, int mode, int dungeon, int playercount) {
		for(int i=0; i<controls.size(); i++) {
			removeKeyListener(controls.get(i));
		}
		controls = new ArrayList<Controls>();
		board = new Board(this,name,mode,dungeon,playercount,cycletime);
		ui_set.makeButtons();
		boards.put(name,board);
	}	
	//load saved Board or create from file if not saved
	public void loadDungeon(String name) {
		for(int i=0; i<controls.size(); i++) {
			removeKeyListener(controls.get(i));
		}
		controls = new ArrayList<Controls>();
		
		if(boards.get(name)!=null) {
			board = boards.get(name);
			board.loadUp();
		}else {
			File f = new File(saveFilePath+name+".txt");
			SaveData bsave;
			try {
				bsave = new SaveData(f);
				board = new Board(this,bsave,cycletime);
				boards.put(name,board);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		ui_set.makeButtons();
	}
	
	public void updateUI() {
		//fps counter
		if(board!=null && board.isPaused()) {
			pause_calibration = true;
		}
		if(fpscheck--<=0) {
			//fps.update(lastFrame,System.currentTimeMillis());
			true_cycle=(int)(System.currentTimeMillis()-lastFrame); 
			if(!pause_calibration && check_calibration && board!=null){
				board.setCalibrations(getAdjustedCycle());
			}
			lastFrame = System.currentTimeMillis();
			fpscheck=100;
			pause_calibration = false;
		}
		if(board!=null)board.updateUI();

	}
	
	public void addControls(Controls c) {
		addKeyListener(c);
		controls.add(c);
	}
}
