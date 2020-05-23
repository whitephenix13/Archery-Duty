package utils;

import java.awt.Point;

import javax.vecmath.Vector2d;

public class PointHelper {
	public static Point VecToPoint(Vector2d vec)
	{
		return new Point((int)vec.x,(int)vec.y);
	}
	
	public static Point RoundVecToPoint(Vector2d vec)
	{
		return new Point((int)Math.round(vec.x),(int)Math.round(vec.y));
	}
	public static Vector2d PointToVect(Point p)
	{
		return new Vector2d(p.x,p.y);
	}
}
