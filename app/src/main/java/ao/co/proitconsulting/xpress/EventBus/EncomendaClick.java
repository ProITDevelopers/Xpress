package ao.co.proitconsulting.xpress.EventBus;

import ao.co.proitconsulting.xpress.modelos.Factura;


public class EncomendaClick {
    private boolean success;
    private Factura factura;

    public EncomendaClick(boolean success, Factura factura) {
        this.success = success;
        this.factura = factura;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }
}
