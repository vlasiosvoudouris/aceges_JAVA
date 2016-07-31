//Chapter3.7 of Mohr PhD thesis
//by country? or whole world?
//two types of interaction of demand and supply:
//independent dynamic: interactions separately by fossil fuel (different driver by fossil fuel)
//dynamic: interactions on all fossil fuels (one driver - total of fossil fuels)
package aceges.utilities.fossilfuelmodel;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.math3.util.FastMath;

public final class Interaction {
	/** driver for dynamics i = 0:coal, 1:oil, 2:gas, 3:total */
	public static double[] gFF = {0,0,0,0};
	/** fossil fuel production */
	public static double[] qProductionFF = {0,0,0,0};
	/** constant (for per capita demand) */
	public static double kd = 0;
	/** factor (for n-th fossil fuel (for all countries p50) */
	public static double[] fa = {1,1,1,1};
	/** constant (for number of fields online) */
	public static double kf = 0;
	/** constant (for number of mines online) */
	public static double km = 0;
	/** driver for oil */
	public static double gRegionOil = 0; 
	/** driver for gas */
	public static double gRegionGas = 0; 
	/** minimum level of driver for region model */
	public static double gRegionMin = 0;
	/** number of oil region */
	public static int nRegionOil = 0;
	/** number of gas region */
	public static int nRegionGas = 0;
	/** constant (for number of regions) */
	public static double kr = 0;
	/** driver level of driver for mine model */
	public static double gMineMin = 0;
	/** driver fo coal mine */
	public static double gMineCoal = 0;
	/** driver fo oil mine */
	public static double gMineOil = 0;
	/** number of mines to upgrade */
	public static int nMineUpgrade = 0;
	/** constant (for upgrade of mines) */
	public static double ku = 0;
	/** upgrade factor for maximum production of mine (1:not upgrading or 2:upgrading) */
	public static Hashtable<String,Integer> upgradeMine = new Hashtable<String,Integer>();
	/** remaining amount in mines of each region (in descending order!) */
	public static Hashtable<String,Double> qRemainMine = new Hashtable<String,Double>();
	
	/**
	 * supply and demand interaction model to converge the difference
	 * @param currentYear
	 * @param reg
	 * @param qRemainMine
	 * @param isPerCapitaDemand
	 * @param isNumberOfField
	 * @param isNumberOfMine
	 * @param isNumberOfRegion
	 * @param isUpgradeOfMine
	 * @param isCoal
	 * @param isOil
	 * @param isGas
	 * @param gasCountries
	 */
	//Do this calculation at the end of previous year or at first of current year
	public static void dynamic(final int currentYear, final double[] qProductionFF, final String reg, final Hashtable<String,Double> qRemainMine,
			final boolean isPerCapitaDemand, final boolean isNumberOfField, final boolean isNumberOfMine,
			final boolean isNumberOfRegion, final boolean isUpgradeOfMine,
			final boolean isCoal, final boolean isOil, final boolean isGas, final boolean isDynamic){
		
		Demand demand = new Demand();
		Field field = new Field();
		Mine mine = new Mine();
		Disruption disruption = new Disruption();
		
		if(isDynamic)
		{
			kd = 0.15; kf = 0.1; km = 0.01; kr = 0.1; 
			gRegionMin = 0.2; ku = 0.1; gMineMin = 0.2;
		}
		
		//eq3.7.1-4
		for(int i=0;i<4;i++)
		{
			gFF[i] = (demand.qDemandFF[i] - qProductionFF[i]) / qProductionFF[i]; //demand and production in previous year 
		}

		if(isPerCapitaDemand) { perCapitaDemand(demand); }
		if(isNumberOfField)   { numberOfField(field,isOil,isGas); }
		if(isNumberOfMine)    { numberOfMine(mine,disruption,isCoal,isOil); }
		if(isNumberOfRegion)  { numberOfRegion(field,isOil,isGas); }
		if(isUpgradeOfMine)   { upgradeOfMine(mine,isCoal,isOil,currentYear,reg,qRemainMine); }
	}

	public static void perCapitaDemand(final Demand demand)
	{
		double[] du = {0,0,0,0};
		int i;
		for(i=0;i<4;i++)
		{ 
			du[i] = demand.dFF[i] * (FastMath.pow(FastMath.E,demand.rDemand) - kd*gFF[i]); //eq3.7.6 
		}
		
		//eq3.7.5 (corresponding to eq3.6.2)
		if(demand.dFF[i]>demand.dMax && du[i]>demand.dMax)
		{
			demand.dFF[i] = demand.dFF[i] * (1-kd*gFF[i]);
		}
		else if(demand.dFF[i]<=demand.dMax && du[i]>demand.dMax)
		{
			demand.dFF[i] = demand.dMax - demand.dFF[i]*kd*gFF[i];
		}
		else if(du[i]<=demand.dMax)
		{
			demand.dFF[i] = du[i];
		}
	}
	
	public static void numberOfField(final Field field, final boolean isOil, final boolean isGas)
	{ //field model eq3.7.7 (corresponding to eq3.5.3, that is eq3.2.7)
		if(isOil)
		{ //for oil field (conventional)
			Set<String> set = field.nField.keySet(); 
			Iterator<String> itr = set.iterator();
		    String str;
			    
			while(itr.hasNext())
			{ //pFieldRegion should be time t not t-1!
				str = itr.next();
				field.nField.put(str,(int) (field.nField.get(str)+field.rField*field.nFieldTotal.get(str)*field.pFieldRegion.get(str)/field.qEUROnline.get(str)
		                                   +kf*fa[1]*field.nField.get(str)*gFF[1]));
			}
		}				
		else if(isGas)
		{ //for gas (conventional and unconventional)
			Set<String> set = field.nField.keySet(); 
			Iterator<String> itr = set.iterator();
		    String str;
			    
			while(itr.hasNext())
			{ //pFieldRegion should be time t not t-1!
				str = itr.next();
				field.nField.put(str,(int) (field.nField.get(str)+field.rField*field.nFieldTotal.get(str)*field.pFieldRegion.get(str)/field.qEUROnline.get(str)
			   		                       +kf*fa[2]*field.nField.get(str)*gFF[2]));
			}
		}
	}

