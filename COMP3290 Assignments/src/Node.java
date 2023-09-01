


public class Node {

    private String id, type;
    private Node leftNode, midNode, rightNode;
    private SymbolTable symbolTable;
    private String symbolValue;


    public Node(String id){

        this.id = id;
        this.leftNode = null;
        this.midNode = null;
        this.rightNode = null;

    }

    public Node(String id, Node leftNode, Node midNode, Node rightNode){

        this.id = id;
        this.leftNode = leftNode;
        this.midNode = midNode;
        this.rightNode = rightNode;

    }

    public Node(String id, Node leftNode, Node midNode, Node rightNode, SymbolTable symbolTable, String type){

        this.id = id;
        this.leftNode = leftNode;
        this.midNode = midNode;
        this.rightNode = rightNode;
        this.symbolTable = symbolTable;
        this.type = type;

    }


    public void setLeftNode(Node node){
        leftNode = node;
    }

    public void setMidNode(Node node){
        midNode = node;
    }

    public void setRightNode(Node node){
        rightNode = node;
    }

    public void setSymbolValue(String value){
        symbolValue = value;
    }

    
    
}
