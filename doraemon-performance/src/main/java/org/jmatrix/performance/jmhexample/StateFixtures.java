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

/**
 * State Object can have fixture method. You would be familiar with them if you Junit Or TestNG before.
 * <p/>
 * As with State, fixture method are only called by those benchmark threads which are using the state object.
 *
 * @author jmatrix
 * @date 16/6/13
 */
@State(Scope.Thread)
public class StateFixtures {

    double x;

    @Setup
    public void prepare() {
        x = Math.PI;
    }

    @TearDown
    public void check() {
        assert x > Math.PI : "nothing happen.";
    }

//    @Benchmark
    public void measureRight() {
        x++;
    }

//    @Benchmark
    public void measureWrong() {
        double x = 0;
        x++;
    }

    /**
     * You can run this test:
     * <p/>
     * a) Via the command line:
     * $ mvn clean install3
     * $ java -ea -jar target/benchmarks.jar JMHSample_05 -wi 5 -i 5 -f 1
     * (we requested 5 warmup/measurement iterations, single fork)
     */
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(StateFixtures.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .jvmArgs("-ea")
                .build();
        new Runner(options).run();
    }

}
