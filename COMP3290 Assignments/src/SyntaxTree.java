
import java.util.ArrayList;

public class SyntaxTree {

    private Node root;
    private Node currentNode;
    private ArrayList<Token> tokenBuffer;
    private Token currentToken;
    private CDScanner cdScanner;

    public SyntaxTree(CDScanner scanner){

        root = new Node("NPROG");
        currentNode = root;

        tokenBuffer = new ArrayList<>();
        cdScanner = scanner;

    }

    public void buildTree(){

        getNextToken();
        currentToken = tokenBuffer.get(0);
        if (currentToken.getTokNum() != 1){
            error();
            return;
        }
        root.setSymbolValue(currentToken.getLex());
        root.setLeftNode(globals());
        root.setMidNode(funcs());
        root.setRightNode(mainbody());

    }

    public void getNextToken(){
        tokenBuffer.add(cdScanner.nextToken());
    }

    public void error(){
        System.out.println("Error!"); // TODO: Error handling
    }



    // Grammar
    public Node globals(){
        Node node = new Node("NGLOB");
        node.setLeftNode(consts());
        node.setMidNode(types());
        node.setRightNode(arrays());

        return node;
    }

    public Node consts(){
        Node node
    }

    



    
    
}
