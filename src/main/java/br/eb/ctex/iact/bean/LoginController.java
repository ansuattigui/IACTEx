/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.eb.ctex.iact.bean;

import br.eb.ctex.iactex.modelo.Perfil;
import br.eb.ctex.iactex.modelo.Usuarios;
import br.eb.ctex.iactex.util.JsfUtil;
import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;

/**
 *
 * @author Ralfh
 */
@Named("loginController")
@SessionScoped
public class LoginController implements Serializable {
    
    @EJB 
    private UsuariosFacade ejbFacade;
    
    private String senha;
    private String msg;
    private String usuario;
    private Usuarios userLoggedIn;
    private Perfil perfilLogged;
    
    private String senhaAtual;
    private String novaSenha;
    private String novaSenhaConfirma;
    private Boolean novaSenhaOK;
    
    private static final long serialVersionUID = 1094801825228386363L;
    
    public LoginController() {
    }
    
    public String getSenha() {
        return senha;
    }
 
    public void setSenha(String senha) {
        this.senha = senha;
    }
 
    public String getMsg() {
        return msg;
    }
 
    public void setMsg(String msg) {
        this.msg = msg;
    }
 
    public String getUsuario() {
        return usuario;
    }
 
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    
    public String login() {                
        LCAuthenticator lca = new LCAuthenticator();
        SearchResult srLdap = null;
        try {
            srLdap = lca.login(usuario, senha);
        } catch (NamingException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (srLdap == null) {
            return "index?faces-redirect=true";
        } else {
            
            return "login?faces-redirect=true";
        }
        
        
        
/*        
        FacesContext context = FacesContext.getCurrentInstance();
        String userName = "cn="+usuario+",ou=Usuarios,dc=ldap,dc=ctex";                
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://"+ldapServer+":389");        
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        
        env.put(Context.SECURITY_PRINCIPAL, userName);
        env.put(Context.SECURITY_CREDENTIALS, senha);
       try
        {
            DirContext authContext = new InitialLdapContext(env, null);
//            userLoggedIn = user;
//            perfilLogged = user.getPerfil();
//            context.getExternalContext().getSessionMap().put("usuarioLogado",user);
            return "index?faces-redirect=true";
        }
        catch (NamingException namEx)
        {
            namEx.getCause().printStackTrace();

            context.getExternalContext().getFlash().setKeepMessages(true);
            context.addMessage(null, new FacesMessage("Usuário não encontrado"));
            return "login?faces-redirect=true";
        }        
*/        
    }
/* 
    //validate login
    public String login() {        
        Usuarios user = ejbFacade.validate(usuario, senha);   
        FacesContext context = FacesContext.getCurrentInstance();
        if (user!=null) {
            userLoggedIn = user;
            perfilLogged = user.getPerfil();
            context.getExternalContext().getSessionMap().put("usuarioLogado",user);
            return "index?faces-redirect=true";
        } else {
            context.getExternalContext().getFlash().setKeepMessages(true);
            context.addMessage(null, new FacesMessage("Usuário não encontrado"));
            return "login?faces-redirect=true";
        }
    }
*/
    
    //logout event, invalidate session
    public String logout() {    
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().remove("usuarioLogado");
        context.getExternalContext().invalidateSession();        
        return "/login?faces-redirect=true";
    }  
    
    public String senhaAterada() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().remove("usuarioLogado");
        context.getExternalContext().invalidateSession();        
        return "/login_fail?faces-redirect=true";
    }
    
    

    /**
     * @return the ejbFacade
     */
    private UsuariosFacade getEjbFacade() {
        return ejbFacade;
    }

    /**
     * @return the userLoggedIn
     */
    public Usuarios returnUserLoggedIn() {
        return userLoggedIn;
    }

    public Usuarios getUserLoggedIn() {
        return userLoggedIn;
    }
    
    /**
     * @param userLoggedIn the userLoggedIn to set
     */
    public void setUserLoggedIn(Usuarios userLoggedIn) {
        this.userLoggedIn = userLoggedIn;
    }

    /**
     * @return the perfilLogged
     */
    public Perfil getPerfilLogged() {
        return perfilLogged;
    }

    /**
     * @param perfilLogged the perfilLogged to set
     */
    public void setPerfilLogged(Perfil perfilLogged) {
        this.perfilLogged = perfilLogged;
    }

    /**
     * @return the novaSenha
     */
    public String getNovaSenha() {
        return novaSenha;
    }

    /**
     * @param novaSenha the novaSenha to set
     */
    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }

    /**
     * @return the novaSenhaConfirma
     */
    public String getNovaSenhaConfirma() {
        return novaSenhaConfirma;
    }

    /**
     * @param novaSenhaConfirma the novaSenhaConfirma to set
     */
    public void setNovaSenhaConfirma(String novaSenhaConfirma) {
        this.novaSenhaConfirma = novaSenhaConfirma;
    }
    
    public String confirmaSenhaAtual() throws InterruptedException {
        FacesContext context = FacesContext.getCurrentInstance();

        if (userLoggedIn.getSenha().equals(JsfUtil.encryptPassword(senhaAtual))) {
            if (novaSenha.equals(novaSenhaConfirma)) {
                if (ejbFacade.mudaSenha(novaSenha, userLoggedIn)) {                
                    context.addMessage(null, new FacesMessage(ResourceBundle.getBundle("/Bundle").getString("SenhaAlterada")));                    
                    return this.senhaAterada();
                } else {
                    context.addMessage(null, new FacesMessage(ResourceBundle.getBundle("/Bundle").getString("SenhaNaoAlterada")));
                    return "/user/passChange/passChange";
                }
            } else {
                context.addMessage(null, new FacesMessage(ResourceBundle.getBundle("/Bundle").getString("SenhasDiferentes")));
                return "/user/passChange/passChange";
            }            
        } else {
            context.addMessage(null, new FacesMessage(ResourceBundle.getBundle("/Bundle").getString("SenhaInvalida")));
            return "/user/passChange/passChange";
        }
    }

    /**
     * @return the senhaAtual
     */
    public String getSenhaAtual() {
        return senhaAtual;
    }

    /**
     * @param senhaAtual the senhaAtual to set
     */
    public void setSenhaAtual(String senhaAtual) {
        this.senhaAtual = senhaAtual;
    }

    /**
     * @return the novaSenhaOK
     */
    public Boolean getNovaSenhaOK() {
        return novaSenhaOK;
    }

    /**
     * @param novaSenhaOK the novaSenhaOK to set
     */
    public void setNovaSenhaOK(Boolean novaSenhaOK) {
        this.novaSenhaOK = novaSenhaOK;
    }

}
