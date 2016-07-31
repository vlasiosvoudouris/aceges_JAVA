/*
  Copyright 2012 by Dr. Vlasios Voudouris and ABM Analytics Ltd 
*/
package aceges;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import org.jfree.data.xy.XYSeries;
import aceges.countries.CountryGasProducer;
import aceges.countries.CountryOilProducer;
//import aceges.countries.EnergyAgent;
import aceges.support.ACEGESModelInitialisationOil;
import aceges.support.ACEGESModelinitialisationGas;
import aceges.support.ACEGESScenarioSettings;
import aceges.support.FileDNGReporter;
import aceges.support.FileOilReporter;
import aceges.utilities.R.ConnectionToR;
import sim.engine.*;

/**
 *ACEGESApp sets up the simulation model. The main building blocks of it are: 
 * a) the scheduler
 * b) the 'agents' 
 * c) the 'elementary_geoParticles' for the representation of the geoEnvironment
 * the start function is used to establish the associations among (a), (b) and (c).
 *  @author Dr. Vlasios Voudouris,
 */

public class ACEGESApp extends SimState {
   
	//************* Field are shown (get/set methods exists) on the model tab ***************************
	
	 /** private  Oil fields */	
	private boolean isCampbellHeapesEUROil = false;
	private boolean isUSGSEURMeanOil=false;
	private boolean isUSGSEURFiveOil=false;
	private boolean isBGREUROil=false;
	
	private boolean WEOCurrentPoliciesGrowthOil = false;
	private boolean WEO450PoliciesGrowthOil = false;
	private boolean WEONewPoliciesGrowthOil = false;
	
	private boolean monteCarloEUROil=true;
	private boolean monteCarloDemandGrowthOil=true;
	private boolean monteCarloPeakPointOil=true;
	private boolean monteCarloProductioGrowthOil=true;
	private double peakOil= 0.5;
	private double productionGrowthOil=0.05;

	private long   numberOfPPNPOil;
	private double worldNetDemandOil;
	private double meanProdPPNPOil;
	private double totalPPNProductionOil;
	private double totalNumberAllPPNOil;
	private double totalNunmberPostPNProdOil;
	 
	 /** private Gas fields */
	private boolean isCampbellHeapesEURGas = false;
	private boolean isUSGSEURMeanGas=false;
	private boolean isUSGSEURFiveGas=false;
	private boolean isBGREURGas=false;
	
	private boolean WEOCurrentPoliciesGrowthGas = false;
	private boolean WEO450PoliciesGrowthGas = false;
	private boolean WEONewPoliciesGrowthGas = false;
	private boolean WEOGasGrowthGas = false;
	
	private boolean monteCarloEURDNGas=true;
	private boolean monteCarloDemandGrowthGas=true;
	private boolean monteCarloPeakPointDNGas=true;
	private boolean monteCarloProductioGrowthDNGas=true;
	private double peakDNGas= 0.5;
	private double productionGrowthDNGas=0.05;

	private double worldNetGasDemand;
	private double totalNumberAllPPNGas;
	private double totalNunmberPostPNProdGas;
	private double numberOfPPNPGas;
	private double totalPPNProductionGas;
	private double meanProdPPNPGas;
	 
	 /** private Coal fields */
	
	
	/** generic private fields */
	private int whichBaseYearFromGUI=2010;
	
	//************* Field are NOT shown on the model tab ***************************
	 /** public  Oil fields */
	public  ArrayList<CountryOilProducer> oilAgentList = new ArrayList<CountryOilProducer>();
	public double shiftCrudeOilSiftedValue=0.0;//this is for the 'stacked graphic as JFreeJava does not understand it otherwise'. 
	public XYSeries testGraphicTotalProductionOil;
	public XYSeries testGraphicTotalDemandOil;
	 
	 /** public Gas fields */
	public  ArrayList<CountryGasProducer> gasAgentList = new ArrayList<CountryGasProducer>();
	public double shiftDNGASSiftedValue=0.0;//this is for the 'stacked graphic as JFreeJava does not understand it otherwise'. 
	public XYSeries testGraphicTotalDemandGas; 
	public XYSeries testGraphicTotalProductionGas; 

	 /** public Coal fields */
		
	/** generic public fields */
	public transient ConnectionToR rConnection=null; //agents need to talk to R	
	public int whichBaseYear=this.whichBaseYearFromGUI;
	public boolean RandomUnrest=false;	
	public int count=0;
	public boolean isFromGUI=false;
	private static final long serialVersionUID = 1L;
	public boolean isOilDataScriptLoaded=false;
	
