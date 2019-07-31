package partie.modelPartie;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import ActiveJComponent.ActiveEmptyBorder;
import ActiveJComponent.ActiveJButton;
import gameConfig.ObjectTypeHelper.ObjectType;
import option.Config;
import option.Touches;
import partie.projectile.fleches.Fleche;

public class ArrowSlotButton extends ActiveJButton{

	public int slot = -1 ; 
	public int original_position = -1; //original position in the list so that when all the arrows from the slot are shown, they are always in the same order
	public boolean isSelected = false;
	public ObjectType arrowType = null; //name of the arrow in the heros slot
	
	final static int xShiftStartHotKey = -10;
	final static int yShiftStartHotKey = -14;
	final static int xSizeHotKey = 10;
	final static int ySizeHotKey = 10;
	Touches touches;
	
	ArrowSlotButton(Touches touches, int _slot)
	{
		super();
		this.removeMouseListener(this.getMouseListeners()[0]);
		
		this.touches=touches;
		slot=_slot;
		isSelected = original_position==0;
		this.setContentAreaFilled(false);
		this.setBorder(new ActiveEmptyBorder(0,5,5,0));
	}
	
	private void setOriginalPosition()
	{
		ObjectType[][] allArrowType = {Fleche.DESTRUCTRICE_CLASS,Fleche.MATERIELLE_CLASS,Fleche.RUSEE_CLASS,Fleche.SPRIRITUELLE_CLASS};
		for(int i=0; i<allArrowType.length;++i)
			for(int j=0; j<allArrowType[i].length;++j)
				if(arrowType.equals(allArrowType[i][j]))
					original_position=j;
	}

	public void AddBorderSize(int top, int left, int bottom, int right)
	{
		((ActiveEmptyBorder)getBorder()).addBorder(top, left, bottom, right);
	}
	public void setIcons(Image im)
	{
		ImageIcon icon =  new ImageIcon(im);
		setIcons(icon);
	}
	public void setIcons(Icon icon)
	{
		this.setIcon(icon);
		this.setRolloverIcon(icon);
		this.setPressedIcon(icon);
	}
	public static ArrowSlotButton[] setIcons(ArrowSlotButton[] buttons, Image[] images)
	{
		for(int i=0; i<buttons.length;++i)
		{
			buttons[i].setIcons(images[i]);
		}
		return buttons;
	}
	public static void setArrowType (ArrowSlotButton[] buttons, ObjectType[] names)
	{
		for(int i=0; i<buttons.length;++i)
		{
			buttons[i].arrowType=names[i];
			buttons[i].setOriginalPosition();
		}
	}
	public static void switchButtonType (ArrowSlotButton button1, ArrowSlotButton button2)
	{
		Icon temp_icon = button1.getIcon();
		ObjectType temp_type = button1.arrowType;
		boolean temp_vis = button1.isActiveVisible();
		boolean temp_ena = button1.isEnabled();
		int temp_original_pos = button1.original_position;
		boolean temp_is_selected = button1.isSelected;
		
		button1.setIcons(button2.getIcon());
		button1.arrowType = button2.arrowType;
		button1.setVisible(button2.isActiveVisible());
		button1.setEnabled(button2.isEnabled());
		button1.original_position = button2.original_position;
		button1.isSelected = button2.isSelected;
		
		button2.setIcons(temp_icon);
		button2.arrowType=temp_type;
		button2.setVisible(temp_vis);
		button2.setEnabled(temp_ena);
		button2.original_position = temp_original_pos;
		button2.isSelected = temp_is_selected;

	}
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		
		if(this.isActiveVisible() && isSelected && Config.showHotkeyWhenPlaying){
			g.setColor(new Color(255,255,255,200));
			g.fillRect(getBounds().width+xShiftStartHotKey, getBounds().height+yShiftStartHotKey, xSizeHotKey, ySizeHotKey);
			g.setColor(Color.black);
			g.drawString(touches.t_tir[slot+1], getBounds().width+xShiftStartHotKey+2, getBounds().height-4);//4 for size of the letter
		}
	}
}
