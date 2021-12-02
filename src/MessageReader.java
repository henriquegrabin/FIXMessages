import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import quickfix.Message;
import quickfix.DataDictionary;
import quickfix.InvalidMessage;

import java.util.ArrayList;
import quickfix.ConfigError;

import quickfix.DoubleField;
import quickfix.FieldNotFound;
import quickfix.StringField;
import quickfix.field.*;
import quickfix.fix44.ExecutionReport.NoPartyIDs;


public class MessageReader {
	
	public static final ArrayList<Message> messageList = new ArrayList<Message>(); // unknown number of lines to be read

	public static void FIXMessageReader(String filename) {
		try {
			DataDictionary dd = new DataDictionary("FIX44.xml");
			FileInputStream fis = new FileInputStream(filename);
			Scanner sc = new Scanner(fis);
			while (sc.hasNextLine()) {
				String stringMessage = sc.nextLine();
				//System.out.println(stringMessage);
				Message message = new Message();
				message.fromString(stringMessage, dd, false);
				//OrderReader read = new OrderReader(message);
				//System.out.println(read.toString());
				messageList.add(message);
			
			}
			sc.close();
		} catch (IOException | ConfigError | InvalidMessage e) {
			e.printStackTrace();
		}
		
		System.out.println("Message files read");	
	}
	
	
	public static void allMsgsCsv(String filename) {
		System.out.println("Creating " + filename);
		File output = new File(filename);
		try {
			//output.mkdirs();
			output.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
        try {
            PrintWriter writer = new PrintWriter(output);
            writer.println(OrderReader.csvHeader());
    		for (int i = 0; i < messageList.size(); i = i + 1) {
    			Message message = messageList.get(i);
    			OrderReader read = new OrderReader(message);
    			writer.println(read.toStringCsv());
    		}
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
	}

	public static Message modifyMessage(Message message) {
		OrderQty quantity_field = new OrderQty();
		Price price_field = new Price();
		NoPartyIDs group = new NoPartyIDs();

		try {
			int quantity;
			double price;
			String trader;
			
			message.getField(quantity_field);
			quantity = (int) quantity_field.getValue();
			
			message.getField(price_field);
			price = price_field.getValue();
			
			message.getGroup(1, group);
			PartyID partyID = group.get(new PartyID());
			trader = partyID.getValue();
			
			message.setField(new DoubleField(1010, quantity*price)); // notional
			message.setField(new StringField(1011, trader));
		} catch (FieldNotFound e) {
			e.printStackTrace();
		}
		return message;
	}
	
	public static void writeFullFill(String filename) {
		System.out.println("Creating " + filename);
		File output = new File(filename);
		OrdStatus ordStatus_field = new OrdStatus();
		char ordStatus = ' ';
		try {
			//output.mkdirs();
			output.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
        try {
            PrintWriter writer = new PrintWriter(output);
    		for (int i = 0; i < messageList.size(); i = i + 1) {
    			Message message = messageList.get(i);
    			message.getField(ordStatus_field);
    			ordStatus = ordStatus_field.getValue();
    			if (ordStatus == '2') {
    				message = modifyMessage(message);
    				writer.println(message.toString());
    			}
    		}
            writer.flush();
            writer.close();
        } catch (FileNotFoundException | FieldNotFound e) {
            e.printStackTrace();
        } 
	}
}