	//20120921KM
	public boolean isGDP1=false; //Base
	public boolean isGDP2=false; //GPI
	public boolean isGDP3=false; //Market
	public boolean isGDP4=false; //Policy
	public boolean isGDP5=false; //Security
	public boolean isGDP6=false; //Sustainability
	public boolean isPopulation1=false; //Low
	public boolean isPopulation2=false; //Medium
	public boolean isPopulation3=false; //High
	public boolean isPopulation4=false; //Constant
	public boolean isPrice1=false; //WEO2011 Current
	public boolean isPrice2=false; //WEO2011 New
	public boolean isPrice3=false; //WEO2011 450	
	public boolean monteCarloGDP=false;
	public boolean monteCarloPopulation=false;
	public boolean monteCarloGasPrice=false;
//20120921KM

	public ACEGESApp(long seed, ConnectionToR rConnection, Boolean isFromGUI)
     {
		 super(seed);
		 this.rConnection=rConnection;
		 this.isFromGUI=isFromGUI;
		 if(isFromGUI)
		 {
			 testGraphicTotalDemandGas = new XYSeries("Total DNGas Demand");
			 testGraphicTotalProductionGas = new XYSeries("Total DNGAS Production");
			 testGraphicTotalProductionOil = new XYSeries("Total Oil Production");
			 testGraphicTotalDemandOil =new XYSeries("Total Oil Demand");
		 }
     }  
	
	public ACEGESApp(long seed)
    {
		this(seed, null, false);
    } 
	

	 public void start()
     {
		 
		 //prepare the scheduler of the simulation
		 super.start();	
		 this.schedule.reset();
		 Steppable[] s = new Steppable[oilAgentList.size()];
		 int i=0;
		 //add all of the oil consumers and producers to the scheduler!
		  Iterator<CountryOilProducer> itr = oilAgentList.iterator();
		    while (itr.hasNext()) {
		      CountryOilProducer element = (CountryOilProducer) itr.next();
		          //this.schedule.scheduleRepeating(element, 0, 1.0);
		       s[i]=element;
	      	   i +=1;
		    }
		    this.schedule.scheduleRepeating(new Sequence(s),0,1.0);
		    //add all of the dng consumers and producers to the scheduler!
		      s = new Steppable[gasAgentList.size()];
		      i=0;
			  Iterator<CountryGasProducer> itr2 = gasAgentList.iterator();
			    while (itr2.hasNext()) {
			      CountryGasProducer element = (CountryGasProducer) itr2.next();
			      	   s[i]=element;
			      	   i +=1;
			          //this.schedule.scheduleRepeating(element, 1, 1.0);
			        
			    }
			    this.schedule.scheduleRepeating(new Sequence(s),1,1.0);
     }
	
	 
	 /************************ Crude Oil (including condensate and NGL) function ************************/
	 
	 	/**
		 * @param oilAgentList the oilAgentList to set and schedule it.
		 */
		public void addOilAgent(CountryOilProducer oilAgent) 
		{
			oilAgentList.add((CountryOilProducer)oilAgent);
		}
	 
		/**
		 * @return the campbellHeapesEUR
		 *
		 */
		public boolean isCampbellHeapesEUROil() {
			return this.isCampbellHeapesEUROil;
		}
		
		/**
		 * @param campbellHeapesEUR the campbellHeapesEUR to set
		 * 
		 */
		public void setCampbellHeapesEUROil(boolean campbellHeapesEUR) {
			this.isCampbellHeapesEUROil = campbellHeapesEUR;
		}

		public boolean isUSGSEURMeanOil() {
			// TODO Auto-generated method stub
			return this.isUSGSEURMeanOil;
		}
		
		public void setUSGSEURMeanOil(boolean temp) {
			// TODO Auto-generated method stub
			 this.isUSGSEURMeanOil = temp;
		}

		public boolean isUSGSEURFiveOil() {
			// TODO Auto-generated method stub
			return this.isUSGSEURFiveOil;
		}
		
		public void setUSGSEURFiveOil(boolean temp) {
			// TODO Auto-generated method stub
			 this.isUSGSEURFiveOil = temp;
		}
		
		public boolean isBGREUROil() 
		{	
			return this.isBGREUROil;
		}
		
		public void setBGREUROil(boolean temp) 
		{
			this.isBGREUROil = temp;
		}
		
		/**
		 * @return the wEOGrowth
		 */
		public boolean isWEOCurrentPoliciesGrowthOil() {
			return this.WEOCurrentPoliciesGrowthOil;
		}
		
		/**
		 * @return the wEOGrowth
		 */
		public boolean isWEONewPoliciesGrowthOil() {
			return this.WEONewPoliciesGrowthOil;
		}
		
		public boolean isWEO450PoliciesGrowthOil() {
			return this.WEO450PoliciesGrowthOil;
		}
		public void setWEO450PoliciesGrowthOil(boolean wEO10450PoliciesGrowth) {
			this.WEO450PoliciesGrowthOil = wEO10450PoliciesGrowth;
		}

		
		/**
		 * @param growth the wEOGrowth to set
		 */
		public void setWEOCurrentPoliciesGrowthOil(boolean growth) {
			this.WEOCurrentPoliciesGrowthOil = growth;
		}
		
