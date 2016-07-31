package aceges.countries;

import java.util.Hashtable;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.util.FastMath;

import sim.engine.SimState;



public class CountryGasConsumer extends EnergyAgent 
{
	public double currentDNGasDemand;
	public double dnGasDemandGrowth;

	//km20120921
	public double currentPrice;
	public double currentGDP;
	public double currentPopulation;
	public double scenarioGDP;
	public double scenarioPopulation;
	//km20120921
	
	public Hashtable<String, Object> dngConsumption = new Hashtable(); // dry natural gas consumption
	public Hashtable<String, Object> dnGasDemandGrowthAll= new Hashtable(); // demand growth numbers
	
	//km20120921
	public Hashtable<String, Object> GDPHist = new Hashtable(); //GDP
	public Hashtable<String, Object> GDPGrowBas = new Hashtable(); //GDP growth rate
	public Hashtable<String, Object> GDPGrowGPI = new Hashtable(); //GDP growth rate
	public Hashtable<String, Object> GDPGrowMar = new Hashtable(); //GDP growth rate
	public Hashtable<String, Object> GDPGrowPol = new Hashtable(); //GDP growth rate
	public Hashtable<String, Object> GDPGrowSec = new Hashtable(); //GDP growth rate
	public Hashtable<String, Object> GDPGrowSus = new Hashtable(); //GDP growth rate
	public Hashtable<String, Object> priceCur = new Hashtable(); //Price
	public Hashtable<String, Object> priceNew = new Hashtable(); //Price
	public Hashtable<String, Object> price450 = new Hashtable(); //Price
	public Hashtable<String, Object> popLow = new Hashtable(); //Population scenario
	public Hashtable<String, Object> popMed = new Hashtable(); //Population scenario
	public Hashtable<String, Object> popHig = new Hashtable(); //Population scenario
	public Hashtable<String, Object> popCon = new Hashtable(); //Population scenario
	public Hashtable<String, Object> popHist = new Hashtable(); //Population history
	public Hashtable<String, Object> demandCoef = new Hashtable(); //Population history
	//km20120921

	public CountryGasConsumer()
	{		
	}

//20120921KM	
//New demand function
	public void calculateCurrentGasDemandRegression()
	{
		double inter = (Double)this.demandCoef.get("inter");
		double betaPrice = (Double)this.demandCoef.get("b_Price");
		double betaGDP = (Double)this.demandCoef.get("b_GDP");
		double betaDemandLast = (Double)this.demandCoef.get("b_Demand_1");

		if((Double)inter == 0)			{ inter = 0.3851400; };
		if((Double)betaPrice == 0)		{ betaPrice = -0.0198335; }; 
		if((Double)betaGDP == 0)		{ betaGDP = 0.0929658; }; 
		if((Double)betaDemandLast == 0){ betaDemandLast = 0.8744799; }; 

		Set keys = this.GDPHist.keySet();
		String year = Integer.toString(simModel.whichBaseYear+(int)simModel.schedule.getSteps()+1);

		if(Integer.parseInt(year)==simModel.whichBaseYear+1 || keys.contains(year)){
			currentGDP = (Double) this.GDPHist.get(year);
		}
		else
		{
			//System.out.println(this.getName() + " "+currentDNGasDemand + " with growth:" +  this.currentDNGasDemand*(1+this.dnGasDemandGrowth));
			currentGDP = currentGDP * (1+scenarioGDP/100);
		}

		keys = this.popHist.keySet();
		if (Integer.parseInt(year)==simModel.whichBaseYear+1 || keys.contains(year))
		{
			currentPopulation = (Double) this.popHist.get(year);
		}
		else
		{
			//System.out.println(this.getName() + " "+currentDNGasDemand + " with growth:" +  this.currentDNGasDemand*(1+this.dnGasDemandGrowth));
			currentPopulation = this.scenarioPopulation;
		}

		keys = dngConsumption.keySet();
		if (keys.contains(year))
		{
			this.currentDNGasDemand=(Double) dngConsumption.get(year);
		}
		else{
			//this.currentDNGasDemand = currentPopulation*(FastMath.pow((this.currentDNGasDemand/currentPopulation),betaDemandLast)*FastMath.pow(this.currentPrice,betaPrice)*FastMath.pow((currentGDP/currentPopulation),betaGDP)*FastMath.exp(inter));
			this.currentDNGasDemand = currentPopulation * ((this.currentDNGasDemand/currentPopulation)*betaDemandLast + currentPrice*betaPrice + (currentGDP/currentPopulation)*betaGDP + inter);
		}
	}
//20120921KM
	
	public void calculateCurrentGasDemandFixedGrowth()
	{
		
		this.currentDNGasDemand= this.currentDNGasDemand*(1+this.dnGasDemandGrowth);
	}
	/**
	 * This method uses the historic and then it forecasts
	 */
	public void calculateCurrentGasDemand()
	{
		Set<String> keys = dngConsumption.keySet();
		String year= Integer.toString(simModel.whichBaseYear+(int)simModel.schedule.getSteps()+1);
		if (keys.contains(year))
		{
			//System.out.println(year);
			this.currentDNGasDemand=(Double) dngConsumption.get(year);
		}
		else
		{
			//System.out.println(this.getName() + " "+currentDNGasDemand + " with growth:" +  this.currentDNGasDemand*(1+this.dnGasDemandGrowth));
			this.currentDNGasDemand= this.currentDNGasDemand*(1+this.dnGasDemandGrowth);
		}
	}

	
	public double getGasDemand() 
	{
		return currentDNGasDemand;
	}

	public void setGasDemand(double gasDemand) {
		this.currentDNGasDemand = gasDemand;
	}

	public double getGasDemandGrowth() {
		return dnGasDemandGrowth;
	}

	public void setGasDemandGrowth(double gasDemandGrowth) {
		this.dnGasDemandGrowth = gasDemandGrowth;
	}

	/**
	 * @return the dngConsumption
	 */
	public Hashtable<String, Object> getDngConsumption() {
		return dngConsumption;
	}

	/**
	 * @param dngConsumption the dngConsumption to set
	 */
	public void setDngConsumption(Hashtable<String, Object> dngConsumption) 
	{
		this.dngConsumption = dngConsumption;
	}
	
}
