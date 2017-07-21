package types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import principal.InterfaceConstantes;

@SuppressWarnings("serial")
public class Monde implements InterfaceConstantes, Serializable{
public String name ="";
public Bloc[][] niveau;

private Bloc blocVide =new Bloc(Bloc.VIDE,0,0,false,false);
public int xStartMap=0;
public int yStartMap =0;
public int xEndMap=0;
public int yEndMap =0;

public int xStartPerso=0;
public int yStartPerso=0;

public List<StockageMonstre> listMonstreOriginal= new ArrayList<StockageMonstre>(); 

public Monde()
{
	initMonde();
	xStartMap=0;
	yStartMap =0;
	xEndMap=0;
	yEndMap =0;

	xStartPerso=0;
	yStartPerso=0;
}

/**
 * initialiser le monde avec des blocs vides
 */	
public void initMonde () {
	if(niveau == null)
		return;
	for(int abs=0;abs<niveau.length;abs++)
	{
		for(int ord=0;ord<niveau[abs].length;ord++)
		{
			blocVide.setPos(abs*100,ord*100);
			niveau[abs][ord]=blocVide;
		}
		
	}
}


}
