/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.eb.ctex.iact.bean;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

/**
 *
 * @author ralfh
 */

public class LCAuthenticator 
{
  private String ldapHost;
  private String searchBase;
   
  public LCAuthenticator()
  {
    this.ldapHost = "ldap://10.1.62.127:389";
    this.searchBase = "ou=Usuarios,dc=ldap,dc=ctex";
  }
 
  public LCAuthenticator(String host, String dn)
  {
    this.ldapHost = host;
    this.searchBase = dn;
  }
 
  public SearchResult login(String user, String pass) throws NamingException
  {

/*    String returnedAtts[] ={ "sn", "givenName", "mail" };
    String searchFilter = "(&(objectClass=user)(sAMAccountName=" + user + "))";
     
    //Create the search controls
    SearchControls searchCtls = new SearchControls();
    searchCtls.setReturningAttributes(returnedAtts);
     
    //Specify the search scope
    searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
*/
      
     
    Hashtable env = new Hashtable();
    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    env.put(Context.PROVIDER_URL, ldapHost);
    // autenticação/bind no servidor ldap feita como "anonimo"
    env.put(Context.SECURITY_AUTHENTICATION, "none");
    InitialDirContext ctxGC = new InitialDirContext(env);
    
    SearchResult sr = findAccountByAccountName(ctxGC, searchBase, user);
    

/*    
    Map amap = null;     
    try {
      //Search objects in GC using filters
      NamingEnumeration answer = ctxGC.search(searchBase, searchFilter, searchCtls);
      ctxGC.close();
      while (answer.hasMoreElements()) {
        SearchResult sr = (SearchResult) answer.next();
        Attributes attrs = sr.getAttributes();       
        if (attrs != null) {
          amap = new HashMap();
          NamingEnumeration ne = attrs.getAll();
          while (ne.hasMore()) {
            Attribute attr = (Attribute) ne.next();
//            amap.put(attr.getID(), attr.get());
          }
          ne.close();
        }
      }
    }
    catch (NamingException ex)
    {
      ex.printStackTrace();
    }      
    return amap;
*/ 
      return sr;

  }

 public SearchResult findAccountByAccountName(DirContext ctx, String ldapSearchBase, String accountName) throws NamingException {

        String searchFilter = "(&(objectClass=user)(sAMAccountName=" + accountName + "))";

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        NamingEnumeration<SearchResult> results = ctx.search(ldapSearchBase, searchFilter, searchControls);

        SearchResult searchResult = null;
        if(results.hasMoreElements()) {
             searchResult = (SearchResult) results.nextElement();

            //make sure there is not another item available, there should be only 1 match
            if(results.hasMoreElements()) {
                System.err.println("Matched multiple users for the accountName: " + accountName);
                return null;
            }
        }
        
        return searchResult;
    }
 
}

