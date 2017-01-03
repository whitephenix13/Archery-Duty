package personnage;

import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.List;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.vecmath.Vector2d;

import collision.Collidable;
import collision.Collision;
import collision.GJK_EPA;
import deplacement.Attente;
import deplacement.Deplace;
import deplacement.Mouvement;
import music.Music;
import partie.AbstractModelPartie;
import partie.ModelPartie;
import principal.InterfaceConstantes;
import types.Bloc;
import types.Hitbox;
import types.Vitesse;

public class Heros extends Collidable{

	public int anim; 
	//on définit son type de déplacement 
	public Mouvement deplacement = new Attente();

	private int life=InterfaceConstantes.MAXLIFE;
	private int spe=InterfaceConstantes.MAXSPE;

	private long tempsTouche;
	private long tempsClignote;
	//derniere fois que la spe a été baissé à cause du slow down ou augmenté hors du slow down
	private long tempsSpe;
	public boolean invincible=true;
	//lorsque le personnage est touche, on le fait clignoter, ce booleen permet de savoir si on l'affiche ou non
	public boolean afficheTouche=true; 
	public boolean useGravity=true;
	//Variables pour mémoriser la direction de la dernière collision
	public boolean last_colli_left=false;
	public boolean last_colli_right=false;
	//{{on charge les images 
	Image attente0;
	Image attente1;

	Image glissade0;
	Image glissade1;

	Image saut0;
	Image saut1;
	Image saut2;
	Image saut3;
	Image saut4;
	Image saut5;

	Image marche0;
	Image marche1;
	Image marche2;
	Image marche3;
	Image marche4;
	Image marche5;
	Image marche6;
	Image marche7;

	Image course0;
	Image course1;
	Image course2;
	Image course3;
	Image course4;
	Image course5;
	Image course6;
	Image course7;

	Image tir0;
	Image tir1;
	Image tir2;
	Image tir3;
	Image tir4;
	Image tir5;
	Image tir6;
	Image tir7;
	//}}
	public Heros(){
		xpos = 0;
		ypos = 0;
		vit = new Vitesse(0,0);
		slowDownFactor=3;//6
		fixedWhenScreenMoves=true;
		anim=0;
		deplacement = new Attente();
		tempsTouche=System.nanoTime();
		chargerImages();
	};
	public Heros( int xPo,int yPo, int _anim, Mouvement dep){
		xpos = xPo;
		ypos = yPo; 
		vit = new Vitesse(0,0);
		slowDownFactor=3;//6
		fixedWhenScreenMoves=true;
		deplacement=dep ;
		anim=_anim;
		tempsTouche=System.nanoTime();
		chargerImages();
	}
	public String droite_gauche (int anim){
		if(this.deplacement.getClass().getName().equals("deplacement.Marche") || this.deplacement.getClass().getName().equals("deplacement.Course") || this.deplacement.getClass().getName().equals("deplacement.Dash"))
		{
			if(anim <4)
			{
				return ("Gauche");
			}
			else return("Droite");
		}
		else if(this.deplacement.getClass().getName().equals("deplacement.Attente"))
		{
			if(anim <1)
			{
				return ("Gauche");
			}
			else return("Droite");
		}
		else if(this.deplacement.getClass().getName().equals("deplacement.Saut"))
		{
			if(anim <3)
			{
				return ("Gauche");
			}
			else return("Droite");
		}
		else if(this.deplacement.getClass().getName().equals("deplacement.Glissade"))
		{
			if(anim==0)
			{
				return ("Gauche");
			} 
			else return("Droite");
		}
		else if(this.deplacement.getClass().getName().equals("deplacement.Tir"))
		{
			if(anim<=3)
			{
				return ("Droite");
			} 
			else return("Gauche");
		}
		else 
			System.out.println("Heros/droite_gauche: ERREUR deplacement inconnu");
		return("Gauche");
	}

