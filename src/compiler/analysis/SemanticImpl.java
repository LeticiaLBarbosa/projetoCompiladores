package compiler.analysis;

import compiler.core.*;
import compiler.exceptions.*;

import java.util.*;

/**
 * Created by Leticia on 24/09/2016.
 */

public class SemanticImpl{

    private HashMap<String,Variable> variables = new HashMap<String,Variable>();
    private List<Type> secondaryTypes = new ArrayList<Type>();
    private ArrayList<Function> functions = new ArrayList<Function>();
    private List<Variable> tempVariables = new ArrayList<Variable>();
    private Stack<ScopedEntity> scopeStack = new Stack<ScopedEntity>();
    Program jProgram = new Program();
    private static Map<String, List<String>> tiposCompativeis = new HashMap<String, List<String>>();
    private int forCounter = 0;
    private static SemanticImpl singleton;
    private Program javaProgram;
    public static SemanticImpl getInstance(){
        if(singleton ==  null){
            singleton = new SemanticImpl();
            initTypeCompatibility();
        }
        return singleton;
    }

    protected SemanticImpl(){
        javaProgram = new Program();
    }

    private final List<Type> BASIC_TYPES = new ArrayList<Type>(){{
        add(new Type("int") );
        add(new Type("float"));
        add(new Type("double"));
        add(new Type("long"));
        add(new Type("char"));
        add(new Type("void"));
        add(new Type("String"));
        add(new Type("boolean"));
        add(new Type("Object"));
        add(new Type("Integer"));
    }};

    private static void initTypeCompatibility(){
        List<String> doubleCompTypes = new ArrayList<String>();
        doubleCompTypes.add("int");
        doubleCompTypes.add("float");
        doubleCompTypes.add("double");
        doubleCompTypes.add("long");

        List<String> floatCompTypes = new ArrayList<String>();
        floatCompTypes.add("int");
        floatCompTypes.add("float");
        floatCompTypes.add("long");

        List<String> longCompTypes = new ArrayList<String>();
        longCompTypes.add("long");
        longCompTypes.add("int");

        List<String> intCompTypes = new ArrayList<String>();
        intCompTypes.add("int");

        List<String> stringCompTypes = new ArrayList<String>();
        stringCompTypes.add("int");
        stringCompTypes.add("double");
        stringCompTypes.add("long");
        stringCompTypes.add("float");
        stringCompTypes.add("char");
        stringCompTypes.add("null");

        tiposCompativeis.put("double", doubleCompTypes);
        tiposCompativeis.put("float", floatCompTypes);
        tiposCompativeis.put("long", longCompTypes);
        tiposCompativeis.put("int", intCompTypes);
        tiposCompativeis.put("string", stringCompTypes);
        tiposCompativeis.put("String", stringCompTypes);
    }

    private void createNewScope(ScopedEntity scope) {
        scopeStack.push(scope);
    }

    public void exitCurrentScope() throws InvalidFunctionException {
        ScopedEntity scoped = scopeStack.pop();
    }

    public void exitCurrentScope(Expression exp) throws InvalidFunctionException {
        ScopedEntity scoped = scopeStack.pop();
        if(scoped instanceof  Function){
            if(exp != null) {
                checkDeclaredAndReturnedType(scoped.getName(), ((Function) scoped).getDeclaredReturnType(), exp);
            }else{
                if(!((Function) scoped).getDeclaredReturnType().equals(new Type("void"))){
                    throw new InvalidFunctionException("The function "+scoped.getName() +" is missing a return statement in the end of it");
                }
            }
        }
    }

    public ScopedEntity getCurrentScope() {
        return scopeStack.peek();
    }

    public void addFunctionAndNewScope(Function f) {
        functions.add(f);
        createNewScope(f);
    }

    public boolean checkVariableExistence(String variableName) {
        if(!scopeStack.isEmpty() && getCurrentScope().getVariable().get(variableName) != null){
            return true;
        }else if(variables.get(variableName) != null){
            return true;
        }else{
            return false;
        }
    }

    public boolean checkVariableExistenceLocal(String variableName) {
        if(!scopeStack.isEmpty() && getCurrentScope().getVariable().get(variableName) != null){
            return true;
        }else{
            return false;
        }
    }

