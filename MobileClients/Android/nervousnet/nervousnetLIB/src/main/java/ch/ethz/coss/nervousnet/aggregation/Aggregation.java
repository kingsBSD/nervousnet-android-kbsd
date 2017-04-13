package ch.ethz.coss.nervousnet.aggregation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;


public class Aggregation<G extends GeneralAggrItem> {
	private List<G> list;
	public Aggregation(List<G> list) {
		this.list = list;
	}


	public ArrayList<Float> var()
	{

		ArrayList<Float> variance = new ArrayList();
		try {
			int n = this.list.size();
			if (n <= 0) {
				return null;
			}

			int dimensions = this.list.get(0).getValue().size();

			ArrayList<Float> average = getAverage();
			ArrayList<Float> sumOfSquares = new ArrayList();
			for (int i = 0; i < dimensions; i++)
				sumOfSquares.add(new Float(0));

			ArrayList<Float> temp;
			for (G sensorData : list) {
				temp = sensorData.getValue();

				for (int i = 0; i < dimensions; i++) {
					float tmpAdd = temp.get(i) - average.get(i);
					float tmp = sumOfSquares.get(i) + tmpAdd * tmpAdd;
					sumOfSquares.set(i, tmp);
				}
			}

			for (int i = 0; i < dimensions; i++) {
				double rms = sumOfSquares.get(i) / n;
				variance.add(new Float(rms));
			}
		} catch (Exception e){
			System.err.print(e);
		}

		return variance;
	}

	public ArrayList<Float> sd()
	{
		ArrayList<Float> array = new ArrayList();
		try{
			array = var();

			for (int i = 0; i < array.size(); i++) {
				double rms = Math.sqrt(array.get(i));
				array.set(i, new Float(rms));
			}
		} catch (Exception e){
			System.err.print(e);
		}

		return array;
	}

	public G getMaxValue() { //add all three values and find the maximum
		G maxSensDesc = null;
		try{float maxAverage = 0;
			for (G sensorData : list) {
				//G sensDesc = sensorData; // get sensor data
				ArrayList<Float> value = new ArrayList<Float>();
				value = sensorData.getValue(); //get arraylist of values of sensor(3 values generally)
				float newAverage = 0;
				for(int i = 0;i < value.size();i++)
				{
					newAverage = value.get(i) + newAverage;
				}
				newAverage = newAverage / value.size();

				if (newAverage > maxAverage) {
					maxAverage = newAverage;
					maxSensDesc = sensorData;
				}
			}
		}
		catch(Exception e1){
			System.out.println(e1);
		}
		return maxSensDesc; //return the object itself
	}

	public G getMinValue() { // add all three values and find the minimum
		G minSensDesc = null;
		try{float maxAverage = Float.MAX_VALUE;
			for (G sensorData : list) {
				//G sensDesc = sensorData;
				ArrayList<Float> value = new ArrayList<Float>();
				value = sensorData.getValue(); //get arraylist of values of sensor(3 values generally)
				float newAverage = 0;
				for(int i = 0;i < value.size();i++)
				{
					newAverage = value.get(i) + newAverage;
				}
				newAverage = newAverage / value.size();
				if (newAverage < maxAverage) {
					maxAverage = newAverage;
					minSensDesc = sensorData;
				}
			}
		}
		catch(Exception e1){
			System.out.println(e1);
		}
		return minSensDesc; //return object itself
	}

	public ArrayList<G> getLargest(int k) {  //largest values within this range
		ArrayList<G> descList = new ArrayList<G>();
		try{
			Comparator<G> comparator = new LargestFirstComparator();
			PriorityQueue<G> prioQueue = new PriorityQueue<G>(3,comparator);

			for (G sensorData : list) {
				prioQueue.add(sensorData);
			}

			int i = 1;

			while (i <= k && !prioQueue.isEmpty()) {
				descList.add(prioQueue.poll());
				++i;
			}
		}
		catch(Exception e1){
			System.out.println(e1);
		}
		return descList;
	}

	public G getRankLargest(int k) {  //largest values within this range
		G dummydesc = null;
		try{Comparator<G> comparator = new LargestFirstComparator();
			PriorityQueue<G> prioQueue = new PriorityQueue<G>(11,comparator);

			for (G sensorData : list) {
				prioQueue.add(sensorData);
			}

			int i = 1;

			while (i <= k && !prioQueue.isEmpty()) {
				if(i == k)
				{
					dummydesc = prioQueue.poll();
					break;
				}
				prioQueue.poll();
				++i;
			}
		}
		catch(Exception e1){
			System.out.println(e1);
		}
		return dummydesc;
	}

