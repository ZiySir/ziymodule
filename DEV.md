# 规约
1. 数据库中对外暴露`业务主键`，而内部互相关联则推荐使用`自然主键`

# 初始化项目
1. 更改包名
* 使用ai进行修改
```markdown
阅读整个项目,将包名的前缀和groupId统一从`me.ziyframework`修改到`填写自己的groupId`，我补充一些必须修改的地方
1. 每个项目src下的包名前缀
2. gradle文件中的groupId
3. SpringBoot中关于通过注解定义`basePackages`扫描的路径
4. java spi文件和Spring AutoConfig机制声明的一些全限定类型
* 明确不要修改的地方
  1. 关于依赖相关的groupId
* 基于上面场景自行进一步扩展可能存在的需要修改的地方
* 验收目标：能通过`./gradlew spotlessApply check -x test --no-daemon --no-parallel`命令
```
