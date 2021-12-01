import quickfix.Message;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MessagesGenerator {
	
	public static int messageNum = 5000;
	
	public static Message[] messageArray = new Message[messageNum]; // array with all messages created
	public static ArrayList<Order> openOrders = new ArrayList<Order>(); // arraylist with the orders that are not fully filled	

	public static Random r = new Random();
	
	public static void main(String[] args) {
		// Part 1
		generateNewMessages(); // create random orders
		writeTxtFile("output.txt"); // write orders to txt file
		
		// Part 2
		MessageReader.FIXMessageReader("output.txt"); //read orders from file
		MessageReader.allMsgsCsv("AllMsgs.csv");
		MessageReader.writeFullFill("FullFill.txt");

		// Part 3
		FileCompare.compareFiles("FullFill.txt","AllMsgs.csv");
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
				System.out.println("Open Order");
			} else { // use new order
				System.out.println("New Order");
				order = new Order();
			}
			
			// partial fill or full fill
			if ((chooseFill == 1) && (order.leavesQty >= 200)){ // new partial fill order
				order.ExecutePartial();
				openOrders.add(order);
				System.out.println("Partial Fill");
			} else { //
				System.out.println("Full Fill");
				order.ExecuteAll();
			}	
			messageArray[i] = MessageCreator.createMessage(order);
			
			// if the order was in the order list and we did 
			
			System.out.println(messageArray[i].toString());
		}
	}
	
	public static void writeTxtFile(String fileName) {
		File output = new File(fileName);
		try {
			//output.mkdirs();
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
