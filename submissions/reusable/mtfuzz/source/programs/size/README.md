1. The tested binaries are compiled into 32bit format (to compare with NEUZZ). MTFuzz can actually run on any 64bit binaries. Install some required libraies for 32bit binaries.
```sh
    sudo dpkg --add-architecture i386
    sudo apt-get update
    sudo apt-get install libc6:i386 libncurses5:i386 libstdc++6:i386 lib32z1
```

2. Set CPU scaling algorithm and core dump notification with root. 
```sh
    sudo -i
    cd /sys/devices/system/cpu
    echo performance | tee cpu*/cpufreq/scaling_governor
    echo core >/proc/sys/kernel/core_pattern
    exit
```

3. Open a terminal at this directory to start NN module.
```sh  
    python nn.py ./size 
```

4. Open another terminal at this directory to start fuzzing module.
```sh
    # -l, file len is obtained by maximum file lens in the mtfuzz_in ( ls -lS mtfuzz_in|head )
    python ./mtfuzz_wrapper.py -i mtfuzz_in -o seeds -l 7402 ./size @@
```
