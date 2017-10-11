package monstre;

import java.util.Arrays;

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
}
