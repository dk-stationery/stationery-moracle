package org.tommy.stationery.moracle.core.optimizer;

import com.espertech.esper.client.EPRuntime;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.BlockingDeque;

/**
 * Created by kun7788 on 15. 5. 13..
 */
public class EsperEventSender extends AbstractExecutionThreadService {
    public static Logger logger = LoggerFactory.getLogger(EsperEventSender.class);
    private EPRuntime runtime;
    private String streamName;

    static class WriteCtx {
        public final Map<String, Object> data;
        public WriteCtx( Map<String, Object> data) {
            this.data = data;
        }
    }

    private final BlockingDeque<WriteCtx> deque = Queues.newLinkedBlockingDeque();

    /**
     * ctor
     * @throws java.sql.SQLException
     */
    public EsperEventSender(EPRuntime runtime, String streamName) throws SQLException {
        this.runtime = runtime;
        this.streamName = streamName;
    }

    /**
     * writeAsync
     */
    public void writeAsync(Map<String, Object> data) {
        deque.addLast(new WriteCtx(data));
    }

    @Override
    protected void startUp() throws Exception {
    }

    @Override
    protected void shutDown() throws Exception {
    }

    @Override
    protected void triggerShutdown() {
        deque.addLast(new WriteCtx(null));
    }

    @Override
    protected void run() throws Exception {
        while (deque.size() != 0) {
            WriteCtx writeCtx = deque.takeFirst();
            Map<String, Object> data = writeCtx.data;
            if (data != null) { // sentinel value
                try {
                    runtime.sendEvent(data, streamName);
                } catch (Exception e) {
                    //deque.putFirst(writeCtx); // undo
                    Thread.sleep(1);
                }
            }
        }
    }
}
