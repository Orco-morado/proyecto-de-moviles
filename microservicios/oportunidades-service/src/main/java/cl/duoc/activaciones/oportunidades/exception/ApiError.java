package cl.duoc.activaciones.oportunidades.exception;

import java.util.List;

public class ApiError {

    private final int status;
    private final String error;
    private final List<String> mensajes;

    public ApiError(int status, String error, List<String> mensajes) {
        this.status = status;
        this.error = error;
        this.mensajes = mensajes;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public List<String> getMensajes() {
        return mensajes;
    }
}
