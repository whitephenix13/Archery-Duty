package partie.deplacement.effect;

import partie.collision.Collidable;
import partie.deplacement.Mouvement;
import partie.deplacement.Mouvement.TypeMouv;
import partie.effects.Effect;
import utils.Vitesse;

public abstract class Mouvement_effect extends Mouvement{
	
	public static enum MouvEffectEnum implements TypeMouv {ELECTRIQUE_IDLE,EXPLOSIVE_IDLE,FEU_IDLE,GLACE_IDLE,GRAPPIN_IDLE,LUMIERE_IDLE,OMBRE_IDLE,ROCHE_IDLE,TROU_NOIR_IDLE,VENT_IDLE};
	protected Vitesse vit;
	
	@Override
	public Vitesse __getUncheckedSpeed(Collidable object, int anim) {
		return vit;
	}

	@Override
	public DirSubTypeMouv droite_gauche(int anim,double rotation) {
		if( rotation <= Math.PI/2 && (rotation >= 3*Math.PI/2) )
			return DirSubTypeMouv.GAUCHE; 
		else
			return DirSubTypeMouv.DROITE;
	}

}
