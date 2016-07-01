package io.advantageous.qbit.example.perf.websocket;

import io.advantageous.qbit.reactive.Callback;
import io.advantageous.reakt.promise.Promise;

public interface TradeServiceAsync {

    void a(Callback<Boolean> callback, final Trade trade);
    void t(Callback<Boolean> callback, final Trade trade);
    Promise<Boolean> b(final Trade trade);
    Promise<Boolean> t2(final Trade trade);
    void count(Callback<Long> callback);
}
