package types;

import java.awt.Point;
import java.io.Serializable;

//utilisé dans l'éditeur pour stocker un monstre. On stocke le nombre minima d'information
@SuppressWarnings("serial")
public class StockageMonstre implements Serializable{
	
	public String nom="";
	public Point pos = new Point();
	public boolean immobile =false;
	/*public StockageMonstre()
	{
		nom="";
		pos=new Point();
		immobile=false;
	}*/
	public StockageMonstre(String _nom, Point _pos, boolean _immobile)
	{
		nom=_nom;
		pos=_pos;
		immobile=_immobile;
	}
	/*public String toString()
	{
		return("nom "+ nom + " pos " + pos.x +","+pos.y+" immobile: "+immobile+"\n");
	}*/
}
