package partie.mouvement.effect;

import partie.collision.Collidable;
import partie.effects.Effect;
import partie.mouvement.Mouvement;
import partie.mouvement.Mouvement.TypeMouv;
import utils.Vitesse;

public abstract class Mouvement_effect extends Mouvement{
	
	public static enum MouvEffectEnum implements TypeMouv {ELECTRIQUE_APPEAR,ELECTRIQUE_IDLE,ELECTRIQUE_SPLIT,
		EXPLOSIVE_IDLE,FEU_IDLE,GLACE_IDLE,GRAPPIN_IDLE,LUMIERE_IDLE,OMBRE_IDLE,ROCHE_IDLE,TROU_NOIR_IDLE,VENT_IDLE};
	protected Vitesse vit;
	
	@Override
	public Vitesse __getUncheckedSpeed(Collidable object, int animationFrame) {
		return vit;
	}

	@Override
	public DirSubTypeMouv droite_gauche(int animationFrame,double rotation) {
		if( rotation <= Math.PI/2 && (rotation >= 3*Math.PI/2) )
			return DirSubTypeMouv.GAUCHE; 
		else
			return DirSubTypeMouv.DROITE;
	}

}
