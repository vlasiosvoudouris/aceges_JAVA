//Chapter3.5 of Mohr PhD thesis
//three types of disruptions
package aceges.utilities.fossilfuelmodel;

import java.util.Iterator;
import java.util.Set;
import org.apache.commons.math3.util.FastMath;

public final class Disruption {
	/** start year of disruption */
	public static int yStart = 0; 
	/** end year of disruption */
	public static int yEnd = 0; 
	/** percentage of mines/wells(fields?) online in the last year of disruption */
	public static double rOnline = 0; 
	/** number of mines/fields online during disruption period */
	public static int nOnline = 0; 
	/** variable in qEURExploit */
	public static double qu = 0;
	
	/**
	 * Unfortunate component of the real world such as war
	 * @param currentYear
	 * @param isField
	 */
	public static void disruptionsFF(final int currentYear, final boolean isField, final int numberOnlineStart)
	{
		Field field = new Field();
		Mine mine = new Mine();	
		nOnline = (int) (numberOnlineStart * ((1-rOnline)/(yStart-yEnd)*(currentYear-yStart)+1)); //eq3.5.1 (for all fossil fuelds)
		if(isField){ //for field model (3.5.2-3 for 3.2.7)
			Set<String> set = field.nField.keySet(); 
			Iterator<String> itr = set.iterator();
			String str;    
			while(itr.hasNext())
			{ //calculation by region
				str = itr.next();

				field.nField.put(str,(int) (field.nField.get(str)+field.rField*field.nFieldTotal.get(str)*field.pFieldRegion.get(str)/field.qEURRegion.get(str))); //eq3.5.2-3
			}
		}
		else
		{ //for mine model (3.5.4-5 for 3.4.6)
			Set<String> set = mine.qEURMineFirst.keySet(); 
			Iterator<String> itr = set.iterator();
			String str;
	    
			while(itr.hasNext())
			{ //calculation by region
				str = itr.next();
				
				qu = (mine.qEURCountry-mine.qEURMineFirst.get(str))/(1-FastMath.pow(FastMath.E,-mine.rExploit)); //eq3.5.5

				double cr = FastMath.pow(FastMath.E,(-mine.rExploit*mine.pMineRegion.get(str)/mine.qEURCountry));
				mine.qEURExploit.put(str,mine.qEURExploit.get(str)*cr+qu-qu*cr); //eq3.5.4
			}	
		}
	}
}