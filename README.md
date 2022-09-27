# Maxwell数据迁移方案

> deploy environment: Ubuntu 20.04 64位

## 1. JRE11.0.16

- **创建文件夹**

  ```shell
  cd /usr、mkdir java、cd java、mkdir jdk
  ```

- **上传jdk**

  ```shell
  cd /usr/java/jdk/jdk-11.0.16
  ```

- **解压**

  ```shell
  tar -zxvf jdk-11.0.16_linux-x64_bin.tar.gz
  ```

- **环境变量**

  - 创建脚本

    ```shell
    touch /etc/profile.d/java.sh
    ```

  - 按i编辑

    ```shell
    vim /etc/profile.d/java.sh
    
    JAVA_HOME=/usr/java/jdk/jdk-11.0.16
    CLASSPATH=$JAVA_HOME/lib
    PATH=$JAVA_HOME/bin:$PATH
    export PATH JAVA_HOME CLASSPATH
    
    :wq
    ```

  - 使配置环境生效

    ```shell
    source /etc/profile.d/java.sh
    ```

  - java -version

- **删除jdk11**

  删除安装文件夹、删除java.sh



## 2. MySQL8.0.30(自带)

> [reference](https://blog.csdn.net/wavehaha/article/details/114730222)

- **安装**

  ```shell
  sudo apt-get update
  
  sudo apt-get install mysql-server
  ```

- **检查是否运行**

  ```shell
  systemctl status mysql.service
  ```

- **设置密码**

  ```shell
  sudo mysql_secure_installation
  ```

- **登录**

  ```shell
  在任何地方执行以下
  
  mysql -uroot -proot
  ```

- **knowledge**

  - 以下打印，是询问**是否**使用**密码强度判断组件**，若选择是，则root等简单密码无法设置

    ```
    Securing the MySQL server deployment.
    
    Connecting to MySQL using a blank password.
    
    VALIDATE PASSWORD COMPONENT can be used to test passwords
    and improve security. It checks the strength of password
    and allows the users to set only those passwords which are
    secure enough. Would you like to setup VALIDATE PASSWORD component?
    
    Press y|Y for Yes, any other key for No: 
    ```

    

- **bugs**

  E: Unable to locate package MySQL-server

  ```shell
  再执行以下命令
  
  sudo apt-get update
  
  sudo apt-get install mysql-server
  ```

- **卸载**

  > [reference](https://blog.csdn.net/w3045872817/article/details/77334886)

  ```shell
  查看安装依赖项
  dpkg --list|grep mysql
  
  sudo apt-get remove mysql-common
  
  sudo apt-get autoremove --purge mysql-server-8.0
  
  清除残留数据
  dpkg -l |grep ^rc|awk '{print $2}' |sudo xargs dpkg -P
  ```





## 3. Maxwell1.38.0

> [official website](https://maxwells-daemon.io/quickstart/)
>
> [reference](https://blog.csdn.net/Allenzyg/article/details/105810760)

- **配置binlog**

  ```shell
  vim /etc/my.cnf
  ```

  ```shell
  [mysql_d]
  server_id=1
  log-bin=master
  binlog_format=row
  ```

  ```shell
  service mysql restart
  ```

- **MySQL创建maxwell用户**

  ```shell
  mysql -uroot -proot
  ```

  以下直接沾沾即可全部执行

  ```shell
  CREATE database maxwell;
  CREATE USER 'maxwell'@'%' IDENTIFIED BY 'maxwell';
  GRANT ALL ON maxwell.* TO 'maxwell'@'%';
  GRANT SELECT, REPLICATION CLIENT, REPLICATION SLAVE ON *.* TO 'maxwell'@'%';
  flush privileges;
  ```

- **编辑maxwell配置文件**

  ```shell
  cd /usr/java/maxwell/maxwell-1.38.0
  
  mv config.properties.example config.properties
  
  vim config.properties
  
  任性以下两种配置，推荐two
  ```

- **两种配置，两种运行方式**

  - **one**

    ```shell
    log_level=info
    
    # mysql login info
    host=localhost
    ```

    ```shell
    bin/maxwell --user='maxwell' --password='maxwell' --host='127.0.0.1' --producer=stdout
    ```

  - **two**

    ```shell
    log_level=info
    
    # mysql login info
    host=localhost
    user=maxwell
    password=maxwell
    ```

    ```shell
    bin/maxwell --config=./config.properties
    ```

- **创建非maxwell数据库，编写SQL测试，注意是非maxwell数据库**

  ```shell
  CREATE DATABASE hdgaadd;
  
  use hdgaadd;
  
  DROP TABLE IF EXISTS `test`;
  CREATE TABLE `test`  (
    `id` int(11) NULL DEFAULT NULL,
    `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL
  ) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Compact;
  
  INSERT INTO `test`(`id`, `name`) VALUES (1, '1');
  UPDATE `test` SET `id` = 1, `name` = '1333333333' WHERE `id` = 1 LIMIT 1;
  DELETE FROM `test`;
  ```

- **bugs**

  - **测试数据库必须使用非maxwell**

    若**使用maxwell数据库**，会出现插入、更新表，maxwell**没有监听**到

    ```mysql
    CREATE DATABASE hdgaadd
    
    user hdgaadd;
    ```

  - **maxwell-1.38.0要求的jdk版本必须 >= 11**

    ```shell
    Error: A JNI error has occurred, please check your installation and try again
    Exception in thread "main" java.lang.UnsupportedClassVersionError: com/zendesk/maxwell/Maxwell has been compiled by a more recent version of the Java Runtime (class file version 55.0), this version of the Java Runtime only recognizes class file versions up to 52.0
    ```

  - **ERROR: Operation CREATE USER failed for 'maxwell'@'%**

    ```shell
    删除该用户后创建
    drop user maxwell
    
    若是以下异常，则执行以下命令
    ERROR: Operation CREATE USER failed for 'maxwell'@'%localhos'
    drop user 'maxwell'@'localhost';
    ```

- **knowledge**

  - **binlog是ubuntu的日志文件，mysql-bin是win的日志文件**

    - win

      ```shell
      show master logs;
      +------------------+-----------+
      | Log_name         | File_size |
      +------------------+-----------+
      | mysql-bin.000001 |       393 |
      +------------------+-----------+
      ```

    - ubuntu

      ```shell
      show master logs;
      +---------------+-----------+-----------+
      | Log_name      | File_size | Encrypted |
      +---------------+-----------+-----------+
      | binlog.000001 |       180 | No        |
      | binlog.000002 |       404 | No        |
      | binlog.000003 |       180 | No        |
      | binlog.000004 |       180 | No        |
      | binlog.000005 |       404 | No        |
      | binlog.000006 |      1522 | No        |
      +---------------+-----------+-----------+
      ```

  - **bin设置成什么，日志文件就是什么名称**

    ```shell
    binlog_format=row
    server_id=1 
    log-bin=master
    ```

    ```shell
    mysql> show master logs;
    +---------------+-----------+-----------+
    | Log_name      | File_size | Encrypted |
    +---------------+-----------+-----------+
    | master.000001 |       498 | No        |
    +---------------+-----------+-----------+
    1 row in set (0.00 sec)
    
    ```

  - **查看MySQL日志文件**

    > [reference](https://www.jianshu.com/p/3eb4c44307c1)

    ```shell
    // 查看是否设置日志
    show variables like 'log_%';
    
    // 展示所有biglog，最底下的最新
    show master logs;
    +---------------+-----------+-----------+
    | Log_name      | File_size | Encrypted |
    +---------------+-----------+-----------+
    | binlog.000001 |       180 | No        |
    | binlog.000002 |       404 | No        |
    | binlog.000003 |       180 | No        |
    | binlog.000004 |    111875 | No        |
    +---------------+-----------+-----------+
    
    // 删除所有biglog
    reset master; 
    
    // 打印某个biglog，ubuntu默认是在/var/lib/mysql/binlog
    /usr/bin/mysqlbinlog /var/lib/mysql/binlog.000005
    
    // 由于Base64编码，导致SQL打印看不清楚，可使用以下命令（打印最后一个binlog可查看最新记录）
    /usr/bin/mysqlbinlog --base64-output=decode-rows -v /var/lib/mysql/binlog.000001
    ```

  - **Linux中binlog的文件地址**

    ```shell
    cd /var/lib/mysql/
    ```




## 4. RabbitMQ(自带)

> [reference address](https://www.jianshu.com/p/5c8c4495827f)

- **安装erlong语言环境**

  ```shell
  sudo apt-get install erlang-nox
  
  检查是否成功安装
  erl
  ```

- **添加公钥**

  ```shell
  wget -O- https://www.rabbitmq.com/rabbitmq-release-signing-key.asc | sudo apt-key add -
  ```

- **安装**

  ```shell
  sudo apt-get update
  
  sudo apt-get install rabbitmq-server
  ```

- **检查是否运行**

  ```shell
  systemctl status rabbitmq-server
  ```

- **启动、停止**

  ```shell
  sudo service rabbitmq-server start    # 启动
  sudo service rabbitmq-server stop     # 停止
  sudo service rabbitmq-server restart  # 重启 
  ```

- **安装管理界面**

  > [reference](https://blog.csdn.net/fanbaodan/article/details/103335793)
  > [reference](https://blog.csdn.net/z446981439/article/details/103634524)

  ```shell
  rabbitmq-plugins enable rabbitmq_management
  
  创建用户
  rabbitmqctl add_user hdgaadd root
  设置管理员角色
  rabbitmqctl set_user_tags hdgaadd administrator
  ```

  ```shell
  测试访问
  curl http://IP地址:15672
  ```

- **基于以上对MySQL的配置，再进行配置RabbitMQ**

  > [reference](https://blog.csdn.net/xiehd313/article/details/81289150)

  ```shell
  cd /usr/java/maxwell/maxwell-1.38.0
  
  vim config.properties
  ```

  ```shell
  producer=rabbitmq
  
  rabbitmq_host=localhost
  rabbitmq_port=5672
  rabbitmq_user=hdgaadd
  rabbitmq_pass=root
  rabbitmq_virtual_host=/
  rabbitmq_exchange=maxwell
  rabbitmq_exchange_type=topic
  rabbitmq_exchange_durable=false
  rabbitmq_exchange_autodelete=false
  rabbitmq_routing_key_template=%db%.%table%
  rabbitmq_message_persistent=false
  ```

  以下为**备份**的完整文件

  ```shell
  # tl;dr config
  log_level=info
  
  #producer=rabbitmq
  #kafka.bootstrap.servers=localhost:9092
  
  # mysql login info
  host=localhost
  user=maxwell
  password=maxwell
  
  
  producer=rabbitmq
  
  rabbitmq_host=localhost
  rabbitmq_port=5672
  rabbitmq_user=hdgaadd
  rabbitmq_pass=root
  rabbitmq_virtual_host=/
  rabbitmq_exchange=maxwell
  rabbitmq_exchange_type=topic
  rabbitmq_exchange_durable=false
  rabbitmq_exchange_autodelete=false
  rabbitmq_routing_key_template=%db%.%table%
  rabbitmq_message_persistent=false
  ```

- **运行maxwell**

  ```shell
  cd /usr/java/maxwell/maxwell-1.38.0
  
  bin/maxwell --config=./config.properties
  ```

- **登录RabbitMQ，查看消息**

  > [reference](https://blog.csdn.net/xiehd313/article/details/81289150)

  - Queues模块下: 创建队列，队列名以以上的**rabbitmq_routing_key_template=%db%.%table%**配置为标准，故队列名为

    ```shell
    hdgaadd.test
    ```

  - Exchanges模块下: 点击maxwell，绑定以上队列，点击GetMessage发送消息







# Nacos1.4.3多环境配置

## 1. Nacos1.4.3

- **解压**

  ```shell
  tar -zxvf nacos-server-1.4.3.tar.gz
  ```

- **关闭8848防火墙**

  ```shell
  ufw allow 8848
  ```

  ```shell
  ufw status
  ```

- **启动**

  ```shell
  cd到bin目录
  
  ubuntu系统单机必须使用: 
  bash startup.sh -m standalone
  
  ubuntu系统使用右边命令会报错web异常: sh startup.sh -m standalone
  org.springframework.context.ApplicationContextException: Unable to start web server;...
  ```

- **检查是否运行**

  ```shell
  curl http://127.0.0.1:8848/nacos
  ```

- **外网访问**

  ```
  http://ip地址:8848/nacos
  ```

- **problems**

  - Nacos1.4.3只支持jdk8，jdk11环境下无法运行



## 2. config

- springboot、spring-cloud-starter-alibaba-nacos-config、nacos的版本要对应得上

  [version reference](https://blog.csdn.net/m0_45406092/article/details/123411227)

- **yaml的变量类型为String，要添加""**

  ```yaml
  environment:
          content: halo, nacos-environment-dev
  ```

  ```yaml
  environment:
          content: "halo, nacos-environment-dev"
  ```

- **nacos配置的文件名的格式有要求**

  ```yaml
  项目模块名-命名空间环境名-文件名
  ${prefix}-${spring.profiles.active}.${file-extension}
  ```

  ```yaml
  example-dev-yaml
  
  应该修改为
  
  example-dev.yaml
  ```



# Ubuntu22.04部署

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









## Nacos1.4.3集群

- **配置数据库源**

  在nacos目录下的conf，修改**application.properties**，把以下注释去除

  ```shell
  #*************** Config Module Related Configurations ***************#
  ### If use MySQL as datasource:
   spring.datasource.platform=mysql
  
  ### Count of DB:
   db.num=1
  
  ### Connect URL of DB:
   db.url.0=jdbc:mysql://127.0.0.1:3306/nacos?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useUnicode=true&useSSL=false&serverTimezone=UTC
   db.user.0=账号
   db.password.0=密码
  
  ```

- **创建数据库**

  ```mysql
  create database nacos
  ```

- **创建表**

  执行conf目录下的**nacos-mysql.sql**

  ```mysql
  CREATE TABLE `config_info` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `data_id` varchar(255) NOT NULL COMMENT 'data_id',
    `group_id` varchar(255) DEFAULT NULL,
    `content` longtext NOT NULL COMMENT 'content',
    `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `src_user` text COMMENT 'source user',
    `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
    `app_name` varchar(128) DEFAULT NULL,
    `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
    `c_desc` varchar(256) DEFAULT NULL,
    `c_use` varchar(64) DEFAULT NULL,
    `effect` varchar(64) DEFAULT NULL,
    `type` varchar(64) DEFAULT NULL,
    `c_schema` text,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_configinfo_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info';
  ```










## Redis(自带)

> [reference](https://www.redis.com.cn/redis-installation-on-ubuntu.html)

- **安装ubuntu自带的Redis**

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

  

## Kafka

> [Index of /kafka/2.8.2 (apache.org)](https://downloads.apache.org/kafka/2.8.2/)







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
