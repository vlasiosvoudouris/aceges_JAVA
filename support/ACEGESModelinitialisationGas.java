package aceges.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import aceges.ACEGESApp;
import aceges.countries.CountryGasProducer;
import aceges.utilities.io.CSVFileReader;

public class ACEGESModelinitialisationGas 
{
	public  Hashtable<String, CountryGasProducer> gasCountries = new Hashtable<String, CountryGasProducer>();
	public  Hashtable<String, String> countryFIPS = new Hashtable<String, String>();
	public  ACEGESApp myModel=null;
	
	public  ArrayList <String>dataEIAGas = new ArrayList<String>(); //various data files
	
	static final int DNGCONSUMPTION=0;
	static final int DNGDEMANDGROWTH=1;
	static final int DNGPRODUCTION=2;
	static final int EURDNGUS=3;
	static final int EURDNGCH=4;
	static final int EURDNGBG=5;
	static final int HISTORICNATURALGAS=6;
	static final int PROVEDDNGASRESERVES=7;	
	static final int CONVERSION=8;
	static final int EURDNGUSUP=9;//km20120524

	static final int GDPHIST=100;//km20120921
	static final int GDPGROWBAS=101;//km20120921
	static final int GDPGROWGPI=102;//km20120921
	static final int GDPGROWMAR=103;//km20120921
	static final int GDPGROWPOL=104;//km20120921
	static final int GDPGROWSEC=105;//km20120921
	static final int GDPGROWSUS=106;//km20120921

	static final int POPHIST=110;//km20120921
	static final int POPFUTURELOW=111;//km20120921
	static final int POPFUTUREMED=112;//km20120921
	static final int POPFUTUREHIG=113;//km20120921
	static final int POPFUTURECON=114;//km20120921

	static final int GASPRICECUR=121;//km20120921
	static final int GASPRICENEW=122;//km20120921
	static final int GASPRICE450=123;//km20120921
	
	static final int GASDEMANDCOEF=13;//km20120921
	
