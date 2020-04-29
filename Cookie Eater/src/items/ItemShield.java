package items;
import ce3.*;

public class ItemShield extends Item{

	private double initrecoil;
	private double newrecoil;
	
	public ItemShield(Board frame) {
		super(frame);
		name = "Shield";
		desc="Shields the player.`Amplify: Recoil increases";
		initrecoil=player.getRecoil();
		newrecoil=initrecoil;
	}

	public void initialize() {
	
	}
	
	public void execute() {
		if(checkCanceled())return;
		player.setShielded(true);
		if(player.getRecoil()!=newrecoil)
			player.setRecoil(newrecoil);
	}
	
	public void end(boolean interrupted) {
		player.setRecoil(initrecoil);
		player.setShielded(false);
	}
	public void amplify() {
		super.amplify();
		newrecoil+=initrecoil;
	}
	public void deamplify() {
		super.deamplify();
		newrecoil-=initrecoil;
	}
}
