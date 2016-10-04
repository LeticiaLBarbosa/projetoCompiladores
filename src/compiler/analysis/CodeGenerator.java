package compiler.analysis;

import compiler.core.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rodrigofarias on 9/27/16.
 */

public class CodeGenerator {

    private int labels;
    private int register;
    private String assemblyCode;
    private Register[] registers;
    private Map<String, Integer> functionAddres;


    public CodeGenerator() {
        this.labels = 100;
        this.register = -1;
        this.registers = Register.values();
        this.assemblyCode = initAssemblyCode();
        this.functionAddres = new HashMap<String, Integer>();
    }

    public static Object getInstance() {
        // TODO Auto-generated method stub
        return null;
    }
    private String initAssemblyCode() {
        return "100: LD SP, 4000\n";
    }

    public void assignmentDeclaration(Variable var, Object obj) {
        if (obj instanceof String) {
            System.out.println("chegou no assignmenaisjdsjdt");
            //generateLDCode((Expression) obj);
            generateSTCode(var);
        }
        if (obj instanceof Function) {
            Function f = (Function) obj;
            generateLDCode(new Expression(f.getName()));
            generateSTCode(var);
        }
        System.out.println(assemblyCode);
    }

    public void generateADDCode() {
        labels += 8;

        Register one = registers[register - 1];
        Register two = allocateRegister();

        register++;
        Register result = allocateRegister();
        addCode(labels + ": ADD " + result + ", " + one + ", " + two);
    }

    public void generateADDCode(Register result, Register one, Register two) {
        labels += 8;
        addCode(labels + ": ADD " + result + ", " + one + ", " + two);
    }

    public void generateADDCode(Register result, Register one, Expression exp) {
        labels += 8;
        addCode(labels + ": ADD " + result + ", " + one + ", #" + exp.getAssemblyValue());
    }

    public void generateSUBCode() {
        labels += 8;
        Register one = registers[register - 1];
        Register two = allocateRegister();

        register++;
        Register result = allocateRegister();
        addCode(labels + ": SUB " + result + ", " + one + ", " + two);
    }

    public void generateSUBCode(Register result, Register one, Expression exp) {
        labels += 8;
        addCode(labels + ": SUB " + result + ", " + one + ", #" + exp.getAssemblyValue());
    }

    public void generateSUBCode(Register result, Register one, Register two) {
        labels += 8;
        addCode(labels + ": SUB " + result + ", " + one + ", " + two);
    }

    public void generateMULCode() {
        labels += 8;

        Register one = registers[register - 1];
        Register two = allocateRegister();

        register++;
        Register result = allocateRegister();
        addCode(labels + ": MUL " + result + ", " + one + ", " + two);
    }

    public void generateDIVCode() {
        labels += 8;

        Register one = registers[register - 1];
        Register two = allocateRegister();

        register++;
        Register result = allocateRegister();
        addCode(labels + ": DIV " + result + ", " + one + ", " + two);
    }

    public void generateMULCode(Register result, Register one, Expression exp) {
        labels += 8;
        addCode(labels + ": MUL " + result + ", " + one + ", #" + exp.getValue());
    }

    public void generateNOTCode() {
        generateBEQZCode(4);
        generateMULCode(Register.R2, Register.R1, new Expression(new Type("int"), "-1"));
        generateADDCode(Register.R1, Register.R1, Register.R2);
        generateBRCode(2);
        generateADDCode(Register.R1, Register.R1, new Expression(new Type("int"), "1"));
    }

    public void generateBEQZCode(int br) {
        labels += 8;
        int jump = (br * 8) + labels;

        Register current = allocateRegister();
        addCode(labels + ": BEQZ " + current + ", " + jump);
    }

    public void generateBNEQZCode(int br) {
        labels += 8;
        int jump = (br * 8) + labels;

        Register current = allocateRegister();
        addCode(labels + ": BNEQZ " + current + ", " + jump);
    }

    public void generateBGEQZCode(int br) {
        labels += 8;
        int jump = (br * 8) + labels;

        Register current = allocateRegister();
        addCode(labels + ": BGEQZ " + current + ", " + jump);
    }

