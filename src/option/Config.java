package option;

public class Config {
	
	public static double musicVolume=0;//between 0 and 1
	public static double bruitageVolume= 0;//between 0 and 1
	private static int default_fps = 60;
	private static int fps = 60; // 60 update every 17ms
	public static boolean pauseWhenLooseFocus = false;
	public static boolean showHotkeyWhenPlaying = false;
	public static int fps(){return fps;}
	/**
	 * return fps/default_fps
	 * @return
	 */
	public static float ratio_fps(){return (float)(fps)/default_fps;}
	/**
	 * return default_fps/fps
	 * @return
	 */
	public static float i_ratio_fps(){return ((float)default_fps)/fps;}

	public static double getDeltaFrame(boolean ms){return ((ms?1000.0:1.0)/fps);}//in ms
}