		/**
		 * @param growth the WEOGrowth to set
		 */
		public void setWEONewPoliciesGrowthOil(boolean growth) {
			this.WEONewPoliciesGrowthOil = growth;
		}
		
		private void resetCrudeOilCondensateNGLModelParameters()
		{
			 
			  Iterator<CountryOilProducer> itr = oilAgentList.iterator();
			    while (itr.hasNext()) 
			    {
			      CountryOilProducer element = (CountryOilProducer) itr.next();
			      //element.setAgentParameters();
			     // element.setOilCountryParametersfromACEGESFactory();
			      element.setCrudeCondensateOilCountryParametersfromACEGESModelInitialisation();
			      //element.setCrudeOilCondensateNGLCountryParameters();		      
			      //System.out.println(element.toString());
			    }			  
		}

		/**
		 * @return the monteCarloProductioGrowthOil
		 */
		public boolean isMonteCarloProductioGrowthOil() {
			return monteCarloProductioGrowthOil;
		}
		/**
		 * @param monteCarloProductioGrowthOil the monteCarloProductioGrowthOil to set
		 */
		public void setMonteCarloProductioGrowthOil(boolean monteCarloProductioGrowthOil) {
			this.monteCarloProductioGrowthOil = monteCarloProductioGrowthOil;
		}

		/**
		 * @return the monteCarloDemandGrowthOil
		 */
		public boolean isMonteCarloDemandGrowthOil() {
			return monteCarloDemandGrowthOil;
		}
		/**
		 * @param monteCarloDemandGrowthOil the monteCarloDemandGrowthOil to set
		 */
		public void setMonteCarloDemandGrowthOil(boolean monteCarloDemandGrowthOil) {
			this.monteCarloDemandGrowthOil = monteCarloDemandGrowthOil;
		}
		
		/**
		 * @return the monteCarloPeakPointOil
		 */
		public boolean isMonteCarloPeakPointOil() {
			return monteCarloPeakPointOil;
		}
		/**
		 * @param monteCarloPeakPointOil the monteCarloPeakPointOil to set
		 */
		public void setMonteCarloPeakPointOil(boolean monteCarloPeakPointOil) {
			this.monteCarloPeakPointOil = monteCarloPeakPointOil;
		}
		
		/**
		 * @return the monteCarloEUROil
		 */
		public boolean isMonteCarloEUROil() {
			return monteCarloEUROil;
		}
		/**
		 * @param monteCarloEUROil the monteCarloEUROil to set
		 */
		public void setMonteCarloEUROil(boolean monteCarloEUROil) {
			this.monteCarloEUROil = monteCarloEUROil;
		}
		
		/**
		 * @return the peakOil
		 */
		public double getPeakOil() {
			return this.peakOil;
		}
		/**
		 * @param peakOil the peakOilDNGas to set
		 */
		public void setPeakOil(double peakOil) {
			this.peakOil = peakOil;
		}
		public Object domPeakOil() 
		{ return new sim.util.Interval(0.1, 1.00); }
		
		/**
		 * @return the productionGrowthOilDNGas
		 */
		public double getProductionGrowthRateOil() {
			return this.productionGrowthOil;
		}
		/**
		 * @param productionGrowthDNGas the productionGrowthOilDNGas to set
		 */
		public void setProductionGrowthOil(double productionGrowthOil) {
			this.productionGrowthOil = productionGrowthOil;
		}
		
		public Object domProductionGrowthOil() 
		{ return new sim.util.Interval(0.001, 0.2); }
		

	 /************************ Gas functions ************************/
		
		/**
		 * @param oilAgentList the oilAgentList to set and schedule it.
		 */
		public void addDNGAgent(CountryGasProducer gasAgent) 
		{
			gasAgentList.add((CountryGasProducer)gasAgent);
		}
	 	
		private void resetGasModelParameters()
		{
			 
			  Iterator<CountryGasProducer> itr = gasAgentList.iterator();
			    while (itr.hasNext()) 
			    {
			      CountryGasProducer element = (CountryGasProducer) itr.next();
			      element.setDNGasCountryParametersfromACEGESModelInitialisation();		      
			    }			  
		}

