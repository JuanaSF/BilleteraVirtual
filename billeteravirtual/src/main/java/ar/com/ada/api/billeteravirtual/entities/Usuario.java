package ar.com.ada.api.billeteravirtual.entities;

import java.util.Date;

public class Usuario {

    private Integer usuarioId;
    private String username;
    private String password;
    private String email;
    private Date fechaLogin;
    private Persona persona;

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getFechaLogin() {
        return fechaLogin;
    }

    public void setFechaLogin(Date fechaLogin) {
        this.fechaLogin = fechaLogin;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

}