## SourceCodeDocxGenerator
 SourceCodeDocxGenerator是一个自动生成软著申请所需的项目源代码Word文档的工具，使用它可以很方便地生成60页的源代码文档，而不用手动撸。  
 SourceCodeDocxGenerator基于Apache POI实现，详情请参考代码。 
 
## 注意
1. 此工具目前只支持生成一种文件类型的源代码文档，如.java，如果要过滤多种文件类型，可以考虑修改此项目源码。
2. 此项目是为了Android软件申请软著开发，所以在目录过滤时未考虑Java项目以及其他语言项目，可能会存在将项目自动生成的代码写进文档中的情况，后续会进行改进。

## 使用SourceCodeDocxGenerator.jar
 本项目已打包成可运行的jar文件，即项目中的SourceCodeDocxGenerator.jar，可以直接在命令行中使用，使用命令格式如下：
 ```
 java -Dfile.encoding=utf-8 -jar jar文件路径 项目路径 软件名称 版本号 源代码文件类型
 ```
 其中`-Dfile.encoding=utf-8`指定了JVM的字符集为UTF-8，以保证生成的Word文档中的中文不会出现乱码。  
 `jar文件路径`是SourceCodeDocxGenerator.jar下载到本地的路径  
 `项目路径`是源代码项目的本地路径  
 `软件名称`和`版本号`是用于生成Word文档的页眉的  
 `源代码文件类型`是指要从项目中提取的源代码的文件类型，如.java等  
 使用SourceCodeDocxGenerator.jar的命令举例：  
 ```
 java -Dfile.encoding=utf-8 -jar D:\Github\SourceCodeDocxGenerator.jar D:\git_workspace\MerchantClient\app XX商户端 V1.0.7 .java
 ```
