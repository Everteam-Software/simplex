package com.intalio.simplex.lifecycle;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.api.core.ResourceConfig;
import com.intalio.simplex.http.EngineWebResource;
import com.intalio.simplex.Options;

import javax.servlet.ServletConfig;
import java.io.File;

/**
 * Extends the Jerset servlet to initialize the engine
 */
public class Servlet extends ServletContainer {

    ServletLifecycle slf;

    @Override
    protected void configure(ServletConfig servletConfig, ResourceConfig resourceConfig, WebApplication webApplication) {
        super.configure(servletConfig, resourceConfig, webApplication);
//        resourceConfig.getRootResourceClasses().add(EngineWebResource.class);

        Options options = new Options();
        String ds = servletConfig.getInitParameter("com.intalio.simplex.datasource");
        if (ds != null) options.setDatasource(ds);
        String tm = servletConfig.getInitParameter("com.intalio.simplex.transaction");
        if (tm != null) options.setTransactionManager(ds);

        slf = new ServletLifecycle(servletConfig.getServletContext(), options);
        slf.start();
    }

//    @Override
//    protected void initiate(ResourceConfig resourceConfig, WebApplication webApplication) {
//        super.initiate(resourceConfig, webApplication);
//    }

    @Override
    public void destroy() {
        super.destroy();
        slf.clean();
    }
}
