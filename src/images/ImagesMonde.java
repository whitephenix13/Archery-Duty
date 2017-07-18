package images;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;

import loading.LoadMediaThread;
import loading.OnLoadingCallback;
import types.Bloc;

public class ImagesMonde extends LoadMediaThread implements OnLoadingCallback{
	Image ciel;
	Image terre;
	Image sol;
	Image vide;
	Image perso;
	Image start;
	Image end;

	Image pciel;
	Image pterre;
	Image psol;
	Image pvide;
	Image pperso;
	Image pstart;
	Image pend;
	public ImagesMonde()
	{
	}
	
	@Override
	public void loadMedia()
	{
		if(mediaLoaded)
			return;
		
		 ciel=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/ciel.png"));		 
		 terre=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/terre.png"));
		 setPercentage((int)(200.0/14));
		 
		 sol=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/sol.png"));
		 vide=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/vide.png"));
		 setPercentage((int)(400.0/14));

		 perso=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/perso.png"));
		 start=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/start.png"));
		 setPercentage((int)(600.0/14));

		 end=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/end.png"));
		 
		 pciel=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/ciel_p.png"));
		 setPercentage((int)(800.0/14));

		 pterre=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/terre_p.png"));
		 psol=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/sol_p.png"));
		 setPercentage((int)(1000.0/14));

		 pvide=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/vide_p.png"));
		 pperso=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/perso_p.png"));
		 setPercentage((int)(1200.0/14));

		 pstart=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/start_p.png"));
		 pend=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/end_p.png"));
		 setPercentage(100);
		 mediaLoaded=true;
	}

	/**
	 * renvoie l'image correspondant au bloc 
	 * 
	 * @param bloc: le bloc a afficher
	 * @param loupe: pour l'editeur, savoir si la loupe(dezoom) est activée ou non 
	 * 
	 * @return l'image a afficher
	 */	
	public Image getImages(Bloc bloc, boolean loupe)
	{
		if(bloc.getImg().equals("vide"))
		{
			if(loupe)return(pvide);
			return(vide);
		}
		else if (bloc.getImg().equals("ciel"))
		{
			if(loupe)return(pciel);
			return(ciel);
		}
		else if (bloc.getImg().equals("sol"))
		{
			if(loupe)return(psol);
			return(sol);
		}
		else if (bloc.getImg().equals("terre"))
		{
			if(loupe)return(pterre);
			return(terre);
		}
		else if (bloc.getImg().equals("perso"))
		{
			if(loupe)return(pperso);
			return(perso);
		}
		else if (bloc.getImg().equals("start"))
		{
			if(loupe)return(pstart);
			return(start);
		}
		else if (bloc.getImg().equals("end"))
		{
			if(loupe)return(pend);
			return(end);
		}
		else
		{
			if(loupe)return(pvide);
			return(vide);
		}
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadMedia(String media_categorie, String filename) {
		// TODO Auto-generated method stub
		
	}
}
