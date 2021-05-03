package dev.majek.homes.data.struct;

public class SharedHome {

    private final Home home;
    private final String sender;
    private final String message;

    public SharedHome(Home home, String sender, String message) {
        this.home = home;
        this.sender = sender;
        this.message = message;
    }

    public Home getHome() {
        return home;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}
