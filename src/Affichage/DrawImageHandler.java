package Affichage;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DrawImageHandler {
	
	public static int MONDE = 1;
	public static int MONSTRE = 2;
	public static int PERSO = 3;
	public static int FLECHE = 4;
	public static int TIRMONSTRE = 5;
	public static int EFFECT = 6;
	public static int EFFECT_FRONT = 7;//for fleche trou_noir
	public static int INTERFACE = 8;
	
	
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
	
	public void drawAll(Graphics g)
	{
		if(!ordered)
		{
			Collections.sort(listImage, imageComparator);
			ordered = true;
		}
		for(int i=0; i<listImage.size();++i)
		{
			listImage.get(i).draw(g);
		}
		listImage.clear();
	}
}
