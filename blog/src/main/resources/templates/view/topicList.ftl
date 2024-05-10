<#include "../import/top.ftl">
<#include "../import/navbar.ftl">
<style>
    .cardimg {
        width: 100%;
    }
</style>
<div class = "col-xs-12">
    <ol class = "breadcrumb" style = "margin-bottom: 5px;">
        <li><a href = "/"><i class = "icon-home"></i> 首页</a></li>
        <li class = "active">${topicTypeName!}</li>
    </ol>
    <div class = "panel">
        <div class = "panel-body">
            <#if topicVoIPage?? && (topicVoIPage.list)?? && (topicVoIPage.list)?size gt 0 >
                <#list  topicVoIPage.list as topicVo>
                    <div class = "col-xs-6 col-sm-3" style = "padding: 2px;">
                        <div class = "card">
                            <a href = "/topic?topicId=${(topicVo.topicId)!}" style = "text-decoration:none">
                                <div class = "card-heading"><strong>${(topicVo.topicTitle)!}</strong></div>
                                <div class = "card-content text-muted">
                                    <i class = "icon-time"></i>
                                    ${(topicVo.topicAddTime)!}
                                </div>

                                <div class = "card-actions">
                                    <span class = "label label-info">
                                        <i class = "icon-eye-open"></i> ${(topicVo.topicLookNumber)!}
                                    </span> |
                                        <span class = "label label-success">
                                        <i class = "icon-thumbs-up"></i> ${(topicVo.topicGoodNumber)!}
                                    </span> |
                                        <span class = "label label-primary">
                                        <i class = "icon-star"></i> ${(topicVo.topicCollectionNumber)!}
                                    </span>
                                </div>
                            </a>
                        </div>
                    </div>
                </#list>
                <div class = "col-xs-12" style = "padding:5px;">
                    <div class = "col-sm-12" style = "padding: 0;text-align: center;">
                        <ul class = "pager" style = "margin-top: 10px;margin-bottom: 10px;">
                            <li class = "previous" onclick = "getNewData(1)">
                                <a href = "javascript:void(0)"><i class = "icon-step-backward"></i></a>
                            </li>

                            <#if (topicVoIPage.pageNumber)?? && (topicVoIPage.pageNumber) lte 1>
                                <li class = "previous disabled">
                                    <a href = "javascript:void(0)"><i class = "icon-chevron-left"></i></a>
                                </li>
                            <#else>
                                <li class = "previous" onclick = "getNewData('${topicVoIPage.pageNumber-1}')">
                                    <a href = "javascript:void(0)"><i class = "icon-chevron-left"></i></a>
                                </li>
                            </#if>
                            <li>
                                <a href = "javascript:void(0)" class = "btn">
                                    ${topicVoIPage.pageNumber}页/共${topicVoIPage.totalPage}</a>
                            </li>
                            <#if topicVoIPage.pageNumber gte topicVoIPage.totalPage>
                                <li class = "next disabled">
                                    <a href = "javascript:void(0)"><i class = "icon-chevron-right"></i></a>
                                </li>
                            <#else>
                                <li class = "next" onclick = "getNewData('${topicVoIPage.pageNumber+1}')">
                                    <a href = "javascript:void(0)"><i class = "icon-chevron-right"></i></a>
                                </li>
                            </#if>
                            <li class = "previous" onclick = "getNewData('${topicVoIPage.totalPage}')">
                                <a href = "javascript:void(0)"><i class = "icon-step-forward"></i></a>
                            </li>

<#--                            <li class = "next">-->
<#--                                <a href = "javascript:void(0)">-->
<#--                                    <input type = "number" id = "renderPageNumber" maxlength = "5"-->
<#--                                           style = "width:40px;height: 20px;margin: 0;"-->
<#--                                           oninput = "value=value.replace(/[^\d]/g,'')">-->
<#--                                </a>-->
<#--                            </li>-->
<#--                            <li class = "next">-->
<#--                                <a href = "javascript:void(0)" onclick = "renderPage()"-->
<#--                                   style = "padding-left: 2px;padding-right: 2px;">-->
<#--                                    跳转-->
<#--                                </a>-->
<#--                            </li>-->
                        </ul>
                    </div>
                </div>

            <#else >
                <#include "../import/nullData.ftl">
            </#if>
        </div>
    </div>
</div>


<script>
    function getNewData(pageNumber) {
        if (!checkNotNull(pageNumber)) {
            pageNumber = 1;
        }
        window.location.href = "/topic/list?pageNumber=" + pageNumber + "<#if topicTypeId??>&topicTypeId=${topicTypeId!}</#if>";
    }

    function renderPage() {
        let renderPageNumber = $("#renderPageNumber").val();
        if (!checkNotNull(renderPageNumber)) {
            new $.zui.Messager("请输入跳转的页码！", {
                type: 'warning',
                placement: 'center'
            }).show();
            return;
        }
        let totalPage = '${topicVoIPage.totalPage}';
        if (parseInt(renderPageNumber) > parseInt(totalPage)) {
            renderPageNumber = totalPage;
        }
        getNewData(renderPageNumber);
    }
</script>
<#include "../import/viewBottom.ftl">
