package serverCatalog;

import protocol2pc.Transaction;
import server.Resource;

public class Product extends Resource {

    private int total;
    private double cost;
    Product(int total,double cost, String id)
    {
        super(id);
        this.total = total;
        this.cost = cost;
    }

    @Override
    public void doOperations(Transaction transaction) {
        System.out.println("--- NOT IMPLEMENTED");
    }
}
