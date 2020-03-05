<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>日志查询</title>
<body>
<table  border="1" cellpadding="3" cellspacing="0">
    <tr>
        <th style="width: 15%">调用时间</th>
        <th style="width: 5%">调用结果</th>
        <th style="width: 15%">信息</th>
        <th>原始信息</th>
    </tr>
    <#list list_log?sort_by('callTime')?reverse as log>
    <#--        <span>${log},-->
    <#--        ${log.result}</span></br>-->
        <tr>
            <#assign dlong = (log.callTime + '000')?number?number_to_datetime/>
            <#--            <td>${num?log.callTime?number_to_datetime?string('yyyy-MM-dd')}</td>-->
            <td >${dlong?string("yyyy-MM-dd HH:mm:ss")}</td>
            <#if log.result == '1'>
                <td >成功</td>
            <#else >
                <td style="background: red">失败</td>
            </#if>
            <td>${log.msg}</td>
            <td>${log.originalMsg}</td>
        </tr>
    </#list>
</table>
</body>
</html>