package ao.co.proitconsulting.xpress.helper;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.BuildConfig;
import ao.co.proitconsulting.xpress.modelos.SobreNos;

public class Common {

    public static Location mLastLocation;
    public static final String SHARE_URL_PLAYSTORE = "https://play.google.com/store/apps/details?id=";
    public static final String SHARE_URL_GOOGLE_DRIVE = "https://drive.google.com/file/d/1bJ1aNdo2fhHblrpm7YtazTXVHUAK6uJp/view?usp=sharing";
    public static String bearerApi = "Bearer ";

    public static final int SPAN_COUNT_ONE = 1;
    public static final int SPAN_COUNT_TWO = 2;

    public static final int VIEW_TYPE_LIST = 1;
    public static final int VIEW_TYPE_GRID = 2;

    public static List<SobreNos> getSobreNosList(){
        List<SobreNos> sobreNosList = new ArrayList<>();

        sobreNosList.add(new SobreNos(1,"Segurança e login","Alterar palavra-passe",""));
        sobreNosList.add(new SobreNos(2,"Sobre nós","Xpress Lengueno é um serviço de delivery que permite o usuário realizar os seus pedidos preferidos.",""));
        sobreNosList.add(new SobreNos(3,"Manual", "Manual de utilizador",""));
        sobreNosList.add(new SobreNos(4,"Versão", BuildConfig.VERSION_NAME,""));
        sobreNosList.add(new SobreNos(5,"Desenvolvedor","Copyright © 2020 - HXA, Powered by Pro-IT Consulting","https://proit-consulting.co.ao/"));
        sobreNosList.add(new SobreNos(6,"Enviar feedback","Tem alguma dúvida? Estamos felizes em ajudar.",""));
        sobreNosList.add(new SobreNos(7,"Partilhar","Partilhe o link da app com os seus contactos.",""));

        return sobreNosList;
    }
}
