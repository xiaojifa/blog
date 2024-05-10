<#include "../import/top.ftl">
<#include "../import/navbar.ftl">

<style>
    .label:hover {
        cursor: pointer
    }
</style>
<div class="col-xs-12">
    <div class="panel" style="margin-bottom: 5px;">
        <div class="panel-heading">
            <i class="icon-edit"></i> 发布话题
        </div>
        <div class="panel-body">
            <div class="col-xs-12 col-sm-3" style="padding: 2px;">
                <img src="${(topic.topicCoverUrl)!'/img/null_topic_cover.gif'}" style="width: 100%;">
            </div>
            <div class="col-xs-12 col-sm-9" style="padding: 2px;">
                <form class="form-horizontal">
                    <input type="hidden" id="topicId" name="topicId" value="${(topic.topicId)!}">
                    <div class="form-group">
                        <label for="topicTitle" class="col-xs-2 col-sm-1">标题</label>
                        <div class="col-xs-10 col-sm-10">
                            <input type="text" class="form-control" value="${(topic.topicTitle)!}"
                                   name="topicTitle"
                                   id="topicTitle" placeholder="标题">
                        </div>
                    </div>
                    <div class="form-group">
                        <#if topicType0List?? && topicType0List?size gt 0>
                            <label for="topicType0" class="col-xs-2 col-sm-1">类型</label>
                            <div class="col-xs-4 col-sm-2">
                                <select class="form-control" id="topicType0" onchange="getTopicTypeChild()">
                                    <option value="">-请选择-</option>
                                    <#list  topicType0List as topicType0>
                                        <#if topicTypeParentId??>
                                            <#if topicTypeParentId==(topicType0.topicTypeId)>
                                                <option value="${(topicType0.topicTypeId)!}"
                                                        selected="selected">${(topicType0.topicTypeName)!}</option>
                                            <#else >
                                                <option value="${(topicType0.topicTypeId)!}">${(topicType0.topicTypeName)!}</option>
                                            </#if>
                                        <#else >
                                            <option value="${(topicType0.topicTypeId)!}">${(topicType0.topicTypeName)!}</option>
                                        </#if>
                                    </#list>
                                </select>
                            </div>
                            <div class="col-xs-6 col-sm-2">
                                <select class="form-control" id="topicTypeId" name="topicTypeId">
                                    <option value="">-请选择-</option>
                                    <#if topicSameTypeList?? && topicSameTypeList?size gt 0 >
                                        <#list topicSameTypeList as topicSameType>
                                            <#if (topicSameType.topicTypeId)?? && topic?? && (topic.topicTypeId)?? && (topicSameType.topicTypeId)== (topic.topicTypeId)>
                                                <option value="${(topicSameType.topicTypeId)!}"
                                                        selected="selected">${(topicSameType.topicTypeName)!}</option>
                                            <#else >
                                                <option value="${(topicSameType.topicTypeId)!}">${(topicSameType.topicTypeName)!}</option>
                                            </#if>
                                        </#list>
                                    </#if>
                                </select>
                            </div>
                        </#if>
                    </div>

                    <#if topicTagList?? && topicTagList?size gt 0 >
                        <div class="form-group">
                            <label for="exampleInputPassword4" class="col-xs-2 col-sm-1">标签</label>
                            <div class="col-xs-10" id="topicTagListBox">
                                <#list topicTagList as topicTag>
                                    <#if topicTagIdList?? && topicTagIdList?size gt 0>
                                        <#if topicTagIdList?seq_contains(topicTag.topicTagId)>
                                            <span class="label label-badge label-success"
                                                  onclick="selectTopicTag('${(topicTag.topicTagId)}')"
                                                  id="${(topicTag.topicTagId)}">${(topicTag.topicTagName)}</span>
                                        <#else >
                                            <span class="label label-badge"
                                                  onclick="selectTopicTag('${(topicTag.topicTagId)}')"
                                                  id="${(topicTag.topicTagId)}">${(topicTag.topicTagName)}</span>
                                        </#if>
                                    <#else >
                                        <span class="label label-badge "
                                              onclick="selectTopicTag('${(topicTag.topicTagId)}')"
                                              id="${(topicTag.topicTagId)}">${(topicTag.topicTagName)}</span>
                                    </#if>
                                </#list>
                            </div>
                        </div>
                    </#if>

                </form>
            </div>


        </div>
    </div>
