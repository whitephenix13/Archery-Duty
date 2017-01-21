package principal;

import java.net.URL;
import java.security.CodeSource;

import choixNiveau.ModelChoixNiveau;

public class TypeApplication {
	public static boolean isJar;
	
	/**
	 * identique si le programme lancé est dans un jar.
	 */	
	public boolean isJar() {
		   String className = this.getClass().getName().replace('.', '/');
		   String classJar =  
		     this.getClass().getResource("/" + className + ".class").toString();
		   if (classJar.startsWith("jar:")) {
		     return(true);
		   }
		   else
		   {
		   return(false);}
		   
		 }
}
