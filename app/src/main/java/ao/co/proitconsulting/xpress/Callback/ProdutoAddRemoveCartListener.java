package ao.co.proitconsulting.xpress.Callback;

import ao.co.proitconsulting.xpress.modelos.Produtos;

public interface ProdutoAddRemoveCartListener {

    void onProdutoAddedCart(int index, Produtos product, int quantidadeSelected);

//    void onProdutoRemovedFromCart(int index, Produtos product, int quantidadeSelected);
}
