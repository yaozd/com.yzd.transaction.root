
> 基本设计思路
```
基于补偿机制实现的数据最终一致性
-----------
表：
1.主事务表
2.主事务详情表
3.本地事务确认日志表
PS:
当前设计的一个缺陷：当事务中断时，无法确认当前分支事务日志最后一步流程是否已经被执行，需要人为异步确认。
```
> 事务回滚基本流程
```
1.获取到当前事务的访问的锁
2.获取到当前主事务中的分支事务列表 (PS:列表信息包含：回滚与没有回滚的数据)
2.判断当前主事务是否有效   PS:情况：无效的事务:指事务只是做了初始化，并没有实际运行
3.确认当前分支事务流程的最后一步流程是否已经被执行
4.确认当前分支事务流程已全部执行完成 （PS:过滤掉已经回滚的数据）
5.回滚所有分支事务流程
```
> 模拟测试事务流程
```
//1.模拟-事务执行
T5Transfer4UnitTest.paymentByTransactional()
//2.模拟-处理运行超时的事务(PS:发版或网络抖动可能会产生次此类问题)
T7TimeoutTransaction4UnitTest.handlerForTimeoutTransaction()
//3.模拟-处理异常的事务-回滚事务
T8ExceptionTransaction4UnitTest.handlerForExceptionTransaction()
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