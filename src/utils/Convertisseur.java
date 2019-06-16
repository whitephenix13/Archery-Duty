package utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import gameConfig.InterfaceConstantes;
import menu.choixNiveau.GetNiveaux;
import partie.bloc.Monde;
import serialize.Serialize;

public class Convertisseur implements InterfaceConstantes{
	public Convertisseur()
	{
	}
	
	/**
	 * Convertir mondeAncien vers monde  
	 * 
	 */	
	public void convertir()
	{
		String nomFichier="";
		List<String> listNiveaux = getListNiveau();
		//on part d'un niveau transformé(MondeAncien) qu'on veut copier(tel quel) en Monde 
		Monde monde = new Monde();
		
		//boucle for sur l'ensemble des fichiers 
		for(int i=0; i<listNiveaux.size();i++)
		{
			nomFichier=listNiveaux.get(i);
			monde=charger(nomFichier);
			Serialize.sauver("_"+nomFichier+"_",monde);

		}
		
	}
	
	/**
	 * Récupère la liste des niveaux par une recherche fichier
	 * 
	 *  @return la liste des noms des niveaux
	 * 
	 */	
	private List<String> getListNiveau()
	{
		List<String> listNomNiveaux = new ArrayList<String>();
		
		if(!TypeApplication.isJar )
		{
			Path url = null;
			try {url = Paths.get(ClassLoader.getSystemResource("resources/levels/").toURI());
			listNomNiveaux= GetNiveaux.getDocInFolder(url.toString() );} 
			catch (URISyntaxException e) {e.printStackTrace();}
		}
		else
		{
		}
		
		return(listNomNiveaux);
	}
	
	/**
	 * Charge un MondeAncien(ancien format du fichier)
	 * 
	 * @param nomFichier, nom de l'ancien monde
	 * 
	 * @return le monde chargé
	 */	
	private Monde charger(String nomFichier){
		ObjectInputStream ois ;
		Monde monde2= new Monde();
		File file = new File ("src/resources/levels/"+nomFichier+"");
		try {
			ois=new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
			monde2=(Monde)ois.readObject();
			ois.close();

			
		} catch (IOException | ClassNotFoundException e1 ) {				
		e1.printStackTrace();	
		}
		return(monde2);

	}

}
