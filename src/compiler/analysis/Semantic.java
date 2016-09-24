package compiler.analysis;

import compiler.core.Type;

/**
 * Created by Leticia on 24/09/2016.
 */

public interface Semantic {

    public boolean checkVariableExistence(String variableName);

    public boolean checkMethodExistence(String methodName);

    public boolean checkValidExistingType(Type type);

    public boolean checkTypeCompatibility(Type leftType, Type rightType);

}
