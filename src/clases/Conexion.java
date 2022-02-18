package clases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Jesus González
 */
public class Conexion {
    private static Connection conexion;

    //Conexion a la propia base de datos
    public static int conectar(String url,String puerto,String usuario,String bd,String clave){
        
        try 
        {            
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            System.out.println("jdbc:mysql://"+url+":"+puerto+"/"+bd+"?serverTimezone=UTC");
            conexion=DriverManager.getConnection("jdbc:mysql://"+url+":"+puerto+"/"+bd+"?serverTimezone=UTC",usuario,clave);
            
            return 0;
        } catch (SQLException e) 
        {
            e.printStackTrace();
             /*Este error en concreto indica que la BD no existía, y por eso no se
            * pudo conectar
             */
            if (e.getErrorCode() == 1049) {
                return -3;
            }
            return -1;
        } catch (ClassNotFoundException e) {
            //Comprobacion Driver MySQL
            e.printStackTrace();
            return -2;
        }
    }

    //Conexion al servidor, que no a la propia base, por eso no le paso el nombre de esta
    public static int conectarComprobarBD(String url, String puerto, String usuario, String clave) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            //Al no pasarle nombre de BD, simplemente compruebo si me conecta al servidor
            System.out.println("jdbc:mysql://" + url + ":" + puerto + "?serverTimezone=UTC");
            conexion = DriverManager.getConnection("jdbc:mysql://" + url + ":" + puerto + "?serverTimezone=UTC", usuario, clave);

            return 0;
        } catch (SQLException e) {
            //Error MySQL (por ejemplo, el código)
            e.printStackTrace();
            return -1;
        } catch (ClassNotFoundException e) {
            //Comprobar Driver MySQL
            e.printStackTrace();
            return -2;
        }
    }    
    
    public static int crearBaseDatos(){
        try {
            /**
             * Lo primero que hago es TRATAR crear la BD
             *
             * 1. Si se crea, entonces es porque no existía 2. Si al ejecutar
             * sentencia no se crea (da error), entonces es que ya existía.
             */
            Statement stmt = getConexion().createStatement();
            String consultaSQL = "CREATE SCHEMA PROYECTO_DAM";
            System.out.println(consultaSQL);
            stmt.executeUpdate(consultaSQL);
            consultaSQL = "USE PROYECTO_DAM";
            stmt.executeUpdate(consultaSQL);
            consultaSQL = "CREATE TABLE `PROYECTO_DAM`.`Coleccion` (\n" +
"                    `id` INT NOT NULL AUTO_INCREMENT,\n" +
"                    `nombre` VARCHAR(100) UNIQUE NOT NULL,\n" +
"                    `nombre_editorial` VARCHAR(50) NOT NULL,\n" +
"                    `ano_lanzamiento` INT NOT NULL,\n" +
"                    `descripcion` VARCHAR(250) NOT NULL,\n" +
"                    \n" +
"                    PRIMARY KEY (`id`))";
            System.out.println(consultaSQL);
            stmt.executeUpdate(consultaSQL);
            consultaSQL = "CREATE TABLE `Numero` (\n" +
"                    `id` INT NOT NULL AUTO_INCREMENT,\n" +
"                    `nombre` VARCHAR(75) UNIQUE NOT NULL,\n" +
"                    `portada` VARCHAR(250) NOT NULL,\n" +
"                    `num_paginas` INT NOT NULL,\n" +
"                    `resumen` VARCHAR(250) NOT NULL,\n" +
"                    `id_coleccion` INT NOT NULL,\n" +
"                    \n" +
"                    PRIMARY KEY (`id`),\n" +
"                    CONSTRAINT `fk_Numero_Coleccion` FOREIGN KEY(`id_coleccion`) \n" +
"                    REFERENCES `PROYECTO_DAM`.`Coleccion` (`id`)\n" +
"			 ON UPDATE RESTRICT\n" +
"                        ON DELETE RESTRICT\n" +
"					)";
            System.out.println(consultaSQL);
            stmt.executeUpdate(consultaSQL);
            consultaSQL = "CREATE TABLE `Ejemplar` (\n" +
"                    `cod_libro` INT NOT NULL AUTO_INCREMENT,\n" +
"                    `fecha_adquisicion` DATETIME NOT NULL,\n" +
"                    `estado_libro` VARCHAR(20) NOT NULL,\n" +
"                    `id_numero` INT NOT NULL,\n" +
"                    \n" +
"                    PRIMARY KEY (`cod_libro`),\n" +
"                    CONSTRAINT fk_Ejemplar_Numero FOREIGN KEY(`id_numero`)\n" +
"                    REFERENCES `proyecto_dam`.`numero`(`id`)\n" +
"			 ON UPDATE RESTRICT\n" +
"                        ON DELETE RESTRICT\n" +
"                    )";
            System.out.println(consultaSQL);
            stmt.executeUpdate(consultaSQL);            

            //Se creo sin problemas porque no existía
            System.out.println("La BD no existía y se creó.");
            return 0;

        } catch (SQLException e) {
            //Al producirse la excepción, tengo que comprobar si esta está 
            //provocada por la previa existencia de la BD
            if (e.getErrorCode() == 1007) {
                System.out.println("La BD ya existe, así que no se crea nada.");
                return -2;
            }
            e.printStackTrace();
            return -1;
        }
    }
    
    public static Connection getConexion()
    {
        return conexion;
    }
}
