/**
 * 
 */
package classifier;

import java.util.Vector;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.Math;


import util.CircularBuffer;


import datastructure.LabelledTimeSeriesClassificationData;
import datastructure.Matrix;

/**
 * @author thayumaanavan
 *
 */

class IndexDist
{
	int x,y;
	double dist;
	public IndexDist()
	{
		x=y=0;
		dist=0;
	}
}

class DTWTemplate
{
	int classLabel,avgTempLength;
	Matrix timeSeries;
	double trainingMu,trainingSigma,threshold;
	
	
	
	public DTWTemplate(){
        classLabel = 0;
		trainingMu = 0.0;
		trainingSigma = 0.0;
		threshold=0.0;
		avgTempLength=0;
		timeSeries=new Matrix();
	}
}

public class DTW {

	private static final double NAN = Double.POSITIVE_INFINITY;
	private static final double DEFAULT_NULL_LIKELIHOOD_VALUE = 0;
	Vector< DTWTemplate > templatesBuffer;
	int numTemplates,avgTempLength;
	int numClasses;
    int predictedClassLabel;
    double bestDistance;
    Vector< Double > classLikelihoods;
    Vector< Double > classDistances;
    Vector< Integer > classLabels;
    Boolean trained;
    CircularBuffer<Vector<Double>>continuousInputDataBuffer;
    int numFeatures;
    double radius;
    double maxLikelihood;
    
    
	/**
	 * 
	 */
	public DTW() {
		// TODO Auto-generated constructor stub
		numTemplates=0;
		avgTempLength=0;
		radius=0.2;
		templatesBuffer=new Vector<DTWTemplate>();
		classLikelihoods=new Vector<Double>();
		classDistances=new Vector<Double>();
		classLabels=new Vector<Integer>();
		
	}
	
	public int getNumTemplates()
	{
		return numTemplates;
	}
	
	public int getNumClasses()
	{
		return this.numClasses;
	}
	
	public int getPredictedClassLabel()
	{
		if( trained ) return predictedClassLabel; 
	    return -1; 
	}

	public int getClassLabel(int i)
	{
		return classLabels.get(i);
	}
	
	public Boolean train(LabelledTimeSeriesClassificationData trainingData)
	{
	    _train(trainingData);
	    
		return trained;
	}

	/**
	 * @param trainingData
	 */
	private Boolean _train(LabelledTimeSeriesClassificationData labelledTrainingData) {
		// TODO Auto-generated method stub
		int bestIndex=0;
		//worstIndex=0;
		
		//templatesBuffer.clear();
		//classLabels.clear();
		trained = false;
	   // continuousInputDataBuffer.clear();
		
		if( labelledTrainingData.getNumSamples() == 0 )
		{
			System.out.println("No samples in training data");
			return false;
		}
		
		numClasses = labelledTrainingData.getNumClasses();
		numTemplates = labelledTrainingData.getNumClasses();
	    numFeatures = labelledTrainingData.getNumDim();
		templatesBuffer.setSize(numTemplates);
	    classLabels.setSize(numClasses);
		avgTempLength = 0;
		
		LabelledTimeSeriesClassificationData trainingData=new LabelledTimeSeriesClassificationData( labelledTrainingData );

		
		for(int k=0;k<numTemplates;k++)
		{
			int classLabel=trainingData.getClassTracker().get(k).getClassLabel();
                        
			LabelledTimeSeriesClassificationData classData=new LabelledTimeSeriesClassificationData(trainingData.getNumDim(),null,null);;
			for(int x=0; x<trainingData.getNumSamples(); x++){
		        if( trainingData.getDataVector().get(x).getClassLabel() == classLabel ){
		            classData.addSample( classLabel, trainingData.getDataVector().get(x).getData() );
		        }
		    }
			int numExamples=classData.getNumSamples();
			//System.out.println("hello"+numExamples);
			bestIndex=0;
			//worstIndex=0;
			templatesBuffer.set(k, new DTWTemplate());
			templatesBuffer.get(k).classLabel=classLabel;
			classLabels.set(k,classLabel);
			
			if(numExamples<1)
			{
				System.out.println("Number of example<1");
				return false;
			}
			if(numExamples==1)
			{
				bestIndex=0;
				//worstIndex=0;
				templatesBuffer.get(k).threshold=0.0;
				
			}
			else
			{
				bestIndex=train_NDDTW(classData,templatesBuffer.get(k),bestIndex);
				
			}
			//System.out.println("Best Index train:"+bestIndex);
			templatesBuffer.get(k).timeSeries=classData.getDataVector().get(bestIndex).getData();
			
			avgTempLength+=templatesBuffer.get(k).avgTempLength;
			
		}
		
		trained=true;
		avgTempLength=(int)avgTempLength/numTemplates;
		//continuousInputDataBuffer.clear();
		
		predictedClassLabel = 0;
		
		return trained;
	}

