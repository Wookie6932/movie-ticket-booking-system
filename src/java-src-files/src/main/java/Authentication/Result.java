package Authentication;
 
public class Result {
    public enum Status {
        SUCCESS,
        VALIDATION_ERROR,
        DUPLICATE_ERROR,
        DATABASE_ERROR
    }
 
    private final Status status;
    private final String message;
 
    public Result(Status status, String message) {
        this.status = status;
        this.message = message;
    }
 
    public Status getStatus() {
        return status;
    }
 
    public String getMessage() {
        return message;
    }
 
    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }
}