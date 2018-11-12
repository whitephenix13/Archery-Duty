package types;

import java.awt.geom.AffineTransform;

import collision.Collidable;
import partie.AbstractModelPartie;

public abstract class Projectile extends Collidable{
	
	protected float speedFactor = 1;
	public AffineTransform draw_tr =null;
	@Override
	public Vitesse getGlobalVit(AbstractModelPartie partie){
		Vitesse vit = localVit.Copy();
		return vit.times(speedFactor);
	}
	public Vitesse getGlobalVit(AbstractModelPartie partie,Vitesse _localVit){
		return _localVit.times(speedFactor);
	}
	
}
