import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class PropertiesReader {
	Properties prop;
	private String[] symbols;
	private String[] traders;
	private String[] accounts;
	
	private static String[] defaultSymbols = new String[] {"PETR4", "BBAS3", "VALE3",
			"RAPT4", "SLCE3", "BPAC11", "POMO4", "ITUB4", "GOAU4", "IGTI3"};
	
	private static String[] defaultTraders = new String[] {"SMITH", "JOHNSON", "WILLIAMS", 
			"BROWN", "JONES", "GARCIA", "MILLER", "DAVIS", "RODRIGUEZ", "MARTINEZ"};
	
	private static String[] defaultAccounts = new String[] {"918237", "918239", "071264", 
			"981727", "849651", "498213", "987453", "879865", "164532"};
	
	public PropertiesReader(String filename) {
		this.prop = new Properties();
		try (FileInputStream fis = new FileInputStream(filename)) {
		    prop.load(fis);
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("Error opening config file. Using default values.");
			this.symbols = defaultSymbols;
			this.traders = defaultTraders;
			this.accounts = defaultAccounts;
		}
		this.symbols = verifyProperty(prop.getProperty("papeis"), defaultSymbols);
		this.traders = verifyProperty(prop.getProperty("nomes"), defaultTraders);
		this.accounts = verifyProperty(prop.getProperty("contas"), defaultAccounts);
	}
	
	public static String[] verifyProperty(String property, String[] defaultValues) {
			/*
			 * Basic check if the config data is ok.
			 */
			if (property == null) {return defaultValues;}
			String[] array = property.split(",");
			if ((array.length == 0) || (array.length > 10)) {
				return defaultValues;
			}
		return array;
	}
	
	public String[] getSymbols(){ //add if not found return default, // if length largest than 10 use only 10
		return this.symbols;
	}
	public String[] getTraders(){
		return this.traders;
	}
	public String[] getAccounts(){
		return this.accounts;
	}
}
