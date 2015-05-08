package org.tommy.stationery.moracle.core.optimizer;

import com.espertech.esper.client.EPRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tommy.stationery.moracle.core.domain.Config;
import org.tommy.stationery.moracle.core.domain.MStream;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by kun7788 on 15. 4. 29..
 */
public class InputExecutor {
    private static final Logger logger = LoggerFactory.getLogger(InputExecutor.class);

    private Config config;
    private Map<String, List<String>> headers;
    private EPRuntime runtime;
    private List<MStream> streams;
    private ExecutorService executorThreadPool = Executors.newFixedThreadPool(10);
    private Set<Future<Boolean>> asyncFutures;

    public InputExecutor(Map<String, List<String>> headers, Config config, EPRuntime runtime, List<MStream> streams) {
        this.config = config;
        this.headers = headers;
        this.runtime = runtime;
        this.streams = streams;
        this.asyncFutures = new HashSet<Future<Boolean>>();
    }

    public void close() {
        executorThreadPool.shutdown();
        executorThreadPool = null;
    }

    public boolean polling() throws Exception {
        for (MStream stream : streams) {
            Callable<Boolean> callable = new InputCallable(headers, config, runtime, stream);
            Future<Boolean> future = executorThreadPool.submit(callable);
            asyncFutures.add(future);
        }

        boolean result = true;
        for (Future<Boolean> asyncFuture : asyncFutures) {
            result =  asyncFuture.get().booleanValue();
            if (result == false) {
                logger.error("error");
                System.exit(1);
            }
        }

        logger.info("end");
        return true;
    }
}