	/**
	 * @param classData
	 * @param dtwTemplate
	 * @param bestIndex
	 * @return
	 */
	private int train_NDDTW(
			LabelledTimeSeriesClassificationData trainingData,
			DTWTemplate dtwTemplate, int bestIndex) {
		// TODO Auto-generated method stub
		
		int numExamples=trainingData.getNumSamples();
		Vector<Double> results=new Vector<Double>(numExamples);
		for(int i=0;i<numExamples;i++)
		{
			results.add(i, 0.0);
			//results.set(i,new Double());
			//results.set(i, 0.0);
		}
		Matrix distanceResults=new Matrix(numExamples,numExamples);
		dtwTemplate.avgTempLength=0;
		for(int m=0;m<numExamples;m++)
		{
			Matrix templateA,templateB;
			dtwTemplate.avgTempLength+=trainingData.getDataVector().get(m).getLength();
			
			templateA=trainingData.getDataVector().get(m).getData();
                        
			for(int n=0;n<numExamples;n++)
			{
				if(m!=n)
				{
					templateB=trainingData.getDataVector().get(n).getData();
					
					//double dist=0.0;
					double dist=computeDistance(templateA,templateB);
					
					distanceResults.dataptr[m][n]=dist;
					results.set(m, dist+results.get(m));
				}
				else
					distanceResults.dataptr[m][n]=0;
			}
		}
		
		for(int m=0;m<numExamples;m++)
			results.set(m, results.get(m)/(numExamples-1));
		
		bestIndex=0;
		double bestAverage=results.get(0);
		for(int m=1;m<numExamples;m++)
		{
			//System.out.println("results"+m+" "+results.get(m));
			if(results.get(m)<bestAverage)
			{
				bestAverage =results.get(m);
				bestIndex=m;
			}
			
		}
		//System.out.println("Best Index:"+bestIndex);
		
		if (numExamples>2)
		{
			
			
            dtwTemplate.trainingMu=results.get(bestIndex);
			dtwTemplate.trainingSigma=0.0;
			
			for(int n=0;n<numExamples;n++)
			{
				if(n!=bestIndex)
				{
					dtwTemplate.trainingSigma +=Math.pow(distanceResults.dataptr[bestIndex][n]-results.get(bestIndex),2);
					
				}
			}
			dtwTemplate.trainingSigma=Math.sqrt(dtwTemplate.trainingSigma/(numExamples-2));
			//System.out.println(dtwTemplate.trainingSigma);
			dtwTemplate.threshold=dtwTemplate.trainingMu+(dtwTemplate.trainingSigma/**nullRejectionCoeff*/);
			
		}
		else
		{
			dtwTemplate.trainingMu=dtwTemplate.trainingSigma=0.0;
			
		}
		
		dtwTemplate.avgTempLength=(int) (dtwTemplate.avgTempLength/(double)numExamples);
		
		return bestIndex;
	}
	
