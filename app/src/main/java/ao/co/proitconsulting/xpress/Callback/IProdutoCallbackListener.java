package ao.co.proitconsulting.xpress.Callback;

import ao.co.proitconsulting.xpress.modelos.Produtos;

public interface IProdutoCallbackListener {
    void onProdutoLoadSuccess(Produtos produto);
    void onProdutoLoadFailed(String message);
}
