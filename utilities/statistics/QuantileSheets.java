package aceges.utilities.statistics;

import java.util.ArrayList;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.util.FastMath;
import aceges.utilities.io.CSVFileReader;
import aceges.utilities.math.ArithmeticSeries;

public final class QuantileSheets 
{

	//x.lambda = 1
	public static double xLambda = 1;
	
	//p.lambda = 10
	public static double pLambda = 10;
	
	//data = NULL
	public static BlockRealMatrix data = null;
	
	//kappa = 0
	public static double kappa = 0;
	
	//cent = c(0.4, 2, 10, 25, 50, 75, 90, 98, 99.6)
	//public static double[] cent = {0.4, 2, 10, 25, 50, 75, 90, 98, 99.6};
	public static double[] cent = {50};
	
	//x.inter = 10
	public static double xInter = 10;
	
	//p.inter = 10
	public static double pInter = 10;
	
	//degree = 3
	public static int degree = 3;
	
	//n.cyc = 100
	public static int nCyc = 100;
	
	//logit = TRUE
	public static boolean logit = true;
	
	//plot = TRUE
	public static boolean plot = true;
	
	//quantileSheets <- function(y, x,x.lambda = 1,p.lambda = 10,data = NULL,kappa = 0,
	//		cent = c(0.4, 2, 10, 25, 50, 75, 90, 98, 99.6),x.inter = 10, p.inter = 10,degree = 3,
    //       														n.cyc = 100,logit = TRUE,plot = TRUE)
	public static BlockRealMatrix quantileSheets(final ArrayRealVector  y, final ArrayRealVector  x){
		
		return quantileSheets(y, x, xLambda, pLambda, data, kappa, cent,
				xInter, pInter, degree, nCyc, logit, plot);
	}
	
