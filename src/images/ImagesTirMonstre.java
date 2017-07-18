package images;

import java.awt.Image;
import java.util.ArrayList;

import deplacement_tir.Mouvement_tir;
import loading.LoadMediaThread;
import loading.OnLoadingCallback;
import monstre.TirMonstre;
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
			im_SP_tir.add(getIm("resources/TirMonstre/monstre.TirSpirel/"+i+".gif",true));
		setPercentage(100);
		mediaLoaded=true;
	}
	public ArrayList<Image> getImage(TirMonstre tir)
	{
		ArrayList<Image> im = new ArrayList<Image>();
		if(tir.type.equals(TypeObject.tir_spirel))
		{
			if(tir.deplacement.IsDeplacement(Mouvement_tir.tir_normal))
				im.add(im_SP_tir.get(tir.anim));
		}

		return im;
	}

	@Override
	public void loadMedia(String media_categorie, String filename) {
		// TODO Auto-generated method stub
		
	}

}
