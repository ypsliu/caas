package cn.rongcapital.caas.sample.resource;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class CaasSampleService {
    @PreAuthorize("checkAuth('/sample/bean/'+#id)")
    public SampleBean getBean(final long id) {
        final SampleBean b = new SampleBean();
        b.setSuccess(true);
        b.setMessage("get the sample bean '" + id + "' OK");
        return b;
    }
}
