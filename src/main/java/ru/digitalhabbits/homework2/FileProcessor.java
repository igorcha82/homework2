package ru.digitalhabbits.homework2;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

import static java.lang.Runtime.getRuntime;
import static java.nio.charset.Charset.defaultCharset;
import static org.slf4j.LoggerFactory.getLogger;

public class FileProcessor {
    private static final Logger logger = getLogger(FileProcessor.class);
    public static final int CHUNK_SIZE = 2 * getRuntime().availableProcessors();
    private final Exchanger<List<Pair<String, Integer>>> exchanger = new Exchanger();
    List<String> stringLine;
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    ExecutorService executorServiceWriter = Executors.newFixedThreadPool(CHUNK_SIZE);
    LineProcessor lineProcessor = new LineCounterProcessor();
    Phaser phaser = new Phaser(CHUNK_SIZE);
    List<Pair<String, Integer>> pairList = new ArrayList<>();

    public void process(@Nonnull String processingFileName, @Nonnull String resultFileName) {
        checkFileExists(processingFileName);

        final File file = new File(processingFileName);

        try (final Scanner scanner = new Scanner(file, defaultCharset())) {
            while (scanner.hasNext()) {

                // TODO: NotImplemented: запускаем FileWriter в отдельном потоке
                FileWriter fileWriter = new FileWriter(resultFileName, exchanger);
                executorService.submit(fileWriter);

                // TODO: NotImplemented: вычитываем CHUNK_SIZE строк для параллельной обработки
                for (int i=0; i<CHUNK_SIZE && scanner.hasNext(); i++)
                {
                    stringLine.add(scanner.nextLine());

                }

                // TODO: NotImplemented: обрабатывать строку с помощью LineProcessor. Каждый поток обрабатывает свою строку.
                for (int i = 0; i<stringLine.size(); i++){
                    int index = i;
                    executorServiceWriter.submit(()->{
                       pairList.set(index, lineProcessor.process(stringLine.get(index)));
                    });
                    phaser.arrive();
                }

                // TODO: NotImplemented: добавить обработанные данные в результирующий файл
                phaser.arriveAndAwaitAdvance();
                exchanger.exchange(pairList);
            }
        } catch (IOException | InterruptedException exception) {
            logger.error("", exception);
        }

        // TODO: NotImplemented: остановить поток writerThread
        executorService.shutdown();
        executorServiceWriter.shutdown();

        logger.info("Finish main thread {}", Thread.currentThread().getName());
    }

    private void checkFileExists(@Nonnull String fileName) {
        final File file = new File(fileName);
        if (!file.exists() || file.isDirectory()) {
            throw new IllegalArgumentException("File '" + fileName + "' not exists");
        }
    }
}