	public ACEGESModelinitialisationGas(ACEGESApp target)
	{
		this.myModel=target;
		this.createCountries(readOilConsumption("data/Countries.csv"));
		
		dataEIAGas= this.readOilConsumption("data/FossilFuels/gas/Consumption/DryNaturalGasConsumption_BilCUF.csv");
		initialiseCountries(dataEIAGas, DNGCONSUMPTION);
		dataEIAGas= this.readOilConsumption("data/FossilFuels/gas/Consumption/GasDemandGrowthWEO.csv");
		initialiseCountries(dataEIAGas, DNGDEMANDGROWTH);
		dataEIAGas= this.readOilConsumption("data/FossilFuels/gas/Production/DryNaturalGasProduction_BilCUF.csv"); 
		initialiseCountries(dataEIAGas, DNGPRODUCTION);	//production from EIA	
		dataEIAGas= this.readOilConsumption("data/FossilFuels/gas/Production/EUR/EURGasUSGS_BilCUF.csv"); 
		initialiseCountries(dataEIAGas, EURDNGUS);
		dataEIAGas= this.readOilConsumption("data/FossilFuels/gas/Production/EUR/EURGasCampbell_BilCUF.csv"); 
		initialiseCountries(dataEIAGas, EURDNGCH);
		dataEIAGas= this.readOilConsumption("data/FossilFuels/gas/Production/EUR/EURGasBGR_BilCUM.csv"); 
		initialiseCountries(dataEIAGas, EURDNGBG);
		dataEIAGas= this.readOilConsumption("data/FossilFuels/gas/Production/historicalFixed/historicalNaturalGas.csv"); 
		initialiseCountries(dataEIAGas, HISTORICNATURALGAS);
		dataEIAGas= this.readOilConsumption("data/FossilFuels/gas/Production/EUR/Proved_Reserves_of_Natural_Gas_(Trillion_Cubic_Feet).csv"); 
		initialiseCountries(dataEIAGas, PROVEDDNGASRESERVES);
		dataEIAGas= this.readOilConsumption("data/FossilFuels/gas/Production/historicalFixed/GasConversion.csv"); 
		initialiseCountries(dataEIAGas, CONVERSION);
		dataEIAGas= this.readOilConsumption("data/FossilFuels/gas/Production/EUR/EURGasUSGSupdate_BilCUF.csv");//km20120524 
		initialiseCountries(dataEIAGas, EURDNGUSUP);//km20120524

		//km2012921		
		dataEIAGas= this.readOilConsumption("data/Socioeconomic/GDP/GDPHistorical_USD2010.csv");
		initialiseCountries(dataEIAGas, GDPHIST);
		dataEIAGas= this.readOilConsumption("data/Socioeconomic/GDP/GDPGrowthRateBase_percent.csv"); 
		initialiseCountries(dataEIAGas, GDPGROWBAS);
		dataEIAGas= this.readOilConsumption("data/Socioeconomic/GDP/GDPGrowthRateGPI_percent.csv"); 
		initialiseCountries(dataEIAGas, GDPGROWGPI);
		dataEIAGas= this.readOilConsumption("data/Socioeconomic/GDP/GDPGrowthRateMarket_percent.csv"); 
		initialiseCountries(dataEIAGas, GDPGROWMAR);
		dataEIAGas= this.readOilConsumption("data/Socioeconomic/GDP/GDPGrowthRatePolicy_percent.csv"); 
		initialiseCountries(dataEIAGas, GDPGROWPOL);
		dataEIAGas= this.readOilConsumption("data/Socioeconomic/GDP/GDPGrowthRateSecurity_percent.csv"); 
		initialiseCountries(dataEIAGas, GDPGROWSEC);
		dataEIAGas= this.readOilConsumption("data/Socioeconomic/GDP/GDPGrowthRateSustainability_percent.csv"); 
		initialiseCountries(dataEIAGas, GDPGROWSUS);

		dataEIAGas= this.readOilConsumption("data/Socioeconomic/Population/PopulationHistorical_Thousand.csv"); 
		initialiseCountries(dataEIAGas, POPHIST);
		dataEIAGas= this.readOilConsumption("data/Socioeconomic/Population/PopulationLow_Thousand.csv"); 
		initialiseCountries(dataEIAGas, POPFUTURELOW);
		dataEIAGas= this.readOilConsumption("data/Socioeconomic/Population/PopulationMedium_Thousand.csv"); 
		initialiseCountries(dataEIAGas, POPFUTUREMED);
		dataEIAGas= this.readOilConsumption("data/Socioeconomic/Population/PopulationHigh_Thousand.csv"); 
		initialiseCountries(dataEIAGas, POPFUTUREHIG);
		dataEIAGas= this.readOilConsumption("data/Socioeconomic/Population/PopulationConstant_Thousand.csv"); 
		initialiseCountries(dataEIAGas, POPFUTURECON);
		
		dataEIAGas= this.readOilConsumption("data/FossilFuels/gas/Price/GasPriceWEO11CurrentPolicy.csv"); 
		initialiseCountries(dataEIAGas, GASPRICECUR);
		dataEIAGas= this.readOilConsumption("data/FossilFuels/gas/Price/GasPriceWEO11NewPolicy.csv"); 
		initialiseCountries(dataEIAGas, GASPRICENEW);
		dataEIAGas= this.readOilConsumption("data/FossilFuels/gas/Price/GasPriceWEO11450.csv"); 
		initialiseCountries(dataEIAGas, GASPRICE450);
		dataEIAGas= this.readOilConsumption("data/FossilFuels/gas/Consumption/gasDemandCoefficient.csv"); 
		initialiseCountries(dataEIAGas, GASDEMANDCOEF);
		//km20120921
		
		dataEIAGas.clear();
		dataEIAGas=null;
		this.calculateGasHistConvFactor();
		this.cleanModel();
//		this.checkGasInitializaton(); //20120703
		this.setUpFinalModelAgentShorted();
	}

	public void checkGasInitializaton()
	{
		Enumeration e = gasCountries.elements();
		String str;
		
		while(e.hasMoreElements())
		{
			CountryGasProducer element = (CountryGasProducer) e.nextElement();
			Set<String> set= element.dnGasDemandGrowthAll.keySet();

	    	Iterator<String> itr = set.iterator();
//    		System.out.println(element.getName()+":"+element.dnGasDemandGrowthAll.get("WEOPolicy01"));
    		System.out.println(element.getName()+":"+element.dnGasDemandGrowthAll.get("WEOPolicy01")+":"+element.dnGasDemandGrowthAll.get("WEOPolicy02")+":"+element.dnGasDemandGrowthAll.get("WEOPolicy03")+":"+element.dnGasDemandGrowthAll.get("WEOPolicy04"));
 		}
	}
	
	/**
	 *Converting historical production to adjust to dry natural production of EIA 
	 */	
	public void calculateGasHistConvFactor()
	{
		double conv=1;
		Enumeration e = gasCountries.elements();
		String str;
		
		while(e.hasMoreElements())
		{
			CountryGasProducer element = (CountryGasProducer) e.nextElement();
			Set<String> set= element.histNaturalGas.keySet();

	    	Iterator<String> itr = set.iterator();
	    	
	    	if (element.conversionFactor.containsKey("ConvFact"))
	    	{
	    		conv = (Double)element.conversionFactor.get("ConvFact");
	    	}

    		//System.out.println(element.getFIPS());
	    	//System.out.println(conv);

	    	while(itr.hasNext())
	    	{
	    		str = itr.next();
	    		element.histNaturalGas.put(str, ((Double)element.histNaturalGas.get(str)*conv));
	    		//System.out.println(str+":" + element.histNaturalGas.get(str));
	    	}
		}
	}
	