    public void generateBLEQZCode(int br) {
        labels += 8;
        int jump = (br * 8) + labels;

        Register current = allocateRegister();
        addCode(labels + ": BLEQZ " + current + ", " + jump);
    }

    public void generateBLTZCode(int br) {
        labels += 8;
        int jump = (br * 8) + labels;

        Register current = allocateRegister();
        addCode(labels + ": BLTZ " + current + ", " + jump);
    }

    public void generateBGTZCode(int br) {
        labels += 8;
        int jump = (br * 8) + labels;

        Register current = allocateRegister();
        addCode(labels + ": BGTZ " + current + ", " + jump);
    }

    public void generateBRCode(int br) {
        labels += 8;
        int jump = (br * 8) + labels;
        addCode(labels + ": BR " + jump);
    }

    public Register generateLDCode(Expression expression) {
        System.out.println("chamou");
        Register r = null;
        System.out.println(expression.getAssemblyValue()+ "DEIDEJOIJEA");
        System.out.println(expression.getAssemblyValue());
        if (expression.getAssemblyValue() != null) {
			System.out.println("Register before ld: "+register);
            register++;
            labels += 8;
            r = allocateRegister();
            addCode(labels + ": LD " + r + ", " + expression.getAssemblyValue());
        }
        return r;
    }

    public Register generateLDCode(Register r, Expression expression) {
        if (expression.getAssemblyValue() != null) {
            labels += 8;
            addCode(labels + ": LD " + r + ", " + expression.getAssemblyValue());
        }
        return r;
    }

    public void generateSTCode(Variable variable) {
        labels += 8;
        addCode(labels + ": ST " + variable.getIdentifier() + ", " + allocateRegister());
        this.register = -1;
    }

    public void generateSTCode(Register one, Expression exp) {
        labels += 8;
        addCode(labels + ": ST " + one + ", " + exp.getAssemblyValue());
        this.register = -1;
    }

    public void generateSTCode(Expression exp) {
        labels += 8;
        addCode(labels + ": ST " + exp.getAssemblyValue() + ", " + allocateRegister());
        this.register = -1;
    }

    public void addCode(String assemblyString) {
        assemblyCode += assemblyString + "\n";
    }

    public void generateCallFunction(String functionName) {
        Expression blockSize = new Expression("size");
        Integer addressFunction = functionAddres.get(functionName);

        generateADDCode(Register.SP, Register.SP, blockSize);

        int jump = (3 * 8) + labels;
        generateSTCode(Register._SP, new Expression(new Type("int"), Integer.toString(jump)));
        generateBRCode(addressFunction);
        generateSUBCode(Register._SP, Register.SP, blockSize);
    }

    public void generateBRCode(Integer address) {
        labels += 8;
        addCode(labels + ": BR " + address);
    }

    public void generateBRCode(Register register) {
        labels += 8;
        addCode(labels + ": BR " + register);
    }

    public void generateHalt() {
        labels += 8;
        addCode(labels + ": halt");
    }

    public String getAssemblyCode() {
        return assemblyCode;
    }

    public void addFunctionAddress(String name) {
        labels += 300;
        functionAddres.put(name, labels + 8);
        addCode("\n");
    }

    public void StorageReturnedType(Function function, Expression returnedExpression) {
        if (returnedExpression.getValue() != null) {
            generateLDCode(returnedExpression);
            generateSTCode(new Expression(function.getName()));

            if(function.getName().equals("main")){
                generateHalt();
            } else {
                generateBRCode(Register._SP);
            }
        } else {
            if (returnedExpression.getValue() != null) {
                generateLDCode(returnedExpression);
                generateSTCode(new Expression(function.getName()));
            } else {
                generateSTCode(new Expression(function.getName()));
            }
            generateBRCode(Register._SP);
        }
    }

    public Register allocateRegister(){
        try {
            Register allocated = registers[register];
            return allocated;
        } catch (Exception e) {
            register++;
            return allocateRegister();
        }
    }

    public int getLabels(){
        return labels;
    }

    public void generateFinalAssemblyCode() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("generated-assembly.txt")));
        writer.write(assemblyCode);
        writer.close();
    }

}