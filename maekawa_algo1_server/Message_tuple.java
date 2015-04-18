package maekawa_algo1_server;

import java.net.Socket;
/*
 * Message tuple Class defines the messages as tuples : <messageType,node_number,timestamp>
 */

public class Message_tuple {
	String messageType;
	int node_number;
	long timestamp;
	Socket soc;
	
    public Message_tuple(String msgtype, String client_number, String time_stamp, Socket soc){
        this.messageType=msgtype;
        this.node_number=Integer.parseInt(client_number);
        this.timestamp =Long.parseLong(time_stamp);
        this.soc = soc;
    }
 
    public String getMessageType(){
    	return messageType;
    }
 
    public int getNodeNumber() {
        return node_number;
    }
    
    public long getTimeStamp(){
    	return timestamp;
    }
    
    public Socket getSoc(){
    	return soc;
    }
}
