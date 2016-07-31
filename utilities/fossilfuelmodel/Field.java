//Chapter3.2 and 3.3 of Mohr PhD thesis
//for gas (conventional and unconventional) and oil (conventional)
//Are eq3.2.7(original version), eq3.2.10, eq3.3.5, and related equations necessary?? 

//assuming hashtable like below is completed
/* Image of hashtable for field/mine
 * country	field(mine)			A11_region	A12_region...	A11_EURfield	A12_EURfield...
 * A		A11,A12,A13,A21,...	A1			A1				value			value
 * B		B11,B21,...			B1(different key from others)...
 * C		C11,C12,...			C1(different key from others)...
 * ...
 */

package aceges.utilities.fossilfuelmodel;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.math3.util.FastMath;

public final class Field {

	/** start year of the field operation*/
	public static int yStart = 0;
	/** production increasing period (for all countries p35) */
	public static int tIncrease = 1;
	/** year starting to decline */
	public static int tDecline = 0;
	/** maximum (and plateau) production in field */
	public static double pMax = 0;
	/** total amount of fuels in declining phase in field */
	public static double qRemain = 0;
	/** EUR in field */
	public static double qEURField = 0;
	/** EUR in region*/
	public static Hashtable<String,Double> qEURRegion = new Hashtable<String,Double>();
	/** life time of field*/
	public static int lField = 0; 
	/** production from field (should be array)*/
	public static double pField = 0;
	/** production from region */
	public static Hashtable<String,Double> pFieldRegion = new Hashtable<String,Double>(); 
	/** production from country */
	public static Hashtable<String,Double> pFieldCountry = new Hashtable<String,Double>();
	/** constant (for pMax; different by country p35 and Appendix A) */
	public static Hashtable<String,Integer> rMax;
	/** constant (for qRemain; different by country p35 and Appendix A) */
	public static Hashtable<String,Integer> rRemain = new Hashtable<String,Integer>();
	/** number of field online - original (<= nFieldTotal)*/
	public static Hashtable<String,Integer> nField = new Hashtable<String,Integer>();
	/** number of field online - alternative */
	public static Hashtable<String,Integer> nField2 = new Hashtable<String,Integer>(); 
	/** number of field online (previous period)*/
	public static Hashtable<String,Integer> nField_1 = new Hashtable<String,Integer>();
	/** constant (rate of field online; for all countries and both gas and oil p38)*/
	public static double rField = 1.05;  
	/** total number of field in each region (by country p37 and Appendix A) */
	public static Hashtable<String,Integer> nFieldTotal = new Hashtable<String,Integer>(); 
	/** cumulative production in region */
	public static Hashtable<String,Double> cFieldRegion = new Hashtable<String,Double>(); 
	/** EUR in the operating (online) field */
	public static Hashtable<String,Double> qEUROnline = new Hashtable<String,Double>();  
	/** estimated amount of EUR in region exploited */
	public static double qEURExploit = 0;
	/** estimated amount of EUR in region exploited (previous year) */
	public static Hashtable<String,Double> qEURExploit_1 = new Hashtable<String,Double>(); 
	/** constant (rate for qEURExploit; by country p38 and Appendix A) */
	public static Hashtable<String,Double> rExploit = new Hashtable<String,Double>();  
	/** production in country */
	public static double pCountry = 0;
	/** production in region */
	public static double pRegion = 0;
	/** number of regions online - original */
	public static Hashtable<String,Integer> nRegion = new Hashtable<String,Integer>(); 
	/** number of regions online - alternative */
	public static Hashtable<String,Integer> nRegion2 = new Hashtable<String,Integer>();
	/** total number of region in each country (by country Appendix A) */
	public static Hashtable<String,Integer> nRegionTotal = new Hashtable<String,Integer>(); 
	/** cumulative production in country */
	public static double qCountry = 0; //
	/** EUR in country (by country p40 and Appendix A)*/
	public static double qEURCountry = 0;  
	/** sum of EUR in the first X regions */
	public static double qEps = 0; //
	/** sum of EUR in the first x regions (previous year) */
	public static double qEps_1 = 0;
	/** constant (rate for qEps by country p42 and Appendix A) */
	public static Hashtable<String,Double> rEps = new Hashtable<String,Double>();  
	//public static String reg; // name of region each field belongs

