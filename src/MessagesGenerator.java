import quickfix.Message;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MessagesGenerator {
	
	public static final int messageNum = 5000;
	public static final int minFullFill = 2500; // minimum number of full fill messages to be created
	public static int remainingMessages = messageNum;
	public static int fullFillCreated = 0; // number of full fill messages created
	
	public static Message[] messageArray = new Message[messageNum]; // array with all messages created
	public static ArrayList<Order> openOrders = new ArrayList<Order>(); // arraylist with the orders that are not fully filled	

	public static Random r = new Random();
	
	public static void main(String[] args) {
	
		// Part 1
		generateNewMessages(); // create random orders
		writeTxtFile("results/MessageData.txt"); // write orders to txt file
		
		// Part 2
		MessageReader.FIXMessageReader("results/MessageData.txt"); //read orders from file
		MessageReader.allMsgsCsv("results/AllMsgs.csv");
		MessageReader.writeFullFill("results/FullFill.txt");

		// Part 3
		FileCompare.compareFiles("results/FullFill.txt", "results/AllMsgs.csv", "results/CompareFiles.csv");
	}
	
	public static void generateNewMessages() {
		System.out.println("Creating messages");		
		for (int i = 0; i < messageNum; i = i + 1) {
			// there are many options of different types of orders we might want to include
			// 1 : new order then total fill
			// 2 : new order then partial fill
			// 3 : use partially filled order and then total fill
			// 4 : use partially filled order and then partial fill again
			
			// choose whether full fill or partial fill
			// 0 : partial fill
			// 1 : full fill
			int chooseFill = r.nextInt(2);
			
			//choose whether use partially filled order or new order
			// 0 : open
			// 1 : new
			int chooseOrder = r.nextInt(2);

			Order order;
			// choose open order or create new one
			if ((openOrders.size() != 0) && (chooseOrder == 0)){ // use open order
				int orderIndex = r.nextInt(openOrders.size());
				order = openOrders.remove(orderIndex);
				// System.out.println("Open Order");
			} else { // use new order
				// System.out.println("New Order");
				order = new Order();
			}
			
			// partial fill or full fill
			// new partial fill order
			// if remaining quantity is 100 its not possible to fill partially
			// if remaining messages == remaining full fill, then I must full fill so to create at least 2500 full fills
			if ((chooseFill == 1) && (order.getLeavesQty() >= 200) && (remainingMessages > (minFullFill - fullFillCreated))){ 
				order.ExecutePartial();
				openOrders.add(order);
				//System.out.println("Partial Fill");
			} else { //
				//System.out.println("Full Fill");
				order.ExecuteAll();
				fullFillCreated += 1;
				
			}	
			messageArray[i] = MessageCreator.createMessage(order);
			remainingMessages -= 1;
		}
		System.out.println("Number of full fills: " + Integer.toString(fullFillCreated));
	}
	
	public static void writeTxtFile(String fileName) {
		File output = new File(fileName);
		try {
			output.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
        try {
            PrintWriter writer = new PrintWriter(output);
    		for (int i = 0; i < messageNum; i = i + 1) {
    			writer.println(messageArray[i].toString());
    		}
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
	}
}
