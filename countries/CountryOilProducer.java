package aceges.countries;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.portrayal.Inspector;
import ec.util.*;

import aceges.ACEGESApp;
import aceges.utilities.statistics.DistLib.uniform;

public class CountryOilProducer extends CountryOilConsumer 
{
	private double lastOilConsuption;
	private double lastOilConsuption2;
	private double reserveToProductionRatio;
	private Boolean isOilProducer = true;	
	private Boolean hasBeenLive=false;
	private double oilYetToProduceExcludingCurrentProduction;
	private double oilPercentageDepleted;
	private int ppnp=0;
	private int postnetprod=0;
	public double dueToInternalDemand=0.00;

	private double oilEUR;
	private double currentOilProduction;
	private double currentOilProductionHist; // to calculate the growth rate
	private double cumulativeOilProduction;
	private double peakPointOil= 0.5;
	private double productioGrowthOil=0.075;
	private boolean unrest=false;
	private  double probForUnrest=0.5;

	public Hashtable<String, Object>ngplProduction= new Hashtable();//Natural_Gas_Plant_Liquids
	public Hashtable<String, Object>ccProduction= new Hashtable();//Crude_Oil_including_Lease_Condensate
	public Hashtable<String, Object>historicCCProduction= new Hashtable();//from Adam's dataset
	public Hashtable<String, Object> oilEURAll= new Hashtable();	
	public double provedReservesCrudeOil=0.0;
	private boolean isNPpdf=false;
	private double[] randomOilEUR;
	private int whichRandomOilEUR=0;
	public double oilDemandGrowthMin;
	public double oilDemandGrowthMax;
	public double productioGrowthMin;
	public double productioGrowthMax;
	public double peakPointMin;
	public double peakPointMax;
	public double oilEURMin;
	public double oilEURMax;
	private boolean isFromScenariosFile=true;
		
	private static final long serialVersionUID = 1L;
	
	public CountryOilProducer(ACEGESApp simModel)
	{
		super();
		this.simModel=simModel;		
	}

	/**
	 * Estimates the Yet To Produce oil using the following equation: oilEUR-(cumulativeOilProduction-currentOilProduction)
	 * @param oilEUR
	 * @param cumulativeOilProduction
	 * @param currentOilProduction
	 * @return the estimated crude oil (including NGL) Yet to produce.  
	 */
	private double calculateoilYetToProduceExcludingCurrentProduction(double oilEUR, double cumulativeOilProduction, double currentOilProduction)
	{
		this.oilYetToProduceExcludingCurrentProduction=oilEUR-(cumulativeOilProduction-currentOilProduction);
		return this.oilYetToProduceExcludingCurrentProduction;
	}
	
	private void calculateOilCumulativeProduction(double currentProd)
	{
		this.cumulativeOilProduction= this.cumulativeOilProduction + currentProd;	
		//this.currentOilProduction= currentProd;
	}
	
