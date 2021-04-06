package com.highestpeak.dimlight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author highestpeak
 */
@SpringBootApplication
public class DimLightRssAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(DimLightRssAdminApplication.class, args);
		/**
		 * todo:
		 *  1. 热度：根据插入数据库变化的条目数目和时间的分布
		 *  2. 动态权重：根据最后一条的更新时间来设置拉取权重（每隔多长时间去拉取一次）
		 *  3. 去重算法：针对所有文档的去重，只根据摘要去重
		 *  	定时对一个标签一个topic下的内容进行去重
		 *  4. 排序算法：时间、热度、标题党等进行降权、根据点赞数浏览数加权、如果在我收藏的内容的关键字里的那就进行加权
		 *  	需要让前端用户可以输入一个质量比较，相当于把在代码中写的comparable给了用户，让用户去写，然后我用这个评分
		 *  5. rss挂掉检测: 每个rss都会进行，当尝试fetch几次之后（在jsonExtra中设定值）设定为挂掉
		 */

		/**
		 * todo:
		 *  1. 特殊tag: 收藏、稍后阅读、喜爱、inbox、挂掉风险
		 *  2. rsshub前缀替换(即多个RSSHub服务，每次尝试一个，逐个尝试)，一次性的一个Task，可以随时启动
		 *  3. 多个订阅url，如公众号有多种生成方式，每种的前缀不一样，那就需要多个订阅url，可以方便的替换和删除
		 */

		/**
		 * todo:
		 * 	1. 必须做的Rss源： 视频、动态、文章
		 */
	}

}
