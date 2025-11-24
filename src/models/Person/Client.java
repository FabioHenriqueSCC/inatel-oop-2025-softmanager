package models.Person;

public class Client extends Person {
    private String company;

    public Client(String name, String email, String company) {
        super(name, email);
        this.company = company;
    }
}
