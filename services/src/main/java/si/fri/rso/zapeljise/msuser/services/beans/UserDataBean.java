package si.fri.rso.zapeljise.msuser.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.SimplyTimed;
import si.fri.rso.zapeljise.msuser.lib.UserData;
import si.fri.rso.zapeljise.msuser.models.converters.UserDataConverter;
import si.fri.rso.zapeljise.msuser.models.entities.UserDataEntity;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequestScoped
public class UserDataBean {
    private Logger log = Logger.getLogger(UserDataBean.class.getName());

    @Inject
    private EntityManager em;

    public List<UserData> getUserData() {
        TypedQuery<UserDataEntity> query = em.createNamedQuery(
                "UserDataEntity.getAll", UserDataEntity.class);
        List<UserDataEntity> resultList = query.getResultList();
        return resultList.stream().map(UserDataConverter::toDto).collect(Collectors.toList());
    }

    @SimplyTimed(name = "getUserDataFilter_timed_method")
    public List<UserData> getUserDataFilter(UriInfo uriInfo) {
        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();
        return JPAUtils.queryEntities(em, UserDataEntity.class, queryParameters).stream()
                .map(UserDataConverter::toDto).collect(Collectors.toList());
    }

    @Counted(name = "getUserData_invocation_counter", absolute = true)
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