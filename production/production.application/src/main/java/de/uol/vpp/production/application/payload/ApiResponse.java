package de.uol.vpp.production.application.payload;

/**
 * Antwortobjekt für REST-Anfragen
 * Beinhaltet Status ob Anfrage erfolgreich war, eine Nachricht und ggf. angefragte Daten
 */
public class ApiResponse {
    private Boolean success;
    private Boolean returnBack;
    private String message;
    private Object data;

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