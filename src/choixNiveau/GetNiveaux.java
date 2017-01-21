package choixNiveau;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GetNiveaux {

	public static List<String> getDocInFolder(String path )
	{
		List<String> _listNomNiveaux= new ArrayList<String>();
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		    for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile())
		      {
		    	  _listNomNiveaux.add(listOfFiles[i].getName());
		      }
		     // else if (listOfFiles[i].isDirectory()) {
		    	  //_listNomNiveaux.add(listOfFiles[i].getName()); 
		    	 // }
		    }
		    return(_listNomNiveaux);
	}
	
	
	public static List<String> getDocInJar(String path)
	{
		List<String> _listNomNiveaux = new ArrayList<String>();
		CodeSource src = ModelChoixNiveau.class.getProtectionDomain().getCodeSource();
		if(src!=null)
		{
			URL jar= src.getLocation();
			ZipInputStream zip = null;
			try {
				zip = new ZipInputStream(jar.openStream());
			} 
			catch (IOException e2) {e2.printStackTrace();}
			while(true)
			{
				ZipEntry e = null;
				try {
					e = zip.getNextEntry();
				} 
				catch (IOException e1) {e1.printStackTrace();}
				if(e==null)
				{
					break;
				}
				String name = e.getName();
				
				if(name.startsWith(path))
				{
					_listNomNiveaux.add(name.replaceFirst(path, ""));
				}
			}
		}
		else
		{
			//fail
		}
		return(_listNomNiveaux);
	}
}
