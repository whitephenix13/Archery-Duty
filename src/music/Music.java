package music;

import java.util.Map;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import gameConfig.InterfaceConstantes;
import option.Config;

public class Music implements InterfaceConstantes{

	public static Music me =null; 
	public LoaderMusic loaderMusic = null;
	
	static double gain = Config.bruitageVolume;
	public static String musiqueEnCours ="";
	//static Map<String,Clip> mapClips = new HashMap<String,Clip>();
	boolean isSlowed = false;
	//boolean soundFound=true;

	public static void init()
	{
		if(me==null)
			me=new Music();
	}
	private Music()
	{
		loaderMusic = new LoaderMusic();
	}


	public void startMusic()
	{
		if(!loaderMusic.isSoundFound())
			return;

		getClip(false).setFramePosition(0);
		volumeControl(gain);
		getClip(false).loop(Clip.LOOP_CONTINUOUSLY);
	}
	public void startNewMusic(String newMusic)
	{
		stopMusic();
		setMusic(newMusic);
		startMusic();
	}
	public void stopMusic()
	{
		if(!loaderMusic.isSoundFound())
			return;

		if(getClip(false).isActive())
			getClip(false).stop();
		if(slowVersionExist())
			if(getClip(true).isActive())
				getClip(true).stop();
	}

	public void setMusic(String nomMusique)
	{
		musiqueEnCours=nomMusique;
	}
	
	Clip getClip(boolean slowedMusic)
	{
		Map<String,Clip> mapClips = loaderMusic.getMapClips();
		return(slowedMusic? mapClips.get(musiqueEnCours+"_s") :mapClips.get(musiqueEnCours) );
	}

	boolean slowVersionExist()
	{
		Map<String,Clip> mapClips = loaderMusic.getMapClips();
		return(mapClips.containsKey(musiqueEnCours+"_s"));
	}
	/**
	 * Permet de régler le volume de la musique
	 *  
	 * @param nouvGain, nouvelle valeur du gain entre 0 et 1 
	 * 
	 */
	public void volumeControl(Double nouvGain)
	{
		gain=nouvGain;
		Config.musicVolume=gain;
		float dB = (float) (Math.log(gain) / Math.log(10.0) * 30.0);
		Map<String,Clip> mapClips = loaderMusic.getMapClips();
		
		FloatControl gainControl = (FloatControl) mapClips.get(musiqueEnCours).getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(dB);

		if(slowVersionExist())
		{
			FloatControl gainControl2 = (FloatControl) mapClips.get(musiqueEnCours+"_s").getControl(FloatControl.Type.MASTER_GAIN);
			gainControl2.setValue(dB);
		}
	}

	
	public void slowDownMusic()
	{
		if(!loaderMusic.isSoundFound())
			return;
		if(isSlowed)
			return;
		Clip c = getClip(false);
		if(c==null)
			return;
		int framePos= c.getFramePosition();
		c.stop();

		getClip(true).setFramePosition(framePos*2);
		volumeControl(gain);
		getClip(true).start();
		isSlowed=true;
	}
	public void endSlowDownMusic() 
	{
		if(!isSlowed)
			return;
		
		Clip c = getClip(true);
		if(c==null)
			return;
		int framePos= c.getFramePosition();
		c.stop();

		getClip(false).setFramePosition(framePos/2);
		volumeControl(gain);
		getClip(false).start();
		isSlowed=false;
	}


}
