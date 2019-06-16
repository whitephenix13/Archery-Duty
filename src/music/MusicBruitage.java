package music;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import gameConfig.InterfaceConstantes;
import option.Config;

public class MusicBruitage implements InterfaceConstantes{

	public static MusicBruitage me;
	public LoaderMusicBruitage loaderMusicBruitage;
	double gain;
	int nombreBruitage =0 ;

	private MusicBruitage()
	{		
		gain = Config.bruitageVolume;
		loaderMusicBruitage= new LoaderMusicBruitage();
	}
	public static void init()
	{			
		if(me==null){
			me = new MusicBruitage();
		}
	}
	
	

	public void volumeControl(double nouvGain)
	{
		me.gain=nouvGain;
		Config.bruitageVolume=me.gain;

		for(Clip c : me.loaderMusicBruitage.mapClips.values())
		{
			FloatControl gainControl = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
			float dB = (float) (Math.log(me.gain) / Math.log(10.0) * 30.0);
			gainControl.setValue(dB);
		}
	}

	public void startBruitage(String typeBruitage)
	{
		volumeControl(me.gain);

		Clip c = me.loaderMusicBruitage.mapClips.get(typeBruitage);
		if(!c.isRunning() || (c.isRunning() && (c.getFramePosition()>0)) )
		{
			c.stop();
			c.setMicrosecondPosition(0);
			while(c.isRunning())
			{}
			c.start();
		}

	}

}
