package ee.ttu.iti0202_gui.android.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Role entity class.
 *
 * @author Priit Käärd
 */
public class Role {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Role() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
