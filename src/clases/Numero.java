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
public class Numero implements Serializable{
    private static long serialVersionUID = 2665101188838279490L;
    
    private int id;
    private String nombre;
    private String portada;
    private int numero_paginas;
    private String resumen;
    private int id_coleccion;
    
    public Numero(int id, String nombre, String portada, 
            int numero_paginas, String resumen, int id_coleccion){
        this.id = id;
        this.nombre = nombre;
        this.portada = portada;
        this.numero_paginas = numero_paginas;
        this.resumen = resumen;
        this.id_coleccion = id_coleccion;
    }
    
    public Numero(String nombre, String portada, 
            int numero_paginas, String resumen, int id_coleccion){
        this.nombre = nombre;
        this.portada = portada;
        this.numero_paginas = numero_paginas;
        this.resumen = resumen;
        this.id_coleccion = id_coleccion;
    }

    @Override
    public String toString() {        
        /*return "Colección '" + this.nombre.toUpperCase() + "' (" + this.nombre_editorial
                + ")\nID: " + this.id + "\nAño de lanzamiento: " + this.ano_lanzamiento
                + "\nDescripción de la colección: " + this.descripcion;*/
        
        return this.nombre;
    }

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
     * @return the portada
     */
    public String getPortada() {
        return portada;
    }

    /**
     * @param portada the portada to set
     */
    public void setPortada(String portada) {
        this.portada = portada;
    }

    /**
     * @return the numero_paginas
     */
    public int getNumero_paginas() {
        return numero_paginas;
    }

    /**
     * @param numero_paginas the numero_paginas to set
     */
    public void setNumero_paginas(int numero_paginas) {
        this.numero_paginas = numero_paginas;
    }

    /**
     * @return the resumen
     */
    public String getResumen() {
        return resumen;
    }

    /**
     * @param resumen the resumen to set
     */
    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    /**
     * @return the id_coleccion
     */
    public int getId_coleccion() {
        return id_coleccion;
    }

    /**
     * @param id_coleccion the id_coleccion to set
     */
    public void setId_coleccion(int id_coleccion) {
        this.id_coleccion = id_coleccion;
    }
    
    
}
