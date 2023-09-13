


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
        this.symbolValue = "";

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

    public Node getLeftNode(){
        return leftNode;
    }

    public Node getMidNode(){
        return midNode;
    }

    public Node getRightNode(){
        return rightNode;
    }

    public void setSymbolValue(String value){
        symbolValue = value;
    }

    public String getSymbolVaue(){
        return symbolValue;
    }

    public void setId(String id){
        this.id = id;
    }

    public Integer printTreeHelper(int current_col){

        String string = this.id;
        int col = current_col + 1;
        if ( col > 9 ){
            string += "\n";
            col = 0;
        }

        if (!this.symbolValue.equals("")){
            string +=  this.getSymbolVaue() + " ";
            col++;
        
            for ( int i = 0; i < string.length() % 7; i++){
                string += " ";
            }
            if ( col > 9 ){
                string += "\n";
                col = 0;
            }
        }

        System.out.print(string);

        if (this.getLeftNode() != null){
            col = this.getLeftNode().printTreeHelper(col);
        }
        if (this.getMidNode() != null){
            col = this.getMidNode().printTreeHelper(col);
        }
        if (this.getRightNode() != null){
            col = this.getRightNode().printTreeHelper(col);
        }

        return col;
        
    }

    public void printTree(){

       printTreeHelper(0);
    }


    
    
}
