package com.highestpeak.dimlight.utils;

import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.entity.Topic;
import com.highestpeak.dimlight.model.entity.RSSSourceType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

/**
 * @author highestpeak
 * @see Topic#getType()
 */
@SuppressWarnings({"AlibabaClassNamingShouldBeCamel", "AlibabaLowerCamelCaseVariableNaming"})
public class TypeUtils {
    /*
    格式(','分割): "博客,论坛,UGC"
    分隔逗号的正则 "[^,]+"
     */

    /**
     * @param types Type list
     * @return 和数据库中 type 字段格式一致的字符串
     */
    public static String typeStrDB(List<RSSSourceType> types) {
        Collections.sort(types);
        List<String> typeNameList = types.stream().map(RSSSourceType::getName).collect(Collectors.toList());
        return String.join(",", typeNameList);
    }

    /**
     * @param dbStr 和数据库中 type 字段格式一致的字符串
     * @return Type list
     */
    @SuppressWarnings({"CommentedOutCode", "AlibabaRemoveCommentedCode"})
    public static List<RSSSourceType> dbStrToTypeName(String dbStr) {
        // 可能不需要i这些代码了，因为现在是 保存了 type 到数据库
//        StringTokenizer tokenizer = new StringTokenizer(dbStr,",");
//        List<RSSSourceType> result = new ArrayList<>(tokenizer.countTokens());
//        while (tokenizer.hasMoreTokens()){
//            String token = tokenizer.nextToken();
//            result.add(new RSSSourceType(token,null));
//        }
//        return result;
        return null;
    }
}
