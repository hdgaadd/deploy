# deploy



## Ubuntu 部署JDK11

- 创建文件夹

  cd /usr、mkdir java、cd java、mkdir jdk、上传

- 解压

  tar -zxvf jdk-11.0.16_linux-x64_bin.tar.gz

- 环境变量

  - ```
    vim /etc/profile
    ```

  - 按i编辑

  - ```
    export JAVA_HOME=/usr/java/jdk1.8.0_271
    
    export JRE_HOME=${JAVA_HOME}/jre
    
    export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib:$CLASSPATH
    
    export JAVA_PATH=${JAVA_HOME}/bin:${JRE_HOME}/bin
    
    export PATH=$PATH:${JAVA_PATH}
    ```

  - :wq

- 使配置环境生效

  source /etc/profile

- 

  
