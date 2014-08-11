package com.noise;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Michael
 */
import java.io.*;
import java.awt.Color;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;

public class ValueNoise extends Noise{

    public ValueNoise(){
    }
    
    private double whiteNoise(int x){
        x = (x * 1619 + seed * 1013) & 0x7fffffff;
        x = (x<<13) ^ x;
        return ( 1.0 - ( (x * (x * x * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0);
    }
    
    private double whiteNoise(int x, int y){
        int n = (x * 1619 + y * 31337 + seed * 1013) & 0x7fffffff;
        n = (n<<13) ^ n;
        return ( 1.0 - ( (n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0);
    }
    
    private double whiteNoise(int x, int y, int z){
        int n = (x * 1619 + y * 31337 + z * 6971 + seed * 1013) & 0x7fffffff;
        n = (n<<13) ^ n;
        return ( 1.0 - ( (n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0);
    }
    
    private double smoothNoise(int x){
        return whiteNoise(x)/4 + whiteNoise(x-1)/8 + whiteNoise(x+1)/8;
    }
    
    private double smoothNoise(int x, int y){
        double corners = (whiteNoise(x-1, y-1) + whiteNoise(x+1, y-1) + whiteNoise(x-1, y+1) + whiteNoise(x+1, y+1)) / 16;
        double sides = (whiteNoise(x-1, y) + whiteNoise(x+1, y) + whiteNoise(x, y-1) + whiteNoise(x, y+1)) / 8;
        double center = whiteNoise(x, y) / 4;
        return corners + sides + center;
    }
    
    //get noise value from corners of 3d cube and the edge centers of the front and back faces
    private double smoothNoise(int x, int y, int z){
        double corners = (whiteNoise(x-1, y-1, z-1) + whiteNoise(x+1, y-1, z-1) + whiteNoise(x-1, y+1, z-1) + whiteNoise(x+1, y+1, z-1) 
                + whiteNoise(x-1, y-1, z+1) + whiteNoise(x+1, y-1, z+1) + whiteNoise(x-1, y+1, z+1) + whiteNoise(x+1, y+1, z+1)) / 16;
        double sides = (whiteNoise(x-1, y, z-1) + whiteNoise(x+1, y, z-1) + whiteNoise(x, y-1, z-1) + whiteNoise(x, y+1, z-1) 
                + whiteNoise(x-1, y, z+1) + whiteNoise(x+1, y+1, z+1) + whiteNoise(x, y-1, z+1) + whiteNoise(x, y+1, z+1)) / 8;
        double center = whiteNoise(x, y, z) / 4;
        return corners + sides + center;
    }
    
    private double interpolatedNoise(double x){
        int  intX = (int)x;
        double weight = x - intX;
        
        double v1 = smoothNoise(intX);
        double v2 = smoothNoise(intX + 1);
        
        return lerp(v1, v2, sCurve(weight));
    }
    
    private double interpolatedNoise(double x, double y){
        int intX = (int)x;
        double weightX = x - intX;
        int intY = (int)y;
        double weightY = y - intY;
        
        double v1 = smoothNoise(intX, intY);
        double v2 = smoothNoise(intX+1, intY);
        double v3 = smoothNoise(intX, intY+1);
        double v4 = smoothNoise(intX+1, intY+1);
        
        double i1 = lerp(v1, v2, sCurve(weightX));
        double i2 = lerp(v3, v4, sCurve(weightX));
        
        return lerp(i1, i2, sCurve(weightY));
    }
    
    private double interpolatedNoise(double x, double y, double z){
        int intX = fastFloor(x);
        double weightX = x - intX;
        int intY = fastFloor(y);
        double weightY = y - intY;
        int intZ = fastFloor(z);
        double weightZ = z - intZ;
        
        double n000 = smoothNoise(intX, intY, intZ);
        double n100 = smoothNoise(intX+1, intY, intZ);
        double n010 = smoothNoise(intX, intY+1, intZ);
        double n001 = smoothNoise(intX, intY, intZ+1);
        double n110 = smoothNoise(intX+1, intY+1, intZ);
        double n101 = smoothNoise(intX+1, intY, intZ+1);
        double n011 = smoothNoise(intX, intY+1, intZ+1);
        double n111 = smoothNoise(intX+1, intY+1, intZ+1);
        
        double nx00 = lerp(n000, n100, sCurve(weightX));
        double nx10 = lerp(n010, n110, sCurve(weightX));
        double nx01 = lerp(n001, n101, sCurve(weightX));
        double nx11 = lerp(n011, n111, sCurve(weightX));
        
        double nxy0 = lerp(nx00, nx10, sCurve(weightY));
        double nxy1 = lerp(nx01, nx11, sCurve(weightY));
        
        return lerp(nxy0, nxy1, sCurve(weightZ));
    }
    
    @Override
    public double noise(double x){
        double total = 0.0;
        double a = amplitude;
        double f = frequency;
        
        for(int i=0; i<octaves; i++){
            f *= lacunarity;
            a *= persistence;
            total += interpolatedNoise(x*f) * a;
        }
        
        return total;
    }
    
    @Override
    public double noise2D(double x, double y){
        double total = 0.0;
        double a = amplitude;
        double f = frequency;
        
        for(int i=0; i<octaves; i++){
            f *= lacunarity;
            a *= persistence;
            total += interpolatedNoise(x*f, y*f) * a;
        }
        
        return total;
    }
    
    double noise3D(double x, double y, double z){
        double total = 0.0;
        double a = amplitude;
        double f = frequency;
        
        for(int i=0; i<octaves; i++){
            f *= lacunarity;
            a *= persistence;
            total += interpolatedNoise(x*f, y*f, z*f) * a;
        }
        
        return total;
    }
    
    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) {
//        Frame f = new Frame();
//    }
}
class Frame extends JFrame{
	private static final long serialVersionUID = -7352169016020423945L;
	ValueNoise n;
    
    public Frame(){
        n = new ValueNoise();
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setTitle("noise map");
        this.setBackground(Color.WHITE);
        this.setSize(new java.awt.Dimension(1024, 600));
        this.setVisible(true);
    }
    
    @Override
    public void paint(Graphics g){
        if(n != null){
            createImages2(g);
            //drawImages(g);
            //Graphics2D g2 = (Graphics2D) g;
            //g2.setColor(Color.RED);
            /*
            for(double x=0; x<this.getWidth(); x+=0.05){
                double noise = n.perlinNoise1D(x);
                //System.out.println(noise);
                g2.draw(new Line2D.Double(x, noise*20d+500d, x, noise*20d+500d));
            }
            */
//            for(double z=1; z<2; z++){ System.out.println(z);
//            for(double x=0; x<this.getWidth(); x++){
//                for(double y=0; y<this.getHeight(); y++){
//                    double noise = n.valueNoise3D(x/100, y/56, z/500);
//                    //double noise = PerlinNoise.noise(x/900, y/900);
//                    //double noise = SimplexNoise.noise((x+(n.seed*this.getWidth()))/1000, (y+(n.seed*this.getHeight()))/800);
//                    //System.out.println(noise);
//                    g2.setColor(getColor(noise));
//                    g2.draw(new Line2D.Double(x, y, x, y));
//                }
//            }
//            try{ Thread.sleep(500); }
//            catch(InterruptedException e){ }
//            }
            
        }
    }
    
    Color getColor(double noise){
        int rgb = (int)(noise * 128d + 128d);
        if(rgb <= 100){ return Color.BLUE; }
        else if(rgb > 100 && rgb <= 190){ return Color.GREEN; }
        else if(rgb > 190 && rgb <= 215){ return Color.ORANGE; }
        else if(rgb > 215 && rgb <= 245){ return Color.GRAY; }
        else{ return Color.WHITE; }
        
        
         
    }
    
    void createImages(Graphics g){
        int numOfImages = (int)Math.ceil(this.getWidth()/256) * (int)Math.ceil(this.getHeight()/256);
        String filename = "ValueNoiseImg";
        int xCoor, yCoor;
        for(int i=0; i<numOfImages; i++){
            File f = new File(filename + i);
            BufferedImage im = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
            for(int x=0; x<im.getWidth(); x++){
                for(int y=0; y<im.getHeight(); y++){
                    xCoor = (x + 256 * (i%(int)(this.getWidth()/256)));
                    yCoor = (y + 256 * (i/(int)(this.getHeight()/256)));
                    double noise = n.noise3D((double)xCoor/(25*(this.getWidth()/256)), (double)yCoor/(25*(this.getHeight()/256)), 0) * 128d + 128d;
                    im.setRGB((int)x, (int)y, getRGB(noise));
                }
            }
            try{ ImageIO.write(im, "PNG", f); }
            catch(IOException e){ System.out.println("fuck"); }
        }
    }
    
    void createImages2(Graphics g){
        File f = new File("ValueNoiseImg.png");
        BufferedImage im = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        for(int x=0; x<im.getWidth(); x++){
                for(int y=0; y<im.getHeight(); y++){
                    double xRatio = (double)this.getWidth()/(double)this.getHeight();
                    double yRatio = 1d/xRatio;
                    double noise = n.noise3D((double)x * 20d/im.getWidth(), (double)y * (20d*yRatio)/im.getHeight(), 0) * 128d + 128d;
                    im.setRGB((int)x, (int)y, getRGB(noise));
                }
        }
        try{ ImageIO.write(im, "PNG", f); }
        catch(IOException e){ System.out.println("fuck"); }
    }
    
    int getRGB(double noise){
        int rgb = (int)noise;
        
        if(rgb <= 100){ return Color.BLUE.getRGB(); }
        else if(rgb > 100 && rgb <= 190){ return Color.GREEN.getRGB(); }
        else if(rgb > 190 && rgb <= 215){ return Color.ORANGE.getRGB(); }
        else if(rgb > 215 && rgb <= 245){ return Color.GRAY.getRGB(); }
        else{ return Color.WHITE.getRGB(); }
    }
    
    void drawImages(Graphics g){
        int numOfImages = (int)Math.ceil(this.getWidth()/256) * (int)Math.ceil(this.getHeight()/256);
        if(g == null || numOfImages <= 0){ return; }
        String filename = "ValueNoiseImg";
        Image im = null;
        for(int i=0; i<numOfImages; i++){
            try{ im = ImageIO.read(new File(filename + i)); }
            catch(IOException e){ }
            g.drawImage(im, 256 * (i%(int)(this.getWidth()/256)), 256 * (i/(int)(this.getHeight()/256)), null);
        }
    }
}