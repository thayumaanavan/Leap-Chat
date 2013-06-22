/**
 * 
 */
package datastructure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;
import util.ClassTracker;


/**
 * @author thayumaanavan
 *
 */
public class LabelledTimeSeriesClassificationData {

	String datasetName;
	String infoText;
	int numDim;
	int totNumSamp;
	Vector <LabelledTimeSeriesClassificationSample> data;
	Vector< ClassTracker > classTracker;	
	Boolean trackingClass;
	
	//Constructor
	public LabelledTimeSeriesClassificationData(int nd,String name,String info)
	{
		this.numDim  =nd;
		this.infoText =info;
		this.datasetName=name;
		this.totNumSamp =0;
		this.data=new Vector<LabelledTimeSeriesClassificationSample>();
		this.classTracker=new Vector<ClassTracker>();
		this.trackingClass=false;
	}
	
	/**
	 * @param current
	 */
	public LabelledTimeSeriesClassificationData(
			LabelledTimeSeriesClassificationData rhs) {
		// TODO Auto-generated constructor stub
		this.data=new Vector<LabelledTimeSeriesClassificationSample>(rhs.data);
		/*for(int i=0;i<rhs.totNumSamp;i++)
		{
			
			this.data.set(i, rhs.data.get(i));
		}*/
		this.datasetName = rhs.datasetName;
	    this.infoText = rhs.infoText;
	    this.numDim = rhs.numDim;
		this.classTracker = rhs.classTracker;
		this.totNumSamp=rhs.totNumSamp;

	}

	public void setDataSetName(String name)
	{
		this.datasetName =name;
	}
	
	public void setNumDim(int nd)
	{
		numDim=nd;
	}
	
	public void setInfoText(String info)
	{
		infoText=info;
	}
	
	public void addSample(int n,Matrix m)
	{
		LabelledTimeSeriesClassificationSample s=new LabelledTimeSeriesClassificationSample(n,m);
		//System.out.println("row="+s.getLength());
		data.addElement(s);
		
		totNumSamp++;
		if( classTracker.size() == 0 )
		{
			ClassTracker tracker=new ClassTracker(n,1);
			classTracker.add(tracker);
		}	
		else
		{
			Boolean labelFound = false;
			for(int i=0; i<classTracker.size(); i++)
			{
				if( n == classTracker.get(i).getClassLabel())
				{
					classTracker.get(i).counter++;
					labelFound = true;
					break;
				}
			}
			if( !labelFound )
			{
				ClassTracker tracker=new ClassTracker(n,1);
				classTracker.add(tracker);
			}
		}
	}
	
	String getDataSetName()
	{
		return this.datasetName;
	}
	
	public int getNumDim()
	{
		return numDim;
	}
	
	public Vector<LabelledTimeSeriesClassificationSample>getDataVector()
	{
		return data;
	}
	
	String getInfoText()
	{
		return infoText;
	}
	
	public void saveDatasetToFile(String fname) throws IOException
	{
		//FileWriter fw=new FileWriter(fname);
		//BufferedWriter bf=new BufferedWriter(fw);
		//String content="LabelledTimeSeriesClassificationData\nDatasetName:"
		//		+datasetName+"\nInfoText:"+infoText+"\nNumDim:"
		//		+numDim+"\nTotalNumTrainingExamples:"+totNumSamp+"\nNumberOfClasses:";
		PrintWriter pw=new PrintWriter(fname);
		pw.println("LabelledTimeSeriesClassificationData-Training dataset");
		pw.println("datasetName: "+datasetName);
		pw.println("InfoText: "+infoText);
		pw.println("NumDim: "+numDim);
		pw.println("TotalNumTrainingExamples: "+totNumSamp);
		pw.println("NumberOfClasses: "+classTracker.size());
		pw.println("ClassIDsAndCounters: ");
		
		for(int i=0;i<classTracker.size();i++)
		{
			pw.println(classTracker.get(i).getClassLabel()+"\t"+classTracker.get(i).counter);
		}
		
		pw.println("LabelledTimeSeriesClassificationData:");
		for(int x=0;x<totNumSamp;x++)
		{
			pw.println();
			pw.println("ClassID: "+data.get(x).getClassLabel());
			pw.println("TimeSerieslength: "+data.get(x).getLength());
			pw.println("TimeSeriesData: ");
			
			for(int i=0;i<data.get(x).getLength();i++)
			{
				for(int j=0;j<numDim;j++)
				{
					pw.print(data.get(x).getData().dataptr[i][j]);
					if(j<numDim-1)
						pw.print("\t");
				}
				pw.println();
			}			
		}
		
		
		//bf.write(content);
		//bf.close();
		pw.close();
		
	}
	
