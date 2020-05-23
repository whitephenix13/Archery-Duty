package partie.input;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import gameConfig.InterfaceConstantes;
import option.Touches;
import partie.modelPartie.AbstractModelPartie;

/***
 * This class is used to get input from the keyboard and map the actions to an input partie listener(most of the time an InputPartiePool) that will store the pressed/released input
 * Then the state of the keys stored in InputPartiePool can be used to determine which action an object should do
 * @author alexandre
 *
 */
public class InputPartie {
	//input : modifiers := shift | control | ctrl | meta | alt | altGraph +++ pressedReleasedID := (pressed | released) key +++key := KeyEvent key code name, i.e. the name following "VK_".
	//"alt shift released X"

	private float releaseMoveTime;
	private float pressedMoveTime;
	private int last_direction=0;
	
	//private boolean clickRight = false;
	//private boolean clickLeft=false;
	
	private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
	private ArrayList<InputPartieListener> listeners = new ArrayList<InputPartieListener>();
	private Touches touches;
	private JComponent comp;
	//actionmap
	//pressed
	private static final String MOVE_RIGHT = "move right";
	private static final String MOVE_LEFT = "move left";
	private static final String DASH = "dash";
	private static final String SLOW = "slow";
	private static final String[] SHOOT = {"normal shot","spe1 shot","spe2 shot","spe3 shot","spe4 shot"};
	private static final String[] SLOT= {"slot 0","slot 1","slot 2","slot 3"};
	private static final String JUMP = "jump";
	private static final String PAUSE = "pause";


	//released
	private static final String R_MOVE_RIGHT = "released move right";
	private static final String R_MOVE_LEFT = "released move left";
	private static final String R_DASH = "released dash";
	private static final String R_SLOW = "released slow";
	private static final String[] R_SHOOT = {"released normal shot","released spe1 shot","released spe2 shot","released spe3 shot","released spe4 shot"};
	private static final String[] R_SLOT= {"released slot 1","released slot 2","released slot 3","released slot 4"};
	private static final String R_JUMP = "released jump";
	private static final String R_PAUSE = "released pause";
	
	public InputPartie(Touches touches)
	{
		this.touches=touches;
	}
	
	public void registerListener(InputPartieListener list){
		listeners.add(list);
	}
	public void removeListener(InputPartieListener list){
		listeners.remove(list);
	}
	public void init(JComponent _comp)
	{
		comp=_comp;
		//PRESSED 
		inputMapPut(touches.t_droite,MOVE_RIGHT);
		actionMapPut(MOVE_RIGHT);

		inputMapPut(touches.t_gauche,MOVE_LEFT);
		actionMapPut(MOVE_LEFT);
		
		inputMapPut(touches.t_dash,DASH);
		actionMapPut(DASH);

		inputMapPut(touches.t_slow,SLOW);
		actionMapPut(SLOW);

		for(int i=0; i<SHOOT.length;++i)
		{
			inputMapPut(touches.t_tir[i],SHOOT[i]);
			actionMapPut(SHOOT[i]);
		}
		for(int i=0; i<SLOT.length;++i)
		{
			inputMapPut(touches.t_slot[i],SLOT[i]);
			actionMapPut(SLOT[i]);
		}

		inputMapPut(touches.t_saut,JUMP);
		actionMapPut(JUMP);

		inputMapPut(touches.t_pause,PAUSE);
		actionMapPut(PAUSE);

		//RELEASED
		inputMapPut(buildReleaseKeyStroke(touches.t_droite),R_MOVE_RIGHT);
		actionMapPut(R_MOVE_RIGHT);

		inputMapPut(buildReleaseKeyStroke(touches.t_gauche),R_MOVE_LEFT);
		actionMapPut(R_MOVE_LEFT);

		inputMapPut(buildReleaseKeyStroke(touches.t_slow),R_SLOW);
		actionMapPut(R_SLOW);
		
		for(int i=0; i<R_SHOOT.length;++i){
			inputMapPut(buildReleaseKeyStroke(touches.t_tir[i]),R_SHOOT[i]);
			actionMapPut(R_SHOOT[i]);
		}
		
		for(int i=0; i<R_SLOT.length;++i){
			inputMapPut(buildReleaseKeyStroke(touches.t_slot[i]),R_SLOT[i]);
			actionMapPut(R_SLOT[i]);
		}

		inputMapPut(buildReleaseKeyStroke(touches.t_saut),R_JUMP);
		actionMapPut(R_JUMP);

		inputMapPut(buildReleaseKeyStroke(touches.t_pause),R_PAUSE);
		actionMapPut(R_PAUSE);

		this.resetGameTouchesFocus();
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
		this.resetGameTouchesFocus();
	}
	public void resetGameTouchesFocus()
	{
		for(InputPartieListener lis : listeners)
			lis.onResetGameTouchesFocus();
	}
	public void inputMapPut(String ks, String a)
	{
		boolean isMouseInput = ks.equals(touches.LEFT_MOUSE) || ks.equals(touches.MIDDLE_MOUSE) || ks.equals(touches.RIGHT_MOUSE) ;
		boolean isReleasedMouseInput = ks.equals(buildReleaseKeyStroke(touches.LEFT_MOUSE)) || 
				ks.equals(buildReleaseKeyStroke(touches.MIDDLE_MOUSE)) || ks.equals(buildReleaseKeyStroke(touches.RIGHT_MOUSE)) ;
		if(isMouseInput|| isReleasedMouseInput)
			touches.mapMouse.put(ks, a);
		else
			comp.getInputMap(IFW).put(KeyStroke.getKeyStroke(ks), a);
	}
	
