<#include "../import/top.ftl">
<#include "../import/navbar.ftl">
<style>
    .commentTs-list {
        margin-left: 25px;
    }

    .hoverPalm {
        cursor: pointer;
    }

    .loadMoreCommentBtn:hover {
        color: red;
    }

    .llBox {
        border-radius: 3px;
        border: 1px solid #D7D7D7;
        padding: 1px;
    }

    .goodCommentBgColor {
        background-color: #2c8931;
    }

    .go-top1 {
        position: fixed;	        /* 设置fixed固定定位 */
        bottom: 20px;		/* 距离浏览器窗口下边框20px */
        right: 20px;		/* 距离浏览器窗口右边框20px */
    }

    .go-top2 {
        position: fixed;	        /* 设置fixed固定定位 */
        bottom: 315px;		/* 距离浏览器窗口下边框20px */
        left: 35px;		/* 距离浏览器窗口右边框20px */
    }

    .go-top1 a {
        display: block;			/* 将<a>标签设为块元素，用于美化样式 */
        text-decoration: none;		/* 取消超链接下画线 */
        color: #333;			/* 设置文本颜色 */
        background-color: #f2f2f2;		/* 设置背景颜色 */
        border: 1px solid #ccc;		/* 设置边框样式 */
        padding: 10px 20px;			/* 设置内边距 */
        border-radius: 5px;			/* 设置圆角矩形 */
        letter-spacing: 2px;		/* 设置文字间距 */
    }

    .go-top2 a {
        display: block;			/* 将<a>标签设为块元素，用于美化样式 */
        text-decoration: none;		/* 取消超链接下画线 */
        color: #333;			/* 设置文本颜色 */
        background-color: #f2f2f2;		/* 设置背景颜色 */
        border: 5px solid #ccc;		/* 设置边框样式 */
        padding: 100px 50px;			/* 设置内边距 */
        border-radius: 5px;			/* 设置圆角矩形 */
        letter-spacing: 2px;		/* 设置文字间距 */
    }
</style>
<div class = "col-xs-12">
    <ol class = "breadcrumb">
        <li><a href = "/"><i class = "icon-home"></i> 首页</a></li>
        <li>
            <a href = "/topic/list?topicTypeId=${(topicType.topicTypeId)!}">${(topicType.topicTypeName)!}</a>
        </li>
        <li class = "active">${(topic.topicTitle)!}</li>
    </ol>
    <hr/>
</div>

<div class = "col-xs-12">
    <div class = "panel">
        <div class = "panel-body" style = "padding-top:0;">
            <topic class = "topic">
                <header>
                    <h1 class = "text-center" style = "margin-top: 5px;">
                        ${(topic.topicTitle)!}
                        <br/>
                        <small>
                            发布时间: ${(topic.topicAddTime)!}
                        </small>
                    </h1>
                    <dl class = "dl-inline">
                        <dd><i class = "icon-user"></i>发布者:${(topic.userName)!}</dd>
                        <dt>
                        <dd class = "pull-right">
                            <span class = "label label-info">
                                <i class = "icon-eye-open"></i> ${(topic.topicLookNumber)!}
                            </span> |
                            <span class = "label label-success hoverPalm"
                                  onclick = "topicGood('${(topic.topicId)!}')">
                                <i class = "icon-thumbs-up"></i> <span
                                        id = "topicGoodNumber">${(topic.topicGoodNumber)!}</span>
                            </span> |
                            <span class = "label label-primary hoverPalm"
                                  onclick = "topicCollection('${(topic.topicId)!}')">
                                <i class = "icon-star"></i> <span
                                        id = "topicCollectionNumber">${(topic.topicCollectionNumber)!}</span>
                            </span>
                        </dd>
                    </dl>
                </header>
                <section class = "content">
                    <p>${(topic.topicContext)!}</p>
                </section>
                <footer>
                    <div class = "col-xs-12" id = "commentTListBox">

                    </div>


                    <div class = "form-group col-xs-12" style = "margin-top: 25px;padding: 2px;">
                        <span onclick = "loadMoreComment()" id = "loadMoreCommentBtn"
                              class = "pull-right hoverPalm loadMoreCommentBtn">更多评论<i
                                    class = "icon-double-angle-down"></i> </span>
                        <hr/>
                        <label for = "commentTContent" id = "commentTInfo"><#if user??>撰写评论:<#else>请先登录</#if></label>

                        <textarea id = "commentTContent" name = "commentTContent" maxlength = "1500"
                                  class = "form-control new-commentT-text" rows = "2"
                                  placeholder = "<#if user??>撰写评论...<#else>请先登录..</#if>"
                                  <#if user??><#else>disabled = "disabled"</#if>></textarea>

                        <button type = "button" id="commentTBtn" onclick = "saveComment('${(topic.topicId)!}')"
                                class = "btn btn-success pull-right"
                                style = "margin-top: 10px;" <#if user??><#else>disabled = "disabled"</#if>>评论
                        </button>

                        <button type = "button" id="commentTReplyBtn" onclick = "commentTReplyAction()"
                                class = "btn btn-success pull-right"
                                style = "margin-top: 10px;" <#if user??><#else>disabled = "disabled"</#if>>回复
                        </button>

                    </div>
                </footer>
            </topic>
        </div>
    </div>
