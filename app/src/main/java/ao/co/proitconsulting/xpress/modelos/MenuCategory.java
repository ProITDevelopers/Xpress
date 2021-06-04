package ao.co.proitconsulting.xpress.modelos;

//public class MenuCategory implements Comparator<MenuCategory> {
public class MenuCategory  {
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

//    @Override
//    public int compare(MenuCategory menuCategory1, MenuCategory menuCategory2) {
//        // Order ascending.
////        int ret = menuCategory1.getDescricao().compareTo(menuCategory2.getDescricao());
//
//        int ret = menuCategory1.getIdTipo() > menuCategory2.getIdTipo() ? -1 : (menuCategory1.getIdTipo() < menuCategory2.getIdTipo()) ? 1 : 0;
//
//        return ret;
//    }
}
