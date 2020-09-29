package ao.co.proitconsulting.xpress.adapters;

import ao.co.proitconsulting.xpress.modelos.Produtos;

public interface ProductDetailsListener {

    void onProductAddedCart(int index, Produtos product);

    void onProductRemovedFromCart(int index, Produtos product);
}
