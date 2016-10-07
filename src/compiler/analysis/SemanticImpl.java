package compiler.analysis;

import compiler.core.*;
import compiler.exceptions.*;

import java.util.*;

/**
 * Created by Leticia on 24/09/2016.
 */

public class SemanticImpl{

    private static Map<String, List<String>> tiposCompativeis = new HashMap<String, List<String>>();
    private HashMap<String,Variable> variables = new HashMap<String,Variable>();
    private List<Type> secondaryTypes = new ArrayList<Type>();
    private List<Variable> tempVariables = new ArrayList<Variable>();
    private ArrayList<Function> functions = new ArrayList<Function>();
    private Stack<ScopedEntity> scopeStack = new Stack<ScopedEntity>();
    private Stack<Integer> condLabel = new Stack<Integer>();
    private Program javaProgram;
    private static String currentOperator;
    private static SemanticImpl singleton;

    public int forCounter = 0;
    public Program jProgram = new Program();
    public static boolean contextFor;
    public static CodeGenerator codeGenerator;

    public static CodeGenerator getCodeGenerator() {
        return codeGenerator;
    }

    public static SemanticImpl getInstance(){
        if(singleton ==  null){
            singleton = new SemanticImpl();
            codeGenerator = new CodeGenerator();
            initTypeCompatibility();
            contextFor = false;
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
        intCompTypes.add("Integer");

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
        tiposCompativeis.put("Integer", intCompTypes);
        tiposCompativeis.put("string", stringCompTypes);
        tiposCompativeis.put("String", stringCompTypes);
    }

    private void createNewScope(ScopedEntity scope) {
        scopeStack.push(scope);
    }

    public void exitForCurrentScope(Expression aexp) throws InvalidFunctionException, InvalidOperationException, InvalidTypeException {
        if(aexp != null){
            String[] parts = aexp.getValue().split(" ");
            switch(Operation.valueOf(parts[1])) {
                case MINUSMINUS:
                    codeGenerator.generateLDCode(findVariableByIdentifier(parts[0]));
                    codeGenerator.generateSUBCode("1");
                    codeGenerator.generateSTCode(findVariableByIdentifier(parts[0]));
                    break;
                case PLUSPLUS:
                    codeGenerator.generateLDCode(findVariableByIdentifier(parts[0]));
                    codeGenerator.generateADDCode("1");
                    codeGenerator.generateSTCode(findVariableByIdentifier(parts[0]));
                    break;
                default:
                    break;

            }
        }
        if(condLabel!=null && !condLabel.isEmpty()){
            codeGenerator.generateBRCode(condLabel.pop());
        }

        String x = codeGenerator.getAssemblyCode().replace("forSTRINGCHAVEQUENAOVAIEXISTIRNOUTROCANTOTOP"+forCounter, ""+(codeGenerator.getLabels()+8));
        forCounter--;
        codeGenerator.setAssemblyCode(x);
        ScopedEntity scoped = scopeStack.pop();
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
                    throw new InvalidFunctionException("ERRO: A funcao "+scoped.getName() +" nao contem um retorno em seu fim.");
                }
            }
        }
    }

    public ScopedEntity getCurrentScope() {
        return scopeStack.peek();
    }

    public void addFunctionAndNewScope(Function f) throws Exception {
        functions.add(f);
        createNewScope(f);
        if(f.getParams() != null) {
            for (Parameter p : f.getParams()) {
                addVariable((Variable) p);
            }
        }
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
                    throw new InvalidFunctionException("ERRO: O metodo "+temp.getName()+" ja foi declarado com um tipo de retorno diferente!");
                }
                if(temp.equals(fun)){
                    throw new InvalidFunctionException("ERRO: O metodo " + temp.getName() + " ja foi declarado com esses mesmos parametros!");
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
                    throw new InvalidFunctionException("ERRO: O metodo chamado " + funcName + " tem a quantidade errada de argumentos");
                }
                for(int i = 0; i < p.size(); i++){
                    if(!p.get(i).getType().getName().equals(args.get(i).getType().getName())){
                        throw new InvalidFunctionException("ERRO: O metodo chamado " + funcName + " esperava o tipo " + p.get(i).getType().getName() + " mas recebeu o tipo " + args.get(i).getType().getName());
                    }
                }
                return true;
            }
        }
        throw new InvalidFunctionException("ERRO: O metodo " + funcName + " pode não ter sido declarado ainda!");
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
            throw new InvalidTypeAssignmentException("ERRO: O tipo "+variable.getType().getName() +" da variavel "
                    + variable.getIdentifier() + " nao corresponde ao tipo do seu valor (" + exp.getType().getName()+")");
        }
        return true;
    }

    public boolean isNumericExpression(Expression le, Expression re) throws InvalidOperationException{
        if((le != null && !le.isNumeric()) || (re != null && !re.isNumeric())){
            if(!isStringExpression(le, re)){
                throw new InvalidOperationException("ERRO: A expressao "+ le.getValue()+ " com tipo " + le.getType().getName() +
                        " e/ou a expressao " + re.getValue() + " com tipo "+ re.getType().getName()+" nao é expressao numerica ou string");
            }
        }
        return true;
    }

    public boolean isStringExpression(Expression le, Expression re) throws InvalidOperationException {
        if((le != null && !le.isString()) && (re != null && !re.isString())){
            throw new InvalidOperationException("ERRO: A expressao formada pela subexpressao de valor " + le.getValue() + " e tipo " +
                    le.getType().getName() + " e a subexpressao de valor " + le.getValue() + " e tipo " + le.getType().getName() +
                    " nao é uma 'string expression'!");
        }
        return true;
    }

    public boolean isRelationalExpression(Expression le, Expression re) throws InvalidOperationException {
        if(!le.getType().equals(re.getType())){
            throw new InvalidOperationException("ERRO: A expressao formada pelas subexpressoes de valor " + le.getValue() + " do tipo "
                    + le.getType().getName()+" e de valor " + re.getValue() + " do tipo " + re.getType().getName()+ " nao é uma expressao relacional!");
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
            throw new InvalidVariableException("ERRO: A variavel de nome " + variable.getIdentifier() + " e tipo " + variable.getType().getName() +
                    " ja existe!");
        }
        if (!checkValidExistingType(variable.getType())){
            if(!variable.getValue().getType().getName().equals("null")){
                throw new InvalidTypeException("ERRO: O tipo " + variable.getType().getName() + " da variavel "+ variable.getIdentifier()+
                        " nao existe!");
            }
        }
    }

    private void validateVariableGlobal(Variable variable) throws Exception{
        if (checkVariableExistenceGlobal(variable.getIdentifier())){
            throw new InvalidVariableException("ERRO: A variavel de nome " + variable.getIdentifier() + " e tipo " + variable.getType().getName() +
                    " ja existe!");
        }
        if (!checkValidExistingType(variable.getType())){
            if(!variable.getValue().getType().getName().equals("null")) {
                throw new InvalidTypeException("ERRO: O tipo " + variable.getType().getName() + " da variavel "+ variable.getIdentifier()+
                        " nao existe!");
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

    public String getFunctionType(String variableName) {
        for(Function f : functions){
            if (f.getName().equals(variableName)){
                return f.getDeclaredReturnType().getName();
            }
        }
        return null;
    }

    public void addVariablesFromTempList(Type type) throws Exception{
        for (Variable variable : tempVariables) {
            variable.setType(type);
            addVariable(variable);
        }

        tempVariables = new ArrayList<Variable>();
    }

    public void validateFunction(String functionName, ArrayList<Parameter> params, Type declaredType) throws Exception {
        if(declaredType == null){
            throw new InvalidFunctionException("ERRO: O metodo "+functionName +" esta sem declaracao do tipo de retorno, ou se possui, nao contem retorno no seu fim!");
        }
        Function temp = new Function(functionName, params);
        temp.setDeclaredReturnedType(declaredType);
        if(checkFunctionExistence(temp)){
            if(params != null){
                checkExistingParameter(params);
            }
            String keyFunc = functionName + " ";
            if(params != null) {
                for (Parameter p : params) {
                    keyFunc += p.getType().getName();
                }
            }
            codeGenerator.addFunctionAddress(keyFunc);
            addFunctionAndNewScope(temp);
        }

    }

    private void checkDeclaredAndReturnedType(String functionName,Type declaredType, Expression exp) throws InvalidFunctionException {
        if(!declaredType.equals(exp.getType()) && !declaredType.equals(new Type("void"))){
            throw new InvalidFunctionException("ERRO: O metodo "+functionName+" nao retorna o tipo esperado: "+declaredType+". Ao inves disso, retorna o tipo: "+exp.getType());
        }
    }

    private void checkExistingParameter(ArrayList<Parameter> params) throws InvalidParameterException {
        for(int i=0; i<params.size();i++){
            for(int k=i+1;k<params.size();k++){
                if(params.get(i).getIdentifier().equals(params.get(k).getIdentifier())){
                    throw new InvalidParameterException("ERRO: O parametro: "+params.get(k).getIdentifier()+ " ja foi definido.");
                }
            }
        }
    }

    //FIXME: INCOMPLETE
    public Expression getExpression(Expression le, Operation md, Expression re) throws InvalidTypeException, InvalidOperationException {
        Register r;

        if (re == null || checkTypeCompatibility(le.getType(), re.getType()) || checkTypeCompatibility(re.getType(), le.getType())){
            switch (md) {
                case AND:
                    return new Expression(new Type("boolean"));
                case OR:
                    return new Expression(new Type("boolean"));
                case GTEQ:
                    if(!contextFor){
                    codeGenerator.generateSUBCode();
                    codeGenerator.generateBLTZCode(3);
                    r = codeGenerator.generateLDCode(new Expression(new Type(
                            "boolean"), "1"));
                    codeGenerator.generateBRCode(2);
                    codeGenerator.generateLDCode(r, new Expression(new Type(
                            "boolean"), "0"));
                    }

                    return new Expression(new Type("boolean"), le.getValue()+" "+md+" "+re.getValue());
                case EQEQ:
                    if(!contextFor) {
                        codeGenerator.generateBEQCode(3);
                        r = codeGenerator.generateLDCode(new Expression(new Type(
                                "boolean"), "0"));
                        codeGenerator.generateBRCode(2);
                        codeGenerator.generateLDCode(r, new Expression(new Type(
                                "boolean"), "1"));
                    }
                    return new Expression(new Type("boolean"), le.getValue()+" "+md+" "+re.getValue());

                case LTEQ:
                    if(!contextFor) {
                        codeGenerator.generateSUBCode();
                        codeGenerator.generateBGTZCode(3);
                        r = codeGenerator.generateLDCode(new Expression(new Type(
                                "boolean"), "1"));
                        codeGenerator.generateBRCode(2);
                        codeGenerator.generateLDCode(r, new Expression(new Type(
                                "boolean"), "0"));
                    }
                    return new Expression(new Type("boolean"), le.getValue()+" "+md+" "+re.getValue());
                case LT:
                    if(!contextFor) {
                        codeGenerator.generateSUBCode();
                        if (le.getContext() == "for") {
                            codeGenerator.generateForCondition("BGEQZ", "forSTRINGCHAVEQUENAOVAIEXISTIRNOUTROCANTOTOP"+forCounter);
                        }
                        codeGenerator.generateBGEQZCode(3);
                        r = codeGenerator.generateLDCode(new Expression(new Type(
                                "boolean"), "1"));
                        codeGenerator.generateBRCode(2);
                        codeGenerator.generateLDCode(r, new Expression(new Type(
                                "boolean"), "0"));
                    }
                    return new Expression(new Type("boolean"), le.getValue()+" "+md+" "+re.getValue());
                case GT:
                    if(!contextFor) {
                        codeGenerator.generateSUBCode();
                        codeGenerator.generateBLEQZCode(3);
                        r = codeGenerator.generateLDCode(new Expression(new Type(
                                "boolean"), "1"));
                        codeGenerator.generateBRCode(2);
                        codeGenerator.generateLDCode(r, new Expression(new Type(
                                "boolean"), "0"));

                    }
                    return new Expression(new Type("boolean"), le.getValue()+" "+md+" "+re.getValue());
                case NOTEQ:
                    if(!contextFor) {
                        codeGenerator.generateBEQCode(3);
                        r = codeGenerator.generateLDCode(new Expression(new Type(
                                "boolean"), "1"));
                        codeGenerator.generateBRCode(2);
                        codeGenerator.generateLDCode(r, new Expression(new Type(
                                "boolean"), "0"));
                    }
                    return new Expression(new Type("boolean"), le.getValue()+" "+md+" "+re.getValue());
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
                    if(!contextFor) {
                        codeGenerator.generateSUBCode();
                    }
                    return new Expression(getMajorType(le.getType(), re.getType()));
                case MULT:
                    if(!contextFor) codeGenerator.generateMULCode();
                    return new Expression(getMajorType(le.getType(), re.getType()));
                case MOD:
                    if(!contextFor) codeGenerator.generateMODCode();
                    return new Expression(getMajorType(le.getType(), re.getType()));
                case PLUS:
                    if(!contextFor) codeGenerator.generateADDCode();
                    return new Expression(getMajorType(le.getType(), re.getType()), le.getValue()+" "+md+" "+re.getValue());
                case DIV:
                    if(!contextFor) codeGenerator.generateDIVCode();
                    return new Expression(getMajorType(le.getType(), re.getType()));
                case DIVEQ:
                    return new Expression(getMajorType(le.getType(), re.getType()));
                case PLUSEQ:
                    return new Expression(getMajorType(le.getType(), re.getType()));
                case MINUSEQ:
                    return new Expression(getMajorType(le.getType(), re.getType()));
                case MULTEQ:
                    return new Expression(getMajorType(le.getType(), re.getType()));
                case PLUSPLUS:
                    if(!contextFor) {
                        codeGenerator.generateADDCode("1");
                        codeGenerator.generateSTCode(le);
                    }
                    return new Expression(le.getType(), le.getValue()+" "+md);
                case MINUSMINUS:
                    if(!contextFor) {
                        codeGenerator.generateSUBCode("1");
                        codeGenerator.generateSTCode(le);

                    }
                    return new Expression(le.getType(), le.getValue()+" "+md);
                default:
                    throw new InvalidOperationException("ERRO: A operacao '"+ md+ "' nao existe!");

            }
        }

        throw new InvalidTypeException("ERRO: Operacao formada pela expressao '"+le.getValue()+" "+md+" " +re.getValue() +"' nao é permitida!");
    }

    private Type getMajorType(Type type1, Type type2) {
        return tiposCompativeis.get(type1.getName()).contains(type2.getName()) ? type1: type2;
    }

    public void checkVariableAttribution(String id, Expression expression) throws InvalidVariableException, InvalidTypeException, InvalidFunctionException{
        if (!checkVariableExistence(id)){
            throw new InvalidVariableException("ERRO: A variavel chamada " +id+ " e com valor "+ expression.getValue()+" nao existe!");
        }
        if (!checkValidExistingType(expression.getType())){
            if(!expression.getType().getName().equals("null")) {
                throw new InvalidTypeException("ERRO: O tipo " + expression.getType().getName()+" atribuido a variavel "+ id + " nao existe!");
            }
        }
        Type identifierType = findVariableByIdentifier(id).getType();
        if (!checkTypeCompatibility(identifierType, expression.getType())){
            String exceptionMessage = String.format("ERRO: Tipos incompativeis! %s nao e  compativel com %s", identifierType, expression.getType());
            throw new InvalidFunctionException(exceptionMessage);
        }
    }

    public void checkVariableAttribution(String id, String function) throws InvalidVariableException, InvalidTypeException, InvalidFunctionException{
        if (!checkVariableExistence(id)){
            throw new InvalidVariableException("ERRO: A variavel chamada " +id+ " atribuida a funcao "+ function+" nao existe!" );
        }
        Type identifierType = findVariableByIdentifier(id).getType();

        for(Function f : functions){
            if(f.getName().equals(function)){
                if (!checkTypeCompatibility(identifierType, f.getDeclaredReturnType())){
                    String exceptionMessage = String.format("ERRO: Tipos incompativeis! %s nao é compativel com %s", identifierType, f.getDeclaredReturnType());
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
            throw new InvalidVariableException("ERRO: A variavel chamada " + variableName + " nao existe!");
        }
    }

    public void addSupertype(String className, String superClassName) throws InvalidTypeException{
        if (superClassName != null) {
            if (tiposCompativeis.containsKey(superClassName)){
                tiposCompativeis.get(superClassName).add(className);
                return;
            }

            throw new InvalidTypeException("ERRO: A super classe " + superClassName + "nao existe!");
        }
    }

    public void addVariableToTempList(Variable var){
        tempVariables.add(var);
    }

    public void createForScope(Variable var, Expression bexp, Expression aexp) throws InvalidTypeException, InvalidOperationException {
        For f = new For("For");
        for(Variable v: getCurrentScope().getVariable().values()){
            f.addVariable(v);
        }
        if(var != null){
            f.addVariable(var);

            codeGenerator.generateLDCode(var.getValue());
            codeGenerator.generateSTCode(var);

        }
        if(bexp != null){
            if(!bexp.getType().getName().equals("boolean")){
                throw new InvalidTypeException("ERRO: A expressão com valor "+bexp.getValue()+" deveria ser boolean, porem é do tipo "+bexp.getType().getName());
            }
            String[] parts = bexp.getValue().split(" ");

            codeGenerator.generateLDCode(findVariableByIdentifier(parts[0]));
            condLabel.push(codeGenerator.getLabels());
            codeGenerator.generateLDCode(new Expression(new Type("int") ,parts[2]));
            switch(Operation.valueOf(parts[1])){
                case GT:
                    codeGenerator.generateSUBCode();
                    codeGenerator.generateForCondition("BLEQZ", "forSTRINGCHAVEQUENAOVAIEXISTIRNOUTROCANTOTOP"+forCounter);
                    break;
                case LTEQ:
                    codeGenerator.generateSUBCode();
                    codeGenerator.generateForCondition("BGTZ", "forSTRINGCHAVEQUENAOVAIEXISTIRNOUTROCANTOTOP"+forCounter);
                    break;
                case LT:
                    codeGenerator.generateSUBCode();
                    codeGenerator.generateForCondition("BGEQZ", "forSTRINGCHAVEQUENAOVAIEXISTIRNOUTROCANTOTOP"+forCounter);
                    break;
                case GTEQ:
                    codeGenerator.generateSUBCode();
                    codeGenerator.generateForCondition("BLTZ", "forSTRINGCHAVEQUENAOVAIEXISTIRNOUTROCANTOTOP"+forCounter);
                    break;
                case EQEQ:
                    codeGenerator.generateForCondition("BNEQ", "forSTRINGCHAVEQUENAOVAIEXISTIRNOUTROCANTOTOP"+forCounter);
                    break;
                case NOTEQ:
                    codeGenerator.generateSUBCode();
                    codeGenerator.generateForCondition("BEQ", "forSTRINGCHAVEQUENAOVAIEXISTIRNOUTROCANTOTOP"+forCounter);
                    break;
            }


            contextFor = false;
        }

        if(aexp != null){
            if(aexp.getType().getName().equals("boolean")){
                throw new InvalidTypeException("ERRO: A expressão com valor "+aexp.getValue()+" deveria ser aritimetica, porem é uma expressao booleana");
            }
        }

        if(var != null){
            getCurrentScope().getVariable().remove(var.getIdentifier());
        }

        scopeStack.push(f);

    }
}