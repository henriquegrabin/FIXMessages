import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import quickfix.Message;
import quickfix.DataDictionary;
import quickfix.InvalidMessage;

import java.util.ArrayList;
import java.util.Arrays;

import quickfix.ConfigError;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FileCompare {
	

	public static void compareFiles(String fullFill, String allMsgs, String filename) {
		
		HashMap<String, Execution> fullFillExecution = fullFillTxtReader(fullFill);
		HashMap<String, Execution> fullMessages = csvMessagesReader(allMsgs);
		createFinalCSV(fullFillExecution, fullMessages, filename);
		System.out.println("Creating " + filename);
	
	}
	
	public static HashMap<String, Execution> fullFillTxtReader(String filename) {
		/*
		 * Reads .txt file with FIX messages
		 * Returns a hashmap with key string in the format account_symbol_side
		 * And the values are an Execution object
		 */
		HashMap<String, Execution> results = new HashMap<String, Execution>();
		
		try {
			DataDictionary dd = new DataDictionary("FIX44.xml");
			FileInputStream fis = new FileInputStream(filename);
			Scanner sc = new Scanner(fis);
			while (sc.hasNextLine()) {
				String stringMessage = sc.nextLine(); // the file doesnt have a header
				Message message = new Message(); // create new message
				message.fromString(stringMessage, dd, false); // reads message from string
				OrderReader read = new OrderReader(message); // creates a parser object
				String mapIndex = String.join("_", read.account, read.symbol, Character.toString(read.side)); // creates index for the hashmap
				Execution exec = results.get(mapIndex); // get value with the particular index from the hashmap
				if (exec != null) { // if the key is in the hashmap 
					exec.add(read.quantity, read.lastPx); // use the add function of Execution to add data
				} else { // if the key is not on the hashmap, create a new object
					results.put(mapIndex, new Execution(read.quantity, read.quantity * read.lastPx));
				}
			}
			sc.close();
		} catch (IOException | ConfigError | InvalidMessage e) {
			e.printStackTrace();
		}
		return results; // returns hashmap
	}
	
	public static HashMap<String, Execution> csvMessagesReader(String filename) {
		/*
		 * Reads .csv file with data from executions
		 * Returns a hashmap with key string in the format account_symbol_side
		 * And the values are an Execution object
		 */
		
		HashMap<String, Execution> results = new HashMap<String, Execution>();
		try {
			FileInputStream fis = new FileInputStream(filename);
			Scanner sc = new Scanner(fis);
			
			// reads the header of the csv which is the first line
			String csvLine = sc.nextLine();
			//ArrayList<String> header = new ArrayList<String>(Arrays.asList(csvLine.split(",")));
			
			while (sc.hasNextLine()) {
				csvLine = sc.nextLine();
				ArrayList<String> lineData = new ArrayList<String>(Arrays.asList(csvLine.split(","))); // split the lines into array for each field
				// if the fields total quantity and cumquantity are equal it means that the order is fully executed
				// so we can add it to the list of full orders
				if (Integer.parseInt(lineData.get(4)) == Integer.parseInt(lineData.get(6))) {
					String account = lineData.get(1);
					String symbol = lineData.get(2);
					String side = lineData.get(3);
					
					int quantity = Integer.parseInt(lineData.get(6));
					double price = Double.parseDouble(lineData.get(7));
					
					String mapIndex = String.join("_", account, symbol, side);
					Execution exec = results.get(mapIndex);
					if (exec != null) { // if the key is in the hashmap 
						exec.add(quantity, price); // append values to the object
					} else { // if the key is not on the hashmap
						results.put(mapIndex, new Execution(quantity, quantity * price)); // create new object
					}
				}			
					
			}
			sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return results;
	}
	
	public static void createFinalCSV(HashMap<String, Execution> results_txt, HashMap<String, Execution> results_csv, String filename) {
		/*
		 * Takes two hashmaps as inputs, one for csv file and another for txt file
		 * Hashmap key : String with account_symbol_side
		 * Hashmap values : Execution object with price and notional for the particular key
		 */
		
		// first put all keys together into one set
		Set<String> combinedKeys = new HashSet<String>();
		combinedKeys.addAll(results_txt.keySet());
		combinedKeys.addAll(results_csv.keySet());
		
		File output = new File(filename);
		try {
			output.createNewFile();
            PrintWriter writer = new PrintWriter(output);
            // creates header
            writer.println(String.join(",", "Conta", "Papel", "Lado", "TxtPrecoMedio", "CsvPrecoMedio"));
            
            // for every key of any dictionary
            for (String key : combinedKeys) {
            	ArrayList<String> txt_keys = new ArrayList<String>(Arrays.asList(key.split("_")));
            	String account = txt_keys.get(0);
            	String symbol = txt_keys.get(1);
            	String side = txt_keys.get(2);
            	writer.print(String.join(",", account, symbol, side)); // write the first three columns
            	
            	Execution txtExec = results_txt.get(key); // get the values of the hashmaps
            	Execution csvExec = results_csv.get(key);
            	// extract data from txt hashmap
            	if (txtExec != null) { // if there is data for this key we just write it
            		writer.print("," + Double.toString(txtExec.getAvgPrice()));
            	}else writer.print(","); // if the value is not present just put a comma and keep the data empty
            	// extract data from csv hashmap
            	if (csvExec != null) {
            		writer.print("," + Double.toString(csvExec.getAvgPrice()));
            	}else writer.print(",");
            	writer.println();
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}
}
	