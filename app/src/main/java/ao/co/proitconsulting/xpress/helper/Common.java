package ao.co.proitconsulting.xpress.helper;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.BuildConfig;
import ao.co.proitconsulting.xpress.api.ADAO.GetTaxaModel;
import ao.co.proitconsulting.xpress.modelos.CategoriaEstabelecimento;
import ao.co.proitconsulting.xpress.modelos.EscolherFiltros;
import ao.co.proitconsulting.xpress.modelos.Estabelecimento;
import ao.co.proitconsulting.xpress.modelos.Factura;
import ao.co.proitconsulting.xpress.modelos.Produtos;
import ao.co.proitconsulting.xpress.modelos.SobreNos;

public class Common {

    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;

    public static int selectedEstabPosition;
    public static Estabelecimento selectedEstab;
    public static List<Estabelecimento> todosEstabelecimentoList;

    public static int selectedCategoryEstabPosition;
    public static CategoriaEstabelecimento selectedCategoryEstab;

    public static int selectedProdutoPosition;
    public static Produtos selectedProduto;

    public static List<GetTaxaModel> getTaxaModelList = new ArrayList<>();

    public static Location mLastLocation;
    public static final String SHARE_URL_PLAYSTORE = "https://play.google.com/store/apps/details?id=";
    public static final String SHARE_URL_GOOGLE_DRIVE = "https://drive.google.com/file/d/1bJ1aNdo2fhHblrpm7YtazTXVHUAK6uJp/view?usp=sharing";
    public static final String GOOGLE_DRIVE_READ_PDF = "https://docs.google.com/viewer?embedded=true&url=";
    public static final String MANUAL_XPRESS_LINK = "https://express2020.s3.us-east-2.amazonaws.com/Docs/Manual_XpressLengueno_android.pdf";
    public static final String TERMS_CONDITIONS_XPRESS_LINK = "https://express2020.s3.us-east-2.amazonaws.com/Docs/Termos_Condicoes_XpressLengueno_android.pdf";
    public static final String PROIT_CONSULTING_LINK = "https://proit-consulting.co.ao/";
    public static String bearerApi = "Bearer ";

    public static final int SPAN_COUNT_ONE = 1;
    public static final int SPAN_COUNT_TWO = 2;

    public static final int VIEW_TYPE_LIST_LEFT = 1;
    public static final int VIEW_TYPE_LIST_RIGHT = 2;
    public static final int VIEW_TYPE_LIST = 1;
    public static final int VIEW_TYPE_GRID = 2;
    public static Factura selectedFactura;

    public static final String BASE_URL_XPRESS_ADAO_TAXA = "https://apitaxas.lengueno.com/";
    public static final String BASE_URL_XPRESS_ADAO_LOGIN = "https://apilocalizacao.lengueno.com/";
    public static String getLink="";

    public static List<SobreNos> getSobreNosList(){
        List<SobreNos> sobreNosList = new ArrayList<>();

//        sobreNosList.add(new SobreNos(3,"Manual", "Manual de utilizador",MANUAL_XPRESS_LINK));

        sobreNosList.add(new SobreNos(1,"Segurança e login","Alterar palavra-passe",""));
        sobreNosList.add(new SobreNos(2,"Sobre nós","Xpress Lengueno é um serviço de delivery que permite o usuário realizar os seus pedidos preferidos.",MANUAL_XPRESS_LINK));

        sobreNosList.add(new SobreNos(3,"Versão", BuildConfig.VERSION_NAME,""));
        sobreNosList.add(new SobreNos(4,"Desenvolvedor","Copyright © 2020 - HXA"+"\n"+"Powered by Pro-IT Consulting",PROIT_CONSULTING_LINK));
        sobreNosList.add(new SobreNos(5,"Enviar feedback","Tem alguma dúvida? Estamos felizes em ajudar.",""));
        sobreNosList.add(new SobreNos(6,"Partilhar","Partilhe o link da app com os seus contactos.",""));

        return sobreNosList;
    }


    public static List<EscolherFiltros> getEncontrarFiltrosPorList(){
        List<EscolherFiltros> escolherFiltrosList = new ArrayList<>();

        escolherFiltrosList.add(new EscolherFiltros("Todos"));
        escolherFiltrosList.add(new EscolherFiltros("Perto de mim"));
        escolherFiltrosList.add(new EscolherFiltros("Mais populares"));
        escolherFiltrosList.add(new EscolherFiltros("Altas horas"));


        return escolherFiltrosList;
    }


}
