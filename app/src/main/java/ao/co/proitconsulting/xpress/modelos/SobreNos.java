package ao.co.proitconsulting.xpress.modelos;

public class SobreNos {

    private int id;
    private String title, description;

    public SobreNos() {}

    public SobreNos(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
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
}