    public boolean checkVariableExistenceGlobal(String variableName) {
        return variables.get(variableName) != null ? true : false;
    }

    public boolean checkFunctionExistence(Function temp) throws InvalidFunctionException {
        for(Function fun : functions){
            if(fun.getName().equals(temp.getName())) {
                if(!fun.getDeclaredReturnType().getName().equals(temp.getDeclaredReturnType().getName())){
                    throw new InvalidFunctionException("ERROR: The function "+temp.getName()+" has already been declared with another return type!");
                }
                if(temp.equals(fun)){
                    throw new InvalidFunctionException("ERROR: The function " + temp.getName() + " has already been declared with the same parameters!");
                }

            }
        }
        return true;
    }

    public boolean verifyCall(String funcName, ArrayList<Expression> args) throws InvalidFunctionException {

        for (Function f : functions){
            if(f.getName().equals(funcName)){
                ArrayList<Parameter> p = (ArrayList<Parameter>) f.getParams();
                if(p.size() != args.size()){
                    throw new InvalidFunctionException("The method call of " + funcName + " has incorrect number of arguments");
                }
                for(int i = 0; i < p.size(); i++){
                    if(!p.get(i).getType().getName().equals(args.get(i).getType().getName())){
                        throw new InvalidFunctionException("The method call of " + funcName + " expects a " + p.get(i).getType().getName() + " but got the type " + args.get(i).getType().getName());
                    }
                }
                return true;
            }
        }
        throw new InvalidFunctionException("The function " + funcName + " may have not been declared");
    }


    public boolean checkValidExistingType(Type type) {
        return BASIC_TYPES.contains(type) || secondaryTypes.contains(type);
    }

    public boolean checkTypeCompatibility(Type leftType, Type rightType) {
        if (leftType.equals(rightType)){
            return true;
        } else {
            List<String> tipos = tiposCompativeis.get(leftType.getName());
            if(tipos == null) return false;
            return tipos.contains(rightType.getName());
        }
    }

    public void addType(Type type){
        if (type != null){
            if(type.getName().contains(".")){
                String[] typeNames = type.getName().split(".");
                String typeName = typeNames[typeNames.length-1];
                type.setName(typeName);
            }
        }

        if(!secondaryTypes.contains(type)){
            secondaryTypes.add(type);
            List<String> tipos = new ArrayList<String>();
            tipos.add(type.getName());
            tiposCompativeis.put(type.getName(), tipos);
        }
    }

    public boolean checkTypeOfAssignment(Variable variable, Expression exp) throws InvalidTypeAssignmentException{
        if (!variable.getType().equals(exp.getType())){
            throw new InvalidTypeAssignmentException("Alguma msg aqui");
        }
        return true;
    }

    public boolean isNumericExpression(Expression le, Expression re) throws InvalidOperationException{
        if((le != null && !le.isNumeric()) || (re != null && !re.isNumeric())){
            if(!isStringExpression(le, re)){
                throw new InvalidOperationException("Not a numeric or string expression");
            }
        }
        return true;
    }

    public boolean isStringExpression(Expression le, Expression re) throws InvalidOperationException {
        if((le != null && !le.isString()) && (re != null && !re.isString())){
            throw new InvalidOperationException("Not a string expression");
        }
        return true;
    }

    public boolean isRelationalExpression(Expression le, Expression re) throws InvalidOperationException {
        if(!le.getType().equals(re.getType())){
            throw new InvalidOperationException("Not a relational expression!");
        }
        return true;
    }

    /**
     * Valida uma variavel:
     * 	- se o tipo dela existe
     *  - se o nome ja esta em uso
     *
     * @param variable variable a ser validade
     *
     * @throws Exception
     */
    private void validateVariable(Variable variable) throws Exception{
        if (checkVariableExistenceLocal(variable.getIdentifier())){
            throw new InvalidVariableException("Name already exists");
        }
        if (!checkValidExistingType(variable.getType())){
            if(!variable.getValue().getType().getName().equals("null")){
                throw new InvalidTypeException("Type non existing");
            }
        }
    }

    private void validateVariableGlobal(Variable variable) throws Exception{
        if (checkVariableExistenceGlobal(variable.getIdentifier())){
            throw new InvalidVariableException("Name already exists");
        }
        if (!checkValidExistingType(variable.getType())){
            if(!variable.getValue().getType().getName().equals("null")) {
                throw new InvalidTypeException("Type non existing");
            }
        }
    }

