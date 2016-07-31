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
import aceges.countries.CountryOilProducer;
import aceges.countries.EnergyAgent;
import aceges.utilities.io.CSVFileReader;

public class ACEGESScenarioSettings 
{

	private ACEGESApp myModel;
	//public  Hashtable<String,EnergyAgent> energyAgent = new Hashtable<String,EnergyAgent>();
	public  Hashtable<String,String> countryFIPS = new Hashtable<String,String>();
	public  ArrayList <String>scenarioDataOil = new ArrayList<String>(); //various data files
	public  ArrayList <String>scenarioDataGas = new ArrayList<String>(); //various data files

	private Hashtable<String, CountryOilProducer> oilCountries=new Hashtable<String,CountryOilProducer>();;
	private Hashtable<String, CountryGasProducer> gasCountries= new Hashtable<String,CountryGasProducer>();
	public  ArrayList <String>dataEIA = new ArrayList<String>(); //various data files from the Energy Information Administration
	public  ArrayList <String>dataEIAGas = new ArrayList<String>(); //various data files

	
	static final int LPGCONSUMPTION=0;
	static final int NGPLPRODUCTION=1;
	static final int TPCONSUMPTION=2;
	static final int CCPRODUCTION=3;
	static final int HISTORICCRUDEOIL=4;
	static final int EUROILCH=5;
	static final int OILDEMANDWEO2010=6;
	static final int USGSEUR=7;
	static final int PROVEDRESERVESEIA=8;
	static final int EUROILBGR=9;
	
	
	static final int DNGCONSUMPTION=0;
	static final int DNGDEMANDGROWTH=1;
	static final int DNGPRODUCTION=2;
	static final int EURDNGUS=3;
	static final int EURDNGCH=4;
	static final int EURDNGBG=5;
	static final int HISTORICNATURALGAS=6;
	static final int PROVEDDNGASRESERVES=7;	
	static final int CONVERSION=8;
	
	
	
	public ACEGESScenarioSettings(ACEGESApp target)
	{
		this.myModel=target;
		this.createCountries(readOilConsumption("data/Countries.csv"));
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Consumption/Consumption_of_Liquefied_Petroleum_Gases_(Thousand_Barrels_Per_Day).csv");
		initialiseCountriesOil(dataEIA, LPGCONSUMPTION);
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Production/ngl/Production_of_Natural_Gas_Plant_Liquids_(Thousand_Barrels_Per_Day).csv"); 
		initialiseCountriesOil(dataEIA, NGPLPRODUCTION);
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Consumption/Total_Petroleum_Consumption_(Thousand_Barrels_Per_Day).csv"); 
		initialiseCountriesOil(dataEIA, TPCONSUMPTION);
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Production/crude/Production_of_Crude_Oil_including_Lease_Condensate_(Thousand_Barrels_Per_Day).csv"); 
		initialiseCountriesOil(dataEIA, CCPRODUCTION);		
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Production/crude/historicalFixed/historicCrudeOilProduction.csv"); 
		initialiseCountriesOil(dataEIA, HISTORICCRUDEOIL);
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Production/EUR/crude/EURCHBillion.csv"); 
		initialiseCountriesOil(dataEIA, EUROILCH);
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Consumption/DemandGrowthWEO.csv"); 
		initialiseCountriesOil(dataEIA, OILDEMANDWEO2010);
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Production/EUR/crude/EURUSGSMMBO.csv"); 
		initialiseCountriesOil(dataEIA, USGSEUR);
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Production/EUR/crude/Crude_Oil_Proved_Reserves_(Billion_Barrels).csv"); 
		initialiseCountriesOil(dataEIA, PROVEDRESERVESEIA);
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Production/EUR/crude/EURBGRMegaton.csv"); 
		initialiseCountriesOil(dataEIA, EUROILBGR);
		dataEIA.clear();
		dataEIA=null;
		this.cleanModel();
	
		
		dataEIAGas= this.readOilConsumption("data/FossilFuels/gas/Consumption/DryNaturalGasConsumption_BilCUF.csv");
		initialiseCountriesGas(dataEIAGas, DNGCONSUMPTION);
		dataEIAGas= this.readOilConsumption("data/FossilFuels/gas/Consumption/GasDemandGrowthWEO.csv");
		initialiseCountriesGas(dataEIAGas, DNGDEMANDGROWTH);
		dataEIAGas= this.readOilConsumption("data/FossilFuels/gas/Production/DryNaturalGasProduction_BilCUF.csv"); 
		initialiseCountriesGas(dataEIAGas, DNGPRODUCTION);	//production from EIA	
		dataEIAGas= this.readOilConsumption("data/FossilFuels/gas/Production/EUR/EURGasUSGS_BilCUF.csv"); 
		initialiseCountriesGas(dataEIAGas, EURDNGUS);
		dataEIAGas= this.readOilConsumption("data/FossilFuels/gas/Production/EUR/EURGasCampbell_BilCUF.csv"); 
		initialiseCountriesGas(dataEIAGas, EURDNGCH);
		dataEIAGas= this.readOilConsumption("data/FossilFuels/gas/Production/EUR/EURGasBGR_BilCUM.csv"); 
		initialiseCountriesGas(dataEIAGas, EURDNGBG);
		dataEIAGas= this.readOilConsumption("data/FossilFuels/gas/Production/historicalFixed/historicalNaturalGas.csv"); 
		initialiseCountriesGas(dataEIAGas, HISTORICNATURALGAS);
		dataEIAGas= this.readOilConsumption("data/FossilFuels/gas/Production/EUR/Proved_Reserves_of_Natural_Gas_(Trillion_Cubic_Feet).csv"); 
		initialiseCountriesGas(dataEIAGas, PROVEDDNGASRESERVES);
		dataEIAGas= this.readOilConsumption("data/FossilFuels/gas/Production/historicalFixed/GasConversion.csv"); 
		initialiseCountriesGas(dataEIAGas, CONVERSION);
		dataEIAGas.clear();
		dataEIAGas=null;
		this.calculateGasHistConvFactor();
		this.cleanModelGas();
		
		updateScenarioParameters("data/Scenarios/oilh.csv","data/Scenarios/gash.csv");	
		this.setUpFinalModelAgentShorted();
	}
	
