package org.apache.ode.simpel;

import junit.framework.TestCase;
import org.antlr.runtime.RecognitionException;
import org.apache.ode.bpel.rtrep.v2.OProcess;
import org.apache.ode.Descriptor;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * @author Matthieu Riou <mriou@apache.org>
 */
public class SimPELCompilerTest extends TestCase {

    Descriptor desc;

    public SimPELCompilerTest() {
        super();
        desc = new Descriptor();
        desc.setRestful(false);
    }

    /**
     * If this was Ruby, I'd just dynamically create methods for each tested process
     * and we'd have one clean method for each test case. But this is Java so there's
     * only one reported test for all the processes.
     * @throws Exception
     */
    public void testAllOk() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(
                getClass().getClassLoader().getResource("compile-tests-ok.simpel").getFile()));

        int testCount = 0;
        String line;
        StringBuffer processBody = new StringBuffer();
        TestErrorListener l = new TestErrorListener();
        SimPELCompiler comp = new SimPELCompiler();
        comp.setErrorListener(l);
        StringBuffer allErrors = new StringBuffer();
        String testCaseName = "";

        // Priming the pump
        while (!reader.readLine().startsWith("#="));
        testCaseName = reader.readLine().trim().substring(2);
        reader.readLine();reader.readLine();

        while ((line = reader.readLine()) != null) {
            if (line.trim().startsWith("#=")) {
                // Found next test case divider, process is complete so we can parse
                OProcess oprocess = comp.compileProcess(processBody.toString(), desc);
                if (l.messages.toString().length() > 0) {
                    // Shit happened
                    allErrors.append("Test case ").append(testCaseName).append(" failed!!\n");
                    allErrors.append(l.messages.toString()).append("\n");
                } else {
                    System.out.println(oprocess);
                }
                testCount++;

                // Preparing for next test case
                testCaseName = reader.readLine().trim().substring(2);
                reader.readLine();reader.readLine();
                processBody = new StringBuffer();
                l.messages = new StringBuffer();
            } else {
                processBody.append(line).append("\n");
            }
        }

        // And the last one
        try {
            comp.compileProcess(processBody.toString(), desc);
        } catch (Exception e) {
            System.out.println("Error compiling " + testCaseName);
            e.printStackTrace();
        }
        testCount++;
        if (l.messages.toString().length() > 0) {
            // Shit happened
            allErrors.append("Test case ").append(testCaseName).append(" failed!!");
            allErrors.append(l.messages.toString()).append("\n");
        }

        if (allErrors.toString().length() > 0) {
            System.out.println("Some test processes failed to compile:\n");
            System.out.println(allErrors.toString());
            fail("There were failures.");
        } else {
            System.out.println("\nCompiled " + testCount + " processes successfully.");
        }
    }

    public void testLoanApproval() throws Exception {
        SimPELCompiler c = compiler();
        c.compileProcess(readProcess("loan-approval.simpel"), desc);
        reportErrors("Loan approval", c);
    }

    public void testAuction() throws Exception {
        SimPELCompiler c = compiler();
        c.compileProcess(readProcess("auction.simpel"), desc);
        reportErrors("Auction service", c);
    }

    public void testTaskManager() throws Exception {
        SimPELCompiler c = compiler();
        c.compileProcess(readProcess("task-manager.simpel"), desc);
        reportErrors("Auction service", c);
    }

    private String readProcess(String fileName) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(
                getClass().getClassLoader().getResource(fileName).getFile()));

        String line;
        StringBuffer processText = new StringBuffer();
        while ((line = reader.readLine()) != null) processText.append(line).append("\n");
        return processText.toString();
    }

    private SimPELCompiler compiler() {
        TestErrorListener l = new TestErrorListener();
        SimPELCompiler comp = new SimPELCompiler();
        comp.setErrorListener(l);
        return comp;
    }

    private void reportErrors(String testName, SimPELCompiler c) {
        if (((TestErrorListener)c.getErrorListener()).messages.toString().length() > 0) {
            System.out.println(testName+" failed to compile:\n");
            System.out.println(((TestErrorListener)c.getErrorListener()).messages.toString());
            fail("There were failures.");
        }
    }

    private static class TestErrorListener implements ErrorListener {
        public StringBuffer messages = new StringBuffer();

        public void reportRecognitionError(String[] tokens, int line, String message, RecognitionException e) {
            messages.append(" - line ").append(line).append(": ").append(message).append("\n");
        }
    }
}
