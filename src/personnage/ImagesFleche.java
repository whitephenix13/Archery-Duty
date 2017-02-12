package personnage;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;

public class ImagesFleche {

	ArrayList<Image> im_fleche= new ArrayList<Image>();
	ArrayList<Image> im_slow_aura= new ArrayList<Image>();
	public ImagesFleche()
	{
		chargerImages();
	}
	
	public void chargerImages()
	{
		for(int i=0; i<4; ++i){
			im_fleche.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/fleches/"+i+".png")));
			im_slow_aura.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(
					"resources/fleches/auras/slow/"+i+".png")));
		}
	}
	
	public ArrayList<Image> getImage(Fleche fleche)
	{
		ArrayList<Image> im = new ArrayList<Image>();
		if(!fleche.aura.equals("") && !fleche.encochee)
		{
			if(fleche.aura.equals(Fleche.SLOW_AURA))
				im.add(im_slow_aura.get(fleche.anim));
		}
		im.add(im_fleche.get(fleche.anim));

		return im;
		
	}
}
