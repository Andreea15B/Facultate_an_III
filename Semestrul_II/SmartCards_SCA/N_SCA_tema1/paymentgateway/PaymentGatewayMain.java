package paymentgateway;

public class PaymentGatewayMain {
    public static void main(String[] args) {
        PaymentGateway pg = new PaymentGateway();
        
        if (true == pg.init()) {
            pg.run();
            pg.deinit();
        }
    }
}
