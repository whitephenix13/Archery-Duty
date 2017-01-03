package choixNiveau;

public class ControlerChoixNiveau extends AbstractControlerChoixNiveau{

		
	public ControlerChoixNiveau(AbstractModelChoixNiveau _choix)
	{
		choix=_choix;
	}

	public void controlPlayLevel() {
		if(!choix.niveauSelectionne.equals(""))
			choix.playLevel();
	}
}
