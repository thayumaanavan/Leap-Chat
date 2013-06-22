/**
 * 
 */
package util;

/**
 * @author thayumaanavan
 *
 */
public class ClassTracker {

	
	public int classLabel;
    public int counter;
    String className;
	/**
	 * 
	 */
	public ClassTracker() {
		// TODO Auto-generated constructor stub
		this.classLabel = 0;
        this.counter = 0;
        this.className = "NOT_SET";
	}
	
	public ClassTracker( int classLabel , int counter  ){
        this.classLabel = classLabel;
        this.counter = counter;
       // this.className = className;
    }
	
	public int getClassLabel()
	{
		return classLabel;
	}
	
	public int getCounter()
	{
		return counter;
	}

}
