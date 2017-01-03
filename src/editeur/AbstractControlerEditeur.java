package editeur;

public abstract class AbstractControlerEditeur {
	
	public AbstractModelEditeur edit;
	public AbstractControlerEditeur(AbstractModelEditeur _edit)
	{
		edit=_edit;
	}
	public abstract void controlDraw(int xpos, int ypos);
	public abstract void controlSauvegarde(String nom);
	public abstract void controlChargement(String nom);

}
