package images;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;

import gameConfig.ObjectTypeHelper.ObjectType;
import partie.bloc.Bloc.BlocImModifier;
import partie.bloc.Bloc.TypeBloc;

public class ImagesMonde extends ImagesContainer{
	public static String path ="resources/editeur/";
	
	//image editeur 
	Image delete;
	Image spirel;
	
	Image pciel;
	Image pterre;
	Image psol;
	Image pvide;
	Image pmainHeros;
	Image pstart;
	Image pend;
	Image ploupe;
	Image pdelete;
	Image psouris;
	Image pspirel;
	
	Image start;
	Image end;
	Image vide;
	
	Image ciel;
	Image terre;
	Image sol;
	Image mainHeros;
	

	
	public ImagesMonde()
	{
		super("Image monde");
	}
	
	@Override
	public void run()
	{
		percentage=0;
		if(alreadyLoaded){
			percentage=100;
			return;
		}
		
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
		 
		 delete=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(path+"delete.png"));
		 spirel=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(path+"spirel.png"));
		 
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
		 
		 ploupe=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(path+"loupe_p.png"));
		 pdelete=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(path+"delete_p.png"));
		 psouris=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(path+"souris_p.png"));
		 pspirel=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(path+"spirel_p.png"));
		 
		 percentage = 100;
		 alreadyLoaded=true;
	}

	/***
	 * objType: null
	 * info1 : {@link TypeBloc}
	 * info2 : {@link BlocImModifier}
	 */
	@Override
	public Image getImage(ObjectType objType, ImageInfo info1,ImageInfo info2)
	{
		boolean loupe = info2!=null && info2.equals(BlocImModifier.LOUPE);
		if (info1.equals(TypeBloc.CIEL))
		{
			if(loupe)return(pciel);
			return(ciel);
		}
		else if (info1.equals(TypeBloc.SOL))
		{
			if(loupe)return(psol);
			return(sol);
		}
		else if (info1.equals(TypeBloc.TERRE))
		{
			if(loupe)return(pterre);
			return(terre);
		}
		else if (info1.equals(TypeBloc.PERSO))
		{
			if(loupe)return(pmainHeros);
			return(mainHeros);
		}
		else if (info1.equals(TypeBloc.START))
		{
			if(loupe)return(pstart);
			return(start);
		}
		else if (info1.equals(TypeBloc.END))
		{
			if(loupe)return(pend);
			return(end);
		}
		else if (info1.equals(TypeBloc.LOUPE))
		{
			if(loupe)return(ploupe);
			return(vide);
		}
		else if (info1.equals(TypeBloc.DELETE))
		{
			if(loupe)return(pdelete);
			return(delete);
		}
		else if (info1.equals(TypeBloc.SOURIS))
		{
			if(loupe)return(psouris);
			return(vide);
		}
		else if (info1.equals(TypeBloc.SPIREL))
		{
			if(loupe)return(pspirel);
			return(spirel);
		}
		else
		{
			if(loupe)return(pvide);
			return(vide);
		}
	}
	@Override
	public ArrayList<Image> getImages(ObjectType objType, ImageInfo info1,ImageInfo info2, int anim)
	{
		return null;
	}
}
