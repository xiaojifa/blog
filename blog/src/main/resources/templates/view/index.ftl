<#include "../import/top.ftl">
<#include "../import/navbar.ftl">
<style>
    .col2Padding {
        padding-left: 2px;
        padding-right: 2px;
    }

    .articleTagStyle {
        color: #ffffff;
    }

    .articleTagStyle:hover {
        font-weight: bold;
    }

    .go-top {
        position: fixed;	        /* 设置fixed固定定位 */
        bottom: 20px;		/* 距离浏览器窗口下边框20px */
        right: 20px;		/* 距离浏览器窗口右边框20px */
    }

    .go-top a {
        display: block;			/* 将<a>标签设为块元素，用于美化样式 */
        text-decoration: none;		/* 取消超链接下画线 */
        color: #333;			/* 设置文本颜色 */
        background-color: #f2f2f2;		/* 设置背景颜色 */
        border: 1px solid #ccc;		/* 设置边框样式 */
        padding: 10px 20px;			/* 设置内边距 */
        border-radius: 5px;			/* 设置圆角矩形 */
        letter-spacing: 2px;		/* 设置文字间距 */
    }
</style>

<div class="container ">
    <div class="col-xs-12  col-sm-9 col2Padding ">
        <#if adIndexList?? && adIndexList?size gt 0 >
            <div class="img-thumbnail container-fluid my-navbar4" style="margin-bottom: 10px;">
                <div id="myNiceCarousel" class="carousel slide" data-ride="carousel">
                    <ol class="carousel-indicators">
                        <#list adIndexList as adIndex>
                            <#if adIndex_index == 0>
                                <li data-target="#myNiceCarousel" data-slide-to="${adIndex_index!}" class="active"></li>
                            <#else >
                                <li data-target="#myNiceCarousel" data-slide-to="${adIndex_index!}"></li>
                            </#if>
                        </#list>
                    </ol>
                    <div class="carousel-inner">
                        <#list adIndexList as adIndex>
                            <#if adIndex_index == 0>
                                <div class="item active">
                                    <a target="_blank" href="${(adIndex.adLinkUrl)!}">
                                        <img alt="" src="${(adIndex.adImgUrl)!}">
                                    </a>
                                </div>
                            <#else >
                                <div class="item">
                                    <a target="_blank" href="${(adIndex.adLinkUrl)!}">
                                        <img alt="First slide" src="${(adIndex.adImgUrl)!}">
                                        <div class="carousel-caption">
                                            <h3>${(adIndex.adTitle)!}</h3>
                                        </div>
                                    </a>
                                </div>
                            </#if>
                        </#list>
                    </div>
                    <a class="left carousel-control" href="#myNiceCarousel" data-slide="prev">
                        <span class="icon icon-chevron-left"></span>
                    </a>
                    <a class="right carousel-control" href="#myNiceCarousel" data-slide="next">
                        <span class="icon icon-chevron-right"></span>
                    </a>
                </div>
            </div>
        </#if>

        <h4 style="margin-top: 0px;"><i class="icon icon-time"></i>最新文章</h4>
        <#if indexArticleList?? && indexArticleList?size gt 0>
            <#list indexArticleList as indexArticle>
                <div class="col-xs-6 col-sm-3" style="padding: 2px">
                    <div class = "card container-fluid my-navbar3">
                        <a href = "/article?articleId=${(indexArticle.articleId)!}" style = "text-decoration:none">
                            <img class = "cardimg" src = "${(indexArticle.articleCoverUrl)!}"
                                 alt = "${(indexArticle.articleTitle)!}">
                            <div class = "card-heading"><strong>${(indexArticle.articleTitle)!}</strong></div>
                            <div class = "card-content text-muted">
                                <i class = "icon icon-time"></i>
                                ${(indexArticle.articleAddTime)!}
                            </div>

                            <div class = "card-actions">
                                    <span class = "label label-info">
                                        <i class = "icon-eye-open"></i> ${(indexArticle.articleLookNumber)!}
                                    </span> |
                                <span class = "label label-success">
                                        <i class = "icon-thumbs-up"></i> ${(indexArticle.articleGoodNumber)!}
                                    </span> |
                                <span class = "label label-primary">
                                        <i class = "icon-star"></i> ${(indexArticle.articleCollectionNumber)!}
                                    </span>
                            </div>
                        </a>
                    </div>
                </div>
            </#list>
        </#if>
    </div>

    <div class="col-xs-12  col-sm-3 col2Padding ">
        <div class="panel panel-primary">
            <div class="panel-heading" style="background-image: linear-gradient(135deg, #fc00ff, #00dbde);">
                🔥 热门文章
            </div>
            <div class="panel-body">
                <ul class="list-group">
                    <#if articleHotList?? && articleHotList?size gt 0 >
                        <#list articleHotList as articleHot>
                        <#--                            console.log(articleHotList)-->
                            <li class="list-group-item">
                                <a style="text-decoration: none;"
                                   href="/article?articleId=${(articleHot.articleId)!}">${(articleHot.articleAddTime)!}
                                    ：${(articleHot.articleTitle)!}</a>
                            </li>
                        </#list>
                    </#if>
                </ul>
            </div>
        </div>

        <div class="panel panel-primary ">
            <div class="panel-heading" style="background-image: linear-gradient(135deg, #fc00ff, #00dbde);">
                文章标签
            </div>
            <div class="panel-body" id="articleTagListBox">
                <#if articleTagList?? && articleTagList?size gt 0 >
                    <#list articleTagList as articleTag>
                        <span class="label label-badge"><a class="articleTagStyle" style="text-decoration: none;"
                                                           href="/tag/article/list?articleTagId=${(articleTag.articleTagId)!}">${(articleTag.articleTagName)!}</a></span>
                    </#list>
                </#if>
            </div>
        </div>
    </div>

    <div class="col-xs-12  col-sm-3 col2Padding ">
        <div class="panel panel-primary">
            <div class="panel-heading" style="background-image: linear-gradient(135deg, #fc00ff, #00dbde);">
                🔥 热门话题
            </div>
            <div class="panel-body">
                <ul class="list-group">
                    <#if topicHotList?? && topicHotList?size gt 0 >
                        <#list topicHotList as topicHot>
                        <#--                            console.log(articleHotList)-->
                            <li class="list-group-item">
                                <a style="text-decoration: none;"
                                   href="/topic?topicId=${(topicHot.topicId)!}">${(topicHot.topicAddTime)!}
                                    ：${(topicHot.topicTitle)!}</a>
                            </li>
                        </#list>
                    </#if>
                </ul>
            </div>
        </div>

        <div class="panel panel-primary ">
            <div class="panel-heading" style="background-image: linear-gradient(135deg, #fc00ff, #00dbde);">
                话题标签
            </div>
            <div class="panel-body" id="topicTagListBox">
                <#if topicTagList?? && topicTagList?size gt 0 >
                    <#list topicTagList as topicTag>
                        <span class="label label-badge"><a class="topicTagStyle" style="text-decoration: none;"
                                                           href="/tag/topic/list?articleTagId=${(topicTag.topicTagId)!}">${(topicTag.topicTagName)!}</a></span>
                    </#list>
                </#if>
            </div>
        </div>
    </div>
</div>

<!-- 悬浮框结构 -->
<div class="go-top">
    <a href="#">返回<br>顶部</a>
</div>


<script>

    $(function () {
        let labelClassList = ["label-badge", "label-primary", "label-success", "label-info", "label-warning", "label-danger"];
        let articleTags = $("#articleTagListBox span");
        for (let i = 0; i < articleTags.length; i++) {
            $(articleTags[i]).addClass(labelClassList[Math.floor(Math.random() * labelClassList.length)]);
        }
    })

    $(function () {
        let labelClassList = ["label-badge", "label-primary", "label-success", "label-info", "label-warning", "label-danger"];
        let topicTags = $("#topicTagListBox span");
        for (let i = 0; i < topicTags.length; i++) {
            $(topicTags[i]).addClass(labelClassList[Math.floor(Math.random() * labelClassList.length)]);
        }
    })

</script>

<#include "../import/viewBottom.ftl">