		/**
		 * @return the wEO10CurrentPoliciesGrowthGas
		 */
		public boolean isWEOCurrentPoliciesGrowthGas() {
			return this.WEOCurrentPoliciesGrowthGas;
		}
		/**
		 * @param wEO10CurrentPoliciesGrowthGas the wEO10CurrentPoliciesGrowthGas to set
		 */
		public void setWEOCurrentPoliciesGrowthGas(
				boolean wEO10CurrentPoliciesGrowthGas) {
			this.WEOCurrentPoliciesGrowthGas = wEO10CurrentPoliciesGrowthGas;
		}
		/**
		 * @return the wEO10450PoliciesGrowthGas
		 */
		public boolean isWEO450PoliciesGrowthGas() {
			return this.WEO450PoliciesGrowthGas;
		}
		/**
		 * @param wEO10450PoliciesGrowthGas the wEO10450PoliciesGrowthGas to set
		 */
		public void setWEO450PoliciesGrowthGas(boolean wEO10450PoliciesGrowthGas) {
			this.WEO450PoliciesGrowthGas = wEO10450PoliciesGrowthGas;
		}
		/**
		 * @return the wEO10NewPoliciesGrowthGas
		 */
		public boolean isWEONewPoliciesGrowthGas() {
			return this.WEONewPoliciesGrowthGas;
		}
		/**
		 * @param wEO10NewPoliciesGrowthGas the wEO10NewPoliciesGrowthGas to set
		 */
		public void setWEONewPoliciesGrowthGas(boolean wEO10NewPoliciesGrowthGas) {
			this.WEONewPoliciesGrowthGas = wEO10NewPoliciesGrowthGas;
		}
		/**
		 * @return the campbellHeapesEURGas
		 * 
		 */
		public boolean isCampbellHeapesEURGas() {
			return this.isCampbellHeapesEURGas;
		}
		/**
		 * @param campbellHeapesEURGas the campbellHeapesEURGas to set
		 * 
		 */
		public void setCampbellHeapesEURGas(boolean campbellHeapesEURGas) {
			this.isCampbellHeapesEURGas = campbellHeapesEURGas;
		}

		/**
		 * @param bgrEURGas the bgrEURGas to set
		 */
		public void setBGREURGas(boolean bgrEURGas) {
			this.isBGREURGas = bgrEURGas;
		}
		
		/**
		 * @return the bgrEURGas
		 */
		public boolean isBGREURGas() {
			return this.isBGREURGas;
		}
		
		
		/**
		 * @param usgsEURGas the usgsEURGas to set
		 */
		public void setUSGSEURMeanGas(boolean usgsEURGas) {
			this.isUSGSEURMeanGas = usgsEURGas;
		}

		/**
		 * @return the usgsEURGas
		 */
		public boolean isUSGSEURMeanGas() {
			return this.isUSGSEURMeanGas;
		}
		
		/**
		 * @param usgsEURGas the usgsEURGas to set
		 */
		public void setUSGSEURFiveGas(boolean usgsEURGas) {
			this.isUSGSEURFiveGas = usgsEURGas;
		}

		/**
		 * @return the usgsEURGas
		 */
		public boolean isUSGSEURFiveGas() {
			return this.isUSGSEURFiveGas;
		}
		
		/**
		 * @return the monteCarloProductioGrowthDNGas
		 */
		public boolean isMonteCarloProductioGrowthDNGas() {
			return monteCarloProductioGrowthDNGas;
		}
		
		/**
		 * @param monteCarloProductioGrowthDNGas the monteCarloProductioGrowthDNGas to set
		 */
		public void setMonteCarloProductioGrowthDNGas(
				boolean monteCarloProductioGrowthDNGas) {
			this.monteCarloProductioGrowthDNGas = monteCarloProductioGrowthDNGas;
		}
		
		/**
		 * @return the monteCarloDemandGrowthGas
		 */
		public boolean isMonteCarloDemandGrowthGas() {
			return monteCarloDemandGrowthGas;
		}
		/**
		 * @param monteCarloDemandGrowthGas the monteCarloDemandGrowthGas to set
		 */
		public void setMonteCarloDemandGrowthGas(boolean monteCarloDemandGrowthGas) {
			this.monteCarloDemandGrowthGas = monteCarloDemandGrowthGas;
		}

		/**
		 * @return the monteCarloPeakPointDNGas
		 */
		public boolean isMonteCarloPeakPointDNGas() {
			return monteCarloPeakPointDNGas;
		}
		/**
		 * @param monteCarloPeakPointDNGas the monteCarloPeakPointDNGas to set
		 */
		public void setMonteCarloPeakPointDNGas(boolean monteCarloPeakPointDNGas) {
			this.monteCarloPeakPointDNGas = monteCarloPeakPointDNGas;
		}

		/**
		 * @return the monteCarloEURDNGas
		 */
		public boolean isMonteCarloEURDNGas() {
			return monteCarloEURDNGas;
		}
		/**
		 * @param monteCarloEURDNGas the monteCarloEURDNGas to set
		 */
		public void setMonteCarloEURDNGas(boolean monteCarloEURDNGas) {
			this.monteCarloEURDNGas = monteCarloEURDNGas;
		}
		/**
		 * @return the peakDNGas
		 */
		public double getPeakDNGas() {
			return this.peakDNGas;
		}
		/**
		 * @param peakDNGas the peakDNGas to set
		 */
		public void setPeakDNGas(double peakDNGas) {
			this.peakDNGas = peakDNGas;
		}
		
