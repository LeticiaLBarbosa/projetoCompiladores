package compiler.main;

import compiler.generated.Parser;
import compiler.generated.Scanner;
import java_cup.runtime.Symbol;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {

    public static void main(String[] args) {
        String filePath = "examples/Test3.txt";
        Scanner scanner = null;
        try {
            scanner = new Scanner(new BufferedReader(new FileReader(filePath)));
        } catch (FileNotFoundException e1) {
            System.out.println(e1.getMessage());
        }
        Parser parser = new Parser(scanner);
        Symbol s = null;
        try {
            s = parser.parse();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Done!");
        System.out.println(s);
    }

}