	public G getRankSmallest(int k) {  //largest values within this range
		G dummydesc = null;
		try{
			Comparator<G> comparator = new SmallestFirstComparator();
			PriorityQueue<G> prioQueue = new PriorityQueue<G>(11,comparator);

			for (G sensorData : list) {
				prioQueue.add(sensorData);
			}

			int i = 1;

			while (i <= k && !prioQueue.isEmpty()) {
				if(i == k)
				{
					dummydesc = prioQueue.poll();
					break;
				}
				prioQueue.poll();
				++i;
			}
		}
		catch(Exception e1){
			System.out.println(e1);
		}
		return dummydesc;
	}

	public ArrayList<G> getSmallest(int k) {
		ArrayList<G> descList = new ArrayList<G>();
		try{
			Comparator<G> comparator = new SmallestFirstComparator();
			PriorityQueue<G> prioQueue = new PriorityQueue<G>(11, comparator);

			for (G sensorData : list) {
				prioQueue.add(sensorData);
			}
			int i = 1;

			while (i <= k && !prioQueue.isEmpty()) {
				descList.add(prioQueue.poll());
				++i;
			}
		}
		catch(Exception e1){
			System.out.println(e1);
		}
		return descList;
	}

	public ArrayList<Float> getAverage() {                       // find the average of all the values
		ArrayList<Float> average = new ArrayList<Float>();
		try{        // 0-> avg of x and so on...
			int size = 0;
			if (list.size() > 0){
				size = list.get(0).getValue().size();
				for (int i = 0; i < size; i++)
					average.add(new Float(0));
			}
			ArrayList<Float> temp;
			for (G sensorData : list) {
				temp = sensorData.getValue();
				for(int i = 0; i < size; i++)                   //for each x,z & z
				{
					float temptemp = average.get(i) + temp.get(i);   //add current data to the existing one and replace
					average.set(i,temptemp);
				}

			}

			for(int i = 0 ; i < average.size(); i++ )
			{
				float temptemp = average.get(i);
				temptemp = temptemp/list.size();
				average.set(i,temptemp);
			}
		}
		catch(Exception e1){
			System.out.println(e1);
		}
		return average;
	}

	public ArrayList<Float> getMedian() {

		/*public static double median(double[] m) {
		    int middle = m.length/2;
		    if (m.length%2 == 1) {
		        return m[middle];
		    } else {
		        return (m[middle-1] + m[middle]) / 2.0;
		    }
		}*/

		try{ Comparator<G> comparator = new SmallestFirstComparator();
			ArrayList<G> arrList = new ArrayList<G>();

			// Add all SensorDesc
			for (G sensorData : list) {
				arrList.add(sensorData);
			}
			Collections.sort(arrList, comparator);  //sort array
			int middle = (arrList.size()/2);

			if(arrList.size()%2 == 1)
			{
				return arrList.get(middle).getValue();
			}
			else
			{
				ArrayList<Float> temp = new ArrayList<Float>();
				ArrayList<Float> temp1 = new ArrayList<Float>();
				ArrayList<Float> median = new ArrayList<Float>();
				temp = arrList.get(middle).getValue();
				temp1 = arrList.get(middle+1).getValue();
				int s = temp.size();//each variable x,y,z?
				for(int i = 0; i < s ;i++) //usually size = 3
				{
					float temptemp = 0;
					temptemp = (temp.get(i) + temp1.get(i))/2;
					median.set(i, temptemp);

				}

				return median;
			}
		}
		catch(Exception e1){
			System.out.println(e1);
			ArrayList<Float> k= new ArrayList<Float>();
			return k;
		}

	}


	public ArrayList<Float> getSum() {                       // find the average of all the values
		ArrayList<Float> sum = new ArrayList<Float>();        // 0-> avg of x and so on...
		int size = 0;
		if (list.size() > 0){
			size = list.get(0).getValue().size();
			for (int i = 0; i < size; i++)
				sum.add(new Float(0));
		}
		try{
			ArrayList<Float> temp;
			for (G sensorData : list) {
				G sensDesc = sensorData; // loop over the sensor data,get the object
				temp = sensDesc.getValue();
				for(int i = 0; i < size; i++)                   //for each x,z & z
				{
					float temptemp = sum.get(i) + temp.get(i);   //add current data to the existing one and replace
					sum.set(i,temptemp);
				}

			}
		}
		catch(Exception e1){
			System.out.println(e1);
		}
		return sum;
	}

	public ArrayList<Float> getSumSquare() {                       // find the average of all the values
		ArrayList<Float> sum = new ArrayList<Float>();        // 0-> avg of x and so on...
		int size = 0;
		if (list.size() > 0){
			size = list.get(0).getValue().size();
			for (int i = 0; i < size; i++)
				sum.add(new Float(0));
		}
		try{
			ArrayList<Float> temp;
			for (G sensorData : list) {
				G sensDesc = sensorData; // loop over the sensor data,get the object
				temp = sensDesc.getValue();
				for(int i = 0; i < size; i++)                   //for each x,z & z
				{
					float temptemp = (float) (sum.get(i) + Math.pow(temp.get(i),2));   //add current data to the existing one and replace
					sum.set(i,temptemp);
				}

			}
		}
		catch(Exception e1){
			System.out.println(e1);
		}
		return sum;
	}

