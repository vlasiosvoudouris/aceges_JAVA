package aceges.utilities.maths;



import org.apache.commons.math3.util.FastMath;

public final class ArithmeticSeries 
{
 	  
	  public static double[] getSeries(final double start, final double end, final double step) 
	  {
		  int length =(int)FastMath.ceil((end - start)/(step))+1;
		  double[] x = new double[length];	  
		  for (int i=0; i<length;i++)
	      {
	    	 x[i]= start + step * i;
	      }
		  return x;		  
	  }
	  
	  public static double[] getSeries(final double start, final double end, final int length) 
	  {
		  double step=(end-start)/(length-1);
		  double[] x = new double[length];		  
		  for (int i=0; i<length;i++)
	      {
	    	 x[i]= start + step * i;
	      }
		  return x;		  
	  }
	  	  
	  public static double[] getRep(final double number, final int times)
	  {
		  double[] x= new double[times];
		  for (int i=0; i<times;i++)
	      {
	    	 x[i]= number;
	      }
		  return x;	
	  }
	  
	  public static void main(String[] args) 
	  {
		  long t=System.currentTimeMillis();
		  ArithmeticSeries.getRep(10, 1000);      
	      System.out.println((System.currentTimeMillis()-t));
	  }

}