	/**
	 * This is  a resource-constrained model
	 */	
	private void calculateCurrentOilProductionResourceConstrainedModel()
	{
		
		// vv - in some simulations important countries are assigned production of zero
		if (this.getFIPS().contentEquals("SA"))
		{
			//System.out.println(simModel.schedule.getSteps());
			//System.out.println(this.getCurrentOilProduction());
			if (this.getCurrentOilProduction()==0)
			{
				System.err.println((Double)this.ccProduction.get(Integer.toString(simModel.whichBaseYear)) + (Double)this.ngplProduction.get(Integer.toString(simModel.whichBaseYear)));
				System.err.println(simModel.whichBaseYear);
				System.err.println(simModel.schedule.getSteps());
				System.err.println(this.toString());
				//System.exit(0);
			}
		}
		//vv
		this.currentOilProductionHist=this.getCurrentOilProduction();
		
		if(!this.isOilProducer)
		{
			return;
		}

		double netWorldDemand = this.simModel.getWorldNetDemandOil();
		long numberOfPPNP = this.simModel.getNumberOfPPNPOil();
		double totalProdPPNP = this.simModel.getTotalPPNProductionOil();
		double meanProdPPNP = this.simModel.calculateMeanProdPPNPOil();
		
		if(this.getCurrentOilProduction()>this.getOilDemand() && this.cumulativeOilProduction < this.peakPointOil*this.oilEUR)
		{
			this.ppnp=1; 
			this.postnetprod=0;
								
			//DTI for The only difference between it and the previous formula is that this does NOT add an additional increment to PPNPs corresponding to a portion net world demand for imports.
			double tempcurrentESTOilPrduction1 = 0.00;
			double tempcurrentESTOilPrduction2=0.00;	
			tempcurrentESTOilPrduction1 = this.currentOilProduction+this.getOilDemand()*this.getDemandGrowthRateOil();			
			tempcurrentESTOilPrduction2 = this.currentOilProduction+(this.peakPointOil*this.oilEUR - this.cumulativeOilProduction);
			this.dueToInternalDemand = Math.min(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2);
			if (this.dueToInternalDemand<0)
			{
				this.dueToInternalDemand =Math.max(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2);
			}
		}		
		else if (this.currentOilProduction>this.getOilDemand() && this.cumulativeOilProduction >= this.peakPointOil*this.oilEUR)
		{
			this.postnetprod=1;
			this.ppnp=0;
		//	System.out.println(this.getName() + ":" + postnetprod);
		}
		else
		{
			this.ppnp=0;
			this.postnetprod=0;
		}
		
		//*******************start production************************************
		
		if (this.cumulativeOilProduction < this.peakPointOil*this.oilEUR)
		{
			if((this.cumulativeOilProduction/this.oilEUR)>=(this.peakPointOil-0.05))
			{	
				double tempcurrentESTOilPrduction1 = 0.00;
				double tempcurrentESTOilPrduction2=0.00;
				double tempcurrentESTOilPrduction3=0.00;
				
				if (ppnp==1)
				{	
					tempcurrentESTOilPrduction1 = this.currentOilProduction+this.currentOilProduction*
					this.productioGrowthOil* (1-(0.7*(this.cumulativeOilProduction/(this.peakPointOil*this.oilEUR))));
					tempcurrentESTOilPrduction2 = this.currentOilProduction +((this.getOilDemand()*this.getDemandGrowthRateOil()) +
					((netWorldDemand/numberOfPPNP)+(((this.currentOilProduction-meanProdPPNP)/meanProdPPNP)*(netWorldDemand/numberOfPPNP)))) * (1-(0.7*(this.cumulativeOilProduction/(this.peakPointOil*this.oilEUR))));
					
					tempcurrentESTOilPrduction3 = this.currentOilProduction + (this.peakPointOil*this.oilEUR-this.cumulativeOilProduction);
					
					setCurrentOilProduction(Math.min(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2));	
					setCurrentOilProduction(Math.min(this.currentOilProduction, tempcurrentESTOilPrduction3));
				}
				else
				{
					tempcurrentESTOilPrduction1 = this.currentOilProduction+this.currentOilProduction*
					this.productioGrowthOil* (1-(0.7*(this.cumulativeOilProduction/(this.peakPointOil*this.oilEUR))));
					tempcurrentESTOilPrduction2 = this.currentOilProduction +(this.getOilDemand()*this.getDemandGrowthRateOil())  * (1-(0.7*(this.cumulativeOilProduction/(this.peakPointOil*this.oilEUR))));
					tempcurrentESTOilPrduction3 = this.currentOilProduction + (this.peakPointOil*this.oilEUR-this.cumulativeOilProduction);
					setCurrentOilProduction(Math.min(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2));	
					
					if (this.currentOilProduction<0)
					{
						setCurrentOilProduction(Math.max(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2));
					}
					
					   setCurrentOilProduction(Math.min(this.currentOilProduction, tempcurrentESTOilPrduction3));
				
					if (this.currentOilProduction<0)
					{
						setCurrentOilProduction(Math.max(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2));
						setCurrentOilProduction(Math.max(this.currentOilProduction, tempcurrentESTOilPrduction3));
					}
				}
			}
			else
			{
				double tempcurrentESTOilPrduction1 = 0.00;
				double tempcurrentESTOilPrduction2=0.00;
				double tempcurrentESTOilPrduction3=0.00;
				
				if (ppnp==1)
				{
					tempcurrentESTOilPrduction1 = this.currentOilProduction+this.currentOilProduction*this.productioGrowthOil;
					
					tempcurrentESTOilPrduction2 = this.currentOilProduction +((this.getOilDemand()*this.getDemandGrowthRateOil()) +
							(netWorldDemand/numberOfPPNP)+((this.currentOilProduction-meanProdPPNP)/meanProdPPNP)*(netWorldDemand/numberOfPPNP));
					
					tempcurrentESTOilPrduction3 = this.currentOilProduction + (this.peakPointOil*this.oilEUR-this.cumulativeOilProduction);
					
					setCurrentOilProduction(Math.min(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2));	
					setCurrentOilProduction(Math.min(this.currentOilProduction, tempcurrentESTOilPrduction3));
					
				}
				else
				{
					//System.out.println(this.getName());
					tempcurrentESTOilPrduction1 = this.currentOilProduction+this.currentOilProduction*
					this.productioGrowthOil;
					
					tempcurrentESTOilPrduction2 = this.currentOilProduction +(this.getOilDemand()*this.getDemandGrowthRateOil());
					
					tempcurrentESTOilPrduction3 = this.currentOilProduction + (this.peakPointOil*this.oilEUR-this.cumulativeOilProduction);
					
					setCurrentOilProduction(Math.min(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2));	
					if (this.currentOilProduction<0)
					{
						setCurrentOilProduction(Math.max(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2));
					}
					
					setCurrentOilProduction(Math.min(this.currentOilProduction, tempcurrentESTOilPrduction3));
				
					if (this.currentOilProduction<0)
					{
						setCurrentOilProduction(Math.max(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2));
						setCurrentOilProduction(Math.max(this.currentOilProduction, tempcurrentESTOilPrduction3));
					}					
				}
			}
		}//end of pre-peak
		else
		{	
			//The intent is to set the decline rate of post-peak agents
			//as the ratio of production in the last year to the volume of oil remaining 
			//at the start of that previous year before production began.		
			double tempcurrentESTOilPrduction1 = 0.00;
			double tempcurrentESTOilPrduction2=0.00;
					
			if ((this.cumulativeOilProduction/this.oilEUR)<=(this.peakPointOil+0.05))
			{				
				tempcurrentESTOilPrduction1=this.currentOilProduction - (this.currentOilProduction*(this.currentOilProduction/this.oilYetToProduceExcludingCurrentProduction))*
				(1-(0.7*((this.cumulativeOilProduction-(2*(this.cumulativeOilProduction-(this.peakPointOil*this.oilEUR))))/(this.peakPointOil*this.oilEUR))));
				tempcurrentESTOilPrduction2=this.oilYetToProduceExcludingCurrentProduction;
				this.currentOilProduction = Math.min(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2);	
				this.dueToInternalDemand=this.currentOilProduction;
			}
			else
			{
				tempcurrentESTOilPrduction1=this.currentOilProduction - (this.currentOilProduction*(this.currentOilProduction/this.oilYetToProduceExcludingCurrentProduction));
				tempcurrentESTOilPrduction2=this.oilYetToProduceExcludingCurrentProduction;	
				if (Math.min(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2)>0)
				{
					this.currentOilProduction = Math.min(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2);	
				}
				else
				{
					this.currentOilProduction = Math.max(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2);	
				}
				this.dueToInternalDemand=this.currentOilProduction;	
			}
		}//end of post-peak oil
		
		//A bit of calculations cleaning for next iteration
		if (this.currentOilProduction<0 ||Double.isNaN(this.currentOilProduction))
		{
			this.currentOilProduction=0;
		}
		this.calculateOilCumulativeProduction(this.currentOilProduction);	
		this.calculateoilYetToProduceExcludingCurrentProduction(this.getOilEUR(),this.getCumulativeOilProduction(),this.getCurrentOilProduction());	
		if (this.oilYetToProduceExcludingCurrentProduction<=0.0)
		{
			//System.out.println(this.currentOilProduction + " "+ this.getName() + this.oilYetToProduceExcludingCurrentProduction);
			this.isOilProducer=false;
			this.ppnp=0;
			this.postnetprod=0;
			this.currentOilProduction=0;
			this.currentOilProductionHist=0;
	    }
	}//end of function
		
