package ao.co.proitconsulting.xpress.EventBus;

import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;

public class CartProdutoClick {

    private boolean success;
    private CartItemProdutos cartItemProduto;

    public CartProdutoClick(boolean success, CartItemProdutos cartItemProduto) {
        this.success = success;
        this.cartItemProduto = cartItemProduto;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public CartItemProdutos getCartItemProduto() {
        return cartItemProduto;
    }

    public void setCartItemProduto(CartItemProdutos cartItemProduto) {
        this.cartItemProduto = cartItemProduto;
    }
}