	/**
	 * This method cleans the data when necessary. For example, it merges the different countries like West Germany and East Germany. 
	 * Look at the ACEGESModelinitialisationOil.cleanModel() for an example. 
	 * which countries need to be merged?
	 */
	private void cleanModel()
    {
		//Serbia and Serbia-Montenegro (aggregation)
		this.addGasData("RI","RIMJ", gasCountries);

		//Former Yugoslavia (split)
		CountryGasProducer[] countries2 = {(CountryGasProducer)	gasCountries.get("MJ"),(CountryGasProducer)	gasCountries.get("RI"),(CountryGasProducer) gasCountries.get("HR"),(CountryGasProducer)	gasCountries.get("SI"),(CountryGasProducer) gasCountries.get("BK"),(CountryGasProducer) gasCountries.get("MK") };
		CountryGasProducer originalCountry= (CountryGasProducer) gasCountries.get("MJRIHRSI");
		this.estimateHistGasProdConForSplitCountries(1992,1992, countries2, originalCountry);

		//Former Czechoslovakia (split)
		CountryGasProducer[] countries3 = {(CountryGasProducer) gasCountries.get("LO"),(CountryGasProducer) gasCountries.get("EZ")}; 
		originalCountry= (CountryGasProducer) gasCountries.get("LOEZ");
		this.estimateHistGasProdConForSplitCountries(1993, 1993, countries3, originalCountry);
	
		//Former USSR (split)
		CountryGasProducer[] countries4 = {(CountryGasProducer) gasCountries.get("UP"),(CountryGasProducer) gasCountries.get("RS"), (CountryGasProducer) gasCountries.get("LH"), (CountryGasProducer) gasCountries.get("BO"),(CountryGasProducer) gasCountries.get("UZ"),(CountryGasProducer) gasCountries.get("TX"),(CountryGasProducer) gasCountries.get("TI"),(CountryGasProducer) gasCountries.get("KZ"),(CountryGasProducer) gasCountries.get("KG"),(CountryGasProducer) gasCountries.get("GG"),(CountryGasProducer) gasCountries.get("AJ"),(CountryGasProducer) gasCountries.get("AM"),(CountryGasProducer) gasCountries.get("EN"),(CountryGasProducer) gasCountries.get("LG"),(CountryGasProducer) gasCountries.get("MD")}; 
		originalCountry= (CountryGasProducer) gasCountries.get("FSU");
		this.estimateHistGasProdConForSplitCountries(1992,1992, countries4, originalCountry);

		//Denmark and Greenland (aggregation)
		this.addGasData("DA","GL", gasCountries);		

		//East and West Germany (aggretion, Offshore not existing)
		this.addGasData("GM","GC", gasCountries);
		this.addGasData("GM","GE", gasCountries);
		this.addGasData("GM","GMOff", gasCountries);

		//UK and Falkland (aggregation, Offshore and with Falklands not existing)
		this.addGasData("UK","FK", gasCountries);
		this.addGasData("UK","UKOff", gasCountries);
		this.addGasData("UK","UKFK", gasCountries);		

		//China and HongKong (aggregation)
		this.addGasData("CH","HK", gasCountries);

		//US and Puerto Rico (aggregation)
		this.addGasData("US","RQ", gasCountries);

		//France and French Guiana (aggregation)
		this.addGasData("FR","FG", gasCountries);
		this.addGasData("FR","FP",gasCountries);
		
		//deleting a part of country because of zero values
		gasCountries.remove("BXMY"); gasCountries.remove("NLOff"); gasCountries.remove("AY");
		gasCountries.remove("AQ"); gasCountries.remove("AA"); gasCountries.remove("BD"); gasCountries.remove("CJ");
		gasCountries.remove("CW"); gasCountries.remove("FO"); gasCountries.remove("GI"); 
		gasCountries.remove("GP"); gasCountries.remove("GQ"); gasCountries.remove("MC"); gasCountries.remove("MB"); 
		gasCountries.remove("MH"); gasCountries.remove("NT"); gasCountries.remove("NC"); gasCountries.remove("NE");
		gasCountries.remove("RE"); gasCountries.remove("SH"); gasCountries.remove("SB"); gasCountries.remove("TK"); 
		gasCountries.remove("VQ"); gasCountries.remove("VI"); gasCountries.remove("WQ");gasCountries.remove("DAGL"); 
    }
	
