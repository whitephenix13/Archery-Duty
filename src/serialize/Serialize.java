package serialize;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import editeur.StockageMonstre;
import gameConfig.InterfaceConstantes;
import menu.choixNiveau.ModelChoixNiveau;
import partie.bloc.Bloc;
import partie.bloc.Monde;
import partie.bloc.Bloc.TypeBloc;
import utils.TypeApplication;

public class Serialize implements InterfaceConstantes{

	//OPTIMAL BUFFER SIZE: try 64K, 256K, 512KB or 1MB*/
	// Données à sérializer 
	public static int loadPercentage=0;
	public static boolean niveauLoaded = false;

	public static String erreurMsgChargement="";

	private static byte[] byte4;
	private static byte[] byte1;

	
	private static boolean isVersionOlderThan(String myVersion, String versionToCompare)
	{
		return myVersion.compareTo(versionToCompare)<0;
	}
	
	public static String serializeStockageMonstre(FileOutputStream fos,StockageMonstre monstre ) 
	{
		//int corresponding to enum value , x,y , bool
		try {
			fos.write(intToBytes(monstre.type.ordinal()));
			fos.write(intToBytes(monstre.pos.x));
			fos.write(intToBytes(monstre.pos.y));
			fos.write(boolToByte(monstre.immobile));
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "Erreur serialize stockage monstre\n";
		}


	}

	public static StockageMonstre deserializeStockageMonstre(InputStream is,String version) 
	{
		StockageMonstre stock = new StockageMonstre(TypeBloc.NONE, new Point(),false);
		Point p = new Point();

		try
		{
			//REMOVEbyte[] bytes = new byte[4];
			
			//int,4, type of the monster 
			is.read(byte4); 
			stock.type = TypeBloc.values()[bytesToInt(byte4)];
			
			//int, 4 , pos en x du monstre
			is.read(byte4); 
			p.x = bytesToInt(byte4);

			//int, 4 , pos en y du monstre
			is.read(byte4); 
			p.y = bytesToInt(byte4);

			stock.pos =p ;

			//bool, 1 , monstre immobile?
			is.read(byte1); 
			stock.immobile = byteToBool(byte1);

			return(stock);
		}
		catch (IOException e)
		{
			e.getStackTrace();
			String err= "ERREUR CHARGEMENT STOCKAGE MONSTRE\n";

			erreurMsgChargement+=err;
			return(null);
		}
	}

	public static String serializeListStockageMonstre(FileOutputStream fos, List<StockageMonstre> monstres)
	{
		String err = "";
		try {
			fos.write(intToBytes(monstres.size()));
			for(StockageMonstre m : monstres)
			{
				err+=serializeStockageMonstre(fos,m);
			}
			return err;
		} catch (IOException e) {
			e.printStackTrace();
			err+= "Erreur serialize list stockage monstre\n";
			return err;

		}

	}

	public static List<StockageMonstre> deserializeListStockageMonstre(InputStream is,String version) 
	{
		List<StockageMonstre> l = new ArrayList<StockageMonstre>();

		try 
		{
			//int, 4, taille de la liste
			is.read(byte4);
			int nb = bytesToInt(byte4);

			for(int i =0; i<nb; i++)
			{
				l.add(deserializeStockageMonstre(is,version));
				loadPercentage = (int) (80 + 25.0/nb * i);

			}
			return(l);
		}
		catch (IOException e)
		{
			e.getStackTrace();
			String err= "ERREUR CHARGEMENT LISTE STOCKAGE MONSTRE\n";

			erreurMsgChargement+=err;
			return null;
		}

	}

	public static String serializeBloc (FileOutputStream fos, Bloc b,String version) 
	{
		try {
			fos.write(intToBytes(b.getType().ordinal()));
			fos.write(intToBytes(b.getXpos()));
			fos.write(intToBytes(b.getYpos()));
			fos.write(boolToByte(b.getBloquer()));
			fos.write(boolToByte(b.getBack()));
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return ("Erreur serialize bloc\n");
		}

	}

