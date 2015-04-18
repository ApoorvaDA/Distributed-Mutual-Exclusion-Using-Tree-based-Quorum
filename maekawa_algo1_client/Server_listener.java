package maekawa_algo1_client;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.io.BufferedReader;

/*
 * Server_listener class is the thread called for each server conneection
 * to listen to incoming messages 
 */
public class Server_listener extends Thread {
	Socket soc;
	BufferedReader BR;
	IOReadWrite IH;
	public static List <String> reply_list = new ArrayList<String>();
	Boolean IsQuorum = false;
	
	Server_listener(Socket socket, IOReadWrite IH){
		super();
		start();
		this.IH = IH;
		this.soc = socket;
		try {
			BR = Client.inputStreamReaders.get(soc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		String message;

		try{
			while((message=BR.readLine()) != null){
				System.out.println(message);
				Client.receive_msg_count++;
				String token[] = message.split(",");
				String messageType = token[0];
				String server_number = token[1];
				String time_stamp = token[2];
				
				if(messageType.equals("START")){
					Client.start_msg_count++;
					if((Client.start_msg_count == 7)){
						System.out.println("Start message received from all the servers; Sending request messages");
						Client.sendRequestMessage();
					}
				} else if(messageType.equals("REPLY")){
					//Add the server_node to the array and check for Quorum
					Client.reply_msg_count++;
					reply_list.add(server_number);
						
					Boolean IsQuorum = Client.checkCS(reply_list);
					if(IsQuorum == true){
						Client.quorum_status=false;
						reply_list.clear();
					}	
				} else if(messageType.equals("HALT")){
				    Client.halt_msg_count++;
				    if(Client.halt_msg_count == 7){
				    	//Client.closeAllOpenSockets();
				    	Client.exitprogram();
				    }	
				}
			}	
			//System.out.println("Did not enter while loop at all");
		} catch(Exception E) {
			E.printStackTrace();
		}	
	}
}
