package types;

import java.io.Serializable;

import javax.vecmath.Vector2d;

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
	public Vitesse minus()
	{
		return new Vitesse(-x,-y);
	}
	public Vitesse Copy()
	{
		return new Vitesse(x,y);
	}
}
