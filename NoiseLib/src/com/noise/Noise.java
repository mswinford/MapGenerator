package com.noise;
/**
 *
 * @author Michael
 */
public abstract class Noise {
    protected double frequency = 1.0d;
    protected double amplitude = 1.0d;
    protected double lacunarity = 2.0d;
    protected double persistence = 0.75d;
    protected int octaves = 5;
    protected int seed = 100000;
    
    public abstract double noise(double x);
    public abstract double noise2D(double x, double y);
    //abstract double noise3D(double x, double y, double z);
    
    protected int fastFloor(double x){
        return x>=0 ? (int)x : (int)x-1;
    }
    
    protected double dot(double[] g, double x){
        return g[0]*x;
    }
    
    protected double dot(double[] g, double x, double y){
        return g[0]*x + g[1]*y;
    }
    
    protected double dot(double[] g, double x, double y, double z){
        return g[0]*x + g[1]*y + g[2]*z;
    }
    
    protected double lerp(double a, double b, double w){
        return a*(1-w) + b*w;
    }
    
    protected double sCurve(double w){
        return w*w*w*(w*(w*6-15)+10);
    }
    
    public void setFrequency(double freq){
        this.frequency = freq;
    }
    
    public void setAmplitude(double amp){
        this.amplitude = amp;
    }
    
    public void setLacunarity(double lac){
        this.lacunarity = lac;
    }
    
    public void setPersistence(double p){
        this.persistence = p;
    }
    
    public void setOctaves(int octs){
        this.octaves = octs;
    }
    
    public void setSeed(int seed){
        this.seed = seed;
    }
    
    public double getFrequency(){
        return frequency;
    }
    
    public double setAmplitude(){
        return amplitude;
    }
    
    public double getLacunarity(){
        return lacunarity;
    }
    
    public double getPersistence(){
        return persistence;
    }
    
    public double getOctaves(){
        return octaves;
    }
    
    public int getSeed(){
        return seed;
    }
}