	/**
	 * @return the oilEUR
	 */
	public double getOilEUR() {
		return this.oilEUR;
	}

	/**
	 * @param oilEUR the oilEUR to set
	 * @return updated EUR
	 */
	public double setOilEUR(double oilEUR) {
		this.oilEUR = oilEUR;
		return this.oilEUR;
	}

	/**
	 * @return the reserveToProductionRatio
	 */
	public double getOilReserveToProductionRatio() {
		return this.oilYetToProduceExcludingCurrentProduction/this.currentOilProduction;
	}

	/**
	 * @param reserveToProductionRatio the reserveToProductionRatio to set
	 */
	public void setOilReserveToProductionRatio(double reserveToProductionRatio) {
		this.reserveToProductionRatio = reserveToProductionRatio;
	}

	/**
	 * @return the isProducer
	 */
	public Boolean getIsOilProducer() {
		return isOilProducer;
	}

	/**
	 * @param isProducer the isProducer to set
	 */
	public void setIsOilProducer(Boolean isProducer) {
		this.isOilProducer = isProducer;
	}

	/**
	 * @return the currentOilProduction
	 */
	public double getCurrentOilProduction() {
		return this.currentOilProduction;
	}

	/**
	 * @return the cumulativeOilProduction
	 */
	public double getCumulativeOilProduction() {
		return this.cumulativeOilProduction;
	}

	/**
	 * @return the oilYetToProduceExcludingCurrentProduction
	 */
	public double getoilYetToProduceExcludingCurrentProduction() {
		return oilYetToProduceExcludingCurrentProduction;
	}
	
	/**
	 * @return the oilPercentageDepleted
	 */
	public double getOilPercentageDepleted() {
		return oilPercentageDepleted;
	}

	/**
	 * 
	 * @param currentOilProduction
	 * @return the updated currentOilProduction 
	 */
	public double setCurrentOilProduction(double currentOilProduction) 
	{
		
		this.currentOilProduction = currentOilProduction;
		return this.currentOilProduction;
	}

	/**
	 * @param cumulativeOilProduction the cumulativeOilProduction to set
	 */
	public double setCumulativeOilProduction(double cumulativeOilProduction) {
		this.cumulativeOilProduction = cumulativeOilProduction;
		return this.cumulativeOilProduction;
	}

	/**
	 * @param oilYetToProduceExcludingCurrentProduction the oilYetToProduceExcludingCurrentProduction to set
	 */
	public void setoilYetToProduceExcludingCurrentProduction(double oilYetToProduceExcludingCurrentProduction) {
		this.oilYetToProduceExcludingCurrentProduction = oilYetToProduceExcludingCurrentProduction;
	}

	/**
	 * @param oilPercentageDepleted the oilPercentageDepleted to set
	 */
	public void setOilPercentageDepleted(double oilPercentageDepleted) {
		this.oilPercentageDepleted = oilPercentageDepleted;
	}

