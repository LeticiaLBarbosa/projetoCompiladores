package compiler.analysis;

import compiler.core.Register;

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
    private Map<String, Integer> functionsAddress;

    public CodeGenerator(){
        this.labels = 100;
        this.register = 0;
        this.registers = Register.values();
        this.functionsAddress = new HashMap<String, Integer>();
        assemblyCode = "100: LD SP, 4000\n";
    }


}