	public void updateScenarioParameters(String scenariofileOil, String scenariofileGas)
	{
			 CSVFileReader csvFile=new CSVFileReader(scenariofileOil);
			 csvFile.readFile();
			 scenarioDataOil= csvFile.storeValues;
			// first row of the data file SHOULD be the column names of the sceario parameters
			 String[] fieldKeys = scenarioDataOil.get(0).split(",");
			 
			 CSVFileReader csvFile2=new CSVFileReader(scenariofileGas);
			 csvFile2.readFile();
			 scenarioDataGas= csvFile2.storeValues;
			// first row of the data file SHOULD be the column names of the sceario parameters
			 String[] fieldKeys2 = scenarioDataGas.get(0).split(",");
			 
			 for (int x=1; x<scenarioDataOil.size();x++)
			 {
				 String[] lineOil = scenarioDataOil.get(x).split(",");
				 String[] lineGas = scenarioDataGas.get(x).split(",");
				 CountryOilProducer oilProducer=null;
				 if (countryFIPS.containsKey(lineOil[0]))
				 {
					 if (oilCountries.containsKey(countryFIPS.get(lineOil[0])))
					 {
						  oilProducer	=(CountryOilProducer) oilCountries.get(countryFIPS.get(lineOil[0]));
							 //updates with scenario settings
							 oilProducer.oilDemandGrowthMin=Double.parseDouble(lineOil[2]);
							 oilProducer.oilDemandGrowthMax=Double.parseDouble(lineOil[3]);
							 oilProducer.productioGrowthMin=Double.parseDouble(lineOil[4]);
							 oilProducer.productioGrowthMax=Double.parseDouble(lineOil[5]);
							 oilProducer.peakPointMin=Double.parseDouble(lineOil[6]);
							 oilProducer.peakPointMax=Double.parseDouble(lineOil[7]);
							 oilProducer.oilEURMin=Double.parseDouble(lineOil[8]);
							 oilProducer.oilEURMax=Double.parseDouble(lineOil[9]);
					 }
					 
					 CountryGasProducer gasProducer	=null;
					 if (gasCountries.containsKey(countryFIPS.get(lineOil[0])))
					 {
						 /*
						 gasProducer=(CountryGasProducer) gasCountries.get(countryFIPS.get(lineOil[0]));
						 gasProducer.dnGasDemandGrowthMin=Double.parseDouble(lineGas[2]);
						 gasProducer.dnGasDemandGrowthMax=Double.parseDouble(lineGas[3]);
						 gasProducer.productioGrowthDngasMin=Double.parseDouble(lineGas[4]); 
						 gasProducer.productioGrowthDngasMax=Double.parseDouble(lineGas[5]);
						 gasProducer.peakPointdnGasMin=Double.parseDouble(lineGas[6]);
						 gasProducer.peakPointdnGasMax=Double.parseDouble(lineGas[7]);
						 gasProducer.dngEURMin=Double.parseDouble(lineGas[8]);
						 gasProducer.dngEURMax=Double.parseDouble(lineGas[9]);	
						 */
					 }
					 	 
				 }
			 }
	}
	
