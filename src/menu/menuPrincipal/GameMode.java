package menu.menuPrincipal;

import Affichage.Affichage;
import Affichage.Drawable;

public interface GameMode {
	public abstract void doComputations(Affichage affich);//need affichage to check that the game has focus 
	public abstract void updateGraphics();
	public abstract boolean isComputationDone();//both from do computations AND listeners
	public abstract boolean isGameModeLoaded(); //usefull if the GameMode required a loading before starting 
	public abstract GameMode getLoaderGameMode();//Return the drawable used for the transition
}
