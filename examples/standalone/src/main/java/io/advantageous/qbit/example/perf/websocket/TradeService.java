package io.advantageous.qbit.example.perf.websocket;

import io.advantageous.qbit.admin.ManagedServiceBuilder;
import io.advantageous.qbit.annotation.RequestMapping;
import io.advantageous.qbit.annotation.Service;
import io.advantageous.qbit.annotation.http.GET;
import io.advantageous.qbit.annotation.http.PUT;
import io.advantageous.qbit.reactive.Callback;
import io.advantageous.reakt.promise.Promise;
import io.advantageous.reakt.promise.Promises;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.advantageous.qbit.admin.ManagedServiceBuilder.managedServiceBuilder;

/**
 * curl  -H "Content-Type: application/json"  -X PUT http://localhost:8080/trade -d '{"name":"ibm", "amount":1}'
 * curl  http://localhost:8080/count
 */
@RequestMapping("/")
@Service("t")
public class TradeService {

    private long count;

    final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @PUT("/trade")
    public void t(Callback<Boolean> cb, final Trade trade) {
        executorService.submit(() -> {
            trade.getNm().hashCode();
            trade.getAmt();
            count++;
            cb.resolve(true);
            try {
                Thread.sleep(199);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

    }



    @PUT("/a")
    public void a(Callback<Boolean> cb, final Trade trade) {
            trade.getNm().hashCode();
            trade.getAmt();
            count++;
            cb.resolve(true);
    }



    @PUT("/trade2")
    public Promise<Boolean> t2(final Trade trade) {
        return Promises.invokablePromise(promise -> {
            executorService.submit(() -> {
                trade.getNm().hashCode();
                trade.getAmt();
                count++;
                promise.resolve(true);
                try {
                    Thread.sleep(201);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });
    }


    @PUT("/trade2")
    public Promise<Boolean> b(final Trade trade) {
        return Promises.invokablePromise(promise -> {
                trade.getNm().hashCode();
                trade.getAmt();
                count++;
                promise.resolve(true);
        });
    }

    @GET("/count")
    public long count() {
        return count;
    }

    public static void main(final String... args) {

        final ManagedServiceBuilder managedServiceBuilder = managedServiceBuilder();

        managedServiceBuilder
                .addEndpointService(new TradeService())
                .setRootURI("/");

        //managedServiceBuilder.getEndpointServerBuilder().setHost("192.168.0.1");

        managedServiceBuilder.startApplication();
    }
}
