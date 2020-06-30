package PayWord;

import PayWord.LastPayment;

import java.io.Serializable;

public class Token implements Serializable{
    LastPayment lastpay;
    int valueToBuy;
    int whatToBuy;
    int ind1,ind2,ind3;

    public Token(int whatToBuy,LastPayment lp,int ValueToBuy,int index1,int index2,int index3){
        this.whatToBuy=whatToBuy;
        this.valueToBuy=ValueToBuy;
        this.lastpay=lp;
        this.ind1=index1;
        this.ind2=index2;
        this.ind3=index3;
    }

    @Override
    public String toString() {
        return this.whatToBuy+" "+ this.lastpay.toString() + " " + this.valueToBuy;
    }
}
