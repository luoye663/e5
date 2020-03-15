package io.qyi.e5.service.rabbitMQ;

/**
 * @program: msgpush
 * @description:
 * @author: 落叶随风
 * @create: 2020-01-13 23:34
 **/
public interface Listener {
    public void listen(String msg);
}
