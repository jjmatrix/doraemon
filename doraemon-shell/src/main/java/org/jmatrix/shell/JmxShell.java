package org.jmatrix.shell;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;

/**
 * @author jmatrix
 * @date 16/8/11
 */
public class JmxShell {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("usage parameter: [ip] [port] [objectName] [mbean class] [operation]");
            return;
        }
        try {
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + args[0] + ":" + args[1] + "/jmxrmi");
            JMXConnector jmxConnector = JMXConnectorFactory.connect(url, null);
            MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();
            ObjectName objectName = new ObjectName(args[2]);

            Class cls = Class.forName(args[3]);

            Constructor noParamConstructor = null;
            Constructor[] constructors = cls.getConstructors();
            for (Constructor constructor : constructors) {
                if (constructor.getParameterCount() == 0) {
                    noParamConstructor = constructor;
                    break;
                }
            }

            if (noParamConstructor == null) {
                return;
            }

            Object obj = cls.newInstance();



        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
