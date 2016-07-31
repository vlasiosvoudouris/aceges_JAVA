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
import aceges.utilities.io.CSVFileReader;

public class ACEGESModelInitialisationOil 
{
	public  Hashtable oilCountries = new Hashtable();
	public  Hashtable countryFIPS = new Hashtable();
	public  ACEGESApp myModel=null;
	
	public  ArrayList <String>dataEIA = new ArrayList<String>(); //various data files from the Energy Information Administration
	
	static final int LPGCONSUMPTION=0;
	static final int NGPLPRODUCTION=1;
	static final int TPCONSUMPTION=2;
	static final int CCPRODUCTION=3;
	static final int HISTORICCRUDEOIL=4;
	static final int EUROILCH=5;
	static final int OILDEMANDWEO2010=6;
	static final int USGSEUROil=7;
	static final int PROVEDRESERVESEIA=8;
	static final int EUROILBGR=9;
	static final int USGSEURUPOil=10;
	static final int EUROILBGRUNCONV=11;
	static final int USGSEURNGL=12;
	static final int USGSEURUPNGL=13;
	
	/**
	 * 
	 * @param target
	*/
	public ACEGESModelInitialisationOil(ACEGESApp target)
	{
		this.myModel=target;
		this.createCountries(readOilConsumption("data/Countries.csv"));
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Consumption/Consumption_of_Liquefied_Petroleum_Gases_(Thousand_Barrels_Per_Day).csv");
		initialiseCountries(dataEIA, LPGCONSUMPTION);
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Production/ngl/Production_of_Natural_Gas_Plant_Liquids_(Thousand_Barrels_Per_Day).csv"); 
		initialiseCountries(dataEIA, NGPLPRODUCTION);
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Consumption/Total_Petroleum_Consumption_(Thousand_Barrels_Per_Day).csv"); 
		initialiseCountries(dataEIA, TPCONSUMPTION);
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Production/crude/Production_of_Crude_Oil_including_Lease_Condensate_(Thousand_Barrels_Per_Day).csv"); 
		initialiseCountries(dataEIA, CCPRODUCTION);		
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Production/crude/historicalFixed/historicCrudeOilProduction.csv"); 
		initialiseCountries(dataEIA, HISTORICCRUDEOIL);
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Production/EUR/crude/EURCHBillion.csv"); 
		initialiseCountries(dataEIA, EUROILCH);
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Consumption/DemandGrowthWEO.csv"); 
		initialiseCountries(dataEIA, OILDEMANDWEO2010);
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Production/EUR/crude/EURUSGSMMBO.csv"); 
		initialiseCountries(dataEIA, USGSEUROil);
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Production/EUR/crude/EURUSGSMMBOupdate.csv"); 
		initialiseCountries(dataEIA, USGSEURUPOil);
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Production/EUR/ngl/EURUSGSNGL_MilB.csv"); 
		initialiseCountries(dataEIA, USGSEURNGL);
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Production/EUR/ngl/EURUSGSNGLupdate_MilB.csv"); 
		initialiseCountries(dataEIA, USGSEURUPNGL);
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Production/EUR/crude/EURBGRMegaton.csv"); 
		initialiseCountries(dataEIA, EUROILBGR);
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Production/EUR/crude/EURBGRUnconventionalMegaton.csv"); 
		initialiseCountries(dataEIA, EUROILBGRUNCONV);
		dataEIA= this.readOilConsumption("data/FossilFuels/oil/Production/EUR/crude/Crude_Oil_Proved_Reserves_(Billion_Barrels).csv"); 
		initialiseCountries(dataEIA, PROVEDRESERVESEIA);
		dataEIA.clear();
		dataEIA=null;
		this.cleanModel();
//		this.checkOilInitialization(); //km20120703
		this.setUpFinalModelAgentShorted();
	}

	/**
	 * 
	 */
	public void checkOilInitialization()
	{
		Enumeration e = oilCountries.elements();
		String str;
		
		while(e.hasMoreElements())
		{
			CountryOilProducer element = (CountryOilProducer) e.nextElement();
			Set<String> set= element.oilDemandGrowthAll.keySet();
	    	Iterator<String> itr = set.iterator();
	    	System.out.println(element.getName()+":"+element.oilDemandGrowthAll.get("WEOPolicy01")+":"+element.oilDemandGrowthAll.get("WEOPolicy02")+":"+element.oilDemandGrowthAll.get("WEOPolicy03"));
 		}
	}
	