	public void step(SimState state) 
	{		
		this.calculateCurrentOilProductionResourceConstrainedModel();
		if(simModel.isFromGUI)
		{
			int year =simModel.whichBaseYear+(int)simModel.schedule.getSteps()+1;
			this.simulatedCrudeOilStackedProduction.add(year,this.getCurrentOilProduction()+simModel.shiftCrudeOilSiftedValue);
			this.simulatedCrudeOilProduction.add(year,this.getCurrentOilProduction());
			this.simulatedCrudeOilDemand.add(year,this.getOilDemand());
			simModel.shiftCrudeOilSiftedValue+=this.getCurrentOilProduction();
		}
		this.calculateCurrentOilDemandFixedGrowth();	
	}
	
	
	/**
	 * Initialise the oil and NGL model with the data
	 * The country is initialised with data loaded from ACEGESModelOilInitialisation
	 */
	public void setCrudeOilCondensateNGLCountryParameters()
	{
		if(this.simulatedCrudeOilProduction !=null)
		{
			this.simulatedCrudeOilProduction.clear();
		}
		
		if(this.simulatedCrudeOilStackedProduction !=null)
		{
			this.simulatedCrudeOilStackedProduction.clear();
		}
		
		if (this.simulatedCrudeOilDemand !=null)
		{
			this.simulatedCrudeOilDemand.clear(); 
		}

		String yearOfIntisialisation= Integer.toString(simModel.setModelBaseYear(simModel.getUserEnteredBaseYear()));
		
		//this.setCurrentOilProduction(0);
		this.setCurrentOilProduction((Double)this.ccProduction.get(yearOfIntisialisation) + (Double)this.ngplProduction.get(yearOfIntisialisation));
		this.setOilDemand((Double)this.tpConsumption.get(yearOfIntisialisation));

	    this.setCumulativeOilProduction(0);
	    /* the iteration below assumes that crude oil and ngl have data for the same years from 1980 onwards */
		Set<String> set = this.ccProduction.keySet();
		Iterator<String> itr = set.iterator();
	    String str;	    
	    double tmtProd=0;
	    while (itr.hasNext()) 
	    {
	    	str = itr.next(); 
	    	//cumulative production from 1980 to initialisation year.
	    	if (Integer.parseInt(str)<=simModel.whichBaseYear)
	    	{
	    		// System.out.println(str);
	    		tmtProd= (Double)this.ccProduction.get(str);
	    		if (tmtProd<=0 && Integer.parseInt(str)>=1980)
	    		{
	    			tmtProd =(Double) this.historicCCProduction.get(str);
	    		}
	    	// add as cumulative production: crude oil (including condensate) and NGL	
	        this.setCumulativeOilProduction(this.getCumulativeOilProduction()+tmtProd+(Double)this.ngplProduction.get(str));
	    	}	  
	    }
	    
	    set = this.historicCCProduction.keySet();
		itr = set.iterator();	
		tmtProd=0;
	    while (itr.hasNext()) 
	    {
	    	str = itr.next(); 
	    	// check the first year from the file: 
	    	// Production_of_Crude_Oil_including_Lease_Condensate_(Thousand_Barrels_Per_Day)
	    	if (Integer.parseInt(str)<1980)					
	    	{
	    		//System.out.println(str);
	    		tmtProd= (Double)this.historicCCProduction.get(str);
	    		// we do not have data of NGL before 1980.
	    		this.setCumulativeOilProduction(this.getCumulativeOilProduction()+tmtProd);
	    	}	  
	    }
	    		
		if (this.getCurrentOilProduction()>0)
		{
			this.isOilProducer=true;
			this.hasBeenLive=true;
		}
		else
		{
			this.isOilProducer=false;
			this.hasBeenLive=false;
		}
		

		if (simModel.isUSGSEURMeanOil())//USGSEUR MEAN
		{
			if (this.oilEURAll.containsKey("OilMeanUSGS"))
			{
				double tempEUR=(Double) this.oilEURAll.get("CumulativeOilUSGS") + (Double) this.oilEURAll.get("RemainingOilUSGS") +
						(Double)this.oilEURAll.get("OilMeanUSGS") + (Double)this.oilEURAll.get("GrowthOilUSGS");
				tempEUR= tempEUR+ (Double) this.oilEURAll.get("CumulativeNGL_MMBNGL") + (Double) this.oilEURAll.get("RemainingNGL") +
						(Double)this.oilEURAll.get("NGLMean") + (Double)this.oilEURAll.get("GrowthNGL");;
				this.setOilEUR(tempEUR);
				this.calculateoilYetToProduceExcludingCurrentProduction(this.getOilEUR(),this.getCumulativeOilProduction(),this.getCurrentOilProduction());	
			}
			else
			{
				this.setOilEUR(0.0);	
			}
		}
		else if(simModel.isUSGSEURFiveOil())//USGSEUR MAX
		{
			if (this.oilEURAll.containsKey("OilF5USGS"))
			{
				double tempEUR=(Double) this.oilEURAll.get("CumulativeOilUSGS") + (Double) this.oilEURAll.get("RemainingOilUSGS") +
						(Double)this.oilEURAll.get("OilF5USGS") + (Double)this.oilEURAll.get("GrowthOilUSGS");
				tempEUR= tempEUR+ (Double) this.oilEURAll.get("CumulativeNGL_MMBNGL") + (Double) this.oilEURAll.get("RemainingNGL") +
						(Double)this.oilEURAll.get("NGLF5") + (Double)this.oilEURAll.get("GrowthNGL");
				this.setOilEUR(tempEUR);
				this.calculateoilYetToProduceExcludingCurrentProduction(this.getOilEUR(),this.getCumulativeOilProduction(),this.getCurrentOilProduction());	
			}
			else
			{
				this.setOilEUR(0.0);	
			}
		} // this is default option = if nothing is selected. 
		else
		{
			if (this.oilEURAll.containsKey("EURBGR"))
			{
				this.setOilEUR((Double)this.oilEURAll.get("EURBGR"));
				this.calculateoilYetToProduceExcludingCurrentProduction(this.getOilEUR(),this.getCumulativeOilProduction(),this.getCurrentOilProduction());	
			}
			else
			{
				this.setOilEUR(0.0);
			}
		}
	
		
		if (simModel.isWEO450PoliciesGrowthOil())
		{		
			this.setDemandGrowthRateOil((Double)this.oilDemandGrowthAll.get("WEOPolicy03"));
		}
		else if (simModel.isWEONewPoliciesGrowthOil())
		{
			this.setDemandGrowthRateOil((Double) this.oilDemandGrowthAll.get("WEOPolicy02"));
		}
		else
		{		
			this.setDemandGrowthRateOil((Double) this.oilDemandGrowthAll.get("WEOPolicy01"));
		}
		
		
		if(simModel.isMonteCarloPeakPointOil())
		{
			double minGrowth=0.35;
			double maxGrowth=0.70;		
			this.setPeakPointOil(roundDecimals(minGrowth +  (maxGrowth - minGrowth) *randonGenerator.nextDouble()));			
		}
		else
		{
			this.setPeakPointOil(simModel.getPeakOil());
		}
		
		
		if(simModel.isMonteCarloProductioGrowthOil())
		{
			double minGrowth=0.05;
			double maxGrowth=0.151;		
			this.setProductionGrowthRateOil(roundDecimals(minGrowth +  (maxGrowth - minGrowth) *randonGenerator.nextDouble()));			
		}
		else
		{
			this.setProductionGrowthRateOil(simModel.getProductionGrowthRateOil());
		}
		
		if(simModel.isMonteCarloDemandGrowthOil())
		{
			double minGrowth=0.0;
			double maxGrowth=0.0;
			minGrowth = Math.min( (Double)this.oilDemandGrowthAll.get("WEOPolicy02"),(Double) this.oilDemandGrowthAll.get("WEOPolicy01"));
			minGrowth = Math.min(minGrowth,(Double)this.oilDemandGrowthAll.get("WEOPolicy03"));
			maxGrowth = Math.max((Double)this.oilDemandGrowthAll.get("WEOPolicy02"),(Double) this.oilDemandGrowthAll.get("WEOPolicy01"));
			maxGrowth = Math.max(maxGrowth, (Double)this.oilDemandGrowthAll.get("WEOPolicy03"));
			this.setDemandGrowthRateOil(roundDecimals(minGrowth + (maxGrowth - minGrowth) * randonGenerator.nextDouble()));
		}
		
		if (this.getDemandGrowthRateOil()==0)
		{
			this.setDemandGrowthRateOil(0.0053);
		}
		
		if(simModel.isMonteCarloEUROil() && this.oilEURAll.containsKey("EURBGR") &&
				this.oilEURAll.containsKey("OilF95USGS"))
		{
			double minEUR=0.0;
			double maxEUR=0.0;
			
			double tempUSGSEURmin=(Double) this.oilEURAll.get("CumulativeOilUSGS") + (Double) this.oilEURAll.get("RemainingOilUSGS") +
			(Double)this.oilEURAll.get("OilF95USGS") + (Double)this.oilEURAll.get("GrowthOilUSGS");
			
			tempUSGSEURmin= tempUSGSEURmin+ (Double) this.oilEURAll.get("CumulativeNGL_MMBNGL") + (Double) this.oilEURAll.get("RemainingNGL") +
					(Double)this.oilEURAll.get("NGLF95") + (Double)this.oilEURAll.get("GrowthNGL");
			
			minEUR = Math.min((Double) this.oilEURAll.get("EURBGR"), tempUSGSEURmin);
			
			if (minEUR<=1)
			{
				minEUR = Math.max((Double) this.oilEURAll.get("EURBGR"), tempUSGSEURmin);
			}
								
			double tempEURmax=(Double) this.oilEURAll.get("CumulativeOilUSGS") + (Double) this.oilEURAll.get("RemainingOilUSGS") +
			(Double)this.oilEURAll.get("OilF5USGS") + (Double)this.oilEURAll.get("GrowthOilUSGS");
			
			tempEURmax= tempEURmax+ (Double) this.oilEURAll.get("CumulativeNGL_MMBNGL") + (Double) this.oilEURAll.get("RemainingNGL") +
					(Double)this.oilEURAll.get("NGLF5") + (Double)this.oilEURAll.get("GrowthNGL");
			
			maxEUR = Math.max((Double) this.oilEURAll.get("EURBGR"),tempEURmax);	
			this.setOilEUR(minEUR + (maxEUR - minEUR) * randonGenerator.nextDouble());
			this.calculateoilYetToProduceExcludingCurrentProduction(this.getOilEUR(),this.getCumulativeOilProduction(),this.getCurrentOilProduction());	
		}
		
		if(isFromScenariosFile)
		{
			
			this.demandGrowthRateOil = roundDecimals(oilDemandGrowthMin + (oilDemandGrowthMax - oilDemandGrowthMin) * randonGenerator.nextDouble());			
			if(demandGrowthRateOil==0)
			{
				demandGrowthRateOil=0.01;
			}
			this.productioGrowthOil= roundDecimals(productioGrowthMin +  (productioGrowthMax - productioGrowthMin) *randonGenerator.nextDouble());			
			
			if(productioGrowthOil<0.05)
			{
				productioGrowthOil=0.05;
			}
			this.peakPointOil= roundDecimals(peakPointMin +  (peakPointMax - peakPointMin) *randonGenerator.nextDouble());			
			if(peakPointOil<0.35)
			{
				peakPointOil=0.5;
			}
			this.oilEUR= oilEURMin + (oilEURMax - oilEURMin) * randonGenerator.nextDouble();
			this.calculateoilYetToProduceExcludingCurrentProduction(this.getOilEUR(),this.getCumulativeOilProduction(),this.getCurrentOilProduction());	
		}
					
		if (this.oilEUR<=0 || this.oilEUR<=this.cumulativeOilProduction)
		{
			this.setOilEUR(this.provedReservesCrudeOil);//this is from the EIA	
		  set = this.ccProduction.keySet();
		  itr = set.iterator();  
		  while (itr.hasNext()) 
		  {
			  str = itr.next(); 
			  tmtProd= (Double)this.ccProduction.get(str);
	          if (tmtProd<=0 && Integer.parseInt(str)>=1980)
	          {
	        	  tmtProd =(Double) this.historicCCProduction.get(str);
	          }
			 this.setOilEUR(this.oilEUR+tmtProd+(Double)this.ngplProduction.get(str));
		  }
		  
		  set = this.historicCCProduction.keySet();
		  itr = set.iterator();	
		  tmtProd=0;
		  while (itr.hasNext()) 
		  {
			  str = itr.next(); 
			  // check the first year from the file: 
			  // Production_of_Crude_Oil_including_Lease_Condensate_(Thousand_Barrels_Per_Day)
			  if (Integer.parseInt(str)<1980)					
			  {
				  tmtProd= (Double)this.historicCCProduction.get(str);
				  this.setOilEUR(this.oilEUR+tmtProd);
			  }	  
		  }
			this.calculateoilYetToProduceExcludingCurrentProduction(this.getOilEUR(),this.getCumulativeOilProduction(),this.getCurrentOilProduction());	
		}
				
	}

	
	/**
	 * Initialise the crude oil (including condensate) model with observational data from US EIA
	 * The country is initialised with data loaded from ACEGESModelOilInitialisation
	 * 
	 */
	public void setCrudeCondensateOilCountryParametersfromACEGESModelInitialisation()
	{
		if(this.simulatedCrudeOilProduction !=null)
		{
			this.simulatedCrudeOilProduction.clear();
		}
		
		if(this.simulatedCrudeOilStackedProduction !=null)
		{
			this.simulatedCrudeOilStackedProduction.clear();
		}
		
		if (this.simulatedCrudeOilDemand !=null)
		{
			this.simulatedCrudeOilDemand.clear(); 
		}
		
		String yearOfIntisialisation= Integer.toString(simModel.setModelBaseYear(simModel.getUserEnteredBaseYear()));
		//System.out.println(this.getName());
		this.setCurrentOilProduction((Double)this.ccProduction.get(yearOfIntisialisation));
		double lpgCons= (Double) this.lpgConsumption.get(yearOfIntisialisation);
		this.oilDemand = (Double)this.tpConsumption.get(yearOfIntisialisation) - simModel.calculateLNGConsumpConvFactor()*lpgCons;
		this.cumulativeOilProduction=0;
		 
		Set<String> set = this.ccProduction.keySet();
		Iterator<String> itr = set.iterator();
	    String str;	    
	    this.cumulativeOilProduction=0;
	    double tmtProd=0;
	    while (itr.hasNext()) 
	    {
	    	str = itr.next(); 
	    	//cumulative production through to initialisation year.
	    	if (Integer.parseInt(str)<=simModel.whichBaseYear)
	    	{
	    		// System.out.println(str);
	    		tmtProd= (Double)this.ccProduction.get(str);
	    		if (tmtProd<=0)
	    		{
	    			tmtProd =(Double) this.historicCCProduction.get(str);
	    		}
	    		this.cumulativeOilProduction= this.cumulativeOilProduction+tmtProd;
	    	}	  
	    }
	    
	    set = this.historicCCProduction.keySet();
		itr = set.iterator();	    
	    while (itr.hasNext()) 
	    {
	    	str = itr.next(); 
	    	// check the first year from the file: 
	    	// Production_of_Crude_Oil_including_Lease_Condensate_(Thousand_Barrels_Per_Day)
	    	if (Integer.parseInt(str)<1980)					
	    	{
	    		//System.out.println(str);
	    		tmtProd= (Double)this.historicCCProduction.get(str);
	    		this.cumulativeOilProduction= this.cumulativeOilProduction+tmtProd;
	    	}	  
	    }
	    		
		if (this.currentOilProduction>0)
		{
			this.isOilProducer=true;
			this.hasBeenLive=true;
		}
		else
		{
			this.isOilProducer=false;
			this.hasBeenLive=false;
		}
		
		if (simModel.isCampbellHeapesEUROil())
		{
			if (this.oilEURAll.containsKey("EURCH"))
			{
				this.oilEUR= (Double) this.oilEURAll.get("EURCH");
				this.calculateoilYetToProduceExcludingCurrentProduction(this.getOilEUR(),this.getCumulativeOilProduction(),this.getCurrentOilProduction());	
			}
			else
			{
				this.oilEUR=0.0;
			}		
		}
		else if (simModel.isUSGSEURMeanOil())//USGSEUR MEAN
		{
			if (this.oilEURAll.containsKey("OilMeanUSGS"))
			{
				this.oilEUR=(Double) this.oilEURAll.get("CumulativeOilUSGS") + (Double) this.oilEURAll.get("RemainingOilUSGS") +
				(Double)this.oilEURAll.get("OilMeanUSGS") + (Double)this.oilEURAll.get("GrowthOilUSGS");
				this.calculateoilYetToProduceExcludingCurrentProduction(this.getOilEUR(),this.getCumulativeOilProduction(),this.getCurrentOilProduction());	
			}
			else
			{
				this.oilEUR=0.0;	
			}
		}
		else if(simModel.isUSGSEURFiveOil())//USGSEUR MAX
		{
			if (this.oilEURAll.containsKey("OilF5USGS"))
			{
				this.oilEUR=(Double) this.oilEURAll.get("CumulativeOilUSGS") + (Double) this.oilEURAll.get("RemainingOilUSGS") +
				(Double)this.oilEURAll.get("OilF5USGS") + (Double)this.oilEURAll.get("GrowthOilUSGS");
				this.calculateoilYetToProduceExcludingCurrentProduction(this.getOilEUR(),this.getCumulativeOilProduction(),this.getCurrentOilProduction());	
			}
			else
			{
				this.oilEUR=0.0;	
			}
		}
		else
		{
			if (this.oilEURAll.containsKey("EURBGR"))
			{
				this.oilEUR=(Double)this.oilEURAll.get("EURBGR");
				this.calculateoilYetToProduceExcludingCurrentProduction(this.getOilEUR(),this.getCumulativeOilProduction(),this.getCurrentOilProduction());	
			}
			else
			{
				this.oilEUR=0.0;
			}
		}
			
		if (simModel.isWEOCurrentPoliciesGrowthOil())
		{		
			this.demandGrowthRateOil = (Double) this.oilDemandGrowthAll.get("WEOPolicy01");
		}
		else if (simModel.isWEONewPoliciesGrowthOil())
		{
			this.demandGrowthRateOil = (Double) this.oilDemandGrowthAll.get("WEOPolicy02");
		}
		else
		{
			this.demandGrowthRateOil = (Double) this.oilDemandGrowthAll.get("WEOPolicy03");
		}
		
		if(simModel.isMonteCarloPeakPointOil())
		{
			double minGrowth=0.35;
			double maxGrowth=0.70;		
			this.peakPointOil= roundDecimals(minGrowth +  (maxGrowth - minGrowth) *randonGenerator.nextDouble());			
		}
		else
		{
			this.peakPointOil = simModel.getPeakDNGas();
		}
		
		
		if(simModel.isMonteCarloProductioGrowthOil())
		{
			double minGrowth=0.05;
			double maxGrowth=0.151;		
			this.productioGrowthOil= roundDecimals(minGrowth +  (maxGrowth - minGrowth) *randonGenerator.nextDouble());			
		}
		else
		{
			this.productioGrowthOil = simModel.getProductionGrowthRateOil();			
		}
		
		if(simModel.isMonteCarloDemandGrowthOil())
		{
			double minGrowth=0.0;
			double maxGrowth=0.0;
			minGrowth = Math.min( (Double)this.oilDemandGrowthAll.get("WEOPolicy02"),(Double) this.oilDemandGrowthAll.get("WEOPolicy01"));
			minGrowth = Math.min(minGrowth,(Double)this.oilDemandGrowthAll.get("WEOPolicy03"));
			maxGrowth = Math.max((Double)this.oilDemandGrowthAll.get("WEOPolicy02"),(Double) this.oilDemandGrowthAll.get("WEOPolicy01"));
			maxGrowth = Math.max(maxGrowth, (Double)this.oilDemandGrowthAll.get("WEOPolicy03"));
			this.demandGrowthRateOil = roundDecimals(minGrowth + (maxGrowth - minGrowth) * randonGenerator.nextDouble());
		}
		
		if (this.demandGrowthRateOil==0)
		{
			this.demandGrowthRateOil=0.0053;//This the average of WEO2010 policies
		}
		
		if(simModel.isMonteCarloEUROil() && this.oilEURAll.containsKey("EURCH") && this.oilEURAll.containsKey("EURBGR") &&
				this.oilEURAll.containsKey("OilF95USGS"))
		{
			double minEUR1=0.0;
			double minEUR=0.0;
			double minEUR2=0.0;
			double maxEUR=0.0;
			
			minEUR1 = Math.min((Double)this.oilEURAll.get("EURCH"), (Double) this.oilEURAll.get("EURBGR"));
			if (minEUR1<=1)
			{
				minEUR1 = Math.max((Double)this.oilEURAll.get("EURCH"), (Double) this.oilEURAll.get("EURBGR"));
			}
			
			double tempEURmin=(Double) this.oilEURAll.get("CumulativeOilUSGS") + (Double) this.oilEURAll.get("RemainingOilUSGS") +
			(Double)this.oilEURAll.get("OilF95USGS") + (Double)this.oilEURAll.get("GrowthOilUSGS");
			
			minEUR2 = Math.min(minEUR1, tempEURmin);
			
			if (minEUR2<=1)
			{
				minEUR2 = Math.max(minEUR1, tempEURmin);
			}
			
			if (minEUR2>0)
			{
				minEUR = minEUR2;
			}
			else				
			{
				minEUR= minEUR1;
			}
			
			maxEUR = Math.max((Double)this.oilEURAll.get("EURCH"), (Double)  this.oilEURAll.get("EURBGR"));
			
			double tempEURmax=(Double) this.oilEURAll.get("CumulativeOilUSGS") + (Double) this.oilEURAll.get("RemainingOilUSGS") +
			(Double)this.oilEURAll.get("OilF5USGS") + (Double)this.oilEURAll.get("GrowthOilUSGS");
			
			maxEUR = Math.max(maxEUR,tempEURmax);	
			this.oilEUR= minEUR + (maxEUR - minEUR) * randonGenerator.nextDouble();
			this.calculateoilYetToProduceExcludingCurrentProduction(this.getOilEUR(),this.getCumulativeOilProduction(),this.getCurrentOilProduction());	
		}
		
		
		if(isFromScenariosFile)
		{
			
			this.demandGrowthRateOil = roundDecimals(oilDemandGrowthMin + (oilDemandGrowthMax - oilDemandGrowthMin) * randonGenerator.nextDouble());			
			if(demandGrowthRateOil==0)
			{
				demandGrowthRateOil=0.01;
			}
			this.productioGrowthOil= roundDecimals(productioGrowthMin +  (productioGrowthMax - productioGrowthMin) *randonGenerator.nextDouble());			
			
			if(productioGrowthOil<0.05)
			{
				productioGrowthOil=0.05;
			}
			this.peakPointOil= roundDecimals(peakPointMin +  (peakPointMax - peakPointMin) *randonGenerator.nextDouble());			
			if(peakPointOil<0.35)
			{
				peakPointOil=0.5;
			}
			this.oilEUR= oilEURMin + (oilEURMax - oilEURMin) * randonGenerator.nextDouble();
			this.calculateoilYetToProduceExcludingCurrentProduction(this.getOilEUR(),this.getCumulativeOilProduction(),this.getCurrentOilProduction());	
		}
					
		if (this.oilEUR<=0 || this.oilEUR<this.cumulativeOilProduction )
		{
		  this.oilEUR=this.provedReservesCrudeOil;//this is from the EIA	
		  set = this.ccProduction.keySet();
		  itr = set.iterator();  
		  while (itr.hasNext()) 
		  {
			  str = itr.next(); 
			  tmtProd= (Double)this.ccProduction.get(str);
	          if (tmtProd<=0)
	          {
	        	  tmtProd =(Double) this.historicCCProduction.get(str);
	          }
			  this.oilEUR= this.oilEUR+tmtProd;
		  }
		  
		  set = this.historicCCProduction.keySet();
		  itr = set.iterator();	    
		  while (itr.hasNext()) 
		  {
			  str = itr.next(); 
			  // check the first year from the file: 
			  // Production_of_Crude_Oil_including_Lease_Condensate_(Thousand_Barrels_Per_Day)
			  if (Integer.parseInt(str)<1980)					
			  {
				  tmtProd= (Double)this.historicCCProduction.get(str);
				  this.oilEUR= this.oilEUR+tmtProd;
			  }	  
		  }
			this.calculateoilYetToProduceExcludingCurrentProduction(this.getOilEUR(),this.getCumulativeOilProduction(),this.getCurrentOilProduction());	
		}		
	}


