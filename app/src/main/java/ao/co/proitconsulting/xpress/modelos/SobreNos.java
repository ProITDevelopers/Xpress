package ao.co.proitconsulting.xpress.modelos;

public class SobreNos {

    private int id;
    private String title, description, link;

    public SobreNos() {}

    public SobreNos(int id, String title, String description, String link) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }
}
