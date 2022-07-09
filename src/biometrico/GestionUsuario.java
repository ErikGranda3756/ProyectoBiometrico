/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biometrico;

import static java.awt.Frame.MAXIMIZED_BOTH;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author copar
 */
public class GestionUsuario extends javax.swing.JFrame {

    /**
     * Creates new form GestionUsuario
     */
    public GestionUsuario() {

        initComponents();
        llenartabla("");
        listarcombo();
        AutoCompleteDecorator.decorate(cbx_acceso);
        bloquearTextos();
        bloquearBotones();

    }

    public void bloquearTextos() {

        txt_cedula.setEnabled(false);
        txt_nombre.setEnabled(false);
        txt_apellido.setEnabled(false);
        cbx_acceso.setEnabled(false);
        txt_contrasenia.setEnabled(false);
    }

    public void desbloquearTextos() {

        txt_cedula.setEnabled(true);
        txt_nombre.setEnabled(true);
        txt_apellido.setEnabled(true);
        cbx_acceso.setEnabled(true);
        txt_contrasenia.setEnabled(true);
    }

    public void desbloquearTextosEditarEliminar() {

        txt_cedula.setEnabled(false);
        txt_nombre.setEnabled(true);
        txt_apellido.setEnabled(true);
        cbx_acceso.setEnabled(true);
        txt_contrasenia.setEnabled(true);
    }

    public void bloquearBotones() {
        btn_nuevo.setEnabled(true);
        btn_registrar.setEnabled(false);
        btn_actualizar.setEnabled(false);
        btn_eliminar.setEnabled(false);
        btn_cancelar.setEnabled(false);
        //  jbtnSalir.setEnabled(true);
    }

    public void desbloquearBotones() {

        btn_nuevo.setEnabled(false);
        btn_registrar.setEnabled(true);
        btn_actualizar.setEnabled(false);
        btn_eliminar.setEnabled(false);
        btn_cancelar.setEnabled(true);

        // jbtnSalir.setEnabled(true);
    }

    public void desbloquearBotonesEditarEliminar() {

        btn_nuevo.setEnabled(false);
        btn_registrar.setEnabled(false);
        btn_actualizar.setEnabled(true);
        btn_eliminar.setEnabled(true);
        btn_cancelar.setEnabled(true);
        //   jbtnSalir.setEnabled(true);
    }
    Connection connection;
    PreparedStatement preparedStatement;
    ResultSet resultSet;
    Conexion con = new Conexion();
    Connection reg = con.conexion();

