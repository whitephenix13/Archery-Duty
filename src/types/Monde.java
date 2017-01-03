package types;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import principal.InterfaceConstantes;

@SuppressWarnings("serial")
public class Monde implements InterfaceConstantes, Serializable{
public Bloc[][] niveau = new Bloc[ABS_MAX][ORD_MAX];

private Bloc blocVide =new Bloc("vide",0,0,false,false);
public int xStartMap=0;
public int yStartMap =0;
public int xEndMap=0;
public int yEndMap =0;

public int xStartPerso=0;
public int yStartPerso=0;

public List<StockageMonstre> listMonstreOriginal= new ArrayList<StockageMonstre>(); 

Image ciel;
Image terre;
Image sol;
Image vide;
Image perso;
Image start;
Image end;

Image pciel;
Image pterre;
Image psol;
Image pvide;
Image pperso;
Image pstart;
Image pend;

/*public String ToString()
{
	String s= new String();
	s+="Blocs non nuls :\n";
	for(int i=0; i<niveau.length; i++)
	{
		for(int j=0; j<niveau[0].length; j++)
		{
			if(!niveau[i][j].isEmpty())
			{
				s+=niveau[i][j].ToString()+ "\n";
			}
		}
	}
	s+="Start spawn: "+xStartMap +","+yStartMap +" End spawn : "+ xEndMap + ","+yEndMap+"\n";
	s+="Start perso: "+xStartPerso +","+yStartPerso+"\n";
	s+=listMonstreOriginal.toString();
	
	return(s);
}*/
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
//to load Pictures
public Monde(String s)
{
	chargerImages();
}
/**
 * initialiser le monde avec des blocs vides
 */	
private void initMonde () {
	for(int abs=0;abs<ABS_MAX;abs++)
	{
		for(int ord=0;ord<ORD_MAX;ord++)
		{
			blocVide.setPos(abs*100,ord*100);
			niveau[abs][ord]=blocVide;
		}
		
	}
}

/**
 * charge les images en memoire pour gagner du temps en acces
 */	
void chargerImages()
{
	 ciel=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/ciel.png"));
	 terre=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/terre.png"));
	 sol=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/sol.png"));
	 vide=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/vide.png"));
	 perso=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/perso.png"));
	 start=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/start.png"));
	 end=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/end.png"));
	 
	 pciel=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/ciel_p.png"));
	 pterre=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/terre_p.png"));
	 psol=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/sol_p.png"));
	 pvide=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/vide_p.png"));
	 pperso=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/perso_p.png"));
	 pstart=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/start_p.png"));
	 pend=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/end_p.png"));
}

/**
 * renvoie l'image correspondant au bloc 
 * 
 * @param bloc: le bloc a afficher
 * @param loupe: pour l'editeur, savoir si la loupe(dezoom) est activée ou non 
 * 
 * @return l'image a afficher
 */	
public Image getImage(Bloc bloc, boolean loupe)
{
	if(bloc.getImg().equals("vide"))
	{
		if(loupe)return(pvide);
		return(vide);
	}
	else if (bloc.getImg().equals("ciel"))
	{
		if(loupe)return(pciel);
		return(ciel);
	}
	else if (bloc.getImg().equals("sol"))
	{
		if(loupe)return(psol);
		return(sol);
	}
	else if (bloc.getImg().equals("terre"))
	{
		if(loupe)return(pterre);
		return(terre);
	}
	else if (bloc.getImg().equals("perso"))
	{
		if(loupe)return(pperso);
		return(perso);
	}
	else if (bloc.getImg().equals("start"))
	{
		if(loupe)return(pstart);
		return(start);
	}
	else if (bloc.getImg().equals("end"))
	{
		if(loupe)return(pend);
		return(end);
	}
	else
	{
		if(loupe)return(pvide);
		return(vide);
	}
}
}
