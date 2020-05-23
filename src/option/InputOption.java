package option;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import partie.input.InputPartie;

public class InputOption {
	private AbstractControlerOption controlerOption;
	//actionmap
	//the action map has the name of the input 
	private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
	private InputPartie inputPartie;
	
	public InputOption(AbstractControlerOption _cont, InputPartie _inputPartie)
	{controlerOption = _cont;inputPartie=_inputPartie;}
	
	public void init(JComponent comp)
	{
		String alpha= "A/B/C/D/E/F/G/H/I/J/K/L/M/N/O/P/Q/R/S/T/U/V/W/X/Y/Z";
		String num  ="0/1/2/3/4/5/6/7/8/9";
		String f_keys ="F1/F2/F3/F4/F5/F6/F7/F8/F9/F10/F11/F12";
		String arrows ="UP/DOWN/LEFT/RIGHT";
		//shift | control | ctrl | meta | alt | altGraph
		String special_keys ="released SHIFT/released CONTROL/released ALT/released ALT_GRAPH";
		String others = "SPACE/ESCAPE/ENTER/BACK_SPACE";
		String modifiers = "shift/ctrl/alt";
		
		String all = alpha +"/"+ num + "/"+f_keys +"/"+arrows+"/"+ special_keys +"/"+ others;
		for(String s : all.split("/"))
		{
			inputActionMapPut(comp,s);
		}
		for(String modif : modifiers.split("/"))
		{
			for(String alph : alpha.split("/"))
			{
				inputActionMapPut(comp,(modif+" released "+alph));
			}
		}
	}
	
	public void reset(JComponent comp)
	{
		ArrayList<Object> actMap = new ArrayList<Object>();
		for( KeyStroke k : comp.getInputMap(IFW).keys())
		{
			actMap.add(comp.getInputMap(IFW).get(k));
			comp.getInputMap(IFW).remove(k);
		}
		for(Object am : actMap)
		{
			comp.getActionMap().remove(am);
		}
	}
	public void inputActionMapPut(JComponent comp, String ks)
	{
		comp.getInputMap(IFW).put(KeyStroke.getKeyStroke(ks), ks);
		comp.getActionMap().put(ks, new ChangeKeyAction(ks));
	}
	
	private class ChangeKeyAction extends AbstractAction
	{
		String key ="";
		ChangeKeyAction(String _key){key=_key;}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//Comme la touche a ete modifiée, plus aucune case n'est selectionne
			controlerOption.opt.setModifTouches(simplifyKey(key),inputPartie);
			controlerOption.opt.setCaseFocus(false);
			//On arrete le clignotement de la case
			controlerOption.opt.blinkCustomClickableLabel();
			//on retire la case memorise
			controlerOption.controlCustomClickableLabel(null);
			
		}}
	//remove released from the key string
	private String simplifyKey(String key)
	{
		return key.replaceAll("released ", "");
	}
}
