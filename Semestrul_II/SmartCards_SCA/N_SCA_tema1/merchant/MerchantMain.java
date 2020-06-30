package merchant;

public class MerchantMain {
    public static void main(String[] args) {
        Merchant merchant = new Merchant();
        
        if (true == merchant.init()) {
            merchant.run();
            merchant.deinit();
        }
    }
}
