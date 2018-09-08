package ee.ttu.iti0202_gui.restapi.rest.model;

import javax.persistence.*;

/**
 * Entity class for Roles.
 */
@Entity
@Table(name = "roles")
public class Role {
    private Long id;
    private String name;

    public Role() { }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
