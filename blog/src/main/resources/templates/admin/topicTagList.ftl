<#include "../import/adminTop.ftl">
<div class="panel col-xs-12">
    <div class="panel-body">
        <div class="form-inline">
            <div class="form-group">
                <label for="topicTagAdd">标签名称：</label>
                <input class="form-control" type="text" id="topicTagAdd">
            </div>
            <button type="button" onclick="topicTagAdd()" class="btn btn-success">添加</button>
        </div>
        <hr/>
        <#if topicTagList?? && topicTagList?size gt 0 >
            <#list  topicTagList as topicTag>
                <div class="col-sm-2" style="padding: 2px; margin-bottom: 5px;">
                    <div class="img-thumbnail">
                        <div class="pull-right">
                            <i class="icon icon-cog" data-toggle="tooltip" data-placement="bottom" title="修改"
                               onclick="topicTagUpdate('${(topicTag.topicTagId)!}')"></i>
                            <i class="icon icon-remove" data-toggle="tooltip" data-placement="bottom" title="删除"
                               onclick="topicTagDel('${(topicTag.topicTagId)!}')"></i>
                        </div>
                        <input class="form-control" type="text" value="${(topicTag.topicTagName)!}"
                               id="${(topicTag.topicTagId)!}" name="${(topicTag.topicTagId)!}"/>
                    </div>
                </div>
            </#list>
        <#else >
            <#include "../import/nullData.ftl">
        </#if>
    </div>
</div>
<div class="modal fade">

</div>
<script>
    function topicTagAdd() {
        let topicTagName = $("#topicTagAdd").val();
        if(!checkNotNull(topicTagName)) {
            zuiMsg("标签名称不能为空");
            return;
        }
        $.post("/hzy2003/topic/tag/addOrUpdate", {
                topicTagName: topicTagName
            },
            function (data) {
                if (data.code === 200) {
                    alert(data.message)
                    location.reload();
                }
                zuiMsg(data.message);
            });
    }

    function topicTagDel(topicTagId) {
        if(confirm("确定要删除吗？")) {
            $.post("/hzy2003/topic/tag/del", {
                    topicTagId: topicTagId
                },
                function (data) {
                    if (data.code === 200) {
                        alert(data.message)
                        location.reload();
                    }
                    zuiMsg(data.message);
                });
        }
    }

    function topicTagUpdate(topicTagId) {
        if(confirm("确定要修改吗？")) {
            let topicTagName = $("#" + topicTagId).val();


            if(!checkNotNull(topicTagId) || !checkNotNull(topicTagName)) {
                zuiMsg("修改参数不正确");
                return;
            }

            $.post("/hzy2003/topic/tag/addOrUpdate", {
                    topicTagId: topicTagId,
                    topicTagName: topicTagName
                },
                function (data) {
                    if (data.code === 200) {
                        alert(data.message)
                        location.reload();
                    }
                    zuiMsg(data.message);
                });
        }

    }

    $('[data-toggle="tooltip"]').tooltip({
        placement: 'bottom'
    });
</script>

<#include "../import/bottom.ftl">