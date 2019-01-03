package top.hunfan.mail.roundrobin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 标准轮询
 * @author hf-hf
 * @date 2018/12/27 9:51
 */
public class NormalRoundRobin extends AbstractRoundRobin {

    private final AtomicInteger position = new AtomicInteger();

    public NormalRoundRobin(List<Invoker> invokers) {
        nodes = new ArrayList<>(invokers.size());
        invokers.forEach(invoker -> nodes.add(new Node(invoker)));
    }

    @Override
    public Invoker select() {
        if (!checkNodes())
            return null;
        int index = position.updateAndGet(p -> p + 1 < nodes.size() ? p + 1 : 0);
        return nodes.get(index).invoker;
    }
}
