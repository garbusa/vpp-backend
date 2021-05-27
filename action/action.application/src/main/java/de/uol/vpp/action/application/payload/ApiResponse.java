package de.uol.vpp.action.application.payload;

/**
 * Antwortobjekt zwischen Backend -> Frontend.
 * Beinhaltet die DTOs und Informationen über das Ergebnis
 */
public class ApiResponse {
    private Boolean success;
    private Boolean returnBack;
    private String message;
    private Object data;

    /**
     * Konstruktor
     *
     * @param success    Ist Anfrage erfolgreich?
     * @param returnBack Zurück zur vorherigen Seite?
     * @param message    Nachricht
     * @param data       DTO
     */
    public ApiResponse(Boolean success, Boolean returnBack, String message, Object data) {
        this.success = success;
        this.returnBack = returnBack;
        this.message = message;
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getReturnBack() {
        return returnBack;
    }

    public void setReturnBack(Boolean returnBack) {
        this.returnBack = returnBack;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}