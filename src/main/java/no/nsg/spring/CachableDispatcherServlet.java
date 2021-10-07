package no.nsg.spring;

import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class CachableDispatcherServlet extends DispatcherServlet {

    @Override
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            if (!(request instanceof ContentCachingRequestWrapper)) {
                request = new ContentCachingRequestWrapper(request);
            }
            super.doDispatch(request, response);
        } catch(Exception e) {
            throw new RuntimeException("doDispatch failed");
        }
    }

}