	/**
	 * Merges (add) gas production AND consumption datasets
	 * It also adds the EUR. At the end, it removes the fromAgent from the list. 
	 * @param toAgent
	 * @param fromAgent
	 * @param oilCountries
	 */
	private void addGasData(String toAgent, String fromAgent, Hashtable oilCountries)
	{
		this.addGasProductions(toAgent,fromAgent, gasCountries);
		this.addGasConsumptions(toAgent,fromAgent, gasCountries);
		this.addGasEUR(toAgent, fromAgent, oilCountries);
		gasCountries.remove(fromAgent);		
	}
	
	/**
	 * 
	 * @param firstYearAfterSplit
	 * @param calculationPeriod
	 * @param countries
	 * @param countryOrigin
	 */
	private void estimateHistGasProdConForSplitCountries(int firstYearAfterSplit,int calculationPeriod, CountryGasProducer[] countries, CountryGasProducer countryOrigin)
	{
		this.estimateHistGasProdForSplitCountries(firstYearAfterSplit, calculationPeriod, countries, countryOrigin);
		this.estimateHistGasConsForSplitCountries(firstYearAfterSplit, calculationPeriod, countries, countryOrigin);
		gasCountries.remove(countryOrigin.getFIPS());
	}
	
	private void estimateHistGasConsForSplitCountries(int firstYearAfterSplit,int calculationPeriod, CountryGasProducer[] countries, CountryGasProducer countryOrigin)
	{
		double sum=0;
		int numOfCountries = countries.length;
		for (int i=0; i<numOfCountries; i++)
		{
			sum = sum + Double.parseDouble(countries[i].dngConsumption.get(Integer.toString(firstYearAfterSplit)).toString());
		}
	   if(sum>0)
	   {
		   double propotion=0;
			for (int i=0; i<numOfCountries; i++)
			{
				propotion=Double.parseDouble(countries[i].dngConsumption.get(Integer.toString(firstYearAfterSplit)).toString())/sum;
				//dry natural gas consumption
				Enumeration keysE =countryOrigin.dngConsumption.keys();
				Object key;
				double gasCons=0;
				while( keysE.hasMoreElements() )
			    {    
			   		key = keysE.nextElement();
			   	 	if (Integer.parseInt(key.toString())<calculationPeriod)
			   	 	{
			   	 		gasCons=  Double.parseDouble(countryOrigin.dngConsumption.get(key).toString());
			   	 		countries[i].dngConsumption.put(key.toString(), gasCons*propotion);
			   	 	}
			    }	
			}			
	   }
	   else
	   {
		  System.err.println("the sum is zero in the function estimateHistGasConsForSplitCountries()");
	   }
		
	}
	
	/**
	 * Updates both the dngProduction AND histNaturalGas 
	 * @param firstYearAfterSplit
	 * @param calculationPeriod
	 * @param countries
	 * @param countryOrigin
	 */
	private void estimateHistGasProdForSplitCountries(int firstYearAfterSplit,int calculationPeriod, CountryGasProducer[] countries, CountryGasProducer countryOrigin)
	{
		double sum=0;
		int numOfCountries = countries.length;
		for (int i=0; i<numOfCountries; i++)
		{
			sum = sum + Double.parseDouble(countries[i].dngProduction.get(Integer.toString(firstYearAfterSplit)).toString());
		}
		
	  if(sum>0)
	  {
		double propotion=0;
		for (int i=0; i<numOfCountries; i++)
		{
			propotion=Double.parseDouble(countries[i].dngProduction.get(Integer.toString(firstYearAfterSplit)).toString())/sum;
			//dry natural gas production
			Enumeration keysE =countryOrigin.dngProduction.keys();
			Object key;
			double gasProd=0;
			while( keysE.hasMoreElements() )
		    {    
		   		key = keysE.nextElement();
		   	 	if (Integer.parseInt(key.toString())<calculationPeriod)
		   	 	{
		   	 		//System.out.println(countries[i].getFIPS());
		   	 		//System.out.println(key.toString() +"="+ propotion);
		   	 		gasProd=  Double.parseDouble(countryOrigin.dngProduction.get(key).toString());
		   	 		countries[i].dngProduction.put(key.toString(), gasProd*propotion);
		   	 		//System.out.println(countries[i].dngProduction);
		   	 	}
		    }	
			//historical production production
			Enumeration keysE2=countryOrigin.histNaturalGas.keys();
			Object key2;
			gasProd=0;
			while( keysE2.hasMoreElements() )
		    {    
				key2 = keysE2.nextElement();
		       	if (Integer.parseInt(key2.toString())<calculationPeriod)
		       	{
		       		gasProd=  Double.parseDouble(countryOrigin.histNaturalGas.get(key2).toString());
		       		countries[i].histNaturalGas.put(key2.toString(), gasProd*propotion);
		       	}
		    }
		  }// end of agent iteration
		}//end of if (sum>0)	
	  else
	  {
		  System.err.println("the sum is zero in the function estimateHistGasProdForSplitCountries()");
	  }
	}

