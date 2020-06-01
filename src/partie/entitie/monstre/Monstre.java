package partie.entitie.monstre;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector2d;

import gameConfig.InterfaceConstantes;
import gameConfig.ObjectTypeHelper;
import gameConfig.ObjectTypeHelper.ObjectType;
import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.effects.Effect;
import partie.entitie.Entity;
import partie.entitie.heros.Heros;
import partie.input.InputPartie;
import partie.input.InputPartiePool;
import partie.modelPartie.AbstractModelPartie;
import partie.mouvement.Deplace;
import partie.mouvement.Mouvement;
import partie.mouvement.Mouvement.DirSubTypeMouv;
import partie.mouvement.entity.Mouvement_entity;
import partie.projectile.Projectile;
import partie.projectile.fleches.Fleche;


@SuppressWarnings("serial")
public abstract class Monstre extends Entity implements InterfaceConstantes, Serializable{
	//on définit la position du coin en haut à  gauche de la hitbox

	protected boolean wasGrounded=false;
	
	public boolean isStatic;
	
    public interface AIAction{};//used for implementation in enum of sub classes (i.e. Ai of spirel)
	public AIAction lastIAAction =null;

	protected InputPartie inputPartie;
	private InputPartiePool monstreInputPool; //based on a unique input partie since montre input and heros input need to be different
	private InputPartiePool playerInputPool; //input pool of the player that controles this monstre 
	protected InputPartiePool getCurrentInputPool(){if(controlledBy==null)return monstreInputPool; else return playerInputPool;}; //input pool that is currently used: either monstreInputPool or playerInputPool
	
	protected Heros controlledBy=null; //null if AI controls the monster, reference to the collidable controlling the monster if not
	
	protected ResetHandleCollision resetHandleCollision;
	Image SPattente0; 
	Image SPattente1;

	Image SPmarche0;
	Image SPmarche1;
	Image SPmarche2;
	Image SPmarche3; 
	public Monstre(InputPartie inputPartie)
	{
		super();
		this.setCollideWithout(Arrays.asList(ObjectType.MONSTRE,ObjectType.TIR_MONSTRE));
		monstreInputPool = new InputPartiePool();
	}
	/**
	 * Permet de savoir de quel cote est tourné le monstre
	 * 
	 * @param mouv_index, l'animation du monstre
	 * 
	 * @return String , Mouvement.DROITE ou Mouvement.GAUCHE, direction dans laquelle le monstre est tourné
	 */
	public abstract DirSubTypeMouv droite_gauche (int mouv_index);  
	
	public void startControlledBy(Heros who){
		
		playerInputPool = new InputPartiePool(who.getInputPartie());//Create new input partie pool: we want to get the input from keyboard but to have 
		//Get previous input and release jeys for continuity of the movement
		playerInputPool.copyValues(monstreInputPool);
		playerInputPool.releaseIfDown(false);
		monstreInputPool.resetAll();
		//a different handling (in case both heros and monster are controlled at the same time)
		controlledBy = who;
	}
	
	public void endControlledBy(Heros who){
		if(who.equals(controlledBy)){
			//Get previous input and release jeys for continuity of the movement
			monstreInputPool.copyValues(playerInputPool);
			monstreInputPool.releaseIfDown(false);
			playerInputPool=null;
			controlledBy=null;
		}
	}
	/**
	 * IA pour le deplacement du monstre 
	 * 
	 * @param monstre, le monstre a deplacer 
	 * @param heros, le personnage jouable
	 * @param Monde, le niveau en cours  
	 */	
	public abstract void AI (List<Projectile> tabTirMonstre, AbstractModelPartie partie);

	
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
		return getMouvement().getMaxBoundingSquare();
	}
	@Override
	public Point getMaxBoundingRect()
	{
		return getMouvement().getMaxBoundingRect();
	}
	@Override
	public AffineTransform computeDrawTr(Point screenDisp)
	{
		return null;
	}
	@Override
	public Hitbox computeHitbox(Point INIT_RECT,Point screenDisp) {
		return getMouvementHitboxCopy(getMouvIndex()).translate(getXpos(),getYpos());
	}

	@Override
	public Hitbox computeHitbox(Point INIT_RECT,Point screenDisp,Mouvement _dep, int _mouv_index) {
		Mouvement temp = _dep.Copy(); //create the mouvement
		return temp.getScaledHitboxCopy(_mouv_index,getScaling()).translate(getXpos(),getYpos());//no need to copy hitbox as it comes from a copied mouvement 
	}
	@Override
	protected void onStartDeplace(){}
	@Override
	public void handleWorldCollision(Vector2d normal, AbstractModelPartie partie,Collidable collidedObject,boolean stuck) {
		conditions.OnAttacherCollided();
		boolean collision_gauche = normal.x>0;
		boolean collision_droite = normal.x<0;
		//boolean collision_haut = normal.y>0;
		boolean collision_bas = normal.y<0;
		
		last_colli_left=collision_gauche;
		last_colli_right=collision_droite;
		
		final boolean mem_useGravity=useGravity;
		final boolean mem_wasGrounded = wasGrounded;
		resetHandleCollision = new ResetHandleCollision(){
			@Override
			public void reset()
			{
				useGravity= mem_useGravity;
				wasGrounded=mem_wasGrounded;
			}};
			
		if(collision_bas)
		{
			wasGrounded=true;
			useGravity=false;
		}
	}
	@Override
	public void handleObjectCollision(AbstractModelPartie partie,Collidable collider,Vector2d normal) 
	{
		if(ObjectTypeHelper.isTypeOf(collider, ObjectType.FLECHE))
		{
			addLife( ((Fleche)collider).damage);
		}
	}
	public class ResetHandleCollision
	{
		public void reset()
		{};
	}
	
}


