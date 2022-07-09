/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biometrico;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Erik
 */
public class Conexion {
  Connection con=null;
    public Connection  conexion(){ 
    try{
        Class.forName("com.mysql.jdbc.Driver");
        con=DriverManager.getConnection("jdbc:mysql://localhost/proyectovisual", "root", "");
        System.out.println("conexion establecida");
    }  catch (ClassNotFoundException | SQLException e) { 
           System.out.println("error de conexion"+e);
           JOptionPane.showMessageDialog(null, "Error de conexion con la base de datos."+e);
           }
       return con;
    }  
}
