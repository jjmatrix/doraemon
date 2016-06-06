package org.jmatrix.core.aspectj;

public aspect HttpClientAspect {

    pointcut HttpClient4ExecuteTime():
            call(* org.apache.http.client.HttpClient.execute(..));

    Object around():HttpClient4ExecuteTime(){
        try {

        } catch (Exception e) {
        }
        Throwable throwable = null;
        Object retVal = null;
        try {
            retVal = proceed();
        } catch (Throwable e) {
            throwable = e;
        }
        try {
        } catch (Exception e) {
        } finally {
            if (throwable != null) {
            }
        }
        return retVal;
    }
}
