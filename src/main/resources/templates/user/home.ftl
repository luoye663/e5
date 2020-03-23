<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Home</title>
    <link rel="stylesheet" href="//cdnjs.loli.net/ajax/libs/mdui/0.4.3/css/mdui.min.css">
    <script src="//cdnjs.loli.net/ajax/libs/mdui/0.4.3/js/mdui.min.js"></script>
    <script src="https://cdn.bootcss.com/jquery/3.4.1/jquery.min.js"></script>
</head>
<body class="mdui-appbar-with-toolbar mdui-theme-primary-indigo mdui-theme-accent-blue mdui-loaded">
<header class="mdui-appbar mdui-appbar-fixed">
    <div class="mdui-toolbar mdui-color-theme">
        <span class="mdui-typo-title">Office E5 自动续订</span>
        <div class="mdui-toolbar-spacer"></div>
    </div>
</header>
<div class="mdui-card-media in floats">
    使用说明
    <ol>
        <#--<li>输入 client_id 与 client_secret 保存</li>
        <li>点击 “授权”，请用不使用的空账号登录授权</li>
        <li>授权成功后就不用管了，系统会自动调用你的out api</li>-->
        <li>程序会读取授权的outlook账号邮箱邮件，但不会保存任何信息，仅仅是调用api。</li>
        <li>请单独创建一个同域 E5 子账号，不要使用此账号进行发送、接收个人邮件，以免发生误会。</li>
    </ol>
</div>
<#--数据输入-->
<div class="mdui-card-media in floats">
    <div class="mdui-textfield">
        <label class="mdui-textfield-label" style="font-weight: 500;">client_id</label>
        <input id="client_id" class="mdui-textfield-input" type="text" value="${client_id!}"/>
    </div>
    <div class="mdui-textfield">
        <label class="mdui-textfield-label" style="font-weight: 500;">client_secret</label>
        <input id="client_secret" class="mdui-textfield-input" type="text" value="${client_secret!}"/>
    </div>
    <button id="authorization" class="mdui-btn mdui-color-theme-accent mdui-ripple">授权</button>
    <button id="save" class="mdui-btn mdui-color-theme-accent mdui-ripple">保存</button>
</div>
<div class="mdui-divider"></div>
<#--日志表格-->
<div class="mdui-table-fluid table-container floats">
    <p>日志会在每日0点清空。</p>
    <button id="findLog" class="mdui-btn mdui-color-theme-accent mdui-ripple">查询日志</button>
</div>

<#--时间设置-->
<div class="mdui-table-fluid table-container floats">
    <div class="mdui-textfield">
        <label class="mdui-textfield-label" style="font-weight: 500;">调用时间间隔</label>
        <input id="cron_time" class="mdui-textfield-input" type="number" value="${cron_time!}"/>
    </div>
    <div class="mdui-textfield">
        <label class="mdui-textfield-label" style="font-weight: 500;">随机时间范围</label>
        <input id="cron_time_random" class="mdui-textfield-input" type="text"
               value="${cron_time_random_start!}-${cron_time_random_end!}"/>
    </div>
    <ol>
        <li>调用时间间隔，单位 秒。</li>
        <li>随机时间范围格式，“10-30”，指的是 调用时间间隔+取范围中的一个值，进行调用。</li>
        <li>调用时间间隔不得大于12小时，也就是‭43200秒‬</li>
    </ol>
    <button id="save_random_time" class="mdui-btn mdui-color-theme-accent mdui-ripple">保存</button>
</div>

</body>
<script src="//www.mdui.org/source/dist/js/mdui.min.js"></script>
<script type="text/javascript">
    $(document).ready(function () {
        //日志查询
        $("#findLog").click(function () {
            var url = "/outlookLog/findLog"
            window.open(url, '_blank')
        })
        //保存随机时间
        $("#save_random_time").click(function () {
            var cron_time = $("#cron_time").val();
            var cron_time_random = $("#cron_time_random").val();
            if ((cron_time || cron_time_random) == "") {
                alert("cron_time 或 cron_time_random 不能为空!")
                return;
            }
            ;
            $.post("/outlook/outlook/saveRandomTime", {
                cronTime: cron_time,
                crondomTime: cron_time_random
            }, function (data, status) {
                console.log(data);
                if (status != "success") {
                    alert("未知错误，请联系管理员!")
                    return;
                }
                if (data.code == 0) {
                    alert("保存成功!");
                } else {
                    alert("错误:  " + data.msg);
                }

            })
        })
        // 授权
        $("#authorization").click(function () {
            var url = "/outlook/auth2/getAuthorizeUrl"
            window.location.href = url;
        })
        //保存
        $("#save").click(function () {
            var client_id = $("#client_id").val();
            var client_secret = $("#client_secret").val();
            if ((client_id || client_secret) == "") {
                alert("client_id 或 client_secret 不能为空!")
                return;
            }
            ;
            $.post("/outlook/outlook/save", {
                client_id: client_id,
                client_secret: client_secret
            }, function (data, status) {
                console.log(data);
                if (status != "success") {
                    alert("未知错误，请联系管理员!")
                    return;
                }
                if (data.code == 0) {
                    alert("保存成功!");
                } else {
                    alert("错误:  " + data.msg);
                }

            })
        });
    });
</script>
<style>
    .table-container {
        border-radius: 15px;
        background-clip: padding-box;
        margin: 1% 1%;
        width: 25%;
        padding: 35px 35px 15px 35px;
        background: #fff;
        border: 1px solid #eaeaea;
        box-shadow: 0 0 25px #cac6c6;
    }

    .in {
        border-radius: 15px;
        background-clip: padding-box;
        margin: 1% 1%;
        width: 25%;
        padding: 35px 35px 15px 35px;
        background: #fff;
        border: 1px solid #eaeaea;
        box-shadow: 0 0 25px #cac6c6;
        float: left;
    }

    .floats {
        display: inline-block;
    }
</style>
</html>