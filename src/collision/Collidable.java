package collision;

import java.awt.Point;

import javax.vecmath.Vector2d;

import partie.AbstractModelPartie;
import types.Hitbox;
import types.TypeObject;
import types.Vitesse;
import deplacement.Deplace;
import deplacement.Mouvement;
import deplacement.Mouvement_perso;

//Specify that an object can enter in collision. 
//Store the information needed to manage a collision
public abstract class Collidable {
	
	public String type;
	public int xpos; 
	public int ypos; 
	public double rotation=0;
	public int anim; 
	public Mouvement deplacement;
	public int reaffiche;
	
	public boolean needDestroy=false;
	public Vitesse vit;
	
	public int slowDownFactor; 
	public boolean fixedWhenScreenMoves ; //true : not influenced by screen displacement (ie: use for the hero)
	
	protected CurrentValue currentValue;
	public boolean useGravity=false;
	//Variables pour mémoriser la direction de la dernière collision
	public boolean last_colli_left=false;
	public boolean last_colli_right=false;
	public abstract Hitbox getHitbox(Point INIT_RECT);
	public abstract Hitbox getHitbox(Point INIT_RECT,Mouvement mouv, int _anim);
	
	public abstract void handleWorldCollision(Vector2d normal,AbstractModelPartie partie,Deplace deplace);
	public abstract void handleObjectCollision(AbstractModelPartie partie,Deplace deplace);
	/**
	 * Function used in Deplace to get back to  the previous correct posistion if stuck
	 */
	public abstract void memorizeCurrentValue();
	/**
	 * 
	 * @param partie
	 * @param deplace
	 * @return [shouldMove,changedAnimation] shouldMove: if the collision (hence movement) have to be applied to this object. 
	 * changedAnimation : if the animation changed due to a change of movement or a change in droite_gauche
	 */
	public abstract boolean[] deplace(AbstractModelPartie partie, Deplace deplace);
	//Use the function trick to memorize the reset values
	protected class CurrentValue{public void res(){};}
	public abstract void applyFriction(int minSpeed);
	public abstract void resetVarBeforeCollision();
	public abstract void handleStuck(AbstractModelPartie partie,Deplace deplace);
	public abstract void handleDeplacementSuccess(AbstractModelPartie partie,Deplace deplace);
	public abstract void resetVarDeplace();

	public abstract void destroy();
	
	public void alignHitbox(int animActu,Mouvement depSuiv, int animSuiv, AbstractModelPartie partie,Deplace deplace, boolean left, boolean down,
			String deplacement_type, boolean useTouchCollision)
	{
		Mouvement depActu= deplacement;
		
		int xdir = left ? -1 :1;
		int ydir = down ? 1 :-1;
		double dx= Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,depActu, animActu).polygon).x -
				Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,depSuiv, animSuiv).polygon).x;

		double dy= Hitbox.supportPoint(new Vector2d(0,ydir), getHitbox(partie.INIT_RECT,depActu, animActu).polygon).y -
				Hitbox.supportPoint(new Vector2d(0,ydir), getHitbox(partie.INIT_RECT,depSuiv, animSuiv).polygon).y;

		double m_dx=0; //-dx, computed if needed
		double m_dy=0; //-dy, computed if needed

		double xadded = dx; //remember how to get back to normal xpos
		double yadded = dy;//remember how to get back to normal ypos
		xpos+= dx;
		ypos+= dy;

		String s ="";
		String s_x =left? "left":"right";
		String s_mx =!left? "left":"right";
		String s_y =down? " down":" up";
		String s_my =!down? " down":" up";

		boolean valid=alignTestValid(depSuiv, animSuiv, partie,deplace,deplacement_type,useTouchCollision);

		s+= (valid && s=="") ? s_x+s_y : "";
		boolean n_glisse = depSuiv.IsDeplacement(Mouvement_perso.glissade);
		//test the opposite y 
		if(!valid)
		{
			m_dy=Hitbox.supportPoint(new Vector2d(-ydir,0), getHitbox(partie.INIT_RECT,depActu, animActu).polygon).y -
					Hitbox.supportPoint(new Vector2d(-ydir,0), getHitbox(partie.INIT_RECT,depSuiv, animSuiv).polygon).y;
			xpos+=dx-xadded;
			ypos+=m_dy-yadded;
			xadded=dx;
			yadded=m_dy;
			valid=alignTestValid(depSuiv, animSuiv, partie,deplace,deplacement_type,useTouchCollision);
			s+= (valid && s=="") ? s_x+s_my : "";

		}

		//test the opposite x with the first value of y
		if(!valid && !n_glisse)
		{
			m_dx=Hitbox.supportPoint(new Vector2d(-xdir,0), getHitbox(partie.INIT_RECT,depActu, animActu).polygon).x -
					Hitbox.supportPoint(new Vector2d(-xdir,0), getHitbox(partie.INIT_RECT,depSuiv, animSuiv).polygon).x;;
					xpos+=m_dx-xadded;
					ypos+=dy-yadded;
					xadded=m_dx;
					yadded=dy;
					valid=alignTestValid(depSuiv, animSuiv, partie,deplace,deplacement_type,useTouchCollision);
					s+= (valid && s=="") ? s_mx+s_y : "";

		}

		//test the opposite x with the opposite y
		if(!valid && !n_glisse)
		{
			xpos+=m_dx-xadded;
			ypos+=m_dy-yadded;
			xadded=m_dx;
			yadded=m_dy;
			valid=alignTestValid(depSuiv, animSuiv, partie,deplace,deplacement_type,useTouchCollision);
			s+= (valid && s=="") ? s_mx+s_my : "";

		}

		/*if(deplacement_type.equals(TypeObject.heros)){
			System.out.println(deplacement.getClass().getName() +animActu +" "+depSuiv.getClass().getName()+animSuiv);
			System.out.println(s);
		}*/
	}
	
	public boolean alignTestValid(Mouvement depSuiv, int animSuiv, AbstractModelPartie partie,Deplace deplace, 
			String deplacement_type, boolean useTouchCollision)
	{
		int prev_anim = anim;
		Mouvement prev_mouv = deplacement.Copy(deplacement_type);
		this.anim=animSuiv;
		this.deplacement=depSuiv;

		boolean valid= !deplace.colli.isWorldCollision(partie, deplace, this,useTouchCollision);
		this.anim=prev_anim;
		this.deplacement=prev_mouv;
		return valid;
	}
	
	public Vitesse convertSpeed(double norm_speed, double angle)
	{
		double cos_angle = Math.cos(angle);
		double sin_angle = Math.sin(angle);

		double x = norm_speed * cos_angle;
		double y = norm_speed * sin_angle;
		return new Vitesse((int)x,(int)y);
	}
		
	
}
