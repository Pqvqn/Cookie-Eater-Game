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
		desc="Shields the user.`Amplify: Recoil increases";
		initminrecoil=user.getMinRecoil();
		newminrecoil=initminrecoil;
		initmaxrecoil=user.getMaxRecoil();
		newmaxrecoil=initmaxrecoil;
	}

	public void initialize() {
	
	}
	
	public void execute() {
		if(checkCanceled())return;
		user.setShielded(true);
		if(user.getMinRecoil()!=newminrecoil)
			user.setMinRecoil(newminrecoil);
		if(user.getMaxRecoil()!=newmaxrecoil)
			user.setMaxRecoil(newmaxrecoil);
	}
	
	public void end(boolean interrupted) {
		user.setMinRecoil(initminrecoil);
		user.setMaxRecoil(initmaxrecoil);
		user.setShielded(false);
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
