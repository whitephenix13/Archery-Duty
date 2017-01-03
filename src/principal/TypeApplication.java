package principal;

public class TypeApplication {
	public static boolean isJar;
	public static boolean isExe;
	
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
	//TODO: pas a jour 
	public boolean isExe() {
		   String className = this.getClass().getName().replace('.', '/');
		   String classExe =  
		     this.getClass().getResource("/" + className + ".class").toString();
		   if (classExe.startsWith("exe:")) {
		     return(true);
		   }
		   else
		   {
		   return(false);}
		   
		 }
}
