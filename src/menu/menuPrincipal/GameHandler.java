package menu.menuPrincipal;

import java.awt.Image;
import java.util.ArrayList;

import gameConfig.ObjectTypeHelper.ObjectType;
import images.ImagesContainer;
import images.ImagesContainer.ImageGroup;
import images.ImagesContainer.ImageInfo;
import images.ImagesContainer.ObjectSubType;

public interface GameHandler {
	public static enum GameModeType {QUIT,OPTION,EDITOR,CREDIT,LEVEL_SELECTION,GAME,MAIN_MENU,LOADER};
	public abstract GameModeType currentGameMode();
	public abstract void setGameMode(GameModeType newMode);
	public ImagesContainer getImageGroup(ImageGroup group);
	public abstract Image getImage(ImageGroup group,ObjectType typeObj, ImageInfo info1,ImageInfo info2);
	public abstract ArrayList<Image> getImages(ImageGroup group,ObjectType typeObj, ImageInfo info1,ImageInfo info2,int mouv_index);
	public abstract void updateSwing();//Mainly used to switch from loading to gamemode screen
	public abstract void forceRepaint();
	public abstract int getFps();
	
}
