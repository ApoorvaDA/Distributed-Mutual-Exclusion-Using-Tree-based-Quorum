package maekawa_algo1_client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
 * IOReadWrite Class reads from the input configuration file 
 * and formats it in the form of a map containing node number as key and
 * hostname and port number as values
 * It also creates a log file and writes the request_time and CS entered time stamp with latency
 * 
 */
public class IOReadWrite {
	public int ClientNodeCount=0;
	public Map<String, List<String>> inputFileMap = new HashMap<String, List<String>>();
	
	public Map<String, List<String>> readConfigFile()
	{
		try (BufferedReader br = new BufferedReader(new FileReader("config.txt")))
		{
			String CurrentLine;
			while ((CurrentLine = br.readLine()) != null)
			{
				if (!CurrentLine.startsWith("#"))
				{
					String[] vals = CurrentLine.split(" ");
					if(vals[1].startsWith("#")){
						ClientNodeCount=Integer.parseInt(vals[0]);
					} else {
						List<String> valueList = new ArrayList<String>();
						valueList.add(vals[1]);
						valueList.add(vals[2]);
						inputFileMap.put(vals[0], valueList);
					}
				}
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return inputFileMap;
	}
	// logging messages
	public synchronized void log(int client_node_number,int CS_count, int total_msgs, long latency)
	{
		try
		{
			File file = new File("logfile.txt");
			if (!file.exists())
			{
				file.createNewFile();
			}
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("Client number:"+client_node_number+"  Critical Section:"+CS_count+"  Total messages exchanged:"+ total_msgs+"  Latency:"+latency+"\n");
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}