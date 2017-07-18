package monstre;

import java.awt.Image;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import deplacement.Deplace;
import deplacement.Mouvement;
import deplacement.Mouvement_perso;
import effects.Effect;
import music.MusicBruitage;
import partie.AbstractModelPartie;
import personnage.Heros;
import principal.InterfaceConstantes;
import types.Hitbox;
import types.Vitesse;


@SuppressWarnings("serial")
public abstract class Monstre extends Collidable implements InterfaceConstantes, Serializable{
	//on définit la position du coin en haut à  gauche de la hitbox

	public boolean finSaut;
	public boolean peutSauter;
	public boolean glisse;
		
	
	public boolean actionReussite;
	public boolean doitChangMouv;
	public Mouvement_perso nouvMouv;
	public int nouvAnim;

	protected ResetHandleCollision resetHandleCollision;

	public List<Integer> xDecallagePlacementTir= new ArrayList<Integer>();
	public List<Integer> yDecallagePlacementTir=  new ArrayList<Integer>();

	Image SPattente0; 
	Image SPattente1;

	Image SPmarche0;
	Image SPmarche1;
	Image SPmarche2;
	Image SPmarche3; 

	private int life=MAXLIFE;
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
	public abstract void IA (List<TirMonstre> tabTirMonstre, Heros heros,AbstractModelPartie partie);
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
	 * getter
	 */	
	public int getLife(){return(life);};
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
	public Hitbox getHitbox(Point INIT_RECT) {
		return  Hitbox.plusPoint(deplacement.hitbox.get(anim), new Point(xpos(),ypos()),true);
	}

	@Override
	public Hitbox getHitbox(Point INIT_RECT,Mouvement _dep, int _anim) {
		Mouvement temp = _dep.Copy(type); //create the mouvement
		return Hitbox.plusPoint(temp.hitbox.get(_anim), new Point(xpos(),ypos()),true);	
	}
	
	public Hitbox getWorldHitbox(AbstractModelPartie partie) {
		Hitbox hit1  =Hitbox.plusPoint(deplacement.hitbox.get(anim), new Point(xpos(),ypos()),true);
		return Hitbox.plusPoint(hit1, new Point(partie.xScreendisp,partie.yScreendisp),true);	}
	@Override
	public void handleWorldCollision(Vector2d normal, AbstractModelPartie partie) {
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
	public void handleObjectCollision(AbstractModelPartie partie,Collidable collider) {}
	public class ResetHandleCollision
	{
		public void reset()
		{};
	}
	
}


