package partie.projectile.tirMonstre;

import java.awt.Point;
import java.util.Arrays;

import gameConfig.ObjectTypeHelper.ObjectType;
import partie.collision.Hitbox;
import partie.deplacement.Deplace;
import partie.modelPartie.AbstractModelPartie;
import partie.projectile.Projectile;

public abstract class TirMonstre extends Projectile{

	public float damage;

	public TirMonstre()
	{
		super();
		this.setCollideWithout(Arrays.asList(ObjectType.MONSTRE,ObjectType.TIR_MONSTRE));
	}
	@Override
	public int getMaxBoundingSquare()
	{
		return getDeplacement().getMaxBoundingSquare(this);
	}
	@Override
	public Point getMaxBoundingRect()
	{
		return getDeplacement().getMaxBoundingRect(this);
	}
	
	
	@Override
	public boolean[] deplace(AbstractModelPartie partie,Deplace deplace){
		//update rotation 
		//switch anim 
		//update hitbox 
			//only need to update if animation changed 
		//set speed of deplacement
		//update draw transform only if needed 
		boolean[] res = {true,false};
		return res;
	}
	@Override
	public Hitbox getNextEstimatedHitbox(AbstractModelPartie partie,double newRotation,int anim)
	{
		throw new java.lang.UnsupportedOperationException("Not supported yet.");
	}
}
