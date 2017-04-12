package fleches;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import collision.Collision;
import deplacement.Deplace;
import deplacement.Mouvement;
import deplacement_tir.Mouvement_tir;
import deplacement_tir.T_normal;
import monstre.Monstre;
import music.MusicBruitage;
import partie.AbstractModelPartie;
import principal.InterfaceConstantes;
import types.Hitbox;
import types.TypeObject;
import types.Vitesse;

public class Fleche extends Collidable implements InterfaceConstantes{
	
	public static String NORMAL="";
	
	public static class MATERIELLE
	{
		public static String FOUDRE="foudre";
		public static String ELECTRIQUE="electrique";
		public static String GLACE="glace";
		public static String ROCHE="roche";

	}
	public static class SPIRITUELLE
	{
		public static String FEU="feu";
		public static String OMBRE="ombre";
		public static String VENT="vent";
		public static String GRAPPIN="grappin";

	}
	public static class DESTRUCTRICE
	{
		public static String CHARGEE="chargee";
		public static String EXPLOSIVE="explosive";
		public static String TROU_NOIR="trou_noir";
		public static String BOGUE="bogue";

	}
	public static class RUSEE
	{
		public static String AUTO_TELEGUIDEE="auto_teleguidee";
		public static String RETARD="retard";
		public static String V_FLECHE="v_fleche";
		public static String CAC="corps_a_corps";

	}
	
	public boolean doitDeplace=false;
	public boolean isPlanted=false; //fleche plantée dans le sol
	public boolean generatedEffect = false;
	public long TEMPS_DESTRUCTION_FLECHE = (long) Math.pow(10, 9);//nanos, 1sec 
	public boolean nulle =false;
	public boolean encochee =false;
	
	public String type_fleche= NORMAL;

	public AffineTransform draw_tr;
	//relative to heros position
	public List<Integer> xanchor=Arrays.asList(28,20,45,45,40,30,55,52,70,35);
	public List<Integer> yanchor=Arrays.asList(30,20,22,25,45,50,65,42,30,40);

	//timer pour savoir quand est ce que la fleche doit disparaitre (voir interface constante)
	public long tempsDetruit = 0;
	
	public int degat= -50;
	private boolean animationChanged=false;
		
	public Fleche(List<Fleche> tabFleche,int current_frame)
	{
		type = TypeObject.fleche;
		type_fleche=NORMAL;
		anim=0;
		doitDeplace=false;
		needDestroy= false;
		tempsDetruit = 0;
		slowDownFactor=3;
		fixedWhenScreenMoves=false;
		localVit=new Vitesse(0,0);
		envirVit=new Vitesse(0,0);

		deplacement = new T_normal(TypeObject.fleche,T_normal.tir,current_frame);
		
		nulle=false;
		encochee=true;
		tabFleche.add(this);
	}

	public void timer()
	{
		tempsDetruit=System.nanoTime();
	}
	public void setPosition(int x, int y)
	{
		xpos=x;
		ypos=y;
	}
	
	public void flecheDecochee(AbstractModelPartie partie,Deplace deplace)
	{
		doitDeplace=true;
		encochee=false;
		
		//get current position
		Point2D newpos= draw_tr.transform(new Point(0,0), null);
		xpos=(int) newpos.getX()-partie.xScreendisp;
		ypos=(int) newpos.getY()-partie.yScreendisp;
		
		convertHitbox(partie.INIT_RECT,draw_tr,new Point(xpos,ypos),new Point(partie.xScreendisp,partie.yScreendisp));

		//reset the 0 of the transformation. usefull since the position changed from 0 to its drawing position (~700,400)
		draw_tr.translate(-xpos, -ypos);


		deplacement.setSpeed(TypeObject.fleche, this, anim);
		MusicBruitage.me.startBruitage("arc");
		
	}
	@Override
	public void destroy(){
		//do nothing when detroyed 
	}
	protected void onPlanted(List<Collidable> objects,AbstractModelPartie partie)
	{}
	public void convertHitbox(Point INIT_RECT,AffineTransform tr,Point pos,Point screendisp) {
		List<Hitbox> current = deplacement.hitbox;
		List<Hitbox> new_rotated_hit = new ArrayList<Hitbox>();

		for (int i = 0; i<current.size(); ++i)
		{
			Polygon current_pol = current.get(i).polygon; 
			Polygon new_pol = new Polygon();
			for(int j = 0; j<current_pol.npoints; ++j)
			{
				Point2D temp = tr.transform(new Point(current_pol.xpoints[j],current_pol.ypoints[j]), null);
				new_pol.addPoint((int)temp.getX()-pos.x-screendisp.x,(int)temp.getY()-pos.y-screendisp.y);
				
			}
			new_rotated_hit.add(new Hitbox(new_pol));
		}

		deplacement.hitbox_rotated= new_rotated_hit;
	}
	@Override
	public Hitbox getHitbox(Point INIT_RECT) {
		if(!encochee)
			return  Hitbox.plusPoint(deplacement.hitbox_rotated.get(anim), new Point(xpos,ypos),true);	
		else
			return  Hitbox.plusPoint(deplacement.hitbox.get(anim), new Point(xpos,ypos),true);	
	}
	
