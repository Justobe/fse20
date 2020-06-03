# cDep: Configuration Dependency Analysis

cDep is a tool for discovering configuration dependencies both within and across software components. cDep analyzes Java bytecode of the target software programs (supporting both Java and Scala code). It outputs specific types of dependencies and the corresponding interdependent configuration parameters. 

cDep currently supports:
 
<div align="left">
  <img src="https://github.com/xlab-uiuc/cdep/blob/master/figure/sw_supported.png" width="750">
</div>

<br>

cDep could be got from the github repository: xxxx

The repository contains **all the artifacts** (including all the code and datasets) of the paper:

* **Understanding and Discovering Software Configuration Dependencies in Cloud and Datacenter Systems** <br>
Qingrong Chen, Teng Wang, Owolabi Legunsen, Shanshan Li, and Tianyin Xu <br>
In Proceedings of the ACM Joint European Software Engineering Conference and Symposium on the Foundations of Software Engineering (ESEC/FSE 2020), November 8-13, 2020 Sacramento, CA.

----

## Building and Running cDep

<div align="left">
  <img src="https://github.com/xlab-uiuc/cdep/blob/master/figure/build.png" width="250">
</div>

### Docker Container Image

We prepare a Docker container image, with which you can directly interact with the pre-built cDep.

The cDep Docker image can be downloaded from: https://hub.docker.com/repository/docker/cdep/cdep/

To run the Docker image, there is one CLI option:

* `-a <arg>`: The supported applications are `hdfs`, `mapreduce`, `yarn`, `hadoop_common`, 
                     `hadoop_tools`, `hbase`, `alluxio`, `zookeeper`, `spark`

One example running command is as follows:
```
$ ./dockerrun.sh -a hdfs,mapreduce
```
Note that multiple applications should be seperated by `,`.

The results will be stored at `/tmp/output/cDep_result.csv`.

**The analysis could take several tens of minutes (so be patient).**

### Build Docker Image Locally

We provide the Dockerfile (under the root directory) as well, with which you could build the docker image locally and run the program.

To build the docker image:
```
$ git clone git@github.com:xlab-uiuc/cdep.git
$ cd cdep
$ docker build -t cdep/cdep:1.0 .
```

Then the running command is same as above. One example running command is:
```
$ ./dockerrun.sh -a hdfs,mapreduce
```

### Building cDep in Your Own Environment

We build cDep using Java(TM) SE Runtime Environment (build 12.0.2+10) and Apache Maven 3.6.1.
We did not guarantee you can build with other Java versions.

First, clone the repository,
```
$ git clone git@github.com:xlab-uiuc/cdep.git
$ cd cdep
```

Second, build cDep (we use Maven as the build tool for cDep)
```
$ mvn compile
```
After compiling, `cDep.class` should be generated at `target/classes/cdep/cDep.class`.

Third, use the script `run.sh`. One example running command is as follows:
```
$ ./run.sh -a hdfs,mapreduce
```

----

## Reproducibility
<div align="left">
  <img src="https://github.com/xlab-uiuc/cdep/blob/master/figure/repro.png" width="150">
</div>

<br>
 
**All the results in the paper, including both the study dataset and the cDep results can be reproduced.**

The cDep results can be reproduced by running cDep:
```
$ ./dockerrun.sh  -a  hdfs,mapreduce,yarn,hadoop_common,hadoop_tools,hbase,alluxio,zookeeper,spark
```

The `cDep_result.csv` is in the format of:
`["parameter A","parameter B","dependency type","java class","java method","jimple stmt"]`

The output means `parmaeter A` and `parmaeter B` have a `dependency type`. And that dependency relation is identified in the `jimple stmt` of a certain `java method` and `java class`.

The following shows an example of a dependency cDep extracts from MapReduce:

```
(
  'mapreduce.output.fileoutputformat.compress',
  'mapreduce.output.fileoutputformat.compress.type',
  'control dependency',
  'org.apache.hadoop.mapred.MapFileOutputFormat',
  '<org.apache.hadoop.mapred.MapFileOutputFormat:org.apache.hadoop.mapred.RecordWriter getRecordWriter(org.apache.hadoop.fs.FileSystem,org.apache.hadoop.mapred.JobConf,java.lang.String,org.apache.hadoop.util.Progressable)>', 
  'if $z0 == 0 goto $r7 = new org.apache.hadoop.io.MapFile$Writer'
)
```

