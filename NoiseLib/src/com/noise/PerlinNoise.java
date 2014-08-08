package com.noise;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Michael
 */
public class PerlinNoise extends Noise{
    private static double[][] grad2 = new double[8][2];
    static{ 
        for(int i=0; i<8; i++){
            grad2[i][0] = Math.cos((Math.PI / 4) * (double)i);
            grad2[i][1] = Math.sin((Math.PI / 4) * (double)i);
        }
    }
    
    
    private static final int[] p = {151,160,137,91,90,15,
    131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
    190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
    88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166,
    77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
    102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196,
    135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123,
    5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,
    223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9,
    129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228,
    251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,
    49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254,
    138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180};
    
    private static int[] perm = new int[512];
    static{ 
        for(int i=0; i<512; i++){
                perm[i]=p[i & 255];
            } 
    }  
    
    double perlinNoise1D(double x){
        int intX = fastFloor(x);
        double dx = x - intX;
        
        int g0 = perm[intX & 255] & 7;
        int g1 = perm[(intX + 1) & 255] & 7;
        
        double n0 = dot(grad2[g0], dx);
        double n1 = dot(grad2[g1], dx-1);
        
        double nx = lerp(n0, n1, sCurve(dx));
        
        return nx;
    }
    
    @Override
    public double noise(double x){
        double total = 0.0;
        double a = amplitude;
        double f = frequency;
        
        for(int i=0; i<octaves; i++){
            f *= lacunarity;
            a *= persistence;
            total += perlinNoise1D(x*f) * a;
        }
        
        //total /= Math.sqrt(2);
        
        return total;
    }
    
    double perlinNoise2D(double x, double y){
        int intX = fastFloor(x);
        int intY = fastFloor(y);
        
        double dx = x - intX;
        double dy = y - intY;
        
        int g00 = perm[(intX + perm[intY & 255]) & 255] & 7;
        int g01 = perm[(intX + perm[(intY+1) & 255]) & 255] & 7;
        int g10 = perm[(intX + 1 + perm[intY & 255]) & 255] & 7;
        int g11 = perm[(intX + 1 + perm[(intY + 1) & 255]) & 255] & 7;
        
        double n00 = dot(grad2[g00], dx, dy);
        double n01 = dot(grad2[g01], dx, dy-1);
        double n10 = dot(grad2[g10], dx-1, dy);
        double n11 = dot(grad2[g11], dx-1, dy-1);
        
        double nx0 = lerp(n00, n10, sCurve(dx));
        double nx1 = lerp(n01, n11, sCurve(dx));
        
        double nxy = lerp(nx0, nx1, sCurve(dy));
        
        return nxy;
    }
    
    @Override
    public double noise2D(double x, double y){
        double total = 0.0;
        double a = amplitude;
        double f = frequency;
        
        for(int i=0; i<octaves; i++){
            f *= lacunarity;
            a *= persistence;
            total += perlinNoise2D(x*f, y*f) * a;
        }
        
        //total /= Math.sqrt(2);
        
        return total;
    }
    
//    public static void main(String[] args){
//        //PerlinNoise p = new PerlinNoise();
//        for(double x=0; x<20; x++){
//            for(double y=0; y<20; y++){
//                //double noise = PerlinNoise.noise2D(x, y);
//                //System.out.printf("%8.3f", noise);
//            }
//        }
//    }
}
