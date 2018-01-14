package monstre;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.Arrays;

import deplacement.Deplace;
import partie.AbstractModelPartie;
import types.Hitbox;
import types.Projectile;
import types.TypeObject;

public abstract class TirMonstre extends Projectile{

	public float damage;
	@Override
	public void init()
	{
		super.init();
		this.setCollideWithout(Arrays.asList(TypeObject.MONSTRE,TypeObject.TIR_MONSTRE));
	}
	@Override
	public int getMaxBoundingSquare()
	{
		return deplacement.getMaxBoundingSquare(this);
	}
	
	@Override
	public boolean[] deplace(AbstractModelPartie partie,Deplace deplace, boolean update_with_speed){
		//update rotation 
		//switch anim 
		//update hitbox 
			//only need to update if animation changed 
		//set speed of deplacement
		//update draw transform only if needed 
		boolean[] res = {true,false};
		return res;
	}
}