	public void loadDatasetToFile(String fname) throws IOException
	{
		Scanner sc=new Scanner(new File(fname));
		
		int numClasses=0;
		//clear();
		String word;
		word=sc.next();
		
		if(word.compareTo("LabelledTimeSeriesClassificationData-Training")!=0)
		{
			System.out.println("wrong file");
			return;
		}
		word=sc.next();
		
		word=sc.next();
		if(!word.equals("datasetName:"))
		{
			System.out.println("no dataset name");
			return;
		}
		this.datasetName=sc.next();
		
		word=sc.next();
		if(!word.equals("InfoText:"))
		{
			System.out.println("no InfoText");
			return;
		}
		this.infoText=sc.next();
		
		word=sc.next();
		if(!word.equals("NumDim:"))
		{
			System.out.println("no number of dimension");
			return;
		}
		this.numDim=sc.nextInt();
		
		word=sc.next();
		//System.out.println(word);
		if(!word.equals("TotalNumTrainingExamples:"))
		{
			System.out.println("no total num training examples");
			return;
		}
		this.totNumSamp=sc.nextInt();
		//System.out.println(totNumSamp);
		word=sc.next();
		if(!word.equals("NumberOfClasses:"))
		{
			System.out.println("no class");
			return;
		}
		numClasses=sc.nextInt();
		
		classTracker.setSize(numClasses);
		
		word=sc.next();
		if(!word.equals("ClassIDsAndCounters:"))
		{
			System.out.println("no classid and counters");
			return;
		}
		for(int i=0;i<classTracker.size();i++)
		{
			classTracker.set(i, new ClassTracker());
			classTracker.get(i).classLabel=sc.nextInt();
			classTracker.get(i).counter=sc.nextInt();
			
		}
		
		word=sc.next();
		if(!word.equals("LabelledTimeSeriesClassificationData:"))
		{
			System.out.println("Failed to find LabelledTimeSeriesClassificationData");
			return;
		}
		
		this.data.setSize(totNumSamp);
		for(int i=0;i<totNumSamp;i++)
			data.set(i,new LabelledTimeSeriesClassificationSample());
		
		for(int x=0;x<totNumSamp;x++)
		{
			int classLabel=0,timeSeriesLength=0;
			
			word=sc.next();
			//System.out.println(word+" "+x);
			if(!word.equals("ClassID:"))
			{
				System.out.println("Failed to find ClassID");
				return;
			}
			classLabel=sc.nextInt();
			
			word=sc.next();
			if(!word.equals("TimeSerieslength:"))
			{
				System.out.println("Failed to find TimeSeriesData");
				return;
			}
			timeSeriesLength=sc.nextInt();
			//System.out.println(this.data.get(x).getLength());
			word=sc.next();
			if(!word.equals("TimeSeriesData:"))
			{
				System.out.println("Failed to find TimeSeriesData");
				return;
			}
			//System.out.println(word);
			Matrix trainingExample=new Matrix(timeSeriesLength,numDim);
			for(int i=0;i<timeSeriesLength;i++)
			{
		
				for(int j=0;j<numDim;j++)
				{
					trainingExample.dataptr[i][j]=sc.nextDouble();
					//System.out.println(trainingExample.dataptr[i][j]+"  ");
				}
			}
			
			data.get(x).setTrainingSample(classLabel, trainingExample);
			
			
			
		}
		
		sc.close();
		
	}

	/**
	 * @return
	 */
	public int getCountOfData() {
		// TODO Auto-generated method stub
		return this.data.size();
		
	}

	/**
	 * @return
	 */
	public int getNumSamples() {
		// TODO Auto-generated method stub
		return this.totNumSamp;
	}

	/**
	 * @return
	 */
	public int getNumClasses() {
		// TODO Auto-generated method stub
		return classTracker.size();
	}

	/**
	 * @return
	 */
	public Vector<ClassTracker> getClassTracker() {
		// TODO Auto-generated method stub
		return this.classTracker;
	}
	
	public LabelledTimeSeriesClassificationData getClassData(int classLabel){
	    LabelledTimeSeriesClassificationData classData=new LabelledTimeSeriesClassificationData(numDim,null,null);
	    for(int x=0; x<totNumSamp; x++){
	        if( data.get(x).getClassLabel() == classLabel ){
	            classData.addSample( classLabel, data.get(x).getData() );
	        }
	    }
	    return classData;
	}

}