    /**
     * Valida uma variavel.
     * Caso seja valida, adiciona a um mapa de variaveis sendo usadas.
     *
     * @param variable variable a ser validade e posteriormente adicionada.
     *
     * @throws Exception
     */
    private void addVariable(Variable variable) throws Exception{
        if(validateVariableNameForFunction(variable.getIdentifier())){
            throw new InvalidVariableException("Sorry this id is already used by a method");
        }
        if(scopeStack.isEmpty()){
            validateVariableGlobal(variable);
            variables.put(variable.getIdentifier(),variable);
        }else{
            validateVariable(variable);
            getCurrentScope().addVariable(variable);
        }

        if (variable.getValue() != null){
            checkVariableAttribution(variable.getIdentifier(), variable.getValue());
        }
    }

    private boolean validateVariableNameForFunction(String variableName) {
        for(Function f : functions){
            System.out.println(f.getName());
            if (f.getName().equals(variableName)){
                return true;
            }
        }
        return false;
    }

    public void addVariablesFromTempList(Type type) throws Exception{
        for (Variable variable : tempVariables) {
            variable.setType(type);
            addVariable(variable);
        }

        tempVariables = new ArrayList<Variable>();
    }

    public void validateFunction(String functionName, ArrayList<Parameter> params, Type declaredType) throws InvalidFunctionException, InvalidParameterException{
        if(declaredType == null){
            throw new InvalidFunctionException("The function "+functionName +" is missing either a declared return type or a return statement in the end of it");
        }
        Function temp = new Function(functionName, params);
        temp.setDeclaredReturnedType(declaredType);
        if(checkFunctionExistence(temp)){
            if(params != null){
                for(Parameter p : params){
                    variables.put(p.getIdentifier(), (Variable) p);
                }
                checkExistingParameter(params);
            }
            addFunctionAndNewScope(temp);
        }

    }

    private void hasReturn(Expression exp) throws InvalidFunctionException {
        if(!exp.getContext().equalsIgnoreCase("return")){
            throw new InvalidFunctionException("Missing a return statement");
        }
    }

    private void checkDeclaredAndReturnedType(String functionName,Type declaredType, Expression exp) throws InvalidFunctionException {
        if(!declaredType.equals(exp.getType()) && !declaredType.equals(new Type("void"))){
            throw new InvalidFunctionException("The function "+functionName+" didn't return the expected type: "+declaredType+". It returns "+exp.getType() + " instead");
        }
    }

    private void checkExistingParameter(ArrayList<Parameter> params) throws InvalidParameterException {
        for(int i=0; i<params.size();i++){
            for(int k=i+1;k<params.size();k++){
                if(params.get(i).getIdentifier().equals(params.get(k).getIdentifier())){
                    throw new InvalidParameterException("The parameter: "+params.get(k).getIdentifier()+ " has been previously defined.");
                }
            }
        }
    }

    //FIXME: INCOMPLETE
    public Expression getExpression(Expression le, Operation md, Expression re) throws InvalidTypeException{
        if (checkTypeCompatibility(le.getType(), re.getType()) || checkTypeCompatibility(re.getType(), le.getType())){
            switch (md) {
                case AND:
                    return new Expression(new Type("boolean"));
                case OR:
                    return new Expression(new Type("boolean"));
                case EQEQ:
                    return new Expression(new Type("boolean"));
                case GTEQ:
                    return new Expression(new Type("boolean"));
                case LTEQ:
                    return new Expression(new Type("boolean"));
                case LT:
                    return new Expression(new Type("boolean"));
                case GT:
                    return new Expression(new Type("boolean"));
                case NOTEQ:
                    return new Expression(new Type("boolean"));
                case NOT:
                    return new Expression(new Type("boolean"));
                case XOREQ:
                    return new Expression(new Type("boolean"));
                case XOR:
                    return new Expression(new Type("boolean"));
                case OROR:
                    return new Expression(new Type("boolean"));
                case ANDAND:
                    return new Expression(new Type("boolean"));
                case ANDEQ:
                    return new Expression(new Type("boolean"));
                case OREQ:
                    return new Expression(new Type("boolean"));
                case OROREQ:
                    return new Expression(new Type("boolean"));
                case MINUS:
                    return new Expression(getMajorType(le.getType(), re.getType()));
                case MULT:
                    return new Expression(getMajorType(le.getType(), re.getType()));
                case MOD:
                    return new Expression(getMajorType(le.getType(), re.getType()));
                case PLUS:
                    return new Expression(getMajorType(le.getType(), re.getType()));
                case DIV:
                    return new Expression(getMajorType(le.getType(), re.getType()));
                case DIVEQ:
                    return new Expression(getMajorType(le.getType(), re.getType()));
                case PLUSEQ:
                    return new Expression(getMajorType(le.getType(), re.getType()));
                case MINUSEQ:
                    return new Expression(getMajorType(le.getType(), re.getType()));
                case MULTEQ:
                    return new Expression(getMajorType(le.getType(), re.getType()));
            }
        }

        throw new InvalidTypeException("Not allowed!");
    }

