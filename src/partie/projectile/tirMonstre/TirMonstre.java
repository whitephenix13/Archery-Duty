package partie.projectile.tirMonstre;

import java.awt.Point;
import java.util.Arrays;

import gameConfig.ObjectTypeHelper.ObjectType;
import partie.collision.Hitbox;
import partie.input.InputPartie;
import partie.modelPartie.AbstractModelPartie;
import partie.mouvement.Deplace;
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
		return getMouvement().getMaxBoundingSquare();
	}
	@Override
	public Point getMaxBoundingRect()
	{
		return getMouvement().getMaxBoundingRect();
	}
	@Override
	protected void onStartDeplace(){}
}