	/**
	 * 
	 * @param toAgent
	 * @param fromAgent
	 * @param gasCountries
	 */
	private void addGasEUR(String toAgent, String fromAgent, Hashtable gasCountries)
	{
		 CountryGasProducer agentTo =(CountryGasProducer) gasCountries.get(toAgent);
		 CountryGasProducer agentFrom =(CountryGasProducer) gasCountries.get(fromAgent); 			   
		 Enumeration keysE = agentFrom.ngEURAll.keys();
		   double mergedProd=0;  
		   Object key;
	       while( keysE.hasMoreElements() )
	       {    
	       	 	key = keysE.nextElement();
	       	 	if (agentTo.ngEURAll.containsKey(key))
	       	 	{
	         	mergedProd= Double.parseDouble(agentFrom.ngEURAll.get(key).toString()) + Double.parseDouble(agentTo.ngEURAll.get(key).toString());
	         	agentTo.ngEURAll.put(key.toString(), mergedProd) ;  
	       	 	}
	       	 	else
	       	 	{
	       	 		mergedProd= Double.parseDouble(agentFrom.ngEURAll.get(key).toString());
		         	agentTo.ngEURAll.put(key.toString(), mergedProd) ;  
	       	 	}
	       }	
	}
	
	/**
	 * 
	 * @param toAgent
	 * @param fromAgent
	 * @param gasCountries
	 */
	private void addGasConsumptions(String toAgent, String fromAgent, Hashtable gasCountries)
	{
	   CountryGasProducer agentTo =(CountryGasProducer) gasCountries.get(toAgent);
	   CountryGasProducer agentFrom =(CountryGasProducer) gasCountries.get(fromAgent); 		
	   Enumeration keysE = agentFrom.dngConsumption.keys();
	   double mergedProd;  
	   Object key;
       while( keysE.hasMoreElements() )
       {    
       	 	key = keysE.nextElement();
         	mergedProd= Double.parseDouble(agentFrom.dngConsumption.get(key).toString()) + Double.parseDouble(agentTo.dngConsumption.get(key).toString());
         	//System.out.println(key.toString() + "," +mergedProd + ", "+ agentFrom.dngConsumption.get(key).toString() + ", "+ agentTo.dngConsumption.get(key).toString() );
         	agentTo.dngConsumption.put(key.toString(), mergedProd) ;   	
       }	
	}
	
	/**
	 * 
	 * @param toAgent
	 * @param fromAgent
	 * @param gasCountries
	 */
	private void addGasProductions(String toAgent, String fromAgent, Hashtable gasCountries)
	{
	   CountryGasProducer agentTo =(CountryGasProducer) gasCountries.get(toAgent);
	   CountryGasProducer agentFrom =(CountryGasProducer) gasCountries.get(fromAgent); 		

	   //for dry natural gas production (EIA)
	   Set<String> set= agentFrom.dngProduction.keySet();
	   Iterator<String> itr = set.iterator();
	   double mergedProd;
	   String str;
       while( itr.hasNext() )
       {    
      	    str = itr.next(); 
            //System.out.println(key.toString());
       	 	mergedProd= Double.parseDouble(agentFrom.dngProduction.get(str).toString()) + Double.parseDouble(agentTo.dngProduction.get(str).toString());
         	//System.out.println(key.toString() + "," +mergedProd + ", "+ agentFrom.dngProduction.get(str).toString() + ", "+ agentTo.dngProduction.get(str).toString() );
         	agentTo.dngProduction.put(str.toString(), mergedProd) ;   	
       }	
       
	   //for historical production
	   set = agentFrom.histNaturalGas.keySet();
	   itr = set.iterator();
	   while( itr.hasNext() )
       {    
       	    str = itr.next(); 
         	//System.out.println(key.toString());
   	 		mergedProd= Double.parseDouble(agentFrom.histNaturalGas.get(str).toString()) + Double.parseDouble(agentTo.histNaturalGas.get(str).toString());
   	 		//System.out.println(key.toString() + "," +mergedProd + ", "+ agentFrom.dngProduction.get(str).toString() + ", "+ agentTo.dngProduction.get(str).toString() );
   	 		agentTo.histNaturalGas.put(str.toString(), mergedProd) ; 
       }	
	}
	