    private Type getMajorType(Type type1, Type type2) {
        return tiposCompativeis.get(type1.getName()).contains(type2.getName()) ? type1: type2;
    }

    public void checkVariableAttribution(String id, Expression expression) throws InvalidVariableException, InvalidTypeException, InvalidFunctionException{
        if (!checkVariableExistence(id)){
            throw new InvalidVariableException("Variable doesn't exist");
        }
        if (!checkValidExistingType(expression.getType())){
            if(!expression.getType().getName().equals("null")) {
                throw new InvalidTypeException("Type non existing");
            }
        }
        Type identifierType = findVariableByIdentifier(id).getType();
        if (!checkTypeCompatibility(identifierType, expression.getType())){
            String exceptionMessage = String.format("Incompatible types! %s doesn't match %s", identifierType, expression.getType());
            throw new InvalidFunctionException(exceptionMessage);
        }
    }
    public void checkVariableAttribution(String id, String function) throws InvalidVariableException, InvalidTypeException, InvalidFunctionException{
        if (!checkVariableExistence(id)){
            throw new InvalidVariableException("Variable doesn't exist");
        }
        Type identifierType = findVariableByIdentifier(id).getType();

        for(Function f : functions){
            if(f.getName().equals(function)){
                if (!checkTypeCompatibility(identifierType, f.getDeclaredReturnType())){
                    String exceptionMessage = String.format("Incompatible types! %s doesn't match %s", identifierType, f.getDeclaredReturnType());
                    throw new InvalidFunctionException(exceptionMessage);
                }
            }
        }


    }


    public Variable findVariableByIdentifier(String variableName){
        if(!scopeStack.isEmpty() && getCurrentScope().getVariable().get(variableName) != null){
            return getCurrentScope().getVariable().get(variableName);
        }else{
            return variables.get(variableName);
        }

    }

    public void validateVariableName(String variableName) throws InvalidVariableException{
        if (!checkVariableExistence(variableName)){
            throw new InvalidVariableException("Variable doesn't exist");
        }
    }

    public void addSupertype(String className, String superClassName) throws InvalidTypeException{
        if (superClassName != null) {
            if (tiposCompativeis.containsKey(superClassName)){
                tiposCompativeis.get(superClassName).add(className);
                return;
            }

            throw new InvalidTypeException("Superclass doesn't exist");
        }
    }

	/* Auxiliary functions*/

    public void addVariableToTempList(Variable var){
        tempVariables.add(var);
    }

    /* FOR */
    public void createForScope(Variable var, Expression bexp, Expression aexp) throws InvalidTypeException{
        For f = new For("For" + forCounter++);
        for(Variable v: getCurrentScope().getVariable().values()){
            f.addVariable(v);
        }
        if(var != null){
            f.addVariable(var);
            getCurrentScope().getVariable().remove(var.getIdentifier());
        }
        if(bexp != null){
            if(!bexp.getType().getName().equals("boolean")){
                throw new InvalidTypeException("A expressão deveria ser boolean");
            }
        }
        if(aexp != null){
            if(aexp.getType().getName().equals("boolean")){
                throw new InvalidTypeException("A expressão deve ser aritmetica");
            }
        }
        scopeStack.push(f);
    }

}

