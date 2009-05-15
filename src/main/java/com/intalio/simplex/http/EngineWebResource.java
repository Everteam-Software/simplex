/*
 * Simplex, lightweight SimPEL server
 * Copyright (C) 2008-2009  Intalio, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.intalio.simplex.http;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.MatchResult;

import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.ode.bpel.iapi.Resource;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.SessionHandler;
import org.mortbay.jetty.webapp.WebAppContext;

import com.intalio.simplex.embed.ServerLifecycle;
import com.intalio.simplex.lifecycle.StandaloneLifecycle;
import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.spi.container.servlet.ServletContainer;

@Path("/")
public class EngineWebResource {

    private static Server _server;
    private static ServerLifecycle _serverLifecyle;

    private static ConcurrentHashMap<UriTemplate,ResourceDesc> _engineResources;

    @Path("{subpath}")
    public ProcessWebResource buildProcessResource(@javax.ws.rs.core.Context UriInfo subpath) {
        Object[] rdesc = findResource(subpath.getRequestUri().getPath());
        if (rdesc == null) throw new NotFoundException("No known resource at this location.");
        else if (((ResourceDesc)rdesc[0]).removed) throw new WebApplicationException(Response.status(410)
                .entity("The resource isn't available anymore.").type("text/plain").build());
        else {
            return new ProcessWebResource((ResourceDesc)rdesc[0], _serverLifecyle,
                    getRoot(subpath.getRequestUri()), (HashMap<String,String>)rdesc[1]);
        }
    }

    private Object[] findResource(String url) {
        String surl = stripSlashes(url);
        for (Map.Entry<UriTemplate, ResourceDesc> resourceDesc : _engineResources.entrySet()) {
            MatchResult mr;
            if ((mr = resourceDesc.getKey().getPattern().match(surl)) != null) {
                HashMap<String,String> params = new HashMap<String,String>();
                List<String> vars = resourceDesc.getKey().getTemplateVariables();
                for (int m = 0; m < mr.groupCount(); m++)
                    params.put(vars.get(m), mr.group(m+1));
                return new Object[] { resourceDesc.getValue(), params };
            }
        }
        return null;
    }

    private static String stripSlashes(String sl) {
        int start = sl.charAt(0) == '/' ? 1 : 0;
        int end = sl.charAt(sl.length()-1) == '/' ? sl.length() - 1 : sl.length();
        return sl.substring(start, end);
    }

    public static void registerResource(Resource resource) {
        String nonSlashed = stripSlashes(resource.getUrl());
        ResourceDesc desc = _engineResources.get(new UriTemplate(nonSlashed));
        if (desc == null) {
            desc = new ResourceDesc();
            desc.resourcePath = nonSlashed;
            _engineResources.put(new UriTemplate(nonSlashed), desc);
        } else {
            desc.removed = false;
        }
        desc.enable(resource.getMethod());
    }

    public static void unregisterResource(Resource resource) {
        ResourceDesc rdesc = _engineResources.get(new UriTemplate(stripSlashes(resource.getUrl())));
        rdesc.removed = true;
        // TODO eventually cleanup removed resources after a while
    }

    public static void startRestfulServer(ServerLifecycle serverLifecyle) {
        _serverLifecyle = serverLifecyle;
        _engineResources = new ConcurrentHashMap<UriTemplate,ResourceDesc>();

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

        if (_serverLifecyle instanceof StandaloneLifecycle) {
            StandaloneLifecycle standaloneLifecycle = (StandaloneLifecycle) _serverLifecyle;

            // Serving  built-in public html
            ResourceHandler phrh = new ResourceHandler();
            File phDir = new File(standaloneLifecycle.getWorkDir(), "public_html");
            phrh.setResourceBase(phDir.getAbsolutePath());
            handlerList.addHandler(phrh);

            // Serving files in the script directory in addition to Jersey resources
            ResourceHandler rh = new ResourceHandler();
            rh.setResourceBase(standaloneLifecycle.getScriptsDir().getAbsolutePath());
            handlerList.addHandler(rh);
            
            // bootstrap all the war files and folders 
            // from the webapps folder when standalone
			//
			FilenameFilter appFilter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".war") || new File(dir+File.separator+name).isDirectory();
				}
			};
			
			String webappFolder = standaloneLifecycle.getWorkDir()+ "/../webapps";
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

    public static void stopRestfulServer() {
        try {
            _server.stop();
            _server = null;
            _serverLifecyle = null;
            _engineResources = null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class ResourceDesc {
        String resourcePath;
        String contentType;
        boolean get;
        boolean post;
        boolean put;
        boolean delete;
        boolean removed;

        public Resource toResource(String method) {
            return new Resource("/"+resourcePath, contentType, method);
        }

        public void enable(String method) {
            if ("GET".equalsIgnoreCase(method)) get = true;
            else if ("POST".equalsIgnoreCase(method)) post = true;
            else if ("PUT".equalsIgnoreCase(method)) put = true;
            else if ("DELETE".equalsIgnoreCase(method)) delete = true;
        }
        public String methods() {
            StringBuffer m = new StringBuffer();
            if (get) m.append("GET");
            if (post) {
                if (m.length() > 0) m.append(",");
                m.append("POST");
            }
            if (put) {
                if (m.length() > 0) m.append(",");
                m.append("PUT");
            }
            if (delete) {
                if (m.length() > 0) m.append(",");
                m.append("DELETE");
            }
            return m.toString();
        }
    }

    private String getRoot(URI uri) {
        return uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort();
    }

}
