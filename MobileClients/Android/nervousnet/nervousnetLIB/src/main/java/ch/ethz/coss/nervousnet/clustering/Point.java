package ch.ethz.coss.nervousnet.clustering;

/**
 * Created by ales on 20/07/16.
 * This class represents one point with its own coordinates. It keeps reference to
 * the original object and cluster number.
 */
public class Point implements iPoint{
    protected double[] coordinates;
    protected int cluster;
    protected Object reference;

    public Point(double[] coordinates){
        this.coordinates = coordinates;
    }

    public Point(double[] coordinates, Object ref){
        this.coordinates = coordinates;
        this.reference = ref;
    }

    public int getDimensions(){
        return coordinates.length;
    }

    public double[] getCoordinates(){
        return this.coordinates;
    }

    public int getCluster(){
        return cluster;
    }

    public Object getReference(){
        return this.reference;
    }

    public void setReference(Object ref){
        this.reference = ref;
    }

    public void setCluster(int c){
        this.cluster = c;
    }

}