	//populates the countries with historical data
	public void initialiseCountries(ArrayList<String> data, int fieldName)
	{
		String[] fieldKeys = data.get(0).split(","); // first row of the data file SHOULD be the column names
		for (int x=1; x<data.size();x++)
		{
			 String[] line = data.get(x).split(",");
			//only start from Country name (second column)
			if (countryFIPS.containsKey(line[0]))
			{
				CountryGasProducer gasProducer	=(CountryGasProducer) gasCountries.get(countryFIPS.get(line[0]));
				switch(fieldName)
				{
					case DNGCONSUMPTION:
						updateDNGConsumption(gasProducer, line, fieldKeys);
						break;
					case DNGDEMANDGROWTH:
						updateDNGDemandGrowth(gasProducer, line, fieldKeys);
						break;
					case DNGPRODUCTION:
						updateDNGProduction(gasProducer, line, fieldKeys);
						break;
					case EURDNGUS:
						updateUSGSEURGas(gasProducer, line, fieldKeys, true);
						break;
					case EURDNGCH:
						updateEURDNG(gasProducer, line, fieldKeys, Boolean.FALSE);
						break;
					case EURDNGBG:
						updateEURDNG(gasProducer, line, fieldKeys, Boolean.TRUE);
						break;
					case HISTORICNATURALGAS:
						updateHistNG(gasProducer, line, fieldKeys);
						break;
					case PROVEDDNGASRESERVES:
						updateProvedDNG(gasProducer, line, fieldKeys);
						break;
					case CONVERSION:
						updateConvFact(gasProducer, line, fieldKeys);
						break;
					case EURDNGUSUP:
						updateUSGSEURGas(gasProducer, line, fieldKeys, false);
						break;

					//km20120921
					case GDPHIST:
						updateGDPHist(gasProducer, line, fieldKeys);
						break;
					case GDPGROWBAS:
						updateGDPGrow(gasProducer, line, fieldKeys, 1);
						break;
					case GDPGROWGPI:
						updateGDPGrow(gasProducer, line, fieldKeys, 2);
						break;
					case GDPGROWMAR:
						updateGDPGrow(gasProducer, line, fieldKeys, 3);
						break;
					case GDPGROWPOL:
						updateGDPGrow(gasProducer, line, fieldKeys, 4);
						break;
					case GDPGROWSEC:
						updateGDPGrow(gasProducer, line, fieldKeys, 5);
						break;
					case GDPGROWSUS:
						updateGDPGrow(gasProducer, line, fieldKeys, 6);
						break;
					case POPHIST:
						updatePopHist(gasProducer, line, fieldKeys);
						break;
					case POPFUTURELOW:
						updatePopFuture(gasProducer, line, fieldKeys, 1);
						break;
					case POPFUTUREMED:
						updatePopFuture(gasProducer, line, fieldKeys, 2);
						break;
					case POPFUTUREHIG:
						updatePopFuture(gasProducer, line, fieldKeys, 3);
						break;
					case POPFUTURECON:
						updatePopFuture(gasProducer, line, fieldKeys, 4);
						break;
					case GASPRICECUR:
						updatePrice(gasProducer, line, fieldKeys, 1);
						break;
					case GASPRICENEW:
						updatePrice(gasProducer, line, fieldKeys, 2);
						break;
					case GASPRICE450:
						updatePrice(gasProducer, line, fieldKeys, 3);
						break;
					case GASDEMANDCOEF:
						updateCoef(gasProducer, line, fieldKeys);
						break;
					//km20120921
					default: 
						System.out.println("I have not updated the agent:" + gasProducer.getName() + " form the file "+ fieldName);
				}
			}
			else
			{
				  //System.out.println("Miss match between 'data file' and 'counrty file':" + line[0].toString() + " form the file "+ fieldName);
			}	   
		}
	}

	//km20120921
	private void updateCoef(CountryGasProducer gasProducer, String[] line, String[] fieldKeys) {
		for (int i=1; i<line.length; i++)
		{
			gasProducer.demandCoef.put(fieldKeys[i], Double.parseDouble(line[i]));
			//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
		}		
	}
	
	private void updateGDPHist(CountryGasProducer gasProducer, String[] line, String[] fieldKeys) {
		for (int i=1; i<line.length; i++)
		{
			gasProducer.GDPHist.put(fieldKeys[i], Double.parseDouble(line[i]));
			//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
		}		
	}

