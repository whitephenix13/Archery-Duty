package types;

import java.io.Serializable;

import javax.vecmath.Vector2d;

import principal.InterfaceConstantes;

@SuppressWarnings("serial")
public class Vitesse implements Serializable{
	public double x=0;
	public double y=0;
	public Vitesse() {};
	public Vitesse(double xx, double yy){x=xx; y=yy;}
	public Vector2d vect2d()
	{
		return new Vector2d(x,y);
	}
	public Vitesse add(Vitesse vit2)
	{
		return new Vitesse(x+vit2.x,y+vit2.y);
	}
	public Vitesse negate()
	{
		return new Vitesse(-x,-y);
	}
	public double norm2()
	{
		return(Math.pow(x, 2)+Math.pow(y, 2));
	}
	public double norm()
	{
		return(Math.sqrt(norm2()));
	}
	public Vitesse Copy()
	{
		return new Vitesse(x,y);
	}
	//can use useGravity of Heros to detect if it is on ground or not 
	public static Vitesse applyFriction(Vitesse vit,boolean onGround,double minEnvirSpeed)
	{
		boolean negx = vit.x<0;
		boolean negy = vit.y<0;
		double frict = (!onGround?InterfaceConstantes.AIRFRICTION:InterfaceConstantes.FRICTION);
		double newVitX= vit.x - (vit.x* frict);
		double newVitY= vit.y - (vit.y* frict);
		
		double resX = 0;
		double resY = 0;
		if( (!negx && newVitX<minEnvirSpeed) || (negx && newVitX>-1*minEnvirSpeed) )
			resX=minEnvirSpeed;
		else
			resX=newVitX;
		if( (!negy && newVitY<minEnvirSpeed) || (negy && newVitY>-1*minEnvirSpeed) )
			resY=minEnvirSpeed;
		else
			resY=newVitY;
		return new Vitesse(resX,resY);
	}
	
	@Override
	public String toString()
	{
		String s= "("+this.x+", "+this.y+")";
		return s;
	}
	
	/**
	 * <pre>
	 *       ^ 
	 *    ___|____  speed goes in object : project it to the ground 
	 *   |  /     |
	 *     v
	 * 
	 *    ^  ^
	 *    _\_|____  speed goes out of object or normal unknown: no operation to do 
	 *   |        |
	 *</pre>
	 * @param vit
	 * @param normal
	 * @return the speed with "penetration component" removed 
	 */
	public static Vitesse removePenetrationComponent(Vitesse vit, Vector2d normal)
	{
		if(normal==null)
			return vit;
		
		double dotNormal = vit.vect2d().dot(normal);
		if(dotNormal>=0)
			return vit;
		double coef1= dotNormal/normal.lengthSquared();
		vit = new Vitesse(vit.x-coef1*normal.x,vit.y-coef1*normal.y);
		return vit;

	}
	
}
