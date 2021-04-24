package _util;

import java.util.*;   
import java.text.*;

/**  
 *Reference   
 *"A Kind of Improved Method of Fuzzy Clustering"  
 *"A validity measure for fuzzy clustering"  
 *  
 *Creation Date:06-5-8 14:39  
 */   
public class _FuzzyCmeans {
    private int fsize;  			//feature size    
    private int ssize;  			//sample size    
    private int clsNum; 			//cluster number    
    private double s;  	 			//coefficient in FCM 
    private double[][] samples;   
    private double[][] centroids;   
    private double[][] u;   		//relationship matrix    
    private double[][] ou;  		//old relationship matrix    
    private double E = 0.0001;   	//threshold of termination condition    
       
    private double[][] optu;    	//optimal relationship due to optimal cluster number    
    private int optimalC = 1;   
    private double oldconver;   
    private double convergence;   
    private double CE = 1;  		//threshold of convergence    
    private int MAXROUND = 2000;  	//max rounds for computation    
    
    public _FuzzyCmeans(double[][] data, int c, int ss) { 
        ssize = data.length;
        fsize = data[0].length;
        clsNum = c;
        s = ss;
        initData(c);   
        samples = new double[ssize][fsize];
        for(int i = 0; i < data.length; i++)
            copyArray(data[i],samples[i]);             
    }   
       
    public void initData(int cl) {   
        ou = new double[ssize][cl];   
        u = new double[ssize][cl];   
        centroids = new double[cl][fsize];     
    }   
       
    public void initU() {   
        for(int i = 0; i < ssize; i++) {   
            double sum = 0;   
            for(int j = 0; j < clsNum-1; j++) {   
                u[i][j] = Math.random()*(1-sum);   
                sum += u[i][j];   
            }   
            if(sum > 1) System.err.println("Initization Error!");   
            u[i][clsNum-1] = 1-sum;    
        }   
    }   
       
    /**  
     * Tranditional implemention for FCM  
     */   
    public void clustering() {   
        int count = 0;   
        initU();   
        while(count < MAXROUND) {   
    //      System.out.print(".");    
            updateCenters();   
            for(int k = 0; k < ssize; k++) {   
                System.arraycopy(u[k],0,ou[k],0,u[k].length);   
                updateU(k);   
            }   
        // convergence termination test     
            convergenceTest();   
            if(Math.abs(oldconver-convergence) < CE)   
                break;   
            count++;   
        }   
    }   
       
    /**  
     * Modified implemention for FCM  
     * The termination condition is modified to convergence test  
     */   
    public void clustering(int c) {   
        int count = 0;   
        clsNum = c;   
        initData(c);   
        initU();   
        while(count < MAXROUND) {   
            System.out.print("|");   
            updateCenters();   
            for(int k = 0; k < ssize; k++) {   
                updateU(k);   
            }   
               
            convergenceTest();   
            if(Math.abs(oldconver-convergence) < CE)   
                break;   
               
            count++;   
               
        }          
        System.out.println();   
    }   
    /**  
     * find optimal cluster number from [cl_min, cl_max]  
     */    
    public void optimalCluster(int cl_min, int cl_max) {   
        double ES = Double.MAX_VALUE;   
        optimalC = 1;   
        for(int c = cl_min; c <= cl_max; c++) {   
            clustering(c);   
            double ts = 0;   
            if(s < 2) {   
                double min = Double.MAX_VALUE;   
                for(int i = 0; i < clsNum; i++)   
                    for(int j = 0; j < i; j++) {   
                        double temp = sdistance(centroids[i],centroids[j]);   
                        if(min > temp)   
                            min = temp;   
                    }   
                ts = convergence * 1.0 / (ssize * min);        
            }   
            else {   
                ts = evaluation();   
            }   
            System.out.println("cluster[" + c + "]: " + ts);   
            if(ES > ts) {   
                ES = ts;   
                optimalC = c;   
                optu = new double[ssize][c];   
                copy2DArray(u,optu);   
            }   
            System.gc();   
        }   
           
        System.out.println("ES:" + ES);   
        System.out.println("Optimal Cluster:" + optimalC + " in [" + cl_min + "," + cl_max +"]");   
    }   
       
