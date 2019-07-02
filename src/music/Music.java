package music;

import java.util.Map;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import gameConfig.InterfaceConstantes;
import option.Config;

public class Music extends AsynchroneMusic implements InterfaceConstantes{

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
		super();
		loaderMusic = new LoaderMusic();
	}


	public void startNewMusic(final String newMusic)
	{
		this.requests.add(new Request(){
			@Override
			public void run() {
				if(!musiqueEnCours.equals(""))
					stopMusic();
				setMusic(newMusic);
				startMusic();
			}});
		this.runRequests();
	}

	public void slowDownMusic()
	{
		slowDown(true);
	}
	public void endSlowDownMusic() 
	{
		slowDown(false);
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
	private void slowDown(final boolean start)
	{
		if(!loaderMusic.isSoundFound())
			return;
		if((start && isSlowed) || (!start && !isSlowed))
			return;
		final Clip c = getClip(!start);//if start, get the unslowed music
		if(c==null)
			return;
		
		this.requests.add(new Request(){
			@Override
			public void run() {
				int framePos= c.getFramePosition();
				c.stop();
		
				getClip(start).setFramePosition((int)(framePos*(start?2:0.5f))); //if start, get the slowed music
				volumeControl(gain);
				getClip(start).start();
			}});
		this.runRequests();
		isSlowed=start;//if start, we slowed the music 
	}
	
	private void startMusic()
	{
		System.out.println("Start music" );
		if(!loaderMusic.isSoundFound())
			return;
	
		getClip(false).setFramePosition(0);
		volumeControl(gain);
		getClip(false).loop(Clip.LOOP_CONTINUOUSLY);
	}
	private void setMusic(String nomMusique)
	{
		musiqueEnCours=nomMusique;
	}
	private void stopMusic()
	{
		if(!loaderMusic.isSoundFound())
			return;
		
		final Clip c =getClip(false);
		final Clip c_slow =getClip(true);

		if(c.isActive())
			c.stop();
		if(slowVersionExist())
			if(c_slow.isActive())
				c_slow.stop();
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




}