The two parameters, `mapreduce.output.fileoutputformat.compress` and `mapreduce.output.fileoutputformat.compress.type`, have a control dependency. And that relation is found from class `org.apache.hadoop.mapred.MapFileOutputFormat`.

----

## Datasets
<div align="left">
  <img src="https://github.com/xlab-uiuc/cdep/blob/master/figure/dataset.png" width="120">
</div
 
<br>
 
We also replease all the datasets included in the paper under the `dataset` directory.

### Configuration Dependency Dataset

It contains the following four files:
* `hadoop_intra.csv` : Intra-component dependencies in each individual component of the Hadoop-based stack;
* `hadoop_inter.csv` : Inter-component dependencies across components of the Hadoop-based stack;
* `openstack_intra.csv` : Intra-component dependencies in each individual component of OpenStack;
* `openstack_inter.csv` : Inter-component dependencies across components of OpenStack;
* `one_off_dep.csv` : One-off dependencies described in Section 4.3.

All the data sheets are in the format of CSV, with the first row describing the meaning of each column.

The data sheets provide detailed labels of the analysis results presented in our study.

### cDep Findings

The found dependency cases from cDep could be found at `cDep_result`.
It contains the following two files:
* `intra.csv` : Intra-component dependencies in each individual component of the Hadoop-based stack;
* `inter.csv` : Inter-component dependencies across components of the Hadoop-based stack;

All the data sheets are in the format of CSV, with the first row describing the meaning of each column.

----

## Code Walkthrough

The following graph shows the end-to-end workflow of cDep:

<div align="left">
  <img src="https://github.com/xlab-uiuc/cdep/blob/master/figure/cdep_overview.png" width="750">
</div>

<br>

The source code of `cdep` are placed under the `src/main/java` directory.

It contains the following main modules:
* `configinterface` implements the configuration interface methods to read configuration values in different projects;
* `dataflow` implements the inter-procedure and intra-procedure taint tracking;
* `handlingdep` implements the methods to capture different types of configuration dependencies;
* `utility` implements utility methods.

----

## Verification and Validation

We show some configuration dependency cases (found by cDep) and explain why they are dependent on each other.

### Value Relationship Dependency

The first parameter is no less than the second parameter:
1. `yarn.nm.liveness-monitor.expiry-interval-ms`
2. `yarn.resourcemanager.nodemanagers.heartbeat-interval-ms`

Code snippets:
```
long expireIntvl = conf.getLong(YarnConfiguration.RM_NM_EXPIRY_INTERVAL_MS,
    YarnConfiguration.DEFAULT_RM_NM_EXPIRY_INTERVAL_MS);
long heartbeatIntvl =
    conf.getLong(YarnConfiguration.RM_NM_HEARTBEAT_INTERVAL_MS,
        YarnConfiguration.DEFAULT_RM_NM_HEARTBEAT_INTERVAL_MS);
if (expireIntvl < heartbeatIntvl) {
    throw new YarnRuntimeException("Nodemanager expiry interval should be no"
       + " less than heartbeat interval, "
       + YarnConfiguration.RM_NM_EXPIRY_INTERVAL_MS + "=" + expireIntvl
       + ", " + YarnConfiguration.RM_NM_HEARTBEAT_INTERVAL_MS + "="
       + heartbeatIntvl);
}
```

### Value Relationship Dependency

If the first parameter is not `null`, then the second parameter has to be `kerberos` to enable authentication.
1. `hbase.thrift.security.qop`
2. `hadoop.security.authentication`

Code snippets:
```
if (qop != null) {
    ...
    if (!securityEnabled) {
        throw new IOException("Thrift server must run in secure mode to support authentication");
    }
}
```
(`qop` stores values of the first parameter, while `securityEnabled` takes the value from the second parameter.)


### Overwrite Dependency

The second parameter overwrites the first parameter. 
1. `hadoop.security.service.user.name.key`
2. `mapreduce.jobhistory.principal`

Code snippets:
```
private Configuration addSecurityConfiguration(Configuration conf) {
    ...
    conf.set(CommonConfigurationKeys.HADOOP_SECURITY_SERVICE_USER_NAME_KEY,
             conf.get(JHAdminConfig.MR_HISTORY_PRINCIPAL, ""));
    return conf;
}
```