	@Override
	public Hitbox getHitbox(Point INIT_RECT, Mouvement _dep, int _anim) {
		//ASSUME WE ALWAYS USE THIS WHEN THE ARROW IS SHOT (!encochee)
		Mouvement_tir temp = (Mouvement_tir) _dep.Copy(TypeObject.fleche); //create the mouvement
		return Hitbox.plusPoint(temp.hitbox_rotated.get(_anim), new Point(xpos,ypos),true);
	}
	public Hitbox getWorldHitbox(AbstractModelPartie partie) {
		Hitbox hit1 = getHitbox(partie.INIT_RECT);
		/*if(encochee)
			hit1=Hitbox.plusPoint(deplacement.hitbox.get(anim), new Point(xpos,ypos),true);
		else
			hit1=Hitbox.plusPoint(deplacement.hitbox_rotated.get(anim), new Point(xpos,ypos),true);*/
		return Hitbox.plusPoint(hit1, new Point(partie.xScreendisp,partie.yScreendisp),true);
	}
		
		
	@Override
	public void handleWorldCollision(Vector2d normal, AbstractModelPartie partie,
			Deplace deplace) {
		
		double coef1= localVit.vect2d().dot(normal)/normal.lengthSquared();
		localVit = new Vitesse((int)(localVit.x-coef1*normal.x),(int)(localVit.y-coef1*normal.y));
		double coef2= envirVit.vect2d().dot(normal)/normal.lengthSquared();
		envirVit = new Vitesse((int)(envirVit.x-coef2*normal.x),(int)(envirVit.y-coef2*normal.y));

		boolean collision_gauche = (localVit.x<=0) && (normal.x>0);
		boolean collision_droite = (localVit.x>=0) && (normal.x<0);
		//boolean collision_haut = (vit.y<=0) && (normal.y>0);
		//boolean collision_bas = (vit.y>=0) && (normal.y<0);
		last_colli_left=collision_gauche;
		last_colli_right=collision_droite;
		localVit = new Vitesse(0,0);
		envirVit = new Vitesse(0,0);
		this.doitDeplace=false;
		this.isPlanted=true;
		ArrayList<Collidable> objects = Collidable.getAllCollidable(partie,true,true);

		onPlanted(objects,partie);
	}
	@Override
	public void handleObjectCollision(AbstractModelPartie partie,
			Deplace deplace) 
	{
		this.needDestroy=true;
		ArrayList<Collidable> objects = Collidable.getAllCollidable(partie, true, true);
		onPlanted(objects,partie);
	}

	@Override
	public void memorizeCurrentValue() {
		//nothing to memorize so far 
		/*currentValue=new CurrentValue(){		
			@Override
			public void res()
			{}};*/
	}
	@Override
	public boolean[] deplace(AbstractModelPartie partie, Deplace deplace) {
			try {
				anim=changeAnim(partie,deplace);} 
			catch (InterruptedException e) {e.printStackTrace();}
		boolean[] res = {doitDeplace,animationChanged};
		return res;
	}
	public int changeAnim(AbstractModelPartie partie,Deplace deplace) throws InterruptedException//{{
	{
		if(encochee && !doitDeplace)
		{
			//set the anim 
			double[] anim_rot = deplace.getAnimRotationTir(partie,true);
			int animFleche = deplacement.updateAnimation(TypeObject.fleche, anim, partie.getFrame());
			rotation = anim_rot[1];
			if(animFleche==anim)
				animationChanged=false;
			return animFleche;
		}

		else if(doitDeplace)
		{
			if (useGravity)
			{
				int animSuivante= gravityAnim();
				if(animSuivante==anim)
					animationChanged=false;
				decallageFleche (animSuivante, partie,deplace );
				return(animSuivante);
			}
			else {
				int animFleche = deplacement.updateAnimation(TypeObject.fleche, anim, partie.getFrame());
				if(animFleche==anim)
					animationChanged=false;
				return animFleche;
			}
			
		}
		return (anim+1)%4;
	}
	public int gravityAnim()
	{
		Vitesse gvit = getGlobalVit();
		if(gvit.y ==0 && gvit.x==0)
		{
			return(anim);//on garde la même animation
		}
		else if(gvit.x>0 && Math.abs((float)gvit.y/gvit.x)<=Math.abs(Math.tan(Math.PI/ 8)))
		{
			return(0);
		}
		else if(gvit.y > 0 && gvit.x>0 && Math.abs((float)gvit.y/gvit.x)>=Math.abs(Math.tan(Math.PI/ 8)))
		{
			return(1);
		}
		else if(gvit.y > 0  && Math.abs((float)gvit.y/gvit.x)>=Math.abs(Math.tan(3* Math.PI/ 8)))
		{
			return(2);
		}
		else if(gvit.y > 0 && gvit.x<0 && Math.abs((float)gvit.y/gvit.x)>=Math.abs(Math.tan(Math.PI/ 8)))
		{
			return(3);
		}
		else if(gvit.x<0 && Math.abs((float)gvit.y/gvit.x)<= Math.abs(Math.tan(Math.PI/ 8)))
		{
			return(4);
		}
		else if(gvit.y <0 && gvit.x<0 && Math.abs((float)gvit.y/gvit.x)>=Math.abs(Math.tan(Math.PI/ 8)))
		{
			return(5);
		}
		else if(gvit.y <0 && Math.abs((float)gvit.y/gvit.x)>=Math.abs(Math.tan(3* Math.PI/ 8)))
		{
			return(6);
		}
		else if(gvit.y <0 && gvit.x>0 && Math.abs((float)gvit.y/gvit.x)>=Math.abs(Math.tan(Math.PI/ 8)))
		{
			return(7);
		}
		else {
			throw new IllegalArgumentException("Fleche/gravityAnim: Unknown values");
		}
	}
	
