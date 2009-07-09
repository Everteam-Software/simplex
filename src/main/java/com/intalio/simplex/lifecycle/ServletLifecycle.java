package com.intalio.simplex.lifecycle;

import com.intalio.simplex.embed.EmbeddedLifecycle;
import com.intalio.simplex.Options;

import javax.servlet.ServletContext;
import java.io.File;

public class ServletLifecycle extends EmbeddedLifecycle {

    protected File _scriptsDir;
    protected File _workDir;    

    public ServletLifecycle(ServletContext context, Options options) {
        super(options);
        // The eternal question: where are we?
        File rootLocation;
        if (System.getProperty("simplex.root") != null) rootLocation = new File(System.getProperty("simplex.root"));
        else rootLocation = new File(context.getRealPath("WEB-INF"));

        String sysScriptsDir = System.getProperty("simplex.script.dir");
        _scriptsDir = sysScriptsDir != null ? new File(sysScriptsDir) : new File(rootLocation, "scripts");
        if (!_scriptsDir.exists()) _scriptsDir.mkdirs();

        String sysWorkDir = System.getProperty("simplex.work.dir");
        _workDir = sysWorkDir != null ? new File(sysWorkDir) : new File(rootLocation, "work");
        if (!_workDir.exists()) _workDir.mkdirs();
    }

    protected void initProcessStore() {
        _store = new ScriptBasedStore(_scriptsDir, _workDir);
        _store.registerListener(new ProcessStoreListenerImpl());
    }

}
