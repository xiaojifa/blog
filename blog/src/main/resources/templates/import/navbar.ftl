    <div style="margin-bottom: 2px;" class="col-xs-12">
        <div class="col-xs-6" style="padding-left: 2px;">
            <h2 style="color: #ff2fc5;padding: 0;margin: 2px;">NSDA论坛</h2>
            <small style="color: #df66ff;">&emsp;- 游戏&学习 - </small>
        </div>
        <div class="col-xs-6">
            <span class="btn btn-mini btn-danger pull-right" style="margin-top: 10px;">
                <a style="text-decoration:none;color: #ccffbc;" href="/app/app.apk"><i class="icon-android"></i> 安卓APP</a>
            </span>
        </div>
    </div>
</div>

<nav class="navbar navbar-inverse" role="navigation" style="margin-bottom: 5px;">
    <div class="container-fluid my-navbar1">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse"
                        data-target=".navbar-collapse-example">
                    <span class="sr-only">切换导航</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="/">Home</a>
            </div>
            <div class="collapse navbar-collapse navbar-collapse-example">
                <ul class="nav navbar-nav">
                    <#if articleTypeList?? && articleTypeList?size gt 0 >
                        <#list articleTypeList as articleType>
                            <li class="dropdown">
                                <a href="javascript:void(0);" class="dropdown-toggle" data-toggle="dropdown">${(articleType.articleTypeName)!}<b class="caret"></b></a>
                               <#if (articleType.articleTypeTreeVoList)?? && (articleType.articleTypeTreeVoList)?size gt 0>
                                   <ul class="dropdown-menu" role="menu">
                                       <#list (articleType.articleTypeTreeVoList) as articleTypeTreeVo>
                                            <li><a href="article/list?articleTypeId=${(articleTypeTreeVo.articleTypeId)!}">${(articleTypeTreeVo.articleTypeName)!}</a></li>
                                       </#list>
                                   </ul>
                               </#if>
                            </li>
                        </#list>
                    </#if>
                    <li><a href="/contact"><i class="icon-comments"></i> 联系</a></li>
                    <li><a href="/donation"><i class="icon-yen"></i> 我要可乐</a></li>
                    <form class="navbar-form navbar-left" role="search" action="/article/search" method="get">
                        <div class="form-group">
                            <input type="text" value="${articleTitle!}" name="articleTitle" maxlength="25" max="25" class="form-control" placeholder="搜索">
                        </div>
                        <button type="submit" class="btn btn-default"><i class="icon-search"></i> 搜索</button>
                    </form>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <#if user??>
                        <li>
                            <a href="/view/chat"><i class="icon-chat"></i> 聊天</a>
                        <li>
                        <li>
                            <a href="/user/manager"><i class="icon-user"></i> 个人中心</a>
                        <li>
                        <li>
                        <#if (user.userPublishArticle)?? && (user.userPublishArticle) == 1>
                            <li><a href="/user/publishArticle"><i class="icon-edit"></i> 发布文章</a></li>
                        </#if>
                        <li>
                        <li>
                        <#if (user.userPublishTopic)?? && (user.userPublishTopic) == 1>
                            <li><a href="/user/publishTopic"><i class="icon-edit"></i> 发布话题</a></li>
                        </#if>
                        <li>
                        <li>
                            <a href="/logout"><i class="icon-signout"></i> 退出登录</a>
                        <li>
                    <#else >
                        <li><a href="/register"><i class="icon-user"></i> 注册</a></li>
                        <li><a href="/login"><i class="icon-signin"></i> 登陆</a></li>
                    </#if>
                </ul>
            </div>
        </div>

    </div>
</nav>
<div class="container">