	public static BlockRealMatrix quantileSheets(final ArrayRealVector y, final ArrayRealVector x, final double xLambda, 
			final double pLambda, final BlockRealMatrix data, final double kappa, final double[] cent, final double xInter, final double pInter, 
			final int degree, final int nCyc, final boolean logit, final boolean plot){
		
		//if (!is.null(data))
		if(data != null)
		{
			//{attach(data); on.exit(detach(data))}
		}
		
		//m <- length(x)
		int m = x.getDimension();
		
		//xl <- min(x)
		double xl = x.getMinValue();
	
		//xr <- max(x)
		double xr = x.getMaxValue();
	
		//nsegx <- x.inter # this has to be an argument  
		double nsegx = xInter;
	
		//nsegp <- p.inter  # this has to be an argument
		double nsegp = pInter;
	
		//bdeg <- degree
		int bdeg = degree;
	
		//p <- cent/100  #seq(0.05, 0.95, by = 0.1)
		int size = cent.length;
		double[] p = new double[size];
		for(int i=0; i<size; i++)
		{
			p[i] = cent[i]/100;
		}
	
		//n <- length(p)
		int n = p.length;
	
		//Bx <- bbase(x, xl, xr, nsegx, bdeg)
		BlockRealMatrix Bx = bbase(x, xl, xr, nsegx, bdeg);
		
		//if (logit) 
		BlockRealMatrix Bp = null;
		if (logit) 
		{
			//logitp <- log(p/(1-p))
			double[] logitp = new double[n];
			for(int i=0; i<n; i++)
			{
				logitp[i] = FastMath.log(p[i]/(1-p[i]));
			}
			
			//Bp <- bbase(logitp, -20, 20, nsegp, bdeg)
			Bp = bbase(new ArrayRealVector(logitp, false), -20, 20, nsegp, bdeg);
		}
		//else (Bp <- bbase(p, 0, 1, nsegp, bdeg)) 
		else
		{
			//Bp <- bbase(p, 0, 1, nsegp, bdeg)
			Bp = bbase(new ArrayRealVector(p, false), 0, 1, nsegp, bdeg);
		}
		
		//nbx <- ncol(Bx)
		int nbx = Bx.getColumnDimension();
		
		//nbp <- ncol(Bp)
		int nbp = Bp.getColumnDimension();
		
		//Tx <- rowtens(Bx)
		BlockRealMatrix Tx = rowtens(Bx);
		
		//Tp <- rowtens(Bp)
		BlockRealMatrix Tp = rowtens(Bp);
		
		//Dx <- diff(diag(nbx), diff = 2)
		BlockRealMatrix diagNBX = buildDiagMatrix(nbx);
		BlockRealMatrix Dx = diff(diagNBX, 2);

		//Dp <- diff(diag(nbp), diff = 2)
		BlockRealMatrix diagNBP = buildDiagMatrix(nbp);
		BlockRealMatrix Dp = diff(diagNBP, 2);

		//Px <- x.lambda * t(Dx) %*% Dx
		BlockRealMatrix Px = (BlockRealMatrix)Dx.transpose().multiply(Dx).scalarMultiply(xLambda);
				
		//Pp <- p.lambda * t(Dp) %*% Dp
		BlockRealMatrix Pp = (BlockRealMatrix)Dp.transpose().multiply(Dp).scalarMultiply(pLambda);
		
		//P <- kronecker(Pp, diag(nbx)) + kronecker(diag(nbp), Px)
		BlockRealMatrix P = new BlockRealMatrix(kronecker(Pp.getData(), diagNBX.getData())).add
									(new BlockRealMatrix(kronecker(diagNBP.getData(), Px.getData())));
		
		//P <- P + kappa * diag(nrow(P))
		P = P.add(buildDiagMatrix(P.getRowDimension()).scalarMultiply(kappa));

		//Y <- outer(y, rep(1, n))
		BlockRealMatrix Y = outer(y, repeatNbyMtimes(1, n));
		
		//Z <- 0 * Y + mean(Y)
		Mean mean = new Mean();
		double[] tempArr = new double[Y.getRowDimension()];
		for(int i=0; i<tempArr.length; i++)
		{
			tempArr[i] = mean.evaluate(Y.getRow(i));
		}
		BlockRealMatrix Z =(BlockRealMatrix) Y.scalarMultiply(0).scalarAdd(mean.evaluate(tempArr));
		tempArr = null;
		
		//OP <- outer(rep(1, m), p)
		BlockRealMatrix OP = outer(repeatNbyMtimes(1, m), new ArrayRealVector(p, false));
		
		//b <- 0.001
		double b = 0.001;
		
	  	//for (it in 1:n.cyc) 
		BlockRealMatrix R  	  = null;
		BlockRealMatrix W  	  = null;
		BlockRealMatrix Q  	  = null;
		BlockRealMatrix A  	  = null;
		BlockRealMatrix Znew  = null;
		double[][] tempArrArr = null;
		for (int i =0; i<nCyc; i++) 
	  	{
	  		//R <- Y - Z
	  		R = Y.subtract(Z);
			
			//W <- ifelse(R > 0, OP, 1- OP) / sqrt(b + R ^ 2)
	  		ArrayRealVector tempV = (ArrayRealVector)OP.getColumnVector(0);
	  		for(int j=1; j<OP.getColumnDimension(); j++)
	  		{
	  			tempV = (ArrayRealVector)tempV.append(OP.getColumnVector(j));
	  		}
	  		tempV = extend(tempV, R.getColumnDimension()*R.getRowDimension());
	  		int ink = 0;
	  		tempArrArr = new double[R.getRowDimension()][R.getColumnDimension()];
	  		for(int k=0; k<R.getColumnDimension(); k++)
	  		{
	  			for(int j=0; j<R.getRowDimension(); j++)
	  			{
	  				if(R.getEntry(j, k) > 0)
	  				{
	  					tempArrArr[j][k] = tempV.getEntry(ink+j)/FastMath.sqrt(b+R.getEntry(j, k)*R.getEntry(j, k));
	  				}
	  				else
	  				{
	  					tempArrArr[j][k] = (1-tempV.getEntry(ink+j))/FastMath.sqrt(b+R.getEntry(j, k)*R.getEntry(j, k));
	  				}
	  			}
	  			ink = ink + R.getRowDimension();
	  		}
	  		W = new BlockRealMatrix(tempArrArr);
	  		tempV = null;
	  		tempArrArr = null;

			//Q <- t(Tx) %*% W %*% Tp
	  		Q = Tx.transpose().multiply(W).multiply(Tp);
	  		

			//dim(Q) <- c(nbx, nbx, nbp, nbp);
			//Q <- aperm(Q, c(1, 3, 2, 4))
			//dim(Q) <- c(nbx * nbp, nbx * nbp)
			ink = 0;
			int colInk = 0;
			BlockRealMatrix tempM = new BlockRealMatrix(nbx*nbp, nbx*nbp);
			tempArrArr = new double[nbx][nbp];
			
			for(int column=0; column<tempM.getColumnDimension(); column+=(int)FastMath.sqrt(tempM.getColumnDimension())){
				
				for(int row=0; row<tempM.getRowDimension(); row+=(int)FastMath.sqrt(tempM.getRowDimension())){
			
					for(int k=0; k<FastMath.sqrt(tempM.getColumnDimension()); k++)
					{
						for(int j=0; j<FastMath.sqrt(tempM.getRowDimension()); j++)
						{
							tempArrArr[j][k] = Q.getColumnVector(colInk).getEntry(ink+j);
						
						}
						ink = ink + (int)FastMath.sqrt(tempM.getRowDimension());
			
					}
					tempM.setSubMatrix(tempArrArr, row, column);
					tempArrArr = new double[nbx][nbp];
					colInk++;
					ink = 0;
			  	}
			}
			Q = tempM.copy();
			tempM = null;
			tempArrArr = null;
			
			//r <- t(Bx) %*% (Y * W) %*% Bp
	  		tempArrArr = new double[Y.getRowDimension()][Y.getColumnDimension()];
	  		for(int j=0; j<Y.getRowDimension(); j++)
	  		{
	  			for(int k=0; k<Y.getColumnDimension(); k++)
	  			{
	  				tempArrArr[j][k] =  Y.getEntry(j, k)*W.getEntry(j, k);
	  			}
	  		}
	  		
BlockRealMatrix M1 = Bx.transpose();
BlockRealMatrix M2 = M1.multiply(new BlockRealMatrix(tempArrArr));
BlockRealMatrix M3 = M2.multiply(Bp);
	  		
	  		
	  		BlockRealMatrix r =Bx.transpose().multiply(new BlockRealMatrix(tempArrArr)).multiply(Bp);
	  		
	  		tempArrArr = null;
	  		
			//dim(r) <- c(nbx * nbp, 1)
			tempArrArr = new double[r.getRowDimension()*r.getColumnDimension()][1];
			int col = 0;
			int row = 0;
			for(int k=0; k<tempArrArr.length; k++)
			{
				if(row == r.getRowDimension())
				{
					col++;
					row = 0;
				}
				tempArrArr[k][0] =  r.getEntry(row, col);
				row++;
			}
			r = new BlockRealMatrix(tempArrArr);
			
			tempArrArr = null;
BlockRealMatrix	TT = Q.add(P);

		    //A <- solve(Q + P, r)
			DecompositionSolver solver = new SingularValueDecomposition(Q.add(P)).getSolver();
			A = new BlockRealMatrix (solver.solve(r).getData());
			
			//dim(A) <- c(nbx, nbp)
	  		ink = 0;
	  		tempArrArr = new double[nbx][nbp];
	  		for(int k=0; k<nbp; k++)
	  		{
	  			for(int j=0; j<nbx; j++)
	  			{
	  					tempArrArr[j][k] = A.getEntry(ink+j,0);
	  		
	  			}
	  			ink = ink + nbx;
	  		}
	  		A = new BlockRealMatrix(tempArrArr);
	  		tempArrArr = null;
			
			//Znew <- Bx %*% A %*% t(Bp)
			Znew = Bx.multiply(A).multiply(Bp.transpose());
			
		    //dz <- sum(abs(Z - Znew))
			double dz = 0;
	  		for(int j=0; j<Z.getRowDimension(); j++)
	  		{
	  			for(int k=0; k<Z.getColumnDimension(); k++)
	  			{
	  				dz = dz + FastMath.abs(Z.getEntry(j, k)- Znew.getEntry(j, k));
	  			}
	  		}
			
		    //if (dz < 1e-5) break
	  		if(dz < 1e-5)
	  		{
	  			break;
	  		}
	  		
		    //Z <- Znew
	  		Z = Znew.copy();
	  	}
		//xg <- seq(xl, xr, length = 100)
		ArrayRealVector xg = new ArrayRealVector(ArithmeticSeries.getSeries(xl, xr, 100),false);
		
		//Bg <- bbase(xg, xl, xr, nsegx, bdeg)
		BlockRealMatrix Bg = bbase(xg, xl, xr, nsegx, bdeg);
		
		//Zg <- Bg %*% A %*% t(Bp)
		BlockRealMatrix Zg = Bg.multiply(A).multiply(Bp.transpose());
		
		if (plot)
		{
		    //plot(x, y, pch = 15, cex = 0.5, col = gray(0.7))
		    //matlines(xg, Zg, type = 'l', lty = 1, lwd = 2)
		 }
		
		//colnames(Zg) <- as.character(cent)
/*		
		//per <- rep(0, length(cent))
		double[] per = new double[cent.length];
		
		//for (i in 1:length(cent))
		for(int i=0; i<cent.length; i++)
		{
		    //ff <- approxfun(xg,Zg[,i])
			LinearInterpolator nterpolator = new LinearInterpolator(); 
			PolynomialSplineFunction ff = nterpolator.interpolate(xg.toArray(), Zg.getColumn(i));
			
		    //ll <- ff(x)
			double[] ll = ff.getKnots();
			
		    //per[i] <- (1-sum(y>ll)/length(y))*100
			int sum = 0;
			for(int j=0; j<cent.length; j++)
			{
				if(y.getEntry(j)>ll[j])
				{
					sum++;
				}
			}
			per[i] = (1-sum/y.getDimension())*100;
			
		   // cat("% of cases below ", cent[i],"centile is ", per[i], "\n" )
			System.out.println("% of cases below  " +cent[i]+"  centile is  "+ per[i]);
		}
		
		//out <- list(y=y, x=xg, fitted.values=Zg, cent=cent, per=per)  
		//class(out) <- "qSheets"
	*/	
		return Zg;
	}
	
//------------------------------------------------------------------------------------------------
	/**
	 * Extends vector sigma by copying its values consequently until it reaches dimension maxDim 
	 * @param sigma - vector of sigma values
	 * @param sigmaDim -  dimension of vector sigma
	 * @param maxDim - dimension of the larget vector of x, mu or sigma
	 * @return extended vector sigma
	 */
	  private static ArrayRealVector extend(final ArrayRealVector v, final int maxDim)
	  {
		int n=0;
		int vDim = v.getDimension();
		double [] sigmaArr = v.toArray();
		double [] out = new double[(int)maxDim];		
		for (int i=0; i<(int)maxDim; i++){				
			out[i] = sigmaArr[n];
			n++;
			if (n==vDim)
			{
				n=n-vDim;
			}				
		}			
		return new ArrayRealVector(out,false);
	  }
//------------------------------------------------------------------------------------------------	
	private static BlockRealMatrix buildDiagMatrix(final int size){
		double[] tempArr = new double[size]; 
		for (int i=0; i<size; i++)
		{
			tempArr[i] = 1;
		}
		return new BlockRealMatrix(MatrixUtils.createRealDiagonalMatrix(tempArr).getData());
	}

//------------------------------------------------------------------------------------------------	
	private static ArrayRealVector repeatNbyMtimes(final double n, final int m){
		ArrayRealVector tempV = new ArrayRealVector(m);
		tempV.set(n);
		return tempV;
	}
	
//-------------------------------------------------------------------------------------------------	   
	/**
	 * @param v1 vector 1
	 * @param v2 vector 2
	 * @return  matrix C with Cij = v1i * v2j.
	 */
	//tpower <- function(x, t, p)
	private static BlockRealMatrix  tpower( final ArrayRealVector x, final ArrayRealVector t, final int p){
		  double[][] newdata=new double[x.getDimension()][t.getDimension()];
		  //(x - t) ^ p * (x > t)
		  for (int j=0; j<x.getDimension();j++)
		  {
				for (int j2=0; j2<t.getDimension();j2++)
				{
					if (x.getEntry(j) > t.getEntry(j2))
					{
						newdata[j][j2]=FastMath.pow((x.getEntry(j)-t.getEntry(j2)), p);
					}
					else
					{
						newdata[j][j2] = 0d;
					}
				}
		  }
		  return new BlockRealMatrix(newdata);
	}
	
//-------------------------------------------------------------------------------------------------	   
	/**
	 * @param v1 vector 1
	 * @param v2 vector 2
	 * @return  matrix C with Cij = v1i * v2j.
	 */
	//tpower <- function(x, t, p)
	private static BlockRealMatrix  outer(final ArrayRealVector x, final ArrayRealVector t, final int p){
		  double[][] newdata=new double[x.getDimension()][t.getDimension()];
		  //(x - t) ^ p * (x > t)
		  for (int j=0; j<x.getDimension();j++)
		  {
				for (int j2=0; j2<t.getDimension();j2++)
				{
						newdata[j][j2]=x.getEntry(j)*t.getEntry(j2);
			
				}
		  }
		  return new BlockRealMatrix(newdata);
	}
		
//------------------------------------------------------------------------------------------------
	//tpower <- function(x, t, p=1)
	private static BlockRealMatrix  tpower(final ArrayRealVector x, final ArrayRealVector t){
		int p = 1;
		return tpower(x,t, p);
	}

//------------------------------------------------------------------------------------------------
	//tpower <- function(x, t, p=1)
	private static BlockRealMatrix  outer( final ArrayRealVector x, final ArrayRealVector t){
		int p = 1;
		return outer(x,t, p);
	}

//-------------------------------------------------------------------------------------------------	    
	//bbase <- function(x, xl = min(x), xr = max(x), ndx = 10, deg = 3)
	private static BlockRealMatrix bbase(final ArrayRealVector x,final double xl, final double xr, final double ndx, final int deg){
	//public BlockRealMatrix formX(ArrayRealVector x, double xmin, double xmax, double inter, int degree,

		//dx <- (xr - xl) / ndx
		double dx = (xr - xl) / ndx;
		
		//kts <-   seq(xl - deg * dx, xr + deg * dx, by = dx)
		ArrayRealVector kts = new ArrayRealVector(ArithmeticSeries.getSeries(xl-deg*dx, xr+deg*dx, dx),false);
		
		//P <- outer(x, kts, FUN = tpower, deg)
		BlockRealMatrix P = tpower(x, kts, deg);
		
		//D <- diff(diag(n), diff = deg + 1) / (gamma(deg + 1) * dx ^ deg)
		BlockRealMatrix tempM = diff(buildDiagMatrix(P.getColumnDimension()), deg+1);
		double[][] tempArrArr = new double[tempM.getRowDimension()][tempM.getColumnDimension()];
		for(int i=0;i<tempArrArr.length; i++)
		{
			 for(int j=0;j<tempArrArr[i].length;j++)
			 {
				 tempArrArr[i][j]= tempM.getEntry(i, j)/((FastMath.exp(Gamma.logGamma(deg+1)))*FastMath.pow(dx, deg));
			 }
		}
		tempM = new BlockRealMatrix(tempArrArr);
		 
		 //B <- (-1) ^ (deg + 1) * P %*% t(D)
		 return  (BlockRealMatrix) P.multiply(tempM.transpose()).scalarMultiply(FastMath.pow(-1, deg+1));
		
	}
	
//-------------------------------------------------------------------------------------------------		
	private static BlockRealMatrix bbase(final ArrayRealVector x){
		double xl = x.getMinValue();
		double xr = x.getMaxValue();
		double ndx = 10;
		int deg = 3;
		return bbase( x,  xl,  xr,  ndx,  deg);
	}

//-------------------------------------------------------------------------------------------------   
	 /**
	 * if the length of the return matrix is zero, then the function was not completed properly. 
	 * Check the 'diff' operator. 
	 */
	  private static BlockRealMatrix diff(final BlockRealMatrix data, final int diff)
	  {
		 BlockRealMatrix newdata = new BlockRealMatrix(data.getRowDimension()-1, data.getColumnDimension());
		 for (int i=0; i<diff; i++)
		 {
			  if (i==0)
			  {
			     newdata = getDiff(data);
			  }
			  else
			  {
				  newdata = getDiff(newdata); 
			  }
			  
		 }		 		 
		 return newdata;		 
	  }

//-------------------------------------------------------------------------------------------------   
	  /**
	   * if the length of the return matrix is zero, then the diff was not completed properly. 
	   */
	  private static BlockRealMatrix getDiff(final BlockRealMatrix data)
	  {
		 double[][] newdata= new double[data.getRowDimension()-1][data.getColumnDimension()];
		 
		 for(int i=0;i<newdata.length; i++)
		 {
			 for(int j=0;j<newdata[i].length;j++)
			 {
				 newdata[i][j]= data.getEntry(i+1,j)-data.getEntry(i,j);
			 }
		 }
		 return new BlockRealMatrix(newdata);		 
	  }

//------------------------------------------------------------------------------------------------- 
	  private static double[][] kronecker(final double[][] A, final double[][] B) {
	    final int m = A.length;
	    final int n = A[0].length;
	    final int p = B.length;
	    final int q = B[0].length;
	
	    double[][] out = new double[m * p][n * q];
	    return out = product(A, B, out);
	}
	
