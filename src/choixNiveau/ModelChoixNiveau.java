package choixNiveau;

import java.awt.Color;
import java.awt.Font;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JButton;

import menuPrincipal.AbstractModelPrincipal;
import principal.TypeApplication;

public class ModelChoixNiveau extends AbstractModelChoixNiveau{

	public void resetBouton(JButton bouton, String nomNiveau)
	{
		bouton.setText(nomNiveau);
		bouton.setForeground(Color.WHITE);
		bouton.setBackground(Color.BLACK);
		bouton.setFont(new Font("Courrier",Font.PLAIN,44));
	}
	public void resetBoutons()
	{
		for(JButton bouton:listNiveaux){
			bouton.setForeground(Color.WHITE);
			bouton.setBackground(Color.BLACK);
			bouton.setFont(new Font("Courrier",Font.PLAIN,44));
		}
	}
	/**
	 * Affiche la liste des niveaux
	 */
	public void getAllNiveaux()
	{
		//on reinitialise les variables
		listNomNiveaux.clear();
		listNiveaux.clear();
		//on recupère le nom des niveaux 
		try 
		{
			if(!TypeApplication.isJar)
			{
				Path url = Paths.get(ClassLoader.getSystemResource("resources/Levels/").toURI());
				listNomNiveaux= GetNiveaux.getDocInFolder(url.toString() );

			}
			else
			{
				listNomNiveaux=GetNiveaux.getDocInJar("resources/Levels/");
				for(int i=0; i<listNomNiveaux.size();++i)
					System.out.println("Niveau: " + listNomNiveaux.get(i));
			}

			updateListLevels=true;
			notifyObserver();
		} 
		catch (URISyntaxException e) {e.printStackTrace();}

		/*//on créer les boutons
		panelBoutons.setLayout(new GridLayout(listNomNiveaux.size(),1));
		//panelBoutons.setLayout(new FlowLayout());
		for(int i=0; i <listNomNiveaux.size(); i++ )
		{
			listNiveaux.add(new JButton());

			resetBouton(listNiveaux.get(i),listNomNiveaux.get(i));
			listNiveaux.get(i).setEnabled(true);
			listNiveaux.get(i).setVisible(true);

			panelBoutons.add(listNiveaux.get(i));
		}
		panelBoutonScroll = new JScrollPane(panelBoutons);*/
	}

	public static String getPath()
	{
		if(!TypeApplication.isJar)
		{
			String path="";
			try {
				path = Paths.get(ClassLoader.getSystemResource("resources/Levels/").toURI()).toString()+"\\";
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			return(path);
		}
		else
		{
			String path="/resources/Levels/";
			return(path);
		}
	}

	public String getNiveauSelectionne()
	{
		return(niveauSelectionne);
	}


	public List<JButton> getListBoutonNiveau()
	{
		return(listNiveaux);
	}

	public void selectLevel(JButton button) {
		//on reset les boutons 
		for (int i=0; i<listNiveaux.size();i++){resetBouton(listNiveaux.get(i),listNomNiveaux.get(i));}

		button.setBackground(Color.GRAY);
		niveauSelectionne=button.getText();
	}

	public void playLevel() {
		AbstractModelPrincipal.changeFrame=true;
		AbstractModelPrincipal.modeSuivant="Partie";
		AbstractModelPrincipal.changeMode=true;
		resetBoutons();
	}

}