	public Boolean predict(Matrix inputTimeSeries)
	{
		if(!trained)
		{
			System.out.println("not trained");
			return false;
		}
		Boolean debug=false;
		
		if(classLikelihoods.size()!=numTemplates)
			classLikelihoods.setSize(numTemplates);
		if(classDistances.size()!=numTemplates)
			classDistances.setSize(numTemplates);
		
		predictedClassLabel=0;
		maxLikelihood=DEFAULT_NULL_LIKELIHOOD_VALUE;
		for(int k=0;k<classLikelihoods.size();k++)
		{
			classLikelihoods.set(k, 0.0);
			classDistances.set(k, DEFAULT_NULL_LIKELIHOOD_VALUE);	
		}
		
		if(numFeatures!=inputTimeSeries.getNumCols())
		{
			return false;
		}
		
		double sum=0;
		for(int k=0;k<numTemplates;k++)
		{
			classDistances.set(k, computeDistance(templatesBuffer.get(k).timeSeries,inputTimeSeries));
			classLikelihoods.set(k,classDistances.get(k));
			//System.out.println("classDis"+classDistances.get(k)+"classLike"+classLikelihoods.get(k)+" "+k);
			sum +=classLikelihoods.get(k);
			
		}
		
		if(debug)
		{}
			
		Boolean sumIsZero=false;
		if(sum==0) sumIsZero=true;
		
		int closestTemplateIndex=0;
		bestDistance=classDistances.get(0);
		
		for(int k=1;k<numTemplates;k++)
		{
			if(classDistances.get(k)<bestDistance)
			{
				bestDistance=classDistances.get(k);
				closestTemplateIndex=k;
				//System.out.println("bestdistance:"+k+" "+bestDistance);
			}
		}
		
		double sum2 = 0;
	    for(int k=0; k<numTemplates; k++){
	        classLikelihoods.set(k , 1.0 - (classLikelihoods.get(k)/sum));
	        sum2 += classLikelihoods.get(k);
	    }

		
		maxLikelihood = 0;
		int maxLikelihoodIndex=0;
		if(!sumIsZero)
		{
			classLikelihoods.set(0,classLikelihoods.get(0)/sum2);
			maxLikelihood=classLikelihoods.get(0);
			for(int k=1;k<numTemplates;k++)
			{
				classLikelihoods.set(k,classLikelihoods.get(k)/sum2);
				if(classLikelihoods.get(k)>maxLikelihood)
				{
					maxLikelihood=classLikelihoods.get(k);
					maxLikelihoodIndex=k;
					//System.out.println("maxLikelihood:"+k+" "+maxLikelihood);
				}
			}
		}
		//for(int i=1;i<numTemplates;i++)
		//{
			//System.out.println("maxLikelihood:"+maxLikelihood);
			//System.out.println("closestLikelihood:"+closestTemplateIndex);
			
			
		//}
		if(debug)
		{
			
		}
		
		/*if(useNullRejection)
		{
			
		}*/
		//if(maxLikelihood>=0.99)
		//{
		//	predictedClassLabel = templatesBuffer.get( maxLikelihoodIndex ).classLabel;
		//}
		//if( bestDistance <= templatesBuffer.get(closestTemplateIndex).threshold )
			predictedClassLabel = templatesBuffer.get(closestTemplateIndex).classLabel;
		//else predictedClassLabel = 0;
		

      // if( maxLikelihood >= 0.99 ) predictedClassLabel = templatesBuffer.get( maxLikelihoodIndex ).classLabel;
       // else predictedClassLabel = 0;
		

       /* if( bestDistance <= templatesBuffer.get(closestTemplateIndex ).threshold && maxLikelihood >= 0.99 )
            predictedClassLabel = templatesBuffer.get(closestTemplateIndex ).classLabel;
        else predictedClassLabel = 0;*/


		//predictedClassLabel=templatesBuffer.get(closestTemplateIndex).classLabel;
		
		/*Vector<Integer> data = new Vector<Integer>();
		data.add(0,	predictedClassLabel);
		
		if(!this.cl.process(data))
			System.out.println("post process failed");
		data=cl.getProcessedData();
		System.out.println("class Label before post procesing:"+predictedClassLabel);
		if(data.get(0)!= -1)
			predictedClassLabel=data.get(0);
		System.out.println("class Label after post procesing:"+predictedClassLabel);
			*/
		return true;
	}
	