	private static double[][] product(final double[][] A, final double[][] B, final double[][] out) {
	    final int m = A.length;
	    final int n = A[0].length;
	    final int p = B.length;
	    final int q = B[0].length;
	
	    if (out == null || out.length != m * p || out[0].length != n * q) {
	        throw new RuntimeException("Wrong dimensions in Kronecker product");
	    }
	
	    for (int i = 0; i < m; i++) {
	        final int iOffset = i * p;
	        for (int j = 0; j < n; j++) {
	            final int jOffset = j * q;
	            final double aij = A[i][j];
	
	            for (int k = 0; k < p; k++) {
	                for (int l = 0; l < q; l++) {
	                    out[iOffset + k][jOffset + l] = aij * B[k][l];
	                }
	            }
	
	        }
	    }
	    return out;
	}
	
//-------------------------------------------------------------------------------------------------   
	  private static BlockRealMatrix rowtens(final BlockRealMatrix X)
	  {
		    //one = matrix(1, nrow = 1, ncol = ncol(X))
		  	double[][] one = new double[1][X.getColumnDimension()];
		    for (int i = 0; i < X.getColumnDimension(); i++) {
		    	one[0][i] = 1;
		    }
		    
		    //kronecker(X, one) * kronecker(one, X)
		    BlockRealMatrix tempM1 = new BlockRealMatrix(kronecker(X.getData(), one));
		    BlockRealMatrix tempM2 = new BlockRealMatrix(kronecker(one, X.getData()));

	  		one = new double[tempM1.getRowDimension()][tempM1.getColumnDimension()];
	  		for(int j=0; j<tempM1.getRowDimension(); j++)
	  		{
	  			for(int k=0; k<tempM1.getColumnDimension(); k++)
	  			{
	  				one[j][k] =  tempM1.getEntry(j, k)*tempM2.getEntry(j, k);
	  			}
	  		}	  		
	  		return new BlockRealMatrix(one);
	  }
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
   public static void main(String[] args)
   {
		
	//ArrayRealVector tempV1 = new ArrayRealVector(2);
	ArrayRealVector tempV1 = new ArrayRealVector(6);
	//ArrayRealVector tempV2 = new ArrayRealVector(3);
	ArrayRealVector tempV3 = new ArrayRealVector(2);
	tempV1.setEntry(0, 1);
	tempV1.setEntry(1, 2);
	tempV1.setEntry(2, 0.00091);
	tempV1.setEntry(3, 34);
	tempV1.setEntry(4, -134534);
	tempV1.setEntry(5, 66678987);
	//tempV2.setEntry(0, -0.0056);
	//tempV2.setEntry(1, 33);
	//tempV2.setEntry(2, 4);

	tempV3.setEntry(0, 1000101550);
	tempV3.setEntry(1, 1000101551);
	//BlockRealMatrix tempM = quant.bbase(tempV1);
	
	//double[][] tempD1 = {{11,22},{33,44}};
	//double[][] tempD2 = {{2,3},{4,5}};
	//double[][] tempD3 = {{3,3,3},{3,3,3},{3,3,3}};
	//double[][] tempOut = quant.kronecker(tempD3,tempD1);
	

	 String fileName = "/Users/vlasiosvoudouris/Documents/research/programming/aceges/src/aceges/utilities/statistics/in2.csv";	
	 CSVFileReader readData = new CSVFileReader(fileName);
	 readData.readFile();
	 ArrayList<String> data = readData.storeValues;
	 
	 
	 ArrayRealVector y= new ArrayRealVector(data.size());
	 //ArrayRealVector yy= new ArrayRealVector(data.size());
	 BlockRealMatrix muX = new BlockRealMatrix(data.size(), 1);
	 BlockRealMatrix sigmaX = new BlockRealMatrix(data.size(), 1);
	 BlockRealMatrix nuX = new BlockRealMatrix(data.size(), 1);
	 BlockRealMatrix tauX = new BlockRealMatrix(data.size(), 1); 
	 ArrayRealVector w = new ArrayRealVector(data.size());
	
	 BlockRealMatrix smoothMU = new BlockRealMatrix(data.size(), 1);
	 BlockRealMatrix smoothSIGMA = new BlockRealMatrix(data.size(), 1);
	 BlockRealMatrix smoothNU = new BlockRealMatrix(data.size(), 1);
	 BlockRealMatrix smoothTAU = new BlockRealMatrix(data.size(), 1); 
	 
		 
		 for(int i=0;i<data.size();i++)
		 {
			String[] line = data.get(i).split(",");
			y.setEntry(i,  Double.parseDouble(line[0]));
			//yy.setEntry(i,  Double.parseDouble(line[1]));
			muX.setEntry(i, 0, Double.parseDouble(line[1]));
			//muX.setEntry(i, 1, Double.parseDouble(line[1]));
			smoothMU.setEntry(i, 0, Double.parseDouble(line[1]));
			sigmaX.setEntry(i, 0, Double.parseDouble(line[1]));
			//sigmaX.setEntry(i, 1, Double.parseDouble(line[2]));
			smoothSIGMA.setEntry(i, 0, Double.parseDouble(line[1]));
			nuX.setEntry(i, 0, Double.parseDouble(line[1]));
			smoothNU.setEntry(i, 0, Double.parseDouble(line[1]));
			tauX.setEntry(i, 0, Double.parseDouble(line[1]));
			smoothTAU.setEntry(i, 0, Double.parseDouble(line[1]));
		 }	
	 BlockRealMatrix tempM = QuantileSheets.quantileSheets(y,(ArrayRealVector)muX.getColumnVector(0));
	 //BlockRealMatrix tempM = quant.bbase( tempV3);
	 //BlockRealMatrix tempM = quant.rowtens(new BlockRealMatrix(tempD3));
	 System.out.println(tempM);
	
   }
	
}
