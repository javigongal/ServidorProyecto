/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.io.Serializable;

/**
 *
 * @author Jesus González
 */
public class Coleccion implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String nombre;
    private String nombre_editorial;
    private int ano_lanzamiento;
    private String descripcion;
    
    public Coleccion(int id, String nombre, String nombre_editorial, 
            int ano_lanzamiento, String descripcion){
        this.id = id;
        this.nombre = nombre;
        this.nombre_editorial = nombre_editorial;
        this.ano_lanzamiento = ano_lanzamiento;
        this.descripcion = descripcion;
    }
    
    public Coleccion(String nombre, String nombre_editorial, 
            int ano_lanzamiento, String descripcion){
        this.nombre = nombre;
        this.nombre_editorial = nombre_editorial;
        this.ano_lanzamiento = ano_lanzamiento;
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {        
        return this.nombre.toUpperCase();
    }

//    @Override
//    public String toString() {        
//        return "Colección '" + this.nombre.toUpperCase() + "' (" + this.nombre_editorial
//                + ")\nID: " + this.id + "\nAño de lanzamiento: " + this.ano_lanzamiento
//                + "\nDescripción de la colección: " + this.descripcion;
//    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the nombre_editorial
     */
    public String getNombre_editorial() {
        return nombre_editorial;
    }

    /**
     * @param nombre_editorial the nombre_editorial to set
     */
    public void setNombre_editorial(String nombre_editorial) {
        this.nombre_editorial = nombre_editorial;
    }

    /**
     * @return the ano_lanzamiento
     */
    public int getAno_lanzamiento() {
        return ano_lanzamiento;
    }

    /**
     * @param ano_lanzamiento the ano_lanzamiento to set
     */
    public void setAno_lanzamiento(int ano_lanzamiento) {
        this.ano_lanzamiento = ano_lanzamiento;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    
    
}
