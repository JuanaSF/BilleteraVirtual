package ar.com.ada.api.billeteravirtual.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.com.ada.api.billeteravirtual.entities.*;
import ar.com.ada.api.billeteravirtual.entities.Transaccion.ResultadoTransaccionEnum;
import ar.com.ada.api.billeteravirtual.entities.Transaccion.TipoTransaccionEnum;
import ar.com.ada.api.billeteravirtual.repos.BilleteraRepository;
import ar.com.ada.api.billeteravirtual.sistema.comm.EmailService;

@Service
public class BilleteraService {

    @Autowired
    BilleteraRepository repo;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    EmailService emailService;

    /*
     * 2. Metodo: enviar plata 2.1-- recibir un importe, la moneda en la que va a
     * estar ese importe recibir una billetera de origen y otra de destino 2.2--
     * actualizar los saldos de las cuentas (a una se le suma y a la otra se le
     * resta) 2.3-- generar dos transacciones
     */

    /*
     * 3. Metodo: consultar saldo 3.1-- recibir el id de la billetera y la moneda en
     * la que esta la cuenta
     */

    public void grabar(Billetera billetera) {
        repo.save(billetera);
    }

    public void cargarSaldo(BigDecimal saldo, String moneda, Integer billeteraId, String conceptoOperacion,
            String detalle) {

        Billetera billetera = this.buscarPorId(billeteraId);

        cargarSaldo(saldo, moneda, billetera, conceptoOperacion, detalle);

        emailService.SendEmail(billetera.getPersona().getUsuario().getEmail(), "Recarga Exitosa",
                "Hola, Confirmamos tu recarga de " + saldo + " " + moneda);
    }

    public void cargarSaldo(BigDecimal saldo, String moneda, Billetera billetera, String conceptoOperacion,
            String detalle) {

        Cuenta cuenta = billetera.getCuenta(moneda);

        Transaccion transaccion = new Transaccion();
        transaccion.setMoneda(moneda);
        transaccion.setFecha(new Date());
        transaccion.setConceptoOperacion(conceptoOperacion);
        transaccion.setDetalle(detalle);
        transaccion.setImporte(saldo);
        transaccion.setTipoOperacion(TipoTransaccionEnum.ENTRANTE);
        transaccion.setEstadoId(2);// -1 Rechazada 0 Pendiente 2 Aprobada
        transaccion.setDeCuentaId(cuenta.getCuentaId());
        transaccion.setDeUsuarioId(billetera.getPersona().getUsuario().getUsuarioId());
        transaccion.setaUsuarioId(billetera.getPersona().getUsuario().getUsuarioId());
        transaccion.setaCuentaId(cuenta.getCuentaId());

        cuenta.agregarTransaccion(transaccion);

        this.grabar(billetera);
    }

    public BigDecimal consultarSaldo(Integer billeteraId, String moneda) {

        Billetera billetera = repo.findByBilleteraId(billeteraId);

        Cuenta cuenta = billetera.getCuenta(moneda);

        return cuenta.getSaldo();

    }

    public Billetera buscarPorId(Integer id) {

        return repo.findByBilleteraId(id);
    }

