package partie.modelPartie;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import gameConfig.InterfaceConstantes;

public class InputPartie {
	//input : modifiers := shift | control | ctrl | meta | alt | altGraph +++ pressedReleasedID := (pressed | released) key +++key := KeyEvent key code name, i.e. the name following "VK_".
	//"alt shift released X"

	private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
	//pressed
	protected boolean marcheDroiteDown ;
	protected boolean marcheGaucheDown ;
	protected boolean sautDown ;
	protected boolean toucheTirDown ;
	protected boolean touche2TirDown ;
	protected boolean courseDroiteDown;
	protected boolean courseGaucheDown;
	protected boolean toucheSlowDown;
	protected boolean pauseDown;
	protected int arrowDown; //-1 for none 0->3 otherwise 
	//release
	protected boolean marcheDroiteReleased ;
	protected boolean marcheGaucheReleased ;
	protected boolean sautReleased ;
	protected boolean toucheTirReleased ;
	protected boolean touche2TirReleased ;
	protected boolean courseDroiteReleased;
	protected boolean courseGaucheReleased;
	protected boolean toucheSlowReleased;
	protected boolean pauseReleased;
	protected int arrowReleased;//-1 for none 0->3 otherwise 


	protected float clickTime1;
	protected float clickTime2;

	protected boolean clickRight = false;
	protected boolean clickLeft=false;

	public void resetTouchesFocus()
	{

		if (marcheDroiteDown) { marcheDroiteReleased=true;};
		if (marcheGaucheDown) { marcheGaucheReleased=true;};
		if (sautDown) { sautReleased=true;};
		if (toucheTirDown) { toucheTirReleased=true;};
		if (touche2TirDown) { touche2TirReleased=true;};
		if (courseDroiteDown) {courseDroiteReleased=true;};
		if (courseGaucheDown) { courseGaucheReleased=true;};
		if (toucheSlowDown) { toucheSlowReleased=true;};
		if (pauseDown) { pauseReleased=true;};
		if(arrowDown>-1) {arrowReleased=arrowDown;}


		marcheDroiteDown= false;
		marcheGaucheDown = false;
		sautDown = false;
		toucheTirDown = false;
		touche2TirDown = false;
		courseDroiteDown= false;
		courseGaucheDown= false;
		toucheSlowDown= false;
		pauseDown= false;
		arrowDown=-1;

	}
	//actionmap
	//pressed
	private static final String MOVE_RIGHT = "move right";
	private static final String MOVE_LEFT = "move left";
	private static final String SLOW = "slow";
	private static final String SHOOT = "shoot";
	private static final String SPE_SHOOT = "special shoot";
	private static final String JUMP = "jump";
	private static final String PAUSE = "pause";
	private static final String ARROW0 = "arrow0";
	private static final String ARROW1 = "arrow1";
	private static final String ARROW2 = "arrow2";
	private static final String ARROW3 = "arrow3";

	//released
	private static final String R_MOVE_RIGHT = "released move right";
	private static final String R_MOVE_LEFT = "released move left";
	private static final String R_SLOW = "released slow";
	private static final String R_SHOOT = "released shoot";
	private static final String R_SPE_SHOOT = "released special shoot";
	private static final String R_JUMP = "released jump";
	private static final String R_PAUSE = "released pause";
	private static final String R_ARROW0 = "released arrow0";
	private static final String R_ARROW1 = "released arrow1";
	private static final String R_ARROW2 = "released arrow2";
	private static final String R_ARROW3 = "released arrow3";

	AbstractModelPartie partie;
	JComponent comp;

