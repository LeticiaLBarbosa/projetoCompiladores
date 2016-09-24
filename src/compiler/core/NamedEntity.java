package compiler.core;

/**
<<<<<<< HEAD
 * Created by rodrigofarias on 9/22/16.
=======
 * Created by rodrigofarias on 9/23/16.
>>>>>>> 2541fc0a84ad7bbdd1791c81aa5a1822b02e3665
 */
public class NamedEntity {
    private String name;

    public NamedEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NamedEntity other = (NamedEntity) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