	public void touche (int degat) 
	{
		tempsTouche=System.nanoTime();
		afficheTouche=false;
		addLife(degat);
		invincible=true;
	}
	public void miseAjourTouche()
	{
		if((System.nanoTime()-tempsTouche)*Math.pow(10, -6)<=InterfaceConstantes.INV_TOUCHE)//heros invincible
		{
			if((System.nanoTime()-tempsClignote)*Math.pow(10, -6)>InterfaceConstantes.CLIGNOTE)
			{
				afficheTouche=!afficheTouche;
				tempsClignote=System.nanoTime();
			}
		}
		else
		{
			afficheTouche=true;
			invincible=false;
		}
	}
	public void miseAJourSpe(AbstractModelPartie partie)
	{
		if((System.nanoTime()-tempsSpe)*Math.pow(10, -6)>InterfaceConstantes.TEMPS_VAR_SPE)
		{
			tempsSpe=System.nanoTime();
			if(partie.slowDown)
			{
				addSpe(partie,-1);
			}
			else
			{
				addSpe(partie,1);
			}
		}
	}
	public int getLife()
	{
		return(life);
	}
	public void addLife(int add)
	{
		life += add;
		if(life>InterfaceConstantes.MAXLIFE){life=InterfaceConstantes.MAXLIFE;}
		if(life<InterfaceConstantes.MINLIFE){life=InterfaceConstantes.MINLIFE;}

	}
	public int getSpe()
	{
		return(spe);
	}

	public void addSpe(AbstractModelPartie partie,int add)
	{
		spe += add;
		if(spe>InterfaceConstantes.MAXSPE){spe=InterfaceConstantes.MAXSPE;}
		if(spe<InterfaceConstantes.MINSPE)
		{
			spe=InterfaceConstantes.MINSPE;
			partie.slowDown=false;
			partie.slowCount=0;
			Music music = new Music();

			try {
				if(partie.slowDown)
					music.slowDownMusic();
				else
					music.endSlowDownMusic();
			} catch (UnsupportedAudioFileException | IOException
					| LineUnavailableException e) {
				e.printStackTrace();
			}

		}

	}

	public void chargerImages()
	{
		attente0= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Attente/0.gif"));
		attente1= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Attente/1.gif"));

		glissade0= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Glissade/0.gif"));
		glissade1= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Glissade/1.gif"));

		saut0= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Saut/0.gif"));
		saut1= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Saut/1.gif"));
		saut2= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Saut/2.gif"));
		saut3= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Saut/3.gif"));
		saut4= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Saut/4.gif"));
		saut5= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Saut/5.gif"));

		marche0= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Marche/0.gif"));
		marche1= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Marche/1.gif"));
		marche2= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Marche/2.gif"));
		marche3= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Marche/3.gif"));
		marche4= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Marche/4.gif"));
		marche5= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Marche/5.gif"));
		marche6= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Marche/6.gif"));
		marche7= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Marche/7.gif"));

		course0= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Course/0.gif"));
		course1= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Course/1.gif"));
		course2= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Course/2.gif"));
		course3= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Course/3.gif"));
		course4= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Course/4.gif"));
		course5= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Course/5.gif"));
		course6= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Course/6.gif"));
		course7= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Course/7.gif"));

		tir0= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Tir/0.gif"));
		tir1= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Tir/1.gif"));
		tir2= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Tir/2.gif"));
		tir3= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Tir/3.gif"));
		tir4= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Tir/4.gif"));
		tir5= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Tir/5.gif"));
		tir6= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Tir/6.gif"));
		tir7= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Tir/7.gif"));
	}