		public Object domPeakDNGas() 
		{ return new sim.util.Interval(0.1, 1.00); }			
		
		/**
		 * @return the productionGrowthDNGas
		 */
		public double getProductionGrowthDNGas() {
			return this.productionGrowthDNGas;
		}
		/**
		 * @param productionGrowthDNGas the productionGrowthDNGas to set
		 */
		public void setProductionGrowthDNGas(double ProductionGrowthDNGas) {
			this.productionGrowthDNGas = ProductionGrowthDNGas;
		}
		
		public Object domProductionGrowthDNGas() 
		{ return new sim.util.Interval(0.001, 0.2); }
		
		
		
    /************************ Generic function ************************/
		
	/**
	 * It updates the Demand for Oil and DNGas production and demand to estimate the model parameters such as PPNP etc. 	
	 */
	public void updatedSimStateOilDNGas()
	{
				calculateWorldNetDemandOil();
				calculateNumberOfPPNPOil();
				calculateTotalProdPPNPOil();
				calculateNumberPostPeakProdOil();
				calculateNumberALLPPNPOil();	
				
				calculateWorldNetDemandGas();
				calculateTotalNumberOfPPNPGas();
				calculateTotalProdPPNPGas();
				calculateNumberPostPeakProdGas();
				calculateNumberALLPPNPGas();	
	}

	public void resetModelParameters()
	{
		//this.calculateLNGConsumpConvFactor();//it is not needed anymore
		this.resetCrudeOilCondensateNGLModelParameters();
		this.resetGasModelParameters();
		if (testGraphicTotalDemandGas != null)
		{
		 testGraphicTotalDemandGas.clear();
		 testGraphicTotalProductionGas.clear();
		 testGraphicTotalProductionOil.clear();
		 testGraphicTotalDemandOil.clear();
		}
	}
	
	public void setUserEnteredBaseYear(int year) {
		this.whichBaseYearFromGUI=year;
	}
	
	public int getUserEnteredBaseYear() {
		return this.whichBaseYearFromGUI;
	}

	public Object domUserEnteredBaseYear() 
	{ return new sim.util.Interval(1995, 2009); }
	
	/**
	 * Sets the base year to be used by in the simulation. 
	 * @return the updated base year
	 */
	public int setModelBaseYear(int whichBaseYear)
	{
		this.whichBaseYear=whichBaseYear;
		return this.whichBaseYear;
	}
	public boolean isRandomUnrest() {
		return RandomUnrest;
	}
	public void setRandomUnrest(boolean randomUnrest) {
		RandomUnrest = randomUnrest;
	}
	
	public void calculateNumberOfPPNPOil()
	{
		this.numberOfPPNPOil=0;
		Iterator<CountryOilProducer> itr = oilAgentList.iterator();
	    while (itr.hasNext()) {
	      CountryOilProducer element = (CountryOilProducer) itr.next();
	      this.numberOfPPNPOil =   this.numberOfPPNPOil + element.getPpnp();
	    } 
	}
	
	public void calculateTotalNumberOfPPNPGas()
	{
		this.numberOfPPNPGas=0.0;
		Iterator<CountryGasProducer> itr = gasAgentList.iterator();
	    while (itr.hasNext()) {
	      CountryGasProducer element = (CountryGasProducer) itr.next();
	      this.numberOfPPNPGas =   this.numberOfPPNPGas + element.getPpnp();
	    } 
	}

	/**
	 * @param this uses the Due to Internal Demand Production ONLY
	 * @return 
	 */
	public double calculateWorldNetDemandOil()
	{
		this.worldNetDemandOil=0;
		double tempProd=0;
		double tempDemnd=0;
		Iterator<CountryOilProducer> itr = oilAgentList.iterator();
	    while (itr.hasNext()) {
	      CountryOilProducer element = (CountryOilProducer) itr.next();
	      
	      tempProd =tempProd + element.dueToInternalDemand;
	      tempDemnd = tempDemnd + element.getOilDemand();
	   
	    }
	    this.worldNetDemandOil= tempDemnd-tempProd;
	    if (this.worldNetDemandOil<0)
	    {
	    	this.worldNetDemandOil=0;
	    }
	    return this.worldNetDemandOil;
	    
	}
	
