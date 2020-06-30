package customer;

public class CustomerMain {
    public static void main(String[] args) {
        Customer customer = new Customer();

        if (true == customer.init()) {
            customer.run();
        }
    }
}
