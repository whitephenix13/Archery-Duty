package effects;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;

import fleches.Fleche;

public class ImagesEffect {

	ArrayList<Image> im_vent_aura= new ArrayList<Image>();

	public ImagesEffect()
	{
		chargerImages();
	}
	
	public void chargerImages()
	{
		for(int i=0; i<4; ++i){
			im_vent_aura.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(
					"resources/fleches/effects/vent/"+i+".png")));
			
		}
	}
	
	public ArrayList<Image> getImage(Effect effect)
	{
		ArrayList<Image> im = new ArrayList<Image>();
		if(!effect.name.equals(""))
		{
			if (effect.name.equals(Fleche.SPIRITUELLE.VENT))
				im.add(im_vent_aura.get(effect.anim));
		}
		return im;
		
	}
}