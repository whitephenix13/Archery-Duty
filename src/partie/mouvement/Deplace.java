package partie.mouvement;

import java.awt.Point;

import javax.vecmath.Vector2d;

import gameConfig.InterfaceConstantes;
import menu.menuPrincipal.ModelPrincipal;
import partie.collision.Collidable;
import partie.collision.Collision;
import partie.collision.Hitbox;
import partie.effects.Effect;
import partie.entitie.Entity;
import partie.entitie.heros.Heros;
import partie.modelPartie.AbstractModelPartie;
import partie.mouvement.entity.Mouvement_entity;
import partie.mouvement.entity.Mouvement_entity.EntityTypeMouv;

public class Deplace implements InterfaceConstantes{
	private Gravite gravite = new Gravite();

	public Deplace() 
	{
	}

	public void deplaceObject(Collidable object, AbstractModelPartie partie)
	{
		ModelPrincipal.debugTime.startElapsedForVerbose();
		boolean isHeros = object instanceof Heros;

		
		if(isHeros && partie.slowDown){
			partie.slowCount= (partie.slowCount+1) % (InterfaceConstantes.SLOW_DOWN_FACTOR);
		}

		object.memorizeCurrentValue();
		ModelPrincipal.debugTime.elapsed("test memorize values");

		
		boolean update_with_speed = ( !partie.slowDown || (partie.slowDown && partie.slowCount==0));
		if(!update_with_speed)
			return;
		//Prepare the object to move, the object will actually move in checkCollideWithWorld 
		boolean shouldMove =object.deplace(partie);
		
		if(!shouldMove)
			return;
		ModelPrincipal.debugTime.elapsed(object.toString()+" deplace");

		boolean useGravity = object.useGravity && update_with_speed;

		if(useGravity)
			gravite.gravite(object);
		
		ModelPrincipal.debugTime.elapsed("gravity and friction");

		if(update_with_speed)
			object.applyFriction(0,0);
		ModelPrincipal.debugTime.elapsed("after friction and before reset");

		//deplacement à l'aide de la vitesse  si il n'y a pas collision 
		//on reset les dernières positions de collisions:
		object.resetVarBeforeCollision();
		
		if(update_with_speed)
		{
			boolean checkColli = object.checkCollideWithWorld();
			boolean stuck = false;
			if(checkColli){
				stuck = !Collision.ejectWorldCollision(partie, object);
			}

			if(stuck)
			{
				object.handleStuck(partie);
			}
			else
				object.handleDeplacementSuccess(partie);
			
		}
		else //update with no speed, ie just check if collide with world
		{
			boolean checkColli = object.checkCollideWithWorld();
			boolean stuck = false;
			if(checkColli){
				stuck = Collision.isWorldCollision(partie, object, true);
			}
			if(stuck)
			{
				object.handleStuck(partie);
			}

		}
		
		ModelPrincipal.debugTime.elapsed("collision");

		object.resetVarDeplace(update_with_speed);
		
		if(object.controlScreenMotion){
			Point delta = getdeplaceEcran(partie,(Heros)object,false);
			deplaceEcran(delta,partie,object);
		}
		
		ModelPrincipal.debugTime.elapsed("deplace ecran");
		
		if(object instanceof Entity){
			Entity enti = (Entity) object;

			//update the conditions (or other) before applying them
			for(Effect eff : enti.currentEffects)
				eff.updateOnCollidable(partie, enti);
			
			//Apply conditions damage 
			enti.addLife(enti.conditions.conditionDamageReceived());
		}
		
		ModelPrincipal.debugTime.elapsed("update conditions");
	
	}

	public void deplaceObjectOutOfScreen(AbstractModelPartie partie, Collidable object)
	{
		object.deplaceOutOfScreen(partie);
	}

