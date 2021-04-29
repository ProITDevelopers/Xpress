package ao.co.proitconsulting.xpress.EventBus;

import ao.co.proitconsulting.xpress.modelos.CategoriaEstabelecimento;
import ao.co.proitconsulting.xpress.modelos.Estabelecimento;

public class CategoryClick {
    private boolean success;
    private CategoriaEstabelecimento categoriaEstabelecimento;

    public CategoryClick(boolean success, CategoriaEstabelecimento categoriaEstabelecimento) {
        this.success = success;
        this.categoriaEstabelecimento = categoriaEstabelecimento;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public CategoriaEstabelecimento getCategoriaEstabelecimento() {
        return categoriaEstabelecimento;
    }

    public void setCategoriaEstabelecimento(CategoriaEstabelecimento categoriaEstabelecimento) {
        this.categoriaEstabelecimento = categoriaEstabelecimento;
    }
}
