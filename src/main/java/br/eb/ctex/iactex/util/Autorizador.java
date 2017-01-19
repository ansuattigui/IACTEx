/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.eb.ctex.iactex.util;

import br.eb.ctex.iactex.modelo.Usuarios;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 *
 * @author Ralfh
 */
public class Autorizador implements PhaseListener {

    @Override
    public void afterPhase(PhaseEvent event) {
        
        FacesContext context = event.getFacesContext();
        String nomePagina = context.getViewRoot().getViewId();

        if (("/login.xhtml".equals(nomePagina)) || ("/login_fail.xhtml".equals(nomePagina))) { 
            return;
        }
        
        Usuarios userLoggedIn = (Usuarios) context.getExternalContext()
            .getSessionMap().get("usuarioLogado");

        if(userLoggedIn != null) {
            return;
        }    

        NavigationHandler handler = context.getApplication().getNavigationHandler();
        handler.handleNavigation(context, null, "/login?faces-redirect=true");

        context.renderResponse();            
    }

    @Override
    public void beforePhase(PhaseEvent event) {
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }
    
}
