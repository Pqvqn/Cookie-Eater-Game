package items;
import ce3.*;

public class ItemRecharge extends Item{
	
	private double cash;
	private double chargeamount; //cookie value needed to recharge first time
	
	public ItemRecharge(Board frame) {
		super(frame);
		chargeamount = 5;
		name = "Recharge";
		desc="If enough cookies taken, special can be instantly reinitiated.`Amplify: Less cookies to charge";
	}
	public void prepare() {
	}
	public void initialize() {
		cash = user.getCash();
	}
	public void execute() {
		if(checkCanceled())return;
	}
	public void end(boolean interrupted) {
		if(user.getCash()-cash>=chargeamount*(Math.pow(chargeamount,user.getSpecialRecharges()))) {
			user.rechargeSpecial(user.getCurrentSpecial());
		}
	}
	public void amplify() {
		super.amplify();
		chargeamount*=.8;
	}
	public void deamplify() {
		super.deamplify();
		chargeamount/=.8;
	}
}
