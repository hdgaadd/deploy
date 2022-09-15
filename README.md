# deploy



## Ubuntu 部署JDK11

- 创建文件夹

  cd /usr、mkdir java、cd java、mkdir jdk

- 上传jdk

  cd /usr/java/jdk/jdk-11.0.16

- 解压

  tar -zxvf jdk-11.0.16_linux-x64_bin.tar.gz

- 环境变量

  - 创建脚本

    ```
    touch /etc/profile.d/java.sh
    ```

  - 按i编辑

    ```
    vim /etc/profile.d/java.sh
    
    JAVA_HOME=/usr/java/jdk/jdk-11.0.16
    CLASSPATH=$JAVA_HOME/lib
    PATH=$JAVA_HOME/bin:$PATH
    export PATH JAVA_HOME CLASSPATH
    
    :wq
    ```

  - 使配置环境生效

    ```
    source /etc/profile.d/java.sh
    ```

  - java -version

  
