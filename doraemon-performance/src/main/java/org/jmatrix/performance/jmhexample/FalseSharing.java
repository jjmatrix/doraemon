/*
 *  Copyright (c) 2014, Oracle America, Inc.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *     to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 *  THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jmatrix.performance.jmhexample;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * One of the unusual thing that can bite you back is false sharing.
 * If two threads access (and possibly modify) the adjacent values
 * in memory, chances are, they are modifying the values on the same
 * cache line. This can yield significant (artificial) slowdowns.
 *
 * @author jmatrix
 * @date 16/6/13
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class FalseSharing {

    @State(Scope.Group)
    public static class StateBaseline {
        int readOnly;
        int writeOnly;
    }

    @Benchmark
    @Group("baseline")
    public int reader(StateBaseline s) {
        return s.readOnly;
    }

    @Benchmark
    @Group("baseline")
    public void writer(StateBaseline s) {
        s.writeOnly++;
    }

    /*
     * APPROACH 1: PADDING
     */

    @State(Scope.Group)
    public static class StatePadded {
        int readOnly;
        int p01, p02, p03, p04, p05, p06, p07, p08;
        int p11, p12, p13, p14, p15, p16, p17, p18;
        int writeOnly;
        int q01, q02, q03, q04, q05, q06, q07, q08;
        int q11, q12, q13, q14, q15, q16, q17, q18;
    }

//    @Benchmark
    @Group("padded")
    public int reader(StatePadded s) {
        return s.readOnly;
    }

//    @Benchmark
    @Group("padded")
    public void writer(StatePadded s) {
        s.writeOnly++;
    }

    /*
     * APPROACH 2: CLASS HIERARCHY TRICK
     */
    public static class StateHierarchy_1 {
        int readOnly;
    }

    public static class StateHierarchy_2 extends StateHierarchy_1 {
        int p01, p02, p03, p04, p05, p06, p07, p08;
        int p11, p12, p13, p14, p15, p16, p17, p18;
    }

    public static class StateHierarchy_3 extends StateHierarchy_2 {
        int writeOnly;
    }

    public static class StateHierarchy_4 extends StateHierarchy_3 {
        int q01, q02, q03, q04, q05, q06, q07, q08;
        int q11, q12, q13, q14, q15, q16, q17, q18;
    }

    @State(Scope.Group)
    public static class StateHierarchy extends StateHierarchy_4 {
    }

//    @Benchmark
    @Group("hierarchy")
    public int reader(StateHierarchy s) {
        return s.readOnly;
    }

//    @Benchmark
    @Group("hierarchy")
    public void writer(StateHierarchy s) {
        s.writeOnly++;
    }

    /*
     * APPROACH 3: ARRAY TRICK
     */
    @State(Scope.Group)
    public static class StateArray {
        int[] arr = new int[128];
    }

//    @Benchmark
    @Group("sparse")
    public int reader(StateArray s) {
        return s.arr[0];
    }

//    @Benchmark
    @Group("sparse")
    public void writer(StateArray s) {
        s.arr[64]++;
    }

    /*
    * APPROACH 4:
    */
    @State(Scope.Group)
    public static class StateContended {
        int readOnly;

        //        @sun.misc.Contended
        int writeOnly;
    }

//    @Benchmark
    @Group("contended")
    public int reader(StateContended s) {
        return s.readOnly;
    }

    @Benchmark
    @Group("contended")
    public void writer(StateContended s) {
        s.writeOnly++;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(FalseSharing.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
