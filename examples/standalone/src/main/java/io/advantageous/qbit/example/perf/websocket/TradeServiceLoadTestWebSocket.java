package io.advantageous.qbit.example.perf.websocket;

import io.advantageous.boon.core.Str;
import io.advantageous.boon.core.Sys;
import io.advantageous.qbit.client.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static io.advantageous.boon.core.IO.puts;
import static io.advantageous.qbit.client.ClientBuilder.clientBuilder;
import static io.advantageous.qbit.service.ServiceProxyUtils.flushServiceProxy;

public class TradeServiceLoadTestWebSocket {


    public static void main(final String... args) {

        /** Hold the number of clients we will run. */
        final int numClients = 3;

        /** Hold the number of calls each thread will make. */
        final int numCalls = 50_000_000;

        /** Hold the client threads to run. */
        final List<Thread> threadList = new ArrayList<>(numClients);

        /** Hold the counts to total. */
        final List<AtomicInteger> counts = new ArrayList<>();


        /** Create the client threads. */
        for (int c =0; c < numClients; c++) {
            final AtomicInteger count = new AtomicInteger();
            counts.add(count);
            threadList.add(new Thread(() -> {
                runCalls(numCalls, count);
            }));
        }

        /** Start the threads. */
        threadList.forEach(Thread::start);

        /** Grab the start time. */
        long startTime = System.currentTimeMillis();

        for (int index =0; index<1000; index++) {
            Sys.sleep(1000);

            long totalCount = 0L;

            for (int c = 0; c < counts.size(); c++) {
                totalCount += counts.get(c).get();
            }

            long seconds = (System.currentTimeMillis()-startTime)/1000;

            puts("total", Str.num(totalCount),
                    "\tseconds", seconds,
                    "\telapsed time", Str.num(System.currentTimeMillis()-startTime),
                    "\trate", Str.num(totalCount/seconds));
        }

    }

    /** Each client will run this
     *
     * @param numCalls number of times to make calls
     * @param count holds the total count
     */
    private static void runCalls(final int numCalls, final AtomicInteger count) {
        final Client client = clientBuilder().setUri("/").build();

        final TradeServiceAsync tradeService = client.createProxy(TradeServiceAsync.class, "t");

        client.startClient();

        for (int call=0; call < numCalls; call++) {
            tradeService.a(response -> {
                if (response) {
                    count.incrementAndGet();
                    //System.out.println("count " + count.get());
                }
            }, new Trade("IBM", 1));

//            tradeService.t2(new Trade("IBM", 1))
//                    .then(response -> {
//                                if (response) {
//                                    count.incrementAndGet();
//                                }
//                            }
//                    )
//                    .catchError(Throwable::printStackTrace)
//                    .invoke();

            if (call % 100 == 0) {
                if (call > count.get() - 2000) {
                    Sys.sleep(1);
                }
                //flushServiceProxy(tradeService);
            }

        }

        flushServiceProxy(tradeService);
    }

}
