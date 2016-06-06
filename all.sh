#!/bin/bash

function benchmark_version {

    # checkout version to test
    cd ~/projects/verifa-jacc/
    git checkout master
    git pull
    git checkout $1
    mvn clean install -DskipTests -q
    cd ~/projects/memory-tester/

    # Run benchmark
    ./benchmark.sh

    # save results
    mkdir results
    mkdir results/$2
    cp *_memory.csv results/$2/
    cp time.tex results/$2
}

# print when we started
date

# Run original version
benchmark_version 99ef42e78a37ff570e6c1620813a45355bf311ea 0_before

# Hashmaps in importer tuple replaced by bitmask
benchmark_version 853d5b57fbad1436dccbadb0afb60490a5328ff1 1_importer_tuple_bitmask
# CmpResults not stored for DEL
benchmark_version b8196898def9e16927bee48fd7532bba8727a22e 2_reduced_cmp_del
# JSimple class used together with two-phase loading
benchmark_version 668e3538026582a78cdf72d98b3eb1a78399fe9d 3_jsimple_class

# Run final optimised version
benchmark_version 010938b143922b3cd35a918711dd700caaaacaae 9_final



# share all to Dropbox
cp -r ~/projects/memory-tester/results/ ~/Dropbox/jacc-memory/paper-experiments/

# print when we finished
date