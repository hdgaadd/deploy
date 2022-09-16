# deploy

## aliyun.com reference

- [安装图形界面](https://blog.csdn.net/weixin_44285445/article/details/107485161)
- [配置公网访问](https://blog.csdn.net/qq_35354260/article/details/121878843)
- [阿里云服务器恢复出厂设置](https://blog.csdn.net/liming1016/article/details/107605782)

## Ubuntu部署JDK11

- 创建文件夹

  cd /usr、mkdir java、cd java、mkdir jdk

- 上传jdk

  cd /usr/java/jdk/jdk-11.0.16

- 解压

  tar -zxvf jdk-11.0.16_linux-x64_bin.tar.gz

- 环境变量

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


## Ubuntu部署MySQL5.7.26

- ## MySQL

  > [reference address](https://blog.csdn.net/qq_37598011/article/details/93489404)

  - 解压

    ```shell
    tar -zxvf mysql-5.7.26-linux-glibc2.12-x86_64.tar.gz
    ```

  - 创建mysql用户组和用户并修改权限

    ```shell
    groupadd mysql
    useradd -r -g mysql mysql
    ```

  - 创建data的mysql目录，默认是在根目录的data

    ```shell
    mkdir -p /data/mysql
    ```

  - 赋予权限

    ```shell
    chown mysql:mysql -R /data/mysql
    ```

  - 配置my.cnf

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

  - 进行mysql的bin目录执行以下进行初始化

    ```shell
    ./mysqld --defaults-file=/etc/my.cnf --basedir=/usr/java/mysql/mysql-5.7.26-linux-glibc2.12-x86_64/ --datadir=/data/mysql/ --user=mysql --initialize
    ```

  - **移动mysql.server到**

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

    - 查看

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

      

## Ubuntu部署Jar

- **启动**

  - 关闭Xshell，服务关闭

    ```shell
    java -jar baby-1.0-SNAPSHOT.jar &
    ```

  - 关闭Xshell，服务仍然启动

    ```shell
    nohup java -jar baby-1.0-SNAPSHOT.jar &
    
    最好在jar的文件夹上进行以上命令，否则在其他文件夹执行，则会生成多个nohup.out，造成异常：ignoring input and appending output to 'nohup.out'
    ```

- **停止**

  ```shell
  ps -ef | grep baby-1.0-SNAPSHOT.jar
  
  kill -s 9 10064
  ```
