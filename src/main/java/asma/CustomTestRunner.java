package asma;

import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class CustomTestRunner extends BlockJUnit4ClassRunner {

    public CustomTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override public void run(RunNotifier notifier){

        super.run(notifier);
    }

    public static void main(String [] args) throws ClassNotFoundException, InitializationError {
        CustomTestRunner runner = new CustomTestRunner(Class.forName("asma.SimpleSeleniumCase"));
       Result r = null;

       runner.run(new RunNotifier());

    }
}