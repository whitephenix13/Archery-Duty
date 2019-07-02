package menu.menuPrincipal;

public interface GameHandler {
	public enum GameModeType {QUIT,OPTION,EDITOR,CREDIT,LEVEL_SELECTION,GAME,MAIN_MENU,LOADER};
	public abstract GameModeType currentGameMode();
	public abstract void setGameMode(GameModeType newMode);
	public abstract void updateGraphics();//Mainly used to switch from loading to gamemode screen
	
}
