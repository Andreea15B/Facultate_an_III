

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;


public class ReedSolomon {

    public ReedSolomon(){

    }

    public void solveRS(){
        String s=new String();
        System.out.print("Introduceti mesajul: ");
        Scanner in = new Scanner(System.in);
        s=in.nextLine();

        String p=new String();
        System.out.print("Introduceti p: ");
        p=in.nextLine();

        BigInteger number = new BigInteger(s);
        BigInteger power = new BigInteger(p);

        //Random rnd = new Random();
        //BigInteger power = new BigInteger(4,1,rnd);
        //System.out.println("P: "+power);

        ArrayList rest ;
        rest=buildPolynom(number,power);
        for(int i=0;i<rest.size()-1;i++)
            System.out.print(rest.get(i)+"*x^"+(rest.size()-i)+"+");
        System.out.print(rest.get(rest.size()-1)+"*x^1");
        System.out.println("");

        ArrayList encode = encoding(rest,power);
        System.out.print("Y(");
        for(int i=0;i<encode.size()-1;i++)
            System.out.print(encode.get(i)+",");
        System.out.print(encode.get(encode.size()-1)+")");
        System.out.println("");

        ArrayList decode ;
        decoding(encode,power);

    }
    public ArrayList<BigInteger> buildPolynom(BigInteger m, BigInteger p){
        ArrayList<BigInteger> rest = new ArrayList<>();
        do{
            BigInteger bi[] = m.divideAndRemainder(p);
            BigInteger r=bi[1];
            BigInteger c=bi[0];
            rest.add(r);
            m=c;

        }while(m.compareTo(BigInteger.ZERO)!=0);
        Collections.reverse(rest);
        return rest;
    }
    public BigInteger horner(ArrayList<BigInteger> m,BigInteger x){
        BigInteger suma= x.multiply(m.get(0));
        for(int i = 1;i<m.size();i++){
            suma=suma.add(m.get(i));
            suma=suma.multiply(x);
        }
        suma = suma.add(BigInteger.ZERO);
        return suma;
    }

    public ArrayList<BigInteger> encoding(ArrayList<BigInteger> m,BigInteger p){
        ArrayList encodeArray=new ArrayList<>();
        int k= m.size()+1;
        for(int i=1;i<=k+2;i++){
            BigInteger x = new BigInteger(Integer.toString(i));
            BigInteger bi1 = horner(m,x);
            BigInteger bi2[] = bi1.divideAndRemainder(p);
            encodeArray.add(bi2[1]);
        }
        return encodeArray;
    }


    public BigInteger calculFc(ArrayList<BigInteger> encode,ArrayList<BigInteger> a,BigInteger p){

        BigInteger fc = BigInteger.ZERO;
        for(int i=0;i<a.size();i++){
            BigInteger zi= encode.get(a.get(i).intValue()-1);
            BigInteger produs= BigInteger.ONE;
            for(int j=0;j<a.size();j++)
                if(j!=i){
                    BigInteger invers = a.get(j).add(a.get(i).negate()).modInverse(p);
                    BigInteger produs1 = a.get(j).multiply(invers).mod(p);
                    produs=produs.multiply(produs1);
                    produs=produs.mod(p);
                }
            fc=fc.add(zi.multiply(produs));
        }
        fc=fc.mod(p);
        return fc;
    }

    public BigInteger initY(ArrayList<BigInteger> encode,ArrayList<BigInteger> a,BigInteger p,BigInteger x){
        BigInteger rez = BigInteger.ZERO;
        for(int i=0;i<a.size();i++){
            BigInteger zi= encode.get(a.get(i).intValue()-1);
            BigInteger produs= BigInteger.ONE;
            for(int j=0;j<a.size();j++)
                if(j!=i){
                    BigInteger invers = a.get(i).add(a.get(j).negate()).modInverse(p);
                    BigInteger xMinusJ= x.add(a.get(j).negate());
                    BigInteger produs1 = xMinusJ.multiply(invers).mod(p);
                    produs=produs.multiply(produs1);
                    produs=produs.mod(p);
                }
            rez=rez.add(zi.multiply(produs));
        }
        rez=rez.mod(p);
        return rez;
    }

    public ArrayList<BigInteger> calculPx(ArrayList<BigInteger> encode,ArrayList<BigInteger> a,BigInteger p){
        ArrayList<BigInteger> coef = new ArrayList<>();
        for(int i=0;i<a.size()-1;i++)
            coef.add(BigInteger.ZERO);
        for(int i=0;i<a.size();i++){
            BigInteger zi=encode.get(a.get(i).intValue()-1);
            BigInteger produs = BigInteger.ONE;
            BigInteger sum=BigInteger.ZERO;
            for(int j=0;j<a.size();j++)
                if(j!=i){
                    BigInteger invers = a.get(i).add(a.get(j).negate()).modInverse(p);
                    produs=produs.multiply(invers);
                    sum=sum.add(a.get(j));
                }
            produs=produs.multiply(zi);
            coef.set(0, coef.get(0).add(produs));
            coef.set(1, coef.get(1).add(produs.multiply(sum)));
        }
        for(int i=0;i<coef.size();i++)
            coef.set(i, coef.get(i).mod(p));
        return coef;
    }

    public void decoding(ArrayList<BigInteger> encode,BigInteger p){

        ArrayList<BigInteger> coeficienti =new ArrayList<>();
        ArrayList<BigInteger> a ;
        int n,k;

        Random rnd = new Random();
     /* int pozitie=1+rnd.nextInt(encode.size()-1);
        System.out.println("Pozitia: "+pozitie);
        BigInteger x = new BigInteger(4,rnd);
        BigInteger bi[] = x.divideAndRemainder(p);
        encode.set(pozitie, bi[1]);*/

        int pozitie;
        encode.set(1,new BigInteger("2"));

        System.out.print("Noul Y:(");
        for(int i=0;i<encode.size()-1;i++)
            System.out.print(encode.get(i)+",");
        System.out.print(encode.get(encode.size()-1)+")");
        System.out.println("");

        n= encode.size();
        k=n-2;

        BigInteger fc = BigInteger.ONE;
        do{
            a=new ArrayList<>();
            for(int i=1;i<=n;i++)
                a.add(new BigInteger(Integer.toString(i)));

            for(int i=1;i<=2;i++){
                pozitie=rnd.nextInt(n-1);
                a.remove(a.get(pozitie));
            }
            // a.remove(1);
            // a.remove(1);

            System.out.print("A: ");
            for(int i=0;i<a.size();i++)
                System.out.print(a.get(i)+" ");
            System.out.println("");

            fc= calculFc(encode,a,p);

            System.out.println("Coeficientul liber: "+fc);
        }while(!fc.equals(BigInteger.ZERO));

        for(int i=1;i<=n;i++){
            BigInteger x=initY(encode,a,p,new BigInteger(Integer.toString(i)));
            System.out.print(x+" ");
        }
        System.out.println("");

        coeficienti = calculPx(encode,a,p);
        System.out.print("Coeficienti: ");
        for(int i=0;i<coeficienti.size();i++)
            System.out.print(coeficienti.get(i)+" ");
        System.out.println("");
    }



}
