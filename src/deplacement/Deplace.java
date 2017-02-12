package deplacement;

import java.awt.Point;

import javax.vecmath.Vector2d;

import collision.Collidable;
import collision.Collision;
import partie.AbstractModelPartie;
import personnage.Heros;
import principal.InterfaceConstantes;
import types.Hitbox;

public class Deplace implements InterfaceConstantes{
	public Collision colli;
	public double[] delta_ecran= new double[2];
	public double[] cumul_delta_ecran= new double[2];//cumulate decimal that will add up to integer 
	public Deplace() 
	{
		colli = new Collision();
	}

	public void DeplaceObject(Collidable object, Mouvement nouvMouv, AbstractModelPartie partie)
	{
		boolean isHeros = object instanceof Heros;
		boolean mouvDifferant=false;
		if(isHeros){
			Heros heros = (Heros) object;
			mouvDifferant = (! (heros.deplacement.IsDeplacement(nouvMouv))) && partie.changeMouv ;

		}

		object.memorizeCurrentValue();

		//on change d'animation avant de deplacer si elle doit etre changee
		if(isHeros && partie.slowDown){
			partie.slowCount= (partie.slowCount+1) % (object.slowDownFactor);
		}

		boolean shouldUpdateMove=object.reaffiche<=0 || mouvDifferant;
		if(shouldUpdateMove){
			boolean shouldMove=object.deplace(partie, this);
			if(!shouldMove)
				return;
			}

		boolean useGravity = object.useGravity &&( !partie.slowDown || (partie.slowDown && partie.slowCount==0));
		if(useGravity)
			Gravite.gravite(object, partie.slowDown);

		object.applyFriction(0);
		//deplacement à l'aide de la vitesse  si il n'y a pas collision 
		//on reset les dernières positions de collisions:
		object.resetVarBeforeCollision();
		boolean stuck = !colli.ejectWorldCollision(partie, this, object);
		if(stuck)
		{
			object.handleStuck(partie, this);
		}
		else
			object.handleDeplacementSuccess(partie, this);
		object.resetVarDeplace();

		if(shouldUpdateMove)
			object.reaffiche=object.setReaffiche();

		if(isHeros){
			if(shouldUpdateMove){
			Point delta = getdeplaceEcran(partie,object);
			delta_ecran[0]=((float)delta.x)/object.reaffiche;
			delta_ecran[1]=((float)delta.y)/object.reaffiche;
			interpolateDeplaceEcran(delta_ecran,cumul_delta_ecran,partie,object);
			}
			else
				interpolateDeplaceEcran(delta_ecran,cumul_delta_ecran,partie,object);

		}

		object.reaffiche--;

	}

	/*public void DeplaceObject(Collidable object, Mouvement nouvMouv, AbstractModelPartie partie)
	{
		boolean isHeros = object instanceof Heros;
		boolean mouvDifferant=false;
		if(isHeros){
			Heros heros = (Heros) object;
			mouvDifferant = (! (heros.deplacement.IsDeplacement(nouvMouv))) && partie.changeMouv ;

		}
		if( mouvDifferant || (!mouvDifferant && object.reaffiche<=0 ) )
		{
			object.memorizeCurrentValue();

			//on change d'animation avant de deplacer si elle doit etre changee
			if(isHeros && partie.slowDown){
				partie.slowCount= (partie.slowCount+1) % (object.slowDownFactor);
			}

			boolean shouldMove=object.deplace(partie, this);
			if(shouldMove)
			{
				boolean useGravity = object.useGravity &&( !partie.slowDown || (partie.slowDown && partie.slowCount==0));
				if(useGravity)
					Gravite.gravite(object, partie.slowDown);

				object.applyFriction(0);
				//deplacement à l'aide de la vitesse  si il n'y a pas collision 
				//on reset les dernières positions de collisions:
				object.resetVarBeforeCollision();
				boolean stuck = !colli.ejectWorldCollision(partie, this, object);
				if(stuck)
				{
					object.handleStuck(partie, this);
				}
				else
					object.handleDeplacementSuccess(partie, this);
				object.resetVarDeplace();

				if(object.reaffiche>0 && isHeros){
					delta_ecran[0]*=object.reaffiche;
					delta_ecran[1]*=object.reaffiche;
					interpolateDeplaceEcran(delta_ecran,cumul_delta_ecran,partie,object);
				}

				object.reaffiche=object.setReaffiche();

				if(isHeros){
					Point delta = getdeplaceEcran(partie,object);
					delta_ecran[0]=((float)delta.x)/object.reaffiche;
					delta_ecran[1]=((float)delta.y)/object.reaffiche;
					interpolateDeplaceEcran(delta_ecran,cumul_delta_ecran,partie,object);
				}
			}
		}
		else{
			object.reaffiche--;
			if(isHeros){
				interpolateDeplaceEcran(delta_ecran,cumul_delta_ecran,partie,object);
			}
		}

	}*/
	/**
	 * Recentre l'ecran autour du heros
	 * 
	 * @param heros, le personnage 
	 * @return how much to add to xScreendisp,yScreendisp,object.xpos,object.ypos to get them to the right place
	 * 
	 */	
	public Point getdeplaceEcran(AbstractModelPartie partie, Collidable object) //{{
	{
		int xdelta=0;
		int ydelta=0;
		//les conditions limites sont aux 3/7
		//trop à gauche de l'ecran
		if(object.xpos<2*InterfaceConstantes.LARGEUR_FENETRE/7)
			xdelta= 2*InterfaceConstantes.LARGEUR_FENETRE/7-object.xpos;

		//trop à droite 
		else if((object.xpos+object.deplacement.xtaille.get(object.anim))>5*InterfaceConstantes.LARGEUR_FENETRE/7)
			xdelta= 5*InterfaceConstantes.LARGEUR_FENETRE/7-object.xpos-object.deplacement.xtaille.get(object.anim);

		//trop en haut
		if(object.ypos<2*InterfaceConstantes.HAUTEUR_FENETRE/5)
			ydelta=2*InterfaceConstantes.HAUTEUR_FENETRE/5-object.ypos;

		//trop bas
		else if((object.ypos+object.deplacement.ytaille.get(object.anim))>3*InterfaceConstantes.HAUTEUR_FENETRE/5)
			ydelta=3*InterfaceConstantes.HAUTEUR_FENETRE/5-object.ypos-object.deplacement.ytaille.get(object.anim);

		return new Point(xdelta,ydelta);
	}
	public void interpolateDeplaceEcran(double[] delta, double[] cumul_delta_ecran, AbstractModelPartie partie, Collidable object)
	{
		int xdelta= (int) (delta[0]+cumul_delta_ecran[0]);
		int ydelta= (int) (delta[1]+cumul_delta_ecran[1]);

		partie.xScreendisp+= xdelta;
		object.xpos+= xdelta; 

		partie.yScreendisp+=  ydelta;
		object.ypos+= ydelta; 

		cumul_delta_ecran[0]= delta[0]+cumul_delta_ecran[0]-xdelta;
		cumul_delta_ecran[1]= delta[1]+cumul_delta_ecran[1]-ydelta;

	}
	/**
	 * Renvoie l'animation d'une fleche encochée/du héros en fonction de la position de la souris 
	 * @return l'animation de la fleche/du heros
	 */	


