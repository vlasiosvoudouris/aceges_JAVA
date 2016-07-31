package aceges.countries;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.util.FastMath;
import org.jfree.data.xy.XYSeries;



import sim.engine.SimState;
import aceges.ACEGESApp;

public class CountryGasProducer extends CountryGasConsumer  
{
	public Hashtable<String, Object> dngProduction =new Hashtable(); // dry natural gas production (BCF)
	public double currentdnGasProduction=0.0; // dry natural gas
	public double cumulativednGasProduction=0.0; // dry natural gas
	private boolean isdnGasProducer=false;
	private boolean hasBeenLive=false;	
	public double dngEUR=0.0; // dry natural gas
	public Hashtable<String, Object> ngEURAll=new Hashtable();//ALL EUR
	public Hashtable<String, Object> histNaturalGas=new Hashtable();//ALL EUR
	public double dnGasYetToProduceExcludingCurrentProduction;// dry natural gas
	public double peakPointdnGas=0.5;
	public double productioGrowthDngas;
	private double currentdnGasProductionHist;
	public double dueToInternalDemand;
	private int ppnp=0;
	private int postnetprod=0;
	public double provedReservesDNGAS=0.0;
	public Hashtable<String, Object> conversionFactor=new Hashtable();//ALL EUR
	
	private static final long serialVersionUID = 1L;
	
	public CountryGasProducer(ACEGESApp simModel)
	{
		super();
		this.simModel=simModel;		
	}
	 
	/**
	 * This is the method that is called by the simulation engine to get the agent perform an action
	 * In this case, a country calculates the expected gas production and the demand for gas.  
	 */
	public void step(SimState state) 
	{
		this.calculateCurrentDNGasProductionResourceConstrainedModel();
		if(simModel.isFromGUI)
		{
			int year =simModel.whichBaseYear+(int)simModel.schedule.getSteps()+1;
			this.simulatedDNGasStackedProduction.add(year,this.currentdnGasProduction+simModel.shiftDNGASSiftedValue);
			this.simulatedDNGasProduction.add(year,this.currentdnGasProduction);
			this.simulatedDNGasDemand.add(year,this.currentDNGasDemand);
			simModel.shiftDNGASSiftedValue+=this.currentdnGasProduction;
		}
		 //this.calculateCurrentGasDemandFixedGrowth();	
		this.calculateCurrentGasDemand(); //for fixed demand growth
		//this.calculateCurrentGasDemandRegression(); //20120921
	}
	
