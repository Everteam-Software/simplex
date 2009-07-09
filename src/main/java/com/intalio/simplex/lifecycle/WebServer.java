package com.intalio.simplex.lifecycle;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.SessionHandler;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import com.sun.jersey.spi.container.servlet.ServletContainer;

import java.io.File;
import java.io.FilenameFilter;

public class WebServer {

    protected File _scriptsDir;
    protected File _workDir;

    private static Server _server;

    public WebServer() { }

    public WebServer(File scriptsDir, File workDir) {
        _scriptsDir = scriptsDir;
        _workDir = workDir;
    }

    public void start() {
        _server = new Server(3434);
        ContextHandler context = new ContextHandler("/");
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        _server.addHandler(context);

        HandlerList handlerList = new HandlerList();

        ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter("com.sun.jersey.config.property.resourceConfigClass",
                "com.sun.jersey.api.core.PackagesResourceConfig");
        sh.setInitParameter("com.sun.jersey.config.property.packages", "com.intalio.simplex.http");
        ServletHandler shh = new ServletHandler();
        shh.addServletWithMapping(sh, "/");
        SessionHandler sessionHandler = new SessionHandler();
        sessionHandler.setHandler(shh);

        if (_workDir != null && _scriptsDir != null) {
            // Serving  built-in public html
            ResourceHandler phrh = new ResourceHandler();
            File phDir = new File(_workDir, "public_html");
            phrh.setResourceBase(phDir.getAbsolutePath());
            handlerList.addHandler(phrh);

            // Serving files in the script directory in addition to Jersey resources
            ResourceHandler rh = new ResourceHandler();
            rh.setResourceBase(_scriptsDir.getAbsolutePath());
            handlerList.addHandler(rh);

            // bootstrap all the war files and folders
            // from the webapps folder when standalone
            //
            FilenameFilter appFilter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".war") || new File(dir+File.separator+name).isDirectory();
                }
            };

            String webappFolder = _workDir + "/../webapps";
            File simplexHome = new File(webappFolder);
            File[] warFiles = simplexHome.listFiles(appFilter);
            if(warFiles!=null) {
                for (File warFile : warFiles) {
                    WebAppContext webapp = new WebAppContext();
                    String warName = warFile.getName();
                    String appName = (warFile.isDirectory()) ? warName:warName.substring(0,warName.indexOf(".war"));
                    webapp.setContextPath("/" + appName);
                    webapp.setWar(warFile.getAbsolutePath());
                    handlerList.addHandler(webapp);
                }
            }
        }

        handlerList.addHandler(sessionHandler);

        context.setHandler(handlerList);
        try {
            _server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void stop() {
        try {
            if (_server != null) _server.stop();
            _server = null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
