package monstre;

import java.awt.Image;
import java.awt.Point;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import deplacement.Deplace;
import deplacement.Mouvement;
import deplacement.Mouvement_perso;
import effects.Effect;
import fleches.Fleche;
import partie.AbstractModelPartie;
import personnage.Heros;
import principal.InterfaceConstantes;
import types.Entitie;
import types.Hitbox;
import types.Projectile;
import types.TypeObject;


@SuppressWarnings("serial")
public abstract class Monstre extends Entitie implements InterfaceConstantes, Serializable{
	//on définit la position du coin en haut à  gauche de la hitbox

	public boolean finSaut;
	public boolean peutSauter;
	public boolean glisse;
		
	
	public boolean actionReussite;
	public boolean doitChangMouv;
	public Mouvement_perso nouvMouv;
	public int nouvAnim;

	protected ResetHandleCollision resetHandleCollision;
	Image SPattente0; 
	Image SPattente1;

	Image SPmarche0;
	Image SPmarche1;
	Image SPmarche2;
	Image SPmarche3; 

	@Override
	public void init()
	{
		super.init();
		this.setCollideWithout(Arrays.asList(TypeObject.MONSTRE,TypeObject.TIR_MONSTRE));
	}
	/**
	 * Permet de savoir de quel cote est tourné le monstre
	 * 
	 * @param anim, l'animation du monstre
	 * 
	 * @return String , Mouvement.DROITE ou Mouvement.GAUCHE, direction dans laquelle le monstre est tourné
	 */
	public abstract String droite_gauche (int anim);  

	/**
	 * IA pour le deplacement du monstre 
	 * 
	 * @param monstre, le monstre a deplacer 
	 * @param heros, le personnage jouable
	 * @param Monde, le niveau en cours  
	 */	
	public abstract void IA (List<Projectile> tabTirMonstre, Heros heros,AbstractModelPartie partie);
	/**
	 * Applique l'action voulue pour le deplacement du monstre 
	 * 
	 * @param monstre, le monstre a deplacer 
	 * @param Monde, le niveau en cours 
	 */	
	public abstract void changeMouv (AbstractModelPartie partie,Deplace deplace);
	/**
	 * Align to the rigth/left/up/down the next movement/hitbox to the previous one
	 * @param monstre
	 * @param animActu
	 * @param depSuiv
	 * @param animSuiv
	 * @param partie
	 */
	public abstract void alignHitbox(int animActu,Mouvement depSuiv, int animSuiv, AbstractModelPartie partie,Deplace deplace);
	
	//TODO: DELETE
	@Override
	public void registerEffect(Effect eff)
	{
		super.registerEffect(eff);
	}

	
	/**
	 * accesseur permettant de gérer la vie max et la vie mimimale
	 */	
	public void addLife(int degats)
	{
		if((life+degats)<=MINLIFE)
		{
			life=MINLIFE;
			needDestroy=true;
		}
		else if ((life+degats)>MAXLIFE)
		{
			life=MAXLIFE;
		}
		else
		{
			life+=degats;
		}
	}

	@Override
	public int getMaxBoundingSquare()
	{
		return deplacement.getMaxBoundingSquare(this);
	}
	@Override
	public Hitbox getHitbox(Point INIT_RECT,Point screenDisp) {
		return  Hitbox.plusPoint(deplacement.hitbox.get(anim), new Point(xpos(),ypos()),true);
	}

	@Override
	public Hitbox getHitbox(Point INIT_RECT,Point screenDisp,Mouvement _dep, int _anim) {
		Mouvement temp = _dep.Copy(this); //create the mouvement
		return Hitbox.plusPoint(temp.hitbox.get(_anim), new Point(xpos(),ypos()),true);	
	}
	
	@Override
	public void handleWorldCollision(Vector2d normal, AbstractModelPartie partie,Collidable collidedObject,boolean stuck) {
		conditions.OnAttacherCollided();
		boolean collision_gauche = normal.x>0;
		boolean collision_droite = normal.x<0;
		//boolean collision_haut = normal.y>0;
		boolean collision_bas = normal.y<0;
		
		last_colli_left=collision_gauche;
		last_colli_right=collision_droite;
		
		final boolean mem_glisse=glisse;
		final boolean mem_finSaut = finSaut;
		final boolean mem_peutSauter=peutSauter;
		final boolean mem_useGravity=useGravity;
		
		resetHandleCollision = new ResetHandleCollision(){
			@Override
			public void reset()
			{
				glisse=mem_glisse;
				finSaut=mem_finSaut;
				peutSauter=mem_peutSauter;
				useGravity= mem_useGravity;
			}};
			
		if(collision_gauche || collision_droite)
			glisse=true;
		if(collision_bas)
		{
			finSaut=true;
			peutSauter=true;
			useGravity=false;
		}
	}
	@Override
	public void handleObjectCollision(AbstractModelPartie partie,Collidable collider,Vector2d normal) 
	{
		if(TypeObject.isTypeOf(collider, TypeObject.FLECHE))
		{
			addLife( ((Fleche)collider).damage);
		}
	}
	public class ResetHandleCollision
	{
		public void reset()
		{};
	}
	
	@Override
	public Hitbox getNextEstimatedHitbox(AbstractModelPartie partie,double newRotation,int anim)
	{
		throw new java.lang.UnsupportedOperationException("Not supported yet.");
	}
}


