/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biometrico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Erik
 */
public class RegistroAsistencia extends javax.swing.JFrame implements Runnable {

    DefaultTableModel modelo;
    DefaultTableModel modeloPicada;
    DefaultTableCellRenderer tcr;

    String hora, ampm, horac,horaSis,fechaSis;
    Thread hilo;
    Calendar calendario;
    Date fechahora,fecha,horaMax;
    Conexion con = new Conexion();
    Connection reg = con.conexion();

    /**
     * Creates new form RegistroAsistencia
     */
    public RegistroAsistencia() {
        initComponents();
        empezarHilo();
        cargarTabla();
        cargarTablaPicada();
        llenartablaInfoDocente();
        //activarBotones();
//verificarHorario();

    }

//    public void activarBotones() {
//        horaMax = generarHora("41");
//        while (fechahora.equals(horaMax)) {
//            jbtnMarcarAsistencia.setEnabled(true);
//        }
//        jbtnMarcarAsistencia.setEnabled(false);
//        
//    }
    public void cargarTabla() {
        tcr = new DefaultTableCellRenderer();
        modelo = new DefaultTableModel();
        modelo.addColumn("CEDULA");
        modelo.addColumn("TIPO REGISTRO");
        modelo.addColumn("ENTRADA JORNADA");
        modelo.addColumn("SALIDA JORNADA ");
        TableCellRenderer rendererFromHeader = jtblUsuarioLogin.getTableHeader().getDefaultRenderer();
        JLabel headerLabel = (JLabel) rendererFromHeader;
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        jtblUsuarioLogin.setModel(modelo);
        tcr.setHorizontalAlignment(SwingConstants.CENTER);
        jtblUsuarioLogin.getColumnModel().getColumn(0).setCellRenderer(tcr);
        jtblUsuarioLogin.getColumnModel().getColumn(1).setCellRenderer(tcr);
        jtblUsuarioLogin.getColumnModel().getColumn(2).setCellRenderer(tcr);
        jtblUsuarioLogin.getColumnModel().getColumn(3).setCellRenderer(tcr);
    }

    public void cargarTablaPicada() {
        tcr = new DefaultTableCellRenderer();
        modeloPicada = new DefaultTableModel();
        modeloPicada.addColumn("NR°");
        modeloPicada.addColumn("TIPO REGISTRO");
        modeloPicada.addColumn("HORA REGISTRO(PICADA)");
        modeloPicada.addColumn("FECHA");
        TableCellRenderer rendererFromHeader = jtblTablaPicada.getTableHeader().getDefaultRenderer();
        JLabel headerLabel = (JLabel) rendererFromHeader;
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        jtblTablaPicada.setModel(modeloPicada);
        tcr.setHorizontalAlignment(SwingConstants.CENTER);
        jtblTablaPicada.getColumnModel().getColumn(0).setCellRenderer(tcr);
        jtblTablaPicada.getColumnModel().getColumn(1).setCellRenderer(tcr);
        jtblTablaPicada.getColumnModel().getColumn(2).setCellRenderer(tcr);
        jtblTablaPicada.getColumnModel().getColumn(3).setCellRenderer(tcr);
    }

    public void empezarHilo() {
        hilo = new Thread(this);
        hilo.start();
    }

    @Override
    public void run() {
        Thread ct = Thread.currentThread();

        while (ct == hilo) {
            calcula();
            //jlblReloj.setText(hora + " " + ampm);
            jlblReloj.setText(horaSis);
            horac = horaSis;
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }

        }
    }

    private void calcula() {
        Calendar calendario = Calendar.getInstance();
        fechahora = calendario.getTime();
        fecha =calendario.getTime();
        SimpleDateFormat forhora = new SimpleDateFormat("HH:mm:ss");
        //SimpleDateFormat forfecha = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat forfecha = new SimpleDateFormat("dd-MM-yyyy");
        
        calendario.setTime(fechahora);
        calendario.setTime(fecha);
        fechaSis=forfecha.format(fecha);
        horaSis=forhora.format(fechahora);
    }