    void llenartabla(String valor) {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("CEDULA");
        modelo.addColumn("NOMBRE");
        modelo.addColumn("APELLIDO");
        modelo.addColumn("CARGO");
        modelo.addColumn("CONTRASEÑA");

        tabla_usuarios.setModel(modelo);
        String sql = "";
        if (valor.equals("")) {
            sql = "SELECT * FROM  usuarios";
        } else {

            sql = "SELECT * FROM usuarios WHERE cedula LIKE '%" + valor + "%'";
        }

        String[] datos = new String[6];
        try {
            Statement st = reg.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                datos[0] = rs.getString(1);
                datos[1] = rs.getString(2);
                datos[2] = rs.getString(3);
                datos[3] = rs.getString(4);
                datos[4] = rs.getString(5);
                datos[5] = rs.getString(6);

                modelo.addRow(datos);
            }
            tabla_usuarios.setModel(modelo);

        } catch (SQLException ex) {
            Logger.getLogger(GestionUsuario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void limpiarcampos() {
        txt_cedula.setText("");
        txt_nombre.setText("");
        txt_apellido.setText("");
        cbx_acceso.setSelectedItem(null);
        txt_contrasenia.setText("");

    }

    void listarcombo() {

        cbx_acceso.addItem("ADMINISTRADOR");
        cbx_acceso.addItem("DOCENTE");
        limpiarcampos();

    }

    void registrar_usuario() {
        String sql = "INSERT INTO usuarios (cedula,nombre,apellido,cargo,contrasenia) VALUES(?,?,?,?,?)";
        try {
            PreparedStatement pst = reg.prepareCall(sql);
            pst.setString(1, txt_cedula.getText());
            pst.setString(2, txt_nombre.getText());
            pst.setString(3, txt_apellido.getText());
            pst.setString(4, cbx_acceso.getSelectedItem().toString());
            pst.setString(5, txt_contrasenia.getText());
            int n = pst.executeUpdate();
            llenartabla("");

            if (n > 0) {
                JOptionPane.showMessageDialog(null, "EL USUARIO " + (txt_cedula.getText()) + "\nCON CONTRASENIA " + (txt_contrasenia.getText()) + "\nFUE REGISTRADO CON EXITO");
            } else {
                JOptionPane.showMessageDialog(null, "USUARIO NO REGISTRADO\nPUEDE QUE ALGUN DATO ESTE DUPLICADO");
            }
        } catch (SQLException ex) {
            Logger.getLogger(GestionUsuario.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "USUARIO NO REGISTRADO\nPUEDE QUE ALGUN DATO ESTE DUPLICADO\nINTENTE CON OTRO NOMBRE DE USUARIO Y CONTRASEÃ‘A \n" + ex);
        }
        limpiarcampos();
    }

    void gestionar() {
        try {
            String ssql = "UPDATE usuarios SET nombre=?,apellido=?,cargo=?,contrasenia=? WHERE cedula=?";
            preparedStatement = reg.prepareStatement(ssql);

            preparedStatement.setString(1, txt_nombre.getText());
            preparedStatement.setString(2, txt_apellido.getText());
            preparedStatement.setString(3, cbx_acceso.getSelectedItem().toString());
            preparedStatement.setString(4, txt_contrasenia.getText());
            preparedStatement.setString(5, txt_cedula.getText());

            int res = preparedStatement.executeUpdate();
            llenartabla("");
            if (res > 0) {
                JOptionPane.showMessageDialog(null, "Usuario modificado con exito");
                btn_registrar.setEnabled(false);
                btn_eliminar.setEnabled(false);
                btn_registrar.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(null, "Error al Modificar al usuario");

            }

        } catch (SQLException | NumberFormatException | HeadlessException ex) {
            System.out.println(ex);
        }
        limpiarcampos();
    }

    void eliminar() {
        try {
            preparedStatement = reg.prepareStatement("DELETE FROM usuarios WHERE cedula=?");
            preparedStatement.setString(1, txt_cedula.getText());
            int res = preparedStatement.executeUpdate();
            llenartabla("");
            limpiarcampos();
            if (res > 0) {
                JOptionPane.showMessageDialog(null, "Eliminado");
                btn_registrar.setEnabled(true);
                btn_registrar.setEnabled(false);
                btn_eliminar.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(null, "Error al Eliminar");
            }
            connection.close();
        } catch (SQLException | NumberFormatException | HeadlessException ex) {
            System.out.println(ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txt_busqueda = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txt_cedula = new javax.swing.JTextField();
        txt_nombre = new javax.swing.JTextField();
        txt_apellido = new javax.swing.JTextField();
        txt_contrasenia = new javax.swing.JTextField();
        cbx_acceso = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla_usuarios = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        btn_registrar = new javax.swing.JButton();
        btn_actualizar = new javax.swing.JButton();
        btn_eliminar = new javax.swing.JButton();
        btn_nuevo = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        btn_cancelar = new javax.swing.JButton();
        btn_regresar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(102, 0, 0));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setForeground(new java.awt.Color(51, 51, 51));
        jPanel2.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("BUSCAR:");

        txt_busqueda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_busquedaActionPerformed(evt);
            }
        });
        txt_busqueda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_busquedaKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(36, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(txt_busqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 775, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_busqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 29, 930, -1));

        jPanel3.setBackground(new java.awt.Color(102, 0, 0));
        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel2.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("DATOS INFORMATIVOS");

        jLabel3.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("CEDULA:");

        jLabel4.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("NOMBRE:");

        jLabel5.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(240, 240, 240));
        jLabel5.setText("APELLIDO:");

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setText("CARGO:");