	public static void numberOfMine(final Mine mine, final Disruption disruption, final boolean isCoal, final boolean isOil)
	{ //mine model eq.3.7.8 (corresponding to eq3.5.4, that is eq3.4.6)
		if(isCoal)
		{ //for coal
			Set<String> set = mine.qEURExploit.keySet(); 
			Iterator<String> itr = set.iterator();
		    String str;
			    
			while(itr.hasNext())
			{ //pMineRegion should be time t not t-1!
				str = itr.next();
				double rq = FastMath.pow(FastMath.E,(-mine.rExploit*mine.pMineRegion.get(str)/mine.qEURCountry));
				mine.qEURExploit.put(str,mine.qEURExploit.get(str)*rq+disruption.qu-disruption.qu*rq+km*fa[0]*mine.qEURExploit.get(str)*gFF[0]);
			}
		}
		else if(isOil)
		{ //for oil mine (unconventional)
			Set<String> set = mine.qEURExploit.keySet(); 
			Iterator<String> itr = set.iterator();
		    String str;
			    
			while(itr.hasNext())
			{ //pMineRegion should be time t not t-1!
				str = itr.next();
				double rq = FastMath.pow(FastMath.E,(-mine.rExploit*mine.pMineRegion.get(str)/mine.qEURCountry));
				mine.qEURExploit.put(str,mine.qEURExploit.get(str)*rq+disruption.qu-disruption.qu*rq+km*fa[1]*mine.qEURExploit.get(str)*gFF[1]);
			}
		}
	}
	
	public static void numberOfRegion(final Field field, final boolean isOil, final boolean isGas)
	{ //field model 
		if(isOil){ //for oil field (conventional)
			Set<String> set = field.nRegion.keySet(); 
			Iterator<String> itr = set.iterator();
		    String str;
			    
			while(itr.hasNext())
			{ //pCountry should be time t not t-1
				str = itr.next();
				//eq3.7.9
				if(gFF[1]<=gRegionMin){ gRegionOil = 0; }
				else{ gRegionOil = gFF[1] - gRegionMin; }

				//eq3.7.10-11
				field.nRegion.put(str,(int) (field.nRegion.get(str)
				    	                    *FastMath.sqrt((field.qEURCountry*field.nRegion.get(str)*field.nRegion.get(str)+field.pCountry*field.nRegionTotal.get(str)*field.nRegionTotal.get(str))/(field.qEURCountry*field.nRegion.get(str)*field.nRegion.get(str)))
		                                    +kr*fa[1]*field.nRegion.get(str)*gFF[1])); 
			}
		}
		else if(isGas)
		{ //for gas (unconventional)
			Set<String> set = field.nRegion.keySet(); 
			Iterator<String> itr = set.iterator();
		    String str;
			    
			while(itr.hasNext())
			{ //pCountry should be time t not t-1
				str = itr.next();
				//eq3.7.9
				if(gFF[2]<=gRegionMin){ gRegionGas = 0; }
				else{ gRegionGas = gFF[2] - gRegionMin; }

				//eq3.7.10-11
				field.nRegion.put(str,(int) (field.nRegion.get(str)
                                        *FastMath.sqrt((field.qEURCountry*field.nRegion.get(str)*field.nRegion.get(str)+field.pCountry*field.nRegionTotal.get(str)*field.nRegionTotal.get(str))/(field.qEURCountry*field.nRegion.get(str)*field.nRegion.get(str)))
                                        +kr*fa[2]*field.nRegion.get(str)*gFF[2])); 
			}
		}
	}

	public static void upgradeOfMine(final Mine mine, final boolean isCoal, final boolean isOil, final int currentYear,
			final String reg, final Hashtable<String,Double> qRemainMine)
	{ //mine model - maximum production will be twice
		int i = 0;

		if(isCoal)
		{
			//eq3.7.12
			if(gFF[0]<=gMineMin){ gMineCoal = 0; }
			else{ gMineCoal = gFF[0]-gMineMin; }

			//eq3.7.13 - which nMine is it? region? country?; nMine should be time t not t-1
			nMineUpgrade = (int) (ku * fa[0] * mine.nMine.get(reg) * gMineCoal);
		}
		else if(isOil)
		{
			//eq3.7.12
			if(gFF[1]<=gMineMin){ gMineOil = 0; }
			else{ gMineOil = gFF[1]-gMineMin; }

			//eq3.7.13 - which nMine is it? region? country?; nMine should be time t not t-1
			nMineUpgrade = (int) (ku * fa[1] * mine.nMine.get(reg) * gMineOil);
		}

		Set<String> set = qRemainMine.keySet(); 
		Iterator<String> itr = set.iterator();
	    String str;

	    //initialization of upgradeMine
	    while(itr.hasNext())
		{
			str = itr.next();
			upgradeMine.put(str,1);
		}
	    
		//need to select "nMineUpgrade" mines which have most remaining reserves and minimum of 10-year operating life
		while(i<nMineUpgrade && itr.hasNext())
		{
			if((currentYear-mine.yStart)>=10)
			{
				str = itr.next();
				upgradeMine.put(str,2);
				i++;
			}
		}
	}
}