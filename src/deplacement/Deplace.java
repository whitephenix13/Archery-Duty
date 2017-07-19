package deplacement;

import java.awt.Point;

import javax.vecmath.Vector2d;

import collision.Collidable;
import collision.Collision;
import debug.Debug_time;
import partie.AbstractModelPartie;
import personnage.Heros;
import principal.InterfaceConstantes;
import types.Hitbox;

public class Deplace implements InterfaceConstantes{
	private Gravite gravite = new Gravite();

	public Deplace() 
	{
	}

	public void DeplaceObject(Collidable object, Mouvement nouvMouv, AbstractModelPartie partie)
	{
		Debug_time debugTime = new Debug_time();
		debugTime.init();
		boolean isHeros = object instanceof Heros;

		debugTime.elapsed("test memorize values", 3);
		
		if(isHeros && partie.slowDown){
			partie.slowCount= (partie.slowCount+1) % (InterfaceConstantes.SLOW_DOWN_FACTOR);
		}

		object.memorizeCurrentValue();

		debugTime.elapsed("object.deplace", 3);
		
		boolean update_with_speed = ( !partie.slowDown || (partie.slowDown && partie.slowCount==0));
		boolean[] shouldMov_changedAnim=object.deplace(partie, this);
		boolean shouldMove= shouldMov_changedAnim[0];
		boolean changedAnim= shouldMov_changedAnim[1];
		if(!shouldMove)
			return;

		boolean useGravity = object.useGravity && update_with_speed;
		
		debugTime.elapsed("gravity and friction", 3);

		
		if(useGravity)
			gravite.gravite(object, partie.slowDown);

		debugTime.elapsed("after gravity and before friction", 4);

		if(update_with_speed)
			object.applyFriction(0,0);
		debugTime.elapsed("after friction and before reset", 4);

		//deplacement à l'aide de la vitesse  si il n'y a pas collision 
		//on reset les dernières positions de collisions:
		object.resetVarBeforeCollision();
		
		debugTime.elapsed("collision", 3);

		if(update_with_speed)
		{
			boolean stuck = !Collision.ejectWorldCollision(partie, object);

			if(stuck)
			{
				object.handleStuck(partie);
			}
			else
				object.handleDeplacementSuccess(partie);
		}
		object.resetVarDeplace();
		
		debugTime.elapsed("deplace ecran", 3);

		
		if(isHeros){
			Point delta = getdeplaceEcran(partie,(Heros)object);
			deplaceEcran(delta,partie,object);
		}

	}



