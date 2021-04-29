package ao.co.proitconsulting.xpress.EventBus;

import ao.co.proitconsulting.xpress.modelos.Estabelecimento;

public class EstabelecimentoClick {
    private boolean success;
    private Estabelecimento estabelecimento;

    public EstabelecimentoClick(boolean success, Estabelecimento estabelecimento) {
        this.success = success;
        this.estabelecimento = estabelecimento;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Estabelecimento getEstabelecimento() {
        return estabelecimento;
    }

    public void setEstabelecimento(Estabelecimento estabelecimento) {
        this.estabelecimento = estabelecimento;
    }
}
