import java.time.LocalDateTime;
import java.time.LocalDate;


public class Order {
	/* 
	* OrderID
	* Unique identifier for Order as assigned by sell-side. 
	* Uniqueness is guaranteed within a single trading day/instrument.
	*/
	private static int nextOrderID = 0;
	
	// Unique identifier for Order as assigned by the buy-side.
	private static int nextClOrdID = 0;
	
	private int quantity; // quantidade total desejada
	private double price; // preco de envio da ordem
	private double avgPx; // preco medio de execucao
	
	private String clOrdId;
	private String orderID;
	private int cumQty;
	private double lastPx; // preco ultima execucao
	private int lastQty; // quantidade execucao atual
	private int leavesQty;
	private char side;
	private String symbol; // instrument
	private char ordStatus;
	private String account;
	private double execNotional;
	private String trader;

	private LocalDateTime lastExecTime;
	private LocalDateTime transactTime;
	private LocalDate transactDate;
	
	public Order() {
		this.quantity = RandomFields.RandomQuantity(); // quantidade total
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
		//System.out.println(this.leavesQty);
		int partialFillQty = RandomFields.RandomQuantity(this.leavesQty); //quantidade partialfill
		double partialFillPrice = this.price; //RandomFields.RandomPrice(); //preco do fill
		
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

		this.cumQty = this.cumQty + this.lastQty;
		this.avgPx = recalculateAvgPx();
		this.leavesQty = this.quantity - this.cumQty;
		
		updateMessageTimes();
		this.ordStatus = '2'; // filled
	}
	
	public void updateMessageTimes() {
		if (this.lastExecTime == null) // no previous message
			this.lastExecTime = RandomFields.RandomSendingTime();
		else // there is previous message
			this.lastExecTime = RandomFields.RandomSendingTime(this.lastExecTime); //falta implementar que o horario precisa ser
		this.transactTime = this.lastExecTime;
		this.transactDate = RandomFields.getDate();
	}
	
	public double recalculateAvgPx() {
		if (this.cumQty == 0) return 0;
		this.execNotional += this.lastPx * this.lastQty;
		return this.execNotional/this.cumQty;
	}
	
	public static String getNewClOrdID() {
		nextClOrdID = nextClOrdID + 1;
		return Integer.toString(nextClOrdID);
	}
	public static String getNewOrderID() {
		nextOrderID = nextOrderID + 1;
		return Integer.toString(nextOrderID);
	}
	
	// getters, no need for setters
	public String getClOrdId() { return this.clOrdId; }
	public String getOrderID() { return this.orderID; }
	public int getCumQty() {return this.cumQty;}
	public double getLastPx() {return this.lastPx;}
	public int getLastQty() {return this.lastQty;}
	public int getLeavesQty() {return this.leavesQty;}
	public char getSide() {return this.side;}
	public String getSymbol() {return this.symbol;};
	public char getOrdStatus() {return this.ordStatus;}
	public String getAccount() {return this.account;}
	public double getExecNotional() {return this.execNotional;}
	public String getTrader() {return this.trader;}
	public int getQuantity() {return this.quantity;}
	public double getPrice() {return this.price;}
	public double getAvgPx() {return this.avgPx;}
	
	public LocalDateTime getLastExecTime() {return this.lastExecTime;}
	public LocalDateTime getTransactTime() {return this.transactTime;}
	public LocalDate getTransactDate() {return this.transactDate;}
}
