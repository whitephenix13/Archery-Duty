package images;

import java.awt.Image;
import java.awt.Toolkit;

import loading.LoaderItem;
import partie.bloc.Bloc;
import partie.bloc.Bloc.TypeBloc;

public class ImagesMonde extends LoaderItem{
	public static String path ="resources/editeur/";
	
	Image ciel;
	Image terre;
	Image sol;
	Image vide;
	Image mainHeros;
	Image start;
	Image end;

	Image pciel;
	Image pterre;
	Image psol;
	Image pvide;
	Image pmainHeros;
	Image pstart;
	Image pend;
	public ImagesMonde()
	{
	}
	
	@Override
	public void run()
	{
		if(alreadyLoaded)
			return;
		
		 ciel=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(path+"ciel.png"));		 
		 terre=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(path+"terre.png"));
		 percentage = (int)(2.0*100/14);
		 
		 sol=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(path+"sol.png"));
		 vide=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(path+"vide.png"));
		 percentage = (int)(4.0*100/14);

		 mainHeros=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(path+"heros.png"));
		 start=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(path+"start.png"));
		 percentage = (int)(6.0*100/14);

		 end=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(path+"end.png"));
		 
		 pciel=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(path+"ciel_p.png"));
		 percentage = (int)(8.0*100/14);

		 pterre=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(path+"terre_p.png"));
		 psol=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(path+"sol_p.png"));
		 percentage = (int)(10.0*100/14);

		 pvide=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(path+"vide_p.png"));
		 pmainHeros=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(path+"heros_p.png"));
		 percentage = (int)(12.0*100/14);

		 pstart=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(path+"start_p.png"));
		 pend=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(path+"end_p.png"));
		 percentage = 100;
		 alreadyLoaded=true;
	}

	/**
	 * renvoie l'image correspondant au bloc 
	 * 
	 * @param bloc: le bloc a afficher
	 * @param loupe: pour l'editeur, savoir si la loupe(dezoom) est activée ou non 
	 * 
	 * @return l'image a afficher
	 */	
	public Image getImages(TypeBloc type, boolean loupe)
	{
		if(type.equals(TypeBloc.VIDE))
		{
			if(loupe)return(pvide);
			return(vide);
		}
		else if (type.equals(TypeBloc.CIEL))
		{
			if(loupe)return(pciel);
			return(ciel);
		}
		else if (type.equals(TypeBloc.SOL))
		{
			if(loupe)return(psol);
			return(sol);
		}
		else if (type.equals(TypeBloc.TERRE))
		{
			if(loupe)return(pterre);
			return(terre);
		}
		else if (type.equals(TypeBloc.PERSO))
		{
			if(loupe)return(pmainHeros);
			return(mainHeros);
		}
		else if (type.equals(TypeBloc.START))
		{
			if(loupe)return(pstart);
			return(start);
		}
		else if (type.equals(TypeBloc.END))
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
}