	private void calculateCurrentDNGasProductionResourceConstrainedModel()
	{
		int year= simModel.whichBaseYear+(int)simModel.schedule.getSteps();		
		if (this.currentdnGasProduction==0 && year<2010 && !this.hasBeenLive)
		{
			this.currentdnGasProduction= (Double) this.dngProduction.get(Integer.toString(year));
			if (this.currentdnGasProduction>0)
			{
				this.isdnGasProducer=true;
				this.hasBeenLive=true; // This ONLY works once
				//System.out.println(this.getName() + ":" + year);
			}
			else
			{
				this.isdnGasProducer=false;
				this.hasBeenLive=false; // This ONLY works once
			}
		}
		this.currentdnGasProductionHist=this.currentdnGasProduction;
		
		if (!this.isdnGasProducer)
		{
			return;
		}
				
		double netWorldDemandDNG = this.simModel.getWorldNetGasDemand(); // Global world demand of DNGas
		double numberOfPPNPDNG = this.simModel.getNumberOfPPNPGas();// number of PPNP of DNG
		double totalProdPPNPDNG = this.simModel.getTotalPPNProductionGas();
		double meanProdPPNPDNG = this.simModel.calculateMeanProdPPNPGas();
		
		// consider to remove the "this.currentdnGasProduction>this.getGasDemand()" to accomodate countries that can become ppnp
		if(this.currentdnGasProduction>this.getGasDemand() && this.cumulativednGasProduction < this.peakPointdnGas*this.dngEUR)
		{
			this.ppnp=1; 
			this.postnetprod=0;
								
			//DTI for The only difference between it and the previous formula is that this does NOT add an additional increment to PPNPs corresponding to a portion net world demand for imports.
			double tempcurrentESTGasPrduction1 = 0.00;
			double tempcurrentESTGasPrduction2=0.00;	
			tempcurrentESTGasPrduction1 = this.currentdnGasProduction+this.getGasDemand()*this.getGasDemandGrowth();			
			tempcurrentESTGasPrduction2 = this.currentdnGasProduction+(this.peakPointdnGas*this.dngEUR - this.cumulativednGasProduction);
			this.dueToInternalDemand = Math.min(tempcurrentESTGasPrduction1, tempcurrentESTGasPrduction2);
			if (this.dueToInternalDemand<0)
			{
				this.dueToInternalDemand =Math.max(tempcurrentESTGasPrduction1, tempcurrentESTGasPrduction2);
			}
		}
		else if (this.currentdnGasProduction>this.getGasDemand() && this.cumulativednGasProduction >= this.peakPointdnGas*this.dngEUR)
		{
			this.postnetprod=1;
			this.ppnp=0;
		}
		else
		{
			this.ppnp=0;
			this.postnetprod=0;
		}
		
		//*******************start production************************************
		
		if (this.cumulativednGasProduction < this.peakPointdnGas*this.dngEUR)
		{
			if((this.cumulativednGasProduction/this.dngEUR)>=(this.peakPointdnGas-0.05))
			{	
				double tempcurrentESTOilPrduction1 = 0.00;
				double tempcurrentESTOilPrduction2=0.00;
				double tempcurrentESTOilPrduction3=0.00;
				
				if (ppnp==1)
				{	
					tempcurrentESTOilPrduction1 = this.currentdnGasProduction+this.currentdnGasProduction*
					this.productioGrowthDngas* (1-(0.7*(this.cumulativednGasProduction/(this.peakPointdnGas*this.dngEUR))));
					tempcurrentESTOilPrduction2 = this.currentdnGasProduction +((this.getGasDemand()*this.getGasDemandGrowth()) +
					((netWorldDemandDNG/numberOfPPNPDNG)+(((this.currentdnGasProduction-meanProdPPNPDNG)/meanProdPPNPDNG)*(netWorldDemandDNG/numberOfPPNPDNG)))) * (1-(0.7*(this.cumulativednGasProduction/(this.peakPointdnGas*this.dngEUR))));
					
					tempcurrentESTOilPrduction3 = this.currentdnGasProduction + (this.peakPointdnGas*this.dngEUR-this.cumulativednGasProduction);
					
					this.currentdnGasProduction = Math.min(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2);
					if (this.currentdnGasProduction<=0)
					{
						this.currentdnGasProduction = Math.max(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2);
					}
//km					
					tempcurrentESTOilPrduction1 = this.currentdnGasProduction;
					this.currentdnGasProduction = Math.min(this.currentdnGasProduction,tempcurrentESTOilPrduction3);
					if (this.currentdnGasProduction<=0)
					{
						this.currentdnGasProduction = tempcurrentESTOilPrduction1;
					}
//km
				}
				else // it does not attemp to fullfil the unmet demand
				{
					tempcurrentESTOilPrduction1 = this.currentdnGasProduction+this.currentdnGasProduction*
					this.productioGrowthDngas* (1-(0.7*(this.cumulativednGasProduction/(this.peakPointdnGas*this.dngEUR))));
					tempcurrentESTOilPrduction2 = this.currentdnGasProduction +(this.getGasDemand()*this.getGasDemandGrowth())  * (1-(0.7*(this.cumulativednGasProduction/(this.peakPointdnGas*this.dngEUR))));
					tempcurrentESTOilPrduction3 = this.currentdnGasProduction + (this.peakPointdnGas*this.dngEUR-this.cumulativednGasProduction);
					this.currentdnGasProduction = Math.min(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2);	
					
					if (this.currentdnGasProduction<=0)
					{
						this.currentdnGasProduction = Math.max(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2);
					}
//km					
					tempcurrentESTOilPrduction1 = this.currentdnGasProduction;
					this.currentdnGasProduction = Math.min(this.currentdnGasProduction,tempcurrentESTOilPrduction3);
					if (this.currentdnGasProduction<=0)
					{
						this.currentdnGasProduction = tempcurrentESTOilPrduction1;
					}
//km
				}
				
			}
			else
			{
				double tempcurrentESTOilPrduction1=0.00;
				double tempcurrentESTOilPrduction2=0.00;
				double tempcurrentESTOilPrduction3=0.00;
				
				if (ppnp==1)
				{
					tempcurrentESTOilPrduction1 = this.currentdnGasProduction+this.currentdnGasProduction*this.productioGrowthDngas;
					
					tempcurrentESTOilPrduction2 = this.currentdnGasProduction +((this.getGasDemand()*this.getGasDemandGrowth()) +
					(netWorldDemandDNG/numberOfPPNPDNG)+((this.currentdnGasProduction-meanProdPPNPDNG)/meanProdPPNPDNG)*(netWorldDemandDNG/numberOfPPNPDNG));
					
					tempcurrentESTOilPrduction3 = this.currentdnGasProduction + (this.peakPointdnGas*this.dngEUR-this.cumulativednGasProduction);
					
					this.currentdnGasProduction = Math.min(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2);
					if (this.currentdnGasProduction<=0)
					{
						this.currentdnGasProduction = Math.max(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2);
					}
//km					
					tempcurrentESTOilPrduction1 = this.currentdnGasProduction;
					this.currentdnGasProduction = Math.min(this.currentdnGasProduction,tempcurrentESTOilPrduction3);
					if (this.currentdnGasProduction<=0)
					{
						this.currentdnGasProduction = tempcurrentESTOilPrduction1;
					}
//km
				}
				else
				{
					//System.out.println(this.getName());
					tempcurrentESTOilPrduction1 = this.currentdnGasProduction+this.currentdnGasProduction*
					this.productioGrowthDngas;
					
					tempcurrentESTOilPrduction2 = this.currentdnGasProduction +(this.getGasDemand()*this.getGasDemandGrowth());
					
					tempcurrentESTOilPrduction3 = this.currentdnGasProduction + (this.peakPointdnGas*this.dngEUR-this.cumulativednGasProduction);
					
					this.currentdnGasProduction = Math.min(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2);	
					if (this.currentdnGasProduction<=0)
					{
						this.currentdnGasProduction = Math.max(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2);
					}
//km					
					tempcurrentESTOilPrduction1 = this.currentdnGasProduction;
					this.currentdnGasProduction = Math.min(this.currentdnGasProduction,tempcurrentESTOilPrduction3);
					if (this.currentdnGasProduction<=0)
					{
						this.currentdnGasProduction = tempcurrentESTOilPrduction1;
					}
//km
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
					
			if ((this.cumulativednGasProduction/this.dngEUR)<=(this.peakPointdnGas+0.05))
			{				
				tempcurrentESTOilPrduction1=this.currentdnGasProduction - (this.currentdnGasProduction*(this.currentdnGasProduction/this.dnGasYetToProduceExcludingCurrentProduction))*
						(1-(0.7*((this.cumulativednGasProduction-(2*(this.cumulativednGasProduction-(this.peakPointdnGas*this.dngEUR))))/(this.peakPointdnGas*this.dngEUR))));
				tempcurrentESTOilPrduction2=this.dnGasYetToProduceExcludingCurrentProduction;
				this.currentdnGasProduction = Math.min(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2);	
				this.dueToInternalDemand=this.currentdnGasProduction;
			}
			else
			{
				tempcurrentESTOilPrduction1=this.currentdnGasProduction - (this.currentdnGasProduction*(this.currentdnGasProduction/this.dnGasYetToProduceExcludingCurrentProduction));
				tempcurrentESTOilPrduction2=this.dnGasYetToProduceExcludingCurrentProduction;	
				if (Math.min(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2)>0)
				{
					this.currentdnGasProduction = Math.min(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2);	
				}
				else
				{
					this.currentdnGasProduction = Math.max(tempcurrentESTOilPrduction1, tempcurrentESTOilPrduction2);	
				}
				this.dueToInternalDemand=this.currentdnGasProduction;	
			}
		}//end of post-peak oil
		
		//A bit of calculations cleaning for next iteration
		if (this.currentdnGasProduction<0 ||Double.isNaN(this.currentdnGasProduction))
		{
			this.currentdnGasProduction = 0;
		}
		
		this.calculatendGasCumulativeProduction(this.currentdnGasProduction);	
		this.calculatednGasYetToProduceExcludingCurrentProduction();	
		if(this.dnGasYetToProduceExcludingCurrentProduction==0.0)
		{
			this.isdnGasProducer=false;
		}
		else if (this.dnGasYetToProduceExcludingCurrentProduction<0.0)
		{
			//System.out.println(this.currentdnGasProduction + " "+ this.getName() + this.oilYetToProduce);
			this.isdnGasProducer=false;
			this.ppnp=0;
			this.postnetprod=0;
			this.currentdnGasProduction=0;
			this.currentdnGasProductionHist=0;
	    }		
	}
	
	private void calculatendGasCumulativeProduction(double currentdnGasProduction2) 
	{
		this.cumulativednGasProduction= this.cumulativednGasProduction + currentdnGasProduction2;
	}

	/**
	 * Initialise the gas model with the data
	 * The country is initialised with data loaded from ACEGESModelGasInitialisationGas
	 */
	public void setDNGasCountryParametersfromACEGESModelInitialisation()
	{
		if (this.simulatedDNGasStackedProduction != null) 
		{
			this.simulatedDNGasStackedProduction.clear();	
		}
		
		if (this.simulatedDNGasProduction != null) 
		{
			this.simulatedDNGasProduction.clear();
		}
		
		if (this.simulatedDNGasDemand !=null)
		{
			this.simulatedDNGasDemand.clear();
		}
		 
		this.simModel.setModelBaseYear(this.simModel.getUserEnteredBaseYear());
		String yearOfIntisialisation= Integer.toString(simModel.whichBaseYear);
		//System.out.println(this.getName());
		this.currentdnGasProduction=(Double) this.dngProduction.get(yearOfIntisialisation);
		this.currentDNGasDemand= (Double) this.dngConsumption.get(yearOfIntisialisation);
		this.cumulativednGasProduction=0;
		Set<String> set = this.dngProduction.keySet();
		Iterator<String> itr = set.iterator();
	    String str;	    
	    double tmtProd=0;
	    //prepares the cumulative production up to (and including) the initialisation date
	    while (itr.hasNext()) 
	    {
	    	str = itr.next(); 
	    	//cumulative production through to (including) initialisation year.
	    	if (Integer.parseInt(str)<=simModel.whichBaseYear)
	    	{
	    		//System.out.println(str);
	    		tmtProd= (Double)this.dngProduction.get(str);
	    		this.cumulativednGasProduction= this.cumulativednGasProduction+tmtProd;
	    	}	  
	    }
	    
	    set=this.histNaturalGas.keySet();
	    itr=set.iterator();
	    tmtProd=0;
	    //prepares the cumulative production up to (and including) 1979 (from 1980 the data is from the EIA). 
	    while (itr.hasNext()) 
	    {
	    	str = itr.next(); 
	    	//cumulative production through to (including) initialisation year.
	    	if (Integer.parseInt(str)<1980)				
	    	{
	    		//System.out.println(str);
	    		tmtProd= (Double)this.histNaturalGas.get(str);
	    		this.cumulativednGasProduction= this.cumulativednGasProduction+tmtProd;
	    	}	  
	    }
	    	    		
		if (this.currentdnGasProduction>0)
		{
			this.isdnGasProducer=true;
			this.hasBeenLive=true;
		}
		else
		{
			this.isdnGasProducer=false;
			this.hasBeenLive=false;
		}
		

		if (simModel.isWEOCurrentPoliciesGrowthGas())
		{		
			this.dnGasDemandGrowth = (Double) this.dnGasDemandGrowthAll.get("WEOPolicy01");
		}
		else if (simModel.isWEONewPoliciesGrowthGas())
		{
			this.dnGasDemandGrowth = (Double) this.dnGasDemandGrowthAll.get("WEOPolicy02");
		}
		else if (simModel.isWEO450PoliciesGrowthGas())
		{
			this.dnGasDemandGrowth = (Double) this.dnGasDemandGrowthAll.get("WEOPolicy03");
		}
		else
		{
			this.dnGasDemandGrowth = (Double) this.dnGasDemandGrowthAll.get("WEOPolicy04");
		}
	
		
		//prepares the EUR for DNG		
		if (simModel.isCampbellHeapesEURGas())
		{
			if (this.ngEURAll.containsKey("EURCH"))
			{
				this.dngEUR= (Double) this.ngEURAll.get("EURCH");
				this.calculatednGasYetToProduceExcludingCurrentProduction();	
			}
			else
			{
				this.dngEUR=0.0;
			}	
					
		}
		else if (simModel.isBGREURGas())//BGRGAR
		{
			if (this.ngEURAll.containsKey("EURBGR"))
			{
				this.dngEUR=(Double) this.ngEURAll.get("EURBGR");
				this.calculatednGasYetToProduceExcludingCurrentProduction();
			}
			else
			{
				this.dngEUR=0.0;
			}
		}
		else if (simModel.isUSGSEURMeanGas())//USGSEUR MEAN
		{
			if (this.ngEURAll.containsKey("GasMeanUSGS"))
			{
				this.dngEUR=(Double) this.ngEURAll.get("CumulativeGasUSGS") + (Double) this.ngEURAll.get("RemainingGasUSGS") +
						(Double)this.ngEURAll.get("GasMeanUSGS") + (Double)this.ngEURAll.get("GrowthGasUSGS");
				this.calculatednGasYetToProduceExcludingCurrentProduction();
			}
			else
			{
				this.dngEUR=0.0;
			}
		}
		else //DEFAULT = USGS (this is based on Maximum (5%). 
		{
			if (this.ngEURAll.containsKey("GasF5USGS"))
			{
				this.dngEUR=(Double) this.ngEURAll.get("CumulativeGasUSGS") + (Double) this.ngEURAll.get("RemainingGasUSGS") +
			    (Double)this.ngEURAll.get("GasF5USGS") + (Double)this.ngEURAll.get("GrowthGasUSGS");
			    this.calculatednGasYetToProduceExcludingCurrentProduction();
			}
			else
			{
				this.dngEUR=0.0;
			}
		}
		
		if(simModel.isMonteCarloPeakPointDNGas())
		{
//			double minGrowth=0.45;
//			double maxGrowth=0.75;		
			double minGrowth=0.50;
			double maxGrowth=0.70;		
			this.peakPointdnGas= roundDecimals(minGrowth +  (maxGrowth - minGrowth) *randonGenerator.nextDouble());			
		}
		else
		{
			this.peakPointdnGas = simModel.getPeakDNGas();
		}
		
		if(simModel.isMonteCarloProductioGrowthDNGas())
		{
//			double minGrowth=0.05;
//			double maxGrowth=0.20;		
			double minGrowth=0.10;
			double maxGrowth=0.20;		
			this.productioGrowthDngas= roundDecimals(minGrowth +  (maxGrowth - minGrowth) *randonGenerator.nextDouble());			
		}	
		else
		{
			this.productioGrowthDngas = simModel.getProductionGrowthDNGas();			
		}
				
		if(simModel.isMonteCarloDemandGrowthGas())
		{
			double minGrowth= Math.min((Double) this.dnGasDemandGrowthAll.get("WEOPolicy01"), (Double) this.dnGasDemandGrowthAll.get("WEOPolicy02"));
			minGrowth= Math.min(minGrowth, (Double) this.dnGasDemandGrowthAll.get("WEOPolicy03"));
//KM20120423
			minGrowth= Math.min(minGrowth, (Double) this.dnGasDemandGrowthAll.get("WEOPolicy04"));
					
			double maxGrowth = Math.max((Double) this.dnGasDemandGrowthAll.get("WEOPolicy01"), (Double) this.dnGasDemandGrowthAll.get("WEOPolicy02"));
			maxGrowth= Math.max(maxGrowth, (Double) this.dnGasDemandGrowthAll.get("WEOPolicy03"));
//KM20120423
			maxGrowth= Math.max(maxGrowth, (Double) this.dnGasDemandGrowthAll.get("WEOPolicy04"));
			this.dnGasDemandGrowth = roundDecimals(minGrowth + (maxGrowth - minGrowth) * randonGenerator.nextDouble());
		}
		
		if (this.dnGasDemandGrowth==0)
		{
//KM		this.dnGasDemandGrowth=0.019;//This the average of WEO10NewPolicies (I do not believe that 0 is realistic!
		}
		
		
		if(simModel.isMonteCarloEURDNGas() && this.ngEURAll.containsKey("EURCH") && 
				this.ngEURAll.containsKey("EURBGR") && 
				this.ngEURAll.containsKey("GasF95USGS"))
		{
			double minEUR1=0.0;
			double minEUR=0.0;
			double minEUR2=0.0;
			double maxEUR=0.0;
			
			minEUR1 = Math.min((Double)this.ngEURAll.get("EURCH"), (Double) this.ngEURAll.get("EURBGR"));
			if (minEUR1<=1)
			{
				minEUR1 = Math.max((Double)this.ngEURAll.get("EURCH"), (Double) this.ngEURAll.get("EURBGR"));
			}
			
			double tempEURmin=(Double) this.ngEURAll.get("CumulativeGasUSGS") + (Double) this.ngEURAll.get("RemainingGasUSGS") +
					(Double)this.ngEURAll.get("GasF95USGS") + (Double)this.ngEURAll.get("GrowthGasUSGS");
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

			double tmtProd2 = 0;
			double tmtCumProd = this.cumulativednGasProduction;
		    while (itr.hasNext()) 
		    {
		    	str = itr.next(); 
		    	//cumulative production through to (including) initialisation year.
		    	if (Integer.parseInt(str)>simModel.whichBaseYear)//VV20120509 - check line 334
		    	{
		    		// System.out.println(str);
		    		tmtProd2= (Double)this.dngProduction.get(str);
		    		tmtCumProd = tmtCumProd + tmtProd2;
		    	}	  
		    }

			if(minEUR<(tmtCumProd+this.provedReservesDNGAS))
			{
				minEUR=tmtCumProd+this.provedReservesDNGAS;
			}
			
			double tempEURmax= (Double) this.ngEURAll.get("CumulativeGasUSGS") + (Double) this.ngEURAll.get("RemainingGasUSGS") +
					(Double)this.ngEURAll.get("GasF5USGS") + (Double)this.ngEURAll.get("GrowthGasUSGS");
			maxEUR = Math.max((Double)this.ngEURAll.get("EURCH"), (Double) this.ngEURAll.get("EURBGR"));
			maxEUR = Math.max(maxEUR, tempEURmax);	
			if (maxEUR<minEUR)
			{
				maxEUR=minEUR;
			}
			this.dngEUR= minEUR + (maxEUR - minEUR) * randonGenerator.nextDouble();
			this.calculatednGasYetToProduceExcludingCurrentProduction();
		}
		
		//if I do not have the EUR for the counrty, estimate it with the historic data up to NOW. 
		//NOTE: this is just a substitution for EUR. 
		if (this.dngEUR<=0 || this.dngEUR<=(this.cumulativednGasProduction+this.provedReservesDNGAS))
		{
			this.dngEUR=this.provedReservesDNGAS;
			set = this.dngProduction.keySet();
			itr = set.iterator();
			while (itr.hasNext()) 
			{
				str = itr.next(); 
				tmtProd= (Double)this.dngProduction.get(str);
				this.dngEUR= this.dngEUR+tmtProd;  
		    }
		    
			set=this.histNaturalGas.keySet();
		    itr=set.iterator();
		    tmtProd=0;
		    //prepares the cumulative production up to (and including) 1979 (from 1980 the data is from the EIA). 
		    while (itr.hasNext()) 
		    {
		    	str = itr.next(); 
		    	//cumulative production through to (including) initialisation year.
		    	if (Integer.parseInt(str)<1980)					
		    	{
		    		// System.out.println(str);
		    		tmtProd= (Double)this.histNaturalGas.get(str);
		    		this.dngEUR= this.dngEUR+tmtProd;
		    	}	  
		    } 
		    this.calculatednGasYetToProduceExcludingCurrentProduction();
		}	

	/**	to CHECK IT 
	//20120921KM
	GDPScenario();
	populationScenario();
	priceScenario();
	//20120921KM
	 * 
	 */
	}

	/**
	 * 
	 * @param step
	 */
	public void setDNGasCountryParametersfromACEGESModelStep(int step)
	{
		String y = Integer.toString(simModel.whichBaseYear + step + 1);
		GDPScenarioStep(y);
		populationScenarioStep(y);
		priceScenarioStep(y);
	}

	public void GDPScenarioStep(String y)
	{
		//GDP growth in 2035: Policy>Market>Base>GPI>Sustainability>Security
		double minGDP = (Double) this.GDPGrowSec.get(y);
		double maxGDP = (Double) this.GDPGrowPol.get(y);
		minGDP= minGDP*0.50;
		maxGDP = maxGDP*2;
		this.scenarioGDP = minGDP + (maxGDP - minGDP) * randonGenerator.nextDouble();
		//System.out.println(this.getName()+":"+minGDP);
	}
	
	public void populationScenarioStep(String y)
	{
		double minPop = (Double) this.popLow.get(y);
		double maxPop = (Double) this.popHig.get(y);
		this.scenarioPopulation = minPop + (maxPop - minPop) * randonGenerator.nextDouble();
	}
	
	public void priceScenarioStep(String y)
	{
		double minPrice = (Double) this.priceCur.get(y);
		double maxPrice = (Double) this.price450.get(y);
		this.currentPrice = minPrice + (maxPrice - minPrice) * randonGenerator.nextDouble();
	}
	//20121012KM
	
	//20120921KM
	public void GDPScenario()
	{
		//GDP growth in 2035: Policy>Market>Base>GPI>Sustainability>Security
		double minGDP = this.getMinValue(this.GDPGrowSec);
		double maxGDP = this.getMaxValue(this.GDPGrowPol);
		this.scenarioGDP = minGDP + (maxGDP - minGDP) * randonGenerator.nextDouble();
		//System.out.println(this.getName()+":"+minGDP);
	}
	
	public void populationScenario()
	{
		double minPop = this.getMinValue(this.popLow);
		double maxPop = this.getMaxValue(this.popHig);
		this.scenarioPopulation = minPop + (maxPop - minPop) * randonGenerator.nextDouble();
	}
	
	public void priceScenario()
	{
		double minPrice = this.getMinValue(this.priceCur);
		double maxPrice = this.getMaxValue(this.price450);
		this.currentPrice = minPrice + (maxPrice - minPrice) * randonGenerator.nextDouble();
	}
	//20120921KM
	
	public String toString() 
	{
		return 
				this.getName()+ ", Current Gas Prod:" +  this.currentdnGasProduction  +
				", Cumulative Gas Prod:" + this.cumulativednGasProduction+	
				", Gas Demand:" + this.currentDNGasDemand +
				", Gas EUR:" + this.dngEUR +
				", Gas Demand Growth:" + this.dnGasDemandGrowth;
	}


	/**
	 * This effectively calculates the gas yet to produce at the beggining of time t. 
	 */
	private void calculatednGasYetToProduceExcludingCurrentProduction() {
		this.dnGasYetToProduceExcludingCurrentProduction=this.dngEUR-(this.cumulativednGasProduction-this.currentdnGasProduction);
		
	}

	public int getPpnp() {
		return ppnp;
	}

	public int getPostPeakNP() {
		return postnetprod;
	}

	/**
	 * @return the currentdnGasProduction
	 */
	public double getCurrentdnGasProduction() {
		return currentdnGasProduction;
	}

	/**
	 * @param currentdnGasProduction the currentdnGasProduction to set
	 */
	public void setCurrentdnGasProduction(double currentdnGasProduction) {
		this.currentdnGasProduction = currentdnGasProduction;
	}
	
	public double getDNGasReserveToProductionRatio() 
	{
		return this.dnGasYetToProduceExcludingCurrentProduction/this.currentdnGasProduction;
	}

	public int getCurrentGasProductionHist() 
	{
		return 0;
	}

//20120921KM
	private double getMinValue(Hashtable data)
	{
		SummaryStatistics stats = new SummaryStatistics();
	    Enumeration e = data.keys();
	    
	    while (e.hasMoreElements())
	    {
	    	stats.addValue((Double) data.get(e.nextElement()));
	    }
	    return stats.getMin();
	}

	private double getMaxValue(Hashtable data)
	{
		SummaryStatistics stats = new SummaryStatistics();
	    Enumeration e = data.keys();
	    
	    while (e.hasMoreElements())
	    {
	    	stats.addValue((Double) data.get(e.nextElement()));
	    }
	    return stats.getMax();
	}
	//20120921KM
}
