//Chapter3.4 of Mohr PhD thesis
//coal and unconventional oil
//Are eq3.4.7 (original version), eq3.4.8, and related equations necessary??
package aceges.utilities.fossilfuelmodel;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.math3.util.FastMath;

public final class Mine {
	/** production of mine */
	public static double pMine = 0;
	/** production from mines in region */
	public static Hashtable<String,Double> pMineRegion = new Hashtable<String,Double>();
	/** maximum production of mine - function of time */
	public static double pMineMax = 0;
	/** life time of mine - function of time */
	public static int lMine = 0;
	/** initially assumed mine life (by country p 47 and Appendix A) */
	public static int lMineInit = 0; 
	/** initially assumed annual production (by country p 46 and Appendix A) */
	public static double pMineInit = 0; 
	/** finally-reaching maximum mine life (by country p 47 and Appendix A) */
	public static int lMineFin = 0; 
	/** finally-reaching maximum annual production (by country p 46 and Appendix A) */
	public static double pMineFin = 0; 
	/** constant for production and life (for all countries p46) */
	public static double rTime = 0.037;
	/** midpoint year (by country p46 and Appendix A) */
	public static double tTime = 0;
	/** start year of mining */
	public static int yStart = 0;
	/** year first mine become online in each region */
	public static Hashtable<String,Integer> yFirstRegion = new Hashtable<String,Integer>(); 
	/** EUR of mine */
	public static double qEURMine = 0;
	/** EUR of active mines in a region */
	public static Hashtable<String,Double> qEURMineRegion = new Hashtable<String,Double>(); 
	/** EUR of active mines in a region (previous year) */
	public static Hashtable<String,Double> qEURMineRegion_1 = new Hashtable<String,Double>(); 
	/** EUR of first mine in a region */
	public static Hashtable<String,Double> qEURMineFirst = new Hashtable<String,Double>(); 
	/** EUR of mines start at current year in region */
	public static Hashtable<String,Double> qEURMineStart = new Hashtable<String,Double>(); 
	/** EUR of mines being exploited */
	public static Hashtable<String,Double> qEURExploit = new Hashtable<String,Double>(); 
	/** EUR of country */
	public static double qEURCountry = 0; 
	/** constant (for qExploit; by country p46 and Appendix A) */
	public static double rExploit = 0;
	/** cumulative production of country */
	public static double qCountry = 0; 
	/** number of mine online - original */
	public static Hashtable<String,Integer> nMine = new Hashtable<String,Integer>(); 
	/** number of mine online - alternative */
	public static Hashtable<String,Integer> nMine2 = new Hashtable<String,Integer>(); 
    /** name of region */
	public static String reg;
	/** name of country */
	public static String cnt;
    
