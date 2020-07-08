package ar.com.ada.api.billeteravirtual;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ar.com.ada.api.billeteravirtual.entities.*;
import ar.com.ada.api.billeteravirtual.entities.Transaccion.ResultadoTransaccionEnum;
import ar.com.ada.api.billeteravirtual.security.Crypto;
import ar.com.ada.api.billeteravirtual.services.*;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	UsuarioService usuarioService;

	@Autowired
	BilleteraService billeteraService;

	@Test
	void EncryptionTest() {

		String textoClaro = "Este es un texto que todos pueden leer";

		// aca va algo que sepamos que cambie por cada usuario o transaccion
		String unSaltoLoco = "un numero random";

		// Aca vamos a dejar el texto encriptado(reversible!)
		String textoEncriptado = "";

		textoEncriptado = Crypto.encrypt(textoClaro, unSaltoLoco);

		//Este print no lo hagan en los testing reales! si bien sirve para buscar, lo mejor es
		//tenerlos desactivados! En tal caso debuguean!
		System.out.println("el texto encriptado es: "+textoEncriptado);

		// Aca vamos a dejar el texto desencriptado de "textoEncryptado"
		String textoDesencriptado = "";
		
		// Desencripto!!
		textoDesencriptado = Crypto.decrypt(textoEncriptado, unSaltoLoco);

		
		// Todo va a estar bien, si el "textoClaro" es igual al "textoDesencriptado";
		assertTrue(textoClaro.equals(textoDesencriptado));

	}


	@Test
	void HashTest() {

		String textoClaro = "Este es un texto que todos pueden leer";

		// aca va algo que sepamos que cambie por cada usuario o transaccion
		String unSaltoLoco = "algo atado al usuario, ej UserId 20";

		// Aca vamos a dejar el texto hasheado(NO reversible)
		String textoHasheado = "";

		textoHasheado = Crypto.hash(textoClaro, unSaltoLoco);


		//Este print no lo hagan en los testing reales! si bien sirve para buscar, lo mejor es
		//tenerlos desactivados! En tal caso debuguean!
		System.out.println("el texto hasheado es: "+textoHasheado);


		// Aca vamos a dejar el texto desencriptado de "textoEncryptado"
		String hashEsperado = "lxT/9Zj6PUyV/xTfCS90qfLMNEL7wnvg8VxsG/slFvZghZvQvFCZQvg584s6TMlkHqJ3wMA2J9rofsERmKGSUg==";

		// Todo va a estar bien, si el hash del texto es el 
		assertTrue(textoHasheado.equals(hashEsperado));

	}

	@Test
	void CrearUsuarioTest() {

		Usuario usuario = usuarioService.crearUsuario("Karen", 32, 1, "21231123", new Date(), "karen@gmail.com",
				"a12345");

		// System.out.println("SALDO de usuario: " +
		// usuario.getPersona().getBilletera().getCuenta("ARS").getSaldo());

		// Usuario usuarioVerificado =
		// usuarioService.buscarPorUsername(usuario.getUsername());

		// assertTrue(usuario.getUsuarioId() == usuarioVerificado.getUsuarioId());
		assertTrue(usuario.getUsuarioId() > 0);
		assertTrue(usuario.getPersona().getBilletera().getCuenta("ARS").getSaldo().equals(new BigDecimal(500)));
	
	}

	@Test
	void EnviarSaldoMonedaARSTest(){

		Usuario usuarioEmisor = usuarioService.crearUsuario("Carolina", 32, 1, "44274555", new Date(), "caro@test.com", "pass123");
		Usuario usuarioReceptor = usuarioService.crearUsuario("Marcos", 32, 1, "37495676", new Date(), "marquitos@test.com", "Micontraseña123");

		Integer bOrigenId = usuarioEmisor.getPersona().getBilletera().getBilleteraId();
		Integer bDestinoId = usuarioReceptor.getPersona().getBilletera().getBilleteraId();

		BigDecimal saldoOrigen = usuarioEmisor.getPersona().getBilletera().getCuenta("ARS").getSaldo();
		BigDecimal saldoDestino = usuarioReceptor.getPersona().getBilletera().getCuenta("ARS").getSaldo();

		BigDecimal saldoAEnviar = new BigDecimal(200);

		ResultadoTransaccionEnum resultado = billeteraService.enviarSaldo(saldoAEnviar, "ARS", bOrigenId, bDestinoId, "Prestamo", "Ya no te debo nada");

		BigDecimal saldoOrigenActualizado = billeteraService.consultarSaldo(bOrigenId, "ARS");
		BigDecimal saldoDestinoActualizado = billeteraService.consultarSaldo(bDestinoId, "ARS");

		/*se usa el compare, que devuelve 0 si son iguales, 
		-1 si el primero es menor que el segundo y 
		1 si el primero es mayor que segundo. */
		
		assertTrue(resultado == ResultadoTransaccionEnum.INICIADA, "El resultado fue" + resultado);
		// AFIRMAMOS QUE, el saldo origen - 200, sea igual al saldoOrigeActualizado
		assertTrue(saldoOrigen.subtract(saldoAEnviar).compareTo(saldoOrigenActualizado) == 0, " HUBO error en la comparacion SOrigen: " + saldoOrigen + " actualizado: " + saldoOrigenActualizado);
		// AFIRMAMOS QUE, el saldo destino + 200, sea igual al saldoDestinoActualizado
		assertTrue(saldoDestino.add(saldoAEnviar).compareTo(saldoDestinoActualizado) == 0, " HUBO error en la comparacion SDestino: " + saldoDestino + " actualizado: " + saldoDestinoActualizado);

	}

	@Test
	void EnviarSaldoMonedaUSDSinSaldoTest() {

		Usuario usuarioEmisor = usuarioService.crearUsuario("Karen Envia", 32, 1, "21231123", new Date(),
				"karenenvia@gmail.com", "a12345");
		Usuario usuarioReceptor = usuarioService.crearUsuario("Claudia Recibe", 32, 1, "21231123", new Date(),
				"claudiarecibe@gmail.com", "a12345");

		Integer borigen = usuarioEmisor.getPersona().getBilletera().getBilleteraId();
		Integer bdestino = usuarioReceptor.getPersona().getBilletera().getBilleteraId();

		BigDecimal saldoAEnviar = new BigDecimal(200);

		ResultadoTransaccionEnum resultado = billeteraService.enviarSaldo(saldoAEnviar, "USD", borigen, bdestino,
				"PRESTAMO", "ya no me debes nada");

		assertTrue(resultado == ResultadoTransaccionEnum.SALDO_INSUFICIENTE, "El resultado fue " + resultado);

	}

	@Test
	void EnviarSaldoNegativoTest() {

		Usuario usuarioEmisor = usuarioService.crearUsuario("Karen Envia", 32, 1, "21231123", new Date(),
				"karenenvia@gmail.com", "a12345");
		Usuario usuarioReceptor = usuarioService.crearUsuario("Claudia Recibe", 32, 1, "21231123", new Date(),
				"claudiarecibe@gmail.com", "a12345");

		Integer borigen = usuarioEmisor.getPersona().getBilletera().getBilleteraId();
		Integer bdestino = usuarioReceptor.getPersona().getBilletera().getBilleteraId();

		ResultadoTransaccionEnum resultado = billeteraService.enviarSaldo(new BigDecimal(-1200), "ARS", borigen,
				bdestino, "PRESTAMO", "ya no me debes nada");

		assertTrue(resultado == ResultadoTransaccionEnum.ERROR_IMPORTE_NEGATIVO, "El resultado fue " + resultado);

	}

	// Hago mas test para practicar

	@Test
	void EnviarSaldoMonedaUSDTest() {

		Usuario usuarioEmisor = usuarioService.crearUsuario("Karen Envia", 32, 1, "21231123", new Date(),
				"karenenvia@gmail.com", "a12345");
		Usuario usuarioReceptor = usuarioService.crearUsuario("Claudia Recibe", 32, 1, "21231123", new Date(),
				"claudiarecibe@gmail.com", "a12345");	

		Integer borigen = usuarioEmisor.getPersona().getBilletera().getBilleteraId();
		Integer bdestino = usuarioReceptor.getPersona().getBilletera().getBilleteraId();

		BigDecimal saldoEnUSD = new BigDecimal(1500);
		//Como el usuario esta recien creado su cuenta en USD va a estar en saldo 0 por eso realizo recarga
		billeteraService.cargarSaldo(saldoEnUSD, "USD", borigen, "Recargo saldo", "Es mi primera recarga en USD");

		BigDecimal saldoAEnviar = new BigDecimal(200);

		ResultadoTransaccionEnum resultado = billeteraService.enviarSaldo(saldoAEnviar, "USD", borigen, bdestino,
				"PRESTAMO", "ya no me debes nada");

		assertTrue(resultado == ResultadoTransaccionEnum.INICIADA, "El resultado fue " + resultado);

	}

	@Test
	void EnviarSaldoABilleteraInexistenten(){

		Usuario usuarioEmisor = usuarioService.crearUsuario("Elena Envia", 76, 30, "21231123", new Date(),
				"e.detroya@gmail.com", "a12345");
		Usuario usuarioReceptor = usuarioService.crearUsuario("Ricardo Recibe", 32, 0, "43561123", new Date(),
				"ricardo@gmail.com", "aguanteElDulceDeLeche");

		Integer borigen = usuarioEmisor.getPersona().getBilletera().getBilleteraId();
		Integer bdestino = 159; // Mando cualquier numero para que no sea encontrado

		BigDecimal saldoAEnviar = new BigDecimal(200);

		ResultadoTransaccionEnum resultado = billeteraService.enviarSaldo(saldoAEnviar, "USD", borigen, bdestino,
				"PRESTAMO", "ya no me debes nada");

		assertTrue(resultado == ResultadoTransaccionEnum.BILLETERA_DESTINO_NO_ENCONTRADA, "El resultado fue " + resultado);
	}



}
