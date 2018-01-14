package Affichage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;

import types.Hitbox;

public class DrawImageItem {
	
	Image img =null;
	Polygon poly;
	String s =null;
	int x; 
	int[] x_bar;
	int y;
	int[] y_bar;
	int[] width_bar;
	int[] height_bar;
	Color[] color_bar ; 
	AffineTransform tr=null;
	ImageObserver observer=null;
	int layerIndex ;
	public enum Type_Item{Image,Transform_Image,String,Polygon,Bar};
	Type_Item item = Type_Item.Image;
	Color originalColor =null; 
	Color newColor=null;
	int fontsize;
	
	//g.drawImage(img,x,y,oberserver)
	public DrawImageItem(Image _img, int _x,int _y , ImageObserver _observer, int _layerIndex)
	{
		img=_img;
		x=_x;
		y=_y;
		observer=_observer;
		layerIndex=_layerIndex;
		item = Type_Item.Image;
	}
	
	//g.drawImage(img,AffineTransform,oberserver)
	public DrawImageItem(Image _img, AffineTransform _tr , ImageObserver _observer, int _layerIndex)
	{
		img=_img;
		tr=_tr;
		observer=_observer;
		layerIndex=_layerIndex;
		item = Type_Item.Transform_Image;
	}
	
	//g.drawHitbox(poly)
	public DrawImageItem(Hitbox _hit, Point screenDisp,Color _newColor, Color _originalColor, int _layerIndex)
	{
		poly=Hitbox.plusPoint(_hit,screenDisp,true).polygon;
		newColor=_newColor;
		originalColor =_originalColor;
		layerIndex=_layerIndex;
		item = Type_Item.Polygon;
	}
	
	//g.drawPolygon(poly)
	public DrawImageItem(Polygon _poly,Color _newColor, Color _originalColor, int _layerIndex)
	{
		poly=_poly;
		newColor=_newColor;
		originalColor =_originalColor;
		layerIndex=_layerIndex;
		item = Type_Item.Polygon;
	}
	
	//g.drawString (s,x,y)
	public DrawImageItem(String _s, int _x, int _y, Color _newColor, Color _originalColor,int _fontsize, int _layerIndex)
	{
		s=_s;
		x=_x;
		y=_y; 
		layerIndex=_layerIndex;
		newColor=_newColor;
		originalColor =_originalColor;
		fontsize=_fontsize;
		item = Type_Item.String;
	}
	
	//g.drawbar 
	public DrawImageItem(int[] _x, int[] _y, int[] _width, int[] _height,Color[] _colors, int _layerIndex)
	{
		x_bar=_x;
		y_bar=_y;
		width_bar=_width;
		height_bar=_height;
		color_bar =_colors; 
		layerIndex=_layerIndex;

		item = Type_Item.Bar;

	}
	public void draw(Graphics g)
	{
		if (Type_Item.Image.equals(item))
		{
			g.drawImage(img, x,y,observer);
		}
		else if (Type_Item.Transform_Image.equals(item))
		{
			Graphics2D g2d = (Graphics2D)g;
			g2d.drawImage(img,tr,observer);
		}
		else if (Type_Item.String.equals(item))
		{
			if(newColor != null)
				g.setColor(newColor);
			if(fontsize != 0)
				g.setFont(new Font(g.getFont().getFontName(),g.getFont().getStyle(), fontsize ));
			g.drawString(s, x, y);
			if(originalColor != null)
				g.setColor(originalColor);
		}
		else if (Type_Item.Polygon.equals(item))
		{
			if(newColor != null)
				g.setColor(newColor);
			//make the hitbox relative to the screen
			g.drawPolygon(poly);
			if(originalColor != null)
				g.setColor(originalColor);		
		}
		else if (Type_Item.Bar.equals(item))
		{
			for(int i=0; i< x_bar.length; i++)
			{
				if(width_bar[i]>0){
					if(color_bar[i]!=null)
						g.setColor(color_bar[i]);
					g.fillRect(x_bar[i], y_bar[i], width_bar[i], height_bar[i]);}
			}
		}
		
	}
	
}
