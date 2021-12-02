import quickfix.Message;
import quickfix.field.*;
import java.time.LocalDateTime;
import quickfix.fix44.ExecutionReport.NoPartyIDs;

public class OrderReader {
	private String account; //OK
	private String symbol; //OK
	private char side; // OK
	private int quantity; //OK
	private int lastQty; //OK
	private int cumQty; //OK
	private double lastPx; //OK
	private double lastNotional;
	private double orderNotional;
	private double cumNotional;
	private LocalDateTime transactTime; //OK
	private String trader; //OK
	
	public OrderReader(Message message) {
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
	
	public String getAccount() {return this.account;} //OK
	public String getSymbol() {return this.symbol;} //OK
	public char getSide() {return this.side;} // OK
	public int getQuantity() {return this.quantity;} //OK
	public double getLastPx() {return this.lastPx;} //OK

	
}