	public double[] getAnimRotationTir(AbstractModelPartie partie, boolean getForArrow)
	{
		double[] anim_rotation = new double[2];
		/*Anims:
		 * 6____ 7/8___ 9
		 *  |          | 
		 *  |          |   
		 * 5|          |0
		 *  |__________|
		 * 4     3/2    1
		 * 
		 * 
		 * 
		 * */
		double tolerance = 0;
		Heros heros = partie.heros;
		boolean isFiring = heros.deplacement.IsDeplacement(Mouvement_perso.tir);
		double xcenter= heros.xpos+ (isFiring? heros.deplacement.x_center_tir.get(heros.anim) : 
			(heros.deplacement.xtaille.get(heros.anim)/2));

		double ycenter= heros.ypos+(isFiring? heros.deplacement.y_center_tir.get(heros.anim):
			(heros.deplacement.ytaille.get(heros.anim)/4));//arms at neck level

		double xPosRelative= partie.getXPositionSouris()-xcenter; 
		double yPosRelative= partie.getYPositionSouris()-ycenter;
		double angle= Math.atan(yPosRelative/xPosRelative);
		if(xPosRelative<0 && yPosRelative>0)
			angle= Math.PI + angle;
		if(xPosRelative<0 && yPosRelative<=0)
			angle= Math.PI + angle;
		if(xPosRelative>=0 && yPosRelative<=0)
			angle= 2*Math.PI + angle;
		double range = 2 * Math.PI / 8;
		double left_range= 15 * Math.PI / 8;
		double right_range=  Math.PI / 8;
		//value to remove to get the rotation angle
		double delta_rot=0;
		for (int i=0;i<10;++i)
		{
			boolean i0_angle_between = (i==0) && (((left_range-tolerance) <= angle )|| (angle < (right_range+tolerance)));
			boolean angle_between = ((left_range-tolerance) <= angle )&& (angle < (right_range+tolerance));

			if(i0_angle_between || angle_between) //above 15 pi / 8 and below Pi/8
			{
				if(getForArrow)
				{
					anim_rotation[0]=0;
					anim_rotation[1]=angle;
				}
				else
				{
					anim_rotation[0]=i;
					anim_rotation[1]=angle -delta_rot;
				}
				break;
			}

			if(i==0)
			{
				left_range=right_range;
				right_range+=range;
				delta_rot+=range;
			}
			else if(i==1|| i==2||i==6||i==7)
			{
				left_range+=range/2;
				right_range+=range/2;
				if(i==1||i==6)
					delta_rot+=range;
			}
			else
			{
				left_range+=range;
				right_range+=range;
				delta_rot+=range;
			}
		}
		return anim_rotation;
	}

}


