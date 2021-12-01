public class Execution {
	private double notional;
	private int quantity;
	
	public Execution(int qty, double not) {
		this.quantity = qty;
		this.notional = not;
	}
	
	public void add(int qty, double price) {
		this.quantity += qty;
		this.notional += qty * price;
	}
	
	public double getAvgPrice() {
		return this.notional/this.quantity;
	}
	
}
