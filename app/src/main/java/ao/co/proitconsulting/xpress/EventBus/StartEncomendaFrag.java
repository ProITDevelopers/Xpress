package ao.co.proitconsulting.xpress.EventBus;

public class StartEncomendaFrag {

    private boolean success;

    public StartEncomendaFrag(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
