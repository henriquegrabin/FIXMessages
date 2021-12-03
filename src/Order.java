import java.time.LocalDateTime;

import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.AvgPx;
import quickfix.field.CumQty;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.OrderQty;
import quickfix.field.PartyID;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TransactTime;
import quickfix.fix44.ExecutionReport.NoPartyIDs;

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
	
	private double lastNotional;
	private double orderNotional;
	private double cumNotional;
	
	public Order() { // create new order
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
	
	public Order(Message message) { // read order from message
		// Fields I am interested at
		try {
			// Instrumento
			Symbol symbol_field = new Symbol();
			message.getField(symbol_field);
			this.symbol = symbol_field.getValue();
			
			// Conta
			Account account_field = new Account();
			message.getField(account_field);
			this.account = account_field.getValue();
			
			// Lado
			Side side_field = new Side();
			message.getField(side_field);
			this.side = side_field.getValue();
			
			// Quantidade Ordem
			OrderQty quantity_field = new OrderQty();
			message.getField(quantity_field);
			this.quantity = (int) quantity_field.getValue();
			
			// Quantidade Executacao Atual
			LastQty lastQty_field = new LastQty();
			message.getField(lastQty_field);
			this.lastQty = (int) lastQty_field.getValue();
			
			// Qtd Executada Acumulada
			//this.cumQty = (int) message.getField(new CumQty()).getValue();
			CumQty cumQty_field = new CumQty();
			message.getField(cumQty_field);
			this.cumQty = (int) cumQty_field.getValue();
			
			// Preco Executado
			//this.lastPx = message.getField(new LastPx()).getValue();
			LastPx lastPx_field = new LastPx();
			message.getField(lastPx_field);
			this.lastPx = lastPx_field.getValue();
					
			// 1. Calcular o notional da ordem
			Price price_field = new Price();
			message.getField(price_field);
			this.orderNotional = (price_field.getValue() * this.quantity);
			
			// 2. Calcular o notional da execucao atual
			this.lastNotional = this.lastPx * this.lastQty;
			
			// 3. Calcular o notional da execucao acumulada
			AvgPx avgPx_field = new AvgPx();
			message.getField(avgPx_field);
			this.cumNotional = (double) avgPx_field.getValue() * this.cumQty;
					
			// Horario
			TransactTime transactTime_field = new TransactTime();
			message.getField(transactTime_field);
			this.transactTime = transactTime_field.getValue();
			
			// Entering Trader
			NoPartyIDs group = new NoPartyIDs();
			message.getGroup(1, group);
			PartyID partyID = group.get(new PartyID());		
			this.trader = partyID.getValue();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
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
	
	// this part is ugly and could be improved
	public String toStringCsv() {
		String[] fieldValues = {this.transactTime.toString(), this.account, this.symbol, String.valueOf(this.side), Integer.toString(this.quantity), Integer.toString(this.lastQty),
				Integer.toString(this.cumQty), Double.toString(this.lastPx), Double.toString(this.orderNotional),
				Double.toString(this.lastNotional), Double.toString(this.cumNotional), this.trader};
		return String.join(",", fieldValues);
	}
	
	public static String csvHeader() {
		String[] fields = {"Horario", "Conta", "Instrumento", "Lado", "QtdOrdem", "QtdExecucaoAtual",
				"QtdExecutadaAcumulada","PrecoExecutado","NotionaldaOrdem",
				"NotionaldaExecucaoAtual","NotionaldaExecucaoAcumulada", "EnteringTrader"};
		return String.join(",", fields);
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
