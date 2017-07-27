package effects;

import java.awt.Image;
import java.awt.Point;
import java.util.Arrays;

import collision.Collidable;
import collision.Collision;
import conditions.Condition;
import fleches.Fleche;
import partie.AbstractModelPartie;
import types.Entitie;
import types.Hitbox;
import types.Vitesse;

public class Lumiere_effect extends Effect{
	
	double DUREE_VITESSE = 10;

	public Lumiere_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _anim, int current_frame)
	{
		anim=_anim;

		ref_fleche = _ref_fleche;
		name = Fleche.SPIRITUELLE.LUMIERE;
		xtaille =  Arrays.asList(100,100,100,100,100);
		ytaille =  Arrays.asList(100,100,100,100,100);
		hitbox= Hitbox.createSquareHitboxes(0,0,100,100,5);

		rotation = _ref_fleche.rotation;
		int start_index =0;
		int end_index =5;
		animation.start(Arrays.asList(4,8,12,16,20), current_frame, start_index, end_index);
		maxnumberloops = 1;
		
		localVit= new Vitesse();
		partie.arrowsEffects.add(this);
	}


	@Override
	public void updateOnCollidable(AbstractModelPartie partie,Entitie attacher)
	{
		if(Collision.testcollisionObjects(partie, this, attacher,true))
			attacher.conditions.addNewCondition(Condition.VITESSE, DUREE_VITESSE);
	}
	
	@Override
	public Vitesse getModifiedVitesse(AbstractModelPartie partie,
			Collidable obj) {
		return new Vitesse();
	}
	@Override
	public Point getTranslationFromTranformDraw(AbstractModelPartie partie) {
		int fanim = ref_fleche.anim;
		//get the middle of the effect
		int x_eff_center = (int) (xtaille.get(anim)/2 * Math.cos(rotation) - ytaille.get(anim)/2 * Math.sin(rotation));
		int y_eff_center = (int) (xtaille.get(anim)/2 * Math.sin(rotation) + ytaille.get(anim)/2 * Math.cos(rotation));
		
		//get the tip of the arrow
		int x_tip_fleche =  (int) (ref_fleche.xpos() + ref_fleche.deplacement.xtaille.get(fanim) * Math.cos(rotation) 
				- ref_fleche.deplacement.ytaille.get(fanim)/2 * Math.sin(rotation));
		int y_tip_fleche= (int) (ref_fleche.ypos() + ref_fleche.deplacement.xtaille.get(fanim) * Math.sin(rotation) 
				+ ref_fleche.deplacement.ytaille.get(fanim)/2 * Math.cos(rotation));
		
		Point transl = new Point(x_tip_fleche-x_eff_center+partie.xScreendisp, +y_tip_fleche-y_eff_center+partie.yScreendisp);
		return transl;
	}

	
	

}
