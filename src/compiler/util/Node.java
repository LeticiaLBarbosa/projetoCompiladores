package compiler.util;

import java.util.ArrayList;
import java.util.List;

public class Node {

    List<Node> listaDeFilhos = new ArrayList<Node>();
    private String value;
    private String tipo;
    private String identifier;

    public Node(String value, String tipo, String indentifier) {
        this.setValue(value);
        this.setTipo(tipo);
        this.setIdentifier(indentifier);
    }

    public void addNode(Node n) {
        listaDeFilhos.add(n);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return getValue();
    }

}