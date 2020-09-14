package ao.co.proitconsulting.xpress.modelos;


import java.io.Serializable;

public class Estabelecimento implements Serializable {


    public int estabelecimentoID;


    public String nomeEstabelecimento;

    public String descricao;
    public String contacto1;
    public String contacto2;
    public int valorMinimoEncomenda;
    public String website;
    public String logotipo;
    public String imagemCapa;
    public String latitude;
    public String longitude;

    public String provincia;
    public String municipio;
    public String bairro;
    public String rua;
    public String nCasa;

    public boolean take_away,bloqueio,delivery;
    public int popularidade;
//    public TipoDeEstabelecimento tipoDeEstabelecimento;
    public int tipoDeEstabelecimentoID;
    public String endereco;
    public String estadoEstabelecimento;

    public Estabelecimento() {}


}
