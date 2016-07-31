package aceges.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import sim.engine.SimState;
import sim.engine.Steppable;
import aceges.ACEGESApp;
import aceges.countries.CountryGasProducer;
import aceges.countries.CountryOilProducer;
import aceges.gui.GUIACEGESApp;
import aceges.utilities.io.WriteCSV;

public class FileDNGReporter implements Steppable
{
	
	//private GUIACEGESApp myGUIModel;
	private ACEGESApp acegesApp;
	private WriteCSV csvWriter;
	private WriteCSV csvWorldData;
	public long simulationNumber =0;
	private String[] data;
	private String[] worldData;
	private Double worldTotalProduction;
	private double worldTotalDemand;
	private String[] histDNGasData;
	private WriteCSV histDNGasDataWriter;
	
	public FileDNGReporter (GUIACEGESApp guiACEGESApp)
	{
		this(guiACEGESApp.getModelACEGES());				
	}
	
	public FileDNGReporter (ACEGESApp acegesAPP)
	{
		acegesApp=acegesAPP;
		
		this.csvWriter = new WriteCSV("dataSimulatedOutputs/DNGas/SimulatedDNGASData.csv");
		 data = new String[12];
	      data[0]= "DNGasCountry";
	      data[1]= "DNGasProduction";
	      data[2]= "Year";
	      data[3]= "DNGasEUR";
	      data[4]= "DNGasDemandGrowth";
	      data[5]= "SimulationNumber";
	      data[6]= "DNGasPeakPoint";
	      data[7]= "DNGasCumulativeProduction";
	      data[8]= "DNGasDemand";
	      data[9]= "DNGasReservesToProductionRatio";
	      data[10]= "DNGasProductionGrowth";
	      data[11]= "FIPS_CNTRY";
	      try {
			this.csvWriter.writeRecord(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 this.csvWorldData = new WriteCSV("dataSimulatedOutputs/DNGas/WorldDNGasData.csv");
		 worldData = new String[5]; 
		 worldData[0]= "DNGasProduction";
		 worldData[1]= "Year";
		 worldData[2]= "SimulationNumber";
		 worldData[3]= "DNGAsDemand";
	      try {
	    	  this.csvWorldData.writeRecord(worldData);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 this.histDNGasDataWriter = new WriteCSV("dataSimulatedOutputs/DNGas/HistoricalDNGas.csv");
			 histDNGasData = new String[8]; 
			 histDNGasData[0]= "Country";
			 histDNGasData[1]= "Year";
			 histDNGasData[2]= "NaturalGasProduction";
			 histDNGasData[3]= "NaturalGasEURCH";
			 histDNGasData[4]= "NaturalGasEURUSGSMax";
			 histDNGasData[5]= "NaturalGasEURBGR";
			 histDNGasData[6]= "CumulativeNaturalGas";
			 histDNGasData[7]= "NaturalGasProdTOCumulativeProd";
		      try {
		    	  this.histDNGasDataWriter.writeRecord(histDNGasData);
		    	  this.saveHisrtoricalDataDNGas();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
		
	public void step(SimState state) 
	{
		this.saveResults();
	}
	
	public void saveResults() 
	{
		worldTotalProduction=0.0;
		worldTotalDemand=0.0;
		double histTotalProd=0.0;
		double histTotalDemand=0.0;
		ArrayList<CountryGasProducer> agents= acegesApp.gasAgentList;
		int yearOfSimulation=  acegesApp.whichBaseYear+(int)acegesApp.schedule.getSteps();
		Iterator<CountryGasProducer> itr = agents.iterator();
		String year= Integer.toString(yearOfSimulation);
		while (itr.hasNext()) 
		{
			
		      CountryGasProducer element = (CountryGasProducer) itr.next();  
		      data[0]= element.getName();
		      data[1]= Double.toString(element.currentdnGasProduction);
		      data[2]= year;
		      data[3]= Double.toString(element.dngEUR);
		      data[4]= Double.toString(element.getGasDemandGrowth());
		      data[5]= Double.toString(simulationNumber);
		      data[6]= Double.toString(element.peakPointdnGas);
		      data[7]=Double.toString(element.cumulativednGasProduction);
		      data[8]= Double.toString(element.currentDNGasDemand);
		      data[9]= Double.toString(element.getDNGasReserveToProductionRatio());
		      data[10]=Double.toString(element.productioGrowthDngas);
		      data[11]=element.FIPS;
		      worldTotalProduction = worldTotalProduction + element.currentdnGasProduction;
		  	  worldTotalDemand = worldTotalDemand + element.getGasDemand();
		  	  if(element.dngProduction.containsKey(year))
		  	  {
		  		  histTotalProd = histTotalProd+(Double) element.dngProduction.get(year);
		  	  }
		  	  try {
				this.csvWriter.writeRecord(data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}      
		      
		}
		 if(acegesApp.isFromGUI)
		 {
			 acegesApp.testGraphicTotalProductionGas.add(yearOfSimulation,worldTotalProduction);
			// acegesApp.testGraphicTotalDemandGas.add(fistYearOfSimulation,worldTotalDemand);
			acegesApp.testGraphicTotalDemandGas.add(yearOfSimulation,histTotalProd);
		 }
	  	 worldData[0]= Double.toString(worldTotalProduction);
		 worldData[1]= year;
		 worldData[2]= Double.toString(simulationNumber);
		 worldData[3]= Double.toString(worldTotalDemand);
	      try {
	    	  this.csvWorldData.writeRecord(worldData);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void saveHisrtoricalDataDNGas()
	{
		ArrayList<CountryGasProducer> agents= acegesApp.gasAgentList;
		Iterator<CountryGasProducer> itr = agents.iterator();
		while (itr.hasNext()) 
		{
		   CountryGasProducer element = (CountryGasProducer) itr.next();  	
		   Double oilEURCH=0.0;
		   Double oilEURUSGS=0.0;
		   Double oilEURBGR=0.0;
		   
		   if (element.ngEURAll.containsKey("EURCH"))
		   {
		    oilEURCH=   (Double) element.ngEURAll.get("EURCH");
		   }
		   if (element.ngEURAll.containsKey("GasF5USGS"))
		   {
		        oilEURUSGS= (Double) element.ngEURAll.get("CumulativeGasUSGS") + (Double) element.ngEURAll.get("RemainingGasUSGS") +
			    (Double)element.ngEURAll.get("GasF5USGS") + (Double)element.ngEURAll.get("GrowthGasUSGS");
		    }
		   if (element.ngEURAll.containsKey("EURBGR"))
		   {
		  	oilEURBGR=(Double)element.ngEURAll.get("EURBGR");
		   }
		   
		    Vector v = new Vector(element.histNaturalGas.keySet());
		    Collections.sort(v);
		    String str;
		    double tempCumulative=0.0;
		    double tempProduction=0.0;
		    for (Enumeration el = v.elements(); el.hasMoreElements();) 
		    {
		    	  tempProduction=0.0;
			      str = (String)el.nextElement(); 
		    	  if (Integer.parseInt(str)<1980)
			         {
		    		   histDNGasData[0]= element.getName();
		    		   histDNGasData[1]= str;
		    		   tempProduction=(Double)element.histNaturalGas.get(str);
		    		   if (tempProduction>0)
		    		   {
		    			   histDNGasData[2]= Double.toString(tempProduction);
		    		   }
		    		   else
		    		   {
		    			   histDNGasData[2]="NA";
		    		   }		
		    		   histDNGasData[3]= Double.toString(oilEURCH);
		  			   histDNGasData[4]= Double.toString(oilEURUSGS);
		  			   histDNGasData[5]= Double.toString(oilEURBGR);
		  			  if (tempProduction>0)
		  			  {
		  			   tempCumulative = tempCumulative+ tempProduction;
		  			   histDNGasData[6]= Double.toString(tempCumulative);
					   histDNGasData[7]= Double.toString(tempProduction/tempCumulative);
		  			  }
		  			  else
		  			  {
		  				   histDNGasData[6]= "NA";
						   histDNGasData[7]= "NA";
		  			  }
		  			   try {
							histDNGasDataWriter.writeRecord(histDNGasData);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	  
			         }	  
		    }//end of histNaturalGas
		   
		    v = new Vector(element.dngProduction.keySet());
		    Collections.sort(v);
		    for (Enumeration el = v.elements(); el.hasMoreElements();) 
		    {
		    	 tempProduction=0.0;
			      str = (String)el.nextElement(); 
		    	  if (Integer.parseInt(str)>=1980)
			         {
		    		   histDNGasData[0]= element.getName();
		    		   histDNGasData[1]= str;
		    		   tempProduction= (Double)element.dngProduction.get(str);
		    		   if (tempProduction>0)
		    		   {
		    			   histDNGasData[2]=Double.toString(tempProduction);
		    		   }
		    		   else
		    		   {
		    			   histDNGasData[2]="NA";
		    		   }	    		  
		    		   histDNGasData[3]= Double.toString(oilEURCH);
		  			   histDNGasData[4]= Double.toString(oilEURUSGS);
		  			   histDNGasData[5]= Double.toString(oilEURBGR);
		  			   if (tempProduction>0)
		  			   {
		  				 tempCumulative = tempCumulative+ tempProduction;
		  			     histDNGasData[6]= Double.toString(tempCumulative);
					     histDNGasData[7]= Double.toString(tempProduction/tempCumulative);
		  			   }
		  			   else
		  			   {
		  				histDNGasData[6]= "NA";
						histDNGasData[7]= "NA";
		  			   }
					   try {
							histDNGasDataWriter.writeRecord(histDNGasData);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			         }		    	  
		    }//end of ccProduction	   
		}//end of agent iteration
		
		histDNGasDataWriter.flush();
		histDNGasDataWriter.close();
	}

	public void closeCSVFile()
	{
		this.csvWriter.close();
		this.csvWorldData.close();
	}

}