    public ResultadoTransaccionEnum enviarSaldo(BigDecimal importe, String moneda, Integer billeteraOrigenId,
            Integer billeteraDestinoId, String concepto, String detalle) {

        if (importe.compareTo(new BigDecimal(0)) == -1)
            return ResultadoTransaccionEnum.ERROR_IMPORTE_NEGATIVO;
        /**
         * Metodo enviarSaldo buscar billetera por id se identifica cuenta por moneda
         * determinar importe a transferir billetera de origen y billetera destino
         * actualizar los saldos de las cuentas (resta en la origen y suma en la
         * destino) generar 2 transacciones
         * 
         * ver delegaciones sobre entidades
         * 
         */

        Billetera billeteraSaliente = this.buscarPorId(billeteraOrigenId);

        if (billeteraSaliente == null)
            return ResultadoTransaccionEnum.BILLETERA_ORIGEN_NO_ENCONTRADA;

        Billetera billeteraEntrante = this.buscarPorId(billeteraDestinoId);

        if (billeteraEntrante == null)
            return ResultadoTransaccionEnum.BILLETERA_DESTINO_NO_ENCONTRADA;

        Cuenta cuentaSaliente = billeteraSaliente.getCuenta(moneda);

        if (cuentaSaliente == null)
            return ResultadoTransaccionEnum.CUENTA_ORIGEN_INEXISTENTE;

        Cuenta cuentaEntrante = billeteraEntrante.getCuenta(moneda);

        if (cuentaEntrante == null)
            return ResultadoTransaccionEnum.CUENTA_DESTINO_INEXITENTE;

        if (cuentaSaliente.getSaldo().compareTo(importe) == -1)
            return ResultadoTransaccionEnum.SALDO_INSUFICIENTE;

        Transaccion tSaliente = new Transaccion();
        Transaccion tEntrante = new Transaccion();

        tSaliente = cuentaSaliente.generarTransaccion(concepto, detalle, importe, TipoTransaccionEnum.SALIENTE);
        tSaliente.setaCuentaId(cuentaEntrante.getCuentaId());
        tSaliente.setaUsuarioId(billeteraEntrante.getPersona().getUsuario().getUsuarioId());

        tEntrante = cuentaEntrante.generarTransaccion(concepto, detalle, importe, TipoTransaccionEnum.ENTRANTE);
        tEntrante.setDeCuentaId(cuentaSaliente.getCuentaId());
        tEntrante.setDeUsuarioId(billeteraSaliente.getPersona().getUsuario().getUsuarioId());

        cuentaSaliente.agregarTransaccion(tSaliente);
        cuentaEntrante.agregarTransaccion(tEntrante);

        this.grabar(billeteraSaliente);
        this.grabar(billeteraEntrante);

        // se hace envio de mail confirmando el envio al usuario emisor del saldo
        emailService.SendEmail(billeteraSaliente.getPersona().getUsuario().getEmail(), "Envio de saldo",
                "Hola, Te avisamos que el envió de " + importe + " " + moneda + " al usuario "
                        + billeteraEntrante.getPersona().getUsuario().getEmail() + " se realizo correctamente.");
        // se hace envio de mail confirmando el ingreso de saldo al usuario receptor
        emailService.SendEmail(billeteraEntrante.getPersona().getUsuario().getEmail(), "Ya tenes disponible tu dinero",
                "Hola, Recibiste una transferencia de " + importe + " " + moneda + " , por perte de "
                        + billeteraSaliente.getPersona().getUsuario().getEmail());

        return ResultadoTransaccionEnum.INICIADA;

    }

    public ResultadoTransaccionEnum enviarSaldo(BigDecimal importe, String moneda, Integer billeteraOrigenId,
            String email, String concepto, String detalle) {

        Usuario usuarioDestino = usuarioService.buscarPorEmail(email);

        if (usuarioDestino == null)
            return ResultadoTransaccionEnum.EMAIL_DESTINO_INEXISTENTE;
        return this.enviarSaldo(importe, moneda, billeteraOrigenId,
                usuarioDestino.getPersona().getBilletera().getBilleteraId(), concepto, detalle);

    }

    public List<Transaccion> listarTransacciones(Billetera billetera, String moneda) {

        List<Transaccion> movimientos = new ArrayList<>();

        Cuenta cuenta = billetera.getCuenta(moneda);

        for (Transaccion transaccion : cuenta.getTransacciones()) {

            movimientos.add(transaccion);
        }

        return movimientos;
    }

    public List<Transaccion> listarTransacciones(Billetera billetera) {

        List<Transaccion> movimientos = new ArrayList<>();

        for (Cuenta cuenta : billetera.getCuentas()) {

            for (Transaccion transaccion : cuenta.getTransacciones()) {

                movimientos.add(transaccion);
            }
        }
        return movimientos;
    }

}