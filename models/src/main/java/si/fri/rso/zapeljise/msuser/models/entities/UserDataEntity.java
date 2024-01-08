package si.fri.rso.zapeljise.msuser.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "usertbl")
@NamedQueries(value =
        {
                @NamedQuery(name = "UserDataEntity.getAll",
                        query = "SELECT im FROM UserDataEntity im"),

                @NamedQuery(name = "UserDataEntity.getUsernames",
                        query = "SELECT im FROM UserDataEntity im WHERE im.username = :username")
        })
public class UserDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}