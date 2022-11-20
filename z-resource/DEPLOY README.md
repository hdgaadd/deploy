# Ubuntu(22.04 64位)部署

## aliyun.com reference

- [安装图形界面](https://blog.csdn.net/weixin_44285445/article/details/107485161)

- [配置公网访问]()

  ```
  优先级		协议类型		端口范围		授权对象		描述	
  
  1	      自定义TCP     目的:8066/8066    源:0.0.0.0/0     8066
  ```

- [阿里云服务器恢复出厂设置](https://blog.csdn.net/liming1016/article/details/107605782)





## JRE8u341

> [reference](https://www.cnblogs.com/raoyulu/p/13265419.html#:~:text=二：linux系统中安装JDK8 1、下载jdk1.8 下载地址：,http%3A%2F%2Fwww.oracle.com%2Ftechnetwork%2Fjava%2Fjavase%2Fdownloads%2Fjdk8-downloads-2133151.html 根据操作系统的位数（显示x86_64 是64位，或者不显示为32位），选择对应jdk版本下载 2、通过Xftp工具将下载的jdk安装包上传至服务器)

- **解压**

  ```shell
  tar -zxvf jdk-8u341-linux-x64.tar.gz
  ```

- **环境变量**

  ```shell
  vim /etc/profile
  ```

  ```shell
  JAVA_HOME=/usr/java/jdk/jdk1.8.0_341
  PATH=$JAVA_HOME/bin:$PATH
  CLASSPATH=$JAVA_HOME/jre/lib/ext:$JAVA_HOME/lib/tools.jar
  export PATH JAVA_HOME CLASSPATH
  ```

- **刷新profile**

  ```shell
  source /etc/profile
  ```

  ```shell
  java -version
  ```

- **卸载**

  ```shell
  dpkg --list | grep -i jdk
  apt-get purge jdk*
  apt-get purge icedtea-* jdk-*
  ```








## MySQL5.7.26

> [reference](https://blog.csdn.net/qq_37598011/article/details/93489404)

- **解压**

  ```shell
  tar -zxvf mysql-5.7.26-linux-glibc2.12-x86_64.tar.gz
  ```

- **创建mysql用户组和用户并修改权限**

  ```shell
  groupadd mysql
  useradd -r -g mysql mysql
  ```

- **创建data的mysql目录，默认是在根目录的data**

  ```shell
  mkdir -p /data/mysql
  ```

- **赋予权限**

  ```shell
  chown mysql:mysql -R /data/mysql
  ```

- **配置my.cnf**

  ```shell
  vim /etc/my.cnf
  ```

  ```shell
  [mysqld]
  bind-address=0.0.0.0
  port=3306
  user=mysql
  basedir=/usr/java/mysql/mysql-5.7.26-linux-glibc2.12-x86_64
  datadir=/data/mysql
  socket=/tmp/mysql.sock
  log-error=/data/mysql/mysql.err
  pid-file=/data/mysql/mysql.pid
  #character config
  character_set_server=utf8mb4
  symbolic-links=0
  explicit_defaults_for_timestamp=true
  ```

- **进行mysql的bin目录执行以下进行初始化**

  ```shell
  ./mysqld --defaults-file=/etc/my.cnf --basedir=/usr/java/mysql/mysql-5.7.26-linux-glibc2.12-x86_64/ --datadir=/data/mysql/ --user=mysql --initialize
  ```

- **移动mysql.server到/etc/init.d/mysql**

  - ```shell
    cp /usr/java/mysql/mysql-5.7.26-linux-glibc2.12-x86_64/support-files/mysql.server /etc/init.d/mysql
    ```

  - 启动

    ```shell
    service mysql start
    ```

  - 若失败：Failed to start mysql.service: Unit mysql.service not found.

    ```shell
    1.https://blog.csdn.net/shenhaiyushitiaoyu/article/details/121396643
    
    执行cat /etc/my.cnf，复制以下
    basedir=/usr/java/mysql/mysql-5.7.26-linux-glibc2.12-x86_64
    datadir=/data/mysql
    
    沾沾到以下
    vim /etc/init.d/mysql
    
    
    
    2.解除mysql.service的标记，重新启动:https://blog.csdn.net/weixin_43297727/article/details/115386986
    systemctl unmask mysql.service
    ```

- **查看、设置密码**

  - 查看临时密码

    cat /data/mysql/mysql.err

  - 设置

    cd到mysql安装包的**bin**目录下，执行

    ```shell
    设置密码
    SET PASSWORD = PASSWORD('密码字符串');
    设置密码不过期
    ALTER USER 'root'@'localhost' PASSWORD EXPIRE NEVER;
    刷新特权
    FLUSH PRIVILEGES;
    ```

- **启动、关闭**

  - 启动

    ```shell
    service mysql start &
    ```

  - 查看是否启动

    ```shell
    cd /usr/java/mysql/mysql-5.7.26-linux-glibc2.12-x86_64/bin
    
    ./mysql -u账号 -p密码
    ```

  - 关闭

    ```shell
    service mysql stop
    ```

  - 登录

    ```shell
    cd到bin目录
    
    ./mysql -u账号 -p密码
    ```










## jar

- **启动**

  - 关闭Xshell，服务关闭

    ```shell
    java -jar baby-1.0-SNAPSHOT.jar &
    ```

  - 关闭Xshell，服务**仍然启动**

    ```shell
    nohup java -jar baby-1.0-SNAPSHOT.jar &
    
    最好在jar的文件夹上进行以上命令，否则在其他文件夹执行，则会生成多个nohup.out，造成异常：ignoring input and appending output to 'nohup.out'
    若出现以上异常，需要删除其他的'nohup.out'
    ```

- **停止**

  ```shell
  ps -ef | grep baby-1.0-SNAPSHOT.jar
  
  kill -s 9 10064
  ```














## Redis(Debian)

> [reference](https://www.redis.com.cn/redis-installation-on-ubuntu.html)

- **安装**

  ```shell
  sudo apt update 
  sudo apt full-upgrade
  sudo apt install build-essential tcl
  
  
  sudo apt-get install redis-server
  ```

- **启动**

  ```shell
  redis-server
  ```

- **停止**

  > ubuntu默认会开机启动Redis，且**无法使用**kill -9或redis-cli shutdown停止Redis，可使用以下命令

  ```shell
  /etc/init.d/redis-server stop
  ```

- **bugs**

  - **第一次启动出现以下异常**

    ```
    Could not create server TCP listening socket *:6379: bind: Address already in use
    ```

    ```
    停止进程后重新启动
    
    /etc/init.d/redis-server stop
    redis-server
    ```

    

## Zookeeper3.6.3

> [reference](https://blog.csdn.net/cwb228/article/details/122894192?ops_request_misc=%7B%22request%5Fid%22%3A%22166426339916800180634632%22%2C%22scm%22%3A%2220140713.130102334..%22%7D&request_id=166426339916800180634632&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~blog~baidu_landing_v2~default-2-122894192-null-null.nonecase&utm_term=ZooKeeper的安装部署&spm=1018.2226.3001.4450)
>
> [download address](https://downloads.apache.org/zookeeper/zookeeper-3.6.3/)

- **创建log文件夹**

  ```shell
  cd /usr/java/zookeeper/apache-zookeeper-3.6.3-bin/
  
  mkdir data
  cd data
  mkdir snapshot
  mkdir log
  ```

- **连接JDK**

  ```shell
  cd /usr/java/zookeeper/apache-zookeeper-3.6.3-bin/conf
  
  touch java.env
  vi java.env
  ```

  ```shell
  如果是jdk8，需要修改JAVA_HOME位置
  
  #!/bin/sh
  #配置JDK目录
  export JAVA_HOME=/usr/java/jdk/jdk-11.0.16
  #配置JVM参数
  export JVMFLAGS="-Xms1024m -Xmx1024m $JVMFLAGS"
  ```

  ```shell
  修改java.env文件执行权限
  chmod u+x java.env
  ```

- **修改Zookeeper配置文件**

  ```shell
  cd /usr/java/zookeeper/apache-zookeeper-3.6.3-bin/conf
  
  cd /usr/java/zookeeper/apache-zookeeper-3.6.3-bin/conf
  
  vi zoo.cfg
  全部cv即可
  ```

  ```shell
  # The number of milliseconds of each tick
  tickTime=2000
  # The number of ticks that the initial 
  # synchronization phase can take
  initLimit=10
  # The number of ticks that can pass between 
  # sending a request and getting an acknowledgement
  syncLimit=5
  # the directory where the snapshot is stored.
  # do not use /tmp for storage, /tmp here is just 
  # example sakes.
  dataDir=/tmp/zookeeper
  # the port at which the clients will connect
  clientPort=2181
  # The number of snapshots to retain in dataDir
  autopurge.snapRetainCount=3
  # Purge task interval in hours
  # Set to "0" to disable auto purge feature
  autopurge.purgeInterval=1
  #事务日志存储目录
  dataLogDir=/usr/java/zookeeper/apache-zookeeper-3.6.3-bin/data/log
  #HTTP方式查看服务信息
  admin.serverPort=8081
  #开启审核日志
  audit.enable=true
  ```

- **启动与停止**

  ```shell
  ./zkServer.sh start
  ./zkServer.sh stop
  ```




# knowledge

- **不能使用ps -ef | grep，来查看某服务是否运行**

  MySQL**关闭**后，执行**ps -ef | grep mysql.server**:，仍打印以下，故不能

  ```shell
  root       21442   21278  0 21:44 pts/0    00:00:00 grep --color=auto mysql.server
  ```

- **JDK切换**

  - jdk8 -> jdk11

    注释/etc/profile

  - jdk11 -> jdk8

    注释/etc/profile.d/java.sh
