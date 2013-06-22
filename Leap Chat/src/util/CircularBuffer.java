/**
 * 
 */
package util;

import java.util.Vector;

/**
 * @author thayumaanavan
 *
 */
public class CircularBuffer<T> {

	int bufferSize;
	int numValuesInBuffer;
	int circularBufferSize;
	int readPtr;
	int writePtr;
	Vector<Integer> buffer;
	Boolean bufferInit;
	
	/**
	 * 
	 */
	public CircularBuffer() {
		// TODO Auto-generated constructor stub
		bufferSize = 0;
        numValuesInBuffer = 0;
        circularBufferSize = 0;
        readPtr = 0;
        writePtr = 0;
        bufferInit = false;
        buffer=new Vector<Integer>();

	}
	
	public CircularBuffer( int bufferSize){
        bufferInit = false;
        resize(bufferSize);
    }
	
	public Vector<Integer> getbuffer()
	{
		return buffer;
	}
	public int getBuffer(int i)
	{
		return buffer.get((readPtr + bufferSize + i) % circularBufferSize );
	}

    
    
    public Boolean resize(int newBufferSize){
        
        //Cleanup the old memory
        clear();
        
        if( newBufferSize == 0 ) return false;
        
        //Setup the memory for the new buffer
        bufferSize = newBufferSize;
        circularBufferSize = bufferSize*2;
        buffer=new Vector<Integer>(circularBufferSize);
        for(int i=0;i<buffer.capacity();i++)
        	buffer.add(i,0);
        //System.out.println("buffer"+buffer.capacity());
        numValuesInBuffer = 0;
        readPtr = 0;
        writePtr = 0;
        
        //Flag that the filter has been initialised
        bufferInit = true;
        
        return true;
   }

    
    public Boolean push_back(T value)
    {
    	if(!bufferInit)
    		return false;
    	
    	buffer.set(writePtr,(Integer) value);
    	
    	if(++numValuesInBuffer >= bufferSize)
    	{
    		numValuesInBuffer=bufferSize;
    	}
    	
    	readPtr= (++readPtr) %circularBufferSize;
    	writePtr= (++writePtr) % circularBufferSize;
    	
    	return true;
    	
    }
    
    public void clear(){
        if( bufferInit ){
            numValuesInBuffer = 0;
            circularBufferSize = 0;
            readPtr = 0;
            writePtr = 0;
            buffer=new Vector<Integer>();
            bufferInit = false;
        }
    }


    Boolean getInit(){ 
    	return bufferInit; 
    }
    
    Boolean getBufferFilled(){ 
    	return( bufferInit && numValuesInBuffer==bufferSize ); 
    }
    
    int getSize(){ 
    	return bufferInit ? bufferSize : 0; 
    }
    
    public int getNumValuesInBuffer(){ 
    	return bufferInit ? numValuesInBuffer : 0; 
    }
    
    int getReadPointerPosition(){ 
    	return bufferInit ? readPtr : 0; 
    }
    
    int getWritePointerPosition(){ 
    	return bufferInit ? writePtr : 0; 
    }

    


	
	

}