	public static Bloc deserializeBloc(InputStream is,String version) 
	{
		Bloc bloc = new Bloc();
		int nb;
		byte[] bytes;

		try 
		{
			is.read(byte4);
			int temp = bytesToInt(byte4);
			bloc.setType(TypeBloc.values()[temp]);

			//int,4,position en x du bloc
			is.read(byte4);
			bloc.setPos(bytesToInt(byte4), bloc.getYpos());

			//int,4,position en y du bloc
			is.read(byte4);
			bloc.setPos( bloc.getXpos(),bytesToInt(byte4));

			//bool,1,peut-on passer au travers du bloc?
			is.read(byte1);
			bloc.setBloquer(byteToBool(byte1));

			//bool,1,le bloc fait-il partie du décors?
			is.read(byte1);
			bloc.setBackground(byteToBool(byte1));

			return(bloc);
		}
		catch (IOException e)
		{
			e.getStackTrace();
			String err= "ERREUR CHARGEMENT BLOC\n";

			erreurMsgChargement+=err;
			return null;
		}
	}

	public static String serializeMatrixBloc (FileOutputStream fos, Bloc[][] b,String version) 
	{
		try {

			int nb=0;
			int xlength=b.length;
			int ylength=b[0].length;
			List<Integer> li= new ArrayList<Integer>();
			List<Bloc> lb = new ArrayList<Bloc>();
			int xmin=-1; 
			int xmax=-1;
			int ymin=-1;
			int ymax=-1;

			for(int i=0; i< xlength; i++)
			{
				for(int j=0; j<ylength; j++)
				{
					if(b[i][j] != null && !b[i][j].getType().equals(TypeBloc.NONE))
					{
						if(nb==0)//premier bloc non nul
						{
							xmin=i;
							xmax=i;
							ymin=j;
							ymax=j;
						}
						else
						{
							xmin= xmin>i?i:xmin;
							xmax= xmax<i?i:xmax;
							ymin=ymin>j?j:ymin;
							ymax=ymax<j?j:ymax;
						}
						++nb;
						li.add(i);
						li.add(j);
						lb.add(b[i][j]);
					}
				}
			}
			if(nb>0)
			{

				fos.write(intToBytes(nb));
				fos.write(intToBytes(xmin));
				fos.write(intToBytes(ymin));
				fos.write(intToBytes(xmax-xmin+1));
				fos.write(intToBytes(ymax-ymin+1));
				for(int i=0; i<lb.size();i++)
				{
					int x =li.get(2*i);
					int y = li.get(2*i+1);
					fos.write(intToBytes(x));
					fos.write(intToBytes(y));
					serializeBloc(fos,b[x][y],version);
				}
				return ""; 
			}
			else
			{
				return "Erreur serialize matrix bloc: monde à enregistrer vide\n";
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			return "Erreur serialize matrix bloc\n";
		}
	}
	public static Bloc[][] deserializeMatrixBloc(InputStream is,Bloc[][] oldWorld,String version)
	{
		byte[] bytes;
		Bloc[][] monde ;
		int nbBloc ; 
		int xoffset;
		int yoffset;
		int xlength ;
		int ylength;
		int x;
		int y;

		try
		{
			//int,4,nombre de bloc non nuls
			is.read(byte4);
			nbBloc=bytesToInt(byte4);
			loadPercentage=1;

			//int,4, indice x minimal du début du monde 
			is.read(byte4);
			xoffset=bytesToInt(byte4);
			loadPercentage=2;

			//int,4,indice y minimal du début du monde 
			is.read(byte4);
			yoffset=bytesToInt(byte4);
			loadPercentage=3;

			//int,4, longueur en x du monde
			is.read(byte4);
			xlength=bytesToInt(byte4);

			//int,4,longueur en y du monde 
			is.read(byte4);
			ylength=bytesToInt(byte4);

			//Bloc[][], nbBloc, matrice de bloc

			//TODO
			//monde = Bloc.InitBlocMatrix(xlength,ylength);
			xlength=ABS_MAX;
			ylength=ORD_MAX;
			loadPercentage=5;

			if(oldWorld != null)
				monde = oldWorld;
			else
			{
				monde = new Bloc[xlength][ylength];
			}
			loadPercentage=55;
			for(int i=0; i<nbBloc; i++)
			{
				is.read(byte4);
				//x=bytesToInt(bytes)-xoffset;
				x=bytesToInt(byte4);

				is.read(byte4);
				//y=bytesToInt(bytes)-yoffset;
				y=bytesToInt(byte4);

				
				monde[x][y]=deserializeBloc(is,version);
				loadPercentage=(int) (55+20.0/nbBloc * i);

			}

			loadPercentage =75;
			return(monde);
		}
		catch (IOException | ArrayIndexOutOfBoundsException e )
		{
			String err= "ERREUR CHARGEMENT MATRICE BLOC\n";

			System.out.println(err);

			erreurMsgChargement+=err;
			return null;
		}
	}
	public static String serializeMonde(FileOutputStream fos, Monde monde,String version) 
	{
		String err=serializeMatrixBloc(fos,monde.niveau,version);
		if(!err.isEmpty())
		{
			return err;
		}
		try
		{
			fos.write(intToBytes(monde.xStartMap));
			fos.write(intToBytes(monde.yStartMap));
			fos.write(intToBytes(monde.xEndMap));
			fos.write(intToBytes(monde.yEndMap));

			fos.write(intToBytes(monde.xStartPerso));
			fos.write(intToBytes(monde.yStartPerso));

			err+=serializeListStockageMonstre(fos,monde.listMonstreOriginal);

			return err;
		}
		catch (IOException e)
		{
			e.getStackTrace();

			return "Erreur serialize monde";
		}
	}

	public static Monde deserializeMonde(InputStream is,Bloc[][] oldWorld,String version) 
	{
		Monde m = new Monde();
		byte[] bytes;

		try
		{
			//Bloc[][], ..., la matrice des blocs du monde
			m.niveau=deserializeMatrixBloc(is,oldWorld,version);
			if(!erreurMsgChargement.equals(""))
				return null;
			//int, 4, l'indice de début en x où faire spawner des monstres 
			is.read(byte4);
			m.xStartMap=bytesToInt(byte4);

			//int, 4, l'indice de début en y où faire spawner des monstres 
			is.read(byte4);
			m.yStartMap=bytesToInt(byte4);

			//int, 4, l'indice de fin en x où faire spawner des monstres 
			is.read(byte4);
			m.xEndMap=bytesToInt(byte4);

			//int, 4, l'indice de fin en x où faire spawner des monstres 
			is.read(byte4);
			m.yEndMap=bytesToInt(byte4);

			//int, 4, l'indice de début en x du perso
			is.read(byte4);
			m.xStartPerso=bytesToInt(byte4);

			//int, 4, l'indice de début en y du perso
			is.read(byte4);
			m.yStartPerso=bytesToInt(byte4);

			loadPercentage =(int) (80);

			m.listMonstreOriginal=deserializeListStockageMonstre(is,version);

			return(m);
		}
		catch (IOException e)
		{
			e.getStackTrace();
			String err= "ERREUR CHARGEMENT MONDE\n";

			erreurMsgChargement+=err;
			return null;
		}

	}


	public static String sauver(String name,Monde monde) {
		FileOutputStream fos;
		String err="";
		try {
			fos=new FileOutputStream("src/resources/levels/"+name);
			String version = VERSION;
			fos.write(version.getBytes());
			err+=serializeMonde(fos,monde,version);
			fos.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return(err);
	}


	/** If the world is the same, give the previous monde.niveau, otherwise give null
	 */
	public static Monde charger(String name,Bloc[][] oldWorld){
		loadPercentage=0;
		niveauLoaded = false;

		InputStream is;
		String path = ModelChoixNiveau.getPath()+ name;
		byte[] bytes= new byte[5];
		byte4 =new byte[4];
		byte1 = new byte[1];
		String version;
		erreurMsgChargement="";

		Monde m=null;
		try {
			if(TypeApplication.isJar)
				is = Serialize.class.getResourceAsStream(path);
			else
				is = new FileInputStream(path);

			is.read(bytes);
			version= new String(bytes);
			m=deserializeMonde(is,oldWorld,version);
			is.close();

			m.name=name;

		} catch (IOException e1 ) {	
			String err= "ERREUR CHARGEMENT NIVEAU : FILE NOT FOUND";

			erreurMsgChargement=err;
			System.out.println(err);
			loadPercentage=100;
			niveauLoaded=true;
			return null;
		}
		loadPercentage=100;
		niveauLoaded=true;
		return m;

	}

	public static byte[] intToBytes(int i)
	{
		return(ByteBuffer.allocate(4).putInt(i).array());

	}

	public static int bytesToInt(byte[] bytes)
	{
		ByteBuffer wrapped = ByteBuffer.wrap(bytes); 
		int num = wrapped.getInt(); // 1
		return(num);
	}

	public static byte boolToByte(boolean b)
	{
		return(b?(byte)1:(byte)0);
	}
	public static boolean byteToBool(byte[] b)
	{
		return(b[0] == (byte)1 );
	}



}
