pwa-downloader
==============

**Backup your photo and video from PicasaWebAlbums** through google gdata API using cli java tool. Easy pluggable in Task Scheduler of your system for periodic backups.

In order to use binary distribution of this tool you shoud install latest version of JRE (http://www.java.com/en/download/index.jsp) and configure JAVA_HOME environment variable.

**Get latest binary** release here: [v0.93](https://github.com/afon/pwa-downloader/releases/tag/v0.93)

**Syntax:** 
```
java -jar pwa-downloader-vN.jar -l <login> -p <password> -d <output dir> [-t <threads count>]
 -d <arg>   Required. Local path to directory where to download albums
 -l <arg>   Required. Login used to log in into Picasa
 -p <arg>   Required. Password used to log in into Picasa
 -t <arg>   Optional. Threads quantity used for setting up simultaneous download threads. 20 by default
example: java -jar pwa-downloader-0.91.jar -l afon -p 5IsufSog -d D:\picasabackups\afon 
``` 

In order to build sources you shoud use min JDK 6 and Maven 3. For bin dist compilation use 'mvn clean package assembly:single', then point to /target dir to find latest pwa-downloader-VVV-dist.zip, then unpack anywhere and use.

**Licensed under 'Apache 2' license.**
