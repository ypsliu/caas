/**
 * 
 */
package cn.rongcapital.caas.lock;

import javax.servlet.http.HttpSession;

/**
 * @author zhaohai
 *
 */
public interface SessionLock {
    
    /**
     * lock resource by {@code resourceKey} according to session id
     * 
     * @param resourceKey
     * @param session
     * @return lock result
     */
    public boolean lock(String resourceKey, HttpSession session);
    
    /**
     * lock resource by {@code resourceKey} according to session id and set lock timeout seconds
     * 
     * @param resourceKey
     * @param session
     * @param timeoutSeconds
     * @return lock result
     */
    public boolean lock(String resourceKey, HttpSession session, long timeoutSeconds);
    
    /**
     * set lock timeout seconds if current session has lock for resource {@code resourceKey}
     * 
     * @param resourceKey
     * @param session
     * @param timeoutSeconds
     * @return setting result
     */
    public boolean setLockTimeout(String resourceKey, HttpSession session, long timeoutSeconds);
    
    /**
     * release lock for resource {@code resourceKey} if current session has lock
     * 
     * @param resourceKey
     * @param session
     * @return release result
     */
    public boolean unlock(String resourceKey, HttpSession session);
    
    /**
     * check current session if has lock for resource {@code resourceKey}
     * 
     * @param resourceKey
     * @param session
     * @return check result
     */
    public boolean hasLock(String resourceKey, HttpSession session);
}
