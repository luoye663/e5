package io.qyi.e5.service.task;

import io.qyi.e5.outlook.entity.Outlook;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-04-16 16:51
 **/
public interface ITask {
    void updateOutlookExecDateTime(int github_id, int outlookId);

    void sendTaskOutlookMQALL();

    boolean executeE5(int github_id,int outlookId);

    void submit(Outlook mq);
}
