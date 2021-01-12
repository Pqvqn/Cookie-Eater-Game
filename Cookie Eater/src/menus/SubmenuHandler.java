package menus;

import java.util.*;

public class SubmenuHandler {

	
	private HashMap<String,ArrayList<MenuButton>> menuListings; //list of all menus with string key
	private String defMenu; //key for default menu
	
	public SubmenuHandler(String defaultMenu) {
		menuListings = new HashMap<String,ArrayList<MenuButton>>();
		defMenu = defaultMenu;
		menuListings.put(defMenu,new ArrayList<MenuButton>());
	}
	
	//shows or hides the entire menu system
	public void showFull(boolean s) {
		displayMenu(defMenu);
		showMenu(defMenu,s);
	}
	
	//adds button to submenu
	public void addButton(String menuKey, MenuButton button) {
		if(!menuListings.containsKey(menuKey)) {
			menuListings.put(menuKey,new ArrayList<MenuButton>());
		}
		menuListings.get(menuKey).add(button);
	}
	
	
	//sets visibility of a menu
	public void showMenu(String menuKey, boolean show) {
		ArrayList<MenuButton> menu = menuListings.get(menuKey);
		if(menu==null)return;
		for(int i=0; i<menu.size(); i++) {
			menu.get(i).show(show);
		}
	}
	
	//show the chosen menu and hide all others
	public void displayMenu(String menuKey) {//
		for(String om : menuListings.keySet()) {
			if(!om.equals(menuKey)) {
				showMenu(om,false);
			}
		}
		showMenu(menuKey,true);
	}
	
}
