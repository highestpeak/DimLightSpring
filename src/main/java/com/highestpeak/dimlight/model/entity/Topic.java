package com.highestpeak.dimlight.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author highestpeak
 * 主题订阅（用户可控的）（编程的，对信息进行自动操作的，但是是根据不同RSS来源来写的逻辑，这个地方可以编程操作）
 *  （输入信息并且输出到不同的队列，相当于一个分组）
 *  topic 可以是用户手动定制的标签 也可以是编程控制的标签
 *  topic 貌似没什么用。。。
 */
//@Entity(name = "topic")
@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Topic extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom_id")
    @GenericGenerator(name = "custom_id", strategy = "com.highestpeak.dimlight.model.entity.support.CustomIdGenerator")
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "desc")
    private String desc;

    @Column(name = "type", nullable = false)
    private String type;
}
