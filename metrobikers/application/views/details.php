
<style>
    .slidecontent
    {

    }
    .item
    {
        text-align: center;width:800px;
        height: 600px;
        display: inline-block;
        margin-left: auto;
        margin-right: auto;
        position: relative;
        text-align: center;
        vertical-align: middle;
    }
    .carousel-caption
    {
        color: black;
        border: black;
    }
     .carousel-indicators li
    {
        color: black;
        border: black;
        background-color: black;
    }
    .carousel-indicators .active
    {
        background-color: orange;
        border: black;
    }
    img
    {
        margin-left: auto;
        margin-right: auto;
        margin-top: 60px;
    }
</style>
<div class="col-md-1"></div>
<div class="col-md-10">
    <h1>Come utilizzare ECOmmuters</h1>
    <div id="carousel-example-generic" class="carousel slide" data-ride="carousel">
        <!-- Indicators -->
        <ol class="carousel-indicators">
            <li data-target="#carousel-example-generic" data-slide-to="0" class="active"></li>
            <li data-target="#carousel-example-generic" data-slide-to="1"></li>
            <li data-target="#carousel-example-generic" data-slide-to="2"></li>
            <li data-target="#carousel-example-generic" data-slide-to="3"></li>
            <li data-target="#carousel-example-generic" data-slide-to="4"></li>
        </ol>

        <!-- Wrapper for slides -->
        <div class="carousel-inner" >
            <div class="item active"  >
                <img src="/asset/img/logo.png">
                <div class="carousel-caption">
                    <h4>Registrati</h4>
                    E' sufficiente fornire una mail valida, il tuo nome e cognome e accettare l'informativa sulla privacy.
                    <br>
                    Potrai completare con calma il tuo profilo in un momento successivo.
                </div>
            </div>
            <div class="item"><img src="/asset/img/logo-google-play.png"><img src="/asset/img/ecommuterapp.png">

                <div class="carousel-caption">
                    <h4>Scarica l'applicazione da Google Play</h4>
                </div>
            </div>
            <div class="item"> 
                <img src="/asset/img/ecommuterapplogin.png">
                <div class="carousel-caption">
                    <h4>Inserisci le tue credenziali nell'applicazione</h4>
                </div>
            </div>
            <div class="item">
                <img src="/asset/img/ecommuterapprecord.png">
                <div class="carousel-caption">
                    <h4>Registra i tuoi itinerari</h4>
                    Il tasto rosso avvia e interrompe la registrazione.
                    Dovrai fornire un nome all'itinerario registrato e questo verrà inviato a www.ecommuters.com.
                    Questa procedura manuale va effettuata solo la prima volta per ogni itinerario, in seguito avverrà in automatico.
                </div>
            </div>
            <div class="item">
                <img src="/asset/img/ecommuterapproute.png">
                <div class="carousel-caption">
                    <h4>Configura l'intervallo temporale di tracciatura della tua posizione</h4>
                </div>
            </div>
        </div>

        <!-- Controls -->
        <a class="left carousel-control" href="#carousel-example-generic" data-slide="prev">
            <span class="glyphicon glyphicon-chevron-left"></span>
        </a>
        <a class="right carousel-control" href="#carousel-example-generic" data-slide="next">
            <span class="glyphicon glyphicon-chevron-right"></span>
        </a>
    </div>

</div>
<div class="col-md-1"></div>



<script src="twitter-bootstrap-v2/docs/assets/js/bootstrap-carousel.js"></script>  

