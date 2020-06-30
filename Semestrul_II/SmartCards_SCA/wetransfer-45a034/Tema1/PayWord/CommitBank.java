package PayWord;

import java.io.Serializable;

public class CommitBank implements Serializable{
    Commit commit;
    int[]charge;
    LastPayment whereToArrive;

    public CommitBank(Commit Comit,int []Charge,LastPayment WhereToArrive)
    {
        this.commit=Comit;
        this.charge=Charge;
        this.whereToArrive=WhereToArrive;
    }

}