package edu.coderhouse.Jpa;

public class ErrorResponse {
    private String message;

    // Constructor público
    public ErrorResponse(String message) {
        this.message = message;
    }

    // Getter y Setter
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