	/**
	 *Converting historical production to adjust to Dry natural production of EIA 
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
	 * This method the data when neccessary. For example, it merges the different countries like West Germany and East Germany. 
	 * Look at the ACEGESModelinitialisationOil.cleanModel() for an example. 
	 * which countries need to be merged?
	 */
	private void cleanModelGas()
    {
		//Serbia and Serbia-Montenegro (aggregation)
		this.addGasProductions("RI","RIMJ", gasCountries);
		this.addGasConsumptions("RI","RIMJ", gasCountries);

		CountryGasProducer agentTo =(CountryGasProducer) gasCountries.get("RI");
		CountryGasProducer agentFrom =(CountryGasProducer) gasCountries.get("RIMJ"); 
		agentTo.ngEURAll.put("CumulativeGasUSGS", agentFrom.ngEURAll.get("CumulativeGasUSGS"));
		agentTo.ngEURAll.put("RemainingGasUSGS", agentFrom.ngEURAll.get("RemainingGasUSGS"));
		agentTo.ngEURAll.put("GasF95USGS", agentFrom.ngEURAll.get("GasF95USGS"));
		agentTo.ngEURAll.put("GasF50USGS", agentFrom.ngEURAll.get("GasF50USGS"));
		agentTo.ngEURAll.put("GasF5USGS", agentFrom.ngEURAll.get("GasF5USGS"));
		agentTo.ngEURAll.put("GasMeanUSGS", agentFrom.ngEURAll.get("GasMeanUSGS"));
		agentTo.ngEURAll.put("GrowthGasUSGS", agentFrom.ngEURAll.get("GrowthGasUSGS"));
		gasCountries.remove("RIMJ");

		//Former Yugoslavia (split)
		CountryGasProducer[] countries2 = {(CountryGasProducer)	gasCountries.get("MJ"),(CountryGasProducer)	gasCountries.get("RI"),(CountryGasProducer) gasCountries.get("HR"),(CountryGasProducer)	gasCountries.get("SI"),(CountryGasProducer) gasCountries.get("BK"),(CountryGasProducer) gasCountries.get("MK") };
		CountryGasProducer originalCountry= (CountryGasProducer) gasCountries.get("MJRIHRSI");
		this.estimateGasProdForSplitCountries(1992,1992, countries2, originalCountry);
		this.estimateGasConsForSplitCountries(1992,1992, countries2, originalCountry);
		gasCountries.remove("MJRIHRSI");

		//Former Czechoslovakia (split)
		CountryGasProducer[] countries3 = {(CountryGasProducer) gasCountries.get("LO"),(CountryGasProducer) gasCountries.get("EZ")}; 
		originalCountry= (CountryGasProducer) gasCountries.get("LOEZ");
		this.estimateGasProdForSplitCountries(1993, 1993, countries3, originalCountry);
		this.estimateGasConsForSplitCountries(1993, 1993, countries3, originalCountry);
		gasCountries.remove("LOEZ");
	
		//Former USSR (split)
		CountryGasProducer[] countries4 = {(CountryGasProducer) gasCountries.get("UP"),(CountryGasProducer) gasCountries.get("RS"), (CountryGasProducer) gasCountries.get("LH"), (CountryGasProducer) gasCountries.get("BO"),(CountryGasProducer) gasCountries.get("UZ"),(CountryGasProducer) gasCountries.get("TX"),(CountryGasProducer) gasCountries.get("TI"),(CountryGasProducer) gasCountries.get("KZ"),(CountryGasProducer) gasCountries.get("KG"),(CountryGasProducer) gasCountries.get("GG"),(CountryGasProducer) gasCountries.get("AJ"),(CountryGasProducer) gasCountries.get("AM"),(CountryGasProducer) gasCountries.get("EN"),(CountryGasProducer) gasCountries.get("LG"),(CountryGasProducer) gasCountries.get("MD")}; 
		originalCountry= (CountryGasProducer) gasCountries.get("FSU");
		this.estimateGasProdForSplitCountries(1992,1992, countries4, originalCountry);
		this.estimateGasConsForSplitCountries(1992,1992, countries4, originalCountry);
		gasCountries.remove("FSU");

		//Denmark and Greenland (aggregation)
		this.addGasEUR("DA","GL", gasCountries);
		gasCountries.remove("DAGL"); gasCountries.remove("GL");

		//East and West Germany (aggretion, Offshore not existing)
		this.addGasProductions("GM","GC", gasCountries);
		this.addGasConsumptions("GM","GC", gasCountries);
		gasCountries.remove("GC");

		this.addGasProductions("GM","GE", gasCountries);
		this.addGasConsumptions("GM","GE", gasCountries);			
		gasCountries.remove("GE"); gasCountries.remove("GMOff");

		//UK and Falkland (aggregation, Offshore and with Falklands not existing)
		this.addGasEUR("UK","FK", gasCountries);
		gasCountries.remove("UKOff"); gasCountries.remove("UKFK"); gasCountries.remove("FK");

		//China and HongKong (aggregation)
		this.addGasConsumptions("CH","HK", gasCountries);
		gasCountries.remove("HK");

		//US and Puerto Rico (aggregation)
		this.addGasConsumptions("US","RQ", gasCountries);
		gasCountries.remove("RQ");

		//France and French Guiana (aggregation)
		this.addGasEUR("FR","FG", gasCountries);
		gasCountries.remove("FG"); 

//		originalCountry= (CountryGasProducer) gasCountries.get("FR");
//		System.out.println(originalCountry.dngEURALL);
		
		//deleting a part of country because of zero values
		/*
		gasCountries.remove("BXMY"); gasCountries.remove("NLOff"); gasCountries.remove("AY");
		gasCountries.remove("AQ"); gasCountries.remove("AA"); gasCountries.remove("BD"); gasCountries.remove("CJ");
		gasCountries.remove("CW"); gasCountries.remove("FO"); gasCountries.remove("FP"); gasCountries.remove("GI"); 
		gasCountries.remove("GP"); gasCountries.remove("GQ"); gasCountries.remove("MC"); gasCountries.remove("MB"); 
		gasCountries.remove("MH"); gasCountries.remove("NT"); gasCountries.remove("NC"); gasCountries.remove("NE");
		gasCountries.remove("RE"); gasCountries.remove("SH"); gasCountries.remove("SB"); gasCountries.remove("TK"); 
		gasCountries.remove("VQ"); gasCountries.remove("VI"); gasCountries.remove("WQ");
		*/
    }

