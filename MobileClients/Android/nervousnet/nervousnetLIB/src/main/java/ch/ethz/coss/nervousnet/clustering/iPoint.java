package ch.ethz.coss.nervousnet.clustering;

/**
 * Created by ales on 20/07/16.
 * This class represents one point with its own coordinates. It keeps reference to
 * the original object and cluster number.
 */
public interface iPoint {

    public int getDimensions();
    public double[] getCoordinates();
    public int getCluster();
    public void setCluster(int c);
}
