/**
 * 
 */
package cn.rongcapital.caas.agent;

/**
 * @author zhaohai
 *
 */
public class VcodeResult extends Base {
    private static final long serialVersionUID = -7241024831411677506L;
    
    private String base64Image;

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }
}