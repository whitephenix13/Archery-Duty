package effects;

import java.util.Arrays;

import fleches.Fleche;
import partie.AbstractModelPartie;

public class Vent_effect extends Effect{
	public Vent_effect(AbstractModelPartie partie, int _xpos, int _ypos,int _xanc, int _yanc, double _rota,int _anim, int current_frame)
	{
		xpos=_xpos;
		ypos=_ypos;
		rotation = _rota;
		anim=_anim;
		
		name = Fleche.SPIRITUELLE.VENT;
		xtaille =  Arrays.asList(164,191,259,400);
		ytaille =  Arrays.asList(159,222,349,400);
		xanchor =  Arrays.asList(_xanc,_xanc,_xanc,_xanc);
		yanchor =  Arrays.asList(_yanc,_yanc,_yanc,_yanc);
		//TODO set start and end index 
		//TODO: set correct time 
		int start_index =0;
		int end_index =4;
		animation.start(Arrays.asList(4,8,12,16), current_frame, start_index, end_index);
		maxnumberloops = 1;
		
		partie.arrowsEffects.add(this);
	}

	@Override
	public void onDestroy() {
		
	}
}
