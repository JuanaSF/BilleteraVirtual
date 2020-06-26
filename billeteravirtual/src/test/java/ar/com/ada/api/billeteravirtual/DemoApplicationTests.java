package ar.com.ada.api.billeteravirtual;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ar.com.ada.api.billeteravirtual.security.Crypto;

@SpringBootTest
class DemoApplicationTests {


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

}
