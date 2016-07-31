//Chapter3.6 of Mohr PhD thesis
//demand seems to be global
package aceges.utilities.fossilfuelmodel;

import org.apache.commons.math3.util.FastMath;

public final class Demand {
	//static double population = 0; //population
	/** per cap demand i = 0:coal, 1:oil, 2:gas, 3:total  */
	public static double[] dFF = {0,0,0,0}; 
	/**  demand 0:coal, 1:oil, 2:gas, 3:total	 */
	public static double[] qDemandFF = {0,0,0,0}; 
	/**  max achievable per cap demand (global and all fuels p49; GJ/person) */
	public static double dMax = 62;
	/** rate for demand (global p49) */
	public static double rDemand = 0.02502; 
	/** year to reach max per cap demand (global p49) */
	public static int yMax = 1974; 
	/**  fraction of demand 0:coal, 1:oil, 2:gas, 3:total */
	public static double[] fFF = {0,0,0,1};
	/**  highest possible fraction of gas (global p49) */
	public static double fGasHigh = 0.27;
	/**  lowest possible fraction of gas (global p49) */
	public static double fGasLow = 0;
	/** rate for gas fraction (global p49) */
	public static double rGas = 0.03; 
	/**  year of production to reach average of fGasHigh and Low (global p49) */
	public static int yGas = 1960; 
	/** highest possible fraction of coal (global p50) */
	public static double fCoalHigh = 0.295;
	/** lowest possible fraction of coal (global p50) */
	public static double fCoalLow = 1;
	/** rate for coal fraction (global p50) */
	public static double rCoal = 0.03; 
	/** time constant for coal (global p50) */
	public static double rCoal2 = 1969.07; 
	/** asymmetric constant for coal fraction (global p50) */
	public static double rCoal3 = 25; 
	
	/**
	 * Estimate the demand for a fossil fuel such as oil, gas and coal!
	 * @param currentYear
	 * @param population = future population is available at UN or can be estimated using eq 3.6.1
	 * @param gasCountries
	 */
	public static void fuelDemand(final int currentYear,final double population)
	{
		//eq3.6.2
		for(int i=0;i<4;i++)
		{
			if(currentYear<yMax){ dFF[i] = dMax*FastMath.pow(FastMath.E,(rDemand*(currentYear-yMax))); }
			else{ dFF[i] = dMax; }
		}
		fFF[2] = (fGasHigh-fGasLow)/2 * FastMath.tanh(rGas*(currentYear-yGas)) + (fGasHigh+fGasLow)/2; //eq3.6.4
		fFF[0] = (fCoalHigh-fCoalLow)/FastMath.pow((1+FastMath.pow(FastMath.E,(-rCoal*rCoal3*(currentYear-rCoal2)))),(1/rCoal3)) + fCoalLow; //eq3.6.5
		fFF[1] = fFF[3] - fFF[2] - fFF[0];

		for(int i=0;i<4;i++)
		{
			qDemandFF[i] = fFF[i] * dFF[i] * population; //eq3.6.9
		}		
	}
}
