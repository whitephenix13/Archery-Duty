package types;

import java.awt.Point;
import java.awt.Polygon;

import javax.vecmath.Vector2d;

import collision.Collidable;
import deplacement.Deplace;
import deplacement.Mouvement;
import effects.Effect;
import partie.AbstractModelPartie;

public class Bloc extends Collidable{

	public static String VIDE = "vide";
	public static String CIEL = "ciel";
	public static String DELETE = "delete";
	public static String END = "end";
	public static String LOUPE = "loupe";
	public static String PERSO = "perso";
	public static String SOL = "sol";
	public static String SOURIS = "souris";
	public static String SPIREL = "spirel";
	public static String START = "start";
	public static String TERRE = "terre";


	private String img =VIDE;
	private boolean bloquer = false;
	private boolean background=false;

	//private static Bloc blocVide =new Bloc(VIDE,0,0,false,false);

	public Bloc (){
		this(VIDE,-1,-1,false,false);
	}
	public Bloc(String str, int x, int y , boolean bl, boolean back) {
		super.init();
		
		this.img=str;
		this.xpos_sync(x);
		this.ypos_sync(y);
		localVit= new Vitesse(0,0);
		fixedWhenScreenMoves=false;
		this.bloquer=bl;
		this.background=back;
		this.setCollideWithAll();
	}
	/*public static Bloc[][] InitBlocMatrix(int xsize, int ysize)
	{
		Bloc[][] res = new Bloc[xsize][ysize];
		for(int abs=0;abs<xsize;abs++)
		{
			for(int ord=0;ord<ysize;ord++)
			{
				blocVide.setPos(abs*100,ord*100);
				res[abs][ord]=blocVide;
			}

		}
		return(res);
	}
	 *//*
	public boolean isEmpty()
	{
		return (this.img==VIDE);
	}*//*

	public String ToString()
	{
		return("Bloc de "+img+" en pos " + xpos +","+ypos+" bloquant: "+ bloquer +" background: "+ background);
	}*/
	public boolean isVide()
	{
		if(img.equals(VIDE))
			return true;
		else
			return false;
	}
	
	//mutateur
	public void setImg(String str){
		this.img=str;
	}

	public void setPos(int x,int y){
		xpos_sync(x);
		ypos_sync(y);
	}

	public void setBloquer(boolean bloque){
		this.bloquer=bloque;
	}
	public void setBackground(boolean back){
		this.background=back;
	}


	//accesseur
	public String getImg(){
		return(this.img);
	}

	public int getXpos(){
		return(xpos());
	}

	public int getYpos(){
		return(ypos());
	}
	public boolean getBloquer(){
		return(this.bloquer);
	}
	public boolean getBack(){
		return(this.background);
	}

	public Vector2d getNormCollision()
	{
		return null;
	}
	
	@Override
	public int getMaxBoundingSquare()
	{
		return deplacement.getMaxBoundingSquare(this);
	}
	
	public Hitbox getHitbox(Point INIT_RECT,Point screenDisp) {
		if(!bloquer)
			return null;

		Polygon p = new Polygon();
		int xposlocal = (xpos()-INIT_RECT.x);
		int yposlocal = (ypos()-INIT_RECT.y);
		p.addPoint(xposlocal, yposlocal);
		p.addPoint(xposlocal+99, yposlocal);
		p.addPoint(xposlocal+99, yposlocal+99);
		p.addPoint(xposlocal, yposlocal+99);
		return new Hitbox(p);

	}
	public Hitbox getHitbox(Point INIT_RECT,Point screenDisp, Mouvement mouv, int _anim) {
		return getHitbox(INIT_RECT,screenDisp);
	}
	@Override
	public void handleWorldCollision(Vector2d normal, AbstractModelPartie partie,Collidable collidedObject,boolean stuck) {
		//no collision handle: this bloc si currently static which means that the only objects that can collides with it are
		//mobile objects. We'll rather call the handleCollision of the mobile objects 
		//REMOVEfor(int i= currentEffects.size()-1;i>=0;i--)
			//currentEffects.get(i).onAffectedObjectCollide(partie,this,normal);
	}
	@Override
	public void handleObjectCollision(AbstractModelPartie partie,Collidable collider,Vector2d normal) {
		try {
			throw new Exception("Calling handleObjectCollision for a bloc is forbidden");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void memorizeCurrentValue() {
		//do nothing
	}
	@Override
	public boolean[] deplace(AbstractModelPartie partie, Deplace deplace) {
		//Do nothing
		boolean[] res = {false,false};
		return res;
	}
	@Override
	public void applyFriction(double minLocalspeed, double minEnvirSpeed) {
		//do nothing
	}
	@Override
	public void resetVarBeforeCollision() {
		//do nothing
	}
	@Override
	public void handleStuck(AbstractModelPartie partie) {
		//do nothing
	}
	@Override
	public void handleDeplacementSuccess(AbstractModelPartie partie) {
		// TODO Auto-generated method stub

	}
	@Override
	public void resetVarDeplace(boolean speedUpdated) {
		//do nothing
	}
	@Override
	public void onDestroy(AbstractModelPartie partie) {
		//do nothing
	}
	@Override
	public Vitesse getGlobalVit(AbstractModelPartie partie) {
		return null;
	}

}
