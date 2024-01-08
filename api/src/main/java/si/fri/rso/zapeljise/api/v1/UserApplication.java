package si.fri.rso.zapeljise.api.v1;

import com.kumuluz.ee.cors.annotations.CrossOrigin;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import si.fri.rso.zapeljise.api.v1.resources.DemoResource;
import si.fri.rso.zapeljise.api.v1.resources.UserDataResource;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@OpenAPIDefinition(info = @Info(title = "API for users", version = "v1",
        contact = @Contact(email = "gh6987@student.uni-lj.si"),
        license = @License(name = "dev"), description = "API for managing users."),
        servers = @Server(url = "http://localhost:8081"))
@ApplicationPath("/v1")
@CrossOrigin(supportedMethods = "GET, POST, PUT, HEAD, OPTIONS, DELETE")
public class UserApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        classes.add(UserDataResource.class);
        classes.add(DemoResource.class);
        classes.add(CorsFilter.class);

        return classes;
    }
}