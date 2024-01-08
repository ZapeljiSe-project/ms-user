package si.fri.rso.zapeljise.api.v1.interceptors;

import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.runtime.EeRuntime;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.kumuluz.ee.logs.cdi.Log;
import org.apache.logging.log4j.CloseableThreadContext;
import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log
@Interceptor
@Priority(Interceptor.Priority.PLATFORM_BEFORE)
public class LogContextInterceptor {

    @Context
    private HttpHeaders httpHeaders;

    private Logger log = Logger.getLogger(LogContextInterceptor.class.getName());

    @AroundInvoke
    public Object logMethodEntryAndExit(InvocationContext context) throws Exception {

        ConfigurationUtil configurationUtil = ConfigurationUtil.getInstance();

        HashMap settings = new HashMap();

        settings.put("environmentType", EeConfig.getInstance().getEnv().getName());
        settings.put("applicationName", EeConfig.getInstance().getName());
        settings.put("applicationVersion", EeConfig.getInstance().getVersion());
        settings.put("uniqueInstanceId", EeRuntime.getInstance().getInstanceId());

        // Try to get correlation ID from header.
        String correlationId = httpHeaders.getHeaderString("correlationId");

        // If not present or empty, make random.
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
            log.log(Level.INFO, "Creating new correlation Id: " + correlationId);
        }
        else {
            log.log(Level.INFO, "Using correlation Id from header: " + correlationId);
        }

        settings.put("uniqueRequestId", correlationId);

        try (final CloseableThreadContext.Instance ctc = CloseableThreadContext.putAll(settings)) {
            Object result = context.proceed();
            return result;
        }
    }
}