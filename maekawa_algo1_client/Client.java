package maekawa_algo1_client;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Random;

/*
 * The Client Class establishes connections to all server nodes
 * It then start threads defined by thread class Server_listener
 * for listening to server incoming messages and processes them accordingly  
 */
public class Client {

	public static int nodeID;
	public static int exitCSflag=0,start_msg_count=0,receive_msg_count=0,sent_msg_count=0,CS_count=0,halt_msg_count=0,reply_msg_count=0,latency=0;
	public static int Critical_section_execution_time, wait_time_start_range, wait_time_end_range;
	public static List <String> reply_list = new ArrayList<String>();
	public static String[] reply_nodes;
	public static long requestTS, CriticalSectionTS;
	public static Boolean quorum_status=false;
	
    public static HashMap<String,Socket> ClientSocketMap = new HashMap<String,Socket>();
    public static HashMap<Socket,BufferedReader> inputStreamReaders = new HashMap<Socket,BufferedReader>();
    public static HashMap<Socket,PrintWriter> outputStreamWriters = new HashMap<Socket,PrintWriter>();
	
    static IOReadWrite IO = new IOReadWrite();
    
    public static void main(String[] args) throws InterruptedException {
		
		// Parsing the input node argument and get the nodeID
		if (args.length > 0)
		{
		    nodeID = Integer.parseInt(args[0]);
		    Critical_section_execution_time = Integer.parseInt(args[1]);
		    wait_time_start_range = Integer.parseInt(args[2]);
		    wait_time_end_range = Integer.parseInt(args[3]);
		    System.out.println("NodeID:"+nodeID);
		} else {
			System.out.println("No input Arguments provided. Specify the client node number, CS execution time and wait time range as input argument");
		}
		
		Socket socket;
		Map<String, List<String>> inputFileMap_client = IO.readConfigFile(); //inputFileMap_client has the mapping of servers with respective hostname and port number 
		for(String key : inputFileMap_client.keySet())
		{
			// Send connection to all server nodes
			if (key.startsWith("S"))
			{
				String host = inputFileMap_client.get(key).get(0);
				int port = Integer.parseInt(inputFileMap_client.get(key).get(1));
				try
				{
					System.out.println("Connecting to "+host+":"+port);
					socket = new Socket(host,port); //opens a socket for the servers
					System.out.println("Connection established by Client "+nodeID+" to " + key +" successfully");
					
					ClientSocketMap.put(key,socket); //Hashmap of server node number and socket
					inputStreamReaders.put(socket,new BufferedReader(new InputStreamReader(socket.getInputStream()))); //Hashmap of socket and respective buffer reader
					outputStreamWriters.put(socket,new PrintWriter(socket.getOutputStream())); //Hashmap of socket and respective printwriter
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		//Starting listener threads for all server connections
		for(String key : ClientSocketMap.keySet()){
			Server_listener SL = new Server_listener(ClientSocketMap.get(key),IO);
		}
    }
    
    //checkCS function gets the array of server nodes which have sent reply and checks if they form a quorum 
	public static synchronized Boolean checkCS(List<String> reply_list) {
		
		List<String> replyList = reply_list;
		
		String reply_nodes[] = new String[replyList.size()]; 
		for(int i = 0; i < reply_list.size(); i++) reply_nodes[i] = replyList.get(i);
		
		
		//Create server_node Binary Tree
	    //System.out.println(">>> creating new Binary tree");
	    BinaryTree t = new BinaryTree();
	    
        //System.out.println(">>> adding elements to the tree");
        t.add("1","");
        t.add("3","r");
        t.add("2","l");
        t.add("7","rr");
        t.add("4","ll");
        t.add("5","lr");
        t.add("6","rl");
		
        //If quorum is already formed and further replies are received, do not check critical section
		if(quorum_status != true){
			quorum_status = t.IsQuorum(reply_nodes);
			System.out.println("Does the set "+(Arrays.toString(reply_nodes))+" form a quorum? "+quorum_status);
			
			if(quorum_status == true){
				System.out.println("Calling EnterCS function");
				enterCS();
			}
		} 
		return quorum_status;
	}

	private static void enterCS() {
		CS_count++;
		CriticalSectionTS = getTimestamp();
		System.out.println("=========== Entering Critical Section: "+CS_count+" at "+CriticalSectionTS+" =============");
		try {
			System.out.println("Thread sleeps for"+Critical_section_execution_time+" time-units");
			Thread.sleep(Critical_section_execution_time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("============================================================");
		
		// Sets exitCSflag and sends broadcasts RELEASE message to all the servers 
		exitCSflag = 1;
		if(exitCSflag == 1){
			long latency = CriticalSectionTS - requestTS;
			int Total_msg_count_per_cs = 7+7+reply_msg_count;
			IO.log(nodeID,CS_count,Total_msg_count_per_cs,latency);
			reply_msg_count = 0;
			sendReleaseMessage();
		}
		
		// Wait for a random time time between [x,y] units before entering next CS
		int diff = wait_time_end_range - wait_time_start_range;
		Random rn = new Random();
		int time = wait_time_start_range + rn.nextInt(diff);
		try {
			Thread.sleep(time);
			System.out.println("delay of "+time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//If total number of Critical Sections executed is less than 19, Send Request message
		if(exitCSflag == 1 && CS_count < 20){
			sendRequestMessage();
			exitCSflag = 0;
		}
		
		if(CS_count == 20){
			sendCompleteMessage();
		}
	}	

	public static void sendRequestMessage() {
		// TODO Auto-generated method stub
		requestTS = getTimestamp();
		for(String key : ClientSocketMap.keySet()){
			sent_msg_count++;
			String send_message = new StringBuilder().append("REQUEST").append(",").append(Client.nodeID).append(",").append(requestTS).toString();
			System.out.println("Sending "+send_message+" to "+ key);
			Socket bs = ClientSocketMap.get(key);
			PrintWriter writer = outputStreamWriters.get(bs);
            writer.println(send_message);
            writer.flush();
		}
	}

	private static void sendReleaseMessage() {
		// TODO Auto-generated method stub
		long releaseTS = getTimestamp();
		System.out.println("Broadcast release message");
		for(String key : ClientSocketMap.keySet()){
			sent_msg_count++;
			String send_message = new StringBuilder().append("RELEASE").append(",").append(Client.nodeID).append(",").append(releaseTS).toString();
			System.out.println("Sending "+send_message+" to "+ key);
			Socket bs = ClientSocketMap.get(key);
			PrintWriter writer = outputStreamWriters.get(bs);
            writer.println(send_message);
            writer.flush();
		}
		
	}

	private static void sendCompleteMessage() {
		// TODO Auto-generated method stub
		long completeTS = getTimestamp();
		//System.out.println("In sendCompleteMessage function");
		for(String key : ClientSocketMap.keySet()){
			sent_msg_count++;
			String send_message = new StringBuilder().append("COMPLETE").append(",").append(Client.nodeID).append(",").append(completeTS).toString();
			System.out.println("Sending "+send_message+" to "+ key);
			Socket bs = ClientSocketMap.get(key);
			PrintWriter writer = outputStreamWriters.get(bs);
            writer.println(send_message);
            writer.flush();
		}
	}

    public synchronized static long getTimestamp()
	{
		// TS timestamps
		return new Date().getTime();
	}
    /*
	public static void closeAllOpenSockets() {
		System.out.println("In closeAllOpenSockets function");
		
		// TODO Auto-generated method stub
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (nodeID == 1){
			for(String key : ClientSocketMap.keySet()){
				try{
					System.out.println("Closing socket on channel "+key);
					Socket socket = ClientSocketMap.get(key);
					if (socket != null)
					{
						PrintWriter writer = outputStreamWriters.get(socket);
						BufferedReader BR = inputStreamReaders.get(socket);
						writer.close();
						BR.close();
						socket.close();	
					}
				}	
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}	
		}
	}
	*/
	public static void exitprogram() {
		// TODO Auto-generated method stub
		System.out.println("Halting this node");
		System.out.println("Total messages received: "+receive_msg_count);
		System.out.println("Total messages sent: "+sent_msg_count);
		System.exit(0);
	}
}