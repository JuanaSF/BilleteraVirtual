package ar.com.ada.api.billeteravirtual.services;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.com.ada.api.billeteravirtual.entities.*;
import ar.com.ada.api.billeteravirtual.repos.BilleteraRepository;

@Service
public class BilleteraService {

    @Autowired
    BilleteraRepository repo;

    /* 2. Metodo: enviar plata
    2.1-- recibir un importe, la moneda en la que va a estar ese importe
    recibir una billetera de origen y otra de destino
    2.2-- actualizar los saldos de las cuentas (a una se le suma y a la otra se le resta)
    2.3-- generar dos transacciones
    */

    /* 3. Metodo: consultar saldo 
    3.1-- recibir el id de la billetera y la moneda en la que esta la cuenta
    */

    public void grabar(Billetera billetera){
        repo.save(billetera);
    }

    public void cargarSaldo(BigDecimal saldo, String moneda, Integer billeteraId, 
    String conceptoOperacion, String detalle){

        Billetera billetera = repo.findByBilleteraId(billeteraId);

        Cuenta cuenta = billetera.getCuenta(moneda);

        Transaccion transaccion = new Transaccion();
        transaccion.setMoneda(moneda);
        transaccion.setFecha(new Date());
        transaccion.setConceptoOperacion(conceptoOperacion);
        transaccion.setDetalle(detalle);
        transaccion.setImporte(saldo);
        transaccion.setTipoOperacion(1);// 1 Entrada, 0 Salida
        transaccion.setEstadoId(2);// -1 Rechazada 0 Pendiente 2 Aprobada
        transaccion.setDeCuentaId(cuenta.getCuentaId());
        transaccion.setDeUsuarioId(billetera.getPersona().getUsuario().getUsuarioId());
        transaccion.setaUsuarioId(billetera.getPersona().getUsuario().getUsuarioId());
        transaccion.setaCuentaId(cuenta.getCuentaId());

        cuenta.agregarTransaccion(transaccion);

        BigDecimal saldoActual = cuenta.getSaldo();
        BigDecimal saldoNuevo = saldoActual.add(saldo);
        cuenta.setSaldo(saldoNuevo);

        this.grabar(billetera);
    }
}