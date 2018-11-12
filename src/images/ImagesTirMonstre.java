package images;

import java.awt.Image;
import java.util.ArrayList;

import deplacement_tir.Mouvement_tir;
import deplacement_tir.Mouvement_tir.TypeTir;
import loading.LoadMediaThread;
import loading.OnLoadingCallback;
import monstre.TirMonstre;
import types.Projectile;
import types.TypeObject;

public class ImagesTirMonstre extends LoadMediaThread{

	ArrayList<Image> im_SP_tir= new ArrayList<Image>();
	
	public ImagesTirMonstre()
	{
	}
	
	@Override
	public void loadMedia()
	{
		if(mediaLoaded)
			return;
		
		for(int i=0; i<3; ++i)
			im_SP_tir.add(getIm("resources/TirMonstre/monstre.TirSpirel/"+i+".png",true));
		setPercentage(100);
		mediaLoaded=true;
	}
	public ArrayList<Image> getImage(Projectile tir)
	{
		ArrayList<Image> im = new ArrayList<Image>();
		if(TypeObject.isTypeOf(tir, TypeObject.TIR_SPIREL))
		{
			if(tir.deplacement.IsDeplacement(TypeTir.T_normal))
				im.add(im_SP_tir.get(tir.anim));
		}

		return im;
	}

	@Override
	public void loadMedia(String media_categorie, String filename) {
		// TODO Auto-generated method stub
		
	}

}
