package navi.benchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

public class JMHRunner {

    public static void main(String[] args) throws RunnerException {
        int numberOfCores = Runtime.getRuntime().availableProcessors();
        int threadsPerCore = 2;
        int totalThreads = numberOfCores * threadsPerCore;
        int durationSeconds = 100;

        Options options = new OptionsBuilder()
                .include(HttpRequestBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(3)
                .measurementIterations(durationSeconds)
                .threads(totalThreads)
                .timeUnit(TimeUnit.SECONDS)
                .measurementTime(TimeValue.seconds(1))
                .build();

        new Runner(options).run();
    }
}