package partie;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import principal.InterfaceConstantes;

public class InputPartie {
	//input : modifiers := shift | control | ctrl | meta | alt | altGraph +++ pressedReleasedID := (pressed | released) key +++key := KeyEvent key code name, i.e. the name following "VK_".
	//"alt shift released X"

	private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
	//pressed
	protected boolean marcheDroiteDown ;
	protected boolean marcheGaucheDown ;
	protected boolean sautDown ;
	protected boolean toucheTirDown ;
	protected boolean courseDroiteDown;
	protected boolean courseGaucheDown;
	protected boolean toucheSlowDown;
	protected boolean pauseDown;
	//release
	protected boolean marcheDroiteReleased ;
	protected boolean marcheGaucheReleased ;
	protected boolean sautReleased ;
	protected boolean toucheTirReleased ;
	protected boolean courseDroiteReleased;
	protected boolean courseGaucheReleased;
	protected boolean toucheSlowReleased;
	protected boolean pauseReleased;
	
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
		if (courseDroiteDown) {courseDroiteReleased=true;};
		if (courseGaucheDown) { courseGaucheReleased=true;};
		if (toucheSlowDown) { toucheSlowReleased=true;};
		if (pauseDown) { pauseReleased=true;};

		marcheDroiteDown= false;
		marcheGaucheDown = false;
		sautDown = false;
		toucheTirDown = false;
		courseDroiteDown= false;
		courseGaucheDown= false;
		toucheSlowDown= false;
		pauseDown= false;
	}
	//actionmap
	//pressed
	private static final String MOVE_RIGHT = "move right";
	private static final String MOVE_LEFT = "move left";
	private static final String SLOW = "slow";
	private static final String SHOOT = "shoot";
	private static final String JUMP = "jump";
	private static final String PAUSE = "pause";
	//released
	private static final String R_MOVE_RIGHT = "released move right";
	private static final String R_MOVE_LEFT = "released move left";
	private static final String R_SLOW = "released slow";
	private static final String R_SHOOT = "released shoot";
	private static final String R_JUMP = "released jump";
	private static final String R_PAUSE = "released pause";
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

		inputMapPut(partie.touches.t_saut,JUMP);
		actionMapPut(JUMP);

		inputMapPut(partie.touches.t_pause,PAUSE);
		actionMapPut(PAUSE);

		//RELEASED
		inputMapPut(buildReleaseKeyStroke(partie.touches.t_droite),R_MOVE_RIGHT);
		actionMapPut(R_MOVE_RIGHT);

		inputMapPut(buildReleaseKeyStroke(partie.touches.t_gauche),R_MOVE_LEFT);
		actionMapPut(R_MOVE_LEFT);

		inputMapPut(buildReleaseKeyStroke(partie.touches.t_slow),R_SLOW);
		actionMapPut(R_SLOW);

		inputMapPut(buildReleaseKeyStroke(partie.touches.t_tir),R_SHOOT);
		actionMapPut(R_SHOOT);

		inputMapPut(buildReleaseKeyStroke(partie.touches.t_saut),R_JUMP);
		actionMapPut(R_JUMP);

		inputMapPut(buildReleaseKeyStroke(partie.touches.t_pause),R_PAUSE);
		actionMapPut(R_PAUSE);
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
		else if(a == SHOOT){comp.getActionMap().put(a, new ShootAction());}
		else if(a == JUMP){comp.getActionMap().put(a, new JumpAction());}
		else if(a == PAUSE){comp.getActionMap().put(a, new PauseAction());}
		else if(a == R_MOVE_RIGHT ) {comp.getActionMap().put(a, new R_MoveAction(1));}
		else if(a == R_MOVE_LEFT){comp.getActionMap().put(a, new R_MoveAction(-1));}
		else if(a == R_SLOW){comp.getActionMap().put(a, new R_SlowAction());}
		else if(a == R_SHOOT){comp.getActionMap().put(a, new R_ShootAction());}
		else if(a == R_JUMP){comp.getActionMap().put(a, new R_JumpAction());}
		else if(a == R_PAUSE){comp.getActionMap().put(a, new R_PauseAction());}
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
		ShootAction(){};
		@Override
		public void actionPerformed(ActionEvent arg0) {
			toucheTirDown=true;			
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
		R_ShootAction(){};
		@Override
		public void actionPerformed(ActionEvent arg0) {
			toucheTirDown=false;
			toucheTirReleased=true;
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
