<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>outlook授权结果</title>
</head>
<body>
<#if result>
    <h3>授权成功!</h3>
<#else >
    <h3>授权失败!</h3>
    <h4>错误: ${msg}</h4>
</#if>
    <a href="/user/home">返回用户中心</a>
</body>
</html>