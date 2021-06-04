package ao.co.proitconsulting.xpress.EventBus;

import ao.co.proitconsulting.xpress.modelos.Produtos;

public class ProdutoClick {
    private boolean success;
    private Produtos produto;

    public ProdutoClick(boolean success, Produtos produto) {
        this.success = success;
        this.produto = produto;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Produtos getProduto() {
        return produto;
    }

    public void setProduto(Produtos produto) {
        this.produto = produto;
    }
}
