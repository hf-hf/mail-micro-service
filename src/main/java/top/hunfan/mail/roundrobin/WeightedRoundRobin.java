package top.hunfan.mail.roundrobin;

import java.util.ArrayList;
import java.util.Map;

/**
 * 加权轮询
 * @author hf-hf
 * @date 2018/12/27 9:51
 */
public class WeightedRoundRobin extends AbstractRoundRobin {

    public WeightedRoundRobin(Map<Invoker, Integer> invokersWeight) {
        if (invokersWeight != null && !invokersWeight.isEmpty()) {
            nodes = new ArrayList<>(invokersWeight.size());
            invokersWeight.forEach((invoker, weight) -> {
                if(invoker.isAvailable()){
                    nodes.add(new Node(invoker, weight));
                }
            });
        }else
            nodes = null;
    }

    @Override
    public Invoker select() {
        if (!checkNodes())
            return null;
        else if (nodes.size() == 1) {
            if (nodes.get(0).invoker.isAvailable())
                return nodes.get(0).invoker;
            else
                return null;
        }
        Integer total = 0;
        Node nodeOfMaxWeight = null;
        for (Node node : nodes) {
            total += node.effectiveWeight;
            node.currentWeight += node.effectiveWeight;

            if (nodeOfMaxWeight == null) {
                nodeOfMaxWeight = node;
            }else{
                nodeOfMaxWeight = nodeOfMaxWeight.compareTo(node) > 0 ? nodeOfMaxWeight : node;
            }
        }

        nodeOfMaxWeight.currentWeight -= total;
        return nodeOfMaxWeight.invoker;
    }

}
