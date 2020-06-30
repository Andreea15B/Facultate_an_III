package PayWord;

//import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.Serializable;

public class Commit implements Serializable{
    String payWordCertif;
    Token token;
    boolean iWantToBuy;
    int cardLimit;

    public Commit(String Payword, Token tk,int CardLimit, boolean IWantToBuy)
    {
        this.payWordCertif=Payword;
        this.token=tk;
        this.iWantToBuy=IWantToBuy;
        this.cardLimit=CardLimit;
    }

    @Override
    public String toString() {
        return this.payWordCertif+" "+token.toString();
    }
}
