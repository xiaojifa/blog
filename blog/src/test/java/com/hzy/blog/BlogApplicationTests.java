package com.hzy.blog;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import com.hzy.blog.entity.*;
import com.hzy.blog.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest
class BlogApplicationTests {

    @Resource
    private IUserService userService;
    @Resource
    private IArticleTypeService articleTypeService;
    @Resource
    private IArticleTagService articleTagService;
    @Resource
    private IArticleService articleService;
    @Resource
    private IArticleTagListService articleTagListService;
    @Resource
    private ILinkService linkService;
    @Resource
    private IAdService adService;
    @Resource
    private IAdTypeService adTypeService;
    @Resource
    private IAdminService adminService;

    @Test
    void contextLoads() {

        ArrayList<User> users = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            User user = new User();
            user.setUserName(i + "uName");
            user.setUserPassword(SecureUtil.md5("123456"));
            user.setUserFrozen(0);
            user.setUserRegisterTime(LocalDateTime.now());
            users.add(user);
        }
        userService.saveBatch(users, 50);
    }

    /**
     * 添加文章相关的模拟数据
     */
    @Test
    void addArticleData() {
        List<User> users = userService.list();

        List<ArticleType> articleTypeList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ArticleType articleType = new ArticleType();
            articleType.setArticleTypeName("文章分类" + i);
            articleType.setArticleTypeSort(10);
            articleType.setArticleTypeAddTime(LocalDateTime.now());
            articleTypeList.add(articleType);
        }
        articleTypeService.saveBatch(articleTypeList);

        ArrayList<Article> articles = new ArrayList<>();
        for (ArticleType articleType : articleTypeList) {
            for (int i = 0; i < 6; i++) {
                Article article = new Article();
                article.setArticleTypeId(articleType.getArticleTypeId());
                article.setUserId(users.get(ThreadLocalRandom.current().nextInt(users.size())).getUserId());
                article.setArticleTitle("文章标题：" + i);
                article.setArticleAddTime(LocalDateTime.now());
                article.setArticleContext("文章内容：" + ThreadLocalRandom.current().nextInt(1000));
                article.setArticleGoodNumber(0);
                article.setArticleLookNumber(0);
                article.setArticleCollectionNumber(0);
                articles.add(article);
            }
        }
        articleService.saveBatch(articles, 50);

        ArrayList<ArticleTag> articleTags = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ArticleTag articleTag = new ArticleTag();
            articleTag.setArticleTagName("文章标签：" + i);
            articleTag.setArticleTagAddTime(LocalDateTime.now());
            articleTags.add(articleTag);
        }
        articleTagService.saveBatch(articleTags, 50);

        ArrayList<ArticleTagList> articleTagLists = new ArrayList<>();
        for (ArticleTag articleTag : articleTags) {
            for (int i = 0; i < 3; i++) {
                ArticleTagList articleTagList = new ArticleTagList();
                articleTagList.setArticleId(articles.get(ThreadLocalRandom.current().nextInt(articles.size())).getArticleId());
                articleTagList.setArticleTagId(articleTag.getArticleTagId());
                articleTagLists.add(articleTagList);
            }
        }
        articleTagListService.saveBatch(articleTagLists, 50);

    }

    @Test
    public void addLinkData() {
        ArrayList<Link> links = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Link link = new Link();
            link.setLinkTitle("百度"+i);
            link.setLinkUrl("http://www.baidu.com");
            link.setLinkLogoUrl("https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png");
            link.setLinkSort(10);
            link.setLinkAddTime(LocalDateTime.now());
            links.add(link);
        }
        linkService.saveBatch(links);
    }

    @Test
    public void addAdData() {
        ArrayList<AdType> adTypes = new ArrayList<>();
        AdType adType1 = new AdType();
        adType1.setAdTypeTitle("首页轮播图广告");
        adType1.setAdTypeTag("homeAd");
        adType1.setAdTypeSort(0);
        adType1.setAdTypeAddTime(DateUtil.date());
        adTypes.add(adType1);
        AdType adType2 = new AdType();
        adType2.setAdTypeTitle("文章页面广告");
        adType2.setAdTypeTag("articleAd");
        adType2.setAdTypeSort(1);
        adType2.setAdTypeAddTime(DateUtil.date());
        adTypes.add(adType2);
        adTypeService.saveBatch(adTypes);

        Ad ad1 = new Ad();
        ad1.setAdTypeId(adType1.getAdTypeId());
        ad1.setAdTitle("广告1");
        ad1.setAdImgUrl("https://imgtu.com/i/TUCQaQ");
        ad1.setAdLinkUrl("https://www.baidu.com");
        ad1.setAdSort(10);
        ad1.setAdBeginTime(DateUtil.date());
        ad1.setAdEndTime(DateUtil.date());
        ad1.setAdAddTime(DateUtil.date());

        Ad ad2 = new Ad();
        ad2.setAdTypeId(adType1.getAdTypeId());
        ad2.setAdTitle("广告2");
        ad2.setAdImgUrl("https://imgtu.com/i/TUCl5j");
        ad2.setAdLinkUrl("https://www.baidu.com");
        ad2.setAdSort(10);
        ad2.setAdBeginTime(DateUtil.date());
        ad2.setAdEndTime(DateUtil.date());
        ad2.setAdAddTime(DateUtil.date());

        Ad ad3 = new Ad();
        ad3.setAdTypeId(adType2.getAdTypeId());
        ad3.setAdTitle("广告3");
        ad3.setAdImgUrl("https://imgtu.com/i/TUCuqS");
        ad3.setAdLinkUrl("https://www.baidu.com");
        ad3.setAdSort(10);
        ad3.setAdBeginTime(DateUtil.date());
        ad3.setAdEndTime(DateUtil.date());
        ad3.setAdAddTime(DateUtil.date());

        Ad ad4 = new Ad();
        ad4.setAdTypeId(adType2.getAdTypeId());
        ad4.setAdTitle("广告4");
        ad4.setAdImgUrl("https://imgtu.com/i/TUCMVg");
        ad4.setAdLinkUrl("https://www.baidu.com");
        ad4.setAdSort(10);
        ad4.setAdBeginTime(DateUtil.date());
        ad4.setAdEndTime(DateUtil.date());
        ad4.setAdAddTime(DateUtil.date());

        ArrayList<Ad> ads = new ArrayList<>();
        ads.add(ad1);
        ads.add(ad2);
        ads.add(ad3);
        ads.add(ad4);

        adService.saveBatch(ads);


    }

    @Test
    public void addAdminData(){
        Admin admin = new Admin();
        admin.setAdminName("admin");
        admin.setAdminPassword(SecureUtil.md5(admin.getAdminName()+"admin"));
        adminService.save(admin);
    }


}
