package aceges.support;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Vector;
import org.apache.commons.math3.util.FastMath;
import aceges.countries.CountryOilProducer;
import aceges.gui.GUIACEGESApp;



public class PetroleumMarketDiversity implements java.io.Serializable
{

	private double p=1.0d/2.0d;
	private Vector<Double> weights= new Vector<Double>();
	private double diversity=0.0;
	private transient  GUIACEGESApp guiModel;
	
	public	PetroleumMarketDiversity(GUIACEGESApp guiModel)
	{
		this.guiModel = guiModel;
	}

	public double calculateDiversity()
	{
		Iterator itr = weights.iterator();
		this.diversity=0.0;
		  while (itr.hasNext()) 
		  {
			  double weight= (Double)itr.next();
			  this.diversity = this.diversity +  FastMath.pow(weight,p);
		  }
		  this.diversity= FastMath.pow(diversity, 1.0d/p);
		  return this.diversity;
	}

	public Vector<Double> calculateWeights(Vector<Double> list)
	{
		Iterator itr = list.iterator();
		Double sum=0.0;
		 while (itr.hasNext()) 
		  {
			  sum = sum + (Double)itr.next();    
		  }

		 Iterator itr2 = list.iterator();
		 while (itr2.hasNext()) 
		 {
			 weights.add((Double)itr2.next()/sum);    
		  }
		return weights;
	}
	/**
	 * @return the weights
	 */
	public Vector<Double> getWeights() {
		return weights;
	}
	/**
	 * @return the diversity
	 */
	public double getDiversity() {
		return diversity;
	}
	/**
	 * @param weights the weights to set
	 */
	public void setWeights(Vector<Double> weights) {
		this.weights = weights;
	}
	/**
	 * @param diversity the diversity to set
	 */
	public void setDiversity(double diversity) {
		this.diversity = diversity;
	}
	/**
	 * @return the p
	 */
	public double getP() {
		return p;
	}
	/**
	 * @param p the p to set
	 */
	public void setP(double p) 
	{	
		if (p<=0 || p>=1)
		{
			return;
		}
		this.p = p;
	}
	
}
