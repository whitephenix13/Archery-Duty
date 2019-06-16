package editeur;

import java.awt.Point;
import java.io.Serializable;

import partie.bloc.Bloc.TypeBloc;

//utilisé dans l'éditeur pour stocker un monstre. On stocke le nombre minima d'information
@SuppressWarnings("serial")
public class StockageMonstre implements Serializable{
	
	public TypeBloc type;
	public Point pos = new Point();
	public boolean immobile =false;
	/*public StockageMonstre()
	{
		nom="";
		pos=new Point();
		immobile=false;
	}*/
	public StockageMonstre(TypeBloc _type, Point _pos, boolean _immobile)
	{
		type=_type;
		pos=_pos;
		immobile=_immobile;
	}
	/*public String toString()
	{
		return("nom "+ nom + " pos " + pos.x +","+pos.y+" immobile: "+immobile+"\n");
	}*/
}
