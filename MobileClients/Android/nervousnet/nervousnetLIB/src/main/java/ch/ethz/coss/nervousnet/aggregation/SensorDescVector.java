package ch.ethz.coss.nervousnet.aggregation;

import java.util.ArrayList;
import java.util.List;

public abstract class SensorDescVector {

	public abstract ArrayList<Float> getValue();
	public abstract List<String> getValNames();

}