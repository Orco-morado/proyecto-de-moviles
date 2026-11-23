package cl.duoc.activaciones.oportunidades.dto;

import cl.duoc.activaciones.oportunidades.model.EstadoOportunidad;
import cl.duoc.activaciones.oportunidades.model.Prioridad;
import cl.duoc.activaciones.oportunidades.model.TipoEvento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OportunidadDto {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "Selecciona el tipo de evento")
    private TipoEvento tipo;

    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;

    @NotBlank(message = "La fuente de información es obligatoria")
    private String fuenteInformacion;

    @NotNull(message = "La fecha de detección es obligatoria")
    private Long fechaDeteccion;

    private Long responsableId;

    @NotNull(message = "La prioridad es obligatoria")
    private Prioridad prioridad;

    @NotBlank(message = "El potencial comercial es obligatorio")
    private String potencialComercial;

    private EstadoOportunidad estado;

    private Long eventoId;

    private Long fechaActualizacion;

    private String observaciones;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public void setTipo(TipoEvento tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFuenteInformacion() {
        return fuenteInformacion;
    }

    public void setFuenteInformacion(String fuenteInformacion) {
        this.fuenteInformacion = fuenteInformacion;
    }

    public Long getFechaDeteccion() {
        return fechaDeteccion;
    }

    public void setFechaDeteccion(Long fechaDeteccion) {
        this.fechaDeteccion = fechaDeteccion;
    }

    public Long getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(Long responsableId) {
        this.responsableId = responsableId;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public String getPotencialComercial() {
        return potencialComercial;
    }

    public void setPotencialComercial(String potencialComercial) {
        this.potencialComercial = potencialComercial;
    }

    public EstadoOportunidad getEstado() {
        return estado;
    }

    public void setEstado(EstadoOportunidad estado) {
        this.estado = estado;
    }

    public Long getEventoId() {
        return eventoId;
    }

    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }

    public Long getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Long fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