        jLabel7.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(240, 240, 240));
        jLabel7.setText("CONTRASEÑA:");

        txt_cedula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_cedulaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel5))
                                .addGap(45, 45, 45))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel7))
                                .addGap(18, 18, 18)))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txt_cedula)
                            .addComponent(txt_nombre)
                            .addComponent(txt_apellido)
                            .addComponent(cbx_acceso, 0, 146, Short.MAX_VALUE)
                            .addComponent(txt_contrasenia, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jLabel2)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txt_cedula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(txt_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_apellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(24, 24, 24)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbx_acceso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(26, 26, 26)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_contrasenia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 310, 320));

        tabla_usuarios.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tabla_usuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabla_usuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabla_usuariosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabla_usuarios);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 110, 590, 500));

        jPanel4.setBackground(new java.awt.Color(51, 51, 51));
        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_registrar.setBackground(new java.awt.Color(255, 255, 255));
        btn_registrar.setText("REGISTRAR");
        btn_registrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_registrarActionPerformed(evt);
            }
        });
        jPanel4.add(btn_registrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 80, 100, 30));

        btn_actualizar.setBackground(new java.awt.Color(255, 255, 255));
        btn_actualizar.setText("ACTUALIZAR");
        btn_actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_actualizarActionPerformed(evt);
            }
        });
        jPanel4.add(btn_actualizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 120, 100, 30));

        btn_eliminar.setBackground(new java.awt.Color(255, 255, 255));
        btn_eliminar.setText("ELIMINAR");
        btn_eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_eliminarActionPerformed(evt);
            }
        });
        jPanel4.add(btn_eliminar, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 80, 100, 30));

        btn_nuevo.setBackground(new java.awt.Color(255, 255, 255));
        btn_nuevo.setText("NUEVO");
        btn_nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_nuevoActionPerformed(evt);
            }
        });
        jPanel4.add(btn_nuevo, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 100, 30));

        jLabel8.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("MENU");
        jPanel4.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 10, -1, -1));

        btn_cancelar.setText("CANCELAR");
        btn_cancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelarActionPerformed(evt);
            }
        });
        jPanel4.add(btn_cancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 40, 100, 30));

        btn_regresar.setText("REGRESAR");
        jPanel4.add(btn_regresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 120, 100, 30));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 440, 310, 170));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_actualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_actualizarActionPerformed
        if (txt_nombre.getText().length() == 0) {
            JOptionPane.showMessageDialog(null, "COMPLETE TODOS LOS CAMPOS PARA CONTINUAR CON EL REGISTRO");
            txt_nombre.requestFocus();
            return;
        }
        if (txt_apellido.getText().length() == 0) {
            JOptionPane.showMessageDialog(null, "COMPLETE TODOS LOS CAMPOS PARA CONTINUAR CON EL REGISTRO");
            txt_apellido.requestFocus();
            return;
        }
        if (cbx_acceso.getSelectedItem().toString().equals("SELECCIONE UNO")) {
            JOptionPane.showMessageDialog(null, "SELECCIONE UNA OPCION\n PARA CONTINUAR CON EL REGISTRO");
            cbx_acceso.requestFocus();
            return;
        }
        if (txt_contrasenia.getText().length() == 0) {
            JOptionPane.showMessageDialog(null, "COMPLETE TODOS LOS CAMPOS PARA CONTINUAR CON EL REGISTRO");
            txt_contrasenia.requestFocus();
            return;
        }
        gestionar();
    }//GEN-LAST:event_btn_actualizarActionPerformed

    private void btn_nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_nuevoActionPerformed
        limpiarcampos();
        desbloquearBotones();
        desbloquearTextos();


    }//GEN-LAST:event_btn_nuevoActionPerformed

    private void tabla_usuariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_usuariosMouseClicked
        int rec = this.tabla_usuarios.getSelectedRow();
        this.txt_cedula.setText(tabla_usuarios.getValueAt(rec, 1).toString());
        this.txt_nombre.setText(tabla_usuarios.getValueAt(rec, 2).toString());
        this.txt_apellido.setText(tabla_usuarios.getValueAt(rec, 3).toString());
        this.cbx_acceso.setSelectedItem(tabla_usuarios.getValueAt(rec, 4).toString());
        this.txt_contrasenia.setText(tabla_usuarios.getValueAt(rec, 5).toString());
        desbloquearBotonesEditarEliminar();
        desbloquearTextosEditarEliminar();


    }//GEN-LAST:event_tabla_usuariosMouseClicked

    private void btn_eliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_eliminarActionPerformed
        eliminar();
    }//GEN-LAST:event_btn_eliminarActionPerformed

    private void txt_busquedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_busquedaActionPerformed

    }//GEN-LAST:event_txt_busquedaActionPerformed

    private void btn_registrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_registrarActionPerformed

        if (txt_cedula.getText().length() == 0) {
            JOptionPane.showMessageDialog(null, "COMPLETE TODOS LOS CAMPOS PARA CONTINUAR CON EL REGISTRO");
            txt_cedula.requestFocus();
            return;
        }
        if (txt_nombre.getText().length() == 0) {
            JOptionPane.showMessageDialog(null, "COMPLETE TODOS LOS CAMPOS PARA CONTINUAR CON EL REGISTRO");
            txt_nombre.requestFocus();
            return;
        }
        if (txt_apellido.getText().length() == 0) {
            JOptionPane.showMessageDialog(null, "COMPLETE TODOS LOS CAMPOS PARA CONTINUAR CON EL REGISTRO");
            txt_apellido.requestFocus();
            return;
        }
        if (cbx_acceso.getSelectedItem().toString().equals("SELECCIONE UNO")) {
            JOptionPane.showMessageDialog(null, "SELECCIONE UNA OPCION\n PARA CONTINUAR CON EL REGISTRO");
            cbx_acceso.requestFocus();
            return;
        }
        if (txt_contrasenia.getText().length() == 0) {
            JOptionPane.showMessageDialog(null, "COMPLETE TODOS LOS CAMPOS PARA CONTINUAR CON EL REGISTRO");
            txt_contrasenia.requestFocus();
            return;
        }

        registrar_usuario();
        bloquearBotones();
        bloquearTextos();
    }//GEN-LAST:event_btn_registrarActionPerformed

    private void txt_busquedaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_busquedaKeyReleased
        llenartabla(txt_busqueda.getText());
    }//GEN-LAST:event_txt_busquedaKeyReleased

    private void txt_cedulaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_cedulaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_cedulaActionPerformed

    private void btn_cancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelarActionPerformed
        limpiarcampos();
        bloquearBotones();
        bloquearTextos();
    }//GEN-LAST:event_btn_cancelarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GestionUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GestionUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GestionUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GestionUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GestionUsuario().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_actualizar;
    private javax.swing.JButton btn_cancelar;
    private javax.swing.JButton btn_eliminar;
    private javax.swing.JButton btn_nuevo;
    private javax.swing.JButton btn_registrar;
    private javax.swing.JButton btn_regresar;
    private javax.swing.JComboBox<String> cbx_acceso;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabla_usuarios;
    private javax.swing.JTextField txt_apellido;
    private javax.swing.JTextField txt_busqueda;
    private javax.swing.JTextField txt_cedula;
    private javax.swing.JTextField txt_contrasenia;
    private javax.swing.JTextField txt_nombre;
    // End of variables declaration//GEN-END:variables
}