//    public Date generarHora(String min) {
//        
//        try {
//            SimpleDateFormat forhora = new SimpleDateFormat("hh:mm");
//            Date horaNueva = forhora.parse("19:" + min);
//            return horaNueva;
//        } catch (ParseException ex) {
//            JOptionPane.showMessageDialog(null, ex);
//        }
//        return fechahora;
//    }
    public void llenartablaInfoDocente() {
        String sql = "";
        sql = "SELECT d.ced_doc,j.ent_jor,j.sal_jor from docentes as d,jornadas as j where j.doc_asi=" + Docente.cedula;
        String[] datos = new String[4];
        try {
            Statement st = reg.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                datos[0] = rs.getString("d.ced_doc");
                datos[2] = rs.getString("j.ent_jor");
                datos[3] = rs.getString("j.sal_jor");
                modelo.addRow(datos);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }
    public void llenartablaRegistrosPicados() {
        String sql = "";
        sql = "SELECT * FROM `asistencias` WHERE id_jor_asi LIKE"+"'"+ Docente.cedula+"'";
        String[] datos = new String[5];
        try {
            Statement st = reg.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                datos[0] = rs.getString("n");
                datos[1] = rs.getString("tipo_reg");
                datos[2] = rs.getString("hora_reg");
                datos[3] = rs.getString("fecha_reg");
                modeloPicada.addRow(datos);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }
    public void registrarAsistencia(String hora,String fecha) {

        try {
            Conexion c1 = new Conexion();
            Connection con = c1.conexion();
            String sql = "INSERT INTO `asistencias`(`tipo_reg`,`hora_reg`,`fecha_reg`,`id_jor_asi`) VALUES (? , ?, ?, ?)";
            PreparedStatement pst = reg.prepareCall(sql);
            pst.setString(1, "Sin Registrar");
            pst.setString(2, hora);
            pst.setString(3, fecha);
            pst.setString(4, Docente.cedula);
            
            int n = pst.executeUpdate();
            //tabla("");

            if (n > 0) {
                JOptionPane.showMessageDialog(null, "Asistencia Registrada");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
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
        jPanel3 = new javax.swing.JPanel();
        jlblReloj = new javax.swing.JLabel();
        jbtnMarcarAsistencia = new javax.swing.JButton();
        jbtnCancelar = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtblUsuarioLogin = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtblTablaPicada = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(102, 0, 0));

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("REGISTRO DE ASISTENCIA");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
        );

        jPanel3.setBackground(new java.awt.Color(245, 117, 117));
        jPanel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true));

        jlblReloj.setFont(new java.awt.Font("Leelawadee", 1, 60)); // NOI18N
        jlblReloj.setForeground(new java.awt.Color(255, 255, 255));
        jlblReloj.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlblReloj.setText("10  :  30  :  59");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlblReloj, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlblReloj, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                .addContainerGap())
        );

        jbtnMarcarAsistencia.setBackground(java.awt.Color.green);
        jbtnMarcarAsistencia.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jbtnMarcarAsistencia.setForeground(new java.awt.Color(255, 255, 255));
        jbtnMarcarAsistencia.setText("Registrar Salida");
        jbtnMarcarAsistencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnMarcarAsistenciaActionPerformed(evt);
            }
        });

        jbtnCancelar.setBackground(java.awt.Color.red);
        jbtnCancelar.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jbtnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        jbtnCancelar.setText("Cancelar");
        jbtnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCancelarActionPerformed(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jtblUsuarioLogin.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jtblUsuarioLogin.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"1803459450", "Salida", "14:00", "17:00"}
            },
            new String [] {
                "CEDULASSSSS", "TIPO REGISTROSSSS", "ENTRADA JORNADASSS", "SALIDA JORNADA"
            }
        ));
        jScrollPane1.setViewportView(jtblUsuarioLogin);

        jLabel3.setBackground(new java.awt.Color(102, 0, 0));
        jLabel3.setFont(new java.awt.Font("Times New Roman", 1, 13)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Jornadas Pendientes");
        jLabel3.setOpaque(true);

        jtblTablaPicada.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "NRSSSS", "TIPO REGISTROSSS", "HORA REGISTROS", "FECHASSSS"
            }
        ));
        jScrollPane2.setViewportView(jtblTablaPicada);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(128, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(jbtnMarcarAsistencia, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 79, Short.MAX_VALUE)
                        .addComponent(jbtnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jbtnMarcarAsistencia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbtnCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnMarcarAsistenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnMarcarAsistenciaActionPerformed
        // TODO add your handling code here:
        registrarAsistencia(horaSis,fechaSis);
        llenartablaRegistrosPicados();
    }//GEN-LAST:event_jbtnMarcarAsistenciaActionPerformed

    private void jbtnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelarActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jbtnCancelarActionPerformed

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
            java.util.logging.Logger.getLogger(RegistroAsistencia.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistroAsistencia.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistroAsistencia.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistroAsistencia.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RegistroAsistencia().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jbtnCancelar;
    private javax.swing.JButton jbtnMarcarAsistencia;
    private javax.swing.JLabel jlblReloj;
    private javax.swing.JTable jtblTablaPicada;
    private javax.swing.JTable jtblUsuarioLogin;
    // End of variables declaration//GEN-END:variables
}
