/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tomhume.webapps;

import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author twhume
 */
@Local
public interface PortfolioControllerLocal {

    List<ShareTransaction> list();

    void add(ShareTransaction transaction);

    void delete(ShareTransaction transaction);
    
}