	/**
	 * Field and region model (conventional oil and conventional and unconventional gas
	 * @param currentYear
	 * @param fieldList
	 * @param reg
	 * @param cnt
	 * @param qEURField
	 * @param rRemain
	 * @param rMax
	 * @param yStart
	 * @param rEps
	 * @param nFieldTotal
	 * @param rExploit
	 * @param qEURCountry
	 * @param qCountry
	 * @param gasCountries
	 */
	public static void prodField(final int currentYear, final String fieldList, final String reg, final String cnt, final double qEURField, 
			final double rRemain, final double rMax, final int yStart, final double rEps,
			final double nFieldTotal, final double rExploit, final double qEURCountry, final double qCountry)
	{
		
		//FFFF (field information in hashtable) should be defined in model initialization
		//if need to reuse the calculated results somewhere else, variables must be Hashtable
		//String fieldList= element.FFFF.get("field"); //get all names of fields in the country
		//reg = (String) element.FFFF.get(fieldEach[i]+"_region");
		//qEURField = (Double) element.FFFF.get(fieldEach[i]+"_EURfield");
		//rRemain = (Double) element.FFFF.get(fieldEach[i]+"_reteremain");
		//rMax = (Double) element.FFFF.get(fieldEach[i]+"_ratemax");
		//yStart = (Integer) element.FFFF.get(fieldEach[i]+"_yearstart");
		//nFieldTotal.put(reg,(Integer) element.FFFF.get(fieldEach[i]+"_totalfield"));
		//rExploit.put(reg,(Double) element.FFFF.get(fieldEach[i]+"_rateexploit")); 

		pFieldRegion.clear();
		pFieldCountry.clear();
		qEURRegion.clear();
		qEUROnline.clear();
		nField.clear();
		nField2.clear();
		
		String[] fieldEach = fieldList.split(",");
			
		for(int i=0;i<fieldEach.length;i++)
		{ //iteration by field
			qRemain = qEURField * rRemain; //eq3.2.5
			pMax = qEURField * rMax; //eq3.2.4

			tDecline = (int) ((qEURField - qRemain)/pMax + tIncrease/2 + yStart); //eq3.2.2
			lField = (int) (tDecline - FastMath.log(0.01)*qRemain/pMax/0.99 - yStart); //eq3.2.3 
			
			//eq3.2.1
			if(currentYear>=yStart && currentYear<(yStart+tIncrease)){ pField = pMax*(currentYear-yStart)/tIncrease; }
			else if(currentYear>=(yStart+tIncrease) && currentYear<tDecline){ pField = pMax; }
			else if(currentYear>=tDecline && currentYear<=(tDecline-FastMath.log(0.01)*qRemain/pMax/0.99)){ pField = pMax*FastMath.pow(FastMath.E,-pMax*0.99*(currentYear-tDecline)/qRemain); }
			else { pField = 0; } //t<yStart or t>(tDecline-FastMath.log(0.01)*qRemain/pMax/0.99)

			pFieldRegion.put(reg,(pFieldRegion.get(reg)+pField)); //eq3.2.6
			pFieldCountry.put(cnt,(pFieldCountry.get(cnt)+pField)); //eq3.3.1 
			qEURRegion.put(reg,(qEURRegion.get(reg)+qEURField)); //calculate EUR in each region

			//cFieldRegion is better to be created in other java file. Also need initial data
			cFieldRegion.put(reg,cFieldRegion.get(reg)+pField); 

			if(pField>0)
			{
				qEUROnline.put(reg,qEUROnline.get(reg)+qEURField); //eq3.2.8
				nField2.put(reg,nField2.get(reg)+1); //eq.3.2.7 (alternative)
			}
		}
		//after iteration by field (iteration by country)
		prodFieldRegion(cnt);
		prodFieldCountry(currentYear,cnt,qEURCountry,qCountry,rEps);
	}
	
	public static void prodFieldRegion(final String cnt){
		Set<String> set = qEUROnline.keySet(); 
		Iterator<String> itr = set.iterator();
	    String str;
	    
	    nRegionTotal.put(cnt,set.size());
	    
		while(itr.hasNext()){ //calculation by region
			str = itr.next();
			nField.put(str,(int) (rField * nFieldTotal.get(str) * cFieldRegion.get(str) / qEURRegion.get(str))); //eq3.2.7 (original)

			qEURExploit = qEURRegion.get(str) * FastMath.pow((nField.get(str)/nFieldTotal.get(str)),rExploit.get(str)); //eq3.2.9		
			//qEURExploit = qEURRegion.get(str) * FastMath.pow((nField2.get(str)/nFieldTotal.get(str)),rExploit.get(str)); //eq3.2.9 (using nField2)		
			if(qEURExploit_1.get(str)!=null && nField_1.get(str)!=null){
				qEURField = (qEURExploit-qEURExploit_1.get(str)) / (nField.get(str)-nField_1.get(str)); //eq3.2.10 - need this?
				//qEURField = (qEURExploit-qEURExploit_1.get(str)) / (nField2.get(str)-nField_1.get(str)); //eq3.2.10 (using nField2) - need this?
			}
			qEURExploit_1.put(str,qEURExploit);
			nField_1.put(str,nField.get(str));
			//nField_1.put(str,nField2.get(str)); //using nField2
		}
	}
	
	public static void prodFieldCountry(final int currentYear, final String cnt, final double qEURCountry, final double qCountry, final double rEps){
		nRegion.clear();
		nRegion2.clear();
		
		nRegion.put(cnt,(int) (nRegionTotal.get(cnt) * FastMath.sqrt(qCountry/qEURCountry))); //eq3.3.2 (but possible to count)

		//alternative eq3.3.2			
		Set<String> set = pFieldRegion.keySet(); 
		Iterator<String> itr = set.iterator();
		String str;
		while(itr.hasNext())
		{ //calculation by region
			str = itr.next();
			if(pFieldRegion.get(str)>0)
			{
				nRegion2.put(cnt,nRegion2.get(cnt)+1);
			}
		}

		for(int i=1;i<nRegionTotal.get(cnt)+1;i++)
		{
			//eq3.3.4 (no eq.3.3.3)
			qEps = qEURCountry * (1-FastMath.pow(FastMath.E,-rEps*(i/nRegionTotal.get(cnt))*(i/nRegionTotal.get(cnt))))
			     / (1-FastMath.pow(FastMath.E,-rEps));
				
			qEURRegion.put(Integer.toString(i),(qEps-qEps_1)); //eq3.3.5, but not possible to distinguish regions - need this?
			qEps_1 = qEps;
		}
	}
}