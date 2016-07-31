package aceges.utilities.statistics;

import aceges.utilities.maths.Matrix;

public class Regression {

	public Matrix betas;

	public Matrix x;

	public Matrix y;

	boolean intercept;


	public Regression()
	{

	}



	    public Matrix Regress(Matrix x, Matrix y, boolean intercept)

	    {

	    	this.intercept=intercept;

	        betas = new Matrix();

	        this.x = x;

	        this.y = y;

	        if(!intercept)

	        {

	            betas = x.Transpose().Mult(x).Inverse().Mult(x.Transpose()).Mult(y);

	        } else

	        {

	            int n = x.n;

	            int m = x.m;

	            Matrix design = new Matrix(n, m + 1);

	            for(int i = 0; i < n; i++)

	                design.setValue(i, 0, 1.0D);



	            for(int i = 0; i < n; i++)

	            {

	                for(int j = 0; j < m; j++)

	                    design.setValue(i, j + 1, x.getValue(i, j));



	            }



	            betas = design.Transpose().Mult(design).Inverse().Mult(design.Transpose()).Mult(y);

	            this.x =new Matrix(design.myData);

	        }

	        return betas;

	    }



	    public Matrix getPredicteds()

	    {

	        Matrix temp= x.Mult(betas);

	        return temp;

	    }



	    public Matrix getResiduals()

	    {

	        Matrix temp = y.Diff(x.Mult(betas));

	        return temp;

	    }

	    

	    public double getEstimatedVariance(){

	    	Matrix temp=new Matrix(x.n,1);

	    	temp.myData=getResiduals().myData;

	    	double temp1=0;

	    	for (int i=0;i<x.n;i++){

	    		temp1+=Math.pow(temp.getValue(i,0),2.0);

	    	}

	    	return (temp1/(x.n-x.m));

	    }

	    

	    public double getEstimatedStd () {

	    	return (Math.sqrt(this.getEstimatedVariance()));

	    }

	    

	    public Matrix getCovarianceMatrixOfParameters(){

	    	Matrix temp=(x.Transpose().Mult(x)).Inverse();

	    	double std=getEstimatedVariance();

	    	for (int i=0;i<x.m;i++){

	    		temp.myData[i][i]=temp.myData[i][i]*std;

	    	}

	    	return (temp);

	    }

	    

	    public Matrix getStandartErrorsOfParameters() {

	    	Matrix temp=getCovarianceMatrixOfParameters();

	    	for (int i=0;i<x.m;i++){

	    		temp.myData[i][i]=Math.sqrt(temp.myData[i][i]);

	    	}

	    	return (temp);

	    }



	    public Matrix getTValues (){

	    	Matrix temp=new Matrix (x.m,1);

	    	Matrix ster=getStandartErrorsOfParameters();

	    	for (int i=0;i<x.m;i++){

	    		temp.setValue(i,0,betas.getValue(i,0)/ster.getValue(i,i));

	    	}

	    	return (temp);

	    }

	   

	    public double getRSquare(){

	    	Matrix temp=new Matrix(x.n,0);

	    	temp.myData=this.getResiduals().myData;

	    	double total=0;

	    	double restotal=0;

	    	for (int i=0;i<x.n;i++){

	    		total+=Math.pow(y.myData[i][0],2.0);

	    		restotal+=Math.pow(temp.myData[i][0],2.0);

	    	}

	    	return (1-(restotal/total));

	    }


}
