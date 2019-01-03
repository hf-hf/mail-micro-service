package top.hunfan.mail.roundrobin;

import java.util.List;

/**
 * 抽象轮询
 * @author hf-hf
 * @date 2018/12/27 10:17
 */
abstract class AbstractRoundRobin implements RoundRobin {

    protected List<Node> nodes;

    protected boolean checkNodes(){
        return nodes != null && nodes.size() > 0;
    }

    class Node implements Comparable<Node> {
        final Invoker invoker;
        Integer weight;
        Integer effectiveWeight;
        Integer currentWeight;

        public Node(Invoker invoker) {
            this.invoker = invoker;
            this.currentWeight = 0;
        }

        Node(Invoker invoker, Integer weight) {
            this.invoker = invoker;
            this.weight = weight;
            this.effectiveWeight = weight;
            this.currentWeight = 0;
        }

        @Override
        public int compareTo(Node o) {
            return currentWeight > o.currentWeight ? 1 : (currentWeight.equals(o.currentWeight) ? 0 : -1);
        }

        /**
         * 调用成功升级
         */
        public void onSuccess() {
            if (effectiveWeight < this.weight){
                effectiveWeight++;
            }
        }

        /**
         * 调用异常服务降级
         */
        public void onFail() {
            effectiveWeight--;
        }
    }

}
