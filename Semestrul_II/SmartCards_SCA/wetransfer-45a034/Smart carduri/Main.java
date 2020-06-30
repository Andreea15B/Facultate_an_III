import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception{
        Terminal terminal = new Terminal();
        terminal.test();

        System.out.println("Teste default");
        System.out.println("Balans initial: " + terminal.balans());
        System.out.println("Creditare 10000: " + terminal.adauga(10000));
        System.out.println("Debitare 25: " + terminal.retrage(25, true));
        System.out.println("Balans asteptat: 9975  |  Balans actual: " + terminal.balans());
        System.out.println("Debitare 60 cu pin corect: " + terminal.retrage(60, true));
        System.out.println("Balans asteptat: 9915  |  Balans actual: " + terminal.balans());
        System.out.println("Debitare 60 cu pin incorect: " + terminal.retrage(60, false));
        System.out.println("Balans asteptat: 9915  |  Balans actual: " + terminal.balans());
        System.out.println("Debitare 120 cu pin corect: " + terminal.retrage(120, true));
        System.out.println("Balans asteptat: 9795  |  Balans actual: " + terminal.balans());
        System.out.println("Debitare 120 cu pin incorect: " + terminal.retrage(120, false));
        System.out.println("Balans asteptat: 9795  |  Balans actual: " + terminal.balans());
        terminal.afisareCVM();

        Scanner keyboard = new Scanner(System.in);
        while(true){
            System.out.println(">>Valoarea: ");
            int valoare = keyboard.nextInt();

            System.out.println(">>pinCorect:");
            try {
                boolean corect = keyboard.nextBoolean();
                System.out.println("Debitare " + valoare+ ": " + terminal.retrage(valoare, corect));
            }catch(Exception ignored){};

            System.out.println("Balans: " + terminal.balans() + "\n");
        }
    }
}
