package partie.bloc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import editeur.StockageMonstre;
import gameConfig.InterfaceConstantes;
import partie.bloc.Bloc.TypeBloc;

@SuppressWarnings("serial")
public class Monde implements InterfaceConstantes, Serializable{
public String name ="";
public Bloc[][] niveau;

public int xStartMap=0;
public int yStartMap =0;
public int xEndMap=0;
public int yEndMap =0;

public int xStartPerso=0;
public int yStartPerso=0;

public List<StockageMonstre> listMonstreOriginal= new ArrayList<StockageMonstre>(); 

public Monde()
{
	xStartMap=0;
	yStartMap =0;
	xEndMap=0;
	yEndMap =0;

	xStartPerso=0;
	yStartPerso=0;
}


}
