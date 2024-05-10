<#include "../import/adminTop.ftl">
<div class="panel col-sm-12">
    <div class="panel-body">
        <div class="panel col-sm-6">
            <div class="panel-body">
                <h4>
                    一级分类：
                </h4>
                <#if topicType0List?? && topicType0List?size gt 0 >
                <div class="panel">
                    <div class="panel-body">
                        <button type="button" class="btn btn-success" onclick="addOrUpdateTopicType()">
                            添加一级类型
                        </button>
                        <hr/>
                        <table class="table">
                            <thead>
                            <tr>
                                <th>排序</th>
                                <th>添加时间</th>
                                <th>话题类型名称</th>
                                <th>操作</th>
                            </tr>
                            </thead>
                            <tbody>
                            <#list topicType0List as topicType>
                            <tr>
                                <td>${(topicType.topicTypeSort)!}</td>
                                <td>
                                    ${(topicType.getTopicTypeAddTime())}
                                </td>
                                <td>
                                    <a href="/hzy2003/topic/type/list?topicTypeParentId=${(topicType.topicTypeId)}">
                                        ${(topicType.topicTypeName)!}
                                    </a>
                                </td>
                                <td>
                                    <button onclick="addOrUpdateTopicType('${(topicType.topicTypeId)!}','${(topicType.topicTypeName)!}','${(topicType.topicTypeSort)!}')"
                                            type="button" class="btn btn-mini"><i class="icon-cog"></i> 修改
                                    </button>
                                    <button onclick="delTopicType('${(topicType.topicTypeId)!}')"
                                            type="button" class="btn btn-mini"><i
                                            class="icon-remove"></i> 删除
                                    </button>
                                </td>
                            </tr>
                            </#list>
                            </tbody>
                        </table>
                    </div>
                </div>
                <#else >
                <#include "../import/nullData.ftl">
            </#if>
        </div>
    </div>
    <div class="panel col-sm-6">
        <div class="panel-body">
            <h4>
                ${topicTypeName!'二级分类'}：
            </h4>
            <#if topicType0List?? && topicType0List?size gt 0 >
            <div class="panel">
                <div class="panel-body">
                    <button type="button" class="btn btn-success" onclick="addOrUpdateTopicType()">添加二级类型
                    </button>
                    <hr/>
                    <table class="table">
                        <thead>
                        <tr>
                            <th>排序</th>
                            <th>添加时间</th>
                            <th>话题类型名称</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <#list topicType1List as topicType>
                        <tr>
                            <td>${(topicType.topicTypeSort)!}</td>
                            <td>
                                ${(topicType.getTopicTypeAddTime())}
                            </td>
                            <td>${(topicType.topicTypeName)!}</td>
                            <td>
                                <button onclick="addOrUpdateTopicType('${(topicType.topicTypeId)!}','${(topicType.topicTypeName)!}','${(topicType.topicTypeSort)!}','${(topicType.topicTypeParentId)!}')"
                                        type="button" class="btn btn-mini"><i class="icon-cog"></i> 修改
                                </button>
                                <button onclick="delTopicType('${(topicType.topicTypeId)!}')"
                                        type="button" class="btn btn-mini"><i
                                        class="icon-remove"></i> 删除
                                </button>
                            </td>
                        </tr>
                        </#list>
                        </tbody>
                    </table>
                </div>
            </div>
            <#else >
            <#include "../import/nullData.ftl">
        </#if>
    </div>
</div>
</div>
</div>


<div class="modal fade" id="updateTopicTypeModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">×</span><span
                        class="sr-only">关闭</span></button>
                <h4 class="modal-title" id="topicTypeTitle">修改二级目录</h4>
            </div>
            <input type="hidden" id="topicTypeId" name="topicTypeId1">
            <div class="modal-body">
                <#if topicType0List?? && topicType0List?size gt 0 >
                <div class="form-group" id="topicTypeParentIdDiv">
                    <label for="topicTypeParentId">上级目录：</label>
                    <select class="form-control" id="topicTypeParentId" name="topicTypeParentId">
                        <option value="">--无--</option>
                        <#list topicType0List as topicType0>
                        <option value="${(topicType0.topicTypeId)!}">${(topicType0.topicTypeName)!}</option>
                    </#list>
                    </select>
                </div>
            </#if>
            <div class="form-group">
                <label for="topicTypeName">分类名称：</label>
                <input type="text" class="form-control" id="topicTypeName" name="topicTypeName"
                       placeholder="分类名称">
            </div>
            <div class="form-group">
                <label for="topicTypeSort">分类排序</label>
                <input type="number" class="form-control" id="topicTypeSort" name="topicTypeSort"
                       placeholder="分类排序">
            </div>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
            <button type="button" class="btn btn-primary" onclick="topicUpdateAction()">提交</button>
        </div>
    </div>
</div>
</div>

<script type="text/javascript">

    function topicUpdateAction() {
        let topicTypeId = $("#topicTypeId").val();
        let topicTypeName = $("#topicTypeName").val();
        let topicTypeSort = $("#topicTypeSort").val();
        let topicTypeParentId = $("#topicTypeParentId").val();

        $.post("/hzy2003/topic/type/addOrUpdate", {
                topicTypeId: topicTypeId,
                topicTypeName: topicTypeName,
                topicTypeSort: topicTypeSort,
                topicTypeParentId: topicTypeParentId
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

    function addOrUpdateTopicType(topicTypeId, topicTypeName, topicTypeSort, topicTypeParentId) {
        $('#updateTopicTypeModal').modal('toggle', 'center');
        $("#topicTypeId").val(topicTypeId);
        $("#topicTypeName").val(topicTypeName);
        $("#topicTypeSort").val(topicTypeSort);
        $("#topicTypeParentId").val(topicTypeParentId);

        if (!checkNotNull(topicTypeId)) {
            $("#topicTypeTitle").text("添加类型");
        } else {
            $("#topicTypeTitle").text("修改类型");
        }
    }

    function delTopicType(topicTypeId) {
        if (confirm("是否删除")) {
            if (!checkNotNull(topicTypeId)) {
                zuiMsg("程序出错，请刷新页面重试");
                return;
            }
            $.post("/hzy2003/topic/type/del", {
                    topicTypeId: topicTypeId
                },
                function (data) {
                    if (data.code === 200) {
                        alert(data.message)
                        location.reload();
                        return;
                    }
                    new $.zui.Messager(data.message, {
                        type: 'warning',
                        placement: 'center'
                    }).show();
                });
        }
    }
</script>
<#include "../import/bottom.ftl">