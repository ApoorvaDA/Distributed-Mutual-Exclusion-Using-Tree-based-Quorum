package maekawa_algo1_server;

import java.net.Socket;
import java.io.BufferedReader;
/*
 * Client_listener class listens to the incoming message from the clients and processes them accordingly
 */
public class Client_listener extends Thread {
	BufferedReader BR;
	IOReadWrite IH;
	Socket soc;
	
	Client_listener(Socket socket, IOReadWrite IH){
		super();
		start();
		this.IH = IH;
		this.soc = socket; 
		try {
			BR = Server.inputStreamReaders.get(soc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		String message;
		
		//System.out.println(soc);
		try{
			while((message=BR.readLine() ) != null)
			{
				Server.received_message_count++;
				String token[] = message.split(",");
				String messageType = token[0];
				String client_number = token[1];
				String time_stamp = token[2];
				//System.out.println(messageType);
			
				if (messageType.equals("REQUEST")){
					//If a request message is received from a client, add it to the priorityqueue
					//to be served by the server when it is in unlock state
					System.out.println("Message from Client:"+ client_number +" is "+ messageType +" at Time stamp " + time_stamp);
					if(!Server.getLockFlag()){
						Server.setLockFlag(true);
						Message_tuple M1 = new Message_tuple(messageType,client_number,time_stamp,soc);
						Server.SendReplyIfUnlock(M1,false);
					} else {
						//System.out.println("Message from Client:"+ client_number +" is "+ messageType +" at Time stamp " + time_stamp);
						System.out.println("Adding to the priority queue");
						Server.messagePriorityQueue.add(new Message_tuple(messageType,client_number,time_stamp,soc));
					}
				} else if(messageType.equals("RELEASE")){
					//Check the server status
					//If the server has been locked by a request from the same client, change the server state and process the next request
					Boolean flag = Server.getLockFlag();
					int locking_client_id = Server.locking_client_node;
					
					if((flag == true) && (client_number.equals(Integer.toString(locking_client_id)))){
						Message_tuple M1 = new Message_tuple(messageType,client_number,time_stamp,soc);
						System.out.println("Message from Client:"+ client_number +" is "+ messageType +" at Time stamp " + time_stamp);
						//Server.setLockFlag(false);
						Server.SendReplyIfUnlock(M1,true);
					} else{
						Message_tuple M2 = new Message_tuple(messageType,client_number,time_stamp,soc);
						Boolean message_already_present = Server.messagePriorityQueue.contains(M2);
						if(message_already_present){
							Server.messagePriorityQueue.remove(M2);
						}
					}
				} else if(messageType.equals("COMPLETE")){
					Server.complete_msg_count++;
					//When complete message is received from all five clients send HALT message to all 
					//clients and close the sockets
					if(Server.complete_msg_count == 5){
						Server.SendHaltMessage();
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}	
}