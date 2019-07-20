package menu.choixNiveau;

import java.awt.Color;
import java.awt.Font;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import ActiveJComponent.ActiveJButton;
import Affichage.GameRenderer;
import Affichage.Drawable;
import menu.menuPrincipal.AbstractModelPrincipal;
import menu.menuPrincipal.GameHandler;
import menu.menuPrincipal.GameMode;
import menu.menuPrincipal.GameHandler.GameModeType;
import utils.TypeApplication;

public class ModelChoixNiveau extends AbstractModelChoixNiveau{

	public ModelChoixNiveau(GameHandler gameHandler)
	{
		this.gameHandler=gameHandler;
	}
	
	public void resetBouton(ActiveJButton bouton, String nomNiveau)
	{
		if(nomNiveau!=null)
			bouton.setText(nomNiveau);
		bouton.setForeground(Color.WHITE);
		bouton.setBackground(Color.BLACK);
		bouton.setFont(new Font("Courrier",Font.PLAIN,44));
	}
	public void resetBoutons()
	{
		for(ActiveJButton bouton:listNiveaux){
			resetBouton(bouton,null);
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
				Path url = Paths.get(ClassLoader.getSystemResource("resources/levels/").toURI());
				listNomNiveaux= GetNiveaux.getDocInFolder(url.toString() );

			}
			else
			{
				listNomNiveaux=GetNiveaux.getDocInJar("resources/levels/");
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
				path = Paths.get(ClassLoader.getSystemResource("resources/levels/").toURI()).toString()+"\\";
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			return(path);
		}
		else
		{
			String path="/resources/levels/";
			return(path);
		}
	}

	public String getNiveauSelectionne()
	{
		return(niveauSelectionne);
	}


	public List<ActiveJButton> getListBoutonNiveau()
	{
		return(listNiveaux);
	}

	public void selectLevel(ActiveJButton button) {
		//on reset les boutons 
		for (int i=0; i<listNiveaux.size();i++){resetBouton(listNiveaux.get(i),listNomNiveaux.get(i));}

		button.setBackground(Color.GRAY);
		niveauSelectionne=button.getText();
	}

	public void playLevel() {
		//REMOVE AbstractModelPrincipal.changeFrame=true;//REMOVE
		//REMOVEAbstractModelPrincipal.modeSuivant="Partie";
		//REMOVEAbstractModelPrincipal.changeMode=true;
		resetBoutons();
		gameHandler.setGameMode(GameModeType.GAME);
	}
	
	public void doComputations(GameRenderer affich){
		//As this mode is controlled by listeners, the computationDone is set to false when a listener is triggered. This function is then left empty
	}
	public void updateSwing(){
		this.notifyMainObserver();
	}
	public boolean isComputationDone(){
		return computationDone;
	}
	@Override
	public boolean isGameModeLoaded()
	{
		//loading not required 
		return true;
	}
	@Override
	public GameMode getLoaderGameMode(){
		//loading not required 
		return null;
	}

}
