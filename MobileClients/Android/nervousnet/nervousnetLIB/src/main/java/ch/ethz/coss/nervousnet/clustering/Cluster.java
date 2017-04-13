package ch.ethz.coss.nervousnet.clustering;

/**
 * Created by ales on 20/07/16.
 *
 * This is a class for a cluster, which contains the point (Point) of its location and
 * other details. Additionally, it includes array of all points under its supervision -
 * the points, belonging to this cluster/centroid.
 */
public class Cluster {

    private double[] coordinates;

    public Cluster(){}

    public double[] getCoordinates(){
        return coordinates;
    }

    public void setCoordinates(double[] coordinates){
        this.coordinates = coordinates;
    }

}