    /**  
     */   
    public void optimalClustering(int cl_min, int cl_max) {   
           
        double preES = 0;          
        double ES = Double.MIN_VALUE;   
        optimalC = 1;   
        for(int c = cl_min; c <= cl_max; c++) {   
            clustering(c);   
            double ts = 0;   
            if(s < 2) {   
                double min = Double.MAX_VALUE;   
                for(int i = 0; i < clsNum; i++)   
                    for(int j = 0; j < i; j++) {   
                        double temp = sdistance(centroids[i],centroids[j]);   
                        if(min > temp)   
                            min = temp;   
                    }   
                ts = convergence * 1.0 / (ssize * min);        
            }   
            else {   
                double t = evaluation();   
                if(c == cl_min) ts = 0;   
                else ts = Math.abs(t - preES)/preES;   
                preES = t;   
            }   
            System.out.println("cluster[" + c + "]: " + preES);   
            if(ES < ts) {   
                ES = ts;   
                optimalC = c;   
                optu = new double[ssize][c];   
                copy2DArray(u,optu);   
            }   
            System.gc();   
        }   
           
        System.out.println("ES:" + ES);   
        System.out.println("Optimal Cluster:" + optimalC + " in [" + cl_min + "," + cl_max +"]");   
    }      
       
    public void convergenceTest() {   
        double jm = 0;   
        for(int i = 0; i < clsNum; i++) {   
            for(int j = 0; j < ssize; j++) {   
                jm += Math.pow(u[j][i], s) * sdistance(samples[j],centroids[i]);   
            }   
        }   
        oldconver = convergence;   
        convergence = jm;   
    }   
       
    public boolean termination() {   
        double max = Double.MIN_VALUE;   
        for(int k = 0; k < ssize; k++) {   
            for(int i = 0; i < clsNum; i++) {   
                double temp = Math.abs(ou[k][i]-u[k][i]);   
                if(max < temp) max = temp;   
            }   
        }   
        if(max < E) return true;   
        return false;   
    }   
       
    public void updateCenters() {   
        double[][] mus = new double[ssize][clsNum];   
        double[] sum_mu = new double[clsNum];   
        for(int i = 0; i < clsNum; i++) {   
            sum_mu[i] = 0;   
            for(int k = 0; k < ssize; k++) {   
                mus[k][i] = Math.pow(u[k][i], s);   
                sum_mu[i] += mus[k][i];   
            }   
        }   
           
        for(int i = 0; i < clsNum; i++) {   
            for(int j = 0; j < fsize; j++) {   
                double sum = 0;   
                for(int k = 0; k < ssize; k++)   
                    sum += mus[k][i] * samples[k][j];   
                centroids[i][j] = sum / sum_mu[i];   
            }   
        }   
    }   
       
    public void updateU(int k) {   
        Vector<Integer> certain = new Vector<Integer>();   
        Vector<Integer> uncertain = new Vector<Integer>();   
        double[] d = new double[clsNum];   
   
        for(int i = 0; i < clsNum; i++) {   
            d[i] = distance(samples[k], centroids[i]);   
            if(d[i] == 0)    
                certain.add(new Integer(i));   
            else   
                uncertain.add(new Integer(i));   
        }   
           
        if(certain.size() == 0) {   
            for(int i = 0; i < clsNum; i++) {   
                double sum = 0;   
                for(int j = 0; j < clsNum; j++)   
                    sum += Math.pow(d[i]/d[j], 2*1.00/(s-1));   
                u[k][i] = 1.0 / sum;   
            }   
        }   
        else {   
            double value = 1.00 / certain.size();   
            int index = 0;   
            for(int i = 0; i < certain.size(); i++) {   
                index =certain.get(i).intValue();   
                u[k][index] = 0;   
            }   
            for(int i = 0; i < uncertain.size(); i++) {   
                index =uncertain.get(i).intValue();   
                u[k][index] = value;   
            }              
        }   
    }   
       
    /**  
     * Reference  
     * "A validity measure for fuzzy clustering"  
     */   
    public double evaluation() {   
        double sum = 0;   
        for(int i = 0; i < clsNum; i++) {   
            for(int k = 0; k < ssize; k++) {   
                sum += u[k][i]*u[k][i]*sdistance(centroids[i], samples[k]);   
            }   
        }   
        double min = Double.MAX_VALUE;   
        for(int i = 0; i < clsNum; i++)   
            for(int j = 0; j < i; j++) {   
                double temp = sdistance(centroids[i],centroids[j]);   
                if(min > temp)   
                    min = temp;   
            }   
        double s = sum * 1.00 / (ssize * min);   
        System.out.println("Compactness: " + sum * 1.00/ssize + " Seperation: " + min);   
        return s;   
    }   
       
    /**  
     * This method return the result cluster based on tranditional method  
     */   
    public int[] CMap() {   
        int[] map = new int[ssize];   
        for(int k = 0; k < ssize; k++) {   
            map[k] = 0;   
            double max = u[k][0];   
            for(int i = 0; i < clsNum; i++)   
                if(max < u[k][i]){   
                    map[k] = i;   
                    max = u[k][i];   
                }   
        }   
        return map;   
    }   
       
