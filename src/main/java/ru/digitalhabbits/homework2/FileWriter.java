package ru.digitalhabbits.homework2;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.Exchanger;

import static java.lang.Thread.currentThread;
import static org.slf4j.LoggerFactory.getLogger;

public class FileWriter
        implements Runnable {
    private static final Logger logger = getLogger(FileWriter.class);
    private String resultFileName;
    private Exchanger<List<Pair<String, Integer>>> exchanger;

    public FileWriter(String resultFileName, Exchanger<List<Pair<String, Integer>>> exchanger) {
        this.resultFileName = resultFileName;
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        logger.info("Started writer thread {}", currentThread().getName());

        logger.info("Finish writer thread {}", currentThread().getName());
    }
}
