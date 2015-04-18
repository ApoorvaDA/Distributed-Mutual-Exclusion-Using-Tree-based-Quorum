package maekawa_algo1_server;

import java.net.Socket;
import java.util.Comparator;
import java.util.Queue;
/*
 * PriorityQueueComparator is defined based upon a comparator method
 * which keeps the message with least timestamp/client id at the head of the queue
 */

public class PriorityQueueComparator {
	public static Comparator<Message_tuple> messageComparator = new Comparator<Message_tuple>(){
        @Override 
		public int compare(Message_tuple M1, Message_tuple M2) {
        	/*if (M1.getTimeStamp() == M2.getTimeStamp()) {
        		return (int) (M1.getNodeNumber() - M2.getNodeNumber());
        	} else {*/
        		return (int) (M1.getTimeStamp() - M2.getTimeStamp());
        	//}
        }
	};    
	/*
    public static Comparator<Message_tuple> messageTimeStampComparator = new Comparator<Message_tuple>(){
         @Override
         public int compare(Message_tuple M1, Message_tuple M2) {	
        		//return (int) (M1.getNodeNumber() - M2.getNodeNumber());
          	return (int) (M1.getTimeStamp() - M2.getTimeStamp());
         }
    };    
    
    public static Comparator<Message_tuple> messageNodeNumberComparator = new Comparator<Message_tuple>(){
    	public int compare(Message_tuple M1, Message_tuple M2) {
            	return (int) (M1.getNodeNumber() - M2.getNodeNumber());
        }
    };
    */
    public static Socket pollDataFromQueue(Queue<Message_tuple> messagePriorityQueue) {
    	Message_tuple msg = messagePriorityQueue.poll();
    	Socket Soc_rply = null;
        if(msg != null) { 
        	Soc_rply = msg.getSoc();
        	System.out.println("Processing Request with ClientID/TimeStamp="+msg.getNodeNumber()+"/"+msg.getTimeStamp());
        }
        return Soc_rply;
    };

}