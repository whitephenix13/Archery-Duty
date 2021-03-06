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

import partie.collision.Hitbox;

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
	//line
	Point p1; //Use p1 for oval size
	Point p2;
	int layerIndex ;
	public enum Type_Item{Image,Transform_Image,String,Polygon,Bar,FillPoly,Line,Oval};
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
		poly=_hit.copy().translate(screenDisp).polygon;
		newColor=_newColor;
		originalColor =_originalColor;
		layerIndex=_layerIndex;
		item = Type_Item.Polygon;
	}
	
	
	//g.drawPolygon(poly)/g.fillPolygon(poly)
	public DrawImageItem(Polygon _poly,Color _newColor, Color _originalColor,boolean filled, int _layerIndex)
	{
		poly=_poly;
		newColor=_newColor;
		originalColor =_originalColor;
		layerIndex=_layerIndex;
		item = filled? Type_Item.FillPoly : Type_Item.Polygon;
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
	
	//g.drawline 
	public DrawImageItem(Point _p1, Point _p2, Color _newColor, Color _originalColor, int _layerIndex)
	{
		p1=_p1;
		p2=_p2;
		newColor=_newColor;
		originalColor =_originalColor;
		layerIndex=_layerIndex;

		item = Type_Item.Line;

	}
	//For oval
	public DrawImageItem(int _x,int _y ,Point _p1, Color _newColor, Color _originalColor, int _layerIndex)
	{
		x=_x;
		y=_y;
		p1=_p1;
		newColor=_newColor;
		originalColor =_originalColor;
		layerIndex=_layerIndex;

		item = Type_Item.Oval;

	}
	public void draw(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		if (Type_Item.Image.equals(item))
		{
			g.drawImage(img, x,y,observer);
		}
		else if (Type_Item.Transform_Image.equals(item))
		{
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
		else if (Type_Item.Polygon.equals(item) || Type_Item.FillPoly.equals(item))
		{
			if(newColor != null)
				g.setColor(newColor);
			//make the hitbox relative to the screen
			if(Type_Item.FillPoly.equals(item))
				g.fillPolygon(poly);
			else
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
		else if (Type_Item.Line.equals(item))
		{
			if(newColor != null)
				g.setColor(newColor);
			g.drawLine(p1.x, p1.y,p2.x,p2.y);
			if(originalColor != null)
				g.setColor(originalColor);
		}
		else if(Type_Item.Oval.equals(item)){
			if(newColor != null)
				g.setColor(newColor);
			//x and y are the center of the object. 
			//in the drawOval function, they are the top left of the oval. 
			//=> translate by width/2 and height/2
			g.drawOval(x-p1.x/2,y-p1.y/2,p1.x,p1.y);
			if(originalColor != null)
				g.setColor(originalColor);
		}
	}
	
}