</div>

<!-- 悬浮框结构 -->
<div class="go-top2">
    <div class="panel-heading">
        <i class="icon-paper-clip"></i>话题发布者资料
    </div>
    <div class="panel-body">
        <span class="label"><i class="icon-time"></i> 注册时间：</span> <span
                class="label label-success">${(topic.userRegisterTime)}</span>
        <br/>
        <span class="label"><i class="icon-user"></i> 用户名&emsp;：</span> <span
                class="label label-success">${(topic.userName)!}</span>
        <br/>
        <span class="label"><i class="icon-leaf"></i> 状态&emsp;&emsp;：</span>
        <#if (user.userFrozen)?? && (topic.userFrozen)==1>
            <span class="label label-danger">冻结</span>
        <#else >
            <span class="label label-success">正常</span>
        </#if>
        <br/>
    </div>
</div>

<script>
    $("#commentTReplyBtn").hide();
    let topicId = '${(topic.topicId)!}';
    let pageNumber = 1;
    let totalPage = 1;
    let commentTId ;

    function commentTReply(commentTId, userName) {
        $("#commentTInfo").text("回复：" + userName);
        $("#commentTContent").attr("placeholder", "回复：" + userName);
        this.commentTId = commentTId;

        $("#commentTReplyBtn").show();
        $("#commentTBtn").hide();

    }

    function commentTReplyAction() {
        let commentTContent = $('#commentTContent').val();
        if (!checkNotNull(commentTContent) || commentTContent.length < 1) {
            zuiMsg("请填写评论");
            return;
        }
        if(!checkNotNull(commentTId)){
            zuiMsg("程序出现错误，请刷新页面重试");
            return;
        }
        $.post("/user/commentTReply", {
                commentTId: commentTId,
                topicId: topicId,
                commentTContent: commentTContent
            },
            function (data) {
                if (data.code === 200) {
                    zuiSuccessMsg("评论成功~");
                    $('#commentTContent').val("");
                    return;
                }
                zuiMsg(data.message);
            });
    }

    function goodComment(obj, commentTId) {
        $.post("/goodCommentT", {
                commentTId: commentTId
            },
            function (data) {
                if (data.code === 200) {
                    zuiSuccessMsg("点赞成功~");
                    $(obj).addClass("goodCommentBgColor");
                    let goodTxt = $(obj).text();
                    goodTxt = goodTxt.replace('点赞', '');
                    goodTxt.replace('次', '');
                    goodTxt = goodTxt.trim();
                    goodTxt = parseInt(goodTxt);
                    goodTxt = ++goodTxt;
                    $(obj).html('<i class="icon-thumbs-o-up"></i> 点赞 ' + goodTxt + '次');
                    return;
                }
                zuiMsg(data.message);
            });
    }

    function saveComment(topicId) {
        let commentTContent = $('#commentTContent').val();
        if (!checkNotNull(commentTContent) || commentTContent.length < 1) {
            zuiMsg("请填写评论");
            return;
        }
        $.post("/user/saveCommentT", {
                topicId: topicId,
                commentTContent: commentTContent
            },
            function (data) {
                if (data.code === 200) {
                    zuiSuccessMsg("评论成功~");
                    $('#commentTContent').val("");
                    addCommentItem(data.data.commentTTime, data.data.commentTGoodNumber, data.data.userName, data.data.commentTContent, data.data.commentTId, 1, 0)
                    return;
                }
                zuiMsg(data.message);
            });
    }

    function topicCollection(topicId) {
        $.post("/topicCollection", {
                topicId: topicId
            },
            function (data) {
                if (data.code === 200) {
                    let topicGoodNumber = $("#topicCollectionNumber").text();
                    $("#topicCollectionNumber").text(++topicGoodNumber);
                    new $.zui.Messager(data.message, {
                        type: 'success',
                        placement: 'center'
                    }).show();
                    return;
                }
                zuiMsg(data.message);
            });
    }


    function topicGood(topicId) {
        $.post("/topicGood", {
                topicId: topicId
            },
            function (data) {
                if (data.code === 200) {
                    let topicGoodNumber = $("#topicGoodNumber").text();
                    $("#topicGoodNumber").text(++topicGoodNumber);
                    new $.zui.Messager(data.message, {
                        type: 'success',
                        placement: 'center'
                    }).show();
                    return;
                }
                zuiMsg(data.message);
            });
    }


    $(function () {
        $("#loadMoreCommentBtn").hide();
        $.post("/commentT/list", {
                topicId: topicId
            },
            function (data) {
                if (data.code === 200) {
                    let commentTList = data.data.list;
                    pageNumber = data.data.pageNumber;
                    totalPage = data.data.totalPage;
                    if (pageNumber < totalPage) {
                        $("#loadMoreCommentBtn").show();
                    } else {
                        $("#loadMoreCommentBtn").hide();
                    }
                    for (let i = 0; i < commentTList.length; i++) {
                        addCommentItem(commentTList[i].commentTTime, commentTList[i].commentTGoodNumber, commentTList[i].userName, commentTList[i].commentTContent, commentTList[i].commentTId, 0, commentTList[i].isGoodComment)
                    }

                    return;
                }
                zuiMsg("获取评论失败！");
            });
    })


    function loadMoreComment() {
        if (pageNumber >= totalPage) {
            return;
        }
        $.post("/commentT/list", {
                topicId: topicId,
                pageNumber: ++pageNumber
            },
            function (data) {
                if (data.code === 200) {
                    let commentTList = data.data.list;
                    pageNumber = data.data.pageNumber;
                    totalPage = data.data.totalPage;
                    if (pageNumber < totalPage) {
                        $("#loadMoreCommentBtn").show();
                    } else {
                        $("#loadMoreCommentBtn").hide();
                    }
                    for (let i = 0; i < commentTList.length; i++) {
                        addCommentItem(commentTList[i].commentTTime, commentTList[i].commentTGoodNumber, commentTList[i].userName, commentTList[i].commentTContent, commentTList[i].commentTId, 0, commentTList[i].isGoodComment)
                    }

                    return;
                }
                zuiMsg("获取评论失败！");
            });
    }

    function addCommentItem(commentTTime, commentTGoodNumber, userName, commentTContent, commentTId, isNewComment, isGoodComment) {
        let commentTHtml =
            '<div class="content" style="border-bottom:1px dashed #D7D7D7;margin-bottom: 20px;margin-top: 10px;padding-bottom: 5px;">' +
            '<div class="pull-right text-muted"><i class="icon-time"></i> ' + commentTTime + '</div> ' +
            '<div>' +
            '<strong>' +
            '<i class="icon-user"></i>' + userName + ' 说：' +
            '</strong>' +
            '</div>' +
            '<div class="text">&emsp;&emsp;' + commentTContent + '</div>' +
            '<div class="actions">'
            // '<span class="hoverPalm llBox" onclick="commentTReply(\'' + commentTId + '\')"><i class="icon-edit"></i> 回复</span>';

        if (isGoodComment != null && isGoodComment === 1) {
            commentTHtml += ' <span class="hoverPalm llBox goodCommentBgColor" onclick="goodComment(this,\'' + commentTId + '\')"><i class="icon-thumbs-o-up"></i> 点赞 ' + commentTGoodNumber + '次</span>';
        } else {
            commentTHtml += ' <span class="hoverPalm llBox" onclick="goodComment(this,\'' + commentTId + '\')"><i class="icon-thumbs-o-up"></i> 点赞 ' + commentTGoodNumber + '次</span>';
        }
        commentTHtml +=
            '</div>' +
            '<div class="commentTs-list" id="commentTReplyBox"></div>' +
            '</div>';

        if (isNewComment === 1) {
            $("#commentTListBox").prepend(commentTHtml);
        } else {
            $("#commentTListBox").append(commentTHtml);
        }
    }

</script>
<#include "../import/viewBottom.ftl">
