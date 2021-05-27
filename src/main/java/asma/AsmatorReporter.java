package asma;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import asma.AsmatorProcessMngr;

import asma.*;

public class AsmatorReporter extends RunListener  {

    JSONObject root_j = new JSONObject();
    JSONObject result_j = new JSONObject();
    JSONObject test_result_j = new JSONObject();
    Map<String,JSONObject> tests = new HashMap<String,JSONObject>();
    String jid;
    String geoloc;
    String agent_name;
    String agent_id;
    String resultsPath;
    String outputFilePath;
    String alloc_dir;

    public void spawnProcessor(String json) throws IOException {

        Process process;
        process = Runtime.getRuntime().exec(String.format("node ./asma-processor.js %s", json));

        AsmatorProcessMngr mngr = new AsmatorProcessMngr(process.getInputStream(), System.out::println);
        Executors.newSingleThreadExecutor().submit(mngr);

    }

    public void testRunStarted(Description description) throws Exception {

        jid = System.getenv("NOMAD_ALLOC_ID");
        geoloc = System.getenv("NOMAD_META_GEOLOC");
        agent_name = System.getenv("NOMAD_NODE_NAME");
        agent_id = System.getenv( "NOMAD_IDENTIFIER");
        resultsPath = System.getenv("ASMA_RESULTS_DIR");
        alloc_dir = System.getenv("NOMAD_ALLOC_DIR");
        outputFilePath = resultsPath + jid + "/result.json";

        root_j.put("message" , description.testCount());
        root_j.put("start_timestamp_ms",new Date().getTime());
        root_j.put("script",description.getClassName());
        root_j.put("check_guid","2983332e-0e57-43df-a81b-179318e11aa0");
        root_j.put("timeout",60.0);
        root_j.put("user",agent_id);
        root_j.put("agent_id",agent_id);
        root_j.put("agent_name",agent_id);
        root_j.put("geoloc",geoloc);
        root_j.put("worker",geoloc);
        root_j.put("customer_guid","2983332e-0e57-43df-a81b-179318e11aa0");
        root_j.put("attempts",1);
        root_j.put("silo",1);


    }

    public void testRunFinished(Result result) throws Exception {

        StringBuilder sb = new StringBuilder();



        root_j.put("returncode", ( result.wasSuccessful() ) ? 0 : 1);

        root_j.put("severity", ( result.wasSuccessful() ) ? "I" : "F");
        root_j.put("stdout","");
        root_j.put("stderr","");


        result_j.put("end_timestamp_ms",new Date().getTime());
        result_j.put("message",result.getRunCount());
        result_j.put("unit","ms");
        result_j.put("value",result.getRunTime());



        result_j.put("success", ( result.wasSuccessful() ) ? true : false);
        result_j.put("duration",result.getRunTime());
        result_j.put("step-count",result.getRunCount());
        result_j.put("passed",result.getRunCount() - result.getFailureCount());

        result_j.put("failed",result.getFailureCount());
        result_j.put("startTime",root_j.get("end_timestamp_ms"));

        Object[] objectArray = tests.entrySet().toArray();
        result_j.put("steps",new JSONArray(objectArray));
        root_j.put("test_result",result_j);

        spawnProcessor(root_j.toString());


    }

    public void testStarted(Description description) throws Exception {


        JSONObject o = new JSONObject();

        o.put("title",description.getClassName());
        o.put("duration",0);
        o.put("startTime",new Date().getTime());
        o.put("failures",new JSONArray());

        tests.put(Integer.toString(description.hashCode()),o);

    }

    public void testFinished(Description description) throws Exception {

        if ( !tests.containsKey(Integer.toString(description.hashCode()))) {
            throw new RuntimeException("Unidentified test object!");
        }

        JSONObject t = tests.get(Integer.toString(description.hashCode()));

        if ( t != null ) {

            t.put("endTime",new Date().getTime());
            t.put("duration", (t.getLong("endTime") - t.getLong("startTime")));

        }

        tests.put(Integer.toString(description.hashCode()),t);

    }

    public void testFailure(Failure failure) throws Exception {

        if ( !tests.containsKey(Integer.toString(failure.getDescription().hashCode()))) {
            throw new RuntimeException("Unidentified test object!");
        }


        JSONObject t = tests.get(Integer.toString(failure.getDescription().hashCode()));
        JSONObject d = new JSONObject();
        if ( t != null ) {

            t.put("endTime",new Date().getTime());
            t.put("duration", (t.getLong("endTime") - t.getLong("startTime")));
            d.put("message",failure.getMessage());
            t.getJSONArray("failures").put(d);

            tests.put(Integer.toString(t.hashCode()),t);

        }
        /*
        System.out.println("Failed: " + failure.getDescription().getMethodName());
        System.out.println("Details " + failure.getMessage());
        System.out.println(failure);
        current.put("success",false);
        current.getJSONArray("failures").put(failure.getDescription().getMethodName() + ": " + failure.getMessage());
        current.put("endTime",new Date().getTime());

        tests.add(current);

        */

    }

    public void testAssumptionFailure(Failure failure) {

        /*
        System.out.println("Failed: " + failure.getDescription().getMethodName());
        System.out.println(failure);
        */

    }

    public void testIgnored(Description description) throws Exception {
    }
}
