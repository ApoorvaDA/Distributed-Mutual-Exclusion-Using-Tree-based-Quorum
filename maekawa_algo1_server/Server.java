package maekawa_algo1_server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
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
/*
 * The Server Class gets socket connection from clients and
 * starts five threads defined by the Client_listener class 
 * The threads are responsible for reading the message from clients
 * The Server initially sends START messages to all the clients
 * and serves a client with the least request timestamp by getting locked
 * A release message from the same client will unlock the server so as to process further requests
 */
public class Server {
	
	public static int nodeID,locking_client_node;
	IOReadWrite IH;
	public static Queue<Message_tuple> messagePriorityQueue = new PriorityQueue<>(5, PriorityQueueComparator.messageComparator);
	Socket reply_soc;
	public static int received_message_count=0,complete_msg_count=0,sent_message_count=0,halt_msg_count=0;
	public static Boolean server_lock=false;
	
    public static HashMap<String,Socket> ServerSocketMap = new HashMap<String,Socket>();
    public static HashMap<Socket,BufferedReader> inputStreamReaders = new HashMap<Socket,BufferedReader>();
    public static HashMap<Socket,PrintWriter> outputStreamWriters = new HashMap<Socket,PrintWriter>();

    public static void main(String[] args) throws InterruptedException {
		
		// Parsing the input node argument
		if (args.length > 0)
		{
		    nodeID = Integer.parseInt(args[0].replaceAll("[\\D]", ""));
		    System.out.println("NodeID:"+nodeID);
		} else {
			System.out.println("No input Arguments provided. Specify the server or client node number as input argument -- S<n> or C<n> respectively");
		}
		IOReadWrite IO = new IOReadWrite();
		IO.readConfigFile();
		
		Map<String, List<String>> inputFileMap_server = IO.readConfigFile();
		int TotalClientNodes = IO.ClientNodeCount;
		System.out.println("Number of client Nodes:"+TotalClientNodes);
    
		for(String key : inputFileMap_server.keySet())
		{
			if (key.equals("S"+nodeID))
			{
				System.out.println("Key:"+key);
				try
				{
					// Start ServerSocket on the server nodes at ports specified in the configfile to listen to incoming connections from clients
					int port = Integer.parseInt(inputFileMap_server.get(key).get(1));
					System.out.println("Port:"+port);
					ServerSocket server = new ServerSocket(port);
					System.out.println("Server:"+ nodeID +" listening to incoming connections from clients on port:"+port);
		
					int i=0;
					while(TotalClientNodes > 0){
						//ServerSocket Listens for a connection to be made to this socket and accepts it
						Socket socket = server.accept();
						System.out.println("Accepted Connection by Server "+ nodeID +" from client "+i+" at "+ socket);
						System.out.println("-------------------------");
						
						ServerSocketMap.put(Integer.toString(i),socket);
						inputStreamReaders.put(socket,new BufferedReader(new InputStreamReader(socket.getInputStream())));
						outputStreamWriters.put(socket,new PrintWriter(socket.getOutputStream()));
						
						i++;
						
						TotalClientNodes --;
					}
				}
				catch (IOException e)
				{				
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		//Starting listener threads for all client connections
		for(String key : ServerSocketMap.keySet()){
			Client_listener CL = new Client_listener(ServerSocketMap.get(key),IO);
		}
		
		// Server sends START messages to all the clients
		for(Socket key : outputStreamWriters.keySet()){
			//System.out.println("In the loop");
			sent_message_count++;
			String send_message = new StringBuilder().append("START").append(",").append(nodeID).append(",").append(getTimestamp()).toString();
			System.out.println("Sending "+send_message+" to "+ key);
			PrintWriter writer = outputStreamWriters.get(key);
	        writer.println(send_message);
            writer.flush();
		}
    }
    
    // This function sends a reply message to a client pooled by the priority queue when in unlock state
	public static synchronized void SendReplyIfUnlock(Message_tuple m, boolean flag) {
		// TODO Auto-generated method stub
			if(!flag) {
				Message_tuple msg = m;
				locking_client_node = msg.getNodeNumber();
				Socket reply_soc = msg.getSoc();
				
				String out_message = new StringBuilder().append("REPLY").append(",").append(nodeID).append(",").append(getTimestamp()).toString();
				System.out.println(" Sending "+out_message+" to "+locking_client_node);
				PrintWriter writer = outputStreamWriters.get(reply_soc);
				writer.println(out_message);
				writer.flush();
				sent_message_count++;
				System.out.println("Server "+nodeID+" locked by Client "+locking_client_node);
				System.out.println("Flag status:"+getLockFlag());
			} else {
				Message_tuple msg = messagePriorityQueue.poll();
				if(msg != null){
					locking_client_node = msg.getNodeNumber();
					Socket reply_soc1 = msg.getSoc();
					String out_message = new StringBuilder().append("REPLY").append(",").append(nodeID).append(",").append(getTimestamp()).toString();
					System.out.println(" Sending "+out_message+" to "+locking_client_node);
					PrintWriter writer = outputStreamWriters.get(reply_soc1);
					writer.println(out_message);
					writer.flush();
					sent_message_count++;
					System.out.println("Server "+nodeID+" locked by Client "+locking_client_node);
					System.out.println("Flag status:"+getLockFlag());
				}else{
					System.out.println("No messages in Server Queue");
					setLockFlag(false);
				}	
			}	
	}
	
	//Sends HALT message upon receiving COMPLETION message from all the clients
	public static void SendHaltMessage() {
		// TODO Auto-generated method stub
		for(Socket key : outputStreamWriters.keySet()){
			//System.out.println("In the loop");
			sent_message_count++;
			String send_message = new StringBuilder().append("HALT").append(",").append(nodeID).append(",").append(getTimestamp()).toString();
			System.out.println("Sending "+send_message+" to "+ key);
			PrintWriter writer = outputStreamWriters.get(key);
	        writer.println(send_message);
            writer.flush();
		}
		//closeAllOpenSockets();
		exitprogram();
	}
	
	private static void exitprogram() {
		// TODO Auto-generated method stub
		System.out.println("Halting this node");
		System.out.println("Total messages received: "+received_message_count);
		System.out.println("Total messages sent: "+sent_message_count);
		System.exit(0);
	}
	/*
	//Closes all the open sockets 
	private static void closeAllOpenSockets() {
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(nodeID == 1){
			for(String key : ServerSocketMap.keySet()){
		
				try{
					System.out.println("Closing socket on channel "+key);
					Socket socket = ServerSocketMap.get(key);
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
	
	// Method to set the Server state to lock/unlock : lock == true and unlock == false
	public static synchronized void setLockFlag(Boolean n){
		server_lock = n;
	}
	
	// Gets the server state
	public static synchronized Boolean getLockFlag(){
		return server_lock;
	}
	
	//Gets timestamps
    public synchronized static long getTimestamp()
	{
		// TS timestamps
		return new Date().getTime();
	}
}