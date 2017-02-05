package personnage;

import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import collision.Collision;
import deplacement.Deplace;
import deplacement.Mouvement;
import deplacement_tir.Mouvement_tir;
import deplacement_tir.T_normal;
import music.MusicBruitage;
import partie.AbstractModelPartie;
import principal.InterfaceConstantes;
import types.Hitbox;
import types.Vitesse;

public class Fleche extends Collidable implements InterfaceConstantes{
	
	public boolean doitDeplace=false;
	public boolean isPlanted=false; //fleche plantée dans le sol
	public boolean nulle =false;
	public boolean encochee =false;
	
	public List<Integer> xcenterFleche=Arrays.asList(37,45,33,6 ,-1,4,38,32);
	public List<Integer> ycenterFleche=Arrays.asList(29,23,60,22,27,2,-18,4);

	Image fleche0;
	Image fleche1;
	Image fleche2;
	Image fleche3;
	Image fleche4;
	Image fleche5;
	Image fleche6;
	Image fleche7;
	//timer pour savoir quand est ce que la fleche doit disparaitre (voir interface constante)
	public long tempsDetruit = 0;
	
	public int degat= -50;
	 MusicBruitage bruitage;

	//constructeur pour charger les images des fleches 
	public Fleche(boolean b)
	{
		chargerFleches();
	}
		
