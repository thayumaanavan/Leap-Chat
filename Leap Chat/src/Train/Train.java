package Train;

/******************************************************************************\
* Copyright (C) 2012-2013 Leap Motion, Inc. All rights reserved.               *
* Leap Motion proprietary and confidential. Not for distribution.              *
* Use subject to the terms of the Leap Motion SDK Agreement available at       *
* https://developer.leapmotion.com/sdk_agreement, or another agreement         *
* between Leap Motion and you, your company or other organization.             *
\******************************************************************************/

import java.io.IOException;
import java.lang.Math;

import logic.ProcessingUnit;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;

import java.util.Scanner;
import java.util.Vector;

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
        	/*for(int i=0;i<5;i++)
        	{
        		if(hand.finger(i).isValid())
        			tmp.add((double) hand.finger(i).length());
        		else
        			tmp.add(0.0);
        	}*/
        	
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

class Train {
    public static void main(String[] args) {
        // Create a sample listener and controller
        TestListener listener = new TestListener();
        Controller controller = new Controller();

        // Have the sample listener receive events from the controller
        controller.addListener(listener);
       
        while(true)
        {
        	System.out.println("1.Training phase\n2.Real time prediction phase\n");
        	
        

        // Keep this process running until Enter is pressed
        //System.out.println("Press Enter to quit...");
        
        
        	try {
        		Scanner in = new Scanner(System.in);
        		int ch=in.nextInt();
        		System.out.println(ch);
        		switch(ch)
        		{
        		case 1:
        			Boolean condition=true;
        			while(condition)
        			{
        			System.out.println("1.Start gesture\n 2.Stop gesture\n 3.Add gesture Sign\n" 
        	        		+"4.Save training data to file\n5.Load training data from file\n6.Train\n7.Back\n");
            		int c=in.nextInt();
            		
            		switch(c)
            		{
            		case 1:
            			listener.gesture.startTraining();
            			break;
            		case 2:
            			listener.gesture.stopTraining();
            			break;
            		case 3:
            			System.out.println("Enter the gesture id:");
            			int id=in.nextInt();
            			listener.gesture.finishTraining(id);
            			break;
            		case 4:
            			System.out.println("Enter the file name:");
            			Scanner s = new Scanner(System.in);
            			listener.gesture.Save(s.next());
            			break;
            		case 5:
            			System.out.println("Enter the file name:");
            			Scanner sc = new Scanner(System.in);
            			listener.gesture.load(sc.next());
            			break;
            		case 6:
            			listener.gesture.train();
            			break;
            		case 7:
            			condition=false;
            			break;
            		}
        			}
        			break;
        		case 2:
        			Boolean cond=true;
        			while(cond)
        			{

        			System.out.println("1.Start gesture\n2.Stop gesture\n3.save model to file\n4.Back\n5.test");
            		int choice=in.nextInt();
            		switch(choice)
            		{
            		case 1:
            			listener.gesture.startRecognition();
            			break;
            		case 2:
            			listener.gesture.stopRecognition();
            			break;
            		case 3:
            			System.out.println("Enter file name:");
            			listener.gesture.saveModel(in.next());
            			break;
            		/*case 4:
            			System.out.println("Enter file name:");
            			listener.gesture.loadModel(in.next()); 
            			break;*/
            		case 4:
            			cond=false;
            			break;
            		case 5:
            			System.out.println("Enter file name:");
            			listener.gesture.test(in.next());
            			break;
            		}
        			}
            		break;
        		}
        		
        		
        		
        	} catch (IOException e) {
            e.printStackTrace();
        	}
        }
        
        
    }
}
