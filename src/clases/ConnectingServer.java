/* La informacion entre cliente y servidor se pasará mediante la un objeto
 de la clase Socket. Sin embargo, hay que abrir crear primero un ServerSocket
 con un puerto asociado para recibir la informacion
 */
package clases;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.sql.Statement;
import javax.imageio.ImageIO;
import org.imgscalr.Scalr;
import java.time.LocalDate;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * @author Jesus González
 */
public class ConnectingServer {
    //Opción refrescar

    static final int ALTA_COLECCION = 11;
    static final int MODIFICAR_COLECCION = 12;
    static final int BAJA_COLECCION = 13;
    static final int ALTA_NUMERO = 21;
    static final int MODIFICAR_NUMERO = 22;
    static final int BAJA_NUMERO = 23;
    static final int ALTA_EJEMPLAR = 31;
    static final int MODIFICAR_EJEMPLAR = 32;
    static final int BAJA_EJEMPLAR = 33;
    static final int PREDEFINIDO_1 = 91;
    static final int PREDEFINIDO_2 = 92;
    static final int PERSONALIZADO_1 = 81;
    static final int PERSONALIZADO_2 = 82;
    static final int BUSQUEDA_COLECCION = 00;
    static final int BUSQUEDA_NUMERO = 01;
    static final int BUSQUEDA_EJEMPLAR = 02;
    static final int BUSQUEDA_COMPLETA = 03;

    private static String URL = "localhost";
    private static String puerto = "3306";
    private static String usuario = "root";
    private static String clave = "root";
    private static String nombre = "PROYECTO_DAM";

    public static ObjectInputStream entrada;
    public static ObjectOutputStream salida;

    public static void main(String[] args) {

        Properties archivoProperties = new Properties();
        InputStream ficheroConfiguracion = null;

        int resultado = Conexion.conectarComprobarBD(URL, puerto, usuario, clave);

        if (resultado != 0) {
            System.out.println("Error conectando a la base de datos.");
            //JOptionPane.showMessageDialog(this, "Error conectando a la base de datos.");
            return;
        }

        //CREACIÓN (SI NO EXISTE), DE LA BD
        resultado = Conexion.crearBaseDatos();

        //En este punto, el programa se ha conectado al servidor.
        resultado = Conexion.conectar(URL, puerto, usuario, nombre, clave);

        if (resultado != 0) {
            System.out.println("Error conectando a la tabla.");
            //JOptionPane.showMessageDialog(this, "Error conectando a la BD.");
            System.exit(0);
        }

        crearDirectorioImagenes();

        while (true) {
            openConnexion();
        }
    }

    public static void crearDirectorioImagenes() {
        String directorioImagenes = "PortadasNumeros";
        File Dir = new File(directorioImagenes);

        if (!Dir.exists()) {
            Dir.mkdir();
            System.out.println("Directorio para almacenar portadas creado"
                    + " con éxito.");
        } else {
            System.out.println("Directorio portadas ya existía");
        }
    }

