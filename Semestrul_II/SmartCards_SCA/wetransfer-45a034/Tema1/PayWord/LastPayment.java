package PayWord;

import java.io.Serializable;

public class LastPayment implements Serializable{
    String hash1;
    String hash2;
    String hash3;
    public LastPayment(String h1,String h2,String h3)
    {
        this.hash1=h1;
        this.hash2=h2;
        this.hash3=h3;
    }

    @Override
    public String toString() {
        return this.hash1+" "+this.hash2+" "+this.hash3;
    }
}
