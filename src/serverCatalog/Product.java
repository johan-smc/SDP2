package serverCatalog;

import protocol2pc.Operation;
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
    protected boolean validate(Transaction transaction) {
        int temporalTotal = total;
        for (Operation o:
                transaction.getOperations()) {
            if( o.getResource().equals(this.id) && o.getType().equals(Operation.TYPE.WRITE)){
                temporalTotal+= (int)o.getValue();
            }
        }

        System.out.println("Validating transaction: " + transaction.getId());
        System.out.println(temporalTotal);
        return temporalTotal >= 0;
    }

    @Override
    public void doOperations(Transaction transaction) {

        for (Operation o:
                transaction.getOperations()) {
            if( o.getResource().equals(this.id) && o.getType().equals(Operation.TYPE.WRITE)){
                this.total += (int)o.getValue();
            }
        }
    }

    @Override
    public String toString() {
        return "id: "+ this.id+ " cost: " + this.cost + " total: " +this.total;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
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
