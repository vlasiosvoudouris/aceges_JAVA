package aceges.support;

import java.io.IOException;

import aceges.gui.GUIACEGESApp;
import aceges.utilities.io.WriteCSV;

public class SaveInitialModelData 
{
	private GUIACEGESApp guiModel;
	private String file;
	private WriteCSV csvWriter;
	private String[] data= new String[12];;
	

	public SaveInitialModelData(GUIACEGESApp guiModel)
	{
		this.guiModel=guiModel;
		this.csvWriter = new WriteCSV("oilData.csv");
		
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
		
	}
	
	
	
	
}