    public double[][] clusterData() {   
        int[] map = CMap();   
        for (int i = 0; i < ssize; i++) {   
            for (int kk = 0; kk < fsize; kk++)   
                samples[i][kk] = centroids[map[i]][kk];   
        }   
           
        return samples;   
    }   
       
    /**  
     * This method return result cluster based on optimal cluster method  
     */   
    public int[] optMap() {   
        int[] map = new int[ssize];   
        for(int k = 0; k < ssize; k++) {   
            map[k] = 0;   
            double max = optu[k][0];   
            for(int i = 0; i < optimalC; i++)   
                if(max < optu[k][i]){   
                    map[k] = i;   
                    max = optu[k][i];   
                }   
        }   
        return map;        
    }   
       
    public double[][] optClusterData() {   
        int[] map = optMap();   
        for (int i = 0; i < ssize; i++) {   
            for (int kk = 0; kk < fsize; kk++)   
                samples[i][kk] = centroids[map[i]][kk];   
        }   
           
        return samples;   
    }   
       
    public void printMap(int[] map) {   
        int len = (int)Math.sqrt(map.length);   
        for(int i = 0; i < ssize; i++)   
            if(i % len == (len-1)) System.out.println(map[i]);   
            else System.out.print(map[i] + " ");   
    }   
       
    public void printSamples() {   
        System.out.println("FCM Samples: ");   
        for(int i = 0; i < samples.length; i++) {   
            for(int j = 0; j < samples[i].length; j++)   
                System.out.print(samples[i][j] + " ");   
            System.out.println();   
        }   
    }   
       
    public void printU() {   
        System.out.println("FCM Relationship Matrix: ");   
        NumberFormat nf = NumberFormat.getInstance();   
        nf.setMaximumFractionDigits(4);   
        for(int i = 0; i < ssize; i++) {   
            for(int j = 0; j < clsNum; j++)   
                System.out.print(nf.format(u[i][j]) + " ");   
                   
            System.out.println();   
        }          
    }   
    
    /**锟斤拷锟截撅拷锟斤拷锟斤拷锟�*/
    public double[][] getU() {
    	return u;
    }   
       
    public void printCenters() {   
        System.out.println("FCM Centroids: ");   
        NumberFormat nf = NumberFormat.getInstance();   
        nf.setMaximumFractionDigits(3);   
        for(int i = 0; i < centroids.length; i++) {   
            for(int j = 0; j < centroids[i].length; j++)   
                System.out.print(nf.format(centroids[i][j]) + " ");   
                   
            System.out.println();   
        }   
    }   
    
    public double[][] getCenters() {
    	return centroids;
    }   
    
    public double sdistance(double[] a, double[] b) {
        double sum = 0;   
        for(int i = 0; i < a.length; i++)   
            sum += (a[i] - b[i]) * (a[i] - b[i]);   
               
        return sum;   
    }      
       
    public static double distance(double[] a, double[] b) {
        double sum = 0;   
        for(int i = 0; i < a.length; i++)   
            sum += (a[i] - b[i]) * (a[i] - b[i]);
               
        return Math.sqrt(sum);   
    }   
       
    public void copyArray(double[] orig, double[] dest) {   
        for(int i = 0; i < orig.length; i++)   
            dest[i] = orig[i];   
    }      
       
    public void copy2DArray(double[][] orig, double[][] dest) {   
        int row = orig.length;   
        int col = orig[0].length;   
        for(int i = 0; i < row; i++) {   
            for(int j = 0; j < col; j++) {   
                dest[i][j] = orig[i][j];   
            }   
        }   
    }

    public static void main(String[] args){
    	double[][] a = {{9.6309}, {5.4681},{5.2114}, {2.3159},{4.8890},{6.2406},
    		    {6.7914},{3.9552},{3.6744},{9.8798},{0.3774},{8.8517}, {9.1329},
    		    {7.9618},{0.9871},{2.6187},{3.3536},{6.7973}, {1.3655},{7.2123},
    		    {1.0676}, {6.5376},{4.9417},{7.7905},{7.1504},{9.0372},{8.9092},
    		    {3.3416}, {6.9875},{1.9781}};
    	_FuzzyCmeans fcm = new _FuzzyCmeans(a, 2, 2);
    	fcm.clustering();
    	fcm.printSamples();
    	fcm.printCenters();
    	fcm.printU();
    }
    


}
