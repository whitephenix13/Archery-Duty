package music;

import java.util.ArrayList;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import gameConfig.InterfaceConstantes;
import menu.menuPrincipal.ModelPrincipal;
import option.Config;

public class MusicBruitage extends AsynchroneMusic implements InterfaceConstantes{

	public static MusicBruitage me;
	public LoaderMusicBruitage loaderMusicBruitage;
	double gain;
	int nombreBruitage =0 ;
	
	private MusicBruitage()
	{	
		super();
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

	public void startBruitage(final String typeBruitage)
	{
		ModelPrincipal.debugTime.startElapsedForVerbose();
		this.requests.add(new Request(){
			@Override
			public void run() {
				volumeControl(me.gain);
				final Clip c = me.loaderMusicBruitage.mapClips.get(typeBruitage);
				c.stop();
				c.setMicrosecondPosition(0);
				while(c.isRunning())
				{}
				c.start();
			}
		});
		ModelPrincipal.debugTime.elapsed("MusicBruitage : create request");
		this.runRequests();
		ModelPrincipal.debugTime.elapsed("MusicBruitage : run request");
	}

}
