package maekawa_algo1_client;

import java.net.Socket;
/*
 * Message_tuple class defines the tuple : <messageType,Nodenumber,timestamp>
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message_tuple other = (Message_tuple) obj;
		if (node_number != other.node_number)
			return false;
		return true;
	}
    
}