	/**
	 * @param this uses the Due to Internal Demand Production ONLY
	 * @return 
	 */
	public double calculateWorldNetDemandGas()
	{
		this.worldNetGasDemand=0;
		double tempProd=0;
		double tempDemnd=0;
		Iterator<CountryGasProducer> itr = gasAgentList.iterator();
	    while (itr.hasNext()) {
	      CountryGasProducer element = (CountryGasProducer) itr.next();
	      
	      tempProd =tempProd + element.dueToInternalDemand;
	      tempDemnd = tempDemnd + element.getGasDemand();
	   
	    }
	    this.worldNetGasDemand= tempDemnd-tempProd;//this implies that the demand is not adjusted downwards!
	    //System.out.println("From calculateWorldNetDemandGas()" + tempDemnd);
	    if (this.worldNetGasDemand<0)
	    {
	    	this.worldNetGasDemand=0;
	    }
	   
	    return this.worldNetGasDemand;
	    
	}
	
	public double calculateMeanProdPPNPOil()
	{
		this.meanProdPPNPOil=0;
		if (this.numberOfPPNPOil>0)
		{
			this.meanProdPPNPOil = this.totalPPNProductionOil/this.numberOfPPNPOil;
		}
		
		return this.meanProdPPNPOil;
	}
	
	public double calculateMeanProdPPNPGas()
	{
		this.meanProdPPNPGas=0.0;
		if (this.numberOfPPNPGas>0)
		{
			this.meanProdPPNPGas = this.totalPPNProductionGas/this.numberOfPPNPGas;
		}
		
		return this.meanProdPPNPGas;
	}
	
	public void calculateTotalProdPPNPOil()
	{
		this.totalPPNProductionOil=0;
		Iterator<CountryOilProducer> itr = oilAgentList.iterator();
	    while (itr.hasNext()) {
	      CountryOilProducer element = (CountryOilProducer) itr.next();
	      if (element.getPpnp()==1)
	      {
	    	  this.totalPPNProductionOil = this.totalPPNProductionOil + element.getCurrentOilProduction();
	      }
	    }
	}
	
	public void calculateTotalProdPPNPGas()
	{
		this.totalPPNProductionGas=0.0;
		Iterator<CountryGasProducer> itr = gasAgentList.iterator();
	    while (itr.hasNext()) {
	      CountryGasProducer element = (CountryGasProducer) itr.next();
	      if (element.getPpnp()==1)
	      {
	    	  this.totalPPNProductionGas = this.totalPPNProductionGas + element.getCurrentdnGasProduction();
	      }
	    }
	}
	
	public void calculateNumberPostPeakProdOil()
	{
		this.totalNunmberPostPNProdOil=0;
		Iterator<CountryOilProducer> itr = oilAgentList.iterator();
	    while (itr.hasNext()) {
	      CountryOilProducer element = (CountryOilProducer) itr.next();
	      if (element.getPostPeakNP()==1)
	      {
	    	  this.totalNunmberPostPNProdOil = this.totalNunmberPostPNProdOil + element.getPostPeakNP();
	      }
	    }
	    
	}
	
	public void calculateNumberALLPPNPOil()
	{
		this.totalNumberAllPPNOil=0;
		Iterator<CountryOilProducer> itr = oilAgentList.iterator();
	    while (itr.hasNext()) 
	    {
	      CountryOilProducer element = (CountryOilProducer) itr.next();
	      if (element.getPpnp()==1)
	      {
	    	 this.totalNumberAllPPNOil = this.totalNumberAllPPNOil + element.getPpnp();
	      }
	      else if(element.getPostPeakNP()==1)
	      {
	    	  this.totalNumberAllPPNOil = this.totalNumberAllPPNOil + element.getPostPeakNP();
	      }
	    }
	}
	
	
	public void calculateNumberALLPPNPGas() 
	{
		this.totalNumberAllPPNGas=0;
		Iterator<CountryGasProducer> itr = gasAgentList.iterator();
	    while (itr.hasNext()) {
	      CountryGasProducer element = (CountryGasProducer) itr.next();
	      if (element.getPpnp()==1 )
	      {
	    	 this.totalNumberAllPPNGas = this.totalNumberAllPPNGas + element.getPpnp();
	      }
	      else if(element.getPostPeakNP()==1)
	      {
	    	  this.totalNumberAllPPNGas = this.totalNumberAllPPNGas +element.getPostPeakNP();
	      }
	    }
	}
	
	
	public void calculateNumberPostPeakProdGas() 
	{
		this.totalNunmberPostPNProdGas=0.0;
		Iterator<CountryGasProducer> itr = gasAgentList.iterator();
	    while (itr.hasNext()) {
	      CountryGasProducer element = (CountryGasProducer) itr.next();
	      if (element.getPostPeakNP()==1)
	      {
	    	  this.totalNunmberPostPNProdGas = this.totalNunmberPostPNProdGas + element.getPostPeakNP();
	      }
	    }	
	}
	
