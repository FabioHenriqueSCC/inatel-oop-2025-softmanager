package models.Person;

public class Developer extends Person {
    private String skillLevel; 

    public Developer(String name, String email, String skillLevel) {
        super(name, email); 
        this.skillLevel = skillLevel;
    }
}