	/**
	 * 
	 * @param data
	 * @param fieldName
	 */
	public void initialiseCountries(ArrayList<String> data, int fieldName)
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
					case USGSEUROil:
						updateUSGSEUROil(oilProducer, line, fieldKeys, true);
						break;
					case PROVEDRESERVESEIA:
						provedReservesEIA(oilProducer, line, fieldKeys);
						break;
					case EUROILBGR:
						updateBGREUROil(oilProducer, line, fieldKeys);
						break;
					case USGSEURUPOil://this updates the original data from the USGS based on the undiscovered file in 2012
						updateUSGSEUROil(oilProducer, line, fieldKeys, false);
						break;
					case EUROILBGRUNCONV://DO SOMETHING WITH UNCONENTIONAL
						break;
					case USGSEURNGL:
						updateUSGSEURNGL(oilProducer, line, fieldKeys, true);
						break;
					case USGSEURUPNGL:
						updateUSGSEURNGL(oilProducer, line, fieldKeys, false);
						break;					
					default: 
						System.out.println("I have not updated the agent:" + oilProducer.getName() + " form the file "+ fieldName);
				}
			}
			else
			{
				//System.err.println("Miss match between 'data file' and 'counrty file':" + line[0].toString() + " form the file "+ fieldName);
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

	/**
	 * It merged various datsets before the final model is initialised 
	 */
	private void cleanModel()
    {
		//Brunei and Malaysia
		CountryOilProducer[] countries = {(CountryOilProducer) oilCountries.get("BX"), (CountryOilProducer) oilCountries.get("MY")} ;
		CountryOilProducer originalCountry= (CountryOilProducer) oilCountries.get("BXMY");
		this.estimateHistOilNGLConProdForSplitCountries(1980,1980,countries,originalCountry);
	
		//former yugoslavia
		CountryOilProducer[] countries2 = {(CountryOilProducer) oilCountries.get("MJ"),(CountryOilProducer) oilCountries.get("RI"),(CountryOilProducer) oilCountries.get("HR"),(CountryOilProducer) oilCountries.get("SI") }; 
		originalCountry= (CountryOilProducer) oilCountries.get("MJRIHRSI");
		this.estimateHistOilNGLConProdForSplitCountries(1993,1992,countries2,originalCountry);

		//czechslovakia
		CountryOilProducer[] countries3 = {(CountryOilProducer) oilCountries.get("LO"),(CountryOilProducer) oilCountries.get("EZ")}; 
		originalCountry= (CountryOilProducer) oilCountries.get("LOEZ");
		this.estimateHistOilNGLConProdForSplitCountries(1993,1993,countries3,originalCountry);
	
		//FSU
		CountryOilProducer[] countries4 = {(CountryOilProducer) oilCountries.get("UP"),(CountryOilProducer) oilCountries.get("RS"), (CountryOilProducer) oilCountries.get("LH"), (CountryOilProducer) oilCountries.get("BO"),(CountryOilProducer) oilCountries.get("UZ"),(CountryOilProducer) oilCountries.get("TX"),(CountryOilProducer) oilCountries.get("TI"),(CountryOilProducer) oilCountries.get("KZ"),(CountryOilProducer) oilCountries.get("KG"),(CountryOilProducer) oilCountries.get("GG"),(CountryOilProducer) oilCountries.get("AJ")}; 
		originalCountry= (CountryOilProducer) oilCountries.get("FSU");
		this.estimateHistOilNGLConProdForSplitCountries(1993,1992,countries4,originalCountry);
	
		//Merge production for Serbia with Serbia-Montenegro
		this.addOilNGLData("RI","RIMJ",oilCountries);
		
		//Denmark (DA), Greenland (GL), and FaroeIsland (FO)
		this.addOilNGLData("DA","GL",oilCountries);
		this.addOilNGLData("DA","FO",oilCountries);
		// no need for any merge - this only exists in the fixed historic file - just remove it. 
		oilCountries.remove("DAGL");	
	
		//Germany (GM), Germany East (GC), Germany West (GE), and Germany Offshore (GMOff)
		this.addOilNGLData("GM","GMOff",oilCountries);
		this.addOilNGLData("GM","GC",oilCountries);
		this.addOilNGLData("GM","GE",oilCountries);
		
		//Netherlands, Aruba, and NLAntiles (aggregations)
		this.addOilNGLData("NL","NLOff",oilCountries);
		this.addOilNGLData("NL","AA",oilCountries);
		this.addOilNGLData("NL","NT",oilCountries);
	
		//UK, UKw/Falkland, Falkland, Bermuda, Cayman, Gibraltar, Montserrat, SaintHelena 
		//Turks&CaicosIslands, and VirginUK (aggregation)
		this.addOilNGLData("UK","UKOff",oilCountries);
		this.addOilNGLData("UK","UKFK",oilCountries);
		this.addOilNGLData("UK","FK",oilCountries);
		this.addOilNGLData("UK","BD",oilCountries);
		this.addOilNGLData("UK","CJ",oilCountries);
		this.addOilNGLData("UK","GI",oilCountries);
		this.addOilNGLData("UK","MH",oilCountries);
		this.addOilNGLData("UK","SH",oilCountries);
		this.addOilNGLData("UK","TK",oilCountries);
		this.addOilNGLData("UK","VI",oilCountries);

		//China, HongKong, and Macau (aggregation)
		this.addOilNGLData("CH","HK",oilCountries);
		this.addOilNGLData("CH","MC",oilCountries);

		//US, PuertoRico, AmericanSamoa, Guam, VirginUS, and WakeIsland (aggregation)
		//this.addOilProductions("US","RQ", oilCountries);
		this.addOilNGLData("US","RQ",oilCountries);
		this.addOilNGLData("US","AQ",oilCountries);
		this.addOilNGLData("US","GQ",oilCountries);
		this.addOilNGLData("US","VQ",oilCountries);
		this.addOilNGLData("US","WQ",oilCountries);
	
		//France, FrenchGuiana, FrenchPolynesia, Guedeloup, Martinique, NewCaledonia, Reunion,
		//and SaintPierre&Miquelon (aggregation)
		this.addOilNGLData("FR","FG",oilCountries);
		this.addOilNGLData("FR","FP",oilCountries);
		this.addOilNGLData("FR","GP",oilCountries);
		this.addOilNGLData("FR","MB",oilCountries);
		this.addOilNGLData("FR","NC",oilCountries);
		this.addOilNGLData("FR","RE",oilCountries);
		this.addOilNGLData("FR","SB",oilCountries);
	
		//NewZealand, CookIsland, and Niue (aggregation)
		this.addOilNGLData("NZ","CW",oilCountries);
		this.addOilNGLData("NZ","NE",oilCountries);
    }
	
	/**
	 * Merges (add) the crude oil and NGPL production AND TP and LPG consumption datasets
	 * It also adds the EUR. At the end, it removes the fromAgent from the list. 
	 * @param toAgent
	 * @param fromAgent
	 * @param oilCountries
	 */
	private void addOilNGLData(String toAgent, String fromAgent, Hashtable oilCountries)
	{
		this.addCrudeOilProductions(toAgent,fromAgent,oilCountries);
		this.addNGPLProductions(toAgent,fromAgent,oilCountries);
		this.addTPOilConsumptions(toAgent,fromAgent,oilCountries); 
		this.addLPGOilConsumptions(toAgent,fromAgent,oilCountries);
		this.addOilEUR(toAgent,fromAgent,oilCountries);
		oilCountries.remove(fromAgent);		
	}
	
	/**
	 * 
	 * @param oilProducer
	 * @param line
	 * @param fieldKeys
	 */
	private void updateLPGSConsumption(CountryOilProducer oilProducer, String[] line, String[] fieldKeys)
	{
		for (int i=1; i<line.length; i++)
		{
			oilProducer.lpgConsumption.put(fieldKeys[i], Double.parseDouble(line[i])*0.365);
			//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  oilProducer.getName());
		}
	}
	
	/**
	 * 
	 * @param oilProducer
	 * @param line
	 * @param fieldKeys
	 */
	private void updateNGPLProduction(CountryOilProducer oilProducer, String[] line, String[] fieldKeys)
	{
		for (int i=1; i<line.length; i++)
		{
			oilProducer.ngplProduction.put(fieldKeys[i], Double.parseDouble(line[i])*0.365);
			//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  oilProducer.getName());
		}
	}

	/**
	 * 
	 * @param oilProducer
	 * @param line
	 * @param fieldKeys
	 */
	private void updateTPConsumption(CountryOilProducer oilProducer, String[] line, String[] fieldKeys)
	{
		for (int i=1; i<line.length; i++)
		{
			oilProducer.tpConsumption.put(fieldKeys[i], Double.parseDouble(line[i])*0.365);
			//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  oilProducer.getName());
		}
	}
	
	/**
	 * 
	 * @param oilProducer
	 * @param line
	 * @param fieldKeys
	 */
	private void updateCCProduction(CountryOilProducer oilProducer, String[] line, String[] fieldKeys)
	{
		for (int i=1; i<line.length; i++)
		{
			oilProducer.ccProduction.put(fieldKeys[i], Double.parseDouble(line[i])*0.365);
			//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  oilProducer.getName());
		}
	}
	
	/**
	 * 
	 * @param oilProducer
	 * @param line
	 * @param fieldKeys
	 */
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
	 * This is in million barrels (No transformation needed). 
	 * @param oilProducer
	 * @param line
	 * @param fieldKeys
	 */
	private void updateUSGSEUROil(CountryOilProducer oilProducer, String[] line,String[] fieldKeys, boolean addOriginalUSGS) 
	{
		double oldEUR=0d;
		String key;
		for (int i=1; i<line.length; i++)
		{
			if(addOriginalUSGS)
			{
				oilProducer.oilEURAll.put(fieldKeys[i], Double.parseDouble(line[i]));
			}
			else
			{
				key=fieldKeys[i];
				//System.out.println(key);
				if (oilProducer.oilEURAll.containsKey(key))
				{
					oldEUR=(Double) oilProducer.oilEURAll.get(key);
					oilProducer.oilEURAll.put(key,oldEUR+Double.parseDouble(line[i]));
//					System.out.println(key +":"+ oldEUR + " + " +  Double.parseDouble(line[i])+ "="+
//							(oldEUR+Double.parseDouble(line[i])) +" for " +  oilProducer.getName());
				}

			}
		}
	}
	
	/**
	 * This is in million barrels (No transformation needed). 
	 * @param oilProducer
	 * @param line
	 * @param fieldKeys
	 */
	private void updateUSGSEURNGL(CountryOilProducer oilProducer, String[] line,String[] fieldKeys, boolean addOriginalUSGS) 
	{
		double oldEUR=0d;
		String key;
		for (int i=1; i<line.length; i++)
		{
			if(addOriginalUSGS)
			{
				oilProducer.oilEURAll.put(fieldKeys[i], Double.parseDouble(line[i]));
			}
			else
			{
				key=fieldKeys[i];
				//System.out.println(key);
				if (oilProducer.oilEURAll.containsKey(key))
				{
					oldEUR=(Double) oilProducer.oilEURAll.get(key);
					oilProducer.oilEURAll.put(key,oldEUR+Double.parseDouble(line[i]));
//					System.out.println(key +":"+ oldEUR + " + " +  Double.parseDouble(line[i])+ "="+
//							(oldEUR+Double.parseDouble(line[i])) +" for " +  oilProducer.getName());
				}

			}
		}
	}
	
	/**
	 * 
	 * @param oilProducer
	 * @param line
	 * @param fieldKeys
	 */
	private void updateOilDemandWEO(CountryOilProducer oilProducer, String[] line, String[] fieldKeys) 
	{
		for (int i=1; i<line.length; i++)
		{
			oilProducer.oilDemandGrowthAll.put(fieldKeys[i], Double.parseDouble(line[i]));
			//System.out.println(fieldKeys[i] +":"+ Double.parseDouble(line[i]) +" for " +  oilProducer.getName());
		}		
	}
	
	
	/**
	 * Prepares the agents of the simulation with their name and FIPS code
	 * 
	*/	
	public void createCountryNameFIPS(String file)
	{
		ArrayList<String> data=	readOilConsumption(file);
		countryFIPS.clear();
		for(int x=1;x<data.size();x++)
		{
			String[] line = data.get(x).split(",");
			countryFIPS.put(line[0], line[1]);
			//System.out.println(line[1] + ":" + line[0]);
		}
	}
	
	
	/**
	 * Prepares the agents of the simulation with their name and FIPS code
	 * 
	*/	
	public void createCountries(ArrayList<String> data)
	{
		countryFIPS.clear();
		oilCountries.clear();
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
				//System.out.println(oilProducer.toString());		  
				oilCountries.put(oilProducer.getFIPS(), oilProducer);
			}
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
		return csvFile.storeValues;
	}

	/**
	 * 
	 * @param firstYearAfterSplit
	 * @param calculationPeriod
	 * @param countries
	 * @param countryOrigin
	 */
	private void estimateTPConsForSplitCountries(int firstYearAfterSplit,int calculationPeriod, CountryOilProducer[] countries, CountryOilProducer countryOrigin)
	{
		double sum=0;
		int numOfCountries = countries.length;
		for (int i=0; i<numOfCountries; i++)
		{
			sum = sum + Double.parseDouble(countries[i].tpConsumption.get(Integer.toString(firstYearAfterSplit)).toString());
		}
		
		if (sum>0)
		{
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
		else
		{
		   System.err.println("the sum is zero in the function estimateTPConsForSplitCountries()");
		}
			
	}

	/**
	 * 
	 * @param firstYearAfterSplit
	 * @param calculationPeriod
	 * @param countries
	 * @param countryOrigin
	 */
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
	
	/**
	 * 
	 * @param firstYearOfPropotionCal
	 * @param calculationPeriod
	 * @param countries
	 * @param countryOrigin
	 */
	private void estimateHistOilNGLConProdForSplitCountries(int firstYearOfPropotionCal,int calculationPeriod, CountryOilProducer[] countries, CountryOilProducer countryOrigin)
	{
		this.estimateTPConsForSplitCountries(firstYearOfPropotionCal,calculationPeriod,countries,countryOrigin);
		this.estimatedHistCrudeOilProdForSplitCountries(firstYearOfPropotionCal,calculationPeriod,countries,countryOrigin);
		this.estimatedHistNGPLProdForSplitCountries(firstYearOfPropotionCal,calculationPeriod,countries,countryOrigin);
		oilCountries.remove(countryOrigin.getFIPS());
	}
	
	/**
	 * It calculates the production before the splitting years (e.g., FSU) and the deletes the original country (e.g., FSU). 
	 * @param firstYearOfPropotionCal
	 * @param calculationPeriod
	 * @param countries
	 * @param countryOrigin
	 */
	private void estimatedHistNGPLProdForSplitCountries(int firstYearOfPropotionCal,int calculationPeriod, CountryOilProducer[] countries, 
			CountryOilProducer countryOrigin)
	{
		double sum=0;
		int numOfCountries = countries.length;
		for (int i=0; i<numOfCountries; i++)
		{
			sum = sum + Double.parseDouble(countries[i].ngplProduction.get(Integer.toString(firstYearOfPropotionCal)).toString());
		}
		
		if (sum>0)
		{
			double propotion=0;
			for (int i=0; i<numOfCountries; i++)
			{
				propotion=Double.parseDouble(countries[i].ngplProduction.get(Integer.toString(firstYearOfPropotionCal)).toString())/sum;
				Enumeration keysE =countryOrigin.ngplProduction.keys();
				Object key;
				double nglProd=0;
				while( keysE.hasMoreElements() )
				{    
					key = keysE.nextElement();
					if (Integer.parseInt(key.toString())<calculationPeriod)
					{
						nglProd=  Double.parseDouble(countryOrigin.ngplProduction.get(key).toString());
						countries[i].ngplProduction.put(key.toString(), nglProd*propotion);
					}
				}	
			}		
		}
		else
		{
			  System.err.println("the sum is zero in the function estimatedHistNGPLProdForSplitCountries()");
		}
	
		
	}
	
	
	/**
	 * It calculates the production before the splitting years (e.g., FSU) and the deletes the original country (e.g., FSU). 
	 * @param firstYearOfPropotionCal
	 * @param calculationPeriod
	 * @param countries
	 * @param countryOrigin
	 */
	private void estimatedHistCrudeOilProdForSplitCountries(int firstYearOfPropotionCal,int calculationPeriod, CountryOilProducer[] countries, 
			CountryOilProducer countryOrigin)
	{
		double sum=0;
		int numOfCountries = countries.length;
		for (int i=0; i<numOfCountries; i++)
		{
			sum = sum + Double.parseDouble(countries[i].historicCCProduction.get(Integer.toString(firstYearOfPropotionCal)).toString());
		}
	
		if(sum>0)
		{
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
		else
		{
			  System.err.println("the sum is zero in the function estimatedHistCrudeOilProdForSplitCountries()");
		}
	}
	
	/**
	 * 
	 * @param toAgent
	 * @param fromAgent
	 * @param oilCountries
	 */
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
	
	/**
	 * 
	 * @param toAgent
	 * @param fromAgent
	 * @param oilCountries
	 */
	private void addOilEUR(String toAgent, String fromAgent, Hashtable oilCountries)
	{
		CountryOilProducer agentTo =(CountryOilProducer) oilCountries.get(toAgent);
		CountryOilProducer agentFrom =(CountryOilProducer) oilCountries.get(fromAgent); 	
		   
		Enumeration keysE = agentFrom.oilEURAll.keys();
		double mergedProd=0;  
		Object key;
		while(keysE.hasMoreElements() )
		{    
			key = keysE.nextElement();
			if (agentTo.oilEURAll.containsKey(key))
			{	
			mergedProd= Double.parseDouble(agentFrom.oilEURAll.get(key).toString()) + Double.parseDouble(agentTo.oilEURAll.get(key).toString());
			agentTo.oilEURAll.put(key.toString(), mergedProd) ;   
			}
			else
			{
				mergedProd= Double.parseDouble(agentFrom.oilEURAll.get(key).toString());
				agentTo.oilEURAll.put(key.toString(), mergedProd) ;   
			}
		}	
	}
	
	/**
	 * 
	 * 
	 * @param toAgent
	 * @param fromAgent
	 * @param oilCountries
	 */
	private void addNGPLProductions(String toAgent, String fromAgent, Hashtable oilCountries)
	{
		CountryOilProducer agentTo =(CountryOilProducer) oilCountries.get(toAgent);
		CountryOilProducer agentFrom =(CountryOilProducer) oilCountries.get(fromAgent); 		
		Enumeration keysE = agentFrom.ngplProduction.keys();
		double mergedProd;  
		Object key;
		while( keysE.hasMoreElements() )
		{    
			key = keysE.nextElement();
			mergedProd= Double.parseDouble(agentFrom.ngplProduction.get(key).toString()) + Double.parseDouble(agentTo.ngplProduction.get(key).toString());
			agentTo.ngplProduction.put(key.toString(), mergedProd) ;   	
		}	
	}
	
	/**
	 * 
	 * 
	 * @param toAgent
	 * @param fromAgent
	 * @param oilCountries
	 */
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
				//System.out.println(key.toString() + "," +mergedProd + ", "+ agentFrom.ccProduction.get(key).toString() + ", "+ agentTo.ccProduction.get(key).toString() );
			}
			agentTo.ccProduction.put(key.toString(), mergedProd) ;   	
		}	
	}
	
	/**
	 * 
	 * @param toAgent
	 * @param fromAgent
	 * @param oilCountries
	 */
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
	
	/**
	 * 
	 * @param toAgent
	 * @param fromAgent
	 * @param oilCountries
	 */
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
	
	/**
	 * 
	 */
	private void setUpFinalModelAgentShorted()
	{
		//Sort hashtable.
		Vector v = new Vector(countryFIPS.keySet());
	    Collections.sort(v);
	    String val="" ;
	    CountryOilProducer agent=null; 
	    // Display (sorted) hashtable.
	    for (Enumeration e = v.elements(); e.hasMoreElements();)
	    {
	    	String key = (String)e.nextElement();
	    	if (oilCountries.containsKey(countryFIPS.get(key)))
	    	{
	    		agent =(CountryOilProducer) oilCountries.get(countryFIPS.get(key));
	    		this.myModel.addOilAgent(agent);
	    	}
	    }
	    countryFIPS.clear();
	    countryFIPS=null;
	    oilCountries.clear();
	    oilCountries=null;
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		ACEGESModelInitialisationOil model =new ACEGESModelInitialisationOil(null);		 	 
	}
}