</div>
<div class="col-xs-12">
    <div class="panel">
        <div class="panel-body">
            <textarea id="topicContext" maxlength="14999" minlength="5">${(topic.topicContext)!}</textarea>

            <div class="form-group" style="margin-top: 15px;text-align: right;">
                <button onclick="publishTopic()" type="button" class="btn btn-success"><i
                            class="icon-edit-sign"></i> 发布
                </button>
            </div>
        </div>
    </div>
</div>
<script src="//cdn.jsdelivr.net/gh/xwlrbh/HandyEditor@1.8.0/HandyEditor.min.js"></script>
<script>
    let topicTagIds = [];

    var he = HE.getEditor('topicContext', {
        width: '100%',
        height: '200px',
        autoHeight: true,
        autoFloat: false,
        topOffset: 0,
        uploadPhoto: true,
        uploadPhotoHandler: '/user/uploadFile',
        uploadPhotoSize: 1024,
        uploadPhotoType: 'gif,png,jpg,jpeg',
        uploadPhotoSizeError: '不能上传大于1024KB的图片',
        uploadPhotoTypeError: '只能上传gif,png,jpg,jpeg格式的图片',
        uploadParam: {},
        lang: 'zh-jian',
        skin: 'HandyEditor',
        externalSkin: '',
        item: ['bold', 'italic', 'strike', 'underline', 'fontSize', 'fontName', 'paragraph', 'color', 'backColor', '|', 'center', 'left', 'right', 'full', 'indent', 'outdent', '|', 'link', 'unlink', 'textBlock', 'code', 'selectAll', 'removeFormat', '|', 'image', 'expression', 'horizontal', 'orderedList', 'unorderedList', '|', 'undo', 'redo', '|', 'html']
    });

    function publishTopic() {
        let topicId = $("#topicId").val();
        let topicTitle = $("#topicTitle").val();
        let topicTypeId = $("#topicTypeId").val();
        let topicContext = he.getHtml();

        if (!checkNotNull(topicTitle)) {
            zuiMsg("请填写标题");
            return;
        }
        if (!checkNotNull(topicTypeId)) {
            zuiMsg("请选择类型");
            return;
        }
        if (!checkNotNull(topicContext)) {
            zuiMsg("请填写文章内容");
            return;
        }

        let formData = new FormData();
        formData.append("topicCoverFile", $("#topicCoverFile")[0].files[0]);
        formData.append("topicId", topicId);
        formData.append("topicTitle", topicTitle);
        formData.append("topicTypeId", topicTypeId);
        formData.append("topicTagIds", topicTagIds);
        formData.append("topicContext", topicContext);
        $.ajax({
            url: "/user/publishTopicAction",
            type: 'POST',
            data: formData,
            // 告诉jQuery不要去处理发送的数据
            processData: false,
            // 告诉jQuery不要去设置Content-Type请求头
            contentType: false,
            beforeSend: function () {
                console.log("正在进行，请稍候");
            },
            success: function (data) {
                if (data.code === 200) {
                    alert(data.message);
                    window.location.href = "/user/myTopicList";
                    return;
                }
                zuiMsg(data.message);
            },
            error: function (responseStr) {
                console.log("error");
            }
        });

    }


    function selectTopicTag(topicTagId) {
        let index = topicTagIds.indexOf(topicTagId);
        if (index > -1) {
            topicTagIds.splice(index, 1);
            $("#" + topicTagId).removeClass("label-success");
        } else {
            topicTagIds[topicTagIds.length] = topicTagId;
            $("#" + topicTagId).addClass("label-success");
        }

        console.log(topicTagIds);
    }

    function getTopicTypeChild() {
        $("#topicTypeId").html("");

        $.post("/user/getTopicTypeChild", {
                topicTypeId: $("#topicType0").val()
            },
            function (data) {
                if (data.code === 200) {
                    let topicTypeList = data.data;
                    for (let i = 0; i < topicTypeList.length; i++) {
                        $("#topicTypeId").append('<option value="' + topicTypeList[i].topicTypeId + '">' + topicTypeList[i].topicTypeName + '</option>')
                    }
                    return;
                }
                zuiMsg(data.message);
            });
    }

    $(function () {
        let selectedTags = $("span.label-success");
        for (let i = 0; i < selectedTags.length; i++) {
            topicTagIds[i] = selectedTags[i].id;
        }
    });

</script>
<#include "../import/viewBottom.ftl">
