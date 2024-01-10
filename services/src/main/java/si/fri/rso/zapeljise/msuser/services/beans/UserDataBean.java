package si.fri.rso.zapeljise.msuser.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import si.fri.rso.zapeljise.msuser.lib.TownDemo;
import si.fri.rso.zapeljise.msuser.lib.UserData;
import si.fri.rso.zapeljise.msuser.models.converters.UserDataConverter;
import si.fri.rso.zapeljise.msuser.models.entities.UserDataEntity;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequestScoped
public class UserDataBean {
    private Logger log = Logger.getLogger(UserDataBean.class.getName());

    @Inject
    private EntityManager em;

    // Demo related code and post contruct.
    private Client httpClient;
    private String baseUrl;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
        baseUrl = "http://localhost:8080";
    }

    public List<UserData> getUserData() {
        TypedQuery<UserDataEntity> query = em.createNamedQuery(
                "UserDataEntity.getAll", UserDataEntity.class);
        List<UserDataEntity> resultList = query.getResultList();
        return resultList.stream().map(UserDataConverter::toDto).collect(Collectors.toList());
    }

    public List<UserData> getUserDataFilter(UriInfo uriInfo) {
        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();
        return JPAUtils.queryEntities(em, UserDataEntity.class, queryParameters).stream()
                .map(UserDataConverter::toDto).collect(Collectors.toList());
    }

    public UserData getUserData(Integer id) {
        UserDataEntity userDataEntity = em.find(UserDataEntity.class, id);

        if (userDataEntity == null) {
            log.log(Level.WARNING, "User data with ID = " + id + " not found.");
            throw new NotFoundException();
        }

        log.log(Level.INFO, "Successfully retrieved user data.");
        UserData userData = UserDataConverter.toDto(userDataEntity);
        return userData;
    }

    public UserData checkUserData(UserData userData) {
        TypedQuery<UserDataEntity> query = em.createNamedQuery("UserDataEntity.getUsernames", UserDataEntity.class);

        query.setParameter("username", userData.getUsername());

        try {
            UserDataEntity userDataEntity = query.getSingleResult();
            return UserDataConverter.toDto(userDataEntity);
        } catch (NoResultException e) {
            return null;
        }
    }

    public UserData createUserData(UserData userData) {
        UserDataEntity userDataEntity = UserDataConverter.toEntity(userData);

        try {
            beginTx();
            em.persist(userDataEntity);
            commitTx();
        }
        catch (Exception e) {
            log.log(Level.WARNING,"Exception occured.");
            rollbackTx();
        }

        if (userDataEntity.getId() == null) {
            log.log(Level.WARNING,"Exception occured - Runtime exception: Entity was not persisted.");
            throw new RuntimeException("Entity was not persisted.");
        }

        log.log(Level.INFO, "Successfully created new user.");
        return UserDataConverter.toDto(userDataEntity);
    }

    public UserData putUserData(Integer id, UserData userData) {
        UserDataEntity c = em.find(UserDataEntity.class, id);

        if (c == null) {
            log.log(Level.INFO, "Data for the chosen user did not update.");
            return null;
        }

        UserDataEntity updatedUserDataEntity = UserDataConverter.toEntity(userData);

        // Whatever is being sent, do not change username and password. It is forbidden by business logic.
        updatedUserDataEntity.setUsername(c.getUsername());
        updatedUserDataEntity.setPassword(c.getPassword());

        try {
            beginTx();
            updatedUserDataEntity.setId(c.getId());
            updatedUserDataEntity = em.merge(updatedUserDataEntity);
            commitTx();
        }
        catch (Exception e) {
            log.log(Level.WARNING,"Exception occured.");
            rollbackTx();
        }

        log.log(Level.INFO, "Successfully updated user's data.");
        return UserDataConverter.toDto(updatedUserDataEntity);
    }

    public boolean deleteUserData(Integer id) {
        UserDataEntity userData = em.find(UserDataEntity.class, id);

        if (userData != null) {
            try {
                beginTx();
                em.remove(userData);
                commitTx();
            }
            catch (Exception e) {
                log.log(Level.WARNING, "Exception occured.");
                rollbackTx();
            }
        }
        else {
            log.log(Level.WARNING, "Chosen user did not delete.");
            return false;
        }

        log.log(Level.INFO, "Successfully deleted the user.");
        return true;
    }

    public List<TownDemo> getTownsDemo() {
        log.log(Level.INFO, "Working demo - Connecting to the other MS...");
        try {
            return httpClient
                    .target(baseUrl + "/v1/town")
                    .request().get(new GenericType<List<TownDemo>>() {
                    });
        }
        catch (WebApplicationException | ProcessingException e) {
            log.log(Level.WARNING, e.getMessage());
            throw new InternalServerErrorException(e);
        }
    }

    @Timeout(value = 10, unit = ChronoUnit.SECONDS)
    @CircuitBreaker(requestVolumeThreshold = 3)
    public List<TownDemo> getTownsDemoWithoutFallback() {
        log.log(Level.INFO, "Without fallback demo - Connecting to the other MS...");
        try {
            return httpClient
                    .target(baseUrl + "/v1/townnnnnnnn")
                    .request().get(new GenericType<List<TownDemo>>() {
                    });
        }
        catch (WebApplicationException | ProcessingException e) {
            log.log(Level.WARNING, e.getMessage());
            throw new InternalServerErrorException(e);
        }
    }

    @Timeout(value = 10, unit = ChronoUnit.SECONDS)
    @CircuitBreaker(requestVolumeThreshold = 3)
    @Fallback(fallbackMethod = "getTownsFallback")
    public List<TownDemo> getTownsDemoWithFallback() {
        log.log(Level.INFO, "With fallback demo - Connecting to the other MS...");
        try {
            return httpClient
                    .target(baseUrl + "/v1/townnnnnnnn")
                    .request().get(new GenericType<List<TownDemo>>() {
                    });
        }
        catch (WebApplicationException | ProcessingException e) {
            log.log(Level.WARNING, e.getMessage());
            throw new InternalServerErrorException(e);
        }
    }

    public List<TownDemo> getTownsFallback() {
        List<TownDemo> biggerTownsOnly = new ArrayList<>();

        TownDemo town1 = new TownDemo();
        town1.setName("Ljubljana");
        biggerTownsOnly.add(town1);

        TownDemo town2 = new TownDemo();
        town2.setName("Maribor");
        biggerTownsOnly.add(town2);

        TownDemo town3 = new TownDemo();
        town3.setName("Celje");
        biggerTownsOnly.add(town3);

        TownDemo town4 = new TownDemo();
        town4.setName("Koper");
        biggerTownsOnly.add(town4);

        TownDemo town5 = new TownDemo();
        town5.setName("Kranj");
        biggerTownsOnly.add(town5);

        TownDemo town6 = new TownDemo();
        town6.setName("Novo mesto");
        biggerTownsOnly.add(town6);

        TownDemo town7 = new TownDemo();
        town7.setName("Murska Sobota");
        biggerTownsOnly.add(town7);

        return biggerTownsOnly;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}