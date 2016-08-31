package compiler.main;

import compiler.generated.*;
import java_cup.runtime.Symbol;

import java.io.FileReader;

public class Main {

    public static void main(String[] args) {
        String prog = "examples/Test.txt";

        try{
            Scanner scanner = new Scanner(new FileReader(prog));
            System.out.println(">> Successful Lexical Analysis");
            Parser p = new Parser(scanner);
            System.out.println(">> Passou aqui");
            Object result = p.parse().value;
            System.out.println(">> Passou aqui2");
            Symbol s = p.parse();
            System.out.println(">> Successful Sintatic Analysis");

            if (s.toString().equals("#0"))
                System.out.println("> SUCCESSFULL COMPILATION");
            else
                System.out.println(s);

        }catch(Exception e){
            System.err.println("Processo de Compilacao Falhou!");
            System.err.println(e.getMessage());
        }
    }

}