# 运行须知
采用Maven构建工程，在`pom.xml`文件中配置如下：
```xml
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>5.0.0.Alpha1</version>
</dependency>
```
netty采用的**5.0**版本。

###examples位置
所有的demo程序均位于`src\main\java\com\yao\netty`目录下。

ps：Git clone代码后，建议采用Intellij IDEA导入Maven工程，可直接运行。