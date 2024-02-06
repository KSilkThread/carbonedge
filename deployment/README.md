# Build process of used images in minifabric-v2.2-arm on RaspberryPi itself for ARM64 compatibility

Images needed:  

- fabric-baseos  
- fabric-peer  
- fabric-orderer  
- fabric-tool  
- fabric-ccenv  
- fabric-ca  
- fabric-javaenv  
- couchdb  

Note: Image from couchdb is going to be managed by couchdb itself therefore no dockerfile is presented to build it.

Systemrequirements:

- Git
- Curl  
- Node  
- Python & pipenv  
- Java JDK v11 atleast  
- Docker 18.04 or later and docker  
- compose 1.11 or later  
- Golang (done with 1.19)

A GO workspace is needed for further steps. After installing dependencies you should create a workspace:

```{bash}
    mkdir -p $HOME/golang
    export GOPATH=$HOME/go
```

Check whether path has been added by executing ```echo $PATH```. If the GOPATH has not been added open the ``` ~/.profile ```or ```~/.bashrc``` and add the GOPATH, then execute ```source ~/.bashrc``` to save the paths.

Verify Golang environment setup by checking ```go env``` and see if GOPATH and GOROOT have been setup.

## Build process of baseos, peer, orderer, tools, ccenv

1. From command line navigate to the new established go workspace and add new directories

    ```{}
        mkdir -p $HOME/go/src/github.com/hyperledger
        cd $HOME/go/src/github.com/hyperledger
    ```

2. Now clone the existing hyperledgerfabric main repository, (v3.0 and above should support ARM64 build)

    ``` {bash}
    git clone https://github.com/hyperledger/fabric.git
    ```

3. script/common/setup.sh has to be updatet as following, replacing following:

    ```{bash}
        ARCH=`uname -m | sed 's|i686|386|' | sed 's|x86_64|amd64|'`
    ```

    with:

    ```{bash}
    ARCH=`uname -m | sed 's|i686|386|' | sed 's|x86_64|amd64|' | sed 's|aarch64|arm64|'`
    ```

    Further, all instances of golang- "version" has to be replaced with your actual golang-"version"

4. For initiating the building process you have to execute, while in hyperledgerfabric directory

    ``` {bash}
    make docker
    ```

## Build process for fabric-ca

1. clone the repository into the go-workspace

    ```{}
    git clone https://github.com/hyperledger/fabric-ca
    ```

2. afterwards execute following command while in fabric-ca directory

    ``` {bash}
    make docker
    ```

## Build process for javaenv

 1. Clone the repository

    ```{bash}
    git clone <https://github.com/hyperledger/fabric-chaincode-java.git>
    ```

 2. From withing root-directory of the repository execute following:

    ```{bash}
    ./gradlew buildImage
    ```
