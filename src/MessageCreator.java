import java.util.HashMap;

import quickfix.Message;
import quickfix.field.*;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.ExecutionReport.NoPartyIDs;

public class MessageCreator {
	private static int msgSeqNum = 10000;
	
	static HashMap<String, Integer> execIDHelper = new HashMap<String, Integer>();
	
	public static String getNewExecID(String symbol) {
		/*
		 * The ExecID is unique per symbol, so every symbol will 
		 * have a counter starting from zero, and the execID will 
		 * be a string with the symbol and the counter
		 */
		if (execIDHelper.containsKey(symbol)) { // there's no count for this symbol
			int symbolCounter = execIDHelper.get(symbol);
			execIDHelper.put(symbol, symbolCounter + 1);
			return symbol + String.format("%05d", symbolCounter + 1);
		} else {
			execIDHelper.put(symbol, 1);
			return symbol + String.format("%05d", 1);
		}
	}
	
	public static int getNewSeqNum() {
		msgSeqNum = msgSeqNum + 1;
		return msgSeqNum;
	}
	
	public static Message createMessage(Order order) {
		Message message = new ExecutionReport();
		message = addHeader(message);
		message = setFields(message, order);
		message = addRepeatingGroup(message, order);
		return message;
	}
	
	public static Message addHeader(Message message) {
		message.setField(new SenderCompID("SYSTEM")); // <49>
		message.setField(new TargetCompID("DROPCOPY")); // <56>
		message.setField(new MsgSeqNum(getNewSeqNum())); // <34>
		return message;
	}
	
	public static Message setFields(Message message, Order order) {
		message.setField(new DeliverToCompID("GATEWAY"));
		message.setField(new OrdType('2'));
		message.setField(new SecurityExchange("BVMF"));
		message.setField(new ExecType('F'));
		message.setField(new AccountType(39));
		message.setField(new OrdStatus(order.getOrdStatus()));
		
		message.setField(new TransactTime(order.getTransactTime()));
		message.setField(new TradeDate(order.getTransactDate().toString()));
		message.setField(new SendingTime(order.getLastExecTime()));
		
		message.setField(new Side(order.getSide()));
		message.setField(new Symbol(order.getSymbol()));;
		message.setField(new OrderQty(order.getQuantity())); //<53>
		message.setField(new ClOrdID(order.getClOrdId()));
		message.setField(new OrderID(order.getOrderID()));
		message.setField(new Account(order.getAccount()));
		message.setField(new ExecID(getNewExecID(order.getSymbol())));
		
		message.setField(new LastPx(order.getLastPx()));
		message.setField(new Price(order.getPrice())); // vamos considerar apenas ordens simples onde as execucoes sairao no preco de envio
		message.setField(new LastQty(order.getLastQty()));
		message.setField(new LeavesQty(order.getLeavesQty()));
		message.setField(new CumQty(order.getCumQty()));
		message.setField(new AvgPx(order.getAvgPx()));
		return message;
	}
	
	public static Message addRepeatingGroup (Message message, Order order){
		NoPartyIDs noPartyIDs = new NoPartyIDs();
		noPartyIDs.setField(new PartyID(order.getTrader()));
		noPartyIDs.setField(new PartyIDSource('D'));
		noPartyIDs.setField(new PartyRole(36)); //entering trader
		message.addGroup(noPartyIDs);
		return message;
	}
}
