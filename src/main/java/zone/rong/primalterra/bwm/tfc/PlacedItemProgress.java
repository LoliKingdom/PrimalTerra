package zone.rong.primalterra.bwm.tfc;

import io.netty.util.internal.UnstableApi;

@UnstableApi
public interface PlacedItemProgress {

    boolean state(int slot);

    void on(int slot);

    void off(int slot);

}
