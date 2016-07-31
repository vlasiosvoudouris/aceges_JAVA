package aceges.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import sim.engine.SimState;
import sim.engine.Steppable;
import aceges.ACEGESApp;
import aceges.countries.CountryOilProducer;
import aceges.countries.EnergyAgent;
import aceges.gui.GUIACEGESApp;
import aceges.utilities.io.WriteCSV;

public class FileOilReporter  implements Steppable
{
	//private GUIACEGESApp myGUIModel;
	private ACEGESApp acegesApp;
	private WriteCSV csvWriter;
	private WriteCSV csvWorldData;
	public int simulationNumber;
	private String[] data;
	private String[] worldData;
	private double worldTotalProduction;
	private double worldTotalDemand;
	private WriteCSV histOilDataWriter;
	private String[] histOilData;
	
	public FileOilReporter (GUIACEGESApp guiACEGESApp)
	{
		this(guiACEGESApp.getModelACEGES());		
	}
	
	public FileOilReporter (ACEGESApp ACEGESApp)
	{
		this.acegesApp=ACEGESApp;
		
		this.csvWriter = new WriteCSV("dataSimulatedOutputs/Oil/SimulatedOilData.csv");
		 data = new String[12];
	      data[0]= "OilCountry";
	      data[1]= "OilProduction";
	      data[2]= "Year";
	      data[3]= "OilEUR";
	      data[4]= "OilDemandGrowth";
	      data[5]= "SimulationNumber";
	      data[6]= "OilPeakPoint";
	      data[7]= "OilCumulativeProduction";
	      data[8]= "OilDemand";
	      data[9]= "OilReservesToProductionRatio";
	      data[10]= "ProductionGrowth";
	      data[11]= "FIPS_CNTRY";
	      try {
			this.csvWriter.writeRecord(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 this.csvWorldData = new WriteCSV("dataSimulatedOutputs/Oil/WorldOilData.csv");
		 worldData = new String[4]; 
		 worldData[0]= "OilProduction";
		 worldData[1]= "Year";
		 worldData[2]= "SimulationNumber";
		 worldData[3]= "OilDemand";
	      try {
	    	  this.csvWorldData.writeRecord(worldData);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			 this.histOilDataWriter = new WriteCSV("dataSimulatedOutputs/Oil/HistoricalCrudeOil.csv");
			 histOilData = new String[8]; 
			 histOilData[0]= "Country";
			 histOilData[1]= "Year";
			 histOilData[2]= "CrudeOilProduction";
			 histOilData[3]= "OilEURCH";
			 histOilData[4]= "OilEURUSGSMax";
			 histOilData[5]= "OilEURBGR";
			 histOilData[6]= "CumulativeCrudeOil";
			 histOilData[7]= "CrudeOilProdTOCumulativeProd";
		      try {
		    	  this.histOilDataWriter.writeRecord(histOilData);
		    	  this.saveHisrtoricalDataCrudeOil();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	
	/**
	 *  this method is called imidiatly BEFORE the next step. THe first NEXT step is 1. Thus, the data reported here is
	 *	the first simulated year. 	
	 */ 	
	public void step(SimState state) 
	{
		this.saveResults();
	}

	/**
	 *  this method is called imidiatly BEFORE the next step. THe first NEXT step is 1. Thus, the data reported here is
	 *	the first simulated year. 	
	 */
	public void saveResults() 
	{
		worldTotalProduction=0.0;
		worldTotalDemand=0.0;
		double histTotalProd=0.0;
		double histTotalDemand=0.0;
		ArrayList<CountryOilProducer> agents= this.acegesApp.oilAgentList;
		int yearOfSimulation=  acegesApp.whichBaseYear+(int)acegesApp.schedule.getSteps();
		String year= Integer.toString(yearOfSimulation);
		Iterator<CountryOilProducer> itr = agents.iterator();
		while (itr.hasNext()) 
		{
		    CountryOilProducer element = (CountryOilProducer) itr.next();  
			//System.out.println(element.getName());
		      data[0]= element.getName();
		      data[1]= Double.toString(element.getCurrentOilProduction()*1000);
		      data[2]= year;
		      data[3]= Double.toString(element.getOilEUR()*1000);
		      data[4]= Double.toString(element.getDemandGrowthRateOil());
		      data[5]= Double.toString(simulationNumber);
		      data[6]= Double.toString(element.getPeakPointOil());
		      data[7]=Double.toString(element.getCumulativeOilProduction()*1000);
		      data[8]= Double.toString(element.oilDemand*1000);
		      data[9]= Double.toString(element.getOilReserveToProductionRatio());
		      data[10]=Double.toString(element.getProductionGrowthRateOil());
		      data[11]=element.FIPS;
		      worldTotalProduction = worldTotalProduction + element.getCurrentOilProduction()*1000;
		  	  worldTotalDemand = worldTotalDemand + element.getOilDemand()*1000;
		  	  if(element.ccProduction.containsKey(year))
		  	  {
		  		  histTotalProd = histTotalProd+(Double) element.ccProduction.get(year)*1000;
		  	  }
		  	  try {
				this.csvWriter.writeRecord(data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}      
		      
		}
		 if (acegesApp.isFromGUI)
		 {
			 acegesApp.testGraphicTotalProductionOil.add(yearOfSimulation,worldTotalProduction);
			 //acegesApp.testGraphicTotalDemandOil.add(fistYearOfSimulation,worldTotalDemand);
			 acegesApp.testGraphicTotalDemandOil.add(yearOfSimulation,histTotalProd);
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
	public void closeCSVFile()
	{
		this.csvWriter.close();
		this.csvWorldData.close();
	}
	
	public void saveHisrtoricalDataCrudeOil()
	{
		ArrayList<CountryOilProducer> agents= this.acegesApp.oilAgentList;
		Iterator<CountryOilProducer> itr = agents.iterator();
		while (itr.hasNext()) 
		{
		   CountryOilProducer element = (CountryOilProducer) itr.next();  	
		   Double oilEURCH=0.0;
		   Double oilEURUSGS=0.0;
		   Double oilEURBGR=0.0;
		   
		   if (element.oilEURAll.containsKey("EURCH"))
		   {
		    oilEURCH=   (Double) element.oilEURAll.get("EURCH");
		   }
		   if (element.oilEURAll.containsKey("OilF5USGS"))
		   {
		        oilEURUSGS= (Double) element.oilEURAll.get("CumulativeOilUSGS") + (Double) element.oilEURAll.get("RemainingOilUSGS") +
				  (Double)element.oilEURAll.get("OilF5USGS") + (Double)element.oilEURAll.get("GrowthOilUSGS");
		    }
		   if (element.oilEURAll.containsKey("EURBGR"))
		   {
		  	oilEURBGR=(Double)element.oilEURAll.get("EURBGR");
		   }
		   
		    Vector v = new Vector(element.historicCCProduction.keySet());
		    Collections.sort(v);
		    String str;
		    double tempCumulative=0.0;
		    double tempProduction=0.0;
		    for (Enumeration el = v.elements(); el.hasMoreElements();) 
		    {
		    	  tempProduction=0.0;	
			      str = (String)el.nextElement(); 
		    	  if (Integer.parseInt(str)<1995)
			         {
		    		   histOilData[0]= element.getName();
		    		   histOilData[1]= str;
		    		   
		    		   tempProduction= (Double)element.historicCCProduction.get(str);
		    		   if (tempProduction>0)
		    		   {
		    			   histOilData[2]= Double.toString(tempProduction);
		    		   }
		    		   else
		    		   {
		    			   histOilData[2]="NA";
		    		   }
		    		   histOilData[3]= Double.toString(oilEURCH);
		  			   histOilData[4]= Double.toString(oilEURUSGS);
		  			   histOilData[5]= Double.toString(oilEURBGR);
		  			   if (tempProduction>0)
		  			   {
			  			   tempCumulative = tempCumulative+ (Double)element.historicCCProduction.get(str);
			  			   histOilData[6]= Double.toString(tempCumulative);
						   histOilData[7]= Double.toString((Double)element.historicCCProduction.get(str)/tempCumulative);
		  			   }
		  			   else
		  			   {
		  				 histOilData[6]="NA";
		  				 histOilData[7]="NA";
		  			   }

		  			   try {
							histOilDataWriter.writeRecord(histOilData);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	  
			         }	  
		    }//end of historicCCProduction
		   
		    v = new Vector(element.ccProduction.keySet());
		    Collections.sort(v);
		    for (Enumeration el = v.elements(); el.hasMoreElements();) 
		    {
			      str = (String)el.nextElement(); 
		    	  if (Integer.parseInt(str)>=1995)
			         {
		    		   histOilData[0]= element.getName();
		    		   histOilData[1]= str;
		    		   tempProduction=(Double)element.ccProduction.get(str);
		    		   if (tempProduction<=0 && element.historicCCProduction.containsKey(str))
		    		   {
		    			   tempProduction= (Double)element.historicCCProduction.get(str);
		    		   }
		    		   
		    		   if(tempProduction>0)
		    		   {
		    			   histOilData[2]=Double.toString(tempProduction);
			    		   
		    		   }
		    		   else
		    		   {
		    			   histOilData[2]="NA";
		    		   }
		    		   histOilData[3]= Double.toString(oilEURCH);
		  			   histOilData[4]= Double.toString(oilEURUSGS);
		  			   histOilData[5]= Double.toString(oilEURBGR);
		  			   if(tempProduction>0)
		  			   {
		  				 tempCumulative = tempCumulative+ tempProduction;   
		  				 histOilData[6]= Double.toString(tempCumulative);
		  				 histOilData[7]= Double.toString(tempProduction/tempCumulative);
		  			   }
		  			   else
		  			   {
		  				 histOilData[6]="NA";
		  				 histOilData[7]="NA";
		  			   }
		  			  
					  
					   try {
							histOilDataWriter.writeRecord(histOilData);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			         }		    	  
		    }//end of ccProduction	   
		}//end of agent iteration
		
		histOilDataWriter.flush();
		histOilDataWriter.close();
	}


}
