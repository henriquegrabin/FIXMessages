import quickfix.Message;
import quickfix.field.*;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.ExecutionReport.NoPartyIDs;

public class MessageCreator {
	private static int msgSeqNum = 10000;
	
	public static int getNewSeqNum() {
		msgSeqNum = msgSeqNum + 1;
		return msgSeqNum;
	}
	
	public static Message createMessage(Order order) {
		//Message message = new Message();
		Message message = new ExecutionReport();
		message = addHeader(message);
		message = setFields(message, order);
		message = addRepeatingGroup(message, order);
		return message;
	}
	
	public static Message addHeader(Message message) {
		//message.setField(new BeginString("FIX.4.4")); // <8>
		//message.setField(new MsgType("8")); // <35>
		message.setField(new SenderCompID("SYSTEM")); // <49>
		message.setField(new TargetCompID("DROPCOPY")); // <56>
		message.setField(new MsgSeqNum(getNewSeqNum())); // <34>

		//message.setField(RandomFields.RandomSendingTime()); // <52>
		return message;
	}
	
	public static Message setFields(Message message, Order order) {
		message.setField(new DeliverToCompID("GATEWAY"));
		message.setField(new OrdType('2'));
		message.setField(new SecurityExchange("BVMF"));
		message.setField(new ExecType('F'));
		message.setField(new AccountType(39));
		message.setField(new OrdStatus(order.ordStatus));
		
		message.setField(new TransactTime(order.transactTime));
		message.setField(new TradeDate(order.transactDate.toString()));
		message.setField(new SendingTime(order.lastExecTime));
		
		message.setField(new Side(order.side));
		message.setField(new Symbol(order.symbol));;
		message.setField(new OrderQty(order.quantity)); //<53>
		message.setField(new ClOrdID(order.clOrdId));
		message.setField(new OrderID(order.orderID));
		message.setField(new Account(order.account));
		
		message.setField(new LastPx(order.lastPx));
		message.setField(new Price(order.price)); //preco de execucao esta igual ao preco de envio teoricamente deveria ser melhor ou igual ao preco de envio
		message.setField(new LastQty(order.lastQty));
		message.setField(new LeavesQty(order.leavesQty));
		message.setField(new CumQty(order.cumQty));
		message.setField(new AvgPx(order.avgPx));
		return message;
	}
	
	public static Message addRepeatingGroup (Message message, Order order){
		NoPartyIDs noPartyIDs = new NoPartyIDs();
		noPartyIDs.setField(new PartyID(order.trader));
		noPartyIDs.setField(new PartyIDSource('D'));
		noPartyIDs.setField(new PartyRole(36)); //entering trader
		message.addGroup(noPartyIDs);
		return message;
	}
}