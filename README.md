1. 信息源：Topic、RSSSource、SpiderSource
    1. Topic是聚合的信息源，RSSSource和SpiderSource是原始信息源
2. Tag作为额外信息标注，方便数据清洗和取某个Tag的信息
3. Task是任务元数据，只保存任务的cron等元信息，具体任务type由TaskEnum指定
4. Mobius 只是一个代号,代表各种元数据,Mobius意味莫比乌斯带，莫比乌斯带的形状类似∞无限符号，
   故取无限之意
5. 对于inbox和like：不需要对source进行like和inbox，因为source有topic作为分组，
   不需要对source额外分组like和inbox
6. 使用sonic作为索引引擎，https://github.com/valeriansaliou/sonic，只有几十M内存就可以很好搜索（宣称）
7. 需要把对rss的desc处理后的内容存一下，存一个主干，并且提取出rss的desc的重要的image的src，方便做缩略图显示
   1. 找不到图的话，可以提取出主要关键字，做一个文字云图，然后作为图片
8. 返回前端做简要的contentItem的时候，不需要返回所有desc，可以返回简易摘要的desc，这个摘要是自己生成的，需要判断desc的
原来的长度，如果长度过长，几乎是全文rss了，那就要自己手动提取摘要了
   对标题的长度也要做出限制
6. 自动计算rss源的标题、描述的长度，从而判断是否是全文rss，从而加上标签，进而判断是否进行摘要提取
7. 把文本保存到全文搜索库时需要去除html的一系列标签