	/**
	 * @return the numberOfPPNPOil
	 */
	public long getNumberOfPPNPOil() {
		return numberOfPPNPOil;
	}
	/**
	 * @param numberOfPPNPOil the numberOfPPNPOil to set
	 */
	public void setNumberOfPPNPOil(long numberOfPPNPOil) {
		this.numberOfPPNPOil = numberOfPPNPOil;
	}
	/**
	 * @return the worldNetOilDemand
	 */
	public double getWorldNetDemandOil() {
		return worldNetDemandOil;
	}
	/**
	 * @param worldNetOilDemand the worldNetOilDemand to set
	 */
	public void setWorldNetDemandOil(double worldNetOilDemand) {
		this.worldNetDemandOil = worldNetOilDemand;
	}
	/**
	 * @return the meanProdPPNPOil
	 */
	public double getMeanProdPPNPOil() {
		return meanProdPPNPOil;
	}
	/**
	 * @param meanProdPPNPOil the meanProdPPNPOil to set
	 */
	public void setMeanProdPPNPOil(double meanProdPPNPOil) {
		this.meanProdPPNPOil = meanProdPPNPOil;
	}
	/**
	 * @return the totalPPNProductionOil
	 */
	public double getTotalPPNProductionOil() {
		return totalPPNProductionOil;
	}
	/**
	 * @param totalPPNProductionOil the totalPPNProductionOil to set
	 */
	public void setTotalPPNProductionOil(double totalPPNProductionOil) {
		this.totalPPNProductionOil = totalPPNProductionOil;
	}
	/**
	 * @return the totalNumberAllPPNOil
	 */
	public double getTotalNumberAllPPNOil() {
		return totalNumberAllPPNOil;
	}
	/**
	 * @param totalNumberAllPPNOil the totalNumberAllPPNOil to set
	 */
	public void setTotalNumberAllPPNOil(double totalNumberAllPPNOil) {
		this.totalNumberAllPPNOil = totalNumberAllPPNOil;
	}
	/**
	 * @return the totalNunmberPostPNProdOil
	 */
	public double getTotalNunmberPostPNProdOil() {
		return totalNunmberPostPNProdOil;
	}
	/**
	 * @param totalNunmberPostPNProdOil the totalNunmberPostPNProdOil to set
	 */
	public void setTotalNunmberPostPNProdOil(double totalNunmberPostPNProdOil) {
		this.totalNunmberPostPNProdOil = totalNunmberPostPNProdOil;
	}
	/**
	 * @return the worldNetGasDemand
	 */
	public double getWorldNetGasDemand() {
		return worldNetGasDemand;
	}
	/**
	 * @param worldNetGasDemand the worldNetGasDemand to set
	 */
	public void setWorldNetGasDemand(double worldNetGasDemand) {
		this.worldNetGasDemand = worldNetGasDemand;
	}
	/**
	 * @return the totalNumberAllPPNGas
	 */
	public double getTotalNumberAllPPNGas() {
		return totalNumberAllPPNGas;
	}
	/**
	 * @param totalNumberAllPPNGas the totalNumberAllPPNGas to set
	 */
	public void setTotalNumberAllPPNGas(double totalNumberAllPPNGas) {
		this.totalNumberAllPPNGas = totalNumberAllPPNGas;
	}
	/**
	 * @return the totalNunmberPostPNProdGas
	 */
	public double getTotalNunmberPostPNProdGas() {
		return totalNunmberPostPNProdGas;
	}
	/**
	 * @param totalNunmberPostPNProdGas the totalNunmberPostPNProdGas to set
	 */
	public void setTotalNunmberPostPNProdGas(double totalNunmberPostPNProdGas) {
		this.totalNunmberPostPNProdGas = totalNunmberPostPNProdGas;
	}
	/**
	 * @return the numberOfPPNPGas
	 */
	public double getNumberOfPPNPGas() {
		return numberOfPPNPGas;
	}
	/**
	 * @param numberOfPPNPGas the numberOfPPNPGas to set
	 */
	public void setNumberOfPPNPGas(double numberOfPPNPGas) {
		this.numberOfPPNPGas = numberOfPPNPGas;
	}
	/**
	 * @return the totalPPNProductionGas
	 */
	public double getTotalPPNProductionGas() {
		return totalPPNProductionGas;
	}
	/**
	 * @param totalPPNProductionGas the totalPPNProductionGas to set
	 */
	public void setTotalPPNProductionGas(double totalPPNProductionGas) {
		this.totalPPNProductionGas = totalPPNProductionGas;
	}
	/**
	 * @return the meanProdPPNPGas
	 */
	public double getMeanProdPPNPGas() {
		return meanProdPPNPGas;
	}
	/**
	 * @param meanProdPPNPGas the meanProdPPNPGas to set
	 */
	public void setMeanProdPPNPGas(double meanProdPPNPGas) {
		this.meanProdPPNPGas = meanProdPPNPGas;
	}
	

