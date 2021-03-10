package com.highestpeak.dimlight;

import com.highestpeak.dimlight.model.entity.RSSSourceTag;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import com.highestpeak.dimlight.utils.RSSUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("CommentedOutCode")
public class SimpleTest {
    public static void main(String[] args) {
//        Arrays.stream(RSSSourceStatus.values()).forEach(System.out::println);
//        String s = Arrays.toString(RSSSourceStatus.values());
//        System.out.println(EnumUtils.getEnumMap(TaskStatus.class).keySet());
        RSSXml rssXml = RSSUtils.getRSSXml("https://rsshub.app/bilibili/bangumi/media/9192");
        System.out.println("rssXml.getTitle() = " + rssXml.getTitle());
    }

    public static void test1(){
        String dbStr = String.join(",", Arrays.asList("blog", "ugc", "video"));
        StringTokenizer tokenizer = new StringTokenizer(dbStr,",");
        List<RSSSourceTag> result = new ArrayList<>(tokenizer.countTokens());
        while (tokenizer.hasMoreTokens()){
            String token = tokenizer.nextToken();
//            result.add(new RSSSourceType(token,null));
        }
        result.forEach(System.out::println);
    }

    public static void test2(){
        /**
         * 博客,论坛,UGC
         * 博客
         *
         */
        String str = "博客,论坛,UGC";
        String pattern = "^([^,]+,)*";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        System.out.println(m.matches());
    }
}
