package aceges.utilities.statistics;

import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.FastMath;
import aceges.utilities.math.ArithmeticSeries;
import aceges.utilities.math.MatrixFunctions;

public final class DensityEstimation 
{	 
	private static double lambda;//penalty for smoothness
	private static double kappa; //penaty for loc-concaveness
	private static double[] x; //the nots (to be used for plot(x,pdf))
	
	/**
	 * default values for smootheness (lambda) 10 and local concaveness (kappa) 100. 
	 */
	public DensityEstimation()
	{		
		this(10d, 100d);
	}
	/**
	 * 
	 * @param lambda (penalty for smoothness)
	 */
	public DensityEstimation (double lambda)
	{
		this(lambda, 100d);
	}
	
	/**
	 * 
	 * @param lambda (penalty for smoothness)
	 * @param kappa (penaty for loc-concaveness)
	 */
	public DensityEstimation (double lambda, double kappa)
	{
		this.lambda= lambda;
		this.kappa=kappa;
	}
		
	/**
	 * This is an java interprentation of Paul Eiler's function. 
	 * @param prob
	 * @param quant
	 * @param mean, if does not exists, then set to 0
	 * @return the pdf approximating the distribution of the quantiles
	 */
	public static double[] quantileSmoothedEstimation(
			final double[] prob, final double[] quant, final double mean)
	{
		double mu=mean;
		RealVector probs = MatrixUtils.createRealVector(prob);
		RealVector quants = MatrixUtils.createRealVector(quant);	
		double xmax =quants.toArray()[quants.toArray().length-1]+0.3;
		double xmin =quants.toArray()[0]-0.3;
		int nx=100;//number of knots
		x= ArithmeticSeries.getSeries(xmin,xmax,nx);		
		int nq=quants.toArray().length;
		int na=nq+2;
		RealMatrix A=MatrixUtils.createRealMatrix(na,nx);
		double tempProb;
		double tempQuant;
		for (int i=0; i<nq; i++)
		{
			tempProb = probs.toArray()[i];
			tempQuant = quants.toArray()[i];
			for (int j=0; j<x.length;j++)
			{
				if(x[j]>tempQuant)
				{
					A.setEntry(i,j, tempProb);
					
				}
				else
				{
					A.setEntry(i,j, -(1-tempProb));
				}	
			}			
		}		
		//Specify the mu - what if mu is zero?
		if (mu>0)
		{
			int wmu=1;
			double[] am = new double[x.length];
			for (int j=0; j<x.length;j++)
			{
				am[j] = wmu*FastMath.pow(10d, x[j])*(FastMath.pow(10d, x[j])-mu);	
			}
			double amMax=StatUtils.max(am);
			for (int j=0; j<x.length;j++)
			{
				A.setEntry(nq, j, wmu*am[j]/amMax);//note java start from 0 rather than 1
			}			
		}
		
		//Condition to sum to 1
		int w1=1000;
		na=nq+2;
		A.setRow(na-1,  ArithmeticSeries.getRep(w1,nx));//note java start from 0 rather than 1
		double[] u=ArithmeticSeries.getRep(0,na);
		u[na-1]=w1;//note java start from 0 rather than 1
		
		//Prepare penalty for smoothness of log(g)
		RealMatrix diagMartix= MatrixUtils.createRealDiagonalMatrix(ArithmeticSeries.getRep(1,nx));
		RealMatrix D3=  MatrixUtils.createRealMatrix(MatrixFunctions.getDiff(diagMartix.getData(), 3)); 
		RealMatrix P3 =D3.preMultiply(D3.transpose()).scalarMultiply(lambda);
		
		//Prepare penaty for loc-concaveness
		RealMatrix diagMartix2= MatrixUtils.createRealDiagonalMatrix(ArithmeticSeries.getRep(1,nx));
		RealMatrix D2= MatrixUtils.createRealMatrix(MatrixFunctions.getDiff(diagMartix2.getData(), 2)); 
		
		//Small ridge penalty for stability
		RealMatrix P0 = MatrixUtils.createRealDiagonalMatrix(ArithmeticSeries.getRep(1,nx)).scalarMultiply(1.0E-7);		
		
		// Starting values (need positive number to compute logs)
		double[] z= ArithmeticSeries.getRep(-FastMath.log(nx),nx);
		
		//iterations
		double[] v= ArithmeticSeries.getRep(0,nx-2);
		double[] g= new double[z.length];
		RealVector r;
		RealVector u1 = MatrixUtils.createRealVector(u);
		double[] outerV=ArithmeticSeries.getRep(1,na);
		RealMatrix B=null;
		RealMatrix Q=null;
		RealMatrix P2=null;
		RealMatrix coefficients=null;
		RealVector constants=null;
		DecompositionSolver solver=null;
		RealVector solution=null;
		double dz=0d;		
		for (int i=0; i<150; i++) // i<1 to be changed to 150
		{
			for (int j=0; j<z.length; j++)
			{
				g[j]= FastMath.exp(z[j]);
			}	
			
			r=u1.subtract(MatrixUtils.createRealVector(A.operate(g)));// u - A %*% g 
			B=MatrixUtils.createRealMatrix
			(MatrixFunctions.hadamardProduct(
			  A.getData(),MatrixFunctions.outerProduct(outerV,g))
			);
			
			Q=B.transpose().multiply(B);
			//P2 = kappa * t(D2) %*% diag.spam(v) %*% D2
			P2= D2.transpose().scalarMultiply(kappa).multiply(
					MatrixUtils.createRealDiagonalMatrix(v)).multiply(D2);	
			coefficients =Q.add(P0).add(P2).add(P3);	
			solver = new QRDecomposition(coefficients).getSolver();
			constants =B.transpose().operate(r).add(MatrixUtils.createRealVector(Q.operate(z)));
			solution = solver.solve(constants);//znew
			dz=MatrixFunctions.maxABS(z,solution.toArray());
			z= solution.toArray();
			//MatrixFunctions.printVector(z);
			double[] temp= MatrixFunctions.getDiff(z,2);	
			for (int j=0; j<temp.length;j++)
			{	
				if (temp[j]>0)
				{
					v[j]=1;
				}
				else
				{
					v[j]=0;
				}
			 }
			if(dz<1.0E-6) {break;}
		}//end of iteration loop
		//System.out.println(dz);
		double dx=x[2]-x[1];
		//MatrixFunctions.printVector(MatrixFunctions.cdf(MatrixFunctions.divideVectorbyScalar(g,dx)));
		return MatrixFunctions.divideVectorbyScalar(g,dx);
	 }
	
	public static void main(String[] args)
    {
		double[] x={0.05, 0.5, 0.95};
		double i1=FastMath.log10(14573.4);
		double i2=FastMath.log10(114093.8);
		double i3=FastMath.log10(417211.05);
		double mu=179970.3;
		double[] y=new double[3];
		//System.out.println(i1+", " + i2+", "+i3);
		y[0]=i1;
		y[1]=i2;
		y[2]=i3;
	
		long t=System.currentTimeMillis();
		new DensityEstimation().quantileSmoothedEstimation(x,y,mu);	
		System.out.println(System.currentTimeMillis()-t);
    }

}
