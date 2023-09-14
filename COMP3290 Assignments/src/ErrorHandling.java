import java.util.LinkedList;

public class ErrorHandling {

    LinkedList<String> errorList;

    private ErrorHandling(){
        this.errorList = new LinkedList<String>();
    }

    public static ErrorHandling getInstance(){
        ErrorHandling eList = new ErrorHandling();
        return eList;
    }

    public void addErrorToList(String error){
        errorList.add(error);
    }

    public LinkedList<String> getErrorList(){
        return errorList;
    }

    public boolean is_Empty(){
        return errorList.isEmpty();
    }

    public int size_is(){
        return errorList.size();
    }

    
}
