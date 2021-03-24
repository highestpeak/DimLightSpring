package com.highestpeak.dimlight;

import com.highestpeak.dimlight.model.entity.RSSSourceTag;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.AbstractElement;
import org.dom4j.tree.DefaultElement;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("CommentedOutCode")
public class SimpleTest {
    public static void main(String[] args) throws Exception {
//        Arrays.stream(RSSSourceStatus.values()).forEach(System.out::println);
//        String s = Arrays.toString(RSSSourceStatus.values());
//        System.out.println(EnumUtils.getEnumMap(TaskStatus.class).keySet());

//        RSSXml rssXml = RSSUtils.getRSSXml("https://rsshub.app/bilibili/bangumi/media/9192");
//        System.out.println("rssXml.getTitle() = " + rssXml.getTitle());

//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        factory.setValidating(true);
//        factory.setIgnoringElementContentWhitespace(true);
//        DocumentBuilder builder = factory.newDocumentBuilder();
//        String opml = "<opml version=\"1.0\">\n" +
//                " <head>\n" +
//                "  <title>Top Ten Sources for podcasting</title>\n" +
//                "  <ownerName>Newsilike Media Group</ownerName>\n" +
//                "  <ownerEmail>opml@TopTenSources.com</ownerEmail>\n" +
//                " </head>\n" +
//                " <body>\n" +
//                "  <outline type=\"link\" text=\"TopTenSources: podcasting\"\n" +
//                "        url=\"http://podcasting.TopTenSources.com/TopTenSources/\" />\n" +
//                "  <outline text=\"CBS Technology News Podcast - Larry Magid' Tech Report\">\n" +
//                "   <outline type=\"link\" text=\"Larry Magid's Tech Report\" url=\"http://www.cbsnews.com\" />\n" +
//                "  </outline>\n" +
//                "  <outline text=\"Adam Curry: Daily Source Code\">\n" +
//                "   <outline type=\"link\" text=\"#374 Daily Source Code for Tuesday April 25th 2006\"\n" +
//                "        url=\"http://radio.weblogs.com/0001014/2006/04/26.html#a7304\" />\n" +
//                "   <outline type=\"link\" text=\"#373 Daily Source Code for Monday April 24th 2006\"\n" +
//                "        url=\"http://radio.weblogs.com/0001014/2006/04/24.html#a7303\" />\n" +
//                "   <outline type=\"link\" text=\"#372 Daily Source Code for Friday April 21st 2006\"\n" +
//                "        url=\"http://radio.weblogs.com/0001014/2006/04/21.html#a7302\" />\n" +
//                "   <outline type=\"link\" text=\"#371 Daily Source Code for Thursday April 20th 2006\"\n" +
//                "        url=\"http://radio.weblogs.com/0001014/2006/04/20.html#a7301\" />\n" +
//                "   <outline type=\"link\" text=\"#370 Daily Source Code for Wednesday April 19th 2006\"\n" +
//                "        url=\"http://radio.weblogs.com/0001014/2006/04/19.html#a7300\" />\n" +
//                "  </outline>\n" +
//                "  <outline text=\"Gillmor Gang\">\n" +
//                "   <outline type=\"link\" text=\"Syndicate Gang Part I\" url=\"http://gillmorgang.podshow" +
//                ".com/?p=44\" />\n" +
//                "   <outline type=\"link\" text=\"HughTrain Gang\" url=\"http://gillmorgang.podshow.com/?p=43\"
//                />\n" +
//                "   <outline type=\"link\" text=\"Phlegm at 11 Gang\" url=\"http://gillmorgang.podshow.com/?p=42\" " +
//                "/>\n" +
//                "   <outline type=\"link\" text=\"NDA Gang\" url=\"http://gillmorgang.podshow.com/?p=41\" />\n" +
//                "   <outline type=\"link\" text=\"When the Music?s Over Gang\" url=\"http://gillmorgang.podshow" +
//                ".com/?p=40\" />\n" +
//                "  </outline>";
//        Document doc = builder.parse(opml);
//        DocumentType doctype = doc.getDoctype();
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("opml");

        Element head = root.addElement("head");
        // buildHead
        Element headTitle = head.addElement("title");
        headTitle.addText("highestpeak");

        Element body = root.addElement("body");
        // buildBody
        Element outline = body.addElement("outline");
        outline.addAttribute("type","rss");
        outline.addAttribute("text","test1");
        outline.addAttribute("xmlUrl","https://xxxxx.rss");

        Element outline1 = body.addElement("outline");
        outline1.addAttribute("type","rss");
        outline1.addAttribute("text","test2");
        outline1.addAttribute("xmlUrl","https://xxxxx2.rss");

        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(System.out, format);
        writer.write( document );

//        parseOpml();

    }

    public static void parseOpml() throws Exception {
//        Document parse = parse(new URL("https://raw.githubusercontent.com/lotosbin/opml-list/master/generate/all
//        .opml"));
        // xPath能够方便的替换选取表达式
        Document document = parse(new File("E:\\_data\\all.opml"));
        Element root = document.getRootElement();
        Node headNode = document.selectSingleNode("opml/head");
        Map<String, String> headValues = null;
        if (headNode != null && headNode.hasContent() && headNode instanceof DefaultElement) {
            DefaultElement headElement = (DefaultElement) headNode;
            List<Node> content = headElement.content();
            headValues = content.parallelStream()
                    .filter(node -> node instanceof DefaultElement)
                    .map(node -> (DefaultElement) node)
                    .collect(Collectors.toMap(AbstractElement::getName, DefaultElement::getText));
        }

        List<Node> list = document.selectNodes("opml/body/outline");
        List<Map<String, String>> outlines = Lists.newArrayList();
        Set<String> keys = Sets.newHashSet();
        for (Node node : list) {
            List<Attribute> attributes = ((DefaultElement) node).attributes();
            Map<String, String> outline = attributes.parallelStream()
                    .collect(Collectors.toMap(Attribute::getName, Attribute::getText));
            outlines.add(outline);
            keys.addAll(outline.keySet());
        }
        System.out.println(keys);
    }

    public static Document parse(URL url) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(url);
        return document;
    }

    public static Document parse(File file) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        return document;
    }

    public static void test1() {
        String dbStr = String.join(",", Arrays.asList("blog", "ugc", "video"));
        StringTokenizer tokenizer = new StringTokenizer(dbStr, ",");
        List<RSSSourceTag> result = new ArrayList<>(tokenizer.countTokens());
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
//            result.add(new RSSSourceType(token,null));
        }
        result.forEach(System.out::println);
    }

    public static void test2() {
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
