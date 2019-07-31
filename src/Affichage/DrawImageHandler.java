package Affichage;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import gameConfig.InterfaceConstantes;
import menu.menuPrincipal.ModelPrincipal;

public class DrawImageHandler {
	
	public final static int BACKGROUND = 0;
	public final static int MONDE = 1;
	public final static int MONSTRE = 2;
	public final static int PERSO = 3;
	public final static int FLECHE = 4;
	public final static int TIRMONSTRE = 5;
	public final static int EFFECT = 6;
	public final static int EFFECT_FRONT = 7;//for fleche trou_noir
	public final static int INTERFACE = 8;
	
	private static String layerToString(final int layout)
	{
		switch(layout){
			case BACKGROUND: return "BACKGROUND";
			case MONDE: return "MONDE";
			case MONSTRE: return "MONSTRE";
			case PERSO: return "PERSO";
			case FLECHE: return "FLECHE";
			case TIRMONSTRE: return "TIRMONSTRE";
			case EFFECT: return "EFFECT";
			case EFFECT_FRONT: return "EFFECT_FRONT";
			case INTERFACE: return "INTERFACE";
			default : return "Unknown "+layout;
		}
		
	}
	
	
	boolean ordered = false;
	private ArrayList<DrawImageItem> listImage = new ArrayList<DrawImageItem>();

	public DrawImageHandler(){}

	public void addImage(DrawImageItem item){listImage.add(item); ordered=false;}

	Comparator<DrawImageItem> imageComparator = new Comparator<DrawImageItem>(){
		@Override
		public int compare(final DrawImageItem o1, final DrawImageItem o2){
			// let your comparator look up your car's color in the custom order
			return Integer.valueOf(o1.layerIndex)
					.compareTo
					(Integer.valueOf(o2.layerIndex));
		}
	};
	public void clearImages()
	{
		listImage.clear();
	}
	public void sortImages()
	{
		if(!ordered)
		{
			Collections.sort(listImage, imageComparator);
			ordered = true;
		}
	}
	public void drawAll(Graphics g)
	{
		//ModelPrincipal.debugTimeAffichage.print();
		//ModelPrincipal.debugTimeAffichage.init(InterfaceConstantes.DEBUG_TIME_AFFICHAGE_PRINT_MODE,-1);
		ModelPrincipal.debugTimeAffichage.startElapsedForVerbose();
		for(DrawImageItem imageItem : listImage)
		{
			//bloc behind hero =78; heros = 140 ; 
			imageItem.draw(g);
			ModelPrincipal.debugTimeAffichage.elapsed(imageItem.item.name()+" "+layerToString(imageItem.layerIndex));
		}
		
	}
}