	public Image getImages()
	//{{
	{
		if(deplacement.getClass().getName().equals("deplacement.Attente"))
		{
			switch(anim)
			{
			case 0: return(attente0);
			case 1: return(attente1);
			default: return(attente0);
			}
		}
		else if(deplacement.getClass().getName().equals("deplacement.Glissade"))
		{
			switch(anim)
			{
			case 0: return(glissade0);
			case 1: return(glissade1);
			default: return(glissade0);
			}
		}
		else if(deplacement.getClass().getName().equals("deplacement.Saut"))
		{
			switch(anim)
			{
			case 0: return(saut0);
			case 1: return(saut1);
			case 2: return(saut2);
			case 3: return(saut3);
			case 4: return(saut4);
			case 5: return(saut5);
			default: return(saut0);
			}
		}
		else if(deplacement.getClass().getName().equals("deplacement.Marche"))
		{
			switch(anim)
			{
			case 0: return(marche0);
			case 1: return(marche1);
			case 2: return(marche2);
			case 3: return(marche3);
			case 4: return(marche4);
			case 5: return(marche5);
			case 6: return(marche6);
			case 7: return(marche7);
			default: return(marche0);
			}
		}
		else if(deplacement.getClass().getName().equals("deplacement.Course"))
		{
			switch(anim)
			{
			case 0: return(course0);
			case 1: return(course1);
			case 2: return(course2);
			case 3: return(course3);
			case 4: return(course4);
			case 5: return(course5);
			case 6: return(course6);
			case 7: return(course7);
			default: return(course0);
			}
		}
		else if(deplacement.getClass().getName().equals("deplacement.Tir"))
		{
			switch(anim)
			{
			case 0: return(tir0);
			case 1: return(tir1);
			case 2: return(tir2);
			case 3: return(tir3);
			case 4: return(tir4);
			case 5: return(tir5);
			case 6: return(tir6);
			case 7: return(tir7);
			default: return(tir0);
			}
		}
		else
		{
			throw new IllegalArgumentException("Heros: GetImages deplacement inconnu");
		}
	}
	//}}

	public boolean IsDeplacement(String s)
	{
		return deplacement.IsDeplacement(s);
	}

	public Hitbox getHitbox(Point INIT_RECT) {
		//heros.xPos + heros.deplacement.xdecallsprite.get(heros.anim)
		int xg=xpos +deplacement.xdecallsprite.get(anim);
		int xd=xg+deplacement.xhitbox.get(anim);
		int yh=ypos +deplacement.ydecallsprite.get(anim);
		int yb=yh+deplacement.yhitbox.get(anim);
		Polygon p = new Polygon();
		p.addPoint(xg, yh);
		p.addPoint(xd, yh);
		p.addPoint(xd, yb);
		p.addPoint(xg, yb);
		return new Hitbox(p);		
	}
	public Hitbox getHitbox(Point INIT_RECT, Mouvement _dep, int _anim) {
		//heros.xPos + heros.deplacement.xdecallsprite.get(heros.anim)
		int xg=xpos +_dep.xdecallsprite.get(_anim);
		int xd=xg+_dep.xhitbox.get(_anim);
		int yh=ypos +_dep.ydecallsprite.get(_anim);
		int yb=yh+_dep.yhitbox.get(_anim);
		Polygon p = new Polygon();
		p.addPoint(xg, yh);
		p.addPoint(xd, yh);
		p.addPoint(xd, yb);
		p.addPoint(xg, yb);
		return new Hitbox(p);		
	}
	
	public Hitbox getShiftedHitbox(int i)//used to get slide hitbox
	{
		//boolean total= (i==0);
		boolean right= (i==1);
		boolean left = (i==-1);
		int decall_x_hit = 2; //need sqrt(2) to go out of the wall
		int xg=xpos +deplacement.xdecallsprite.get(anim) + (right ? decall_x_hit : 0);
		int xd=xg+deplacement.xhitbox.get(anim)+ (left ? -decall_x_hit : 0);
		int yh=ypos +deplacement.ydecallsprite.get(anim)+7;
		int yb=yh+36; 
		Polygon p = new Polygon();
		p.addPoint(xg, yh);
		p.addPoint(xd, yh);
		p.addPoint(xd, yb);
		p.addPoint(xg, yb);
		return new Hitbox(p);	
	}
	
