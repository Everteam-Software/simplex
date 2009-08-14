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
import org.apache.log4j.Logger;

import com.intalio.simplex.lifecycle.EmbeddedLifecycle;
import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.uri.UriTemplate;

/**
 * Main engine resource, caches the list of all process resources the engine knows about and route
 * requests to the appropriate one.
 */
@Path("/")
public class EngineWebResource {

    private static final Logger __log = Logger.getLogger(EngineWebResource.class);

    private static EmbeddedLifecycle _serverLifecyle;

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

    /**
     * Finds the appropriate resource from the map by matching the requested URL against the URI templates of
     * all kown process resources.
     */
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

    /**
     * Strips starting and trailing slashes.
     */
    private static String stripSlashes(String sl) {
        int start = sl.charAt(0) == '/' ? 1 : 0;
        int end = sl.charAt(sl.length()-1) == '/' ? sl.length() - 1 : sl.length();
        return sl.substring(start, end);
    }

    /**
     * Called by the engine to register a knew resource. Needs to be static as EngineWebResource is instantied
     * by Jersey and there's no way (at least now) to get the reference and provide it to the engine.
     * @param resource
     */
    public static void registerResource(Resource resource) {
        if (__log.isDebugEnabled())
            __log.debug("Registering resource " + resource.getUrl() + " / " + resource.getMethod());
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

    public static void setupRestfulServer(EmbeddedLifecycle serverLifecyle) {
        _serverLifecyle = serverLifecyle;
        _engineResources = new ConcurrentHashMap<UriTemplate,ResourceDesc>();
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
