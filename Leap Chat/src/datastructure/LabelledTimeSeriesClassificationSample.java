/**
 * 
 */
package datastructure;

/**
 * @author thayumaanavan
 *
 */
public class LabelledTimeSeriesClassificationSample {
	
	int classLabel;
	Matrix data;
	
	public LabelledTimeSeriesClassificationSample()
	{
		classLabel=0;
		data=new Matrix();
	}
	
	public LabelledTimeSeriesClassificationSample(int cl,Matrix m)
	{
		classLabel=cl;
		data=new Matrix(m);
	}
	
	
	public void setTrainingSample(int classLabel, Matrix data)
	{
		this.classLabel=classLabel;
		this.data =new Matrix(data);
		
	}
	
	public int getLength()
	{ 
		 return data.getNumRows(); 
	}
	
    public int getNumDimensions()
    {
    	return data.getNumCols(); 
    }
    
    public int getClassLabel()
    { 
    	return classLabel; 
    }
    
   public  Matrix getData()
    {
    	return data; 
    }


}
