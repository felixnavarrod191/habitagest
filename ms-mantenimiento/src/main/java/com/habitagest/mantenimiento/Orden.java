package com.habitagest.mantenimiento;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Una orden de mantenimiento con su flujo de estados (RN-09). Los valores son pesos colombianos. */
@Document(collection = "ordenes")
public class Orden {

    public static final String SOLICITADA = "SOLICITADA";
    public static final String APROBADA = "APROBADA";
    public static final String EN_EJECUCION = "EN_EJECUCION";
    public static final String COMPLETADA = "COMPLETADA";

    @Id
    private String id;
    private String descripcion;
    private String zona;
    private long costoEstimado;
    private Long costoFinal;
    private String proveedorId;
    private String estado;
    private String aprobadaPor;
    private LocalDateTime creada;
    private LocalDateTime completada;
    private List<Material> materiales = new ArrayList<>();
    private List<Cambio> historial = new ArrayList<>();

    public Orden() {
    }

    public Orden(String descripcion, String zona, long costoEstimado, String proveedorId, LocalDateTime creada) {
        this.descripcion = descripcion;
        this.zona = zona;
        this.costoEstimado = costoEstimado;
        this.proveedorId = proveedorId;
        this.creada = creada;
        this.estado = SOLICITADA;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getZona() {
        return zona;
    }

    public long getCostoEstimado() {
        return costoEstimado;
    }

    public Long getCostoFinal() {
        return costoFinal;
    }

    public void setCostoFinal(Long costoFinal) {
        this.costoFinal = costoFinal;
    }

    public String getProveedorId() {
        return proveedorId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getAprobadaPor() {
        return aprobadaPor;
    }

    public void setAprobadaPor(String aprobadaPor) {
        this.aprobadaPor = aprobadaPor;
    }

    public LocalDateTime getCreada() {
        return creada;
    }

    public LocalDateTime getCompletada() {
        return completada;
    }

    public void setCompletada(LocalDateTime completada) {
        this.completada = completada;
    }

    public List<Material> getMateriales() {
        return materiales;
    }

    public List<Cambio> getHistorial() {
        return historial;
    }
}
