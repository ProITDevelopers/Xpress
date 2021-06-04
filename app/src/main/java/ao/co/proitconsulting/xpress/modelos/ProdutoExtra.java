package ao.co.proitconsulting.xpress.modelos;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ProdutoExtra  extends RealmObject{

    @PrimaryKey
    public int iDextras;
    public String descricao;
    public float preco;

}