	private void updateGDPGrow(CountryGasProducer gasProducer, String[] line, String[] fieldKeys, int scn) {
		if(scn==1){
			for (int i=1; i<line.length; i++)
			{
				gasProducer.GDPGrowBas.put(fieldKeys[i], Double.parseDouble(line[i]));
				//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
			}		
		}
		else if(scn==2){
			for (int i=1; i<line.length; i++)
			{
				gasProducer.GDPGrowGPI.put(fieldKeys[i], Double.parseDouble(line[i]));
				//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
			}		
		}
		else if(scn==3){
			for (int i=1; i<line.length; i++)
			{
				gasProducer.GDPGrowMar.put(fieldKeys[i], Double.parseDouble(line[i]));
				//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
			}		
		}
		else if(scn==4){
			for (int i=1; i<line.length; i++)
			{
				gasProducer.GDPGrowPol.put(fieldKeys[i], Double.parseDouble(line[i]));
				//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
			}		
		}
		else if(scn==5){
			for (int i=1; i<line.length; i++)
			{
				gasProducer.GDPGrowSec.put(fieldKeys[i], Double.parseDouble(line[i]));
				//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
			}		
		}
		else if(scn==6){
			for (int i=1; i<line.length; i++)
			{
				gasProducer.GDPGrowSus.put(fieldKeys[i], Double.parseDouble(line[i]));
				//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
			}		
		}
	}

	private void updatePopHist(CountryGasProducer gasProducer, String[] line, String[] fieldKeys) {
		for (int i=1; i<line.length; i++)
		{
			gasProducer.popHist.put(fieldKeys[i], Double.parseDouble(line[i])*1000);
			//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i])*1000 +" for " +  gasProducer.getName());
		}		
	}

	private void updatePopFuture(CountryGasProducer gasProducer, String[] line, String[] fieldKeys, int scn) {
		if(scn==1){
			for (int i=1; i<line.length; i++)
			{
				gasProducer.popLow.put(fieldKeys[i], Double.parseDouble(line[i])*1000);
				//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
				//System.out.println(fieldKeys[i] +":"+ gasProducer.popLow.get(fieldKeys[i]) +" for " +  gasProducer.getName());
			}		
		}
		else if(scn==2){
			for (int i=1; i<line.length; i++)
			{
				gasProducer.popMed.put(fieldKeys[i], Double.parseDouble(line[i])*1000);
				//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
			}		
		}
		else if(scn==3){
			for (int i=1; i<line.length; i++)
			{
				gasProducer.popHig.put(fieldKeys[i], Double.parseDouble(line[i])*1000);
				//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
			}		
		}
		else if(scn==4){
			for (int i=1; i<line.length; i++)
			{
				gasProducer.popCon.put(fieldKeys[i], Double.parseDouble(line[i])*1000);
				//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
			}		
		}
	}

	private void updatePrice(CountryGasProducer gasProducer, String[] line, String[] fieldKeys, int scn) 
	{
		if(scn==1){
			for (int i=1; i<line.length; i++)
			{
				gasProducer.priceCur.put(fieldKeys[i], Double.parseDouble(line[i]));
				//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
			}		
		}
		else if(scn==2){
			for (int i=1; i<line.length; i++)
			{
				gasProducer.priceNew.put(fieldKeys[i], Double.parseDouble(line[i]));
				//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
			}		
		}
		else if(scn==3){
			for (int i=1; i<line.length; i++)
			{
				gasProducer.price450.put(fieldKeys[i], Double.parseDouble(line[i]));
				//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
			}		
		}
	}
	//km20120921

	
	/**
	 * This is in million barrels (No transformation needed). 
	 * @param oilProducer
	 * @param line
	 * @param fieldKeys
	 */
	private void updateUSGSEURGas(CountryGasProducer gasProducer, String[] line,String[] fieldKeys, boolean addOriginalUSGS) 
	{
		double gasEUR=0d;
		String key;
		for (int i=1; i<line.length; i++)
		{
			if(addOriginalUSGS)
			{
				gasProducer.ngEURAll.put(fieldKeys[i], Double.parseDouble(line[i]));
			}
			else
			{
				key=fieldKeys[i];
				//System.out.println(key);
				if (gasProducer.ngEURAll.containsKey(key))
				{
					gasEUR=(Double) gasProducer.ngEURAll.get(key);
					gasProducer.ngEURAll.put(key,gasEUR+Double.parseDouble(line[i]));
				}
			}
		}
	}
	
	/**
	 * Conversion factor of histoconversionFactorrical production to adjust to EIA
	 * @param gasProducer
	 * @param line
	 * @param fieldKeys
	 */
	private void updateConvFact(CountryGasProducer gasProducer, String[] line, String[] fieldKeys) 
	{
		for (int i=1; i<line.length; i++)
		{
			gasProducer.conversionFactor.put(fieldKeys[i], Double.parseDouble(line[i]));
		}
	}
	
	/**
	 * 
	 * @param gasProducer
	 * @param line
	 * @param fieldKeys
	 */
	private void updateProvedDNG(CountryGasProducer gasProducer, String[] line, String[] fieldKeys) 
	{
		gasProducer.provedReservesDNGAS=Double.parseDouble(line[1])*1000;
	}

