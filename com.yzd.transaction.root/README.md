
> 基本设计思路
```
基于补偿机制实现的数据最终一致性
-----------
表：
1.主事务表
2.主事务详情表
3.本地事务确认日志表
```

> 参考
- [Myth分布式事务开源框架（基于消息中间件）-源码解析系列文章](https://juejin.im/post/5a5c63986fb9a01cb64ec517)
- [分布式事务的一种实现方式—状态流转-tmycat1](https://github.com/yu199195/tmycat1)
- [基于可靠消息最终一致性分布式事务框架-myth](https://github.com/yu199195/myth)
- [支付宝 分布式事务服务 DTS 一](https://blog.csdn.net/qq_27384769/article/details/79303744)
- [支付宝 分布式事务服务 DTS 二](https://blog.csdn.net/qq_27384769/article/details/79303942)
- [支付宝 分布式事务服务 DTS三](https://blog.csdn.net/qq_27384769/article/details/79304017)
- []()
- [微服务架构下分布式事务解决方案——阿里GTS-推荐参考byArvin](https://www.cnblogs.com/jiangyu666/p/8522547.html)