<#include "../import/adminTop.ftl">

<#if topicVoIPage?? && topicVoIPage.list?size gt 0>
    <form class="form-horizontal" action="/hzy2003/topic/list" method="get">
        <div class="form-group">
            <label for="topicVoTitle" class="col-sm-1">文章标题</label>
            <div class="col-sm-3">
                <input type="text" value="${topicTitle!}" class="form-control" name="topicTitle" id="topicTitle" placeholder="文章标题">
            </div>
            <div class="col-sm-1">
                <button type="submit" class="btn btn-success"><i class="icon icon-search"></i> 搜索</button>
            </div>
            <div class="col-sm-1">
                <a href="/hzy2003/topic/list" class="btn btn-success"><i class="icon icon-search"></i> 搜索全部</a>
            </div>
        </div>
    </form>
    <div class="panel">
        <div class="panel-body">
            <h4>
                当前：${(topicVoIPage.total)!0}篇话题
            </h4>
            <table class="table table-bordered" >
                <thead>
                <tr>
                    <th>发布时间</th>
                    <th>话题类型</th>
                    <th>发布者</th>
                    <th>话题标题</th>
                    <th>浏览数</th>
                    <th>点赞数</th>
                    <th>收藏数</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody>
                <#list topicVoIPage.list as topicVo>
                    <tr>
                        <td>
                            ${(topicVo.topicAddTime)}
                        </td>
                        <td>${(topicVo.topicTypeName)!}</td>
                        <td>${(topicVo.userName)!}</td>
                        <td>
                            <#if (topicVo.topicHot)?? && (topicVo.topicHot) == 1>
                                <span class="label label-badge label-danger">Hot</span>
                            </#if>
                            ${(topicVo.topicTitle)!}
                        </td>
                        <td>${(topicVo.topicLookNumber)!}</td>
                        <td>${(topicVo.topicGoodNumber)!}</td>
                        <td>${(topicVo.topicCollectionNumber)!}</td>
                        <td>
                            <div style="text-align: right">
                                <button onclick="hotTopic('${(topicVo.topicId)!}')" type="button"
                                        class="btn btn-mini">🔥 设为热门
                                </button>
                                <button onclick="delTopic('${(topicVo.topicId)!}')" type="button"
                                        class="btn btn-mini"><i
                                            class="icon-remove"></i> 删除
                                </button>
                                <a target="_blank" href="/topic?topicId=${(topicVo.topicId)!}" class="btn btn-mini"><i class="icon-eye-open"></i> 查看</a>
                            </div>
                        </td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </div>
    </div>

    <div class="panel">
        <div class="panel-body" style="padding: 0;">
            <div class="col-sm-12" style="padding: 0;text-align: center;">
                <ul class="pager" style="margin-top: 10px;margin-bottom: 10px;">
                    <li class="previous" onclick="getNewData(1)">
                        <a href="javascript:void(0)"><i class="icon-step-backward"></i></a>
                    </li>

                    <#if topicVoIPage.pageNumber lte 1>
                        <li class="previous disabled">
                            <a href="javascript:void(0)"><i class="icon-chevron-left"></i></a>
                        </li>
                    <#else>
                        <li class="previous" onclick="getNewData('${topicVoIPage.pageNumber-1}')">
                            <a href="javascript:void(0)"><i class="icon-chevron-left"></i></a>
                        </li>
                    </#if>
                    <li>
                        <a href="javascript:void(0)" class="btn">
                            ${topicVoIPage.pageNumber}页/共${topicVoIPage.totalPage}</a>
                    </li>
                    <#if topicVoIPage.pageNumber gte topicVoIPage.totalPage>
                        <li class="next disabled">
                            <a href="javascript:void(0)"><i class="icon-chevron-right"></i></a>
                        </li>
                    <#else>
                        <li class="next" onclick="getNewData('${topicVoIPage.pageNumber+1}')">
                            <a href="javascript:void(0)"><i class="icon-chevron-right"></i></a>
                        </li>
                    </#if>
                    <li class="previous" onclick="getNewData('${topicVoIPage.totalPage}')">
                        <a href="javascript:void(0)"><i class="icon-step-forward"></i></a>
                    </li>

                    <li class="next">
                        <a href="javascript:void(0)">
                            <input type="number" id="renderPageNumber" maxlength="5"
                                   style="width:50px;height: 20px;" oninput="value=value.replace(/[^\d]/g,'')">
                        </a>
                    </li>
                    <li class="next">
                        <a href="javascript:void(0)" onclick="renderPage()"
                           style="padding-left: 2px;padding-right: 2px;">
                            跳转
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </div>

<#else >
    <#include "../import/nullData.ftl">
</#if>

<script type="text/javascript">

    function hotTopic(topicId) {
        if (confirm("是否设置为热门话题")) {
            if (!checkNotNull(topicId)) {
                zuiMsg('程序出错，请刷新页面重试');
                return;
            }
            $.post("/hzy2003/topic/hot", {
                    topicId: topicId
                },
                function (data) {
                    if (data.code === 200) {
                        alert(data.message)
                        location.reload();
                        return;
                    }
                    zuiMsg(data.message);
                });
        }
    }

    function delTopic(topicId) {
        if (confirm("是否删除")) {
            if (!checkNotNull(topicId)) {
                zuiMsg('程序出错，请刷新页面重试');
                return;
            }
            $.post("/hzy2003/topic/del", {
                    topicId: topicId
                },
                function (data) {
                    if (data.code === 200) {
                        alert(data.message)
                        location.reload();
                        return;
                    }
                    zuiMsg(data.message);
                });
        }
    }

    function getNewData(pageNumber) {
        if (!checkNotNull(pageNumber)) {
            pageNumber = 1;
        }
        window.location.href = "/hzy2003/topic/list?pageNumber=" + pageNumber + "<#if (topicName?? && topicName?length>0)>&topicName=${topicName!}</#if>";
    }

    function renderPage() {
        let renderPageNumber = $("#renderPageNumber").val();
        if (!checkNotNull(renderPageNumber)) {
            zuiMsg("请输入跳转的页码！");
            return;
        }
        let totalPage = '${topicVoIPage.totalPage}';
        if (parseInt(renderPageNumber) > parseInt(totalPage)) {
            renderPageNumber = totalPage;
        }
        getNewData(renderPageNumber);
    }
</script>
<#include "../import/bottom.ftl">