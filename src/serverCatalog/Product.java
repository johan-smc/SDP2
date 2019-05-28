package serverCatalog;

import protocol2pc.Transaction;
import server.Resource;

public class Product extends Resource {

	private int total;
    private double cost;
    
    public Product(int total, double cost, String id) {
        super(id);
        this.total = total;
        this.cost = cost;
    }

    @Override
    public void doOperations(Transaction transaction) {
        System.out.println("--- NOT IMPLEMENTED");
    }
    
    public String getId() {
    	return this.id;
    }

	public double getCost() {
		return cost;
	}
	
	public int getTotal() {
		return total;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}
	

	public void setTotal(int total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "Product [total=" + total + ", cost=" + cost +  "ID="+ this.id + "]";
	}
    
    
}
