package io.qyi.e5.service.task;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-04-16 16:51
 **/
public interface ITask {
    void sendTaskOutlookMQ(int github_id);

    void sendTaskOutlookMQALL();

    boolean executeE5(int github_id);

}