	public void decallageFleche(int animSuivante, AbstractModelPartie partie,Deplace deplace)
	{
		// on veut que le centre bas des flèches coincident 
		Point positionFinal=placerCentreBasFleche(this,animSuivante,partie.INIT_RECT);

		// on effectue le decallage
		xpos=positionFinal.x;
		ypos=positionFinal.y;

		Collision colli = new Collision();
		//si il y a collision lors du changement d'animation, on doit arreter la fleche
		if (!colli.ejectWorldCollision(partie,deplace , this))
		{
			handleWorldCollision(new Vector2d(), partie,deplace);
		}

	}
	private Vector2d angleToVector(double angle)
	{
		Vector2d res = new Vector2d(0,0);
		//Minimize chance of computing big numbers by taking the inverse 
		if(Math.abs(angle-Math.PI/2)< 0.001 * Math.PI || Math.abs(angle-3*Math.PI/2)< 0.001 * Math.PI)
			res=new Vector2d(1/Math.tan(angle),1);
		else
			res=new Vector2d(1,Math.tan(angle));

		res.normalize();
		return res;
	}
	/**
	 * Permet d'obtenir le centre en bas de la fleche (endroit ou on l'encoche)
	 * 
	 * @param xpos, position x de la fleche
	 * @param ypos, position y de la fleche
	 * @param anim, animation de la fleche
	 * @return le centre bas de la fleche 
	 */	
	public Point centreBasFleche(Hitbox flecheHit,int anim)
	{
		//In order to avoid that by taking the perfect direction we find only one points, we will calculate for two shifted angles
		double noise = 0.05; //~5°
		double angle = anim * Math.PI / 8; 
		Vector2d flecheDir1 = angleToVector(angle+noise);
		Vector2d flecheDir2 = angleToVector(angle-noise);

		//Look for the bottom center which is in the opposite way
		flecheDir1.negate();
		flecheDir2.negate();

		Vector2d p1 = Hitbox.supportPoint(flecheDir1, flecheHit.polygon);
		Vector2d p2 = Hitbox.supportPoint(flecheDir2, flecheHit.polygon);

		return new Point((int)(p1.x+p2.x),(int)(p1.y+p2.y));
	}
	/**
	 * Permet de placer le centre bas d'une fleche a un point donné
	 * 
	 * @param xValeurVoulue, position en x voulue pour le bas de la fleche
	 * @param yValeurVoulue,  position en y voulue pour le bas de la fleche
	 * @param anim, animation de la fleche
	 * @return la valeur de xpos et ypos pour la fleche
	 */	
	public Point placerCentreBasFleche(Fleche fleche,int nouvAnim,Point INIT_RECT)
	{
		Point currentCenter = centreBasFleche(fleche.getHitbox(INIT_RECT),fleche.anim);
		Point nextCenter = centreBasFleche(fleche.getHitbox(INIT_RECT, fleche.deplacement, nouvAnim),nouvAnim);

		return new Point(currentCenter.x-nextCenter.x,currentCenter.y-nextCenter.y);

	}
	@Override
	public void handleStuck(AbstractModelPartie partie, Deplace deplace) {
		handleWorldCollision( new Vector2d(), partie, deplace );
	}
	@Override
	public void handleDeplacementSuccess(AbstractModelPartie partie,
			Deplace deplace) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void resetVarBeforeCollision()
	{
		//nothing
	}
	@Override
	public void resetVarDeplace() {
		//nothing
	}

	@Override
	public void applyFriction(double minlocalSpeed, double minEnvirSpeed) {
		//nothing
	}



}
