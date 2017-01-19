/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.eb.ctex.iact.bean;

import br.eb.ctex.iactex.modelo.Usuarios;
import br.eb.ctex.iactex.util.JsfUtil;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author Ralfh
 */
@Stateless
public class UsuariosFacade extends AbstractFacade<Usuarios> {

    @PersistenceContext(unitName = "iactex")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuariosFacade() {
        super(Usuarios.class);
    }
    
    public Usuarios validate(String usuario, String senha)  {
        Usuarios valido;
        TypedQuery<Usuarios> tq = getEntityManager().createNamedQuery("Usuarios.findByLogin", Usuarios.class);
        tq.setParameter("pusername", usuario);
        tq.setParameter("ppassword", JsfUtil.encryptPassword(senha));
        try {
            valido = (tq.getSingleResult());
        } catch (NoResultException nre) {
            valido = null;
        }
        return valido;
    }
    
    public Boolean mudaSenha(String novaSenha, Usuarios user) {
        Integer result = 0;
        
        Query q = getEntityManager().createNativeQuery("UPDATE usuarios SET senha = ? WHERE id = ?");
        q.setParameter(1, JsfUtil.encryptPassword(novaSenha));
        q.setParameter(2, user.getId());
        
        result = q.executeUpdate();
        
        return (result==1);
    }
}
