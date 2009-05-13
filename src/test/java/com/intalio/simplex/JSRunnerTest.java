package com.intalio.simplex;

import junit.framework.TestCase;
import com.intalio.simpel.util.JSTopLevel;
import com.intalio.simplex.lifecycle.ScriptBasedStore;
import com.intalio.simplex.embed.ServerLifecycle;
import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.ToolErrorReporter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class JSRunnerTest extends TestCase {

    public void testAll() throws Exception {
        Context cx = ContextFactory.getGlobal().enterContext();
        cx.setErrorReporter(new ToolErrorReporter(true));

        File jsTestsDir = new File(getClass().getClassLoader().getResource("js-tests").getFile());
        TestServer server= new TestServer();
        server.start(jsTestsDir);

        String testPath = "forall-counter-predef.js";
        File absoluteTestFile = new File(jsTestsDir, testPath).getAbsoluteFile();
        Scriptable scope = new JSTopLevel(cx, absoluteTestFile.getParent());
        Object ret = null;
        try {
            ret = cx.evaluateReader(scope, new FileReader(absoluteTestFile), testPath, 0, null);
        } catch (JavaScriptException e) {
            e.printStackTrace();
        }
        System.out.println("=> " + ret);
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

class TestLifecycle extends ServerLifecycle {
    File testRoot;

    public TestLifecycle() {
        super(new Options());
    }

    @Override
    protected void initProcessStore() {
        _store = new ScriptBasedStore(testRoot, testRoot);
        _store.registerListener(new ProcessStoreListenerImpl());
    }
}