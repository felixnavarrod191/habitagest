package com.habitagest.copropiedad;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** El coeficiente de copropiedad se guarda en porcentaje: 1,5 significa 1,5 %. */
@Document(collection = "apartamentos")
public class Apartamento {

    @Id
    private String id;
    private String torre;
    private String numero;
    private double coeficiente;
    private String propietarioId;

    public Apartamento() {
    }

    public Apartamento(String torre, String numero, double coeficiente) {
        this.torre = torre;
        this.numero = numero;
        this.coeficiente = coeficiente;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTorre() {
        return torre;
    }

    public String getNumero() {
        return numero;
    }

    public double getCoeficiente() {
        return coeficiente;
    }

    public String getPropietarioId() {
        return propietarioId;
    }

    public void setPropietarioId(String propietarioId) {
        this.propietarioId = propietarioId;
    }
}
