package images;

import java.awt.Image;
import java.util.ArrayList;

import deplacement.Mouvement_perso;
import loading.LoadMediaThread;
import monstre.Monstre;
import types.Entitie;
import types.TypeObject;

public class ImagesMonstre extends LoadMediaThread{

	ArrayList<Image> im_SP_attente= new ArrayList<Image>();
	ArrayList<Image> im_SP_marche= new ArrayList<Image>();
	ArrayList<Image> im_SP_saut= new ArrayList<Image>();
	
	public ImagesMonstre()
	{
	}
	
	@Override
	public void loadMedia()
	{
		if(mediaLoaded)
			return;
		
		for(int i=0; i<2; ++i)
			im_SP_attente.add(getIm("resources/monstres/spirel/deplacement.Attente/"+i+".gif",true));
		setPercentage((int) 200.0/8);

		for(int i=0; i<4; ++i)
			im_SP_marche.add(getIm("resources/monstres/spirel/deplacement.Marche/"+i+".gif",true));
		setPercentage((int) 600.0/8);
				
		for(int i=0; i<2; ++i)
			im_SP_saut.add(getIm("resources/monstres/spirel/deplacement.Attente/"+i+".gif",true));
		setPercentage(100);
		mediaLoaded=true;
		
	}
	/**
	 * Renvoie la bonne image pour un monstre donné
	 * 
	 * @param monstre, le monstre a afficher
	 * 
	 * @return l'image a afficher
	 */
	public ArrayList<Image> getImage(Entitie monstre)
	{
		ArrayList<Image> im = new ArrayList<Image>();
//				im.add(this.im_electrique_aura.get(fleche.anim));

		if (TypeObject.isTypeOf(monstre, TypeObject.SPIREL))
		{
			if(monstre.deplacement.IsDeplacement(Mouvement_perso.attente))
				im.add(im_SP_attente.get(monstre.anim));
			else if (monstre.deplacement.IsDeplacement(Mouvement_perso.marche))
				im.add(im_SP_marche.get(monstre.anim));
			else if(monstre.deplacement.IsDeplacement(Mouvement_perso.saut))
				im.add(im_SP_saut.get(monstre.anim));
		}
		return im;
	}

	@Override
	public void loadMedia(String media_categorie, String filename) {
		// TODO Auto-generated method stub
		
	}


}