	public InputPartie(AbstractModelPartie _part)
	{partie=_part;}
	public void init(JComponent _comp)
	{
		comp=_comp;
		//PRESSED 
		inputMapPut(partie.touches.t_droite,MOVE_RIGHT);
		actionMapPut(MOVE_RIGHT);

		inputMapPut(partie.touches.t_gauche,MOVE_LEFT);
		actionMapPut(MOVE_LEFT);

		inputMapPut(partie.touches.t_slow,SLOW);
		actionMapPut(SLOW);

		inputMapPut(partie.touches.t_tir,SHOOT);
		actionMapPut(SHOOT);

		inputMapPut(partie.touches.t_2tir,SPE_SHOOT);
		actionMapPut(SPE_SHOOT);

		inputMapPut(partie.touches.t_saut,JUMP);
		actionMapPut(JUMP);

		inputMapPut(partie.touches.t_pause,PAUSE);
		actionMapPut(PAUSE);
		
		inputMapPut(partie.touches.t_arrow0,ARROW0);
		actionMapPut(ARROW0);
		
		inputMapPut(partie.touches.t_arrow1,ARROW1);
		actionMapPut(ARROW1);
		
		inputMapPut(partie.touches.t_arrow2,ARROW2);
		actionMapPut(ARROW2);

		inputMapPut(partie.touches.t_arrow3,ARROW3);
		actionMapPut(ARROW3);

		//RELEASED
		inputMapPut(buildReleaseKeyStroke(partie.touches.t_droite),R_MOVE_RIGHT);
		actionMapPut(R_MOVE_RIGHT);

		inputMapPut(buildReleaseKeyStroke(partie.touches.t_gauche),R_MOVE_LEFT);
		actionMapPut(R_MOVE_LEFT);

		inputMapPut(buildReleaseKeyStroke(partie.touches.t_slow),R_SLOW);
		actionMapPut(R_SLOW);

		inputMapPut(buildReleaseKeyStroke(partie.touches.t_tir),R_SHOOT);
		actionMapPut(R_SHOOT);

		inputMapPut(buildReleaseKeyStroke(partie.touches.t_2tir),R_SPE_SHOOT);
		actionMapPut(R_SPE_SHOOT);

		inputMapPut(buildReleaseKeyStroke(partie.touches.t_saut),R_JUMP);
		actionMapPut(R_JUMP);

		inputMapPut(buildReleaseKeyStroke(partie.touches.t_pause),R_PAUSE);
		actionMapPut(R_PAUSE);

		inputMapPut(buildReleaseKeyStroke(partie.touches.t_arrow0),R_ARROW0);
		actionMapPut(R_ARROW0);
		
		inputMapPut(buildReleaseKeyStroke(partie.touches.t_arrow1),R_ARROW1);
		actionMapPut(R_ARROW1);

		inputMapPut(buildReleaseKeyStroke(partie.touches.t_arrow2),R_ARROW2);
		actionMapPut(R_ARROW2);

		inputMapPut(buildReleaseKeyStroke(partie.touches.t_arrow3),R_ARROW3);
		actionMapPut(R_ARROW3);



		this.resetTouchesFocus();
	}
	public void reset()
	{
		ArrayList<Object> actMap = new ArrayList<Object>();
		for( KeyStroke k : comp.getInputMap(IFW).allKeys())
		{
			actMap.add(comp.getInputMap(IFW).get(k));
			comp.getInputMap(IFW).remove(k);
		}
		for(Object am : actMap)
		{
			comp.getActionMap().remove(am);
		}
		this.resetTouchesFocus();
	}

