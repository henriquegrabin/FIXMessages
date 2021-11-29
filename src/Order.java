import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.LocalDate;

public class Order {
	// Unique identifier for Order as assigned by sell-side.
	static int nextOrderID = 0;
	
	// Unique identifier for Order as assigned by the buy-side.
	static int nextClOrdID = 0;
	
	int quantity; // quantidade inicial
	double price; // preco de ordem simples
	
	double avgPx; // preco medio de execucao
	
	String clOrdId;
	String orderID;
	int cumQty;
	double lastPx; // preco ultima execucao
	int lastQty; // quantidade execucao atual
	int leavesQty;
	char side;
	String symbol; //instrument
	ArrayList<Double> prices = new ArrayList<>(); //prices filled
	ArrayList<Integer> quantities = new ArrayList<>(); //quantities filled
	char ordStatus;
	String account;
	double execNotional;
	String trader;
	
	LocalDateTime lastExecTime;
	LocalDateTime transactTime;
	LocalDate transactDate;
	
	public static String getNewClOrdID() {
		nextClOrdID = nextClOrdID + 1;
		return Integer.toString(nextClOrdID);
	}
	public static String getNewOrderID() {
		nextOrderID = nextOrderID + 1;
		return Integer.toString(nextOrderID);
	}
	
	public Order() {
		this.quantity = RandomFields.RandomQuantity(); //quantidade total
		this.leavesQty = this.quantity;
	
		this.price = RandomFields.RandomPrice();
		this.side = RandomFields.RandomSide();
		this.symbol = RandomFields.RandomSymbol();
		this.clOrdId = getNewClOrdID();
		this.orderID = getNewOrderID();
		this.ordStatus = '0';
		this.execNotional = 0;
		
		this.account = RandomFields.RandomAccount();
		this.trader = RandomFields.RandomTrader();
	}
	
	public void ExecutePartial() {
		System.out.println(this.leavesQty);
		int partialFillQty = RandomFields.RandomQuantity(this.leavesQty); //quantidade partialfill
		double partialFillPrice = this.price; //RandomFields.RandomPrice(); //preco do fill
		
		this.quantities.add(partialFillQty);
		this.prices.add(partialFillPrice);
		
		this.cumQty = this.cumQty + partialFillQty;
		this.leavesQty = this.quantity - this.cumQty;
		this.lastQty = partialFillQty;
		this.lastPx = partialFillPrice;
		
		this.avgPx = recalculateAvgPx();
		updateMessageTimes();
		this.ordStatus = '1'; // partial fill
		
	}
	
	public void ExecuteAll() {
		this.lastPx = this.price; //RandomFields.RandomPrice();
		this.lastQty = this.leavesQty;
		this.quantities.add(this.lastQty);
		this.prices.add(this.lastPx);
		
		
		this.cumQty = this.cumQty + this.lastQty;
		this.avgPx = recalculateAvgPx();
		this.leavesQty = this.quantity - this.cumQty;
		
		updateMessageTimes();
		this.ordStatus = '2'; // filled
	}
	
	public void updateMessageTimes() {
		if (this.ordStatus == '0') // no previous message
			this.lastExecTime = RandomFields.RandomSendingTime();
		else // there is previous message
			this.lastExecTime = RandomFields.RandomSendingTime(); //falta implementar que o horario precisa ser
		this.transactTime = this.lastExecTime; 
		this.transactDate = RandomFields.getDate();
	}
	
	public double recalculateAvgPx() {
		if (this.cumQty == 0) return 0;
		this.execNotional += this.lastPx * this.lastQty;
		return this.execNotional/this.cumQty;
	}
	
}
