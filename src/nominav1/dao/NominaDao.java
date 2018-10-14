/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nominav1.dao;

/**
 *
 * @author dantecervantes
 */
public class NominaDao {
    
    int retardos, faltas;
    double subtotal;
    double inasistencias, inasistenciast;
    
    public NominaDao(){}   
    
    public void setValues(String _retardos, String _inasistencias){
        this.retardos = Integer.parseInt(_retardos);
        this.faltas = retardos / 3;
        this.inasistencias = Integer.parseInt(_inasistencias);
        this.inasistenciast = inasistencias + faltas;
    }
    
    public double getFaltas(){ return inasistenciast; }
}