	public void inputMapPut(String ks, String a)
	{
		boolean isMouseInput = ks.equals(partie.touches.LEFT_MOUSE) || ks.equals(partie.touches.MIDDLE_MOUSE) || ks.equals(partie.touches.RIGHT_MOUSE) ;
		boolean isReleasedMouseInput = ks.equals(buildReleaseKeyStroke(partie.touches.LEFT_MOUSE)) || 
				ks.equals(buildReleaseKeyStroke(partie.touches.MIDDLE_MOUSE)) || ks.equals(buildReleaseKeyStroke(partie.touches.RIGHT_MOUSE)) ;
		if(isMouseInput|| isReleasedMouseInput)
			partie.touches.mapMouse.put(ks, a);
		else
			comp.getInputMap(IFW).put(KeyStroke.getKeyStroke(ks), a);
	}
	public void actionMapPut(String a)
	{
		if(a == MOVE_RIGHT ) {comp.getActionMap().put(a, new MoveAction(1));}
		else if(a == MOVE_LEFT){comp.getActionMap().put(a, new MoveAction(-1));}
		else if(a == SLOW){comp.getActionMap().put(a, new SlowAction());}
		else if(a == SHOOT){comp.getActionMap().put(a, new ShootAction(1));}
		else if(a == SPE_SHOOT){comp.getActionMap().put(a, new ShootAction(2));}
		else if(a == JUMP){comp.getActionMap().put(a, new JumpAction());}
		else if(a == PAUSE){comp.getActionMap().put(a, new PauseAction());}
		else if(a == ARROW0){comp.getActionMap().put(a, new ArrowAction(0));}
		else if(a == ARROW1){comp.getActionMap().put(a, new ArrowAction(1));}
		else if(a == ARROW2){comp.getActionMap().put(a, new ArrowAction(2));}
		else if(a == ARROW3){comp.getActionMap().put(a, new ArrowAction(3));}


		else if(a == R_MOVE_RIGHT ) {comp.getActionMap().put(a, new R_MoveAction(1));}
		else if(a == R_MOVE_LEFT){comp.getActionMap().put(a, new R_MoveAction(-1));}
		else if(a == R_SLOW){comp.getActionMap().put(a, new R_SlowAction());}
		else if(a == R_SHOOT){comp.getActionMap().put(a, new R_ShootAction(1));}
		else if(a == R_SPE_SHOOT){comp.getActionMap().put(a, new R_ShootAction(2));}
		else if(a == R_JUMP){comp.getActionMap().put(a, new R_JumpAction());}
		else if(a == R_PAUSE){comp.getActionMap().put(a, new R_PauseAction());}
		else if(a == R_ARROW0){comp.getActionMap().put(a, new R_ArrowAction(0));}
		else if(a == R_ARROW1){comp.getActionMap().put(a, new R_ArrowAction(1));}
		else if(a == R_ARROW2){comp.getActionMap().put(a, new R_ArrowAction(2));}
		else if(a == R_ARROW3){comp.getActionMap().put(a, new R_ArrowAction(3));}

		else
		{
			try {throw new Exception("actionMap "+a+" not handled in actionMapPut");} catch (Exception e) {e.printStackTrace();}
		}
	}
	public void applyMouseInput(String mouseInput)
	{
		LinkedHashMap<String,String> mapMouse = partie.touches.mapMouse;
		String a_name =mapMouse.get(mouseInput);
		AbstractAction a=null;
		if(a_name!=null)
			a=(AbstractAction) comp.getActionMap().get(a_name);
		if(a != null)
			a.actionPerformed(new ActionEvent(comp, ActionEvent.ACTION_PERFORMED,""));

	}
	public void rebindKey(String newKey, String oldKey) {
		//KeyStroke.getKeyStrokeForEvent(ke)
		Object a =null;
		Object r_a=null;
		String r_oldKey = buildReleaseKeyStroke(oldKey);
		String r_newKey = buildReleaseKeyStroke(newKey);
		LinkedHashMap<String,String> mapMouse = partie.touches.mapMouse;
		boolean isMouseInput = oldKey.equals(partie.touches.LEFT_MOUSE) || oldKey.equals(partie.touches.MIDDLE_MOUSE) || oldKey.equals(partie.touches.RIGHT_MOUSE) ;
		boolean isReleasedMouseInput = oldKey.equals(buildReleaseKeyStroke(partie.touches.LEFT_MOUSE)) || 
				oldKey.equals(buildReleaseKeyStroke(partie.touches.MIDDLE_MOUSE)) || oldKey.equals(buildReleaseKeyStroke(partie.touches.RIGHT_MOUSE)) ;
		if(isMouseInput|| isReleasedMouseInput){
			a=mapMouse.get(oldKey);
			r_a=mapMouse.get(r_oldKey);
		}
		else{
			a=comp.getInputMap(IFW).get(KeyStroke.getKeyStroke(oldKey));
			r_a=comp.getInputMap(IFW).get(KeyStroke.getKeyStroke(r_oldKey));
		}

		if(isMouseInput||isReleasedMouseInput){
			mapMouse.remove(oldKey);mapMouse.remove(r_oldKey);}
		else{
			comp.getInputMap(IFW).remove(KeyStroke.getKeyStroke(oldKey));comp.getInputMap(IFW).remove(KeyStroke.getKeyStroke(r_oldKey));}

		if(isMouseInput||isReleasedMouseInput){
			mapMouse.put(newKey, (String) a);mapMouse.put(r_newKey,(String) r_a);}
		else{
			comp.getInputMap(IFW).put(KeyStroke.getKeyStroke(newKey),a);comp.getInputMap(IFW).put(KeyStroke.getKeyStroke(r_newKey),r_a);}

	}
	private class MoveAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int direction;
		MoveAction(int direction) {
			// -1 for left, 1 for right
			this.direction = direction;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean right = (direction==1);
			boolean left = (direction == -1);
			if(right|| left)
			{
				clickTime2 = System.nanoTime();
				float delta = (float) ((clickTime2-clickTime1)*Math.pow(10, -6));
				boolean dash = delta<InterfaceConstantes.TDash ; // no tmin dash 

				if(dash && clickRight==true && right)
				{
					courseDroiteDown=true;
					marcheDroiteDown=true;
					clickRight=true;
					clickLeft=false;
				}
				else if(dash && clickLeft==true && left)
				{
					courseGaucheDown=true;
					marcheGaucheDown=true;
					clickLeft=true;
					clickRight=false;
				}
				else
				{
					if(right)
					{
						//deplacement normal
						marcheDroiteDown=true;
						clickRight=true;
						clickLeft=false;
					}
					if(left)
					{
						//deplacement normal
						marcheGaucheDown=true;
						clickLeft=true;
						clickRight=false;
					}
				}
			}

		}
	}
	private class SlowAction extends AbstractAction{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		SlowAction(){}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			toucheSlowDown=true;
		}}
	private class ShootAction extends AbstractAction{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int type_tir=0;//1 normal, 2 special
		ShootAction(int _type){type_tir=_type;};
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(type_tir==1)
				toucheTirDown=true;
			else if(type_tir==2)
				touche2TirDown=true;
		}}
	private class JumpAction extends AbstractAction{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		JumpAction(){};
		@Override
		public void actionPerformed(ActionEvent arg0) {
			sautDown=true;

		}}
	private class PauseAction extends AbstractAction{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		PauseAction(){};
		@Override
		public void actionPerformed(ActionEvent arg0) {
			pauseDown=true;
		}}
	private class ArrowAction extends AbstractAction{
		/**
		 * 
		 */
		int slotnum=0;
		private static final long serialVersionUID = 1L;
		ArrowAction(int _slotnum){slotnum=_slotnum;};
		@Override
		public void actionPerformed(ActionEvent arg0) {
			arrowDown = slotnum;
		}}

	private class R_MoveAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int direction;
		R_MoveAction(int direction) {
			// -1 for left, 1 for right
			this.direction = direction;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean right = (direction==1);
			boolean left = (direction == -1);
			if(right)
			{			
				partie.heros.deplaceSautDroit=false;

				marcheDroiteDown=false;
				marcheDroiteReleased=true;

				courseDroiteDown=false;
				courseDroiteReleased=true;
				//on retient le temps de relachement de la touche
				clickTime1 = System.nanoTime();
			}
			else if (left)
			{
				partie.heros.deplaceSautGauche=false;

				marcheGaucheDown=false;
				marcheGaucheReleased=true;

				courseGaucheDown=false;
				courseGaucheReleased=true;
				//on retient le temps de relachement de la touche
				clickTime1 = System.nanoTime();
			}
			else
			{
				try {throw new Exception("R_moveAction, direction "+direction+" not handled");} catch (Exception ex) {ex.printStackTrace();}
			}
		}
	}
	private class R_SlowAction extends AbstractAction{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		R_SlowAction(){}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			toucheSlowDown=false;
			toucheSlowReleased=true;
		}}
	private class R_ShootAction extends AbstractAction{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int type_tir = 0; //1 for regular, 2 for special
		R_ShootAction(int _type){type_tir=_type;};
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(type_tir==1)
			{
				toucheTirDown=false;
				toucheTirReleased=true;
			}
			else if(type_tir==2)
			{
				touche2TirDown=false;
				touche2TirReleased=true;
			}
		}}
	private class R_JumpAction extends AbstractAction{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		R_JumpAction(){};
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//do nothing
		}}
	private class R_PauseAction extends AbstractAction{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		R_PauseAction(){};
		@Override
		public void actionPerformed(ActionEvent arg0) {
			pauseDown=false;
			pauseReleased=true;
		}}
	private class R_ArrowAction extends AbstractAction{
		/**
		 * 
		 */
		int slotnum=0;
		private static final long serialVersionUID = 1L;
		R_ArrowAction(int _slotnum){slotnum=_slotnum;};
		@Override
		public void actionPerformed(ActionEvent arg0) {
			arrowReleased = slotnum;
			arrowDown = -1;
		}}
	public String buildReleaseKeyStroke(String ks)
	{
		String[] ks_split = ks.split(" ");
		String res="";
		int ind = 0;
		for(String s : ks_split)
		{
			if(ind==(ks_split.length-1))
			{
				if(ind==0)
					res+="released "+s;
				else
					res+=" released "+s;
			}
			else
			{res+=" "+s;}
			ind+=1;
		}
		return res;
	}

}