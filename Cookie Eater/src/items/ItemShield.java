package items;
import ce3.*;

public class ItemShield extends Item{

	private double initminrecoil;
	private double newminrecoil;
	private double initmaxrecoil;
	private double newmaxrecoil;
	
	public ItemShield(Board frame) {
		super(frame);
		name = "Shield";
		desc="Shields the player.`Amplify: Recoil increases";
		initminrecoil=player.getMinRecoil();
		newminrecoil=initminrecoil;
		initmaxrecoil=player.getMaxRecoil();
		newmaxrecoil=initmaxrecoil;
	}

	public void initialize() {
	
	}
	
	public void execute() {
		if(checkCanceled())return;
		player.setShielded(true);
		if(player.getMinRecoil()!=newminrecoil)
			player.setMinRecoil(newminrecoil);
		if(player.getMaxRecoil()!=newmaxrecoil)
			player.setMaxRecoil(newmaxrecoil);
	}
	
	public void end(boolean interrupted) {
		player.setMinRecoil(initminrecoil);
		player.setMaxRecoil(initmaxrecoil);
		player.setShielded(false);
	}
	public void amplify() {
		super.amplify();
		newminrecoil+=initminrecoil;
		newmaxrecoil+=initmaxrecoil;
	}
	public void deamplify() {
		super.deamplify();
		newminrecoil-=initminrecoil;
		newmaxrecoil-=initmaxrecoil;
	}
}