	/**
	 * This is only used IF the EUR estimates include crude oil and NOT NGLs.
	 * @return the conversion factor to include only the crude oil from the demand of the total liquites
	 * 	@Deprecated
	 */
	public double calculateLNGConsumpConvFactor()
	{
		double ngplProduction=0;
		double lpgConsumption=0;
		double tmtProp=0;
		double count=0;
		Iterator<CountryOilProducer> agent = oilAgentList.iterator();
	    while (agent.hasNext()) {
	      CountryOilProducer element = (CountryOilProducer) agent.next();   	
	       Set<String> set= element.lpgConsumption.keySet();
	    	Iterator<String> itr = set.iterator();
	    	String str;	 
	    	//System.out.println(this.myModel.whichYearT);
	    	while (itr.hasNext()) 
	 	    {
	 	    	  str = itr.next(); 
	 	    	 
	 	    	 if (Integer.parseInt(str)<=this.getUserEnteredBaseYear())
	 	    	 {
	 	    	   lpgConsumption= lpgConsumption+ (Double)element.lpgConsumption.get(str);
	 	    	   ngplProduction= ngplProduction+ (Double) element.ngplProduction.get(str);
	 	    	 }
	 	    }
	    }
	    return ngplProduction/lpgConsumption;
	}
	
	/**
	 * It runs the ACEGES model without GUI (Very fast for MANY simulations).
	 * The default is to run 5,000 simulations, 100 step each simulation.
	 * The results are saved in the dataSimulatedOutputs as usual.
	 * @param args
	 */
	public static void main(String[] args)
	{ 
		
		int jobsMax=200;
		int stepMax=10;
		ACEGESApp model = new ACEGESApp(System.currentTimeMillis());
		/* set the base year for the simulation */
		model.whichBaseYear=2010; 
		model.whichBaseYearFromGUI=model.whichBaseYear;
		
		/* set the scenario for oil - ALL the variables are explicitly set up*/
		model.WEO450PoliciesGrowthOil=false;
		model.WEOCurrentPoliciesGrowthOil=false;
		model.WEONewPoliciesGrowthOil=false;
		model.isBGREUROil=true;
		model.isCampbellHeapesEUROil=false;
		model.isUSGSEURFiveOil=false;
		model.isUSGSEURMeanOil=false;		
		model.monteCarloDemandGrowthOil=true;
		model.monteCarloPeakPointOil=true;
		model.monteCarloProductioGrowthOil=true;
		model.monteCarloEUROil=false;
		
		/* set the scenario for gas - ALL the variables are explicitly set up*/
		model.isCampbellHeapesEURGas = false;
		model.isUSGSEURMeanGas=false;
		model.isUSGSEURFiveGas=false;
		model.isBGREURGas=false;	
		model.WEOCurrentPoliciesGrowthGas = false;
		model.WEO450PoliciesGrowthGas = false;
		model.WEONewPoliciesGrowthGas = false;
		model.WEOGasGrowthGas = false;		
		model.monteCarloEURDNGas=true;
		model.monteCarloDemandGrowthGas=true;
		model.monteCarloPeakPointDNGas=true;
		model.monteCarloProductioGrowthDNGas=true;
	
		/* load the oil and gas data from the external files*/
		new ACEGESModelInitialisationOil(model);
		new ACEGESModelinitialisationGas(model);
		new ACEGESScenarioSettings(model);
		
		/* set up the file reporting */
		FileOilReporter myFileOilReported= new FileOilReporter(model);
		FileDNGReporter myFileGasReported= new FileDNGReporter(model);
		
		/* initialise the parameters of the agents */
		model.resetModelParameters();
		
		/* start the simulation engine */
		for (int jobs=0; jobs<=jobsMax; jobs++)
	    {
		    model.start();	//prepare the model schedule	 	
		 	do
		 	{
		 		model.updatedSimStateOilDNGas();//equivalent to scheduleRepeatingImmediatelyBefore
		 		if (!model.schedule.step(model))
		 		{
		 			System.err.println("Something with the step function");
		 			break;
		 		}
		 		//System.err.println(jobs + ","+ model.schedule.getSteps());
		 		myFileOilReported.saveResults();//equivalent scheduleRepeatingImmediatelyAfter
		 		myFileGasReported.saveResults();//equivalent scheduleRepeatingImmediatelyAfter
		 	}while (model.schedule.getSteps() < stepMax);
		 	model.finish();//clean the model schedule
		 	System.out.println("job: " + jobs);
		 	//model.calculateLNGConsumpConvFactor();
			model.resetModelParameters();
		 	myFileOilReported.simulationNumber+=1;
		 	myFileGasReported.simulationNumber+=1;
	    }
	    
	    myFileOilReported.closeCSVFile();
	    myFileGasReported.closeCSVFile();
	    System.exit(0); 
	}
}
