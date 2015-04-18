package maekawa_algo1_server;

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
 * IOReadWrite Class is used to read the input config.txt file
 * and map the entries into key:node_number and values:hostname,portnumber
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
}