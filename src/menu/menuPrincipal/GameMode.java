package menu.menuPrincipal;

import Affichage.GameRenderer;
import Affichage.Drawable;

public interface GameMode {
	public abstract void doComputations(GameRenderer affich);//need affichage to check that the game has focus 
	public abstract void updateSwing();
	public abstract boolean isGameModeLoaded(); //usefull if the GameMode required a loading before starting 
	public abstract GameMode getLoaderGameMode();//Return the drawable used for the transition
}
