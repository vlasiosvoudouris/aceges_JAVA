package aceges.countries;

import java.util.Hashtable;

import sim.engine.SimState;


public class CountryOilConsumer extends EnergyAgent  
{	
	public double oilDemand;
	private double oilPrice;
	private double[] covariatesoilDemand;
	private double[] beta;
	private double theta2;
	private double theta1;
	private double oilPriceElasticity;
	private double lastOilConsuption;
	public double demandGrowthRateOil=0;
	private double lastOilConsuption2;
	
	public Hashtable<String, Object> oilDemandGrowthAll= new Hashtable();
	public Hashtable<String, Object>lpgConsumption= new Hashtable();//Liquefied_Petroleum_Gases
	public Hashtable<String, Object>tpConsumption= new Hashtable();//Total_Petroleum_Consumption
	
	private static final long serialVersionUID = 1L;

	public CountryOilConsumer ()
	{		
	}
	
	/**
	 * @this is based on Hallock et al
	 */
	public void calculateCurrentOilDemandFixedGrowth()
	{
		this.oilDemand= this.oilDemand*(1+this.demandGrowthRateOil);
	}
	
	/**
	 * @this is based on  Houthakker and Taylor (1970) taken from Haiku manual - Resources For Future page 37 equation 9
	 * To be estimated using GAMLSS see the 'aceges.utilities.R.GAMLSS' package
	 */
	protected void calculateCurrentOilDemand()
	{
		double priceWithElast;
		double lastOilDem1;
		double lastOilDem2;
		//double[] covariatesoilDemandSQR = null;
		//int i = 0; //which covariate to use
		
		priceWithElast = Math.pow(oilPrice,oilPriceElasticity);
		lastOilDem1 = Math.sqrt(Math.pow(lastOilConsuption,1-theta1));
		lastOilDem2 = Math.sqrt(Math.pow(lastOilConsuption2,1-theta2));
		
		//a loop here to find all the exploratory variables with the coefficients
		//covariatesoilDemandSQR[i]=Math.sqrt(Math.pow(covariatesoilDemand[i],(theta1+theta2)*beta[i]));		
		
		//oilDemand= priceWithElast*lastOilDem1*lastOilDem2*covariatesoilDemandSQR[i];
		oilDemand= priceWithElast*lastOilDem1*lastOilDem2;
	}
	

	/**
	 * The explanatory variables are i) annual population, 
	 * ii) cost of crude oil import, 
	 * iii) gross domestic production (GDP) 
	 * and iv) annual oil production in the last period.
	 * 
	 */
	protected void regressionForCurrentOilDemand()
	{
		
	}
	
	private void estimateOilDemandGowth()
	{
		
	}
	
	/**
	 * @return the oilDemand
	 */
	public double getOilDemand() 
	{
		return this.oilDemand;
	}

	/**
	 * @param oilDemand the oilDemand to set
	 * @return updated oil demand
	 */
	public double setOilDemand(double oilDemand) 
	{
		this.oilDemand = oilDemand;
		return this.oilDemand;
	}

	/**
	 * @return the oilDemandGrowth
	 */
	public double getDemandGrowthRateOil() 
	{
		return this.demandGrowthRateOil;
	}

	/**
	 * @param oilDemandGrowth the oil Demand Growth rate to set
	 */
	public void setDemandGrowthRateOil(double oilDemandGrowth) 
	{
		this.demandGrowthRateOil = oilDemandGrowth;
	}

	/**
	 * @return the lastOilConsuption
	 */
	protected double getLastOilConsuption() 
	{
		return lastOilConsuption;
	}

	/**
	 * @param lastOilConsuption the lastOilConsuption to set
	 */
	protected void setLastOilConsuption(double lastOilConsuption) 
	{
		this.lastOilConsuption = lastOilConsuption;
	}
	
}
