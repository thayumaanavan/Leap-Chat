package Test;



import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

import logic.ProcessingUnit;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;

class TestListener extends Listener {
	
	ProcessingUnit gesture;
	Vector<Double> tmp=new Vector<Double>(20);
	
	
	public TestListener(){
		gesture=new ProcessingUnit(true);
	}
    public void onInit(Controller controller) {
        System.out.println("Initialized");
    }

    public void onConnect(Controller controller) {
        System.out.println("Connected");
    }

    public void onDisconnect(Controller controller) {
        System.out.println("Disconnected");
    }

    public void onExit(Controller controller) {
        System.out.println("Exited");
    }

    public void onFrame(Controller controller) {
        // Get the most recent frame and report some basic information
        Frame frame = controller.frame();
        if(!frame.hands().empty())
        {
        	Hand hand=new Hand();
        	hand=frame.hands().get(0);
        	
        	tmp.addElement((double)frame.hands().count());
        	tmp.add((double)hand.fingers().count());
        	
        	
        	tmp.addElement((double)hand.sphereRadius());
        	tmp.add((double)hand.sphereCenter().magnitude());
        	tmp.add((double)hand.direction().pitch());
        	tmp.add((double)hand.direction().roll());
        	tmp.add((double)hand.direction().yaw());
        	//tmp.addElement((double)hand.finger(0).tipPosition().getX());
        	tmp.add((double)hand.direction().magnitude());
        	tmp.add((double)hand.palmPosition().magnitude());
        	
        	//System.out.println(tmp.get(1));
        	//System.out.println(tmp.get(2));
        	gesture.addData(tmp);
        	tmp=new Vector<Double>(20);
        }
        
        
        
        
    }
}


class Test {
    public static void main(String[] args) throws IOException {
        // Create a sample listener and controller
        TestListener listener = new TestListener();
        Controller controller = new Controller();

        // Have the sample listener receive events from the controller
        controller.addListener(listener);
        listener.gesture.load("gesture.txt");
		listener.gesture.train();
        while(true)
        {
        	
        		Scanner in = new Scanner(System.in);
        		System.out.println("1.Start Gesture\n2.Stop Gesture");
        		
        		int ch=in.nextInt();
        		while(ch!=2)
        		{
        			listener.gesture.startRecognition();
        		}
        		
        			listener.gesture.stopRecognition();
        			System.out.println();
        			
        			
        			
        		
        }
        
        
    }
    
}
        