	/*public Boolean predict(Vector<Double> inputVector)
	{
		if(!trained)
			return false;
		
		predictedClassLabel=0;
		maxLikelihood=DEFAULT_NULL_LIKELIHOOD_VALUE;
		for(int c=0;c<classLikelihoods.size();c++)
			classLikelihoods.set(c, DEFAULT_NULL_LIKELIHOOD_VALUE);
		
		if(numFeatures!=inputVector.size())
			return false;
		
		continuousInputDataBuffer.push_back(inputVector);
		
		if(continuousInputDataBuffer.getNumValuesInBuffer() <avgTempLength)
			return false;
		
		Matrix predictionTimeSeries =new Matrix(avgTempLength,numFeatures);
		for(int i=0;i<predictionTimeSeries.getNumRows();i++)
			for(int j=0;j<predictionTimeSeries.getNumCols();j++)
				predictionTimeSeries.dataptr[i][j]=continuousInputDataBuffer.getBuffer(i)+j;
		
		return predict(predictionTimeSeries);
	}*/
	
	
	double computeDistance(Matrix timeSeriesA ,Matrix timeSeriesB)
	{
		double[][] distMatrix=null;
                
		Vector<IndexDist> warpPath = new Vector<IndexDist>();
		IndexDist tempW = new IndexDist();
		int M=timeSeriesA.getNumRows();
		int N=timeSeriesB.getNumRows();
		int C=timeSeriesA.getNumCols();
		int i,j,k,index=0;
		double totalDist,v,normFactor=0.0;
		
		radius=Math.ceil(Math.min(M,N)/2.0);
		distMatrix=new double[M+1][N+1];
		for(i=0;i<M;i++)
			distMatrix[i]=new double[N];
                double tab[][]=new double[M+1][N+1];
		//Euclidean distance
                 tab[0][0]=0;
            for(int ii=1;ii<=M;ii++)
            tab[ii][0]=Double.POSITIVE_INFINITY;
            for(int ii=1;ii<=N;ii++)
            tab[0][ii]=Double.POSITIVE_INFINITY;
		for(i=0;i<M;i++)
		{
			for(j=0;j<N;j++)
			{
				distMatrix[i][j]=0.0;
				for(k=0;k<C;k++)
				{
					//distMatrix[i][j]+=Math.sqrt(timeSeriesA.dataptr[i][k]-timeSeriesB.dataptr[j][k]);
                                    distMatrix[i][j]+=Math.abs(timeSeriesA.dataptr[i][k]-timeSeriesB.dataptr[j][k]);
				}
                                double dt=d(i+1,j+1,tab);
                                tab[i+1][j+1]=dt+distMatrix[i][j];
				//distMatrix[i][j]=Math.sqrt(distMatrix[i][j]);
			}
		}
		
		
		
		for(i=0;i<M;i++)
			for(j=0;j<N;j++)
				distMatrix[i][j]=tab[i][j];
		
		i=M-1;
		j=N-1;
		tempW.x = i;
		tempW.y = j;
	    tempW.dist = distMatrix[tempW.x][tempW.y];
		totalDist = distMatrix[tempW.x][tempW.y];
	    warpPath.addElement(tempW);
		

	    normFactor = 1;
		while( i != 0 && j != 0 ) {
			if(i==0) j--;
			else if(j==0) i--;
			else{
	            //Find the minimum cell to move to
				v = Double.MAX_VALUE;
				index = 0;
                                
				if( distMatrix[i-1][j] < v ){ v = distMatrix[i-1][j]; index = 1; }
				if( distMatrix[i][j-1] < v ){ v = distMatrix[i][j-1]; index = 2; }
				if( distMatrix[i-1][j-1] < v ){ v = distMatrix[i-1][j-1]; index = 3; }
                                //System.out.println("ind"+index);
				switch(index){
					case 1:
						i--;
						break;
					case(2):
						j--;
						break;
					case(3):
						i--;
						j--;
						break;
					default:
	                   return Double.POSITIVE_INFINITY;
						
				}
			}
			normFactor++;
			tempW.x = i;
			tempW.y = j;
	        tempW.dist = distMatrix[tempW.x][tempW.y];
			totalDist += distMatrix[tempW.x][tempW.y];
			warpPath.addElement(tempW);
		}

		for(i=0; i<M; i++){
			
			distMatrix[i] = null;
		}
		
		distMatrix=null;
		
		return totalDist/normFactor;
	}

	
	
	/**
	 * @param m
	 * @param n
	 * @param distMatrix
	 * @param M
	 * @param N
	 * @return
	 */
	private double d(int i, int j,double tab[][]) {
		// TODO Auto-generated method stub
		//double dist=0;
		double min;
                 min=tab[i-1][j]<tab[i][j-1]?tab[i-1][j]:tab[i][j-1];
        min=min<tab[i-1][j-1]?min:tab[i-1][j-1];
        return min;
	
            

		
		
	}

	
	
	public Boolean saveModelToFile(String name) throws FileNotFoundException
	{
		PrintWriter pw=new PrintWriter(name);
		
		if(!trained)
		{
			System.out.println("Model not yet trained");
			return false;
		}
		
		pw.println("DTW Model File");
		pw.println("NumberOfDimensions:"+numFeatures);
		pw.println("NumberOfClasses:"+numClasses);
		pw.println("NumberOfTemplates:"+numTemplates);
		//pw.println("DistanceMethod:Euclidea");
		pw.println("OverallAverageTemplateLength:"+avgTempLength);
		
		for(int i=0;i<numClasses;i++)
		{
			pw.println("Template:"+(i+1));
			pw.println("ClassLabel:"+templatesBuffer.get(i).classLabel);
			pw.println("TimeSeriesLength:"+templatesBuffer.get(i).timeSeries.getNumRows());
			pw.println("TemplateThreshold:"+templatesBuffer.get(i).threshold);
			pw.println("TrainingMu:"+templatesBuffer.get(i).trainingMu);
			pw.println("TrainingSigma:"+templatesBuffer.get(i).trainingSigma);
			pw.println("AverageTemplateLength:"+templatesBuffer.get(i).avgTempLength);
			pw.println("TimeSeries");
			
			for(int k=0;k<templatesBuffer.get(i).timeSeries.getNumRows();k++)
			{
				for(int j=0;j<templatesBuffer.get(i).timeSeries.getNumCols();j++)
					pw.print(templatesBuffer.get(i).timeSeries.dataptr[k][j]+"\t");
				pw.println();
			
			}
			//System.out.println("index:"+templatesBuffer.get(i).classLabel);
			pw.println();
		}
		return true;
	}
	
	public Boolean loadModelToFile(String name)
	{
		
		return true;
	}

}