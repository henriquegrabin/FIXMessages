import java.util.Random;
import java.lang.Math;
import java.time.LocalDateTime;
import java.time.LocalDate;

public class RandomFields {
	
	private static String[] symbols = new String[] {"PETR4", "BBAS3", "VALE3",
			"RAPT4", "SLCE3", "BPAC11", "POMO4", "ITUB4", "GOAU4", "IGTI3"};
	
	private static String[] traders = new String[] {"SMITH", "JOHNSON", "WILLIAMS", 
			"BROWN", "JONES", "GARCIA", "MILLER", "DAVIS", "RODRIGUEZ", "MARTINEZ"};
	
	private static String[] accounts = new String[] {"918237", "918239", "071264", 
			"981727", "849651", "498213", "987453", "879865", "164532"};
	
	private static char[] sides = new char[] {'1', '2'};
	
	private static Random r = new Random();
	
	private static LocalDate day = LocalDate.of(2021, 11, 27);
	// conta
	// quantidade
	public static int RandomQuantity() {
		// returns a random int
		return 500+100*(r.nextInt(25)*100);
	}
	
	public static String RandomTrader() {
		int randTradInd = r.nextInt(traders.length);
		return traders[randTradInd];
	}
	
	public static String RandomAccount() {
		int randAccInd = r.nextInt(accounts.length);
		return accounts[randAccInd];
	}
	
	public static int RandomQuantity(int upper) {
		//returns a random int strictly smaller than upper
		return 100 + r.nextInt((upper-100)/100)*100; // random execution between 100 and leavesQty - 100 included
	}
	
	// preco
	public static double RandomPrice() {
		// prices between 10 and 20
		//return new Price(10.0+10*(Math.round(r.nextDouble()*1000.0)/1000.0));
		return 10.0+10*(Math.round(r.nextDouble()*1000.0)/1000.0);
	}
	
	// lado
	public static char RandomSide() {
		//return new Side(sides[r.nextInt(sides.length)]);
		return sides[r.nextInt(sides.length)];
	}
	
	// instrumento
	public static String RandomSymbol() {
		//return new Symbol(symbols[r.nextInt(symbols.length)]);
		return symbols[r.nextInt(symbols.length)];
	}
	
	// horario
	public static LocalDateTime RandomSendingTime() {
		int hour = r.nextInt(8)+9;
		int minute = r.nextInt(59);
		int second = r.nextInt(59);
		int nanosecond = r.nextInt(999)*1000000;
		//return new SendingTime(LocalDateTime.of(2021,  11, 27, hour, minute, second, nanosecond));
		return LocalDateTime.of(2021, 11, 27, hour, minute, second, nanosecond);
	}

	public static LocalDate getDate() {
		return day;
	}
	
}
