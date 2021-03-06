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

package com.intalio.simplex.lifecycle;

import com.intalio.simplex.Options;
import com.intalio.simplex.lifecycle.EmbeddedLifecycle;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class StandaloneLifecycle extends EmbeddedLifecycle {
    
    private static final Logger __log = Logger.getLogger(StandaloneLifecycle.class);

    protected File _scriptsDir;
    protected File _workDir;
    protected File _libDir;
    protected WebServer _webServer;

    public StandaloneLifecycle(File serverRoot, Options options) {
        super(options);

        String sysScriptsDir = System.getProperty("simplex.script.dir");
        _scriptsDir = sysScriptsDir != null ? new File(sysScriptsDir) : new File(serverRoot, "scripts");
        if (!_scriptsDir.exists()) _scriptsDir.mkdirs();

        String sysWorkDir = System.getProperty("simplex.work.dir");
        _workDir = sysWorkDir != null ? new File(sysWorkDir) : new File(serverRoot, "work");
        if (!_workDir.exists()) _workDir.mkdirs();

        _libDir = new File(serverRoot, "lib");
        unzipPublicHtml();
    }

    @Override
    protected void initProcessStore() {
        _store = new ScriptBasedStore(_scriptsDir, _workDir);
        _store.registerListener(new ProcessStoreListenerImpl());
    }

    @Override
    public void clean() {
        super.clean();
        _webServer.stop();
    }

    @Override
    protected void initRestfulServer() {
        super.initRestfulServer();
        _webServer = new WebServer(_scriptsDir, _workDir);
        _webServer.start();
    }

    private void unzipPublicHtml() {
        File[] fileList = _libDir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith("simplex-public-html");
                }
            });
        if (fileList != null) {
            try {
                ZipInputStream zis = new ZipInputStream(new FileInputStream(fileList[0]));
                ZipEntry entry;
                // Processing the package
                while((entry = zis.getNextEntry()) != null) {
                    if(entry.isDirectory()) {
                        new File(_workDir, entry.getName()).mkdir();
                        continue;
                    }

                    File destFile = new File(_workDir, entry.getName());
                    if (!destFile.getParentFile().exists()) destFile.getParentFile().mkdirs();

                    copyInputStream(zis, new BufferedOutputStream(new FileOutputStream(destFile)));
                }
                zis.close();
            } catch (IOException e) {
                throw new RuntimeException("Unzipping public HTML resources failed.", e);
            }
        }
    }

    private static void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while((len = in.read(buffer)) >= 0) out.write(buffer, 0, len);
        out.close();
    }

}
