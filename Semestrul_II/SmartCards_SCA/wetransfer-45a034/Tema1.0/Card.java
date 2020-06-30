import java.io.*;

public class Card implements Serializable {
    public int cvv;
    public String clientName;
    public String cardNumber;
    public String expDate;
    public int creditLimit;

    public Card(String ClientName,String CardNumber,String ExpDate,int Cvv,int CreditLimit) {
        this.clientName=ClientName;
        this.cardNumber=CardNumber;
        this.expDate=ExpDate;
        this.cvv=Cvv;
        this.creditLimit=CreditLimit;
    }

    @Override
    public String toString() {
        return this.clientName+" "+this.cardNumber+" "+this.expDate+" "+this.cvv+" "+this.creditLimit;
    }
}