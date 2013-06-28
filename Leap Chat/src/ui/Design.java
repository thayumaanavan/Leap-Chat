package ui;

import javax.swing.JPanel;
import logic.ProcessingUnit;
import java.awt.BorderLayout;

import java.awt.Font;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
//import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JFrame;
import logic.ProcessingUnit;



import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;

public class Design extends JPanel implements ActionListener{
	static String stringfinal;
	static JTextField text=new JTextField("Text Area");
	static TestListener listener = new TestListener();
    static Controller controller = new Controller();
    JButton stopButton,startButton;
	public Design() throws IOException
{
	
	JFrame frame=new JFrame();
	JPanel jPanel=new JPanel();
	frame.setLayout(new BorderLayout());
    frame.add(jPanel, BorderLayout.CENTER);
    frame.setSize(1000,800);
    frame.setVisible(true);
    jPanel.setVisible(true);
    jPanel.setLayout(new BorderLayout());
	JLabel label = new JLabel("Welcome");                       
	label.setFont(new Font("Helvetica", Font.ROMAN_BASELINE, 13));          
	jPanel.add(label);            
	
	text.setSize(300, 300);
	startButton = new JButton("Start");
	jPanel.add(startButton);
	startButton.setSize(100,40);
	
	stopButton = new JButton("Stop");
	jPanel.add(stopButton);
	jPanel.add(text);
	startButton.setVisible(true);
	//stopButton.setSize(100, 100);
     // Hav;e the sample listener receive events from the controller
  
	controller.addListener(listener);
	   try
	   {
	     listener.gesture.load("gesture.txt");
	   }
	   catch(Exception e1)
	   {}
			listener.gesture.train();
			startButton.setAlignmentX(RIGHT_ALIGNMENT);
			stopButton.setAlignmentX(LEFT_ALIGNMENT);
			startButton.setBounds(100, 50, 100, 50);
			startButton.setVisible(true);
			stopButton.setBounds(150, 50, 150, 50);
			startButton.addActionListener(this);
	stopButton.addActionListener(this);
}
	public void actionPerformed(ActionEvent e)
	{
	
		//int count=0;
		while(e.getSource()!=stopButton)
		//while(count<15)
		{
		
		listener.gesture.startRecognition();
		stringfinal=logic.ProcessingUnit.str;
		text.setText(stringfinal);
		//count++;
		}
		listener.gesture.stopRecognition();
	}

	
		
      

public static void main(String args[]) throws IOException
{
	Design d=new Design();
}
}
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


	
