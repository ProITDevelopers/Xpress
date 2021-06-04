package ao.co.proitconsulting.xpress.api;

import java.util.List;

import ao.co.proitconsulting.xpress.modelos.Estabelecimento;
import ao.co.proitconsulting.xpress.modelos.Factura;
import ao.co.proitconsulting.xpress.modelos.LoginRequest;
import ao.co.proitconsulting.xpress.modelos.MenuCategory;
import ao.co.proitconsulting.xpress.modelos.Order;
import ao.co.proitconsulting.xpress.modelos.ProdutoListExtras;
import ao.co.proitconsulting.xpress.modelos.Produtos;
import ao.co.proitconsulting.xpress.modelos.ReferenciaRequest;
import ao.co.proitconsulting.xpress.modelos.RegisterRequest;
import ao.co.proitconsulting.xpress.modelos.ReporSenha;
import ao.co.proitconsulting.xpress.modelos.UsuarioAuth;
import ao.co.proitconsulting.xpress.modelos.UsuarioPerfil;
import ao.co.proitconsulting.xpress.modelos.UsuarioPerfilRequest;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiInterface {


    @POST("/authenticate2")
    Call<UsuarioAuth> autenticarCliente(@Body LoginRequest loginRequest);

    @POST("/authenticate2")
    Call<ResponseBody> autenticarCliente2(@Body LoginRequest loginRequest);


//    @POST("/FacebookCliente")
//    Call<UsuarioAuth> autenticarFaceBook(@Body FaceBookLoginRequest faceBookLoginRequest);


    @POST("/cadastrarcliente")
    Call<Void> registrarCliente(@Body RegisterRequest registerRequest);



    @Multipart
    @POST("/cadastrarcliente")
    Call<Void> registrarUsuarioComImg(
            @Part("PrimeiroNome") RequestBody primeiroNome,
            @Part("UltimoNome") RequestBody ultimoNome,
            @Part("Email") RequestBody email,
            @Part("Password") RequestBody password,
            @Part("ContactoMovel") RequestBody contactoMovel,
            @Part("Sexo") RequestBody sexo,
            @Part MultipartBody.Part imagem);

    @Multipart
    @POST("/cadastrarcliente")
    Call<Void> registrarUsuarioSemImg(
            @Part("PrimeiroNome") RequestBody primeiroNome,
            @Part("UltimoNome") RequestBody ultimoNome,
            @Part("Email") RequestBody email,
            @Part("Password") RequestBody password,
            @Part("ContactoMovel") RequestBody contactoMovel,
            @Part("Sexo") RequestBody sexo
    );





    @PUT("/SolicitarCodigoRecuperacao/{id}")
    Call<Void> enviarTelefone(@Path("id") int numero_telefone);


    @POST("/ReporSenha/{numero}")
    Call<String> enviarConfirCodigo(
            @Path("numero") String numero_telefone,
            @Body ReporSenha reporSenha);




    @GET("/PerfilCliente")
    Call<List<UsuarioPerfil>> getMeuPerfil();

    @GET("/PerfilCliente")
    Call<List<UsuarioPerfil>> getPerfilLogin(@Header("Authorization") String bearerToken);


    @PUT("/UpdateDadosPessoaisCliente")
    Call<ResponseBody> actualizarPerfil(@Body UsuarioPerfilRequest usuarioPerfilRequest);

    @Multipart
    @POST("/AlterarFotoPerfilCliente")
    Call<ResponseBody> actualizarFotoPerfil(@Part MultipartBody.Part imagem);



    @POST("/FacturaTpa")
    Call<ResponseBody> facturaTPA(@Body Order order);

    @POST("/FacturaReferencia")
    Call<List<List<ReferenciaRequest>>> facturaReferencia(@Body Order order);

    @GET("/FacturasActualCliente")
    Call<List<Factura>> getTodasFacturas();
//
//
//    @GET("/ListarEstabA24h2")
//    Call<List<Estabelecimento>> getAltasHorasEstabelecimentos();
//

    @GET("/ListarTodosTipoEstabelecimento")
    Call<List<MenuCategory>> getMenuCategories();

    @GET("/ListagemEstabelecimentoA")
    Call<List<Estabelecimento>> getAllEstabelecimentos();
//
    @GET("/ListarEstabPorTipo/{IdTipoEstabelecimento}")
    Call<List<Estabelecimento>> getEstabelecimentosPorTipo(@Path("IdTipoEstabelecimento") int idTipoEstabelecimento);
//
//
    @GET("/ListarProdutosEstab/{idE}")
    Call<List<Produtos>> getAllProdutosDoEstabelecimento(@Path("idE") int idEstabelecimento);

    @GET("/api/Produtoextras/listagemextras/{produtoid}")
    Call<List<ProdutoListExtras>> getProdutosExtras(@Path("produtoid") int idProduto);
//
//    @GET("/ListarProdutos")
//    Call<List<Produtos>> getAllProdutos();




}
