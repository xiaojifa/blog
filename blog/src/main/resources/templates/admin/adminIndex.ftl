<#include "../import/adminTop.ftl">
<div class="panel col-xs-12">
    <div class="panel-body">
        <div class="col-xs-4">
            <h4>系统基本信息：</h4>
            <div class="panel">
                <div class="panel-body">
                    <h5><i class="icon icon-desktop"></i>系统类型：${osName!}</h5>
                    <h5><i class="icon icon-server"></i>服务器IP：${hostAddress!}</h5>
                    <h5><i class="icon icon-group"></i>用户数：${userCount!}</h5>
                </div>
            </div>
        </div>
        <div class="col-xs-4">
            <h4>文章基本信息：</h4>
            <div class="panel">
                <div class="panel-body">
                    <h5><i class="icon icon-list"></i>文章类型数：${articleTypeCount!}</h5>
                    <h5><i class="icon icon-th"></i>文章标签数：${articleTagCount!}</h5>
                    <h5><i class="icon icon-stack"></i>文章数：${articleCount!}</h5>
                </div>
            </div>
        </div>
        <div class="col-xs-4">
            <h4>话题基本信息：</h4>
            <div class="panel">
                <div class="panel-body">
                    <h5><i class="icon icon-list"></i>话题类型数：${topicTypeCount!}</h5>
                    <h5><i class="icon icon-th"></i>话题标签数：${topicTagCount!}</h5>
                    <h5><i class="icon icon-stack"></i>话题数：${topicCount!}</h5>
                </div>
            </div>
        </div>
    </div>
</div>

<#include "../import/bottom.ftl">