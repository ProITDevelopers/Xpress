package ao.co.proitconsulting.xpress.modelos;

public class MenuCategory {
    private int idTipo;
    private String descricao;

    public MenuCategory() {
    }

    public MenuCategory(int idTipo, String descricao) {
        this.idTipo = idTipo;
        this.descricao = descricao;
    }

    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
