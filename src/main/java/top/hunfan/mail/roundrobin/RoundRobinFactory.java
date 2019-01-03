package top.hunfan.mail.roundrobin;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * 轮询方式工厂
 * @author hf-hf
 * @date 2018/12/27 11:19
 */
public class RoundRobinFactory {

    public static final String NORMAL = "normal";

    public static final String WEIGHTED = "weighted";

    public static RoundRobin create(String type, final Collection<Properties> properties){
        switch (type){
            case WEIGHTED:
                Map<Invoker, Integer> invokerMap = new HashMap<>(properties.size());
                properties.stream().forEach(pro -> {
                    invokerMap.put(new Invoker() {
                        @Override
                        public Boolean isAvailable() {
                            return Boolean.valueOf(pro.getProperty("mail.isAvailable"));
                        }

                        @Override
                        public String id() {
                            return pro.getProperty("mail.id");
                        }
                    }, Integer.valueOf(pro.getProperty("mail.weight")));
                });
                return new WeightedRoundRobin(invokerMap);
            case NORMAL:
                List<Invoker> invokerList = properties.stream().map(pro -> new Invoker() {
                    @Override
                    public Boolean isAvailable() {
                        return Boolean.valueOf(pro.getProperty("mail.isAvailable"));
                    }

                    @Override
                    public String id() {
                        return pro.getProperty("mail.id");
                    }
                }).collect(Collectors.toList());
                return new NormalRoundRobin(invokerList);
            default:
                return null;
        }
    }

}
