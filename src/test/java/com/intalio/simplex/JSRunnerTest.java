package com.intalio.simplex;

import junit.framework.TestCase;
import com.intalio.simpel.util.JSTopLevel;
import com.intalio.simplex.lifecycle.ScriptBasedStore;
import com.intalio.simplex.lifecycle.WebServer;
import com.intalio.simplex.embed.EmbeddedLifecycle;
import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.ToolErrorReporter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class JSRunnerTest extends TestCase {

    public void testAll() throws Exception {
        TestServer server = null;
        try {
            Context cx = ContextFactory.getGlobal().enterContext();
            cx.setErrorReporter(new ToolErrorReporter(true));

            File jsTestsDir = new File(getClass().getClassLoader().getResource("js-tests").getFile());
            server = new TestServer();
            server.start(jsTestsDir);

            String testPath = "test-runner.js";
            File absoluteTestFile = new File(getClass().getClassLoader().getResource(testPath).getFile());
            Scriptable scope = new JSTopLevel(cx, absoluteTestFile.getParent());
            try {
                cx.evaluateReader(scope, new FileReader(absoluteTestFile), testPath, 0, null);
                // Checking result
                Boolean success = (Boolean) cx.evaluateString(scope, "JSpec.stats.failures == 0", "<test>", 0, null);
                assertTrue("Javascript tests failed!", success);
            } catch (JavaScriptException e) {
                e.printStackTrace();
                fail("Script error: " + e);
            }
        } finally {
            if (server != null) server.stop();
        }
    }
}

class TestServer {
    TestLifecycle _resources;

    public void start(File testRoot) throws ClassNotFoundException, IOException {
        _resources = new TestLifecycle();
        _resources.testRoot = testRoot;
        _resources.start();
        // For tests, one poller run is all we need
        ((ScriptBasedStore)_resources.getStore()).getPoller().scanAndDeploy();
    }
    public void stop() {
        _resources.clean();
    }
}

class TestLifecycle extends EmbeddedLifecycle {
    File testRoot;
    WebServer _webServer;

    public TestLifecycle() {
        super(new Options());
    }

    @Override
    protected void initProcessStore() {
        _store = new ScriptBasedStore(testRoot, testRoot);
        _store.registerListener(new ProcessStoreListenerImpl());
    }

    @Override
    protected void initRestfulServer() {
        super.initRestfulServer();
        _webServer = new WebServer();
        _webServer.start();
    }

    @Override
    public void clean() {
        super.clean();
        _webServer.stop();
    }
}