	public Hitbox getWorldPosition(AbstractModelPartie partie)
	{
		int xCompScreenMove=0; 
		int yCompScreenMove=0;
		Point deplaceEcran =new Point(partie.xdeplaceEcran+partie.xdeplaceEcranBloc,
									  partie.ydeplaceEcran+partie.ydeplaceEcranBloc);
		if(fixedWhenScreenMoves)
		{
			xCompScreenMove=deplaceEcran.x;
			yCompScreenMove=deplaceEcran.y;
		}

		Point p = new Point(xCompScreenMove,
				yCompScreenMove);
		return Hitbox.minusPoint(getHitbox(partie.INIT_RECT),p);
	}
	public Hitbox getWorldPosition(AbstractModelPartie partie,Hitbox hit)
	{
		int xCompScreenMove=0; 
		int yCompScreenMove=0;
		Point deplaceEcran =new Point(partie.xdeplaceEcran+partie.xdeplaceEcranBloc,
									  partie.ydeplaceEcran+partie.ydeplaceEcranBloc);
		if(fixedWhenScreenMoves)
		{
			xCompScreenMove=deplaceEcran.x;
			yCompScreenMove=deplaceEcran.y;
		}

		Point p = new Point(xCompScreenMove,
				yCompScreenMove);
		return Hitbox.minusPoint(hit,p);
	}
	public boolean isGrounded(AbstractModelPartie partie)
	{
		Hitbox hit = getHitbox(partie.INIT_RECT);
		assert hit.polygon.npoints==4;
		//get world hitboxes with Collision
		Point p = new Point(partie.xdeplaceEcran+partie.xdeplaceEcranBloc,partie.ydeplaceEcran+partie.ydeplaceEcranBloc);
		Hitbox objectHitboxL= fixedWhenScreenMoves? Hitbox.minusPoint(hit,p): hit;
		//lowers all points by 1 //sqrt(2)/2 at most 
		for(int i=0; i<objectHitboxL.polygon.npoints; ++i)
			objectHitboxL.polygon.ypoints[i]+=1;
		//get all hitboxes: it can be slower 
		List<Bloc> mondeHitboxes=Collision.getMondeBlocs(partie.monde, objectHitboxL, partie.INIT_RECT, partie.TAILLE_BLOC);
		//if there is a collision between mondeHitboxes and objectHitbox, it means that lower the hitbox by 1 leads to a 
		//collision: the object is likely to be on the ground (otherwise, it is in a bloc).
		for(Bloc b : mondeHitboxes)
			if(GJK_EPA.intersectsB(objectHitboxL.polygon, b.getHitbox(partie.INIT_RECT).polygon, new Vector2d(0,1))==GJK_EPA.INTER)
			{
				System.out.println("\t== monde box"+ b.getHitbox(partie.INIT_RECT).toString()  );
				System.out.println("\t== heros box lowered "+ objectHitboxL.toString()  );

				return true;
			}
		return false;
	}
	
	public void handleCollision(Vector2d normal, AbstractModelPartie partie,
			Deplace deplace) {
		//project speed to ground 
		double coef= vit.vect2d().dot(normal)/normal.lengthSquared();
		vit = new Vitesse((int)(vit.x-coef*normal.x),(int)(vit.y-coef*normal.y));
		
		boolean collision_gauche = (vit.x<=0) && (normal.x>0);
		boolean collision_droite = (vit.x>=0) && (normal.x<0);
		//boolean collision_haut = (vit.y<=0) && (normal.y>0);
		boolean collision_bas = (vit.y>=0) && (normal.y<0);
		last_colli_left=collision_gauche;
		last_colli_right=collision_droite;
		System.out.println("Handle collision g d b " + collision_gauche +" "+collision_droite +" "+ collision_bas );
		System.out.println("normal " + normal.x+" "+ normal.y);
		if(collision_gauche || collision_droite)
			deplace.glisse=true;
		if(collision_bas)
		{
			partie.finSaut=true;
			partie.peutSauter=true;
			useGravity=false;
		}

	}
}