	public ArrayList<Float> getRms() {                       // find the average of all the values
		ArrayList<Float> sum = new ArrayList<Float>();
		int size = 0;
		if (list.size() > 0){
			size = list.get(0).getValue().size();
			for (int i = 0; i < size; i++)
				sum.add(new Float(0));
		}
		try{
			ArrayList<Float> temp;
			for (G sensorData : list) {
				G sensDesc = sensorData; // loop over the sensor data,get the object
				temp = sensDesc.getValue();
				for(int i = 0; i < temp.size(); i++)                   //for each x,z & z
				{
					float temptemp = (float) (sum.get(i) + Math.pow(temp.get(i),2));   //add current data to the existing one and replace
					sum.set(i,temptemp);
				}

			}
			for(int i = 0 ; i < sum.size(); i++ )
			{
				float temptemp = sum.get(i);
				temptemp = temptemp/list.size();
				temptemp = (float) Math.sqrt(temptemp);
				sum.set(i,temptemp);
			}
		}
		catch(Exception e1){
			System.out.println(e1);
		}

		return sum;
	}

	public ArrayList<Float> getMeanSquare() {                       // find the average of all the values
		ArrayList<Float> sum = new ArrayList<Float>();        // 0-> avg of x and so on...
		int size = 0;
		if (list.size() > 0){
			size = list.get(0).getValue().size();
			for (int i = 0; i < size; i++)
				sum.add(new Float(0));
		}
		try{
			ArrayList<Float> temp;
			for (G sensorData : list) {
				G sensDesc = sensorData; // loop over the sensor data,get the object
				temp = sensDesc.getValue();
				for(int i = 0; i < temp.size(); i++)                   //for each x,z & z
				{
					float temptemp = (float) (sum.get(i) + Math.pow(temp.get(i),2));   //add current data to the existing one and replace
					sum.set(i,temptemp);
				}

			}
			for(int i = 0 ; i < sum.size(); i++ )
			{
				float temptemp = sum.get(i);
				temptemp = temptemp/list.size();
				sum.set(i,temptemp);
			}
		}
		catch(Exception e1){
			System.out.println(e1);
		}
		return sum;
	}

	public class SmallestFirstComparator implements Comparator<G> {  // one object smaller than the other

		@Override
		public int compare(G lhs, G rhs) {

			try{ArrayList<Float> lVal = new ArrayList<Float>();
				lVal = lhs.getValue();
				int i;
				Float lsum = (float) 0;
				for(i = 0; i < lVal.size(); i++)
					lsum += lVal.get(i);

				ArrayList<Float> rVal = new ArrayList<Float>();
				rVal = rhs.getValue();

				Float rsum = (float) 0;
				for(i = 0; i < rVal.size(); i++)
					lsum += rVal.get(i);

				if (lsum < rsum) {
					return -1;
				} else if (lsum > rsum) {
					return 1;
				} else {
					return 0;
				}
			}
			catch(Exception e1){
				System.out.println(e1);
				return 1000;
			}
		}

	}

	public class LargestFirstComparator implements Comparator<G> {  //one object larger than the other

		@Override
		public int compare(G lhs, G rhs) {

			try{ArrayList<Float> lVal = new ArrayList<Float>();
				lVal = lhs.getValue();
				int i;
				Float lsum = (float) 0;
				for(i = 0; i < lVal.size(); i++)
					lsum += lVal.get(i);

				ArrayList<Float> rVal = new ArrayList<Float>();
				rVal = rhs.getValue();

				Float rsum = (float) 0;
				for(i = 0; i < rVal.size(); i++)
					lsum += rVal.get(i);

				if (lsum > rsum) {
					return -1;
				} else if (lsum < rsum) {
					return 1;
				} else {
					return 0;
				}
			}
			catch(Exception e1){
				System.out.println(e1);
				return 1000;
			}
		}

	}

