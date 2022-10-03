# 数据迁移方案

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

  > 建议two

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







# Nacos多环境配置

> deploy environment: Ubuntu  22.04 64位

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







# 定时系统方案

> deploy environment: Ubuntu  22.04 64位

## 1. Redis集群搭建

> [reference](https://blog.csdn.net/qq_42815754/article/details/82832335)
>
> [reference](https://blog.csdn.net/qq_42815754/article/details/82912130)

- **download & install**

  ```shell
  wget http://download.redis.io/releases/redis-3.0.0.tar.gz
  
  // 安装c++
  sudo apt-get install gcc-c++
  
  tar -zxvf redis-3.0.0.tar.gz
  ```

  ```shell
  // 编译
  
  cd /usr/java/redis/redis-3.0.0
  make
  make install PREFIX=/usr/java/redis/redis-3.0.0-build
  ```

- **运行单机Redis**

  ```shell
  /usr/java/redis/redis-3.0.0-build/bin/redis-server
  ```

- **配置集群配置**

  ```shell
  // 复制conf到bin
  cd /usr/java/redis/redis-3.0.0
  cp -r /usr/java/redis/redis-3.0.0/redis.conf /usr/java/redis/redis-3.0.0-build/bin
  
  
  // 将bin文件移动到/usr/local下新创建的文件夹
  cd /usr/local
  
  mkdir redis-cluster
  
  cp -r /usr/java/redis/redis-3.0.0-build/bin redis-cluster/redis01
  
  cd /usr/local/redis-cluster/redis01
  rm -rf dump.rdb
  
  vim redis.conf
  ```

  修改配置如下，其中cluster-enabled yes的位置在632行

  ```shell
  daemonize yes
  
  port 6001
  
  ################################ REDIS CLUSTER  ###############################
  cluster-enabled yes
  ```

- **创建多个节点配置**

  ```shell
  cd /usr/local/redis-cluster
  
  cp -r redis01/ redis02
  cp -r redis01/ redis03
  cp -r redis01/ redis04
  cp -r redis01/ redis05
  cp -r redis01/ redis06
  
  每个redis0*的redis.conf的port修改为:port 700*，如redis03修改为port 7003
  ```

- **创建启动文件**

  ```shell
  cd /usr/local/redis-cluster
  
  touch start-all.sh
  
  vim start-all.sh
  ```

  ```shell
  cd redis01
  ./redis-server redis.conf
  cd ..
  cd redis02
  ./redis-server redis.conf
  cd ..
  cd redis03
  ./redis-server redis.conf
  cd ..
  cd redis04
  ./redis-server redis.conf
  cd ..
  cd redis05
  ./redis-server redis.conf
  cd ..
  cd redis06
  ./redis-server redis.conf
  cd ..
  ```

  ```shell
  // 赋予文件权限
  chmod +x start-all.sh
  ```

- **运行**

  ```shell
  ./start-all.sh
  ```

- **判断是否运行**

  ```shell
  ps -ef | grep redis
  
  root       37632       1  0 11:35 ?        00:00:01 ./redis-server *:7001 [cluster]
  root       37636       1  0 11:35 ?        00:00:01 ./redis-server *:7002 [cluster]
  root       37640       1  0 11:35 ?        00:00:01 ./redis-server *:7003 [cluster]
  root       37644       1  0 11:35 ?        00:00:01 ./redis-server *:7004 [cluster]
  root       37648       1  0 11:35 ?        00:00:01 ./redis-server *:7005 [cluster]
  root       37652       1  0 11:35 ?        00:00:01 ./redis-server *:7006 [cluster]
  root       38515   37599  0 12:03 pts/0    00:00:00 grep --color=auto redis
  ```

- **安装Redis脚本**

  ```shell
  // 安装脚本运行环境
  sudo apt update 
  sudo apt full-upgrade
  sudo apt install ruby
  
  cd /
  wget http://rubygems.org/downloads/redis-3.0.0.gem
  
  gem install redis-3.0.0.gem
  ```

- **关闭防火墙**

  ```shell
  // 关闭防火墙
  systemctl stop firewalld.service
  // 查看是否关闭
  systemctl status firewalld.service
  ```

- **开启阿里云出入口**

  ```shell
  阿里云配置出入口7001 ~ 7006、17001 ~ 17006
  ```

- **启动集群**

  > [reference](https://blog.csdn.net/huangxuanheng/article/details/123645185)

  ```shell
  cp -r /usr/java/redis/redis-3.0.0/src/redis-trib.rb /usr/local/redis-cluster
  
  cd /usr/local/redis-cluster
  
  apt install redis-tools
  
  // 只给内网访问
  redis-cli --cluster create 127.0.0.1:7001 127.0.0.1:7002 127.0.0.1:7003 127.0.0.1:7004 127.0.0.1:7005 127.0.0.1:7006 --cluster-replicas 1
  // 只给内网访问
  redis-cli --cluster create 172.31.113.100:7001 172.31.113.100:7002 172.31.113.100:7003 172.31.113.100:7004 172.31.113.100:7005 172.31.113.100:7006 --cluster-replicas 1
  
  // 外网允许访问
  redis-cli --cluster create 106.14.172.7:7001 106.14.172.7:7002 106.14.172.7:7003 106.14.172.7:7004 106.14.172.7:7005 106.14.172.7:7006 --cluster-replicas 1
  ```

- **测试运行**

  ```shell
  cd /usr/local/redis-cluster
  
  redis-cli -c -p 7001
  
  set name hdgaadd
  get hdgaadd
  ```

- **查看集群节点个数**

  ```shell
  cluster nodes
  ```

- **关闭Redis节点**

  > 或者关闭Xshell

  ```shell
  cd /usr/local/redis-cluster
  
  touch shutdown.sh
  ```

  ```shell
  redis-cli -p 7001 shutdown
  redis-cli -p 7002 shutdown
  redis-cli -p 7003 shutdown
  redis-cli -p 7004 shutdown
  redis-cli -p 7005 shutdown
  redis-cli -p 7006 shutdown
  ```

  ```shell
  // 赋予文件权限
  chmod +x shutdown.sh
  ```

  ```shell
  ./shutdown.sh
  ```


- **删除集群缓存**

  ```shell
  touch rm.sh
  ```

  ```shell
  cd redis01
  rm -rf dump.rdb
  rm -rf nodes.conf
  cd ..
  cd redis02
  rm -rf dump.rdb
  rm -rf nodes.conf
  cd ..
  cd redis03
  rm -rf dump.rdb
  rm -rf nodes.conf
  cd ..
  cd redis04
  rm -rf dump.rdb
  rm -rf nodes.conf
  cd ..
  cd redis05
  rm -rf dump.rdb
  rm -rf nodes.conf
  cd ..
  cd redis06
  rm -rf dump.rdb
  rm -rf nodes.conf
  cd ..
  ```

  ```shell
  // 赋予文件权限
  chmod +x rm.sh
  ```

  

## 2. SpringBoot集成

> [official document](https://github.com/redisson/redisson/tree/master/redisson-spring-boot-starter#spring-boot-starter)
>
> [官网集群配置reference](https://github.com/redisson/redisson/wiki/2.-Configuration#24-cluster-mode)

- **Caused by: io.netty.channel.ConnectTimeoutException: connection timed out: 172.31.113.100/172.31.113.100:7001**

  > [reference](https://blog.csdn.net/weixin_44197039/article/details/109906059)

  修改redis01的**nodes.conf**，将172.31.113.100修改为公网





## 3. bugs

- **[ERR] Node 106.14.172.7:7006 is not empty. Either the node already knows other nodes (check with CLU**

  需要关闭Redis服务，删除redis01等的dump.rdb、nodes.conf

- **集群节点至少6个**

  ```shell
  bugs:
  *** ERROR: Invalid configuration for cluster creation.
  *** Redis Cluster requires at least 3 master nodes.
  *** This is not possible with 3 nodes and 1 replicas per node.
  *** At least 6 nodes are required.
  ```

- **Could not connect to Redis at 106.14.172.7:7001: Connection timed out**

  > [reference](https://blog.csdn.net/tutukl/article/details/104672081)

  ```shell
  // 关闭防火墙
  systemctl stop firewalld.service
  // 查看是否关闭
  systemctl status firewalld.service
  ```

- **Waiting for the cluster to join……**

  > [reference](https://www.cnblogs.com/bogiang/p/15016091.html)

  ```shell
  阿里云配置出入口7001 ~ 7006、17001 ~ 17006
  ```

- **Caused by: io.netty.channel.ConnectTimeoutException: connection timed out: 172.31.113.100/172.31.113.100:7001**

  > [reference](https://blog.csdn.net/weixin_44197039/article/details/109906059)

  修改redis01的**nodes.conf**，将172.31.113.100修改为公网









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

  

## Kafka2.13-2.8.0

> [download](https://kafka.apache.org/downloads)
>
> [reference]([Kafka2.8无Zookeeper模式下集群部署__哈利路亚的博客-CSDN博客_kafka2.8部署](https://blog.csdn.net/wanliti1314/article/details/116263788))

- **启动**

  ```shell
  cd /usr/java/kafka/kafka_2.13-2.8.0
  
  // 生成集群id
  ./bin/kafka-storage.sh random-uuid
  
  // 把集群id填充，如下:
  ./bin/kafka-storage.sh format -t o_D3E4ZyRWSlszZe38IDgg -c ./config/kraft/server.properties
  
  // 启动
  ./bin/kafka-server-start.sh ./config/kraft/server.properties
  ```

- **knolwedge**

  - kafka2.80版本后，采用**Kraft**模式，脱离对zookeeper的依赖





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