	public Fleche()
	{
		nulle= true;
		doitDeplace=false;
		needDestroy= false;
		tempsDetruit = 0;
		bruitage = new MusicBruitage("arc");
		slowDownFactor=3;
		fixedWhenScreenMoves=false;
		vit=new Vitesse(0,0);
		deplacement = new T_normal();
	}
	public Fleche(int xF, int yF, Mouvement_tir mouv)
	{
		xpos=xF;
		ypos=yF;
		deplacement=mouv;
		nulle = false;
		tempsDetruit = 0;
		bruitage = new MusicBruitage("arc");
		slowDownFactor=3;
		fixedWhenScreenMoves=false;
		vit=new Vitesse(0,0);

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
	public void chargerFleches()
	{
		 fleche0=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/fleches/0.gif"));
		 fleche1=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/fleches/1.gif"));
		 fleche2=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/fleches/2.gif"));
		 fleche3=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/fleches/3.gif"));
		 fleche4=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/fleches/4.gif"));
		 fleche5=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/fleches/5.gif"));
		 fleche6=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/fleches/6.gif"));
		 fleche7=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/fleches/7.gif"));
	}
	public Image getImage(Fleche fleche)
	//{{
	{
		switch(fleche.anim)
		{
		case 0: return(fleche0);
		case 1: return(fleche1);
		case 2: return(fleche2);
		case 3: return(fleche3);
		case 4: return(fleche4);
		case 5: return(fleche5);
		case 6: return(fleche6);
		case 7: return(fleche7);
		default: return(fleche0);
		}
	}
	//}}
	public void flecheDecochee(Deplace deplace)
	{
		doitDeplace=true;
		encochee=false;
		deplacement.setSpeed(Mouvement_tir.fleche, this, anim, deplace);
		bruitage.startBruitage(100);
		
	}
	@Override
	public void destroy(){
		//do nothing when detroyed 
	}
	@Override
	public Hitbox getHitbox(Point INIT_RECT) {
		return  Hitbox.plusPoint(deplacement.hitbox.get(anim), new Point(xpos,ypos),true);	
	}
	
	@Override
	public Hitbox getHitbox(Point INIT_RECT, Mouvement _dep, int _anim) {
		Mouvement_tir temp = (Mouvement_tir) _dep.Copy(Mouvement_tir.fleche); //create the mouvement
		return Hitbox.plusPoint(temp.hitbox.get(_anim), new Point(xpos,ypos),true);
	}
	public Hitbox getWorldHitbox(AbstractModelPartie partie) {
		Hitbox hit1  =Hitbox.plusPoint(deplacement.hitbox.get(anim), new Point(xpos,ypos),true);
		return Hitbox.plusPoint(hit1, new Point(partie.xdeplaceEcran + partie.xdeplaceEcranBloc,
				partie.ydeplaceEcran + partie.ydeplaceEcranBloc),true);
	}
		
		
	@Override
	public void handleWorldCollision(Vector2d normal, AbstractModelPartie partie,
			Deplace deplace) {
		
		//double coef= vit.vect2d().dot(normal)/normal.lengthSquared();
		//vit = new Vitesse((int)(vit.x-coef*normal.x),(int)(vit.y-coef*normal.y));
		
		//boolean collision_gauche = (vit.x<=0) && (normal.x>0);
		//boolean collision_droite = (vit.x>=0) && (normal.x<0);
		//boolean collision_haut = (vit.y<=0) && (normal.y>0);
		//boolean collision_bas = (vit.y>=0) && (normal.y<0);
		
		vit = new Vitesse(0,0);
		this.doitDeplace=false;
		this.isPlanted=true;
	}
	@Override
	public void handleObjectCollision(AbstractModelPartie partie,
			Deplace deplace) {this.needDestroy=true;}

	@Override
	public void memorizeCurrentValue() {
		//nothing to memorize so far 
		/*currentValue=new CurrentValue(){		
			@Override
			public void res()
			{}};*/
	}
	@Override
	public boolean deplace(AbstractModelPartie partie, Deplace deplace) {
			try {
				anim=changeAnim(partie,deplace);} 
			catch (InterruptedException e) {e.printStackTrace();}
		return doitDeplace;
	}
	public int changeAnim(AbstractModelPartie partie,Deplace deplace) throws InterruptedException//{{
	{
		if(encochee && !doitDeplace)
		{
			//set the anim 
			int animFleche = partie.heros.anim;
			//set the position of the arrow so that it fits the bow
			xpos=(partie.heros.xpos-partie.xdeplaceEcran-partie.xdeplaceEcranBloc)+xcenterFleche.get(animFleche);
			ypos= (partie.heros.ypos-partie.ydeplaceEcran-partie.ydeplaceEcranBloc)+ycenterFleche.get(animFleche);
			return animFleche;
		}

		else if(doitDeplace)
		{
			if (useGravity)
			{
				int animSuivante= gravityAnim();
				decallageFleche (animSuivante, partie,deplace );
				return(animSuivante);
			}
			else 
				return(anim);
			
		}
		return anim;
	}
	public int gravityAnim()
	{
		if(vit.y ==0 && vit.x==0)
		{
			return(anim);//on garde la même animation
		}
		else if(vit.x>0 && Math.abs((float)vit.y/vit.x)<=Math.abs(Math.tan(Math.PI/ 8)))
		{
			return(0);
		}
		else if(vit.y > 0 && vit.x>0 && Math.abs((float)vit.y/vit.x)>=Math.abs(Math.tan(Math.PI/ 8)))
		{
			return(1);
		}
		else if(vit.y > 0  && Math.abs((float)vit.y/vit.x)>=Math.abs(Math.tan(3* Math.PI/ 8)))
		{
			return(2);
		}
		else if(vit.y > 0 && vit.x<0 && Math.abs((float)vit.y/vit.x)>=Math.abs(Math.tan(Math.PI/ 8)))
		{
			return(3);
		}
		else if(vit.x<0 && Math.abs((float)vit.y/vit.x)<= Math.abs(Math.tan(Math.PI/ 8)))
		{
			return(4);
		}
		else if(vit.y <0 && vit.x<0 && Math.abs((float)vit.y/vit.x)>=Math.abs(Math.tan(Math.PI/ 8)))
		{
			return(5);
		}
		else if(vit.y <0 && Math.abs((float)vit.y/vit.x)>=Math.abs(Math.tan(3* Math.PI/ 8)))
		{
			return(6);
		}
		else if(vit.y <0 && vit.x>0 && Math.abs((float)vit.y/vit.x)>=Math.abs(Math.tan(Math.PI/ 8)))
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
	public void applyFriction(int minspeed) {
		//do nothing
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
	public int setReaffiche() {
		if(deplacement.IsDeplacement(Mouvement_tir.tir_normal)){
			return(20);
		}
		else {
			throw new IllegalArgumentException("ERREUR setReaffiche, ACTION INCONNUE  "  +deplacement.getClass().getName());
		}
	}




}
