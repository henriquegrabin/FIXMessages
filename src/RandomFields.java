import java.util.Random;
import java.lang.Math;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;

public class RandomFields  {
	private static PropertiesReader prop = new PropertiesReader("random.properties");
	private static String[] symbols = prop.getSymbols();
	private static String[] traders = prop.getTraders();
	private static String[] accounts = prop.getAccounts();
	
	private static char[] sides = new char[] {'1', '2'};
	
	private static Random r = new Random();
	
	private static final LocalDate day = LocalDate.of(2021, 11, 27);
	private static final LocalTime closeTime = LocalTime.of(17, 59, 59, 999);
	// conta
	// quantidade
	public static int RandomQuantity() {
		// returns a random int
		return 500+10*(r.nextInt(25)*100);
	}
	public static int RandomQuantity(int upper) {
		//returns a random int strictly smaller than upper
		return 100 + r.nextInt((upper-100)/100)*100; // random execution between 100 and leavesQty - 100 included
	}
	
	public static String RandomTrader() {
		int randTradInd = r.nextInt(traders.length);
		return traders[randTradInd];
	}
	
	public static String RandomAccount() {
		int randAccInd = r.nextInt(accounts.length);
		return accounts[randAccInd];
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
		int hour = r.nextInt(8)+10; // between 10:00 e 17:59:59:999
		int minute = r.nextInt(59);
		int second = r.nextInt(59);
		int nanosecond = r.nextInt(999)*1000000;
		LocalTime time = LocalTime.of(hour, minute, second, nanosecond);
		//return new SendingTime(LocalDateTime.of(2021,  11, 27, hour, minute, second, nanosecond));
		return LocalDateTime.of(day, time);
	}
	
	public static LocalDateTime RandomSendingTime(LocalDateTime firstLocalDateTime) {
		LocalDateTime end = LocalDateTime.of(day, closeTime); // close time
		long millis = Duration.between(firstLocalDateTime, end).toMillis();
		// time of next trade is equal to the time of the last trade + random time up to 80% of the remaining time until close
		return firstLocalDateTime.plus(Duration.ofMillis((long) (0.8*r.nextDouble() * millis)));
	}

	public static LocalDate getDate() {
		return day;
	}
	
}