	private void estimateGasConsForSplitCountries(int firstYearAfterSplit,int calculationPeriod, CountryGasProducer[] countries, CountryGasProducer countryOrigin)
	{
		double sum=0;
		int numOfCountries = countries.length;
		for (int i=0; i<numOfCountries; i++)
		{
			sum = sum + Double.parseDouble(countries[i].dngConsumption.get(Integer.toString(firstYearAfterSplit)).toString());
		}
	
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
		   	 		//System.out.println(countries[i].getFIPS());
		   	 		//System.out.println(key.toString() +"="+ propotion);
		   	 		gasCons=  Double.parseDouble(countryOrigin.dngConsumption.get(key).toString());
		   	 		countries[i].dngConsumption.put(key.toString(), gasCons*propotion);
		   	 		//System.out.println(countries[i].dngConsumption);
		   	 	}
		    }	
		}			
	}
	
	private void estimateGasProdForSplitCountries(int firstYearAfterSplit,int calculationPeriod, CountryGasProducer[] countries, CountryGasProducer countryOrigin)
	{
		double sum=0;
		int numOfCountries = countries.length;
		for (int i=0; i<numOfCountries; i++)
		{
			sum = sum + Double.parseDouble(countries[i].dngProduction.get(Integer.toString(firstYearAfterSplit)).toString());
		}
	
		double propotion=0;
		for (int i=0; i<numOfCountries; i++)
		{
			propotion=Double.parseDouble(countries[i].dngProduction.get(Integer.toString(firstYearAfterSplit)).toString())/sum;
			//dry natural gas production
			Enumeration keysE =countryOrigin.dngProduction.keys();
			Enumeration keysE2=countryOrigin.histNaturalGas.keys();
			Object key,key2;
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
			gasProd=0;
			while( keysE2.hasMoreElements() )
		    {    
				key2 = keysE2.nextElement();
		       	if (Integer.parseInt(key2.toString())<calculationPeriod)
		       	{
		       		//System.out.println(countries[i].getFIPS());
		       		//System.out.println(key2.toString() +" "+ propotion);
		       		gasProd=  Double.parseDouble(countryOrigin.histNaturalGas.get(key2).toString());
		       		//System.out.println(key2.toString() +" "+ gasProd);
		       		countries[i].histNaturalGas.put(key2.toString(), gasProd*propotion);
		       		//System.out.println(countries[i].histNaturalGas);
		       	}
		    }	
		}			
	}

	private void addGasEUR(String toAgent, String fromAgent, Hashtable gasCountries)
	{
		 CountryGasProducer agentTo =(CountryGasProducer) gasCountries.get(toAgent);
		 CountryGasProducer agentFrom =(CountryGasProducer) gasCountries.get(fromAgent); 	
		   
		 Enumeration keysE = agentFrom.ngEURAll.keys();
		   double mergedProd;  
		   Object key;
	       while( keysE.hasMoreElements() )
	       {    
	       	 	key = keysE.nextElement();
	         	mergedProd= Double.parseDouble(agentFrom.ngEURAll.get(key).toString()) + Double.parseDouble(agentTo.ngEURAll.get(key).toString());
	         	//System.out.println(key.toString() + "," +mergedProd + ", "+ agentFrom.dngEURALL.get(key).toString() + ", "+ agentTo.dngEURALL.get(key).toString() );
	         	agentTo.ngEURAll.put(key.toString(), mergedProd) ;   	
	       }	
	}
	
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
	public void initialiseCountriesGas(ArrayList<String> data, int fieldName)
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
						updateEURDNG(gasProducer, line, fieldKeys, Boolean.FALSE);
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

	//Conversion factor of histoconversionFactorrical production to adjust to EIA
	private void updateConvFact(CountryGasProducer gasProducer, String[] line, String[] fieldKeys) 
	{
		for (int i=1; i<line.length; i++)
		{
			gasProducer.conversionFactor.put(fieldKeys[i], Double.parseDouble(line[i]));
			//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
		}
	}
	
	
	//tranform the trillin to billion
	private void updateProvedDNG(CountryGasProducer gasProducer, String[] line, String[] fieldKeys) 
	{
		gasProducer.provedReservesDNGAS=Double.parseDouble(line[1])*1000;
	}


	//Historical data is in million cubic meters and is transformed to billion cubic feet
	private void updateHistNG(CountryGasProducer gasProducer, String[] line,String[] fieldKeys) 
	{
		for (int i=1; i<line.length; i++)
		{
			gasProducer.histNaturalGas.put(fieldKeys[i], (Double.parseDouble(line[i])/0.028316)/1000); 
		}
	}


	//The EUR estimates from the German insitute needs conversion using "/0.02831". the others are on the same unit. 
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
		 }
		}
	}

	private void updateDNGProduction(CountryGasProducer gasProducer, String[] line, String[] fieldKeys)
	{
		for (int i=1; i<line.length; i++)
		{
			gasProducer.dngProduction.put(fieldKeys[i], Double.parseDouble(line[i]));
			//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
		}
	}
	
	private void updateDNGDemandGrowth(CountryGasProducer gasProducer,String[] line, String[] fieldKeys) 
	{
		for (int i=1; i<line.length; i++)
		{
			gasProducer.dnGasDemandGrowthAll.put(fieldKeys[i], Double.parseDouble(line[i]));
			//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
		}		
	}
	
	private void updateDNGConsumption(CountryGasProducer gasProducer, String[] line, String[] fieldKeys)
	{
		for (int i=1; i<line.length; i++)
		{
			gasProducer.dngConsumption.put(fieldKeys[i], Double.parseDouble(line[i]));
			//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  gasProducer.getName());
		}
	}

	
	
	
	//populates the countries with historical data
		public void initialiseCountriesOil(ArrayList<String> data, int fieldName)
		{
			String[] fieldKeys = data.get(0).split(","); // first row of the data file SHOULD be the column names
			for (int x=1; x<data.size();x++)
			{
				 String[] line = data.get(x).split(",");
				//only start from Country name (second column)
				if (countryFIPS.containsKey(line[0]))
				{
					CountryOilProducer oilProducer	=(CountryOilProducer) oilCountries.get(countryFIPS.get(line[0]));
					switch(fieldName)
					{
						case LPGCONSUMPTION:
							updateLPGSConsumption(oilProducer, line, fieldKeys);
							break;
						case NGPLPRODUCTION:
							updateNGPLProduction(oilProducer, line, fieldKeys);
							break;
						case TPCONSUMPTION:
							updateTPConsumption(oilProducer, line, fieldKeys);
							break;
						case CCPRODUCTION:
							updateCCProduction(oilProducer, line, fieldKeys);
						case HISTORICCRUDEOIL:
							updateHistoricCCProduction(oilProducer, line, fieldKeys);
							break;
						case EUROILCH:
							updateEUROilCH(oilProducer, line, fieldKeys);
							break;
						case OILDEMANDWEO2010:
							updateOilDemandWEO(oilProducer, line, fieldKeys);
							break;
						case USGSEUR:
							updateUSGSEUROil(oilProducer, line, fieldKeys);
							break;
						case PROVEDRESERVESEIA:
							provedReservesEIA(oilProducer, line, fieldKeys);
							break;
						case EUROILBGR:
							updateBGREUROil(oilProducer, line, fieldKeys);
							break;
						default: 
							System.out.println("I have not updated the agent:" + oilProducer.getName() + " form the file "+ fieldName);
					}
				}
				else
				{
					//System.out.println("Miss match between 'data file' and 'counrty file':" + line[0].toString() + " form the file "+ fieldName);
				}	   
			}
		}
		
		/**
		 * Data in Megaton  (Mt) tranformed to million barrels (1Mt = 6.849315 million barrels)
		 * @param oilProducer
		 * @param line
		 * @param fieldKeys
		 */
		private void updateBGREUROil(CountryOilProducer oilProducer, String[] line,
				String[] fieldKeys) 
		{
			for (int i=1; i<line.length; i++)
			{
				oilProducer.oilEURAll.put(fieldKeys[i], Double.parseDouble(line[i])*6.849315);
				//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  oilProducer.getName());
			}
			
		}

		/**
		 * This data is in billion barrels and are tranformed to million barrels
		 * @param oilProducer
		 * @param line
		 * @param fieldKeys
		 * 
		 */
		private void provedReservesEIA(CountryOilProducer oilProducer,
				String[] line, String[] fieldKeys) 
		{
			oilProducer.provedReservesCrudeOil=Double.parseDouble(line[1])*1000;
			
		}

		private void cleanModel()
	    {
		//Brunei and Malaysia
		CountryOilProducer[] countries = {(CountryOilProducer) oilCountries.get("BX"), (CountryOilProducer) oilCountries.get("MY")} ;
		CountryOilProducer originalCountry= (CountryOilProducer) oilCountries.get("BXMY");
		this.estimatedHistCrudeOilProdForSplitCountries(1980,1980,countries,originalCountry);
		oilCountries.remove("BXMY");
		
		//Merge production for Serbia with Serbia-Montenegro
		this.addCrudeOilProductions("RI","RIMJ",oilCountries);
		this.addTPOilConsumptions("RI","RIMJ",oilCountries); this.addLPGOilConsumptions("RI","RIMJ",oilCountries);
		oilCountries.remove("RIMJ");	

		//check countries!!
		//former yugoslavia
		CountryOilProducer[] countries2 = {(CountryOilProducer) oilCountries.get("MJ"),(CountryOilProducer) oilCountries.get("RI"),(CountryOilProducer) oilCountries.get("HR"),(CountryOilProducer) oilCountries.get("SI") }; 
		originalCountry= (CountryOilProducer) oilCountries.get("MJRIHRSI");
		this.estimatedHistCrudeOilProdForSplitCountries(1993,1992,countries2,originalCountry);
		///this.estimateTPConsForSplitCountries(,,,);
		///this.estimateLPGConsForSplitCountries(,,,);
		oilCountries.remove("MJRIHRSI");

		//czechslovakia
		CountryOilProducer[] countries3 = {(CountryOilProducer) oilCountries.get("LO"),(CountryOilProducer) oilCountries.get("EZ")}; 
		originalCountry= (CountryOilProducer) oilCountries.get("LOEZ");
		this.estimatedHistCrudeOilProdForSplitCountries(1993,1993,countries3,originalCountry);
		///this.estimateTPConsForSplitCountries(,,,);
		///this.estimateLPGConsForSplitCountries(,,,);
		oilCountries.remove("LOEZ");
		
		//check countries!!
		//FSU
		CountryOilProducer[] countries4 = {(CountryOilProducer) oilCountries.get("UP"),(CountryOilProducer) oilCountries.get("RS"), (CountryOilProducer) oilCountries.get("LH"), (CountryOilProducer) oilCountries.get("BO"),(CountryOilProducer) oilCountries.get("UZ"),(CountryOilProducer) oilCountries.get("TX"),(CountryOilProducer) oilCountries.get("TI"),(CountryOilProducer) oilCountries.get("KZ"),(CountryOilProducer) oilCountries.get("KG"),(CountryOilProducer) oilCountries.get("GG"),(CountryOilProducer) oilCountries.get("AJ")}; 
		originalCountry= (CountryOilProducer) oilCountries.get("FSU");
		this.estimatedHistCrudeOilProdForSplitCountries(1993,1992,countries4,originalCountry);
		//this.estimateTPConsForSplitCountries(,,,);
		///this.estimateLPGConsForSplitCountries(,,,);
		oilCountries.remove("FSU");
		
		//Denmark, Greenland, and FaroeIsland
		this.addOilEUR("DA","GL",oilCountries);
		this.addTPOilConsumptions("DA","GL",oilCountries);
		oilCountries.remove("GL");
		oilCountries.remove("DAGL");
		
		this.addTPOilConsumptions("DA","FO",oilCountries);
		oilCountries.remove("FO");
		
		//Germany East, West, and Offshore
		this.addCrudeOilProductions("GM","GMOff",oilCountries);
		this.addOilEUR("GM","GMOff",oilCountries);
		oilCountries.remove("GMOff");
		
		this.addCrudeOilProductions("GM","GC",oilCountries);
		this.addTPOilConsumptions("GM","GC",oilCountries); 
		this.addLPGOilConsumptions("GM","GC",oilCountries);
		oilCountries.remove("GC");
		
		this.addCrudeOilProductions("GM","GE",oilCountries);
		this.addTPOilConsumptions("GM","GE",oilCountries); 
		this.addLPGOilConsumptions("GM","GE",oilCountries);
		oilCountries.remove("GE");			

		//Netherlands, Aruba, and NLAntiles (aggregations)
		this.addCrudeOilProductions("NL","NLOff",oilCountries);
		oilCountries.remove("NLOff");
		
		this.addTPOilConsumptions("NL","AA",oilCountries); 
		this.addLPGOilConsumptions("NL","AA",oilCountries);
		oilCountries.remove("AA");

		this.addTPOilConsumptions("NL","NT",oilCountries); 
		this.addLPGOilConsumptions("NL","NT",oilCountries);
		oilCountries.remove("NT"); 
		
		//UK, UKw/Falkland, Falkland, Bermuda, Cayman, Gibraltar, Montserrat, SaintHelena 
		//Turks&CaicosIslands, and VirginUK (aggregation)
		this.addCrudeOilProductions("UK","UKOff",oilCountries); 
		this.addOilEUR("UK","UKOff",oilCountries);
		oilCountries.remove("UKOff");			

		this.addOilEUR("UK","UKFK",oilCountries);
		//this.addOilDemandGrowth("UK","UKFK", oilCountries);
		oilCountries.remove("UKFK");	

		this.addOilEUR("UK","FK",oilCountries); 
		this.addTPOilConsumptions("UK","FK",oilCountries);	
		oilCountries.remove("FK");
		
		this.addTPOilConsumptions("UK","BD",oilCountries); 
		this.addLPGOilConsumptions("UK","BD",oilCountries);
		oilCountries.remove("BD"); 

		this.addTPOilConsumptions("UK","CJ",oilCountries); 
		this.addLPGOilConsumptions("UK","CJ",oilCountries);
		oilCountries.remove("CJ");
		
		this.addTPOilConsumptions("UK","GI",oilCountries);		
		oilCountries.remove("GI");
		
		this.addTPOilConsumptions("UK","MH",oilCountries);			
		oilCountries.remove("MH"); 
		
		this.addTPOilConsumptions("UK","SH",oilCountries);			
		oilCountries.remove("SH"); 
		
		this.addTPOilConsumptions("UK","TK",oilCountries);			
		oilCountries.remove("TK");
		
		this.addTPOilConsumptions("UK","VI",oilCountries);			
		oilCountries.remove("VI"); 

		//China, HongKong, and Macau (aggregation)
		//this.addOilProductions("CH","HK", oilCountries);
		this.addTPOilConsumptions("CH","HK",oilCountries); 
		this.addLPGOilConsumptions("CH","HK",oilCountries);
		oilCountries.remove("HK");
		
		this.addTPOilConsumptions("CH","MC",oilCountries); 
		this.addLPGOilConsumptions("CH","MC",oilCountries);
		oilCountries.remove("MC"); 

		//US, PuertoRico, AmericanSamoa, Guam, VirginUS, and WakeIsland (aggregation)
		//this.addOilProductions("US","RQ", oilCountries);
		this.addTPOilConsumptions("US","RQ",oilCountries); 
		this.addLPGOilConsumptions("US","RQ",oilCountries);
		oilCountries.remove("RQ");

		this.addTPOilConsumptions("US","AQ",oilCountries); 
		this.addLPGOilConsumptions("US","AQ",oilCountries);
		oilCountries.remove("AQ"); 

		this.addTPOilConsumptions("US","GQ",oilCountries); 
		this.addLPGOilConsumptions("US","GQ",oilCountries);
		oilCountries.remove("GQ"); 
		
		this.addTPOilConsumptions("US","VQ",oilCountries); 
		this.addLPGOilConsumptions("US","VQ",oilCountries);
		oilCountries.remove("VQ");

		this.addTPOilConsumptions("US","WQ",oilCountries);	
		oilCountries.remove("WQ");
		
		//France, FrenchGuiana, FrenchPolynesia, Guedeloup, Martinique, NewCaledonia, Reunion,
		//and SaintPierre&Miquelon (aggregation)
		//this.addOilProductions("FR","FG", oilCountries);
		this.addOilEUR("FR","FG",oilCountries); 
		this.addTPOilConsumptions("FR","FG",oilCountries); 
		this.addLPGOilConsumptions("FR","FG",oilCountries);
		oilCountries.remove("FG"); 

		this.addTPOilConsumptions("FR","FP",oilCountries);			
		oilCountries.remove("FP");
		
		this.addTPOilConsumptions("FR","GP",oilCountries); 
		this.addLPGOilConsumptions("FR","GP",oilCountries);
		oilCountries.remove("GP");
		
		this.addTPOilConsumptions("FR","MB",oilCountries); 
		this.addLPGOilConsumptions("FR","MB",oilCountries);
		oilCountries.remove("MB"); 

		this.addTPOilConsumptions("FR","NC",oilCountries);
		this.addLPGOilConsumptions("FR","NC",oilCountries);
		oilCountries.remove("NC"); 
		
		this.addTPOilConsumptions("FR","RE",oilCountries);
		this.addLPGOilConsumptions("FR","RE",oilCountries);
		oilCountries.remove("RE"); 
		
		this.addTPOilConsumptions("FR","SB",oilCountries); 
		this.addLPGOilConsumptions("FR","SB",oilCountries);
		oilCountries.remove("SB");
		
		//NewZealand, CookIsland, and Niue (aggregation)
		this.addTPOilConsumptions("NZ","CW",oilCountries); 
		this.addLPGOilConsumptions("NZ","CW",oilCountries);
		oilCountries.remove("CW");
		
		this.addTPOilConsumptions("NZ","NE",oilCountries);
		oilCountries.remove("NE");
	   }
					
	   private void updateLPGSConsumption(CountryOilProducer oilProducer, String[] line, String[] fieldKeys)
		{
			//System.out.println(oilProducer.getName());
			for (int i=1; i<line.length; i++)
			{
				oilProducer.lpgConsumption.put(fieldKeys[i], Double.parseDouble(line[i])*0.365);
				//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  oilProducer.getName());
			}
		}
		
		private void updateNGPLProduction(CountryOilProducer oilProducer, String[] line, String[] fieldKeys)
		{
			for (int i=1; i<line.length; i++)
			{
				oilProducer.ngplProduction.put(fieldKeys[i], Double.parseDouble(line[i])*0.365);
				//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  oilProducer.getName());
			}
		}

		private void updateTPConsumption(CountryOilProducer oilProducer, String[] line, String[] fieldKeys)
		{
			for (int i=1; i<line.length; i++)
			{
				oilProducer.tpConsumption.put(fieldKeys[i], Double.parseDouble(line[i])*0.365);
				//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  oilProducer.getName());
			}
		}
		
		private void updateCCProduction(CountryOilProducer oilProducer, String[] line, String[] fieldKeys)
		{
			for (int i=1; i<line.length; i++)
			{
				oilProducer.ccProduction.put(fieldKeys[i], Double.parseDouble(line[i])*0.365);
				//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  oilProducer.getName());
			}
		}
		
		
		private void updateHistoricCCProduction(CountryOilProducer oilProducer, String[] line, String[] fieldKeys) 
		{
			for (int i=1; i<line.length; i++)
			{
				//System.out.println(fieldKeys[i]);
				oilProducer.historicCCProduction.put(fieldKeys[i], Double.parseDouble(line[i])/1000);
				//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  oilProducer.getName());
			}		
		}
		
		/**
		 * In Billion and tranformed to million barrels. 
		 * @param oilProducer
		 * @param line
		 * @param fieldKeys
		 */
		private void updateEUROilCH(CountryOilProducer oilProducer, String[] line,String[] fieldKeys) 
		{
			for (int i=1; i<line.length; i++)
			{
				oilProducer.oilEURAll.put(fieldKeys[i], Double.parseDouble(line[i])*1000);
				//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  oilProducer.getName());
			}
			
		}

		/**
		 * This is in million barrels (No tranformation needed). 
		 * @param oilProducer
		 * @param line
		 * @param fieldKeys
		 */
		private void updateUSGSEUROil(CountryOilProducer oilProducer, String[] line,String[] fieldKeys) 
		{
			for (int i=1; i<line.length; i++)
			{
				oilProducer.oilEURAll.put(fieldKeys[i], Double.parseDouble(line[i]));
				//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  oilProducer.getName());
			}
			
		}
		
		private void updateOilDemandWEO(CountryOilProducer oilProducer, String[] line, String[] fieldKeys) 
		{
			for (int i=1; i<line.length; i++)
			{
				oilProducer.oilDemandGrowthAll.put(fieldKeys[i], Double.parseDouble(line[i]));
				//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  oilProducer.getName());
			}		
		}

		
		private void estimateTPConsForSplitCountries(int firstYearAfterSplit,int calculationPeriod, CountryOilProducer[] countries, CountryOilProducer countryOrigin)
		{
			double sum=0;
			int numOfCountries = countries.length;
			for (int i=0; i<numOfCountries; i++)
			{
				sum = sum + Double.parseDouble(countries[i].tpConsumption.get(Integer.toString(firstYearAfterSplit)).toString());
			}
		
			double propotion=0;
			for (int i=0; i<numOfCountries; i++)
			{
				propotion=Double.parseDouble(countries[i].tpConsumption.get(Integer.toString(firstYearAfterSplit)).toString())/sum;
				//oil consumption
				Enumeration keysE =countryOrigin.tpConsumption.keys();
				Object key;
				double oilCons=0;
				while( keysE.hasMoreElements() )
			    {    
			   		key = keysE.nextElement();
			   	 	if (Integer.parseInt(key.toString())<calculationPeriod)
			   	 	{
			   	 		//System.out.println(countries[i].getFIPS());
			   	 		//System.out.println(key.toString() +"="+ propotion);
			   	 		oilCons=  Double.parseDouble(countryOrigin.tpConsumption.get(key).toString());
			   	 		countries[i].tpConsumption.put(key.toString(), oilCons*propotion);
			   	 		//System.out.println(countries[i].oilConsumptionAll);
			   	 	}
			    }	
			}			
		}

		private void estimateLPGConsForSplitCountries(int firstYearAfterSplit,int calculationPeriod, CountryOilProducer[] countries, 
				CountryOilProducer countryOrigin)
		{
			double sum=0;
			int numOfCountries = countries.length;
			for (int i=0; i<numOfCountries; i++)
			{
				sum = sum + Double.parseDouble(countries[i].lpgConsumption.get(Integer.toString(firstYearAfterSplit)).toString());
			}
		
			double propotion=0;
			for (int i=0; i<numOfCountries; i++)
			{
				propotion=Double.parseDouble(countries[i].lpgConsumption.get(Integer.toString(firstYearAfterSplit)).toString())/sum;
				//oil consumption
				Enumeration keysE =countryOrigin.lpgConsumption.keys();
				Object key;
				double oilCons=0;
				while( keysE.hasMoreElements() )
			    {    
			   		key = keysE.nextElement();
			   	 	if (Integer.parseInt(key.toString())<calculationPeriod)
			   	 	{
			   	 		//System.out.println(countries[i].getFIPS());
			   	 		//System.out.println(key.toString() +"="+ propotion);
			   	 		oilCons=  Double.parseDouble(countryOrigin.lpgConsumption.get(key).toString());
			   	 		countries[i].lpgConsumption.put(key.toString(), oilCons*propotion);
			   	 		//System.out.println(countries[i].oilConsumptionAll);
			   	 	}
			    }	
			}			
		}
		
		
		private void estimatedHistCrudeOilProdForSplitCountries(int firstYearOfPropotionCal,int calculationPeriod, CountryOilProducer[] countries, 
				CountryOilProducer countryOrigin)
		{
			
			double sum=0;
			int numOfCountries = countries.length;
			for (int i=0; i<numOfCountries; i++)
			{
				
				sum = sum + Double.parseDouble(countries[i].historicCCProduction.get(Integer.toString(firstYearOfPropotionCal)).toString());
			}
		
			double propotion=0;
			for (int i=0; i<numOfCountries; i++)
			{
				 propotion=Double.parseDouble(countries[i].historicCCProduction.get(Integer.toString(firstYearOfPropotionCal)).toString())/sum;
				 Enumeration keysE =countryOrigin.historicCCProduction.keys();
				 Object key;
				 double oilProd=0;
				 while( keysE.hasMoreElements() )
			     {    
			       	 	key = keysE.nextElement();
			       	 	if (Integer.parseInt(key.toString())<calculationPeriod)
			       	 	{
			       	 		//System.out.println(countries[i].getFIPS());
			       	 		//System.out.println(key.toString() +" "+ propotion);
			       	 		oilProd=  Double.parseDouble(countryOrigin.historicCCProduction.get(key).toString());
			       	 		countries[i].historicCCProduction.put(key.toString(), oilProd*propotion);
			       	 	}
			    }	
				
			}			
		}
		
		private void restOilDemandGrowth(String toAgent, String fromAgent, Hashtable oilCountries)
		{

			 CountryOilProducer agentTo =(CountryOilProducer) oilCountries.get(toAgent);
			 CountryOilProducer agentFrom =(CountryOilProducer) oilCountries.get(fromAgent); 	
			   
			 Enumeration keysE = agentFrom.oilDemandGrowthAll.keys();
			   //double mergedProd;  
			   Object key;
		       while( keysE.hasMoreElements() )
		       {    
		       	  key = keysE.nextElement();
		       	  double demandFrom = Double.parseDouble(agentFrom.oilDemandGrowthAll.get(key).toString());
		          double demandTo =  Double.parseDouble(agentTo.oilDemandGrowthAll.get(key).toString());
		          if (demandFrom > demandTo)
		          {
		        	  agentTo.oilDemandGrowthAll.put(key.toString(), demandFrom) ;   
		          }
		          else
		          {
		        		agentTo.oilDemandGrowthAll.put(key.toString(), demandTo) ;   	
		          }
		         
		       }	
		}
		
		private void addOilEUR(String toAgent, String fromAgent, Hashtable oilCountries)
		{

			 CountryOilProducer agentTo =(CountryOilProducer) oilCountries.get(toAgent);
			 CountryOilProducer agentFrom =(CountryOilProducer) oilCountries.get(fromAgent); 	
			   
			 Enumeration keysE = agentFrom.oilEURAll.keys();
			   double mergedProd;  
			   Object key;
		       while( keysE.hasMoreElements() )
		       {    
		       	 	key = keysE.nextElement();
		         	mergedProd= Double.parseDouble(agentFrom.oilEURAll.get(key).toString()) + Double.parseDouble(agentTo.oilEURAll.get(key).toString());
		         	agentTo.oilEURAll.put(key.toString(), mergedProd) ;   	
		       }	
		}
		
		private void addCrudeOilProductions(String toAgent, String fromAgent, Hashtable oilCountries)
		{
		   CountryOilProducer agentTo =(CountryOilProducer) oilCountries.get(toAgent);
		   CountryOilProducer agentFrom =(CountryOilProducer) oilCountries.get(fromAgent); 		
		   Enumeration keysE = agentFrom.ccProduction.keys();
		   double mergedProd;  
		   Object key;
	       while( keysE.hasMoreElements() )
	       {    
	       	 	key = keysE.nextElement();
	       	 	mergedProd= Double.parseDouble(agentFrom.ccProduction.get(key).toString()) + Double.parseDouble(agentTo.ccProduction.get(key).toString());
	         	if (fromAgent.equalsIgnoreCase("FK"))
	         	{
	       	 	   System.out.println(key.toString() + "," +mergedProd + ", "+ agentFrom.ccProduction.get(key).toString() + ", "+ agentTo.ccProduction.get(key).toString() );
	         	}
	         	agentTo.ccProduction.put(key.toString(), mergedProd) ;   	
	       }	
		}
		
		private void addTPOilConsumptions(String toAgent, String fromAgent, Hashtable oilCountries)
		{
		   CountryOilProducer agentTo =(CountryOilProducer) oilCountries.get(toAgent);
		   CountryOilProducer agentFrom =(CountryOilProducer) oilCountries.get(fromAgent); 		
		   Enumeration keysE = agentFrom.tpConsumption.keys();
		   double mergedProd;  
		   Object key;
	       while( keysE.hasMoreElements() )
	       {    
	       	 	key = keysE.nextElement();
	         	mergedProd= Double.parseDouble(agentFrom.tpConsumption.get(key).toString()) + Double.parseDouble(agentTo.tpConsumption.get(key).toString());
	         	//System.out.println(key.toString() + "," +mergedProd + ", "+ agentFrom.oilProductionAll.get(key).toString() + ", "+ agentTo.oilProductionAll.get(key).toString() );
	         	agentTo.tpConsumption.put(key.toString(), mergedProd) ;   	
	       }	
		}
		
		private void addLPGOilConsumptions(String toAgent, String fromAgent, Hashtable oilCountries)
		{
		   CountryOilProducer agentTo =(CountryOilProducer) oilCountries.get(toAgent);
		   CountryOilProducer agentFrom =(CountryOilProducer) oilCountries.get(fromAgent); 		
		   Enumeration keysE = agentFrom.lpgConsumption.keys();
		   double mergedProd;  
		   Object key;
	       while( keysE.hasMoreElements() )
	       {    
	       	 	key = keysE.nextElement();
	         	mergedProd= Double.parseDouble(agentFrom.lpgConsumption.get(key).toString()) + Double.parseDouble(agentTo.lpgConsumption.get(key).toString());
	         	//System.out.println(key.toString() + "," +mergedProd + ", "+ agentFrom.oilProductionAll.get(key).toString() + ", "+ agentTo.oilProductionAll.get(key).toString() );
	         	agentTo.lpgConsumption.put(key.toString(), mergedProd) ;   	
	       }	
		}
	
	
	public ArrayList<String> readOilConsumption(String file)
	{
		 CSVFileReader csvFile=new CSVFileReader(file);
		 csvFile.readFile();
		 //csvFile.displayArrayList();
		 return csvFile.storeValues;
		 
	}
	
	 /**
	 * Prepares the agents of the simulation with their name and FIPS code
	 * 
	*/	
	public void createCountries(ArrayList<String> data)
	{
		 countryFIPS.clear();
		 oilCountries.clear();
		 gasCountries.clear();
		 for(int x=1;x<data.size();x++)
		 {
				 String[] line = data.get(x).split(",");
				if (!line[1].equalsIgnoreCase("Region"))
				 { 
					  CountryOilProducer oilProducer= new CountryOilProducer(this.myModel);
					  //set up the initial values		 
					  oilProducer.setName(line[0]);
					  oilProducer.setFIPS(line[1]);
					  countryFIPS.put(line[0], line[1]);
					  //System.out.println(oilProducer.getFIPS());		  
					  oilCountries.put(oilProducer.getFIPS(), oilProducer);
					  
					  CountryGasProducer gasProducer= new CountryGasProducer(this.myModel);
					  //set up the initial values		 
					  gasProducer.setName(line[0]);
					  gasProducer.setFIPS(line[1]);	  
					  gasCountries.put(gasProducer.getFIPS(), gasProducer);	
				 }
		  }
	}
	
	private void setUpFinalModelAgentShorted()
	{
		 // Sort hashtable.
	    Vector v = new Vector(countryFIPS.keySet());
	    Collections.sort(v);
	    String val="" ;
	    CountryOilProducer agentoil=null; 
	    CountryGasProducer agentgas=null; 

	    // Display (sorted) hashtable.
	    for (Enumeration e = v.elements(); e.hasMoreElements();) 
	    {
	      String key = (String)e.nextElement();
	      if (oilCountries.containsKey(countryFIPS.get(key)))
	      {
	    	  agentoil =(CountryOilProducer) oilCountries.get(countryFIPS.get(key));
              this.myModel.addOilAgent(agentoil);
              agentgas =gasCountries.get(countryFIPS.get(key));
              this.myModel.addDNGAgent(agentgas);  	                   
	      }
	     // System.out.println("Key: " + countryFIPS.get(key) + "     Val: " + val);
	     // val="KENOOOOOOOOOOOOOOOO";
	    }
	  }
}
