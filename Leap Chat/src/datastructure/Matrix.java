/**
 * 
 */
package datastructure;

import java.util.Vector;



/**
 * @author thayumaanavan
 *
 */
public class Matrix  {
 
	int rows;
	int cols;
	public double [][] dataptr;
	
	//Constructor
	public Matrix()
	{
		rows=0;
		cols=0;
		dataptr=null;
	}
	
	//Constructor, sets the size of the matrix to [rows cols]
	public Matrix(int r,int c)
	{
		rows=r;
		cols=c;
		dataptr=new double[rows][cols];
	}
	
	//Copy constructor
	public Matrix(Matrix m)
	{
		this.rows=m.rows;
		this.cols=m.cols ;
		dataptr=new double[rows][cols];
		for(int i=0;i<rows;i++)
			for(int j=0;j<cols;j++)
				dataptr[i][j]=m.dataptr[i][j];
	}
	
	

	public int getNumRows()
	{
		return rows;
	}
	
	public int getNumCols()
	{
		return cols;
	}
	
	public void clear(){
		if(dataptr!=null){
			for(int i=0; i<rows; i++)
                 dataptr[i]=null;
			dataptr = null;
		}
		rows = 0;
		cols = 0;
	}

	
	public boolean resize(int r,int c){
        //Clear any previous memory
        clear();
        if( r > 0 && c > 0 ){
            rows = r;
            cols = c;
            dataptr = new double[rows][];
            
            //Check to see if the memory was created correctly
            if( dataptr == null ){
                rows = 0;
                cols = 0;
                return false;
            }
            for(int i=0; i<rows; i++){
            	dataptr[i] = new double[cols];
            }
            return true;
        }
        return false;
	}
	
	public Boolean push_back(Vector<Double> sample)
	{	
		if(dataptr==null)
		{
			//System.out.println("1st row");
			cols=9;
			if( !resize(1,cols) ){
                clear();
                return false;
            }
			for(int j=0; j<cols; j++)
				dataptr[0][j] = sample.get(j);
			
			return true;
		}
		
		

		double [][] temp=null;
		temp=new double[rows+1][];
		
		for(int i=0;i<rows+1;i++)
			temp[i]=new double[cols];
		
		for(int i=0;i<rows;i++)
			for(int j=0;j<cols;j++)
				temp[i][j]=dataptr[i][j];
		
		for(int j=0;j<cols;j++)
			temp[rows][j]=sample.get(j);
		
		for(int i=0; i<rows; i++)
				dataptr[i] = null;
		
		//dataptr=temp;
		dataptr=new double[rows+1][cols];
		for(int i=0;i<rows+1;i++)
			for(int j=0;j<cols;j++)
				dataptr[i][j]=temp[i][j];
		rows++;
		return true;
	}	   
	
	public Vector<Double> getRowVector(int r)
	{
		Vector<Double> rowVector=new Vector<Double>(cols);
		for(int c=0;c<cols;c++)
			rowVector.set(c, dataptr[r][c]);
		return rowVector;
		
	}
}
