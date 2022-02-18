/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author Jesus González
 */
public class Ejemplar implements Serializable {
    private static final long serialVersionUID = 1L;
    //Otra variable a poner puede ser si está firmado o es edición de coleccionista
    private int cod_libro;
    private LocalDate fecha;
    //Este puede ser un enum e int (enum en java, int en mysql)
    private String estado_libro;
    private int id_numero;
    
    public Ejemplar(int cod_libro, LocalDate fecha, String estado_libro, 
            int id_numero){
        this.cod_libro = cod_libro;
        this.fecha = fecha;
        this.estado_libro = estado_libro;
        this.id_numero = id_numero;
    }
    
    public Ejemplar(LocalDate fecha, String estado_libro, int id_numero){
        this.fecha = fecha;
        this.estado_libro = estado_libro;
        this.id_numero = id_numero;
    }

    @Override
    public String toString() {        
        return "EJEMPLAR Código:" + cod_libro;
    }

    /**
     * @return the cod_libro
     */
    public int getCod_libro() {
        return cod_libro;
    }

    /**
     * @param cod_libro the cod_libro to set
     */
    public void setCod_libro(int cod_libro) {
        this.cod_libro = cod_libro;
    }

    /**
     * @return the fecha
     */
    public LocalDate getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the estado_libro
     */
    public String getEstado_libro() {
        return estado_libro;
    }

    /**
     * @param estado_libro the estado_libro to set
     */
    public void setEstado_libro(String estado_libro) {
        this.estado_libro = estado_libro;
    }

    /**
     * @return the id_numero
     */
    public int getId_numero() {
        return id_numero;
    }

    /**
     * @param id_numero the id_numero to set
     */
    public void setId_numero(int id_numero) {
        this.id_numero = id_numero;
    }
}