    public static int openConnexion() {

        try {
            ServerSocket socketServidor = new ServerSocket(4343);
            System.out.println("Aun no");
            //Mejor crear una variable, para que no esté abierto siempre que 
            //estemos ejecutando el programa, si no cuando se lo indiquemos expresamente

            //Aqui estaba puesto previamente el while(true)
            //while (true) {
            Socket socketCliente = socketServidor.accept();
            System.out.println("Conexion establecida con el cliente");
            System.out.println("Enviando informacion obtenida en base a las"
                    + " consultas...");

            entrada = new ObjectInputStream(socketCliente.getInputStream());
            salida = new ObjectOutputStream(socketCliente.getOutputStream());

            //En teoría no hace falta el if ya que el programa lo debería
            //ejecutar siempre correctamente
            HashMap primero = (HashMap) entrada.readObject();
            if ((int) primero.get("RecuperarInfo") == 1) {
                recuperarInfoTablas();
            }

            while (true) {
                if (socketCliente.isConnected()) {
                    //entrada = new ObjectInputStream(socketCliente.getInputStream());
                    //salida = new ObjectOutputStream(socketCliente.getOutputStream());
                    Coleccion c;
                    Numero n;
                    Ejemplar e;
                    ArrayList<Coleccion> coleccionesList = new ArrayList<>();
                    ArrayList<Numero> numerosList = new ArrayList<>();
                    ArrayList<Ejemplar> ejemplaresList = new ArrayList<>();
                    HashMap infoDevolver = new HashMap();

                    HashMap objetos = (HashMap) entrada.readObject();
                    int numAccion = (int) objetos.get("Accion");

                    switch (numAccion) {
                        case ALTA_COLECCION:
                            System.out.println("Alta Coleccion");
                            infoDevolver = new HashMap();
                            c = (Coleccion) objetos.get("Coleccion");

                            try {
                                String consultaSQL = "INSERT INTO PROYECTO_DAM.Coleccion ("
                                        + "nombre, nombre_editorial, ano_lanzamiento, "
                                        + "descripcion) VALUES('" + c.getNombre()
                                        + "', '" + c.getNombre_editorial() + "', "
                                        + c.getAno_lanzamiento() + ", '" + c.getDescripcion()
                                        + "')";
                                System.out.println(consultaSQL);
                                Statement stmt = Conexion.getConexion().createStatement();
                                System.out.println("Comprobamos si se repite el nombre");
                                stmt.executeUpdate(consultaSQL);

                                //Recuperamos informacion
                                consultaSQL = "SELECT id, nombre, nombre_editorial,"
                                        + " ano_lanzamiento, descripcion FROM Proyecto_DAM.Coleccion";
                                Statement stmt1 = Conexion.getConexion().createStatement();
                                ResultSet rs = stmt1.executeQuery(consultaSQL);
                                while (rs.next()) {
                                    c = new Coleccion(rs.getInt(1), rs.getString(2),
                                            rs.getString(3), rs.getInt(4), rs.getString(5));
                                    System.out.println(c.toString());
                                    coleccionesList.add(c);
                                }

                                infoDevolver.put("Error", 0);
                                infoDevolver.put("Colecciones", coleccionesList);

                                salida.writeObject(infoDevolver);
                                System.out.println("Enviando ArrayList");

                            } catch (SQLException ex) {
                                if (ex.getErrorCode() == 1062) {
                                    System.out.println("El nombre de la coleccion ya existe");
                                    infoDevolver.put("Error", 1);
                                    salida.writeObject(infoDevolver);
                                } else {
                                    ex.printStackTrace();
                                    System.out.println("Error insertando datos"
                                            + "o recogiéndolos de la BDD");
                                    infoDevolver.put("Error", 2);
                                    salida.writeObject(infoDevolver);
                                }
                            }
                            break;

                        case MODIFICAR_COLECCION:
                            System.out.println("Modificar Coleccion");
                            infoDevolver = new HashMap();
                            c = (Coleccion) objetos.get("Coleccion");

                            try {
                                String consultaSQL = "UPDATE Proyecto_DAM.Coleccion SET nombre_editorial = '"
                                        + c.getNombre_editorial() + "', ano_lanzamiento = '" + c.getAno_lanzamiento() + "', descripcion =  '"
                                        + c.getDescripcion()
                                        + "' WHERE nombre = '" + c.getNombre() + "';";
                                System.out.println(consultaSQL);
                                Statement stmt = Conexion.getConexion().createStatement();
                                stmt.executeUpdate(consultaSQL);

                                //Recuperamos informacion
                                consultaSQL = "SELECT id, nombre, nombre_editorial,"
                                        + " ano_lanzamiento, descripcion FROM Proyecto_DAM.Coleccion";
                                Statement stmt1 = Conexion.getConexion().createStatement();
                                ResultSet rs = stmt1.executeQuery(consultaSQL);
                                while (rs.next()) {
                                    c = new Coleccion(rs.getInt(1), rs.getString(2),
                                            rs.getString(3), rs.getInt(4), rs.getString(5));
                                    System.out.println(c.toString());
                                    coleccionesList.add(c);
                                }

                                infoDevolver.put("Error", 0);
                                infoDevolver.put("Colecciones", coleccionesList);

                                salida.writeObject(infoDevolver);
                                System.out.println("Enviando ArrayList");

                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case BAJA_COLECCION:
                            System.out.println("Baja Coleccion");
                            c = (Coleccion) objetos.get("Coleccion");
                            infoDevolver = new HashMap();

                            try {
                                String consultaSQL = "DELETE FROM `Proyecto_dam`.`Coleccion` WHERE "
                                        + "`id` = '" + c.getId() + "'";
                                Statement stmt = Conexion.getConexion().createStatement();
                                stmt.executeUpdate(consultaSQL);

                                consultaSQL = "SELECT id, nombre, nombre_editorial,"
                                        + " ano_lanzamiento, descripcion FROM Proyecto_DAM.Coleccion";
                                Statement stmt1 = Conexion.getConexion().createStatement();
                                ResultSet rs = stmt1.executeQuery(consultaSQL);
                                while (rs.next()) {
                                    c = new Coleccion(rs.getInt(1), rs.getString(2),
                                            rs.getString(3), rs.getInt(4), rs.getString(5));
                                    System.out.println(c.toString());
                                    coleccionesList.add(c);
                                }

                                infoDevolver.put("Error", 0);
                                infoDevolver.put("Colecciones", coleccionesList);

                                salida.writeObject(infoDevolver);
                                System.out.println("Enviando ArrayList");

                            } catch (SQLException ex) {
                                if (ex.getErrorCode() == 1451) {
                                    System.out.println("Para poder borrar esta coleccion necesitas"
                                            + " borrar los números asociados");
                                    infoDevolver.put("Error", 1);
                                    salida.writeObject(infoDevolver);
                                } else {
                                    ex.printStackTrace();
                                    infoDevolver.put("Error", 2);
                                    salida.writeObject(infoDevolver);
                                }
                            }

                            break;
                        case ALTA_NUMERO:
                            System.out.println("Alta Numero");

                            String extension,
                             imagenGuardarPath;
                            infoDevolver = new HashMap();
                            n = (Numero) objetos.get("Numero");
                            extension = (String) objetos.get("Extension");
                            imagenGuardarPath = "PortadasNumeros/"
                                    + n.getNombre() + ".png";
                            n.setPortada(imagenGuardarPath);

                            try {
                                String consultaSQL = "INSERT INTO PROYECTO_DAM.Numero ("
                                        + "nombre, portada, num_paginas, resumen, "
                                        + "id_coleccion) VALUES('" + n.getNombre()
                                        + "', '" + n.getPortada() + "', "
                                        + n.getNumero_paginas() + ", '" + n.getResumen()
                                        + "', " + n.getId_coleccion() + ")";
                                System.out.println(consultaSQL);
                                Statement stmt = Conexion.getConexion().createStatement();
                                System.out.println("Comprobamos si se repite el nombre");
                                stmt.executeUpdate(consultaSQL);

                                //Guardamos la imagen
                                byte[] sizeArray = new byte[4];
                                sizeArray = (byte[]) objetos.get("Size");
                                int size = ByteBuffer.wrap(sizeArray).asIntBuffer().get();

                                byte[] byteArray = new byte[size];
                                byteArray = (byte[]) objetos.get("ByteArray");

                                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(byteArray));
                                System.out.println("Received " + bufferedImage.getHeight() + "x"
                                        + bufferedImage.getWidth() + ": " + System.currentTimeMillis());
                                BufferedImage imagenReescalada = Scalr.resize(bufferedImage, 200, 310, null);
                                System.out.println("Guardada " + imagenReescalada.getHeight() + "x"
                                        + imagenReescalada.getWidth() + ": " + System.currentTimeMillis());
                                ImageIO.write(imagenReescalada, "png", new File(imagenGuardarPath));
                                //Fin del guardado de la imagen

                                consultaSQL = "SELECT id, nombre, portada,"
                                        + " num_paginas, resumen, id_coleccion FROM Proyecto_DAM.Numero";
                                Statement stmt1 = Conexion.getConexion().createStatement();
                                ResultSet rs = stmt1.executeQuery(consultaSQL);
                                while (rs.next()) {
                                    n = new Numero(rs.getInt(1), rs.getString(2),
                                            rs.getString(3), rs.getInt(4), rs.getString(5), rs.getInt(6));
                                    System.out.println(n.toString());
                                    numerosList.add(n);
                                }

                                infoDevolver.put("Error", 0);
                                infoDevolver.put("Numeros", numerosList);

                                salida.writeObject(infoDevolver);
                            } catch (SQLException ex) {
                                if (ex.getErrorCode() == 1062) {
                                    System.out.println("El nombre del numero ya existe");
                                    infoDevolver.put("Error", 1);
                                    salida.writeObject(infoDevolver);
                                } else {
                                    ex.printStackTrace();
                                    System.out.println("Error insertando datos"
                                            + "o recogiéndolos de la BDD");
                                    infoDevolver.put("Error", 2);
                                    salida.writeObject(infoDevolver);
                                }
                            }
                            break;
                        case MODIFICAR_NUMERO:
                            System.out.println("Modificar Numero");

                            infoDevolver = new HashMap();
                            n = (Numero) objetos.get("Numero");
                            extension = (String) objetos.get("Extension");
                            imagenGuardarPath = "PortadasNumeros/"
                                    + n.getNombre() + ".png";
                            n.setPortada(imagenGuardarPath);

                            try {
                                String consultaSQL = "UPDATE Proyecto_DAM.Numero SET num_paginas = "
                                        + n.getNumero_paginas() + ", resumen = '" + n.getResumen()
                                        + "' WHERE id = " + n.getId() + ";";
                                System.out.println(consultaSQL);
                                Statement stmt = Conexion.getConexion().createStatement();
                                stmt.executeUpdate(consultaSQL);

                                if ((Boolean) objetos.get("HaCambiadoPortada")) {
                                    byte[] sizeArray = new byte[4];
                                    sizeArray = (byte[]) objetos.get("Size");
                                    int size = ByteBuffer.wrap(sizeArray).asIntBuffer().get();

                                    byte[] byteArray = new byte[size];
                                    byteArray = (byte[]) objetos.get("ByteArray");

                                    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(byteArray));
                                    System.out.println("Received " + bufferedImage.getHeight() + "x"
                                            + bufferedImage.getWidth() + ": " + System.currentTimeMillis());
                                    BufferedImage imagenReescalada = Scalr.resize(bufferedImage, 200, 310, null);
                                    System.out.println("Guardada " + imagenReescalada.getHeight() + "x"
                                            + imagenReescalada.getWidth() + ": " + System.currentTimeMillis());
                                    ImageIO.write(imagenReescalada, "png", new File(imagenGuardarPath));
                                }

                                consultaSQL = "SELECT id, nombre, portada,"
                                        + " num_paginas, resumen, id_coleccion FROM Proyecto_DAM.Numero";
                                Statement stmt1 = Conexion.getConexion().createStatement();
                                ResultSet rs = stmt1.executeQuery(consultaSQL);
                                while (rs.next()) {
                                    n = new Numero(rs.getInt(1), rs.getString(2),
                                            rs.getString(3), rs.getInt(4), rs.getString(5), rs.getInt(6));
                                    System.out.println(n.toString());
                                    numerosList.add(n);
                                }

                                infoDevolver.put("Error", 0);
                                infoDevolver.put("Numeros", numerosList);

                                salida.writeObject(infoDevolver);
                            } catch (SQLException ex) {
                                if (ex.getErrorCode() == 1062) {
                                    System.out.println("El nombre del numero ya existe");
                                    infoDevolver.put("Error", 1);
                                    salida.writeObject(infoDevolver);
                                } else {
                                    ex.printStackTrace();
                                    System.out.println("Error insertando datos"
                                            + "o recogiéndolos de la BDD");
                                    infoDevolver.put("Error", 2);
                                    salida.writeObject(infoDevolver);
                                }
                            }
                            break;
                        case BAJA_NUMERO:
                            System.out.println("Baja Numero");
                            n = (Numero) objetos.get("Numero");
                            infoDevolver = new HashMap();

                            try {
                                String consultaSQL = "DELETE FROM `Proyecto_dam`.`Numero` WHERE "
                                        + "`id` = '" + n.getId() + "'";
                                Statement stmt = Conexion.getConexion().createStatement();
                                stmt.executeUpdate(consultaSQL);

                                File portada = new File(n.getPortada());
                                System.out.println("Portada eliminada");
                                portada.delete();

                                consultaSQL = "SELECT id, nombre, portada,"
                                        + " num_paginas, resumen, id_coleccion FROM Proyecto_DAM.Numero";
                                Statement stmt1 = Conexion.getConexion().createStatement();
                                ResultSet rs = stmt1.executeQuery(consultaSQL);
                                while (rs.next()) {
                                    n = new Numero(rs.getInt(1), rs.getString(2),
                                            rs.getString(3), rs.getInt(4),
                                            rs.getString(5), rs.getInt(6));
                                    System.out.println(n.toString());
                                    numerosList.add(n);
                                }

                                infoDevolver.put("Error", 0);
                                infoDevolver.put("Numeros", numerosList);

                                salida.writeObject(infoDevolver);
                                System.out.println("Enviando ArrayList");

                            } catch (SQLException ex) {
                                if (ex.getErrorCode() == 1451) {
                                    System.out.println("Para poder borrar este numero necesitas"
                                            + " borrar los ejemplares asociados");
                                    infoDevolver.put("Error", 1);
                                    salida.writeObject(infoDevolver);
                                } else {
                                    ex.printStackTrace();
                                    infoDevolver.put("Error", 2);
                                    System.out.println("No se puedo borrar la imagen o sql");
                                    salida.writeObject(infoDevolver);
                                }
                            }
                            break;
                        case ALTA_EJEMPLAR:
                            System.out.println("Alta Ejemplar");
                            infoDevolver = new HashMap();
                            e = (Ejemplar) objetos.get("Ejemplar");
                            
                            try {
                                String consultaSQL = "INSERT INTO PROYECTO_DAM.Ejemplar ("
                                        + "fecha_adquisicion, estado_libro, id_numero) "
                                        + "VALUES('" + e.getFecha().getYear()
                                        + "-" + e.getFecha().getMonthValue() + "-" + e.getFecha().getDayOfMonth()
                                        + "', '" + e.getEstado_libro() + "', "
                                        + e.getId_numero() + ")";
                                System.out.println(consultaSQL);
                                Statement stmt = Conexion.getConexion().createStatement();
                                stmt.executeUpdate(consultaSQL);

                                //Recuperamos informacion
                                consultaSQL = "SELECT cod_libro, fecha_adquisicion, estado_libro,"
                                        + " id_numero FROM Proyecto_DAM.Ejemplar";
                                Statement stmt1 = Conexion.getConexion().createStatement();
                                ResultSet rs = stmt1.executeQuery(consultaSQL);
                                while (rs.next()) {
                                    e = new Ejemplar(rs.getInt(1), rs.getDate(2).toLocalDate(),
                                            rs.getString(3), rs.getInt(4));
                                    System.out.println(e.toString());
                                    ejemplaresList.add(e);
                                }

                                infoDevolver.put("Error", 0);
                                infoDevolver.put("Ejemplares", ejemplaresList);

                                salida.writeObject(infoDevolver);

                            } catch (SQLException ex) {
                                infoDevolver.put("Error", 1);
                                salida.writeObject(infoDevolver);
                            }
                            break;
                        case MODIFICAR_EJEMPLAR:
                            System.out.println("Modificar Ejemplar");
                            infoDevolver = new HashMap();
                            e = (Ejemplar) objetos.get("Ejemplar");

                            try {
                                String consultaSQL = "UPDATE Proyecto_DAM.Ejemplar SET fecha_adquisicion = '"
                                        + e.getFecha().getYear() + "-" + e.getFecha().getMonthValue() + "-"
                                        + e.getFecha().getDayOfMonth() + "', estado_libro = '" + e.getEstado_libro()
                                        + "', id_numero =  " + e.getId_numero()
                                        + " WHERE cod_libro = " + e.getCod_libro() + ";";
                                System.out.println(consultaSQL);
                                Statement stmt = Conexion.getConexion().createStatement();
                                stmt.executeUpdate(consultaSQL);

                                //Recuperamos informacion
                                consultaSQL = "SELECT cod_libro, fecha_adquisicion, estado_libro,"
                                        + " id_numero FROM Proyecto_DAM.Ejemplar";
                                Statement stmt1 = Conexion.getConexion().createStatement();
                                ResultSet rs = stmt1.executeQuery(consultaSQL);
                                while (rs.next()) {
                                    e = new Ejemplar(rs.getInt(1), rs.getDate(2).toLocalDate(),
                                            rs.getString(3), rs.getInt(4));
                                    System.out.println(e.toString());
                                    ejemplaresList.add(e);
                                }

                                infoDevolver.put("Error", 0);
                                infoDevolver.put("Ejemplares", ejemplaresList);

                                salida.writeObject(infoDevolver);

                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                            break;
                        case BAJA_EJEMPLAR:
                            System.out.println("Baja Ejemplar");

                            e = (Ejemplar) objetos.get("Ejemplar");
                            infoDevolver = new HashMap();

                            try {
                                String consultaSQL = "DELETE FROM `Proyecto_dam`.`Ejemplar` WHERE "
                                        + "`cod_libro` = '" + e.getCod_libro() + "'";
                                Statement stmt = Conexion.getConexion().createStatement();
                                stmt.executeUpdate(consultaSQL);

                                consultaSQL = "SELECT cod_libro, fecha_adquisicion, estado_libro,"
                                        + " id_numero FROM Proyecto_DAM.Ejemplar";
                                Statement stmt1 = Conexion.getConexion().createStatement();
                                ResultSet rs = stmt1.executeQuery(consultaSQL);
                                while (rs.next()) {
                                    e = new Ejemplar(rs.getInt(1), rs.getDate(2).toLocalDate(),
                                            rs.getString(3), rs.getInt(4));
                                    System.out.println(e.toString());
                                    ejemplaresList.add(e);
                                }

                                infoDevolver.put("Error", 0);
                                infoDevolver.put("Ejemplares", ejemplaresList);

                                salida.writeObject(infoDevolver);

                            } catch (SQLException ex) {
                                infoDevolver.put("Error", 1);
                                salida.writeObject(infoDevolver);
                            }
                            break;
                        case PREDEFINIDO_1:
                            System.out.println("Informe predefinido 1");
                            infoDevolver = new HashMap();
                            String informe = "/informes/InformePredefinido1.jrxml";
                            InputStream resourceAsStream = 
                                    ConnectingServer.class.getResourceAsStream("../imagenes/Logo_TodoColecciones.png");
                            Image imagen = ImageIO.read(resourceAsStream);
                            try {
                                HashMap param = new HashMap();
                                param.put("LOGO", imagen);
                                JasperReport reporte = JasperCompileManager.compileReport(
                                        ConnectingServer.class.getResourceAsStream(informe));
                                JasperPrint impresion = JasperFillManager.fillReport(
                                        reporte, param, Conexion.getConexion());
                                infoDevolver.put("PREDEFINIDO_1", impresion);
                                
                                System.out.println("Enviado");
                                salida.writeObject(infoDevolver);
                            } catch (JRException ex) {
                                ex.printStackTrace();
                            }
                            break;
                        case PREDEFINIDO_2:
                            System.out.println("Informe predefinido 2");
                            infoDevolver = new HashMap();
                            //No me gusta el scope de esta variable
                            informe = "/informes/InformePredefinido2.jrxml";
                            //Y el de esta, MENOS todavía
                            String subinforme = "/informes/SubInformePredefinido2.jrxml";

                            HashMap param = new HashMap();

                            try {
                                JasperReport reporte = JasperCompileManager.compileReport(
                                        ConnectingServer.class.getResourceAsStream(informe));
                                JasperReport subreporte = JasperCompileManager.compileReport(
                                        ConnectingServer.class.getResourceAsStream(subinforme));

                                param.put("SUBINFORME", subreporte);

                                JasperPrint impresion = JasperFillManager.fillReport(
                                        reporte, param, Conexion.getConexion());
                                infoDevolver.put("PREDEFINIDO_2", impresion);

                                System.out.println("Enviando pdf informe...");
                                salida.writeObject(infoDevolver);
                            } catch (JRException ex) {
                                ex.printStackTrace();
                            }
                            break;
                        case PERSONALIZADO_1:
                            System.out.print("Informe personalizado 1: ");
                            infoDevolver = new HashMap();
                            informe = "/informes/InformePersonalizado1.jrxml";
                            int ano_lanzamiento = (int) objetos.get("AnoLanzamiento");
                            System.out.println(ano_lanzamiento);

                            param = new HashMap();
                            try {
                                JasperReport reporte = JasperCompileManager.compileReport(
                                        ConnectingServer.class.getResourceAsStream(informe));
                                param.put("ANO_LANZAMIENTO", ano_lanzamiento);

                                JasperPrint impresion = JasperFillManager.fillReport(
                                        reporte, param, Conexion.getConexion());
                                infoDevolver.put("PERSONALIZADO_1", impresion);

                                System.out.println("Enviado");
                                salida.writeObject(infoDevolver);
                            } catch (JRException ex) {
                                ex.printStackTrace();
                            }
                            break;
                        case PERSONALIZADO_2:

                            break;
                        case BUSQUEDA_COLECCION:
                            System.out.println("Solicitud de búsqueda por colecciones");
                            infoDevolver = new HashMap();

                            String patron_busqueda = (String) objetos.get("PATRON");

                            try {
                                String consultaSQL = "SELECT Numero.id, Numero.nombre, Numero.portada,"
                                        + " Numero.num_paginas, Numero.resumen, Numero.id_coleccion "
                                        + "FROM Proyecto_DAM.Numero JOIN Proyecto_DAM.Coleccion "
                                        + "ON Numero.id_coleccion = Coleccion.id "
                                        + "WHERE Coleccion.nombre LIKE '%" + patron_busqueda + "%'";
                                Statement stmt1 = Conexion.getConexion().createStatement();
                                ResultSet rs = stmt1.executeQuery(consultaSQL);
                                while (rs.next()) {
                                    n = new Numero(rs.getInt(1), rs.getString(2),
                                            rs.getString(3), rs.getInt(4), rs.getString(5),
                                            rs.getInt(6));
                                    System.out.println(n.toString());
                                    numerosList.add(n);
                                }
                                infoDevolver.put("Error", 0);
                                infoDevolver.put("Numeros", numerosList);

                                salida.writeObject(infoDevolver);
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                            break;
                        case BUSQUEDA_NUMERO:
                            System.out.println("Solicitud de búsqueda por numeros");
                            infoDevolver = new HashMap();

                            patron_busqueda = (String) objetos.get("PATRON");

                            try {
                                //Recuperamos informacion
                                String consultaSQL = "SELECT id, nombre, portada,"
                                        + " num_paginas, resumen, id_coleccion "
                                        + "FROM Proyecto_DAM.Numero "
                                        + "WHERE nombre LIKE '%" + patron_busqueda + "%'";
                                Statement stmt1 = Conexion.getConexion().createStatement();
                                ResultSet rs = stmt1.executeQuery(consultaSQL);
                                while (rs.next()) {
                                    n = new Numero(rs.getInt(1), rs.getString(2),
                                            rs.getString(3), rs.getInt(4), rs.getString(5),
                                            rs.getInt(6));
                                    System.out.println(n.toString());
                                    numerosList.add(n);
                                }

                                infoDevolver.put("Error", 0);
                                infoDevolver.put("Numeros", numerosList);

                                salida.writeObject(infoDevolver);
                            } catch (SQLException ex) {
                                infoDevolver.put("Error", 1);
                                salida.writeObject(infoDevolver);
                            }
                            break;
                        case BUSQUEDA_EJEMPLAR:
                            System.out.println("Solicitud de búsqueda de ejemplares");
                            infoDevolver = new HashMap();
                            int idNumero = (int) objetos.get("ID_NUMERO");

                            try {
                                //Recuperamos informacion
                                String consultaSQL = "SELECT cod_libro,"
                                        + " fecha_adquisicion, estado_libro, id_numero "
                                        + "FROM Proyecto_DAM.Ejemplar "
                                        + "WHERE id_numero = " + idNumero;
                                Statement stmt1 = Conexion.getConexion().createStatement();
                                ResultSet rs = stmt1.executeQuery(consultaSQL);
                                while (rs.next()) {
                                    e = new Ejemplar(rs.getInt(1), rs.getDate(2).toLocalDate(),
                                            rs.getString(3), rs.getInt(4));
                                    System.out.println(e.toString());
                                    ejemplaresList.add(e);
                                }

                                infoDevolver.put("Error", 0);
                                infoDevolver.put("Ejemplares", ejemplaresList);

                                salida.writeObject(infoDevolver);
                            } catch (SQLException ex) {
                                infoDevolver.put("Error", 1);
                                salida.writeObject(infoDevolver);
                            }
                            break;
                        case BUSQUEDA_COMPLETA:
                            System.out.println("Envío de información completa");
                            infoDevolver = new HashMap();
                            idNumero = (int) objetos.get("ID_NUMERO");
                            //Inicializo para que no me salte la excepción en
                            //la consulta del número y de la colección
                            n = null;
                            c = null;

                            try {
                                //Recuperamos informacion
                                String consultaSQL = "SELECT id, nombre, portada,"
                                        + " num_paginas, resumen, id_coleccion "
                                        + "FROM Proyecto_DAM.Numero "
                                        + "WHERE id = " + idNumero;
                                Statement stmt1 = Conexion.getConexion().createStatement();
                                ResultSet rs = stmt1.executeQuery(consultaSQL);
                                while (rs.next()) {
                                    n = new Numero(rs.getInt(1), rs.getString(2),
                                            rs.getString(3), rs.getInt(4), rs.getString(5),
                                            rs.getInt(6));
                                    System.out.println("Sólo saca uno: " + n.toString());
                                }

                                consultaSQL = "SELECT id, nombre, nombre_editorial, "
                                        + "ano_lanzamiento, descripcion "
                                        + "FROM Proyecto_DAM.Coleccion "
                                        + "WHERE id = " + n.getId_coleccion();
                                stmt1 = Conexion.getConexion().createStatement();
                                rs = stmt1.executeQuery(consultaSQL);
                                while (rs.next()) {
                                    c = new Coleccion(rs.getInt(1), rs.getString(2),
                                            rs.getString(3), rs.getInt(4), rs.getString(5));
                                    System.out.println("Sólo saca una: " + n.toString());
                                }

                                //Una vez recogidos los objetos Coleccion y Numeros
                                //procedo a recoger e introducir en el hashMap la imagen
                                File file = new File(n.getPortada());
                                try {
                                    BufferedImage bufferedImage = ImageIO.read(file);

                                    ByteArrayOutputStream byteArrayOutputStream
                                            = new ByteArrayOutputStream();
                                    ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                                    byte[] size = ByteBuffer.allocate(4).putInt(
                                            byteArrayOutputStream.size()).array();
                                    infoDevolver.put("Size", size);
                                    infoDevolver.put("ByteArray", byteArrayOutputStream.toByteArray());
                                    infoDevolver.put("Extension", "png");
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }

                                infoDevolver.put("Error", 0);
                                infoDevolver.put("Numero", n);
                                infoDevolver.put("Coleccion", c);

                                salida.writeObject(infoDevolver);
                            } catch (SQLException ex) {
                                infoDevolver.put("Error", 1);
                                salida.writeObject(infoDevolver);
                            }
                            break;
                    }

                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("Aqui es donde salta la excepción!!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static void recuperarInfoTablas() {

        HashMap hashMap = new HashMap();

        ArrayList<Coleccion> listaColecciones = new ArrayList<Coleccion>();
        ArrayList<Numero> listaNumeros = new ArrayList<Numero>();
        ArrayList<Ejemplar> listaEjemplares = new ArrayList<Ejemplar>();

        Coleccion c;
        Numero n;
        Ejemplar e;

        System.out.println("Hola");
        try {
            //Recuperamos todas las colecciones
            String consultaSQL = "SELECT id, nombre, nombre_editorial,"
                    + " ano_lanzamiento, descripcion FROM Proyecto_DAM.Coleccion";
            Statement stmt = Conexion.getConexion().createStatement();
            ResultSet rs = stmt.executeQuery(consultaSQL);
            while (rs.next()) {
                c = new Coleccion(rs.getInt(1), rs.getString(2),
                        rs.getString(3), rs.getInt(4), rs.getString(5));
                System.out.println(c.toString());
                listaColecciones.add(c);
            }
            System.out.println("Hola2");
            consultaSQL = "SELECT id, nombre, portada,"
                    + " num_paginas, resumen, id_coleccion FROM Proyecto_DAM.Numero";
            Statement stmt1 = Conexion.getConexion().createStatement();
            rs = stmt1.executeQuery(consultaSQL);
            while (rs.next()) {
                n = new Numero(rs.getInt(1), rs.getString(2),
                        rs.getString(3), rs.getInt(4), rs.getString(5), rs.getInt(6));
                System.out.println(n.toString());
                listaNumeros.add(n);
            }

            System.out.println("Hola3");
            consultaSQL = "SELECT cod_libro, fecha_adquisicion, estado_libro,"
                    + " id_numero FROM Proyecto_DAM.Ejemplar";
            Statement stmt2 = Conexion.getConexion().createStatement();
            rs = stmt2.executeQuery(consultaSQL);
            while (rs.next()) {
                e = new Ejemplar(rs.getInt(1), rs.getDate(2).toLocalDate(),
                        rs.getString(3), rs.getInt(4));
                System.out.println(e.toString());
                listaEjemplares.add(e);
            }

            //Recuperamos todos los números
            //Recuperamos todos los ejemplares
            hashMap.put("Colecciones", listaColecciones);
            hashMap.put("Numeros", listaNumeros);
            hashMap.put("Ejemplares", listaEjemplares);

            //Enviamos las 3 listas
            salida.writeObject(hashMap);

        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
