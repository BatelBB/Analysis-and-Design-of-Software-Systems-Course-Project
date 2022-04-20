package ServiceLayer;

public class Response<T> {

    private  String errorMessage;
    private T value;
    public Response(T value) { this.value= value;; }
    public Response(String msg)
    {
        this.errorMessage = msg;
    }
    public boolean ErrorOccured(){
        return errorMessage!=null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}