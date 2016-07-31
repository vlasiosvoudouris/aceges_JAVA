package aceges.utilities.math;

import org.apache.commons.math3.stat.StatUtils;

public final class MatrixFunctions 
{	  	
	  /**
	   * if the length of the return matrix is zero, then the diff was not completed properly. 
	   */
	  public static double[] getDiff(final double[] data)
	  {
		  double[] newdata= new double[data.length-1];
		  
		  for (int i=0; i<newdata.length; i++)
		  {
			  newdata[i]= data[i+1]- data[i];
		  }
		  return newdata;	
	  }
	  
	  /**
	   * finds the maximum absolute (max(abs(v1-v2)) of a vector. 
	   */
	  public static double maxABS(final double[] v1, final double[] v2)
	  { 
		  double newdata=0d;
		  double[] temp=new double[v1.length]; 
		  for (int i=0; i<v1.length; i++)
		  {
			 temp[i]= v1[i]-v2[i];
		  }
		  newdata=StatUtils.max(temp);
		  return newdata;	
	  }
	  
	  /**
	   * if the length of the return vector is zero, then the function was not completed properly. 
	   * Check the 'diff' operator. 
	   */
	  public static double[] getDiff(final double[] data, final int diff)
	  {
		  double[] newdata= new double[data.length-1];
		  for (int i=0; i<diff;i++)
		  {
			  if (i==0)
			  {
			     newdata=MatrixFunctions.getDiff(data);
			  }
			  else
			  {
				  newdata=MatrixFunctions.getDiff(newdata); 
			  }
		  }		  
		  return newdata;
	  }
	  
	  /**
	   * if the length of the return matrix is zero, then the function was not completed properly. 
	   * Check the 'diff' operator. 
	   */
	  public static double[][] getDiff(final double[][] data, final int diff)
	  {
		 double[][] newdata= new double[data.length-1][data[0].length];
		 for (int i=0; i<diff;i++)
		 {
			  if (i==0)
			  {
			     newdata=MatrixFunctions.getDiff(data);
			  }
			  else
			  {
				  newdata=MatrixFunctions.getDiff(newdata); 
			  }
			  
		 }		 		 
		 return newdata;		 
	  }
	  
	  /**
	   * if the length of the return matrix is zero, then the diff was not completed properly. 
	   */
	  public static double[][] getDiff(final double[][] data)
	  {
		 double[][] newdata= new double[data.length-1][data[0].length];
		 
		 for(int i=0;i<newdata.length; i++)
		 {
			 for(int j=0;j<newdata[i].length;j++)
			 {
				 newdata[i][j]= data[i+1][j]-data[i][j];
			 }
		 }
		 
		 return newdata;		 
	  }
	    
	  /**
	   * Also called element-wise multiplication
	   */
	  public static double[][]  hadamardProduct(final double[][] m1, final double[][] m2)
	  {
		  double[][] newdata=new double[m1.length][m1[0].length];
		  for (int j=0; j<m1.length;j++)
			{
				for (int j2=0; j2<m1[j].length;j2++)
				{
					newdata[j][j2]= m1[j][j2]*m2[j][j2];
				}
			}
		  return newdata;
	  }
	  
	  /**
	   * @param v1 vector 1
	   * @param v2 vector 2
	   * @return  matrix C with Cij = v1i * v2j.
	   */
	  public static double[][]  outerProduct(final double[] v1, final double[] v2)
	  {
		  double[][] newdata=new double[v1.length][v2.length];
		  for (int j=0; j<v1.length;j++)
		  {
				for (int j2=0; j2<v2.length;j2++)
				{
					newdata[j][j2]=v1[j]*v2[j2];
				}
		  }
		  return newdata;
	  }
	  
	  public static void printMartix(final double[][] m)
	  {
		  for(int i=0;i<m.length;i++)
		  {		
			  for (int j=0; j<m[i].length; j++)
			  {
				  System.out.println("["+i+","+j+"]  "+ m[i][j]);
			  }
		  }
	  }
	  
	  public static void printVector(final double[] v)
	  {
		  for(int i=0;i<v.length;i++)
		  {			  
			   System.out.println("["+ i + "] "+ v[i]);
		  }
	  }
	  

	  public static double[] divideVectorbyScalar(final double[] v, final double scalar)
	  {
		  double[] newdata=new double[v.length];
		  for(int i=0;i<v.length;i++)
		  {			  
			   newdata[i]= v[i]/scalar;
		  }
		  return newdata;
	  }
	  
	  /**
	   * 
	   * @param the pdf to be derive the cdf
	   * @return the cdf
	   */
	  public static double[] cdf(final double[] pdf)
	  {
		  double[] newdata=new double[pdf.length];
		  double sum= StatUtils.sum(pdf);
		  double cumSum=0;
		  for(int i=0;i<pdf.length;i++)
		  {	
			  cumSum=cumSum+pdf[i];
			  newdata[i]= cumSum/sum;
		  }
		  return newdata;
	  }
	  

	  public static double[][] createMartix(final double[] v1, final double[] v2)
	  {
		  double[][] newdata=new double[v1.length][2];
		  for(int i=0;i<v1.length;i++)
		  {	  
			  newdata[i][0]=v1[i];
			  newdata[i][1]=v2[i];
		  }
		  return newdata;
	  }
	 	  
	  public static void main(String[] args) 
	  {
		  double[] v1= {1,2};
		  double[] v2={13,5, 30,34};		  
		  MatrixFunctions.printMartix(MatrixFunctions.outerProduct(v1, v2));
	  
		  
	  }	 
}