	/**
	 * @param ppnp the ppnp to set
	 */
	public void setPpnp(int ppnp) {
		this.ppnp = ppnp;
	}

	/**
	 * @return the ppnp
	 */
	public int getPpnp() {
		return this.ppnp;
	}

	public int getPostPeakNP() {
		// TODO Auto-generated method stub
		return this.postnetprod;
	}

	/**
	 * @return the postnetprod
	 */
	public int getPostnetprod() {
		return postnetprod;
	}

	public String getFIPS()
	{
		return this.FIPS;
	}

	public void setFIPS(String fips)
	{
		this.FIPS=fips;
	}

	/**
	 * @param postnetprod the postnetprod to set
	 */
	public void setPostnetprod(int postnetprod) {
		this.postnetprod = postnetprod;
	}
	
	 public String toString() 
	 {
		 return 
		 this.getName()+ ", Current Oil Prod:" +  this.getCurrentOilProduction()  +
		 ", Cumulative Oil Prod:" + this.cumulativeOilProduction+
		 ", Oil Demand:" + this.oilDemand +
		 ", Oil EUR:" + this.oilEUR +
		 ", Oil Demand Growth:" + this.demandGrowthRateOil;
	 }

	/**
	 * @return the simModel
	 */
	public ACEGESApp getSimModel() {
		return simModel;
	}

	/**
	 * @param simModel the simModel to set
	 */
	public void setSimModel(ACEGESApp simModel) {
		this.simModel = simModel;
	}

	public void setPeakPointOil(double temp) {
		 this.peakPointOil= temp;
	}
	
	public double getPeakPointOil() {
		return this.peakPointOil;
	}

	// I need this for visual inspection in the simulation console.
	public double getDemandGrowthRateOil()
	{
	//	System.out.println(super.getOilDemandGrowth() + " - " + this.getName());
		return super.getDemandGrowthRateOil();
	}

	/**
	 * @return the productioGrowth
	 */
	public double getProductionGrowthRateOil() {
		return productioGrowthOil;
	}

	/**
	 * @param productioGrowthOil the productioGrowth to set
	 */
	public void setProductionGrowthRateOil(double temp) {
		this.productioGrowthOil = temp;
	}

	public double getCurrentOilProductionHist() {
		return currentOilProductionHist;
	}

	public void setCurrentOilProductionHist(double currentOilProductionHist) {
		this.currentOilProductionHist = currentOilProductionHist;
	}
	
	
}
