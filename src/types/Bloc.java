package types;

import java.awt.Point;
import java.awt.Polygon;

import javax.vecmath.Vector2d;

import collision.Collidable;
import deplacement.Deplace;
import deplacement.Mouvement;
import partie.AbstractModelPartie;

public class Bloc extends Collidable{
 private String img ="vide";
 private boolean bloquer = false;
 private boolean background=false;
 
 //private static Bloc blocVide =new Bloc("vide",0,0,false,false);
 
	public Bloc (){
		type=TypeObject.bloc;
		this.img="vide";
		this.xpos=-1;
		this.ypos=-1;
		localVit=new Vitesse(0,0);
		envirVit=new Vitesse(0,0);
		slowDownFactor=1;
		fixedWhenScreenMoves=false;
		this.bloquer=false;
		this.background=false;
	}
	public Bloc(String str, int x, int y , boolean bl, boolean back) {
		type=TypeObject.bloc;
		this.img=str;
		this.xpos=x;
		this.ypos=y;
		localVit=new Vitesse(0,0);
		envirVit=new Vitesse(0,0);
		slowDownFactor=1;
		fixedWhenScreenMoves=false;
		this.bloquer=bl;
		this.background=back;
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
		return (this.img=="vide");
	}*//*
	
	public String ToString()
	{
		return("Bloc de "+img+" en pos " + xpos +","+ypos+" bloquant: "+ bloquer +" background: "+ background);
	}*/
	
	//mutateur
	public void setImg(String str){
		this.img=str;
	}
	
	public void setPos(int x,int y){
	xpos=x;
	ypos=y;
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
	return(this.xpos);
	}

	public int getYpos(){
		return(this.ypos);
	}
	public boolean getBloquer(){
		return(this.bloquer);
	}
	public boolean getBack(){
		return(this.background);
	}
	
	
	public Hitbox getHitbox(Point INIT_RECT) {
		if(!bloquer)
			return null;
		
		Polygon p = new Polygon();
		int xposlocal = (xpos-INIT_RECT.x);
		int yposlocal = (ypos-INIT_RECT.y);
		p.addPoint(xposlocal, yposlocal);
		p.addPoint(xposlocal+99, yposlocal);
		p.addPoint(xposlocal+99, yposlocal+99);
		p.addPoint(xposlocal, yposlocal+99);
		return new Hitbox(p);
		
	}
	public Hitbox getHitbox(Point INIT_RECT, Mouvement mouv, int _anim) {
		return getHitbox(INIT_RECT);
	}
	public void handleWorldCollision(Vector2d normal, AbstractModelPartie partie,Deplace deplace) {
		//no collision handle: this bloc si currently static which means that the only objects that can collides with it are
		//mobile objects. We'll rather call the handleCollision of the mobile objects 
	}
	public void handleObjectCollision(AbstractModelPartie partie,Deplace deplace) {
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
	public void handleStuck(AbstractModelPartie partie, Deplace deplace) {
		//do nothing
	}
	@Override
	public void handleDeplacementSuccess(AbstractModelPartie partie,
			Deplace deplace) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void resetVarDeplace() {
		//do nothing
	}
	@Override
	public void destroy() {
		//do nothing
	}

}
