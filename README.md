# BilleteraVirtual :dollar::credit_card:

_Web API. Este proyecto es una simulación de una billetera virtual semejante a las que estan en el mercado actualmente._

## Funcionalidades

* **Registro y login de usuarios**. Se guardan el nombre y la contraseña (encriptada).
* **Creación de cuentas**. Tanto en ARS como en otras monedas.
* **Realización de operaciones**. Se pueden hacer recargas a la cuenta, recibir y enviar dinero.
* **Consulta de saldos y movimientos**. Se pueden mostrar todos o de una cuenta en particular.
* **Envio de notificaiones por email**. Al realizar el registro por 1ra vez y cuando se realiza alguna operación.

_Este proyecto fue construido con Spring Boot, MySQL, [Mailgun](https://www.mailgun.com/) para el envio de emails y [JWT](https://jwt.io/) para la parte de seguridad.
También se realizaron distintos test unitarios con junit._

## Deploy 🚀

_Esta API fue deployada en [Heroku](https://heroku.com) y se utilizó Postgre como base de datos en la nube._

Link de la documentación hecha en **Postman**: https://documenter.getpostman.com/view/12251564/T1DwbtDZ