	/**
	 * 
	 * @param gasProducer
	 * @param line
	 * @param fieldKeys
	 */
	private void updateHistNG(CountryGasProducer gasProducer, String[] line,String[] fieldKeys) 
	{
		for (int i=1; i<line.length; i++)
		{
			gasProducer.histNaturalGas.put(fieldKeys[i], (Double.parseDouble(line[i])/0.028316)/1000); 
		}
	}
	
	/**
	 * 
	 * @param gasProducer
	 * @param line
	 * @param fieldKeys
	 * @param isGerman
	 */
	private void updateEURDNG(CountryGasProducer gasProducer, String[] line,String[] fieldKeys, Boolean isGerman) 
	{
		if(isGerman)
		{
			for (int i=1; i<line.length; i++)
			{
				gasProducer.ngEURAll.put(fieldKeys[i], Double.parseDouble(line[i])/0.028316); //converted from CUM to CUF
				//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
			}
		}
		else
		{
		
		 for (int i=1; i<line.length; i++)
		 {
			gasProducer.ngEURAll.put(fieldKeys[i], Double.parseDouble(line[i]));			
			//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
		 }
		}
	}

	/**
	 * 
	 * @param gasProducer
	 * @param line
	 * @param fieldKeys
	 */
	private void updateDNGProduction(CountryGasProducer gasProducer, String[] line, String[] fieldKeys)
	{
		for (int i=1; i<line.length; i++)
		{
			gasProducer.dngProduction.put(fieldKeys[i], Double.parseDouble(line[i]));
			//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
		}
	}
	
	/**
	 * 
	 * @param gasProducer
	 * @param line
	 * @param fieldKeys
	 */
	private void updateDNGDemandGrowth(CountryGasProducer gasProducer,String[] line, String[] fieldKeys) 
	{
		for (int i=1; i<line.length; i++)
		{
			gasProducer.dnGasDemandGrowthAll.put(fieldKeys[i], Double.parseDouble(line[i]));
			//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
		}		
	}
	
	/**
	 * 
	 * @param gasProducer
	 * @param line
	 * @param fieldKeys
	 */
	private void updateDNGConsumption(CountryGasProducer gasProducer, String[] line, String[] fieldKeys)
	{
		for (int i=1; i<line.length; i++)
		{
			gasProducer.dngConsumption.put(fieldKeys[i], Double.parseDouble(line[i]));
			//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
		}
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	public ArrayList<String> readOilConsumption(String file)
	{
		 CSVFileReader csvFile=new CSVFileReader(file);
		 csvFile.readFile();
		 //csvFile.displayArrayList();
		 return csvFile.storeValues;
		 
	}
	
	/**
	 * 
	 * @param args
	 */
	 public static void main(String[] args)
	 {
		 ACEGESModelinitialisationGas model =new ACEGESModelinitialisationGas(null);		 	 
	 }
	 

	 /**
	  * 
	  * @param data
	  */
	 public void createCountries(ArrayList<String> data)
	 {
		 countryFIPS.clear();
		 gasCountries.clear();
		 for(int x=1;x<data.size();x++)
		 {
			 String[] line = data.get(x).split(",");
			 if (!line[1].equalsIgnoreCase("Region"))
			 { 
				 CountryGasProducer gasProducer= new CountryGasProducer(this.myModel);
				 //set up the initial values		 
				 gasProducer.setName(line[0]);
				 gasProducer.setFIPS(line[1]);
				 countryFIPS.put(line[0], line[1]);
				 //System.out.println(oilProducer.toString());		  
				 gasCountries.put(gasProducer.getFIPS(), gasProducer);					  
			 }
		 }
	 }	
	 
	 /**
	  * 
	  */
	 private void setUpFinalModelAgentShorted()
	 {
		 // Sort hashtable.
		 Vector v = new Vector(countryFIPS.keySet());
		 Collections.sort(v);
		 String val="" ;
		 CountryGasProducer agent=null; 
		 // Display (sorted) hashtable.
		 for (Enumeration e = v.elements(); e.hasMoreElements();) {
			 String key = (String)e.nextElement();
			 if (gasCountries.containsKey(countryFIPS.get(key)))
			 {
				 agent =gasCountries.get(countryFIPS.get(key));
				 this.myModel.addDNGAgent(agent);
			 }
			 //System.out.println("Key: " + countryFIPS.get(key) + "     Val: " + val);
			 // val="KENOOOOOOOOOOOOOOOO";
		 }
		    
		 countryFIPS.clear();
		 countryFIPS=null;
		 gasCountries.clear();
		 gasCountries=null;
	 }	
}