	public void actionMapPut(String a)
	{
		if(a.equals(MOVE_RIGHT )) {comp.getActionMap().put(a, new MoveAction(1,false));}
		else if(a.equals(MOVE_LEFT)) {comp.getActionMap().put(a, new MoveAction(-1,false));}
		else if(a.equals(DASH)) {comp.getActionMap().put(a, new DashAction(false));}
		else if(a.equals(SLOW)) {comp.getActionMap().put(a, new SlowAction(false));}
		else if(Touches.inArray(a,SHOOT)) {comp.getActionMap().put(a, new ShootAction(Touches.indexOf(a,SHOOT),false));}
		else if(Touches.inArray(a,SLOT)) {comp.getActionMap().put(a, new SlotAction(Touches.indexOf(a,SLOT),false));}
		else if(a.equals(JUMP)) {comp.getActionMap().put(a, new JumpAction(false));}
		else if(a.equals(PAUSE)) {comp.getActionMap().put(a, new PauseAction(false));}


		else if(a.equals(R_MOVE_RIGHT )){comp.getActionMap().put(a, new MoveAction(1,true));}
		else if(a.equals(R_MOVE_LEFT)) {comp.getActionMap().put(a, new MoveAction(-1,true));}
		else if(a.equals(R_DASH)) {comp.getActionMap().put(a, new DashAction(true));}
		else if(a.equals(R_SLOW)) {comp.getActionMap().put(a, new SlowAction(true));}
		else if(Touches.inArray(a,R_SHOOT)) {comp.getActionMap().put(a, new ShootAction(Touches.indexOf(a,R_SHOOT),true));}
		else if(Touches.inArray(a,R_SLOT)) {comp.getActionMap().put(a, new SlotAction(Touches.indexOf(a,R_SLOT),true));}
		else if(a.equals(R_JUMP)) {comp.getActionMap().put(a, new JumpAction(true));}
		else if(a.equals(R_PAUSE)) {comp.getActionMap().put(a, new PauseAction(true));}

		else
		{
			try {throw new Exception("actionMap "+a+" not handled in actionMapPut");} catch (Exception e) {e.printStackTrace();}
		}
	}
	public void applyMouseInput(String mouseInput)
	{
		LinkedHashMap<String,String> mapMouse = touches.mapMouse;
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
		LinkedHashMap<String,String> mapMouse = touches.mapMouse;
		boolean isMouseInput = oldKey.equals(touches.LEFT_MOUSE) || oldKey.equals(touches.MIDDLE_MOUSE) || oldKey.equals(touches.RIGHT_MOUSE) ;
		boolean isReleasedMouseInput = oldKey.equals(buildReleaseKeyStroke(touches.LEFT_MOUSE)) || 
				oldKey.equals(buildReleaseKeyStroke(touches.MIDDLE_MOUSE)) || oldKey.equals(buildReleaseKeyStroke(touches.RIGHT_MOUSE)) ;
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

		private static final long serialVersionUID = 1L;
		int direction;
		boolean isReleased;
		MoveAction(int direction,boolean isReleased) {
			// -1 for left, 1 for right
			this.direction = direction;
			this.isReleased=isReleased;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean doubleTapped = false;
			if(isReleased)
				releaseMoveTime = System.nanoTime();
			else{
				pressedMoveTime = System.nanoTime();
				float delta = (float) ((pressedMoveTime-releaseMoveTime)*Math.pow(10, -6));
				doubleTapped = (last_direction==direction) && (delta<InterfaceConstantes.T_DOUBLE_TAP);
				last_direction=direction;
			}
			
			for(InputPartieListener lis : listeners)
				lis.onMove(direction, doubleTapped,isReleased);

		}
	}
	private class DashAction extends AbstractAction{
		boolean isReleased;
		public DashAction(boolean isReleased){this.isReleased=isReleased;}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			for(InputPartieListener lis : listeners)
				lis.onDash(isReleased);
		}
		
	}
	private class SlowAction extends AbstractAction{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		boolean isReleased;
		SlowAction(boolean isReleased){this.isReleased = isReleased;}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			for(InputPartieListener lis : listeners)
				lis.onSlow(isReleased);
		}}
	private class ShootAction extends AbstractAction{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		boolean isReleased;
		private int type_tir=-1;//0 normal, 1-4 special
		ShootAction(int _type,boolean isReleased){type_tir=_type;this.isReleased = isReleased;};
		@Override
		public void actionPerformed(ActionEvent arg0) {
			for(InputPartieListener lis : listeners)
				lis.onShoot(type_tir,isReleased);
		}}
	private class SlotAction extends AbstractAction{
		private int type =-1; //0->3 
		boolean isReleased;
		SlotAction(int _type,boolean isReleased){type = _type;this.isReleased=isReleased;}
		public void actionPerformed(ActionEvent arg0) {
			for(InputPartieListener lis : listeners)
				lis.onChangeSlot(type,isReleased);
		}}
	private class JumpAction extends AbstractAction{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		boolean isReleased;
		JumpAction(boolean isReleased){this.isReleased=isReleased;};
		@Override
		public void actionPerformed(ActionEvent arg0) {
			for(InputPartieListener lis : listeners)
				lis.onJump(isReleased);

		}}
	private class PauseAction extends AbstractAction{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		boolean isReleased;
		PauseAction(boolean isReleased){this.isReleased=isReleased;};
		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.out.println("Pause action");
			for(InputPartieListener lis : listeners)
				lis.onPause(isReleased);
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