	/**
	 * Recentre l'ecran autour du heros
	 * 
	 * @param heros, le personnage 
	 * @return how much to add to xScreendisp,yScreendisp,object.xpos,object.ypos to get them to the right place
	 * 
	 */	
	public Point getdeplaceEcran(AbstractModelPartie partie, Heros heros) //{{
	{
		int xdelta=0;
		int ydelta=0;

		int largeur_fenetre=0;
		int hauteur_fenetre=0;

		int left_xpos_hit = (int) Hitbox.supportPoint(new Vector2d(-1,0), heros.getHitbox(partie.INIT_RECT).polygon).x;
		int right_xpos_hit = (int) Hitbox.supportPoint(new Vector2d(1,0), heros.getHitbox(partie.INIT_RECT).polygon).x;
		int up_ypos_hit = (int) Hitbox.supportPoint(new Vector2d(0,-1), heros.getHitbox(partie.INIT_RECT).polygon).y;
		int down_ypos_hit = (int) Hitbox.supportPoint(new Vector2d(0,1), heros.getHitbox(partie.INIT_RECT).polygon).y;

		int xpos_hit=0;
		int ypos_hit=0;
		//les conditions limites sont aux 3/7
		//trop à gauche de l'ecran
		if(left_xpos_hit<2*InterfaceConstantes.LARGEUR_FENETRE/7){
			xpos_hit=left_xpos_hit;
			largeur_fenetre=heros.getGlobalVit(partie).x<0? 2*InterfaceConstantes.LARGEUR_FENETRE/7 :0;
		}
		//trop à droite 
		else if(right_xpos_hit>5*InterfaceConstantes.LARGEUR_FENETRE/7){
			xpos_hit=right_xpos_hit;
			largeur_fenetre=heros.getGlobalVit(partie).x>0 ? 5*InterfaceConstantes.LARGEUR_FENETRE/7:0;
		}

		//trop en haut
		if(up_ypos_hit<2*InterfaceConstantes.HAUTEUR_FENETRE/5){
			ypos_hit= up_ypos_hit;
			hauteur_fenetre=heros.getGlobalVit(partie).y<=0? 2*InterfaceConstantes.HAUTEUR_FENETRE/5:0;
		}

		//trop bas
		else if(down_ypos_hit>3*InterfaceConstantes.HAUTEUR_FENETRE/5){
			ypos_hit =down_ypos_hit;
			hauteur_fenetre=heros.getGlobalVit(partie).y>=0? 3*InterfaceConstantes.HAUTEUR_FENETRE/5:0;
		}

		if(largeur_fenetre != 0 ){
			xdelta= largeur_fenetre-xpos_hit;
		}
		if(hauteur_fenetre!=0){
			ydelta= hauteur_fenetre-ypos_hit;
		}
		return new Point(xdelta,ydelta);
	}
	public void deplaceEcran(Point delta,  AbstractModelPartie partie, Collidable object)
	{
		partie.xScreendisp+= delta.x;
		object.pxpos(delta.x,object.fixedWhenScreenMoves); 

		partie.yScreendisp+=  delta.y;
		object.pypos(delta.y,object.fixedWhenScreenMoves); 
		
	}
	/**
	 * Renvoie l'animation d'une fleche encochée/du héros en fonction de la position de la souris 
	 * @return l'animation de la fleche/du heros
	 */	

	public static double XYtoAngle(double xPosRelative, double yPosRelative)
	{
		double angle= Math.atan(yPosRelative/xPosRelative);
		if(xPosRelative<0 && yPosRelative>0)
			angle= Math.PI + angle;
		if(xPosRelative<0 && yPosRelative<=0)
			angle= Math.PI + angle;
		if(xPosRelative>=0 && yPosRelative<=0)
			angle= 2*Math.PI + angle;
		return angle;
	}
	public static double[] angleToXY(double angle)
	{
		double[] XY = new double[2];
		double tol = Math.PI/10;
		boolean close_270 = Math.abs(angle-3*Math.PI/2)<tol;
		boolean close_90 = Math.abs(angle-Math.PI/2)<tol;
		boolean direction_up = !(angle>=Math.PI && angle <= 2* Math.PI)  ;
		boolean direction_left = (angle>=Math.PI/2 && angle <= 1.5 * Math.PI);

		if( close_90||close_270 )
		{
			XY[1]=direction_up ? 1 : -1;
			XY[0]= Math.abs(XY[1]/Math.tan(angle)) * (direction_left? -1 : 1);
		}
		else
		{
			XY[0]=direction_left? -1:1;
			XY[1]=Math.abs(XY[0]*Math.tan(angle))*(direction_up ? 1 : -1);
		}
		return XY;
	}

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
		double xcenter= heros.xpos()+ (isFiring? heros.deplacement.x_center_tir.get(heros.anim) : 
			(heros.deplacement.xtaille.get(heros.anim)/2));

		double ycenter= heros.ypos()+(isFiring? heros.deplacement.y_center_tir.get(heros.anim):
			(heros.deplacement.ytaille.get(heros.anim)/4));//arms at neck level

		double xPosRelative= partie.getXPositionSouris()-xcenter; 
		double yPosRelative= partie.getYPositionSouris()-ycenter;
		double angle= XYtoAngle(xPosRelative, yPosRelative);
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


