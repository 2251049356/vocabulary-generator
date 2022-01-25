# 英中词汇表生成器

## 项目概要
本项目能根据给定英文单词生成英中词汇表html文档

## 使用方式
注意：需要安装JDK1.8  
1 获取jar包  
从release标签页获取jar包或克隆项目再mvn打包  
2 编写单词文件  
在jar包同级目录下，编写words.txt文件。内容格式如下：
```text
单词1
单词2
.
.
.
单词n
```
3 执行  
打开控制台，进入jar包目录，然后执行命令：
```shell
java -jar vocabulary-generator-v1.0.jar
```
执行完成后会在控制台生成对应报告
