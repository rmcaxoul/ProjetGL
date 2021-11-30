/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ensimag.trigo;

/**
 *
 * @author ensimag
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

import javax.swing.JComponent;

public class CourbeArc extends JComponent {

    private static final long serialVersionUID = 7800853645256601960L;

    private CurveFunction function;
    private CurveFunction function2;
    
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        
        // --- White background ---
        graphics.setColor( Color.WHITE );
        graphics.fillRect( 0, 0, getWidth(), getHeight() );
        
        // --- Draw axis ---
        graphics.setColor( Color.GRAY );
        graphics.drawLine( 0, getHeight()/2, getWidth(), getHeight()/2 );
        graphics.drawLine( getWidth()/2, 0, getWidth()/2, getHeight() );
        int min = -4; //-1 pour asin
        int max = 4; // 1
        // --- Draw values ---
        graphics.setColor( Color.BLACK );
        graphics.drawString( "0,0", (int)(getWidth()*0.51), (int)(getHeight()*0.54));
        graphics.drawString( String.valueOf(min), (int)(getWidth()*0.02), (int)(getHeight()*0.54));
        graphics.drawString( String.valueOf(max), (int)(getWidth()*0.96), (int)(getHeight()*0.54));
        
        
        // --- Draw curve ---
        double step = 0.1;
        Graphics2D graphics2 = (Graphics2D)graphics;
        graphics2.setStroke(new BasicStroke(3));
        
        int oldX = xToPixel( min );
        int oldY = yToPixel( function.compute( min ) );
        
        for( double lx=min+step; lx<= max+step; lx+=step ) {
            int x = xToPixel( lx );
            int y = yToPixel( function.compute( lx ) );
            graphics.setColor( new Color( 255, 0, 0 ) );
            graphics.drawLine( x, y, oldX, oldY );
            
            oldX = x;
            oldY = y;
        }
        
        int oldX2 = xToPixel( min );
        int oldY2 = yToPixel( function2.compute( min ) );
        
        for( double lx=min+step; lx<= max+step; lx+=step ) {
            int x = xToPixel( lx );
            int y = yToPixel( function2.compute( lx ) );
            graphics.setColor( new Color( 0, 0, 255 ) );
            graphics.drawLine( x, y, oldX2, oldY2 );
            
            oldX2 = x;
            oldY2 = y;
        }
    }
    
    public void setFunction(CurveFunction function) {
        this.function = function;
        this.repaint();
    }
    public void setFunction2(CurveFunction function) {
        this.function2 = function;
        this.repaint();
    }
    
    private int xToPixel( double x ) {
        return (int)( getWidth() * (x + 4)/8 ); //pour atan
        //return (int)( getWidth() * (x + 1)/2 ); //pour asin
    }

    private int yToPixel( double y ) {
        return (int)( getHeight() * (1 - (y + 1.32)/2.64) ); //pour atan
        //return (int)( getHeight() * (1 - (y + Math.PI/2)/Math.PI) ); //pour asin 
    }

    
    public static interface CurveFunction {
        
        public double compute( double x );
        
    }
    
}