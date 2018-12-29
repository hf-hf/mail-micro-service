package top.hunfan.mail.roundrobin;

/**
 * 轮询接口
 * @author hefan
 * @date 2018/12/27 10:17
 */
public interface RoundRobin {
    
    Invoker select();

}