	public ArrayList<Float> getCorrelation(ArrayList<G> comp,ArrayList<G> comp1)
	{
		ArrayList<Float> moo = new ArrayList<Float>();//results

		try {ArrayList<Float> avg = new ArrayList<Float>();
			ArrayList<Float> avg1 = new ArrayList<Float>();

			ArrayList< ArrayList<Float> > a = new ArrayList<ArrayList<Float> >();//centered data
			ArrayList< ArrayList<Float> > b = new ArrayList<ArrayList<Float> >();
			//get average

			for(int i = 0 ; i<comp.size();i++)
			{
				ArrayList<Float> temp = comp.get(i).getValue();
				for(int j = 0;j < temp.size();j++)
				{
					float temptemp = avg.get(j);
					temptemp = temptemp + temp.get(j);
					avg.add(j, temptemp);
				}
			}
			for(int i = 0 ; i<comp1.size();i++)
			{
				ArrayList<Float> temp = comp1.get(i).getValue();
				for(int j = 0;j < temp.size();j++)
				{
					float temptemp = avg1.get(j);
					temptemp = temptemp + temp.get(j);
					avg1.add(j, temptemp);
				}
			}
			for(int i = 0;i<avg.size();i++)
			{
				float tmp = avg.get(i);
				tmp = tmp / comp1.size();
				avg.add(i,tmp);
				tmp = avg1.get(i);
				tmp = tmp / comp1.size();
				avg1.add(i,tmp);
			}
			//subtract variable by mean

			for(int i = 0 ; i < comp.size(); i++)
			{
				ArrayList<Float> temp = comp.get(i).getValue();
				for(int j = 0;j < temp.size();j++)
				{
					float temptemp = temp.get(j) - avg.get(j);
					temp.add(j,temptemp);//replace value with centered value
				}
				a.add(temp);
			}
			for(int i = 0 ; i < comp1.size() ; i++)
			{
				ArrayList<Float> temp = comp1.get(i).getValue();
				for(int j = 0;j < temp.size();j++)
				{
					float temptemp = temp.get(j) - avg1.get(j);
					temp.add(j,temptemp);//replace value with centered value
				}
				b.add(temp);
			}
			//now we have a and b centered value and avg and avg1 their averages
			// we need a*b and a2 and b^2


			ArrayList<Float> top = new ArrayList<Float>();//numerator

			ArrayList<Float> bota = new ArrayList<Float>();//denominator
			ArrayList<Float> botb = new ArrayList<Float>();

			for(int i = 0 ; i < comp1.size(); i++)
			{
				ArrayList<Float> temp = comp.get(i).getValue();
				ArrayList<Float> temp1 = comp1.get(i).getValue();
				for(int j = 0 ; j < temp.size() ; j++)
				{
					float temptemp = top.get(j);
					temptemp = temptemp + temp.get(j)*temp1.get(j);
					top.add(j,temptemp);//top

					temptemp = bota.get(j);
					temptemp = (float) (temptemp + Math.pow(temp.get(j), 2));
					bota.add(j,temptemp);//square a

					temptemp = botb.get(j);
					temptemp = (float) (temptemp + Math.pow(temp1.get(j), 2));
					botb.add(j,temptemp);//square b
				}
			}

			//need to divide and multiply
			for(int i = 0 ; i < top.size() ; i++)
			{
				float temp = top.get(i)/(bota.get(i)*botb.get(i));
				moo.add(i,temp);
			}
		}
		catch(Exception e1){
			System.out.println(e1);
		}
		return moo;//size of different variables
	}





	public ArrayList<Float> getEntropy()
	{
		ArrayList<Float> moo = new ArrayList<Float>();
		try{
			ArrayList<Float> average = getAverage();

			ArrayList< ArrayList<Float> > prob = new ArrayList<ArrayList<Float> >();
			ArrayList<Float> temp;
			for (G sensorData : list) {
				G sensDesc = sensorData; // loop over the sensor data,get the object
				temp = sensDesc.getValue();
				for(int i = 0; i < temp.size(); i++)                  //for each x,z & z
				{
					float temptemp = temp.get(i);                    //get x,y or z
					temptemp = temptemp / average.get(i);
					temp.set(i,temptemp);                            //now temp contains the probabilities of x,y,z
				}

				prob.add(temp);

			}

			int size = list.size();
			if (size > 0){
				for (int i = 0; i < list.get(0).getValue().size(); i++)
					moo.add(new Float(0));
			}
			for(int i = 0;i<prob.size();i++)
			{
				temp = prob.get(i);                                     //x,y,z of 1 reading
				for(int j = 0 ; j < temp.size(); j++)
				{
					float temptemp = moo.get(j);                        //get particular x,,y or z
					temptemp = (float) (temptemp + temp.get(j)*Math.log10(1/temp.get(j)));
					moo.set(j,temptemp);
				}
			}


		}
		catch(Exception e1){
			System.out.println(e1);
		}
		return moo;
	}



	public ArrayList<Float> getKMeans(int n,ArrayList<Float> init)
	{
		ArrayList<Float> moo = new ArrayList<Float>();
		try{}
		catch(Exception e1){
			System.out.println(e1);
		}

		return moo;
	}

}
	
	

