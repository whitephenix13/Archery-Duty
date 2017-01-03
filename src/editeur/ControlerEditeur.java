package editeur;

import javax.swing.JOptionPane;

import serialize.Serialize;

public class ControlerEditeur extends AbstractControlerEditeur{

	public ControlerEditeur(AbstractModelEditeur _edit) {
		super(_edit);
	}

	public void controlDraw(int xpos, int ypos) 
	{
		if(edit.monstreActive)
			edit.drawMonster(xpos, ypos);
		
		else if(edit.perso || edit.start || edit.end)
			edit.drawSpecial(xpos, ypos);
		
		else if(edit.texture.equals("Delete"))
			edit.deleteMonster(xpos, ypos);
		
		else if (!edit.texture.isEmpty())
			edit.drawTerrain(xpos, ypos);
			
	}

	public void setTexture(String texture)
	{
		edit.texture=texture;
	}
	
	public void controlSauvegarde(String nom) {
		
		if(nom.isEmpty() || nom.equals(""))
		{
			edit.showMessageDialog=true;
			edit.textMessageDialog[0]="Echec de la sauvegarde: nom incorrect ";
			edit.textMessageDialog[1]="Erreur Saisie";
			edit.typeMessageDialog=JOptionPane.ERROR_MESSAGE;
			edit.notifyObserver();

			return;
		}
		String err =edit.sauver(nom);
		
		if (!err.isEmpty()) 
		{
			edit.showMessageDialog=true;
			edit.textMessageDialog[0]="Une erreur s'est produite lors de la sauvegarde";
			edit.textMessageDialog[1]="Erreur sauvegarde";
			edit.typeMessageDialog=JOptionPane.ERROR_MESSAGE;
			edit.notifyObserver();

		}
		else
		{
			edit.showMessageDialog=true;
			edit.textMessageDialog[0]="La sauvegarde a ete effectuee correctement au nom de :  " + nom;
			edit.textMessageDialog[1]="Sauvegarde reussite";
			edit.typeMessageDialog=JOptionPane.INFORMATION_MESSAGE;
			edit.notifyObserver();

		}
	
	}
	
	public void controlChargement(String nom) {
		if(nom.isEmpty())
		{
			edit.showMessageDialog=true;
			edit.textMessageDialog[0]="Echec du chargement: nom vide ";
			edit.textMessageDialog[1]="Erreur Saisie";
			edit.typeMessageDialog=JOptionPane.ERROR_MESSAGE;
			edit.notifyObserver();
			return;
		}
		
		edit.charger(nom);
		
		if(!Serialize.erreurMsgChargement.equals(""))
		{
			edit.showMessageDialog=true;
			edit.textMessageDialog[0]=Serialize.erreurMsgChargement;
			edit.textMessageDialog[1]="Echec chargement";
			edit.typeMessageDialog=JOptionPane.ERROR_MESSAGE;
			edit.notifyObserver();
		}
		else
		{
		edit.showMessageDialog=true;
		edit.textMessageDialog[0]="Le niveau  "+nom +" a ete correctement chargé";
		edit.textMessageDialog[1]="Chargement reussi";
		edit.typeMessageDialog=JOptionPane.INFORMATION_MESSAGE;
		edit.notifyObserver();
		}
	}

	
}
