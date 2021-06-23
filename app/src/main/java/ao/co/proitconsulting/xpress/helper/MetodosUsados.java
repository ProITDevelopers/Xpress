package ao.co.proitconsulting.xpress.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.Normalizer;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import ao.co.proitconsulting.xpress.R;
import dmax.dialog.SpotsDialog;


public class MetodosUsados {

    public static final String TAG = "METODOS_USADOS";
    public static AlertDialog waitingDialog;


    //=====================SPOTS_DIALOG_LOADING===============================================//
    public static void spotsDialog(Context context) {
        waitingDialog = new SpotsDialog.Builder().setContext(context).setTheme(R.style.CustomSpotsDialog).build();
    }

    public static void showLoadingDialog(String message){
        waitingDialog.setMessage(message);
        waitingDialog.setCancelable(false);
        waitingDialog.show();
    }

    public static void changeMessageDialog(String message){
        waitingDialog.setMessage(message);
    }

    public static void hideLoadingDialog(){
        waitingDialog.dismiss();
        waitingDialog.cancel();
    }
    //=====================================================================//
    //=====================================================================//


    //==============MOSTRAR_MENSAGENS=======================================================//
    public static void mostrarMensagem(Context mContexto, int mensagem) {
        Toast.makeText(mContexto,mensagem,Toast.LENGTH_SHORT).show();
    }

    public static void mostrarMensagem(Context mContexto, String mensagem) {
        Toast.makeText(mContexto,mensagem,Toast.LENGTH_SHORT).show();
    }

    public static void mostrarMensagemSnackBar(View view, String mensagem) {
        Snackbar.make(view, mensagem, 3000)
                .setActionTextColor(Color.WHITE).show();
    }

    //=====================================================================//
    //=====================================================================//


    //====================VALIDAR_EMAIL=================================================//
    public static boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
    //=====================================================================//
    //=====================================================================//
    public static String removeAcentos(String text) {
        return text == null ? null :
                Normalizer.normalize(text, Normalizer.Form.NFD)
                        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }


    //======================ESCONDER_TECLADO===============================================//
    public static void esconderTeclado(Activity activity) {
        try {
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

                if (inputMethodManager!=null)
                    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }catch (Exception e){
            Log.i(TAG,"esconder teclado " + e.getMessage() );
        }
    }
    //=====================================================================//
    //=====================================================================//

    //========================CHANGE_STATUS_BAR_COLOR=============================================//
    public static void changeStatusBarColor(Activity activity, int color) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(color);
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            window.setStatusBarColor(color);
//        }
    }

    //=====================================================================//
    //=====================================================================//

    //========================PARTILHAR_LINK_DA_APP=============================================//
    public static void shareTheApp(Context context) {

        final String appPackageName = context.getPackageName();
        String appName = context.getString(R.string.app_name);
        String appCategory = "Bebidas";

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        String postData = "Obtenha o aplicativo " + appName +
                " para ter acesso as melhores " + appCategory + "\n" +
                Common.SHARE_URL_PLAYSTORE + appPackageName;


        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Baixar Agora!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, postData);
        shareIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(shareIntent, "Partilhar App"));
    }
    //=======================================================================//
    //=====================================================================//


    //======================GERAR_NUMEROS_ALEATORIOS===============================================//
    public static int randNumber(int min, int max){
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }


    //=======================================================================//
    //=====================================================================//


    //======================TRAFEGO_INTERNET===============================================//

    public static boolean conexaoInternetTrafego(Context context, String TAG){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm!=null) {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo !=null) {
                return true;
            } else {
                return false;
            }
        }else{
            return false;
        }

    }

    public static boolean isConnected(int timeOut) {
        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName("google.com");
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(timeOut, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException e) {
            Log.d(TAG, "isConnected: InterruptedException"+e.getMessage());
        } catch (ExecutionException e) {
            Log.d(TAG, "isConnected: ExecutionException"+e.getMessage());
        } catch (TimeoutException e) {
            Log.d(TAG, "isConnected: TimeoutException"+e.getMessage());
        }

        return inetAddress != null && !inetAddress.equals("");
    }

}
