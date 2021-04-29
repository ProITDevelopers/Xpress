package ao.co.proitconsulting.xpress.modelos;

import java.util.List;

public class CategoriaEstabelecimento {

    private MenuCategory menuCategory;
    private List<Estabelecimento> estabelecimentoList;

    public CategoriaEstabelecimento(MenuCategory menuCategory, List<Estabelecimento> estabelecimentoList) {
        this.menuCategory = menuCategory;
        this.estabelecimentoList = estabelecimentoList;
    }

    public MenuCategory getMenuCategory() {
        return menuCategory;
    }

    public void setMenuCategory(MenuCategory menuCategory) {
        this.menuCategory = menuCategory;
    }

    public List<Estabelecimento> getEstabelecimentoList() {
        return estabelecimentoList;
    }

    public void setEstabelecimentoList(List<Estabelecimento> estabelecimentoList) {
        this.estabelecimentoList = estabelecimentoList;
    }
}
