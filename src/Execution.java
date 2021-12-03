public class Execution {
	private double notional;
	private int quantity;
	
	public Execution(int qty, double not)  {
		this.quantity = qty; //quantidade
		this.notional = not; //notional
	}
	
	public void add(int qty, double price) {
		this.quantity += qty;
		this.notional += qty * price;
	}
	
	public double getAvgPrice() {
		return this.notional/this.quantity;
	}
	
}
