# ubuntu部署

## aliyun.com reference

- [安装图形界面](https://blog.csdn.net/weixin_44285445/article/details/107485161)

- [配置公网访问]()

  ```
  优先级		协议类型		端口范围		授权对象		描述	
  
  1	       自定义    TCP目的:8066/8066    源:0.0.0.0/0     8066
  ```

- [阿里云服务器恢复出厂设置](https://blog.csdn.net/liming1016/article/details/107605782)

## JDK11.0.16

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





## JDK8u341

> [reference address](https://www.cnblogs.com/raoyulu/p/13265419.html#:~:text=二：linux系统中安装JDK8 1、下载jdk1.8 下载地址：,http%3A%2F%2Fwww.oracle.com%2Ftechnetwork%2Fjava%2Fjavase%2Fdownloads%2Fjdk8-downloads-2133151.html 根据操作系统的位数（显示x86_64 是64位，或者不显示为32位），选择对应jdk版本下载 2、通过Xftp工具将下载的jdk安装包上传至服务器)

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






## MySQL5.7.26

> [reference address](https://blog.csdn.net/qq_37598011/article/details/93489404)

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





## MySQL8.0.30(自带)

> [reference addressl](https://blog.csdn.net/wavehaha/article/details/114730222)

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
  
  mysql -uroot -p
  ```

- **bugs**

  E: Unable to locate package MySQL-server

  ```shell
  再执行以下命令
  
  sudo apt-get update
  
  sudo apt-get install mysql-server
  ```








## jar

- **启动**

  - 关闭Xshell，服务关闭

    ```shell
    java -jar baby-1.0-SNAPSHOT.jar &
    ```

  - 关闭Xshell，服务仍然启动

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





## Nacos1.4.3

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
  
  ubuntu系统单机必须使用: bash startup.sh -m standalone
  
  ubuntu系统使用右边命令会报错web异常: sh startup.sh -m standalone
  org.springframework.context.ApplicationContextException: Unable to start web server;...
  ```

- **检查是否运行**

  ```shell
  curl http://127.0.0.1:8848/nacos
  ```

- **外网访问**

  ```
  http://公网ip:8848/nacos
  ```

- **problems**

  - Nacos1.4.3只支持jdk8，jdk11环境下无法运行





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





## Nacos1.4.3多环境配置

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






## Redis(自带)

> [reference address](https://www.redis.com.cn/redis-installation-on-ubuntu.html)

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

    



## RabbitMQ(自带)

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










## maxwell1.38.0

> [Quick Start - Maxwell's Daemon (maxwells-daemon.io)](https://maxwells-daemon.io/quickstart/)
>
> [Deployment - Maxwell's Daemon (maxwells-daemon.io)](https://maxwells-daemon.io/deployment/)
>
> [[897\]使用Maxwell实时同步mysql数据 - 腾讯云开发者社区-腾讯云 (tencent.com)](https://cloud.tencent.com/developer/article/1705132#:~:text= maxwell是由java编写的守护进程，可以实时读取mysql,binlog并将行更新以JSON格式写入kafka、rabbitMq、redis等中， 这样有了mysql增量数据流，使用场景就很多了，比如：实时同步数据到缓存，同步数据到ElasticSearch，数据迁移等等。)

- **安装**

  ```shell
  curl -sLo - https://github.com/zendesk/maxwell/releases/download/v1.38.0/maxwell-1.38.0.tar.gz \
         | tar zxvf -
  ```

- **配置MySQL**

  ```shell
  vim /etc/my.cnf
  
  server_id=1
  log-bin=master
  binlog_format=row
  gtid-mode=ON
  log-slave-updates=ON
  enforce-gtid-consistency=true
  ```

  ```mysql
  mysql> CREATE USER 'maxwell'@'%' IDENTIFIED BY 'XXXXXX';
  mysql> CREATE USER 'maxwell'@'localhost' IDENTIFIED BY 'XXXXXX';
  
  mysql> GRANT ALL ON maxwell.* TO 'maxwell'@'%';
  mysql> GRANT ALL ON maxwell.* TO 'maxwell'@'localhost';
  
  mysql> GRANT SELECT, REPLICATION CLIENT, REPLICATION SLAVE ON *.* TO 'maxwell'@'%';
  mysql> GRANT SELECT, REPLICATION CLIENT, REPLICATION SLAVE ON *.* TO 'maxwell'@'localhost';
  ```

- **配置文件**

  ```shell
  cd /usr/java/maxwell/maxwell-1.38.0
  
  重命名
  mv config.properties.example config.properties
  
  vim config.properties
  ```

  ```shell
  # tl;dr config
  log_level=info
  
  #producer=kafka
  #kafka.bootstrap.servers=localhost:9092
  
  # mysql login info
  host=localhost
  user=root
  password=root
  
  output_nulls=true
  jdbc_options=autoReconnet=true
  
  #监控数据库中的哪些表
  filter=exclude: *.*,include: maxwell.AA
  
  #replica_server_id 和 client_id 唯一标示，用于集群部署
  replica_server_id=64
  client_id=test-id
  
  #metrics_type=http
  #metrics_slf4j_interval=60
  #http_port=8111
  #http_diagnostic=true # default false
  
  #rabbitmq
  rabbitmq_host=localhost
  rabbitmq_port=5672
  rabbitmq_user=guest
  rabbitmq_pass=guest
  rabbitmq_virtual_host=/
  rabbitmq_exchange=maxwell
  rabbitmq_exchange_type=topic
  rabbitmq_exchange_durable=false
  rabbitmq_exchange_autodelete=false
  rabbitmq_routing_key_template=%db%.%table%
  rabbitmq_message_persistent=false
  rabbitmq_declare_exchange=true
  ```

- **运行**

  ```shell
  cd /usr/java/maxwell/maxwell-1.38.0
  
  ./bin/maxwell --user='root' --password='root' --host='127.0.0.1' --producer=stdout
  
  或后台启动运行
  ./bin/maxwell --user='root' --password='root' --host='127.0.0.1' --producer=stdout &
  ```

- **可配置**

  - 监控的库

  - 监控的表

    ```
    #监控数据库中的哪些表
    filter=exclude: *.*,include: maxwell.AA
    ```

  - 可操作用户

    ```
    mysql> CREATE USER 'maxwell'@'%' IDENTIFIED BY 'XXXXXX';
    ```

- **bugs**

  - **maxwell-1.38.0要求的jdk版本必须 >= 11**

    ```shell
    Error: A JNI error has occurred, please check your installation and try again
    Exception in thread "main" java.lang.UnsupportedClassVersionError: com/zendesk/maxwell/Maxwell has been compiled by a more recent version of the Java Runtime (class file version 55.0), this version of the Java Runtime only recognizes class file versions up to 52.0
    ```




# knowledge

- **不能使用ps -ef | grep，来查看某服务是否运行**

  mysql**关闭**后，执行**ps -ef | grep mysql.server**:，仍打印以下，故不能

  ```
  root       21442   21278  0 21:44 pts/0    00:00:00 grep --color=auto mysql.server
  ```

- **JDK切换**

  - jdk8 -> jdk11

    注释/etc/profile

  - jdk11 -> jdk8

    注释/etc/profile.d/java.sh

- 