	/**
	 * Field and region model (conventional oil and conventional and unconventional gas
	 * @param currentYear
	 * @param fieldList 
	 * @param reg
	 * @param cnt
	 * @param lMineFin
	 * @param lMineInit
	 * @param pMineFin
	 * @param pMineInit
	 * @param yStart
	 * @param qEURCountry
	 * @param qCountry
	 * @param gasCountries
	 */
	public static void prodMine(final int currentYear, final String fieldList, final String reg, final String cnt,
			final int lMineFin, final int lMineInit, final double pMineFin, final double pMineInit,
			final int tTime, final int yStart, final double qEURCountry, final double qCountry)
	{
  	    //production of current year is difference of cum production of t-1/2 and t+1/2
		//MMMM (mine information in hashtable) should be defined in model initialization
		//String fieldList= element.MMMM.get("mine"); //get all names of mines in the country
		//reg = (String) element.MMMM.get(fieldEach[i]+"_region");
		//cnt = element.MMMM.get(fieldEach[i]+"_");
		//lMineFin = (Integer) element.MMMM.get(fieldEach[i]+"_minelifefinal");
		//lMineInit = (Integer) element.MMMM.get(fieldEach[i]+"_minelifeinitial");
		//pMineFin = (Double) element.MMMM.get(fieldEach[i]+"_mineproductionfinal");
		//pMineInit = (Double) element.MMMM.get(fieldEach[i]+"_mineproductioninitial");
		//yStart = (Integer) element.MMM.get(fieldEach[i]+"_startyear");
  	    
  	    pMineRegion.clear();
  	    qEURMineRegion.clear();
  	    nMine2.clear();
  	    yFirstRegion.clear();
  	    qEURMineFirst.clear();
  	    qEURMineStart.clear();
  	    qEURExploit.clear();
  	    
  	    String[] fieldEach = fieldList.split(",");

  	    for(int i=0;i<fieldEach.length;i++)
  	    { //iteration by field
  	    	//if need to reuse the calculated results somewhere else, variables must be Hashtable
			
  	    	lMine = (int) ((lMineFin+lMineInit)/2 + FastMath.tanh(rTime*(currentYear-tTime)) * (lMineFin-lMineInit)/2); //eq3.4.1
  	    	pMineMax = (pMineFin+pMineInit)/2 + FastMath.tanh(rTime*(currentYear-tTime)) * (pMineFin-pMineInit)/2; //eq3.4.2
			
  	    	//eq3.4.3
  	    	if(currentYear>=yStart && currentYear<(yStart+4)){ pMine = pMineMax*(currentYear-yStart)/4; }
  	    	else if(currentYear>=(yStart+4) && currentYear<(yStart+lMine-4)){ pMine = pMineMax; }
  	    	else if(currentYear>=(yStart+lMine-4) && currentYear<(yStart+lMine)){ pMine = pMineMax*(yStart+lMine-currentYear); }
  	    	else { pMine = 0; } //t<yStart or t>(yStart+lMine)

  	    	pMineRegion.put(reg,(pMineRegion.get(reg)+pMine)); //eq3.4.4

  	    	qEURMine = pMineMax * (lMine - 4);

  	    	if(pMine!=0)
  	    	{
  	    		qEURMineRegion.put(reg,(qEURMineRegion.get(reg)+qEURMine)); //eq3.4.5
  	    		nMine2.put(reg,nMine2.get(reg)+1); //eq.3.4.7 (alternative)
  	    	}
				
  	    	if(currentYear==yStart){
  	    		qEURMineStart.put(reg,qEURMineStart.get(reg)+qEURMine);
  	    	}
				
  	    	if(yFirstRegion.get(reg)==null || yFirstRegion.get(reg)>yStart)
  	    	{
  	    		yFirstRegion.put(reg,yStart);
  	    		qEURMineFirst.put(reg,qEURMine); //for eq3.4.6
  	    	}
  	    }
  	    prodMineRegion(); //need this?
	}
	
	//need this method?
	public static void prodMineRegion(){
		Set<String> set = qEURMineFirst.keySet(); 
		Iterator<String> itr = set.iterator();
	    String str;
		int a = 0;
	    
		while(itr.hasNext()){ //calculation by region
			str = itr.next();
			double cq = FastMath.pow(FastMath.E,-rExploit);
			qEURExploit.put(str,(qEURCountry-qEURMineFirst.get(str)*cq)/(1-cq) 
				              - (qEURCountry-qEURMineFirst.get(str))/(1-cq)*FastMath.pow(FastMath.E,(-rExploit*qCountry/qEURCountry))); //eq3.4.6

			//need this?
			if(qEURExploit.get(str)>qEURMineRegion_1.get(str)){ // qEURMineRegion_1 is previous-year data
				a = (int) ((qEURExploit.get(str)-qEURMineRegion_1.get(str))/qEURMineStart.get(str) + 1); //eq3.4.8?? need check qEURMineStart
			}
			else if(qEURExploit.get(str)>qEURMineRegion_1.get(str)){ //a is max number of mines that can be removed such that qExploit-qEURMineRegion<0 in this year 
				//... not yet
				//a: negative value
			}

			nMine.put(str,nMine.get(str)+a); //eq3.4.7
			qEURMineRegion_1.put(str,qEURMineRegion.get(str)); //to get previous-year data
		}
	}
}