	/**
	 * Recentre l'ecran autour du heros
	 * 
	 * @param heros, le personnage 
	 * @return how much to add to xScreendisp,yScreendisp,object.xpos,object.ypos to get them to the right place
	 * 
	 */	
	public static Point getdeplaceEcran(AbstractModelPartie partie, Collidable object,boolean force) //{{
	{
		if(MOVE_SCREEN_WHEN_HEROS_MOVES)
		{
			
			Vector2d cent = Hitbox.getObjMid(partie, object);
			return new Point(InterfaceConstantes.WINDOW_WIDTH/2-(int)Math.round(cent.x)-partie.xScreendisp,InterfaceConstantes.WINDOW_HEIGHT/2-(int)Math.round(cent.y)-partie.yScreendisp);
		}
		else{
			int xdelta=0;
			int ydelta=0;

			int largeur_fenetre=0;
			int hauteur_fenetre=0;

			//Add partie.getScreenDisp() to get the relative position with respect to the screen 
			int left_xpos_hit = (int) Hitbox.supportPoint(new Vector2d(-1,0), object.getHitbox(partie.INIT_RECT,partie.getScreenDisp()).polygon).x+partie.xScreendisp;
			int right_xpos_hit = (int) Hitbox.supportPoint(new Vector2d(1,0), object.getHitbox(partie.INIT_RECT,partie.getScreenDisp()).polygon).x+partie.xScreendisp;
			int up_ypos_hit = (int) Hitbox.supportPoint(new Vector2d(0,-1), object.getHitbox(partie.INIT_RECT,partie.getScreenDisp()).polygon).y+partie.yScreendisp;
			int down_ypos_hit = (int) Hitbox.supportPoint(new Vector2d(0,1), object.getHitbox(partie.INIT_RECT,partie.getScreenDisp()).polygon).y+partie.yScreendisp;
			int xpos_hit=0;
			int ypos_hit=0;

			//les conditions limites sont aux 3/7
			//trop à gauche de l'ecran
			if(left_xpos_hit<2*InterfaceConstantes.WINDOW_WIDTH/7){
				xpos_hit=left_xpos_hit;
				largeur_fenetre=(object.getGlobalVit().x<0|| force)? 2*InterfaceConstantes.WINDOW_WIDTH/7 :0;
			}
			//trop à droite 
			else if(right_xpos_hit>5*InterfaceConstantes.WINDOW_WIDTH/7){
				xpos_hit=right_xpos_hit;
				largeur_fenetre=(object.getGlobalVit().x>0||force) ? 5*InterfaceConstantes.WINDOW_WIDTH/7:0;
			}

			//trop en haut
			if(up_ypos_hit<2*InterfaceConstantes.WINDOW_HEIGHT/5){
				ypos_hit= up_ypos_hit;
				hauteur_fenetre=(object.getGlobalVit().y<=0||force)? 2*InterfaceConstantes.WINDOW_HEIGHT/5:0;
			}

			//trop bas
			else if(down_ypos_hit>3*InterfaceConstantes.WINDOW_HEIGHT/5){
				ypos_hit =down_ypos_hit;
				hauteur_fenetre=(object.getGlobalVit().y>=0||force)? 3*InterfaceConstantes.WINDOW_HEIGHT/5:0;
			}

			if(largeur_fenetre != 0 ){
				xdelta= largeur_fenetre-xpos_hit;
			}
			if(hauteur_fenetre!=0){
				ydelta= hauteur_fenetre-ypos_hit;
			}

			return new Point(xdelta,ydelta);
		}
	}
	public static void deplaceEcran(Point delta,  AbstractModelPartie partie, Collidable object)
	{
		partie.xScreendisp+= delta.x;
		object.addXpos_sync(delta.x,object.fixedWhenScreenMoves); 

		partie.yScreendisp+=  delta.y;
		object.addYpos_sync(delta.y,object.fixedWhenScreenMoves); 
		
	}
	public static double vectorToAngle(Vector2d direction)
	{
		return XYtoAngle(direction.x,direction.y);
	}
	/**
	 * Return an angle between 0 and 2*PI
	 */	

	public static double XYtoAngle(double xPosRelative, double yPosRelative)
	{
		double angle= Math.atan(yPosRelative/xPosRelative); // between -PI/2 PI/2
		if(xPosRelative<0 && yPosRelative>0)
			angle= Math.PI + angle;
		if(xPosRelative<0 && yPosRelative<=0)
			angle= Math.PI + angle;
		if(xPosRelative>=0 && yPosRelative<=0)
			angle= 2*Math.PI + angle;
		return angle;
	}
	public static Vector2d angleToVector(double angle)
	{
		double[] XY = angleToXY(angle);
		return new Vector2d(XY[0],XY[1]);
	}
	public static double[] angleToXY(double _angle)
	{
		double angle=(_angle+2*Math.PI)%(2*Math.PI);
		double[] XY = new double[2];
		double tol = 0.5 * Math.PI/180 ;
		boolean close_270 = Math.abs(angle-3*Math.PI/2)<tol;
		boolean close_90 = Math.abs(angle-Math.PI/2)<tol ;
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

	public static double[] getMouvIndexRotationTir(AbstractModelPartie partie, boolean getForArrow)
	{
		double[] mouv_index_rotation = new double[2];
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
		double tolerance =0;
		Heros heros = partie.heros;
		boolean isFiring = heros.getMouvement().isMouvement(EntityTypeMouv.TIR);
		double xcenter= heros.getXpos()+ (isFiring? ((Mouvement_entity)heros.getMouvement()).x_center_tir.get(heros.getMouvIndex()) : 
			(heros.getMouvement().xtaille.get(heros.getMouvIndex())/2));

		double ycenter= heros.getYpos()+(isFiring? ((Mouvement_entity)heros.getMouvement()).y_center_tir.get(heros.getMouvIndex()):
			(heros.getMouvement().ytaille.get(heros.getMouvIndex())/4))-6;//arms at neck level
		
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
					mouv_index_rotation[0]=0;
					mouv_index_rotation[1]=angle;
				}
				else
				{
					mouv_index_rotation[0]=i;
					mouv_index_rotation[1]=angle -delta_rot;
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
		return mouv_index_rotation;
	}

}


