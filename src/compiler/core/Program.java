package compiler.core;

import java.util.HashMap;

/**
 *
 * Created by rodrigofarias on 9/23/16.
 */
public class Program extends ScopedEntity {
    HashMap<String, Function> functions;

    public Program() {
        super("program");
        functions = new HashMap<String, Function>();
    }

    public HashMap<String, Function> getFunctions() {
        return functions;
    }

    public void addFunction(Function f) {
        functions.put(f.getName(), f);
    }

}
